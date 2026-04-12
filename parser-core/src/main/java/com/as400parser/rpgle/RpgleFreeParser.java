package com.as400parser.rpgle;

import com.as400parser.common.model.Location;
import com.as400parser.rpgle.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lightweight free-format RPGLE parser using text-based statement extraction.
 * <p>
 * Parses free-format RPGLE source code by splitting into semicolon-delimited
 * statements and classifying each by its leading keyword.
 * <p>
 * A full ANTLR-based parser will replace this in a future iteration once
 * the grammar compatibility issues with ANTLR 4.13.1 are resolved.
 */
public class RpgleFreeParser {

    private static final Pattern CTL_OPT       = Pattern.compile("^CTL-OPT\\b(.*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern DCL_F         = Pattern.compile("^DCL-F\\b(.*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern DCL_S         = Pattern.compile("^DCL-S\\b(.*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern DCL_C         = Pattern.compile("^DCL-C\\b(.*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern DCL_DS        = Pattern.compile("^DCL-DS\\b(.*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern END_DS        = Pattern.compile("^END-DS\\b(.*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern DCL_PR        = Pattern.compile("^DCL-PR\\b(.*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern END_PR        = Pattern.compile("^END-PR\\b(.*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern DCL_PI        = Pattern.compile("^DCL-PI\\b(.*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern END_PI        = Pattern.compile("^END-PI\\b(.*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern DCL_PROC      = Pattern.compile("^DCL-PROC\\b(.*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern END_PROC      = Pattern.compile("^END-PROC\\b(.*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern BEGSR         = Pattern.compile("^BEGSR\\b(.*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern ENDSR         = Pattern.compile("^ENDSR\\b(.*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern COPY_PATTERN  = Pattern.compile("^/(COPY|INCLUDE)\\b(.*)$", Pattern.CASE_INSENSITIVE);

    // Operations that start blocks
    private static final Pattern IF_STMT       = Pattern.compile("^IF\\b(.*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern ELSEIF_STMT   = Pattern.compile("^ELSEIF\\b(.*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern ELSE_STMT     = Pattern.compile("^ELSE\\b(.*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern ENDIF_STMT    = Pattern.compile("^ENDIF\\b(.*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern DOW_STMT      = Pattern.compile("^DOW\\b(.*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern DOU_STMT      = Pattern.compile("^DOU\\b(.*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern ENDDO_STMT    = Pattern.compile("^ENDDO\\b(.*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern FOR_STMT      = Pattern.compile("^FOR\\b(.*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern ENDFOR_STMT   = Pattern.compile("^ENDFOR\\b(.*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern SELECT_STMT   = Pattern.compile("^SELECT\\b(.*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern WHEN_STMT     = Pattern.compile("^WHEN\\b(.*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern OTHER_STMT    = Pattern.compile("^OTHER\\b(.*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern ENDSL_STMT    = Pattern.compile("^ENDSL\\b(.*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern MONITOR_STMT  = Pattern.compile("^MONITOR\\b(.*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern ON_ERROR_STMT = Pattern.compile("^ON-ERROR\\b(.*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern ENDMON_STMT   = Pattern.compile("^ENDMON\\b(.*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern EVAL_STMT     = Pattern.compile("^(EVAL|EVALR|EVAL-CORR)\\b(.*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern RETURN_STMT   = Pattern.compile("^RETURN\\b(.*)$", Pattern.CASE_INSENSITIVE);

    private final RpgleContent content;
    private final List<String> errors = new ArrayList<>();
    private final List<String> copyDirectives = new ArrayList<>();

    public RpgleFreeParser(RpgleContent content) {
        this.content = content;
    }

    /**
     * Parse free-format source text.
     *
     * @param sourceText  the free-format source text
     * @param startLine   the starting line number in the original source
     */
    public void parse(String sourceText, int startLine) {
        if (sourceText == null || sourceText.isBlank()) return;

        String[] lines = sourceText.split("\n");
        int currentLine = startLine;

        // Accumulate multi-line statements (semicolon-delimited)
        StringBuilder stmtBuffer = new StringBuilder();
        int stmtStartLine = currentLine;

        for (String line : lines) {
            String trimmed = line.trim();
            currentLine++;

            // Skip blank lines
            if (trimmed.isEmpty()) continue;

            // Skip **FREE directive
            if (trimmed.toUpperCase().startsWith("**FREE")) continue;

            // Handle line comments
            if (trimmed.startsWith("//")) {
                RpgleContent.Comment comment = new RpgleContent.Comment();
                comment.setLineNumber(currentLine - 1);
                comment.setLocation(Location.ofLine(currentLine - 1));
                comment.setText(trimmed.substring(2).trim());
                comment.setRawSourceLine(line);
                content.getComments().add(comment);
                continue;
            }

            // Accumulate into statement buffer
            if (stmtBuffer.length() == 0) {
                stmtStartLine = currentLine - 1;
            }

            // Handle inline comments
            int commentStart = findInlineCommentStart(trimmed);
            if (commentStart >= 0) {
                String code = trimmed.substring(0, commentStart).trim();
                String cmnt = trimmed.substring(commentStart + 2).trim();
                stmtBuffer.append(code).append(" ");

                RpgleContent.Comment comment = new RpgleContent.Comment();
                comment.setLineNumber(currentLine - 1);
                comment.setLocation(Location.ofLine(currentLine - 1));
                comment.setText(cmnt);
                content.getComments().add(comment);
            } else {
                stmtBuffer.append(trimmed).append(" ");
            }

            // Check for statement terminator
            if (trimmed.endsWith(";")) {
                String stmt = stmtBuffer.toString().trim();
                // Remove trailing semicolon
                if (stmt.endsWith(";")) {
                    stmt = stmt.substring(0, stmt.length() - 1).trim();
                }
                processStatement(stmt, stmtStartLine);
                stmtBuffer.setLength(0);
            }
        }

        // Process any remaining statement
        if (stmtBuffer.length() > 0) {
            String stmt = stmtBuffer.toString().trim();
            if (stmt.endsWith(";")) {
                stmt = stmt.substring(0, stmt.length() - 1).trim();
            }
            if (!stmt.isEmpty()) {
                processStatement(stmt, stmtStartLine);
            }
        }
    }

    public List<String> getErrors() {
        return errors;
    }

    public List<String> getCopyDirectives() {
        return copyDirectives;
    }

    // =========================================================================
    // Statement processing
    // =========================================================================

    private void processStatement(String stmt, int lineNumber) {
        Matcher m;

        // /COPY or /INCLUDE
        m = COPY_PATTERN.matcher(stmt);
        if (m.matches()) {
            String target = m.group(2).trim();
            copyDirectives.add("/" + m.group(1).toUpperCase() + " " + target);
            return;
        }

        // CTL-OPT
        m = CTL_OPT.matcher(stmt);
        if (m.matches()) {
            ControlSpec spec = new ControlSpec();
            spec.setFormat("free");
            spec.setLocation(Location.ofLine(lineNumber));
            spec.setRawSourceLine(stmt);
            spec.setKeywords(m.group(1).trim());
            content.getControlSpecs().add(spec);
            return;
        }

        // DCL-F
        m = DCL_F.matcher(stmt);
        if (m.matches()) {
            FileSpec spec = new FileSpec();
            spec.setFormat("free");
            spec.setLocation(Location.ofLine(lineNumber));
            spec.setRawSourceLine(stmt);
            String[] parts = m.group(1).trim().split("\\s+", 2);
            spec.setFileName(parts[0]);
            if (parts.length > 1) {
                spec.setKeywords(parts[1]);
            }
            content.getFileSpecs().add(spec);
            return;
        }

        // DCL-S (standalone variable)
        m = DCL_S.matcher(stmt);
        if (m.matches()) {
            DefinitionSpec spec = createFreeDefinition(m.group(1).trim(), "S", lineNumber, stmt);
            content.getDefinitionSpecs().add(spec);
            return;
        }

        // DCL-C (named constant)
        m = DCL_C.matcher(stmt);
        if (m.matches()) {
            DefinitionSpec spec = createFreeDefinition(m.group(1).trim(), "C", lineNumber, stmt);
            content.getDefinitionSpecs().add(spec);
            return;
        }

        // DCL-DS
        m = DCL_DS.matcher(stmt);
        if (m.matches()) {
            DefinitionSpec spec = createFreeDefinition(m.group(1).trim(), "DS", lineNumber, stmt);
            content.getDefinitionSpecs().add(spec);

            RpgleContent.DataStructure ds = new RpgleContent.DataStructure();
            ds.setName(spec.getName());
            ds.setType("dataStructure");
            ds.setLocation(spec.getLocation());
            ds.setRawSourceLine(stmt);
            content.getDataStructures().add(ds);
            return;
        }

        // DCL-PR
        m = DCL_PR.matcher(stmt);
        if (m.matches()) {
            DefinitionSpec spec = createFreeDefinition(m.group(1).trim(), "PR", lineNumber, stmt);
            content.getDefinitionSpecs().add(spec);
            return;
        }

        // DCL-PI
        m = DCL_PI.matcher(stmt);
        if (m.matches()) {
            DefinitionSpec spec = createFreeDefinition(m.group(1).trim(), "PI", lineNumber, stmt);
            content.getDefinitionSpecs().add(spec);
            return;
        }

        // DCL-PROC
        m = DCL_PROC.matcher(stmt);
        if (m.matches()) {
            ProcedureSpec spec = new ProcedureSpec();
            spec.setFormat("free");
            spec.setLocation(Location.ofLine(lineNumber));
            spec.setRawSourceLine(stmt);
            spec.setBeginEnd("B");
            String[] parts = m.group(1).trim().split("\\s+", 2);
            spec.setProcedureName(parts[0]);
            if (parts.length > 1) {
                spec.setKeywords(parts[1]);
            }
            content.getProcedureSpecs().add(spec);
            return;
        }

        // END-PROC
        m = END_PROC.matcher(stmt);
        if (m.matches()) {
            ProcedureSpec spec = new ProcedureSpec();
            spec.setFormat("free");
            spec.setLocation(Location.ofLine(lineNumber));
            spec.setRawSourceLine(stmt);
            spec.setBeginEnd("E");
            content.getProcedureSpecs().add(spec);
            return;
        }

        // BEGSR / ENDSR
        m = BEGSR.matcher(stmt);
        if (m.matches()) {
            CalcSpec.SubroutineBlock block = new CalcSpec.SubroutineBlock();
            block.setFormat("free");
            block.setLocation(Location.ofLine(lineNumber));
            block.setRawSourceLine(stmt);
            block.setSubroutineName(m.group(1).trim());
            content.getSubroutines().add(block);
            content.getCalculationSpecs().add(block);
            return;
        }

        // Ignore structural end keywords (they are handled by block context)
        if (ENDIF_STMT.matcher(stmt).matches() || ENDDO_STMT.matcher(stmt).matches()
                || ENDFOR_STMT.matcher(stmt).matches() || ENDSL_STMT.matcher(stmt).matches()
                || ENDMON_STMT.matcher(stmt).matches() || ENDSR.matcher(stmt).matches()
                || END_DS.matcher(stmt).matches() || END_PR.matcher(stmt).matches()
                || END_PI.matcher(stmt).matches()) {
            // These are captured as flat statements
            FreeFormatStatement ffs = new FreeFormatStatement("endBlock");
            ffs.setLocation(Location.ofLine(lineNumber));
            ffs.setRawSourceLine(stmt);
            String firstWord = stmt.split("\\s+")[0];
            ffs.setOperation(firstWord.toUpperCase());
            content.getFreeFormatStatements().add(ffs);
            return;
        }

        // All other statements → FreeFormatStatement
        FreeFormatStatement ffs = new FreeFormatStatement();
        ffs.setLocation(Location.ofLine(lineNumber));
        ffs.setRawSourceLine(stmt);

        // Classify by leading keyword
        String[] words = stmt.split("\\s+", 2);
        String keyword = words[0].toUpperCase();
        ffs.setOperation(keyword);

        if (words.length > 1) {
            ffs.setExpression(words[1]);
        }

        // Map to node type
        ffs.setNodeType(mapToNodeType(keyword));

        // Check for operation extender: EVAL(H), READ(N), etc.
        int parenIdx = keyword.indexOf('(');
        if (parenIdx > 0) {
            int closeIdx = keyword.indexOf(')');
            ffs.setOperation(keyword.substring(0, parenIdx));
            if (closeIdx > parenIdx) {
                ffs.setExtender(keyword.substring(parenIdx + 1, closeIdx));
            }
            ffs.setNodeType(mapToNodeType(keyword.substring(0, parenIdx)));
        }

        content.getFreeFormatStatements().add(ffs);
        content.getCalculationSpecs().add(ffs);
    }

    private DefinitionSpec createFreeDefinition(String after, String defType, int lineNumber, String rawLine) {
        DefinitionSpec spec = new DefinitionSpec();
        spec.setFormat("free");
        spec.setLocation(Location.ofLine(lineNumber));
        spec.setRawSourceLine(rawLine);
        spec.setDefinitionType(defType);

        String[] parts = after.split("\\s+", 2);
        spec.setName(parts[0]);
        if (parts.length > 1) {
            spec.setKeywords(parts[1]);
        }

        return spec;
    }

    private String mapToNodeType(String keyword) {
        return switch (keyword) {
            case "IF" -> "if";
            case "ELSEIF" -> "elseif";
            case "ELSE" -> "else";
            case "DOW" -> "doWhile";
            case "DOU" -> "doUntil";
            case "FOR" -> "for";
            case "SELECT" -> "select";
            case "WHEN" -> "when";
            case "OTHER" -> "other";
            case "MONITOR" -> "monitor";
            case "ON-ERROR" -> "onError";
            case "EVAL", "EVALR", "EVAL-CORR" -> "eval";
            case "CALLP" -> "callp";
            case "RETURN" -> "return";
            case "EXSR" -> "exsr";
            case "READ", "READE", "READP", "READPE", "READC" -> "read";
            case "WRITE" -> "write";
            case "UPDATE" -> "update";
            case "DELETE" -> "delete";
            case "CHAIN" -> "chain";
            case "SETLL", "SETGT" -> "setPosition";
            case "OPEN", "CLOSE" -> "fileControl";
            case "CLEAR", "RESET" -> "clear";
            case "LEAVE", "ITER" -> "loopControl";
            case "LEAVESR" -> "leaveSubroutine";
            case "DSPLY" -> "display";
            case "DUMP" -> "dump";
            case "SORTA" -> "sort";
            default -> "operation";
        };
    }

    /**
     * Find the start of an inline comment (// not inside a string literal).
     */
    private int findInlineCommentStart(String line) {
        boolean inString = false;
        for (int i = 0; i < line.length() - 1; i++) {
            char c = line.charAt(i);
            if (c == '\'') {
                inString = !inString;
            } else if (!inString && c == '/' && line.charAt(i + 1) == '/') {
                return i;
            }
        }
        return -1;
    }
}
