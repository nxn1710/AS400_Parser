package com.as400parser.rpgle;

import com.as400parser.common.model.*;
import com.as400parser.common.normalizer.NormalizedSource;
import com.as400parser.common.normalizer.SourceNormalizer;
import com.as400parser.common.parser.As400Parser;
import com.as400parser.common.parser.ParseOptions;
import com.as400parser.rpgle.model.RpgleContent;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;

/**
 * Public API entry point for RPGLE parsing.
 * <p>
 * Orchestrates the pipeline:
 * <ol>
 *   <li>Normalize source (tabs, padding, sequence numbers)</li>
 *   <li>Build IR via dual parser (fixed + free)</li>
 *   <li>Resolve /COPY members (if enabled)</li>
 *   <li>Populate metadata</li>
 *   <li>Return IrDocument</li>
 * </ol>
 */
public class RpgleParserFacade implements As400Parser {

    private static final String IR_VERSION = "1.0.0";
    private static final String SOURCE_TYPE = "RPGLE";

    // =========================================================================
    // As400Parser interface
    // =========================================================================

    @Override
    public IrDocument parse(Path sourceFile, ParseOptions options) {
        try {
            options = autoDetectCopyPaths(sourceFile, options);
            SourceNormalizer normalizer = createNormalizer(options);
            NormalizedSource normalized = (options.getCharset() != null)
                    ? normalizer.normalize(sourceFile, options.getCharset())
                    : normalizer.normalize(sourceFile);

            IrDocument doc = runPipeline(normalized, options);
            populateMetadataFromFile(doc, sourceFile);

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
        SourceNormalizer normalizer = createNormalizer(options);
        NormalizedSource normalized = normalizer.normalize(sourceText);
        return runPipeline(normalized, options);
    }

    @Override
    public String getSourceType() {
        return SOURCE_TYPE;
    }

    @Override
    public List<String> getSupportedExtensions() {
        return List.of(".rpgle", ".sqlrpgle", ".rpgleinc");
    }

    // =========================================================================
    // Pipeline
    // =========================================================================

    private IrDocument runPipeline(NormalizedSource normalized, ParseOptions options) {
        RpgleIrBuilder irBuilder = new RpgleIrBuilder(normalized);
        IrDocument document = irBuilder.build();

        // Resolve /COPY members
        if (options.isResolveCopies()) {
            resolveCopyMembers(document, options);
        }

        // Populate metadata
        populateMetadata(document, normalized);

        return document;
    }

    // =========================================================================
    // Copy resolution
    // =========================================================================

    private void resolveCopyMembers(IrDocument document, ParseOptions options) {
        if (document.getDependencies() == null) return;
        if (options.getCopyPaths() == null || options.getCopyPaths().isEmpty()) return;

        List<Path> copyPaths = options.getCopyPaths().stream()
                .map(Path::of)
                .toList();
        Path sourceRoot = options.getSourceRoot() != null
                ? Path.of(options.getSourceRoot())
                : null;

        com.as400parser.rpg3.Rpg3CopyResolver resolver =
                new com.as400parser.rpg3.Rpg3CopyResolver(copyPaths, sourceRoot, options.getCharset());

        for (IrDocument.CopyMemberRef ref : document.getDependencies().getCopyMembers()) {
            if (!ref.isResolved()) {
                String directive = "/COPY " + buildDirectiveString(ref);
                var resolved = resolver.resolve(directive);
                if (resolved.isFound()) {
                    ref.setResolved(true);
                    ref.setResolvedPath(resolved.getResolvedPath());
                }
            }
        }
    }

    private String buildDirectiveString(IrDocument.CopyMemberRef ref) {
        StringBuilder sb = new StringBuilder();
        if (ref.getLibraryName() != null && !ref.getLibraryName().isEmpty()) {
            sb.append(ref.getLibraryName()).append("/");
        }
        if (ref.getFileName() != null && !ref.getFileName().isEmpty()) {
            sb.append(ref.getFileName()).append(",");
        }
        sb.append(ref.getMemberName());
        return sb.toString();
    }

    // =========================================================================
    // Auto-detect copy paths
    // =========================================================================

    private ParseOptions autoDetectCopyPaths(Path sourceFile, ParseOptions options) {
        if (options.getCopyPaths() != null && !options.getCopyPaths().isEmpty()) {
            return options;
        }

        Path sourceDir = sourceFile.getParent();
        if (sourceDir == null) return options;

        Path projectRoot = sourceDir.getParent();
        if (projectRoot == null) return options;

        java.io.File[] siblings = projectRoot.toFile().listFiles(java.io.File::isDirectory);
        if (siblings == null) return options;

        List<String> copyPaths = new ArrayList<>();
        for (java.io.File sibling : siblings) {
            String name = sibling.getName().toUpperCase();
            if (name.equals("QCPYSRC") || name.endsWith("CPYSRC")) {
                copyPaths.add(projectRoot.toAbsolutePath().toString());
                break;
            }
        }

        if (!copyPaths.isEmpty()) {
            return ParseOptions.builder()
                    .charset(options.getCharset())
                    .tabStops(options.getTabStops())
                    .copyPaths(copyPaths)
                    .resolveCopies(true)
                    .sourceRoot(projectRoot.toAbsolutePath().toString())
                    .build();
        }
        return options;
    }

    // =========================================================================
    // Metadata
    // =========================================================================

    private void populateMetadata(IrDocument document, NormalizedSource normalized) {
        Metadata metadata = document.getMetadata();
        if (metadata == null) {
            metadata = new Metadata();
            document.setMetadata(metadata);
        }

        metadata.setIrVersion(IR_VERSION);
        metadata.setSourceType(SOURCE_TYPE);

        Metadata.ParseInfo parseInfo = new Metadata.ParseInfo();
        parseInfo.setParsedAt(Instant.now().toString());
        parseInfo.setTotalLines(normalized.getLineCount());
        parseInfo.setParserVersion(IR_VERSION);

        if (document.getErrors().isEmpty()) {
            parseInfo.setParseStatus("complete");
        } else {
            parseInfo.setParseStatus("partial");
        }

        // Convert errors
        List<Map<String, Object>> errorList = new ArrayList<>();
        for (ParseError e : document.getErrors()) {
            Map<String, Object> errMap = new LinkedHashMap<>();
            errMap.put("line", e.getLine());
            errMap.put("column", e.getColumn());
            errMap.put("message", e.getMessage());
            errMap.put("severity", e.getSeverity().name());
            errorList.add(errMap);
        }
        parseInfo.setErrors(errorList);

        // Normalizer warnings
        List<Map<String, Object>> warningList = new ArrayList<>();
        for (var w : normalized.getWarnings()) {
            Map<String, Object> warnMap = new LinkedHashMap<>();
            warnMap.put("line", w.getLine());
            warnMap.put("column", w.getColumn());
            warnMap.put("message", w.getMessage());
            warnMap.put("type", w.getType().name());
            warningList.add(warnMap);
        }
        parseInfo.setWarnings(warningList);

        metadata.setParseInfo(parseInfo);
    }

    private void populateMetadataFromFile(IrDocument document, Path sourceFile) {
        Metadata metadata = document.getMetadata();
        if (metadata == null) return;

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

    private SourceNormalizer createNormalizer(ParseOptions options) {
        int[] tabStops = options.getTabStops();
        return (tabStops != null && tabStops.length > 0)
                ? new SourceNormalizer(tabStops, false)
                : new SourceNormalizer(SourceNormalizer.SEU_TAB_STOPS, false);
    }

    private IrDocument createFailedDocument(String errorMessage) {
        IrDocument doc = new IrDocument();
        Metadata meta = new Metadata();
        meta.setIrVersion(IR_VERSION);
        meta.setSourceType(SOURCE_TYPE);

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
