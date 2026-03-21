package com.as400parser.rpg3;

import com.as400parser.common.model.*;
import com.as400parser.common.normalizer.NormalizedSource;
import com.as400parser.common.normalizer.SourceNormalizer;
import com.as400parser.common.parser.As400Parser;
import com.as400parser.common.parser.ParseOptions;
import com.as400parser.rpg3.model.Rpg3Content;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;

/**
 * Public API entry point for RPG3 parsing.
 * <p>
 * Orchestrates the pipeline:
 * <ol>
 *   <li>Normalize source (tabs, padding, sequence numbers)</li>
 *   <li>Build IR via raw-line column extraction (Rpg3IrBuilder)</li>
 *   <li>Resolve /COPY members (if enabled)</li>
 *   <li>Build symbol table</li>
 *   <li>Populate metadata</li>
 *   <li>Return IrDocument</li>
 * </ol>
 */
public class Rpg3ParserFacade implements As400Parser {

    private static final String IR_VERSION = "1.0.0";
    private static final String SOURCE_TYPE = "RPG3";

    // =========================================================================
    // As400Parser interface implementation
    // =========================================================================

    @Override
    public IrDocument parse(Path sourceFile, ParseOptions options) {
        try {
            // Auto-detect sibling copy directories (QCPYSRC, etc.)
            options = autoDetectCopyPaths(sourceFile, options);

            SourceNormalizer normalizer = createNormalizer(options);
            NormalizedSource normalized = (options.getCharset() != null)
                ? normalizer.normalize(sourceFile, options.getCharset())
                : normalizer.normalize(sourceFile); // auto-detect encoding
            IrDocument doc = runPipeline(normalized, options);
            populateMetadataFromFile(doc, sourceFile);

            // Record detected encoding in metadata
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

    /**
     * Auto-detect sibling copy source directories from the source file location.
     * If source is in QRPGSRC, looks for QCPYSRC (and other Q*SRC) siblings.
     */
    private ParseOptions autoDetectCopyPaths(Path sourceFile, ParseOptions options) {
        // Skip if user already specified copy paths
        if (options.getCopyPaths() != null && !options.getCopyPaths().isEmpty()) {
            return options;
        }

        Path sourceDir = sourceFile.getParent();
        if (sourceDir == null) return options;

        Path projectRoot = sourceDir.getParent();
        if (projectRoot == null) return options;

        // Look for sibling directories that look like copy source (QCPYSRC, etc.)
        java.io.File[] siblings = projectRoot.toFile().listFiles(java.io.File::isDirectory);
        if (siblings == null) return options;

        List<String> copyPaths = new ArrayList<>();
        for (java.io.File sibling : siblings) {
            String name = sibling.getName().toUpperCase();
            if (name.equals("QCPYSRC") || name.endsWith("CPYSRC")) {
                // Add the project root as copy path (resolver appends FILE/ to it)
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
        return List.of(".rpg", ".rpg3", ".rpgsrc", ".mbr", ".cpy", ".cpysrc");
    }

    // =========================================================================
    // Pipeline
    // =========================================================================

    private IrDocument runPipeline(NormalizedSource normalized, ParseOptions options) {
        // Build IR using raw-line column extraction (grammar-independent)
        Rpg3IrBuilder irBuilder = new Rpg3IrBuilder(normalized);
        IrDocument document = irBuilder.build();

        // Resolve /COPY members (if enabled)
        if (options.isResolveCopies()) {
            resolveCopyMembers(document, options);
        }

        // Build symbol table
        Object content = document.getContent();
        if (content instanceof Rpg3Content rpg3Content) {
            new Rpg3SymbolTableBuilder(rpg3Content).build();
        }

        // Populate metadata
        Rpg3ErrorListener errorListener = new Rpg3ErrorListener(normalized.getOriginalLineNumbers());
        populateMetadata(document, normalized, errorListener);

        return document;
    }



    // =========================================================================
    // Copy resolution
    // =========================================================================

    private void resolveCopyMembers(IrDocument document, ParseOptions options) {
        if (document.getDependencies() == null) return;

        List<Path> copyPaths = options.getCopyPaths().stream()
                .map(Path::of)
                .toList();
        Path sourceRoot = options.getSourceRoot() != null
                ? Path.of(options.getSourceRoot())
                : null;

        Rpg3CopyResolver resolver = new Rpg3CopyResolver(copyPaths, sourceRoot, options.getCharset());

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
    // Metadata
    // =========================================================================

    private void populateMetadata(IrDocument document, NormalizedSource normalized,
                                  Rpg3ErrorListener errorListener) {
        Metadata metadata = document.getMetadata();
        if (metadata == null) {
            metadata = new Metadata();
            document.setMetadata(metadata);
        }

        metadata.setIrVersion(IR_VERSION);
        metadata.setSourceType(SOURCE_TYPE);

        // parseInfo
        Metadata.ParseInfo parseInfo = new Metadata.ParseInfo();
        parseInfo.setParsedAt(Instant.now().toString());
        parseInfo.setTotalLines(normalized.getLineCount());
        parseInfo.setParserVersion(IR_VERSION);

        // Determine parse status
        List<ParseError> errors = errorListener.getErrors();
        if (errors.isEmpty()) {
            parseInfo.setParseStatus("complete");
        } else {
            parseInfo.setParseStatus("partial");
        }

        // Convert errors and warnings
        List<Map<String, Object>> errorList = new ArrayList<>();
        for (ParseError e : errors) {
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

        // Attach errors to document
        document.setErrors(errors);
    }

    private void populateMetadataFromFile(IrDocument document, Path sourceFile) {
        Metadata metadata = document.getMetadata();
        if (metadata == null) return;

        String fileName = sourceFile.getFileName().toString();
        // Remove extension
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

    private SourceNormalizer createNormalizer(ParseOptions options) {
        int[] tabStops = options.getTabStops();
        return (tabStops != null && tabStops.length > 0)
                ? new SourceNormalizer(tabStops)
                : new SourceNormalizer();
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
