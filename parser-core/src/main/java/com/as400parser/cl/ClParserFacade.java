package com.as400parser.cl;

import com.as400parser.cl.model.*;
import com.as400parser.common.model.*;
import com.as400parser.common.normalizer.NormalizedSource;
import com.as400parser.common.normalizer.SourceNormalizer;
import com.as400parser.common.parser.As400Parser;
import com.as400parser.common.parser.ParseOptions;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;

/**
 * Public API entry point for CL/CLP/CLLE source file parsing.
 * <p>
 * Orchestrates the parsing pipeline:
 * <ol>
 *   <li>Detect encoding and normalize source (SourceNormalizer)</li>
 *   <li>Build CL IR content model (ClIrBuilder)</li>
 *   <li>Extract dependencies (called programs, files, message files, libraries)</li>
 *   <li>Populate metadata (sourceType, parseInfo)</li>
 *   <li>Return a fully populated IrDocument</li>
 * </ol>
 *
 * <p>Handles all three CL source variants with a single parser:
 * <ul>
 *   <li>{@code .cl}   → sourceType = "CL"</li>
 *   <li>{@code .clp}  → sourceType = "CLP"</li>
 *   <li>{@code .clle} → sourceType = "CLLE"</li>
 * </ul>
 *
 * <p>Implements {@link As400Parser} to integrate with the unified CLI.
 */
public class ClParserFacade implements As400Parser {

    private static final String IR_VERSION = "1.0.0";

    /** Maps file extensions to their canonical sourceType strings. */
    private static final Map<String, String> EXTENSION_SOURCE_TYPE = Map.of(
            ".cl",   "CL",
            ".clp",  "CLP",
            ".clle", "CLLE"
    );

    // =========================================================================
    // As400Parser interface
    // =========================================================================

    /**
     * Parse a CL source file from disk.
     * The encoding is auto-detected unless overridden in {@code options}.
     */
    @Override
    public IrDocument parse(Path sourceFile, ParseOptions options) {
        try {
            SourceNormalizer normalizer = new SourceNormalizer(new int[0], false);
            NormalizedSource normalized = (options.getCharset() != null)
                    ? normalizer.normalize(sourceFile, options.getCharset())
                    : normalizer.normalize(sourceFile);

            // Derive sourceType from file extension
            String sourceType = resolveSourceType(sourceFile);
            IrDocument doc = runPipeline(normalized, sourceType);

            // Populate file-level metadata
            populateMetadataFromFile(doc, sourceFile, sourceType);

            // Record detected encoding if available
            if (normalized.getDetectedCharset() != null
                    && doc.getMetadata() != null
                    && doc.getMetadata().getParseInfo() != null) {
                doc.getMetadata().getParseInfo()
                        .setDetectedEncoding(normalized.getDetectedCharset().name());
            }

            return doc;
        } catch (IOException e) {
            return createFailedDocument(e.getMessage(), "CL");
        }
    }

    /**
     * Parse CL source text provided as a string.
     * The sourceType defaults to "CLLE" for string inputs (no extension available).
     */
    @Override
    public IrDocument parse(String sourceText, ParseOptions options) {
        SourceNormalizer normalizer = new SourceNormalizer(new int[0], false);
        NormalizedSource normalized = normalizer.normalize(sourceText);
        return runPipeline(normalized, "CLLE");
    }

    /**
     * Returns the base source type for this parser.
     * Individual documents may carry "CL", "CLP", or "CLLE" per their extension.
     */
    @Override
    public String getSourceType() {
        return "CL";
    }

    /** Returns all CL source file extensions handled by this parser. */
    @Override
    public List<String> getSupportedExtensions() {
        return List.of(".cl", ".clp", ".clle");
    }

    // =========================================================================
    // Pipeline
    // =========================================================================

    /**
     * Core pipeline: normalize → build → extract dependencies → populate metadata.
     *
     * @param normalized the normalized source
     * @param sourceType "CL", "CLP", or "CLLE"
     * @return fully populated IrDocument
     */
    private IrDocument runPipeline(NormalizedSource normalized, String sourceType) {
        // 1. Build CL IR content
        ClIrBuilder builder = new ClIrBuilder();
        List<String> lines = Arrays.asList(normalized.getLines());
        ClContent content = builder.buildContent(lines, normalized.getSequenceNumbers());

        // 2. Create IrDocument and set content
        IrDocument doc = new IrDocument();
        doc.setContent(content);

        // 3. Extract and populate dependencies
        IrDocument.Dependencies deps = new IrDocument.Dependencies();
        deps.setCalledPrograms(extractCalledPrograms(content));
        deps.setReferencedFiles(extractReferencedFiles(content));
        deps.setCopyMembers(List.of());
        doc.setDependencies(deps);

        // 4. Propagate parse errors
        if (content.getParseErrors() != null && !content.getParseErrors().isEmpty()) {
            doc.setErrors(new ArrayList<>(content.getParseErrors()));
        } else {
            doc.setErrors(new ArrayList<>());
        }

        // 5. Populate metadata
        populateMetadata(doc, normalized, sourceType);

        return doc;
    }

    // =========================================================================
    // Dependency extraction
    // =========================================================================

    /**
     * Extracts all programs called via {@code CALL PGM(lib/pgm)} commands.
     */
    private List<IrDocument.DependencyRef> extractCalledPrograms(ClContent content) {
        List<IrDocument.DependencyRef> result = new ArrayList<>();
        for (ClCommand cmd : content.getCommands()) {
            collectCalledPrograms(cmd, result);
        }
        // Also scan subroutine commands
        for (ClSubroutine sub : content.getSubroutines()) {
            for (ClCommand cmd : sub.getCommands()) {
                collectCalledPrograms(cmd, result);
            }
        }
        return result;
    }

    private void collectCalledPrograms(ClCommand cmd, List<IrDocument.DependencyRef> result) {
        if ("CALL".equalsIgnoreCase(cmd.getName())) {
            String pgm = getParamValue(cmd.getParameters(), "PGM", 0);
            if (pgm != null && !pgm.isBlank()) {
                IrDocument.DependencyRef ref = new IrDocument.DependencyRef(pgm, "call");
                if (cmd.getLocation() != null) {
                    ref.getLocations().add(cmd.getLocation());
                }
                result.add(ref);
            }
        }
        // Recurse into EXEC commands (e.g., MONMSG EXEC(CALL PGM(...)))
        for (ClCommand exec : cmd.getExecCommands()) {
            collectCalledPrograms(exec, result);
        }
    }

    /**
     * Extracts all referenced files, message files, and libraries from CL commands:
     * <ul>
     *   <li>{@code DCLF FILE(lib/file)} → referenceType = "file-declaration"</li>
     *   <li>{@code OVRDBF FILE(x) TOFILE(y)} → referenceType = "file-override"</li>
     *   <li>{@code SNDPGMMSG MSGF(lib/msgf)} → referenceType = "message-file"</li>
     *   <li>{@code ADDLIBLE LIB(lib)} → referenceType = "library"</li>
     * </ul>
     */
    private List<IrDocument.DependencyRef> extractReferencedFiles(ClContent content) {
        List<IrDocument.DependencyRef> result = new ArrayList<>();
        List<ClCommand> allCmds = new ArrayList<>(content.getCommands());
        for (ClSubroutine sub : content.getSubroutines()) {
            allCmds.addAll(sub.getCommands());
        }

        for (ClCommand cmd : allCmds) {
            collectReferencedFiles(cmd, result);
        }

        // Add file declarations (DCLF is no longer in the commands list)
        for (com.as400parser.cl.model.ClFileDeclaration fd : content.getFileDeclarations()) {
            if (fd.getFileName() != null) {
                IrDocument.DependencyRef ref = new IrDocument.DependencyRef(fd.getFileName(), "file-declaration");
                if (fd.getLocation() != null) {
                    ref.getLocations().add(fd.getLocation());
                }
                result.add(ref);
            }
        }

        return result;
    }

    private void collectReferencedFiles(ClCommand cmd, List<IrDocument.DependencyRef> result) {
        String name = cmd.getName();
        if (name == null) return;

        switch (name.toUpperCase()) {

            // ── File override commands ─────────────────────────────────────────
            // TOFILE parameter is the actual dependency target when overriding
            case "OVRDBF" -> {
                String toFile = getParamValue(cmd.getParameters(), "TOFILE", -1);
                String file = getParamValue(cmd.getParameters(), "FILE", 0);
                if (toFile != null) addRef(result, toFile, "file-override", cmd);
                else if (file != null) addRef(result, file, "file-override", cmd);
            }
            case "OVRDSPF" -> {
                String toFile = getParamValue(cmd.getParameters(), "TOFILE", -1);
                String file = getParamValue(cmd.getParameters(), "FILE", 0);
                if (toFile != null) addRef(result, toFile, "file-override", cmd);
                else if (file != null) addRef(result, file, "file-override", cmd);
            }
            case "OVRPRTF" -> {
                String toFile = getParamValue(cmd.getParameters(), "TOFILE", -1);
                String file = getParamValue(cmd.getParameters(), "FILE", 0);
                if (toFile != null) addRef(result, toFile, "file-override", cmd);
                else if (file != null) addRef(result, file, "file-override", cmd);
            }
            case "OVRICFF", "OVRTAPF", "OVRSAVF" -> {
                String toFile = getParamValue(cmd.getParameters(), "TOFILE", -1);
                String file = getParamValue(cmd.getParameters(), "FILE", 0);
                if (toFile != null) addRef(result, toFile, "file-override", cmd);
                else if (file != null) addRef(result, file, "file-override", cmd);
            }

            // ── Remove overrides ──────────────────────────────────────────────
            case "DLTOVR" -> {
                String file = getParamValue(cmd.getParameters(), "FILE", 0);
                if (file != null && !"*ALL".equals(file)) addRef(result, file, "file-override-delete", cmd);
            }

            // ── File open/read/write/close operations ─────────────────────────
            case "OPNDBF", "OPNQRYF" -> {
                String file = getParamValue(cmd.getParameters(), "FILE", 0);
                if (file != null) addRef(result, file, "file-open", cmd);
            }
            case "CLOF", "CLRPFM" -> {
                String file = getParamValue(cmd.getParameters(), "FILE", 0);
                if (file != null) addRef(result, file, "file-close", cmd);
            }
            case "RCVF" -> {
                String file = getParamValue(cmd.getParameters(), "FILE", -1);
                String dev = getParamValue(cmd.getParameters(), "DEV", -1);
                if (file != null) addRef(result, file, "file-read", cmd);
                else if (dev != null) addRef(result, dev, "device-read", cmd);
            }
            case "SNDF", "SNDRCVF" -> {
                String file = getParamValue(cmd.getParameters(), "FILE", -1);
                if (file != null) addRef(result, file, "file-write", cmd);
            }

            // ── Message operations ────────────────────────────────────────────
            case "SNDPGMMSG", "SNDSRVPGMMSG" -> {
                String msgf = getParamValue(cmd.getParameters(), "MSGF", -1);
                if (msgf != null && !msgf.startsWith("*")) addRef(result, msgf, "message-file", cmd);
            }
            case "SNDUSRMSG", "SNDBRKMSG" -> {
                String msgf = getParamValue(cmd.getParameters(), "MSGF", -1);
                if (msgf != null && !msgf.startsWith("*")) addRef(result, msgf, "message-file", cmd);
            }
            case "SNDINQMSG", "SNDMSG" -> {
                String msgf = getParamValue(cmd.getParameters(), "MSGF", -1);
                if (msgf != null && !msgf.startsWith("*")) addRef(result, msgf, "message-file", cmd);
            }
            case "RCVMSG" -> {
                String msgq = getParamValue(cmd.getParameters(), "MSGQ", 0);
                if (msgq != null && !msgq.startsWith("*")) addRef(result, msgq, "message-queue", cmd);
            }
            case "RMVMSG" -> {
                String msgq = getParamValue(cmd.getParameters(), "MSGQ", 0);
                if (msgq != null && !msgq.startsWith("*")) addRef(result, msgq, "message-queue", cmd);
            }
            case "RTVMSG" -> {
                String msgf = getParamValue(cmd.getParameters(), "MSGF", 0);
                if (msgf != null && !msgf.startsWith("*")) addRef(result, msgf, "message-file", cmd);
            }
            case "ADDMSGD" -> {
                String msgf = getParamValue(cmd.getParameters(), "MSGF", 0);
                if (msgf != null) addRef(result, msgf, "message-file-write", cmd);
            }
            case "OVRMSGF" -> {
                String toMsgf = getParamValue(cmd.getParameters(), "TOMSGF", -1);
                String msgf = getParamValue(cmd.getParameters(), "MSGF", 0);
                if (toMsgf != null) addRef(result, toMsgf, "message-file-override", cmd);
                else if (msgf != null) addRef(result, msgf, "message-file-override", cmd);
            }

            // ── Library management ────────────────────────────────────────────
            case "ADDLIBLE" -> {
                String lib = getParamValue(cmd.getParameters(), "LIB", 0);
                if (lib != null && !lib.startsWith("*")) addRef(result, lib, "library", cmd);
            }
            case "RMVLIBLE" -> {
                String lib = getParamValue(cmd.getParameters(), "LIB", 0);
                if (lib != null && !lib.startsWith("*")) addRef(result, lib, "library-remove", cmd);
            }
            case "CHGLIBL" -> {
                String libl = getParamValue(cmd.getParameters(), "LIBL", 0);
                if (libl != null && !libl.startsWith("*")) addRef(result, libl, "library", cmd);
            }
            case "CRTLIB" -> {
                String lib = getParamValue(cmd.getParameters(), "LIB", 0);
                if (lib != null) addRef(result, lib, "library-create", cmd);
            }
            case "DLTLIB" -> {
                String lib = getParamValue(cmd.getParameters(), "LIB", 0);
                if (lib != null) addRef(result, lib, "library-delete", cmd);
            }

            // ── Submit job ────────────────────────────────────────────────────
            // SBMJOB wraps a CMD parameter that is itself a CL command string
            case "SBMJOB" -> {
                String jobq = getParamValue(cmd.getParameters(), "JOBQ", -1);
                if (jobq != null && !jobq.startsWith("*")) addRef(result, jobq, "job-queue", cmd);
                // The CMD parameter may reference another program indirectly
            }

            // ── Data queue operations ─────────────────────────────────────────
            case "CRTDTAQ" -> {
                String dtaq = getParamValue(cmd.getParameters(), "DTAQ", 0);
                if (dtaq != null) addRef(result, dtaq, "data-queue-create", cmd);
            }
            case "DLTDTAQ" -> {
                String dtaq = getParamValue(cmd.getParameters(), "DTAQ", 0);
                if (dtaq != null) addRef(result, dtaq, "data-queue-delete", cmd);
            }
            case "SNDDTAARA", "SNDSMTPMSG" -> {
                // No standard file dependency
            }

            // ── Data area operations ──────────────────────────────────────────
            case "CRTDTAARA" -> {
                String dtaara = getParamValue(cmd.getParameters(), "DTAARA", 0);
                if (dtaara != null) addRef(result, dtaara, "data-area-create", cmd);
            }
            case "CHGDTAARA" -> {
                String dtaara = getParamValue(cmd.getParameters(), "DTAARA", 0);
                if (dtaara != null && !dtaara.startsWith("*")) addRef(result, dtaara, "data-area-write", cmd);
            }
            case "RTVDTAARA" -> {
                String dtaara = getParamValue(cmd.getParameters(), "DTAARA", 0);
                if (dtaara != null && !dtaara.startsWith("*")) addRef(result, dtaara, "data-area-read", cmd);
            }
            case "DLTDTAARA" -> {
                String dtaara = getParamValue(cmd.getParameters(), "DTAARA", 0);
                if (dtaara != null) addRef(result, dtaara, "data-area-delete", cmd);
            }

            // ── Retrieve commands ──────────────────────────────────────────────
            // RTVSYSVAL, RTVJOBA, RTVNETA: no file dependency (reads into variable)
            case "RTVSYSVAL", "RTVNETA" -> {
                // no file dependency; reads system value into a variable
            }
            case "RTVJOBA" -> {}
            case "RTVUSRPRF" -> {
                String usrprf = getParamValue(cmd.getParameters(), "USRPRF", 0);
                if (usrprf != null && !usrprf.startsWith("*")) addRef(result, usrprf, "user-profile", cmd);
            }
            case "RTVMBRD" -> {
                String file = getParamValue(cmd.getParameters(), "FILE", 0);
                if (file != null) addRef(result, file, "file-read", cmd);
            }
            case "RTVOBJD" -> {
                String obj = getParamValue(cmd.getParameters(), "OBJ", 0);
                if (obj != null && !obj.startsWith("*")) addRef(result, obj, "object-read", cmd);
            }

            // ── Object manipulation ───────────────────────────────────────────
            case "CRTPF", "CRTSRCPF" -> {
                String file = getParamValue(cmd.getParameters(), "FILE", 0);
                if (file != null) addRef(result, file, "file-create", cmd);
            }
            case "DLTF" -> {
                String file = getParamValue(cmd.getParameters(), "FILE", 0);
                if (file != null) addRef(result, file, "file-delete", cmd);
            }
            case "CPYF" -> {
                String fromFile = getParamValue(cmd.getParameters(), "FROMFILE", 0);
                String toFile = getParamValue(cmd.getParameters(), "TOFILE", 1);
                if (fromFile != null) addRef(result, fromFile, "file-read", cmd);
                if (toFile != null) addRef(result, toFile, "file-write", cmd);
            }
            case "CPYSRCF" -> {
                String fromFile = getParamValue(cmd.getParameters(), "FROMFILE", 0);
                String toFile = getParamValue(cmd.getParameters(), "TOFILE", 1);
                if (fromFile != null) addRef(result, fromFile, "file-read", cmd);
                if (toFile != null) addRef(result, toFile, "file-write", cmd);
            }
            case "RNMOBJ" -> {
                String obj = getParamValue(cmd.getParameters(), "OBJ", 0);
                if (obj != null) addRef(result, obj, "object-rename", cmd);
            }
            case "MOVOBJ" -> {
                String obj = getParamValue(cmd.getParameters(), "OBJ", 0);
                if (obj != null) addRef(result, obj, "object-move", cmd);
            }
            case "DLTPGM" -> {
                String pgm = getParamValue(cmd.getParameters(), "PGM", 0);
                if (pgm != null) addRef(result, pgm, "program-delete", cmd);
            }

            // ── Program creation / compilation ────────────────────────────────
            case "CRTCLPGM", "CRTBNDCL" -> {
                String pgm = getParamValue(cmd.getParameters(), "PGM", 0);
                String srcFile = getParamValue(cmd.getParameters(), "SRCFILE", -1);
                if (pgm != null) addRef(result, pgm, "program-create", cmd);
                if (srcFile != null) addRef(result, srcFile, "file-read", cmd);
            }
            case "CRTCLMOD" -> {
                String mod = getParamValue(cmd.getParameters(), "MODULE", 0);
                String srcFile = getParamValue(cmd.getParameters(), "SRCFILE", -1);
                if (mod != null) addRef(result, mod, "module-create", cmd);
                if (srcFile != null) addRef(result, srcFile, "file-read", cmd);
            }

            // ── Transfer of control ───────────────────────────────────────────
            case "TFRCTL" -> {
                String pgm = getParamValue(cmd.getParameters(), "PGM", 0);
                if (pgm != null) addRef(result, pgm, "call-transfer", cmd);
            }

            // ── Save/Restore operations ───────────────────────────────────────
            case "SAVOBJ" -> {
                String obj = getParamValue(cmd.getParameters(), "OBJ", 0);
                if (obj != null && !obj.startsWith("*")) addRef(result, obj, "object-save", cmd);
                String savf = getParamValue(cmd.getParameters(), "SAVF", -1);
                if (savf != null) addRef(result, savf, "file-write", cmd);
            }
            case "RSTOBJ" -> {
                String savf = getParamValue(cmd.getParameters(), "SAVF", -1);
                if (savf != null) addRef(result, savf, "file-read", cmd);
            }

            // All other commands: no specific dependency mapping needed
            // They are still parsed and stored as ClCommand entries in the content model
            default -> {}
        }

        // Always recurse into EXEC subcommands (from MONMSG EXEC(...))
        for (ClCommand exec : cmd.getExecCommands()) {
            collectReferencedFiles(exec, result);
        }
    }


    private void addRef(List<IrDocument.DependencyRef> result, String name,
                        String type, ClCommand cmd) {
        IrDocument.DependencyRef ref = new IrDocument.DependencyRef(name, type);
        if (cmd.getLocation() != null) ref.getLocations().add(cmd.getLocation());
        result.add(ref);
    }

    // =========================================================================
    // Metadata
    // =========================================================================

    private void populateMetadata(IrDocument doc, NormalizedSource normalized, String sourceType) {
        Metadata metadata = new Metadata();
        metadata.setIrVersion(IR_VERSION);
        metadata.setSourceType(sourceType);

        Metadata.ParseInfo parseInfo = new Metadata.ParseInfo();
        parseInfo.setParsedAt(Instant.now().toString());
        parseInfo.setTotalLines(normalized.getLineCount());
        parseInfo.setParserVersion(IR_VERSION);

        ClContent content = (ClContent) doc.getContent();
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
        doc.setMetadata(metadata);
    }

    private void populateMetadataFromFile(IrDocument doc, Path sourceFile, String sourceType) {
        Metadata metadata = doc.getMetadata();
        if (metadata == null) return;

        // Override sourceType derived from extension (already set in populateMetadata)
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

    /**
     * Resolves the canonical sourceType string from a file's extension.
     * Falls back to "CL" for any unrecognized extension.
     */
    private String resolveSourceType(Path sourceFile) {
        String name = sourceFile.getFileName().toString().toLowerCase();
        int dot = name.lastIndexOf('.');
        if (dot >= 0) {
            String ext = name.substring(dot);
            return EXTENSION_SOURCE_TYPE.getOrDefault(ext, "CL");
        }
        return "CL";
    }

    /**
     * Retrieves a parameter value from a parsed parameter list by keyword or positional index.
     *
     * @param params          the list of parsed parameters
     * @param keyword         the keyword to search for (case-insensitive)
     * @param positionalIndex 0-based positional fallback index; use -1 to skip
     * @return the parameter value, or null if not found
     */
    private String getParamValue(List<ClParameter> params, String keyword, int positionalIndex) {
        for (ClParameter p : params) {
            if (keyword.equalsIgnoreCase(p.getKeyword())) return p.getValue();
        }
        if (positionalIndex >= 0) {
            int pos = 0;
            for (ClParameter p : params) {
                if (p.isPositional()) {
                    if (pos == positionalIndex) return p.getValue();
                    pos++;
                }
            }
        }
        return null;
    }

    /**
     * Creates a minimal failed IrDocument with the given error message.
     * Used when an IOException occurs during file reading.
     */
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
