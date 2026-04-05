package com.as400parser.rpgle;

import com.as400parser.common.model.*;
import com.as400parser.common.normalizer.NormalizedSource;
import com.as400parser.common.normalizer.SourceNormalizer;
import com.as400parser.common.parser.As400Parser;
import com.as400parser.common.parser.ParseOptions;
import com.as400parser.rpgle.model.*;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;

/**
 * Public API entry point for RPGLE/SQLRPGLE source file parsing.
 * <p>
 * Orchestrates the parsing pipeline following the established
 * {@code ClParserFacade} pattern:
 * <ol>
 *   <li>Detect encoding and normalize source (SourceNormalizer)</li>
 *   <li>Detect format: fully free (**FREE) vs fixed/mixed</li>
 *   <li>Route to appropriate parser engine</li>
 *   <li>Build RPGLE IR content model</li>
 *   <li>Extract dependencies</li>
 *   <li>Populate metadata (sourceType, parseInfo)</li>
 *   <li>Return a fully populated IrDocument</li>
 * </ol>
 *
 * <p>Handles both RPGLE source variants:
 * <ul>
 *   <li>{@code .rpgle}    → sourceType = "RPGLE"</li>
 *   <li>{@code .sqlrpgle}  → sourceType = "SQLRPGLE"</li>
 * </ul>
 *
 * <p>Implements {@link As400Parser} to integrate with the unified CLI.
 */
public class RpgleParserFacade implements As400Parser {

    private static final String IR_VERSION = "1.0.0";

    private static final Map<String, String> EXTENSION_SOURCE_TYPE = Map.of(
            ".rpgle",    "RPGLE",
            ".sqlrpgle", "SQLRPGLE"
    );

    // =========================================================================
    // As400Parser interface
    // =========================================================================

    @Override
    public IrDocument parse(Path sourceFile, ParseOptions options) {
        try {
            SourceNormalizer normalizer = new SourceNormalizer(new int[0], false);
            NormalizedSource normalized = (options.getCharset() != null)
                    ? normalizer.normalize(sourceFile, options.getCharset())
                    : normalizer.normalize(sourceFile);

            String sourceType = resolveSourceType(sourceFile);
            IrDocument doc = runPipeline(normalized, sourceType);

            populateMetadataFromFile(doc, sourceFile, sourceType);

            if (normalized.getDetectedCharset() != null
                    && doc.getMetadata() != null
                    && doc.getMetadata().getParseInfo() != null) {
                doc.getMetadata().getParseInfo()
                        .setDetectedEncoding(normalized.getDetectedCharset().name());
            }

            return doc;
        } catch (IOException e) {
            return createFailedDocument(e.getMessage(), "RPGLE");
        }
    }

    @Override
    public IrDocument parse(String sourceText, ParseOptions options) {
        SourceNormalizer normalizer = new SourceNormalizer(new int[0], false);
        NormalizedSource normalized = normalizer.normalize(sourceText);
        return runPipeline(normalized, "RPGLE");
    }

    @Override
    public String getSourceType() {
        return "RPGLE";
    }

    @Override
    public List<String> getSupportedExtensions() {
        return List.of(".rpgle", ".sqlrpgle");
    }

    // =========================================================================
    // Pipeline
    // =========================================================================

    private IrDocument runPipeline(NormalizedSource normalized, String sourceType) {
        List<String> lines = Arrays.asList(normalized.getLines());
        boolean isFullyFree = detectFullyFree(lines);

        RpgleContent content;
        if (isFullyFree) {
            content = parseFullyFree(lines);
        } else {
            content = parseFixedOrMixed(lines, normalized.getSequenceNumbers());
        }

        // Delegate to RpgleIrBuilder for IrDocument assembly
        RpgleIrBuilder builder = new RpgleIrBuilder();
        return builder.build(content, normalized, sourceType);
    }

    // =========================================================================
    // Format detection
    // =========================================================================

    private boolean detectFullyFree(List<String> lines) {
        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) continue;
            return line.trim().toUpperCase().startsWith("**FREE");
        }
        return false;
    }

    // =========================================================================
    // Parsing delegators
    // =========================================================================

    private RpgleContent parseFullyFree(List<String> lines) {
        RpgleFreeParser freeParser = new RpgleFreeParser();
        List<FreeFormatStatement> stmts = freeParser.parseFullyFree(lines);

        RpgleContent content = new RpgleContent();
        content.setFormatType("free");
        content.setFreeFormatStatements(stmts);
        content.setParseErrors(freeParser.getErrors());
        return content;
    }

    private RpgleContent parseFixedOrMixed(List<String> lines, String[] seqNumbers) {
        RpgleFixedParser fixedParser = new RpgleFixedParser();
        return fixedParser.parse(lines, seqNumbers);
    }

    // =========================================================================
    // File-level metadata (member name, source file, library path)
    // =========================================================================

    private void populateMetadataFromFile(IrDocument doc, Path sourceFile, String sourceType) {
        Metadata metadata = doc.getMetadata();
        if (metadata == null) return;

        metadata.setSourceType(sourceType);

        String fileName = sourceFile.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        String memberName = (dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName).toUpperCase();
        metadata.setSourceMember(memberName);

        Path parent = sourceFile.getParent();
        if (parent != null) {
            metadata.setSourceFile(parent.getFileName().toString());
            Path grandParent = parent.getParent();
            if (grandParent != null) {
                metadata.setSourceLibrary(grandParent.getFileName().toString());
            }
        }
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private String resolveSourceType(Path sourceFile) {
        String name = sourceFile.getFileName().toString().toLowerCase();
        int dot = name.lastIndexOf('.');
        if (dot >= 0) {
            String ext = name.substring(dot);
            return EXTENSION_SOURCE_TYPE.getOrDefault(ext, "RPGLE");
        }
        return "RPGLE";
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
