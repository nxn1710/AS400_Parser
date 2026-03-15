package com.as400parser.rpg3;

import com.as400parser.common.model.*;
import com.as400parser.common.normalizer.NormalizedSource;
import com.as400parser.common.normalizer.SourceNormalizer;
import com.as400parser.common.parser.As400Parser;
import com.as400parser.common.parser.ParseOptions;
import com.as400parser.common.serializer.IrJsonSerializer;
import com.as400parser.rpg3.model.Rpg3Content;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;

/**
 * Public API entry point for RPG3 parsing.
 * <p>
 * Orchestrates the 8-step pipeline:
 * <ol>
 *   <li>Normalize source (tabs, padding, sequence numbers)</li>
 *   <li>Lex (ANTLR Rpg3Lexer)</li>
 *   <li>Parse (ANTLR Rpg3Parser, SLL → LL fallback)</li>
 *   <li>Build IR (Rpg3IrBuilder visitor)</li>
 *   <li>Resolve /COPY members (if enabled)</li>
 *   <li>Build symbol table</li>
 *   <li>Populate metadata</li>
 *   <li>Return IrDocument</li>
 * </ol>
 */
public class Rpg3ParserFacade implements As400Parser {

    private static final String IR_VERSION = "1.0.0";
    private static final String SOURCE_TYPE = "rpg3";

    // =========================================================================
    // As400Parser interface implementation
    // =========================================================================

    @Override
    public IrDocument parse(Path sourceFile, ParseOptions options) {
        try {
            SourceNormalizer normalizer = createNormalizer(options);
            NormalizedSource normalized = normalizer.normalize(sourceFile, options.getCharset());
            IrDocument doc = runPipeline(normalized, options);
            populateMetadataFromFile(doc, sourceFile);
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
        return List.of(".rpg", ".rpg3", ".rpgsrc", ".mbr");
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

        // Populate metadata (no grammar errors since we don't parse with ANTLR)
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
        Map<String, Object> parseInfo = new LinkedHashMap<>();
        parseInfo.put("parseDate", Instant.now().toString());
        parseInfo.put("totalLines", normalized.getLineCount());

        // Determine parse status
        List<ParseError> errors = errorListener.getErrors();
        if (errors.isEmpty()) {
            parseInfo.put("parseStatus", "complete");
        } else {
            parseInfo.put("parseStatus", "partial");
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
        parseInfo.put("errors", errorList);

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
        parseInfo.put("warnings", warningList);

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

        Map<String, Object> parseInfo = new LinkedHashMap<>();
        parseInfo.put("parseDate", Instant.now().toString());
        parseInfo.put("parseStatus", "failed");
        parseInfo.put("errors", List.of(Map.of(
                "line", 0,
                "column", 0,
                "message", errorMessage,
                "severity", "ERROR"
        )));
        parseInfo.put("warnings", List.of());
        meta.setParseInfo(parseInfo);

        doc.setMetadata(meta);
        return doc;
    }

    // =========================================================================
    // CLI main() — invoked by `java -jar`
    // =========================================================================

    /**
     * CLI entry point. Usage:
     * <pre>
     *   java -jar as400-parser-core.jar --source FILE [--charset CHARSET] [--copy-path PATHS]
     * </pre>
     * Prints IR JSON to stdout.
     */
    public static void main(String[] args) {
        String sourcePath = null;
        String charset = "UTF-8";
        String copyPath = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--source" -> { if (i + 1 < args.length) sourcePath = args[++i]; }
                case "--charset" -> { if (i + 1 < args.length) charset = args[++i]; }
                case "--copy-path" -> { if (i + 1 < args.length) copyPath = args[++i]; }
            }
        }

        if (sourcePath == null) {
            System.err.println("Usage: java -jar as400-parser-core.jar --source FILE [--charset CHARSET] [--copy-path PATHS]");
            System.exit(1);
        }

        ParseOptions.Builder optBuilder = ParseOptions.builder()
                .charset(java.nio.charset.Charset.forName(charset));

        if (copyPath != null) {
            optBuilder.copyPaths(List.of(copyPath.split("[;:]")));
            optBuilder.resolveCopies(true);
        }

        Rpg3ParserFacade facade = new Rpg3ParserFacade();
        IrDocument doc = facade.parse(Path.of(sourcePath), optBuilder.build());

        IrJsonSerializer serializer = new IrJsonSerializer();
        // Use UTF-8 output to preserve non-ASCII characters (e.g., Japanese)
        PrintStream utf8Out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        utf8Out.println(serializer.serialize(doc));
    }
}
