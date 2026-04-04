package com.as400parser.common.cli;

import com.as400parser.common.model.IrDocument;
import com.as400parser.common.parser.As400Parser;
import com.as400parser.common.parser.ParseOptions;
import com.as400parser.common.serializer.IrJsonSerializer;
import com.as400parser.cl.ClParserFacade;
import com.as400parser.dds.DdsParserFacade;
import com.as400parser.dspf.DspfParserFacade;
import com.as400parser.prtf.PrtfParserFacade;
import com.as400parser.rpg3.Rpg3ParserFacade;

import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Unified CLI entry point for all AS/400 source parsers.
 * <p>
 * Dispatches to the appropriate parser (RPG3, DDS, DSPF, PRTF) based on file extension.
 * Supports both single-file and batch (directory) parsing modes.
 *
 * <pre>
 *   java -jar as400-parser-core-all.jar --source FILE [OPTIONS]
 *   java -jar as400-parser-core-all.jar --source-dir DIR [OPTIONS]
 * </pre>
 */
public class As400ParserCli {

    /** All known parsers, registered in order. */
    private static final List<As400Parser> PARSERS = List.of(
            new DdsParserFacade(),
            new DspfParserFacade(),
            new PrtfParserFacade(),
            new Rpg3ParserFacade(),
            new ClParserFacade()
    );

    /** All supported extensions across all parsers. */
    private static final Set<String> ALL_EXTENSIONS;
    static {
        Set<String> exts = new LinkedHashSet<>();
        for (As400Parser p : PARSERS) {
            exts.addAll(p.getSupportedExtensions());
        }
        ALL_EXTENSIONS = Collections.unmodifiableSet(exts);
    }

    // =========================================================================
    // Parser dispatch
    // =========================================================================

    /**
     * Select the appropriate parser for a file based on its extension.
     * Falls back to Rpg3ParserFacade if no specific parser matches.
     */
    public static As400Parser selectParser(Path file) {
        String name = file.getFileName().toString().toLowerCase();
        for (As400Parser parser : PARSERS) {
            for (String ext : parser.getSupportedExtensions()) {
                if (name.endsWith(ext)) {
                    return parser;
                }
            }
        }
        // Default fallback
        return new Rpg3ParserFacade();
    }

    // =========================================================================
    // CLI main()
    // =========================================================================

    public static void main(String[] args) {
        if (args.length == 0 || List.of(args).contains("--help") || List.of(args).contains("-h")) {
            printUsage();
            return;
        }

        String sourcePath = null;
        String sourceDirPath = null;
        String outputPath = null;
        String outputDirPath = null;
        String charset = "auto";
        String copyPath = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--source" -> { if (i + 1 < args.length) sourcePath = args[++i]; }
                case "--source-dir" -> { if (i + 1 < args.length) sourceDirPath = args[++i]; }
                case "--output", "-o" -> { if (i + 1 < args.length) outputPath = args[++i]; }
                case "--output-dir" -> { if (i + 1 < args.length) outputDirPath = args[++i]; }
                case "--charset" -> { if (i + 1 < args.length) charset = args[++i]; }
                case "--copy-path" -> { if (i + 1 < args.length) copyPath = args[++i]; }
            }
        }

        if (sourcePath == null && sourceDirPath == null) {
            System.err.println("Error: --source FILE or --source-dir DIR is required.");
            printUsage();
            System.exit(1);
        }

        ParseOptions.Builder optBuilder = ParseOptions.builder()
                .charset("auto".equalsIgnoreCase(charset) ? null : Charset.forName(charset));

        if (copyPath != null) {
            optBuilder.copyPaths(List.of(copyPath.split("[;:]")));
            optBuilder.resolveCopies(true);
        }

        IrJsonSerializer serializer = new IrJsonSerializer();

        if (sourceDirPath != null) {
            parseBatch(serializer, Path.of(sourceDirPath), outputDirPath, optBuilder.build());
        } else {
            Path sourceFilePath = Path.of(sourcePath);
            As400Parser parser = selectParser(sourceFilePath);
            parseSingleFile(parser, serializer, sourceFilePath, outputPath, optBuilder.build());
        }
    }

    // =========================================================================
    // Single file parsing
    // =========================================================================

    private static void parseSingleFile(As400Parser parser, IrJsonSerializer serializer,
                                         Path source, String outputPath, ParseOptions options) {
        try {
            IrDocument doc = parser.parse(source, options);
            String json = serializer.serialize(doc);

            if (outputPath != null) {
                Files.writeString(Path.of(outputPath), json, StandardCharsets.UTF_8);
                System.err.println("Written: " + outputPath);
            } else {
                PrintStream utf8Out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
                utf8Out.println(json);
            }
        } catch (Exception e) {
            System.err.println("Error parsing " + source + ": " + e.getMessage());
            System.exit(1);
        }
    }

    // =========================================================================
    // Batch parsing
    // =========================================================================

    private static void parseBatch(IrJsonSerializer serializer,
                                    Path sourceDir, String outputDirPath, ParseOptions options) {
        try {
            Path outDir = outputDirPath != null ? Path.of(outputDirPath) : sourceDir.resolve("output");
            Files.createDirectories(outDir);

            // Recursively find all supported source files
            List<Path> files;
            try (var stream = Files.walk(sourceDir)) {
                files = stream.filter(Files::isRegularFile)
                    .filter(p -> {
                        String name = p.getFileName().toString().toLowerCase();
                        int dot = name.lastIndexOf('.');
                        return dot >= 0 && ALL_EXTENSIONS.contains(name.substring(dot));
                    })
                    .toList();
            }

            if (files.isEmpty()) {
                System.err.println("No source files found in: " + sourceDir);
                return;
            }

            System.err.println("Found " + files.size() + " source files");

            // Pass 1: Parse all files and collect IrDocuments
            List<IrDocument> allDocuments = new ArrayList<>();
            List<Path> parsedFiles = new ArrayList<>();
            int failed = 0;

            for (Path file : files) {
                try {
                    As400Parser parser = selectParser(file);
                    IrDocument doc = parser.parse(file, options);
                    allDocuments.add(doc);
                    parsedFiles.add(file);
                } catch (Exception e) {
                    System.err.println("  ✗ " + file.getFileName() + ": " + e.getMessage());
                    failed++;
                }
            }

            // Pass 2: Resolve REFFLD cross-references across all DDS documents
            com.as400parser.dds.DdsRefResolver resolver = new com.as400parser.dds.DdsRefResolver();
            int resolvedCount = resolver.resolveAll(allDocuments);
            if (resolvedCount > 0) {
                System.err.println("  Resolved " + resolvedCount + " REFFLD reference(s)");
            }

            // Pass 3: Serialize all documents to JSON
            int success = 0;
            for (int i = 0; i < allDocuments.size(); i++) {
                Path file = parsedFiles.get(i);
                IrDocument doc = allDocuments.get(i);
                try {
                    String json = serializer.serialize(doc);

                    // Preserve directory structure
                    Path relPath = sourceDir.relativize(file);
                    Path outFile = outDir.resolve(relPath.getParent() != null
                            ? relPath.getParent().resolve(file.getFileName() + ".json")
                            : Path.of(file.getFileName() + ".json"));
                    Files.createDirectories(outFile.getParent());
                    Files.writeString(outFile, json, StandardCharsets.UTF_8);
                    System.err.println("  ✓ " + relPath + " → " + outDir.relativize(outFile));
                    success++;
                } catch (Exception e) {
                    System.err.println("  ✗ " + file.getFileName() + ": " + e.getMessage());
                    failed++;
                }
            }
            System.err.println("\nBatch complete: " + success + " succeeded, " + failed + " failed");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    // =========================================================================
    // Usage
    // =========================================================================

    private static void printUsage() {
        System.out.println("AS400 Source Parser v1.0.0");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("  java -jar as400-parser-core-all.jar --source FILE [OPTIONS]");
        System.out.println("  java -jar as400-parser-core-all.jar --source-dir DIR [OPTIONS]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  --source FILE       Parse a single source file");
        System.out.println("  --source-dir DIR    Parse all source files in a directory (recursive)");
        System.out.println("  --output FILE       Write output to file (single mode, default: stdout)");
        System.out.println("  --output-dir DIR    Write output files to directory (batch mode)");
        System.out.println("  --charset CHARSET   Source encoding (default: auto-detect)");
        System.out.println("  --copy-path PATHS   Semicolon-separated /COPY search paths (RPG3 only)");
        System.out.println("  --help, -h          Show this help message");
        System.out.println();
        System.out.println("Supported source types:");
        System.out.println("  RPG3: .rpg, .rpg3, .rpgsrc, .mbr, .cpy, .cpysrc");
        System.out.println("  DDS:  .pf, .lf");
        System.out.println("  DSPF: .dspf");
        System.out.println("  PRTF: .prtf");
        System.out.println("  CL:   .cl, .clp, .clle");
        System.out.println();
        System.out.println("Parser is auto-detected from file extension.");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java -jar as400-parser-core-all.jar --source CUSTINQ.rpg");
        System.out.println("  java -jar as400-parser-core-all.jar --source STUDNTPF.pf -o studntpf.json");
        System.out.println("  java -jar as400-parser-core-all.jar --source-dir ./src --output-dir ./output");
    }
}
