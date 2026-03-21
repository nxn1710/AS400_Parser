package com.as400parser.dds;

import com.as400parser.common.model.*;
import com.as400parser.common.normalizer.NormalizedSource;
import com.as400parser.common.normalizer.SourceNormalizer;
import com.as400parser.common.parser.As400Parser;
import com.as400parser.common.parser.ParseOptions;
import com.as400parser.dds.model.*;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;

/**
 * Public API entry point for DDS (PF/LF) parsing.
 * <p>
 * Orchestrates the pipeline:
 * <ol>
 *   <li>Normalize source (tabs, padding to 80-char width)</li>
 *   <li>Classify lines → parse keywords → build IR (DdsIrBuilder)</li>
 *   <li>Populate metadata</li>
 *   <li>Return IrDocument</li>
 * </ol>
 * Implements {@link As400Parser} for unified parser interface.
 */
public class DdsParserFacade implements As400Parser {

    private static final String IR_VERSION = "1.0.0";

    // =========================================================================
    // As400Parser interface
    // =========================================================================

    @Override
    public IrDocument parse(Path sourceFile, ParseOptions options) {
        try {
            SourceNormalizer normalizer = new SourceNormalizer();
            NormalizedSource normalized = (options.getCharset() != null)
                ? normalizer.normalize(sourceFile, options.getCharset())
                : normalizer.normalize(sourceFile); // auto-detect encoding
            String sourceType = detectSourceType(sourceFile);
            IrDocument doc = runPipeline(normalized, sourceType);
            populateMetadataFromFile(doc, sourceFile, sourceType);

            // Record detected encoding in metadata
            if (normalized.getDetectedCharset() != null && doc.getMetadata() != null
                    && doc.getMetadata().getParseInfo() != null) {
                doc.getMetadata().getParseInfo()
                    .setDetectedEncoding(normalized.getDetectedCharset().name());
            }

            return doc;
        } catch (IOException e) {
            return createFailedDocument(e.getMessage(),
                    detectSourceType(sourceFile));
        }
    }

    @Override
    public IrDocument parse(String sourceText, ParseOptions options) {
        SourceNormalizer normalizer = new SourceNormalizer();
        NormalizedSource normalized = normalizer.normalize(sourceText);
        // Default to DDS_PF for text-only parsing; caller can override via context
        return runPipeline(normalized, "DDS_PF");
    }

    @Override
    public String getSourceType() {
        return "DDS";
    }

    @Override
    public List<String> getSupportedExtensions() {
        return List.of(".pf", ".lf");
    }

    // =========================================================================
    // Pipeline
    // =========================================================================

    private IrDocument runPipeline(NormalizedSource normalized, String sourceType) {
        // Build IR using DdsIrBuilder
        DdsIrBuilder irBuilder = new DdsIrBuilder();
        Object content = irBuilder.buildContent(normalized.getLines(), sourceType);

        // Create IrDocument
        IrDocument doc = new IrDocument();
        doc.setContent(content);

        // Build dependencies (referenced files from PFILE/JFILE/REF keywords)
        IrDocument.Dependencies deps = new IrDocument.Dependencies();
        deps.setReferencedFiles(extractReferencedFiles(content));
        doc.setDependencies(deps);

        // Populate metadata
        populateMetadata(doc, normalized, sourceType);

        return doc;
    }

    // =========================================================================
    // Metadata
    // =========================================================================

    private void populateMetadata(IrDocument document, NormalizedSource normalized,
                                  String sourceType) {
        Metadata metadata = new Metadata();
        metadata.setIrVersion(IR_VERSION);
        metadata.setSourceType(sourceType);

        // parseInfo
        Metadata.ParseInfo parseInfo = new Metadata.ParseInfo();
        parseInfo.setParsedAt(Instant.now().toString());
        parseInfo.setTotalLines(normalized.getLineCount());
        parseInfo.setParserVersion(IR_VERSION);
        parseInfo.setParseStatus("complete");
        parseInfo.setErrors(List.of());
        parseInfo.setWarnings(List.of());

        metadata.setParseInfo(parseInfo);
        document.setMetadata(metadata);
    }

    private void populateMetadataFromFile(IrDocument document, Path sourceFile,
                                          String sourceType) {
        Metadata metadata = document.getMetadata();
        if (metadata == null) return;

        metadata.setSourceType(sourceType);

        String fileName = sourceFile.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        String memberName = (dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName).toUpperCase();
        metadata.setSourceMember(memberName);

        // sourceFile = parent directory name
        Path parent = sourceFile.getParent();
        if (parent != null) {
            metadata.setSourceFile(parent.getFileName().toString());
            // sourceLibrary = grandparent directory name
            Path grandParent = parent.getParent();
            if (grandParent != null) {
                metadata.setSourceLibrary(grandParent.getFileName().toString());
            }
        }
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    /**
     * Detect DDS source type from file extension.
     * .pf → DDS_PF, .lf → DDS_LF
     */
    private String detectSourceType(Path sourceFile) {
        if (sourceFile == null) return "DDS_PF";
        String name = sourceFile.getFileName().toString().toLowerCase();
        if (name.endsWith(".lf")) {
            return "DDS_LF";
        }
        return "DDS_PF";
    }

    /**
     * Extract referenced files from content (PFILE, JFILE, REF keywords).
     */
    private List<IrDocument.DependencyRef> extractReferencedFiles(Object content) {
        Set<String> refs = new LinkedHashSet<>();

        if (content instanceof DdsPfContent pfContent) {
            extractRefsFromFileKeywords(pfContent.getFileKeywords(), refs);
            for (RecordFormat rf : pfContent.getRecordFormats()) {
                extractRefsFromKeywords(rf.getKeywords(), refs);
            }
        } else if (content instanceof DdsLfContent lfContent) {
            extractRefsFromFileKeywords(lfContent.getFileKeywords(), refs);
            for (LfRecordFormat lf : lfContent.getRecordFormats()) {
                if (lf.getPfile() != null) refs.add(lf.getPfile());
                if (lf.getJfile() != null) refs.addAll(lf.getJfile());
                extractRefsFromKeywords(lf.getKeywords(), refs);
            }
        }

        List<IrDocument.DependencyRef> depRefs = new ArrayList<>();
        for (String ref : refs) {
            depRefs.add(new IrDocument.DependencyRef(ref, "physical-file"));
        }
        return depRefs;
    }

    private void extractRefsFromFileKeywords(List<DdsKeyword> keywords, Set<String> refs) {
        for (DdsKeyword kw : keywords) {
            String name = kw.getName();
            if ("REF".equals(name) || "REFACCPTH".equals(name)) {
                if (kw.getValue() != null) refs.add(kw.getValue());
            }
        }
    }

    private void extractRefsFromKeywords(List<DdsKeyword> keywords, Set<String> refs) {
        for (DdsKeyword kw : keywords) {
            String name = kw.getName();
            if ("PFILE".equals(name) || "REF".equals(name) || "REFACCPTH".equals(name)) {
                if (kw.getValue() != null) refs.add(kw.getValue());
            } else if ("JFILE".equals(name)) {
                if (kw.getValues() != null) refs.addAll(kw.getValues());
                else if (kw.getValue() != null) refs.add(kw.getValue());
            }
        }
    }

    private IrDocument createFailedDocument(String errorMessage, String sourceType) {
        IrDocument doc = new IrDocument();
        Metadata meta = new Metadata();
        meta.setIrVersion(IR_VERSION);
        meta.setSourceType(sourceType);

        Metadata.ParseInfo parseInfo = new Metadata.ParseInfo();
        parseInfo.setParsedAt(Instant.now().toString());
        parseInfo.setParseStatus("failed");
        parseInfo.setErrors(List.of(Map.of(
                "line", 0,
                "column", 0,
                "message", errorMessage,
                "severity", "ERROR"
        )));
        parseInfo.setWarnings(List.of());
        meta.setParseInfo(parseInfo);

        doc.setMetadata(meta);
        return doc;
    }
}
