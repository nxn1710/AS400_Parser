package com.as400parser.rpgle;

import com.as400parser.common.model.*;
import com.as400parser.common.normalizer.NormalizedSource;
import com.as400parser.rpgle.model.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds a fully populated {@link IrDocument} from parsed RPGLE content.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Wrap {@link RpgleContent} into an {@code IrDocument} envelope</li>
 *   <li>Extract referenced files from F-specs</li>
 *   <li>Extract called programs from C-spec CALL/CALLP operations</li>
 *   <li>Extract /COPY and /INCLUDE directives into copy members</li>
 *   <li>Populate metadata (source type, parse info, line counts)</li>
 * </ul>
 */
public class RpgleIrBuilder {

    private static final String IR_VERSION = "1.0.0";

    /**
     * Build a complete {@link IrDocument} from parsed content.
     *
     * @param content    the parsed RPGLE content model
     * @param normalized the normalized source (for metadata)
     * @param sourceType "RPGLE" or "SQLRPGLE"
     * @return fully assembled IrDocument
     */
    public IrDocument build(RpgleContent content, NormalizedSource normalized, String sourceType) {
        IrDocument doc = new IrDocument();
        doc.setContent(content);

        // Dependencies
        IrDocument.Dependencies deps = new IrDocument.Dependencies();
        deps.setReferencedFiles(extractReferencedFiles(content));
        deps.setCalledPrograms(extractCalledPrograms(content));
        deps.setCopyMembers(extractCopyMembers(content));
        doc.setDependencies(deps);

        // Parse errors
        if (content.getParseErrors() != null && !content.getParseErrors().isEmpty()) {
            doc.setErrors(new ArrayList<>(content.getParseErrors()));
        } else {
            doc.setErrors(new ArrayList<>());
        }

        // Metadata
        populateMetadata(doc, normalized, sourceType);

        return doc;
    }

    // =========================================================================
    // Dependency: Referenced files from F-specs
    // =========================================================================

    private List<IrDocument.DependencyRef> extractReferencedFiles(RpgleContent content) {
        List<IrDocument.DependencyRef> refs = new ArrayList<>();
        if (content.getFileSpecs() != null) {
            for (FileSpec fs : content.getFileSpecs()) {
                if (fs.getFileName() != null && !fs.getFileName().isBlank()) {
                    IrDocument.DependencyRef ref = new IrDocument.DependencyRef(
                            fs.getFileName().trim(), mapFileType(fs.getFileType()));
                    if (fs.getLocation() != null) {
                        ref.getLocations().add(fs.getLocation());
                    }
                    refs.add(ref);
                }
            }
        }

        // Also extract dcl-f from free-format statements
        if (content.getFreeFormatStatements() != null) {
            for (FreeFormatStatement stmt : content.getFreeFormatStatements()) {
                if ("dcl-f".equals(stmt.getStatementType()) && stmt.getText() != null) {
                    String text = stmt.getText().trim();
                    // dcl-f FILENAME ...;
                    String[] parts = text.split("\\s+", 3);
                    if (parts.length >= 2) {
                        String fileName = parts[1].replaceAll(";$", "").trim();
                        IrDocument.DependencyRef ref = new IrDocument.DependencyRef(
                                fileName.toUpperCase(), "read");
                        if (stmt.getLocation() != null) {
                            ref.getLocations().add(stmt.getLocation());
                        }
                        refs.add(ref);
                    }
                }
            }
        }

        return refs;
    }

    private String mapFileType(String fileType) {
        if (fileType == null) return "read";
        return switch (fileType.trim().toUpperCase()) {
            case "I" -> "read";
            case "O" -> "write";
            case "U" -> "update";
            case "C" -> "combined";
            default -> "read";
        };
    }

    // =========================================================================
    // Dependency: Called programs from C-spec CALL/CALLP
    // =========================================================================

    private List<IrDocument.DependencyRef> extractCalledPrograms(RpgleContent content) {
        List<IrDocument.DependencyRef> programs = new ArrayList<>();

        // From C-specs: CALL and CALLP operations
        if (content.getCalcSpecs() != null) {
            for (CalcSpec cs : content.getCalcSpecs()) {
                String op = cs.getOpcode();
                if (op == null) continue;
                String opTrimmed = op.trim().toUpperCase();

                if ("CALL".equals(opTrimmed)) {
                    // Factor 2 contains the program name (possibly quoted)
                    String pgm = extractProgramName(cs.getFactor2());
                    if (pgm != null) {
                        IrDocument.DependencyRef ref = new IrDocument.DependencyRef(pgm, "call");
                        if (cs.getLocation() != null) {
                            ref.getLocations().add(cs.getLocation());
                        }
                        programs.add(ref);
                    }
                } else if ("CALLP".equals(opTrimmed)) {
                    // Extended Factor 2 contains the procedure call
                    String factor2 = cs.getFactor2();
                    if (factor2 != null) {
                        String proc = extractProcedureName(factor2.trim());
                        if (proc != null) {
                            IrDocument.DependencyRef ref = new IrDocument.DependencyRef(proc, "callp");
                            if (cs.getLocation() != null) {
                                ref.getLocations().add(cs.getLocation());
                            }
                            programs.add(ref);
                        }
                    }
                }
            }
        }

        // From free-format statements: callp
        if (content.getFreeFormatStatements() != null) {
            for (FreeFormatStatement stmt : content.getFreeFormatStatements()) {
                if ("callp".equals(stmt.getStatementType()) && stmt.getText() != null) {
                    String text = stmt.getText().trim();
                    // callp PROCNAME(args)
                    String remainder = text.substring(5).trim(); // skip "callp "
                    String proc = extractProcedureName(remainder);
                    if (proc != null) {
                        IrDocument.DependencyRef ref = new IrDocument.DependencyRef(proc, "callp");
                        if (stmt.getLocation() != null) {
                            ref.getLocations().add(stmt.getLocation());
                        }
                        programs.add(ref);
                    }
                }
            }
        }

        return programs;
    }

    /**
     * Extract program name from CALL factor 2 (possibly quoted).
     * Examples: 'MYPGM', MYLIB/MYPGM
     */
    private String extractProgramName(String factor2) {
        if (factor2 == null || factor2.isBlank()) return null;
        String trimmed = factor2.trim();
        // Remove surrounding quotes
        if (trimmed.startsWith("'") && trimmed.endsWith("'")) {
            trimmed = trimmed.substring(1, trimmed.length() - 1).trim();
        }
        return trimmed.isEmpty() ? null : trimmed;
    }

    /**
     * Extract procedure name from CALLP expression.
     * Example: MyProc(arg1 : arg2) → MyProc
     */
    private String extractProcedureName(String expression) {
        if (expression == null || expression.isBlank()) return null;
        int parenIdx = expression.indexOf('(');
        String name = (parenIdx > 0) ? expression.substring(0, parenIdx).trim() : expression.trim();
        name = name.replaceAll(";$", "");
        return name.isEmpty() ? null : name;
    }

    // =========================================================================
    // Dependency: /COPY and /INCLUDE directives
    // =========================================================================

    private List<IrDocument.CopyMemberRef> extractCopyMembers(RpgleContent content) {
        List<IrDocument.CopyMemberRef> copies = new ArrayList<>();

        if (content.getCopyDirectives() != null) {
            for (RpgleContent.CopyDirective cd : content.getCopyDirectives()) {
                IrDocument.CopyMemberRef ref = new IrDocument.CopyMemberRef();
                ref.setDirective(cd.directive());
                ref.setLocation(cd.location());
                ref.setResolved(false);

                // Parse the path: LIBRARY/FILE,MEMBER or FILE/MEMBER or just MEMBER
                parseCopyPath(cd.path(), ref);
                copies.add(ref);
            }
        }

        return copies;
    }

    /**
     * Parse a /COPY or /INCLUDE path into library, file, and member components.
     * <p>Supported formats:
     * <ul>
     *   <li>{@code MEMBER}</li>
     *   <li>{@code FILE/MEMBER}</li>
     *   <li>{@code LIBRARY/FILE,MEMBER}</li>
     * </ul>
     */
    private void parseCopyPath(String path, IrDocument.CopyMemberRef ref) {
        if (path == null || path.isBlank()) return;
        String trimmed = path.trim();

        if (trimmed.contains(",")) {
            // LIBRARY/FILE,MEMBER format
            int commaIdx = trimmed.indexOf(',');
            String beforeComma = trimmed.substring(0, commaIdx);
            String member = trimmed.substring(commaIdx + 1).trim();
            ref.setMemberName(member);

            if (beforeComma.contains("/")) {
                String[] parts = beforeComma.split("/", 2);
                ref.setLibraryName(parts[0].trim());
                ref.setFileName(parts[1].trim());
            } else {
                ref.setFileName(beforeComma.trim());
            }
        } else if (trimmed.contains("/")) {
            // FILE/MEMBER format
            String[] parts = trimmed.split("/", 2);
            ref.setFileName(parts[0].trim());
            ref.setMemberName(parts[1].trim());
        } else {
            // Just MEMBER
            ref.setMemberName(trimmed);
        }
    }

    // =========================================================================
    // Metadata
    // =========================================================================

    void populateMetadata(IrDocument doc, NormalizedSource normalized, String sourceType) {
        Metadata metadata = new Metadata();
        metadata.setIrVersion(IR_VERSION);
        metadata.setSourceType(sourceType);

        Metadata.ParseInfo parseInfo = new Metadata.ParseInfo();
        parseInfo.setParsedAt(Instant.now().toString());
        parseInfo.setTotalLines(normalized.getLineCount());
        parseInfo.setParserVersion(IR_VERSION);

        RpgleContent content = (RpgleContent) doc.getContent();
        if (content.getParseErrors() != null && !content.getParseErrors().isEmpty()) {
            boolean hasError = content.getParseErrors().stream()
                    .anyMatch(pe -> ParseError.Severity.ERROR.equals(pe.getSeverity()));
            parseInfo.setParseStatus(hasError ? "partial" : "complete");
        } else {
            parseInfo.setParseStatus("complete");
        }

        parseInfo.setErrors(List.of());
        parseInfo.setWarnings(List.of());

        // Propagate parse errors/warnings into ParseInfo using the standard map format
        if (content.getParseErrors() != null && !content.getParseErrors().isEmpty()) {
            List<java.util.Map<String, Object>> errorMaps = new ArrayList<>();
            List<java.util.Map<String, Object>> warningMaps = new ArrayList<>();
            for (ParseError pe : content.getParseErrors()) {
                java.util.Map<String, Object> entry = java.util.Map.of(
                        "line",     pe.getLine(),
                        "column",   pe.getColumn(),
                        "message",  pe.getMessage() != null ? pe.getMessage() : "",
                        "severity", pe.getSeverity() != null ? pe.getSeverity().name() : "WARNING"
                );
                if (ParseError.Severity.ERROR.equals(pe.getSeverity())) {
                    errorMaps.add(entry);
                } else {
                    warningMaps.add(entry);
                }
            }
            if (!errorMaps.isEmpty())   parseInfo.setErrors(errorMaps);
            if (!warningMaps.isEmpty()) parseInfo.setWarnings(warningMaps);
        }
        metadata.setParseInfo(parseInfo);
        doc.setMetadata(metadata);
    }
}
