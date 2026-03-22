package com.as400parser.prtf;

import com.as400parser.common.model.*;
import com.as400parser.common.normalizer.NormalizedSource;
import com.as400parser.common.normalizer.SourceNormalizer;
import com.as400parser.common.parser.As400Parser;
import com.as400parser.common.parser.ParseOptions;
import com.as400parser.dds.model.DdsKeyword;
import com.as400parser.prtf.model.PrtfContent;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;

/**
 * Public API entry point for PRTF (Printer File) parsing.
 * <p>
 * Orchestrates the pipeline:
 * <ol>
 *   <li>Normalize source (tabs, padding to 80-char width)</li>
 *   <li>Build PRTF IR content model (PrtfIrBuilder)</li>
 *   <li>Populate metadata</li>
 *   <li>Return IrDocument</li>
 * </ol>
 * Implements {@link As400Parser} for unified parser interface.
 */
public class PrtfParserFacade implements As400Parser {

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
                : normalizer.normalize(sourceFile);
            IrDocument doc = runPipeline(normalized);
            populateMetadataFromFile(doc, sourceFile);

            // Record detected encoding
            if (normalized.getDetectedCharset() != null && doc.getMetadata() != null
                    && doc.getMetadata().getParseInfo() != null) {
                doc.getMetadata().getParseInfo()
                    .setDetectedEncoding(normalized.getDetectedCharset().name());
            }

            return doc;
        } catch (IOException e) {
            return createFailedDocument(e.getMessage());
        }
    }

    @Override
    public IrDocument parse(String sourceText, ParseOptions options) {
        SourceNormalizer normalizer = new SourceNormalizer();
        NormalizedSource normalized = normalizer.normalize(sourceText);
        return runPipeline(normalized);
    }

    @Override
    public String getSourceType() {
        return "PRTF";
    }

    @Override
    public List<String> getSupportedExtensions() {
        return List.of(".prtf");
    }

    // =========================================================================
    // Pipeline
    // =========================================================================

    private IrDocument runPipeline(NormalizedSource normalized) {
        // Build IR
        PrtfIrBuilder irBuilder = new PrtfIrBuilder();
        List<String> lines = Arrays.asList(normalized.getLines());
        PrtfContent content = irBuilder.buildContent(lines, normalized.getSequenceNumbers());

        // Create IrDocument
        IrDocument doc = new IrDocument();
        doc.setContent(content);

        // Dependencies (PRTF may reference files via REF keyword)
        IrDocument.Dependencies deps = new IrDocument.Dependencies();
        deps.setReferencedFiles(extractReferencedFiles(content));
        deps.setCalledPrograms(List.of());
        deps.setCopyMembers(List.of());
        doc.setDependencies(deps);

        // Errors from parse
        if (content.getParseErrors() != null && !content.getParseErrors().isEmpty()) {
            doc.setErrors(new ArrayList<>(content.getParseErrors()));
        } else {
            doc.setErrors(new ArrayList<>());
        }

        // Populate metadata
        populateMetadata(doc, normalized);

        return doc;
    }

    // =========================================================================
    // Metadata
    // =========================================================================

    private void populateMetadata(IrDocument document, NormalizedSource normalized) {
        Metadata metadata = new Metadata();
        metadata.setIrVersion(IR_VERSION);
        metadata.setSourceType("PRTF");

        // parseInfo
        Metadata.ParseInfo parseInfo = new Metadata.ParseInfo();
        parseInfo.setParsedAt(Instant.now().toString());
        parseInfo.setTotalLines(normalized.getLineCount());
        parseInfo.setParserVersion(IR_VERSION);

        // Determine parse status from errors
        PrtfContent content = (PrtfContent) document.getContent();
        if (content.getParseErrors() != null && !content.getParseErrors().isEmpty()) {
            boolean hasError = content.getParseErrors().stream()
                .anyMatch(pe -> ParseError.Severity.ERROR.equals(pe.getSeverity()));
            parseInfo.setParseStatus(hasError ? "partial" : "complete");
        } else {
            parseInfo.setParseStatus("complete");
        }

        parseInfo.setErrors(List.of());
        parseInfo.setWarnings(List.of());

        metadata.setParseInfo(parseInfo);
        document.setMetadata(metadata);
    }

    private void populateMetadataFromFile(IrDocument document, Path sourceFile) {
        Metadata metadata = document.getMetadata();
        if (metadata == null) return;

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

    private List<IrDocument.DependencyRef> extractReferencedFiles(PrtfContent content) {
        Set<String> refs = new LinkedHashSet<>();

        // Check file-level REF keywords
        if (content.getFileKeywords() != null) {
            for (DdsKeyword kw : content.getFileKeywords()) {
                if ("REF".equals(kw.getName()) && kw.getValue() != null) {
                    refs.add(kw.getValue());
                }
            }
        }

        List<IrDocument.DependencyRef> depRefs = new ArrayList<>();
        for (String ref : refs) {
            depRefs.add(new IrDocument.DependencyRef(ref, "physical-file"));
        }
        return depRefs;
    }

    private IrDocument createFailedDocument(String errorMessage) {
        IrDocument doc = new IrDocument();
        Metadata meta = new Metadata();
        meta.setIrVersion(IR_VERSION);
        meta.setSourceType("PRTF");

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
