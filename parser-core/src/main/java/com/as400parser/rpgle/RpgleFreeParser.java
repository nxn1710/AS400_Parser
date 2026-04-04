package com.as400parser.rpgle;

import com.as400parser.common.model.Location;
import com.as400parser.common.model.ParseError;
import com.as400parser.rpgle.antlr.RpgLexer;
import com.as400parser.rpgle.antlr.RpgParser;
import com.as400parser.rpgle.model.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.List;

/**
 * ANTLR-based RPGLE parser for the entire source file.
 * <p>
 * Uses the generated {@link RpgLexer} and {@link RpgParser} from
 * {@code grammar/rpgle/RpgLexer.g4} and {@code RpgParser.g4}.
 * <p>
 * This grammar is a hybrid — it handles both fixed-format (column-positional)
 * and free-format code via lexer modes, and produces a unified parse tree.
 */
public class RpgleFreeParser {

    private final List<ParseError> errors = new ArrayList<>();

    /**
     * Parse free-format lines into statements.
     * Used for /FREE.../END-FREE blocks in mixed-format files.
     * Falls back to statement-level extraction since the ANTLR grammar
     * expects a full file context (column positions) to trigger mode switches.
     *
     * @param lines     the free-format source lines
     * @param startLine the 1-based line number of the first line in the source file
     * @return list of parsed free-format statements
     */
    public List<FreeFormatStatement> parse(List<String> lines, int startLine) {
        // For mixed-format /FREE blocks, use statement-level extraction
        // because the grammar's lexer requires full-file column-position context
        return statementLevelParse(lines, startLine);
    }

    /**
     * Parse a fully free-format file (starting with **FREE) using ANTLR.
     *
     * @param allLines all source lines (including the **FREE header)
     * @return list of parsed free-format statements
     */
    public List<FreeFormatStatement> parseFullyFree(List<String> allLines) {
        String source = String.join("\n", allLines) + "\n";
        return parseWithAntlr(source);
    }

    /**
     * Parse an entire RPGLE source file using ANTLR.
     * The grammar handles format detection/switching internally via lexer modes.
     *
     * @param source full source text
     * @return ANTLR parse tree root context
     */
    public RpgParser.RContext parseFullSource(String source) {
        try {
            CharStream input = CharStreams.fromString(source);
            RpgLexer lexer = new RpgLexer(input);
            lexer.removeErrorListeners();
            lexer.addErrorListener(new CollectingErrorListener());

            CommonTokenStream tokens = new CommonTokenStream(lexer);
            RpgParser parser = new RpgParser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener(new CollectingErrorListener());

            return parser.r();
        } catch (Exception e) {
            errors.add(new ParseError(0, 0,
                    "ANTLR parsing failed: " + e.getMessage(),
                    ParseError.Severity.ERROR));
            return null;
        }
    }

    public List<ParseError> getErrors() { return errors; }

    // =========================================================================
    // ANTLR-based parsing for fully free files
    // =========================================================================

    private List<FreeFormatStatement> parseWithAntlr(String source) {
        List<FreeFormatStatement> statements = new ArrayList<>();

        try {
            RpgParser.RContext tree = parseFullSource(source);
            if (tree != null) {
                extractStatements(tree, statements);
            }
        } catch (Exception e) {
            errors.add(new ParseError(0, 0,
                    "ANTLR parsing failed, using fallback: " + e.getMessage(),
                    ParseError.Severity.WARNING));
            String[] lines = source.split("\n");
            return statementLevelParse(java.util.Arrays.asList(lines), 1);
        }

        // If ANTLR returned nothing useful, fall back
        if (statements.isEmpty() && !source.isBlank()) {
            String[] lines = source.split("\n");
            return statementLevelParse(java.util.Arrays.asList(lines), 1);
        }

        return statements;
    }

    /**
     * Extract statements from the ANTLR parse tree.
     */
    private void extractStatements(RpgParser.RContext tree,
                                   List<FreeFormatStatement> statements) {
        if (tree == null || tree.children == null) return;

        for (ParseTree child : tree.children) {
            if (child instanceof RpgParser.StatementContext stmt) {
                addStatement(stmt, statements);
            } else if (child instanceof RpgParser.Dcl_prContext ctx) {
                addContextAsStatement(ctx, "dcl-pr", statements);
            } else if (child instanceof RpgParser.Dcl_piContext ctx) {
                addContextAsStatement(ctx, "dcl-pi", statements);
            } else if (child instanceof RpgParser.Ctl_optContext ctx) {
                addContextAsStatement(ctx, "ctl-opt", statements);
            } else if (child instanceof RpgParser.SubroutineContext ctx) {
                addContextAsStatement(ctx, "subroutine", statements);
            } else if (child instanceof RpgParser.ProcedureContext ctx) {
                addContextAsStatement(ctx, "procedure", statements);
            }
        }
    }

    private void addStatement(RpgParser.StatementContext stmt,
                              List<FreeFormatStatement> statements) {
        FreeFormatStatement fs = new FreeFormatStatement();

        Token startToken = stmt.getStart();
        Token stopToken = stmt.getStop();
        int startLine = startToken != null ? startToken.getLine() : 1;
        int endLine = stopToken != null ? stopToken.getLine() : startLine;

        fs.setLocation(new Location(startLine, endLine));

        String text = getOriginalText(stmt);
        fs.setRawSourceLine(text);
        fs.setText(text);
        fs.setStatementType(detectStatementType(stmt));
        statements.add(fs);
    }

    private void addContextAsStatement(ParserRuleContext ctx, String type,
                                       List<FreeFormatStatement> statements) {
        FreeFormatStatement fs = new FreeFormatStatement();
        Token startToken = ctx.getStart();
        Token stopToken = ctx.getStop();
        int startLine = startToken != null ? startToken.getLine() : 1;
        int endLine = stopToken != null ? stopToken.getLine() : startLine;

        fs.setLocation(new Location(startLine, endLine));
        String text = getOriginalText(ctx);
        fs.setRawSourceLine(text);
        fs.setText(text);
        fs.setStatementType(type);
        statements.add(fs);
    }

    private String getOriginalText(ParserRuleContext ctx) {
        Token start = ctx.getStart();
        Token stop = ctx.getStop();
        if (start != null && stop != null) {
            CharStream input = start.getInputStream();
            if (input != null) {
                return input.getText(new Interval(start.getStartIndex(), stop.getStopIndex()));
            }
        }
        return ctx.getText();
    }

    /**
     * Detect the statement type from the ANTLR parse context.
     */
    private String detectStatementType(RpgParser.StatementContext stmt) {
        if (stmt == null || stmt.children == null || stmt.children.isEmpty())
            return "unknown";

        ParseTree firstChild = stmt.getChild(0);
        if (firstChild == null) return "unknown";

        String className = firstChild.getClass().getSimpleName();
        if (className.endsWith("Context")) {
            return className.replace("Context", "").replace("_", "-").toLowerCase();
        }
        return "statement";
    }

    // =========================================================================
    // Fallback: statement-level extraction (for /FREE blocks or if ANTLR fails)
    // =========================================================================

    private List<FreeFormatStatement> statementLevelParse(List<String> lines, int startLine) {
        List<FreeFormatStatement> statements = new ArrayList<>();
        StringBuilder currentStmt = new StringBuilder();
        int stmtStartLine = startLine;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            int lineNum = startLine + i;

            if (line == null || line.trim().isEmpty()) continue;

            String trimmed = line.trim();
            if (trimmed.startsWith("//")) continue;
            if (trimmed.toUpperCase().startsWith("**FREE")) continue;

            // Strip inline comments (not inside strings)
            int commentIdx = trimmed.indexOf("//");
            if (commentIdx > 0 && !isInsideString(trimmed, commentIdx)) {
                trimmed = trimmed.substring(0, commentIdx).trim();
            }

            if (currentStmt.length() == 0) {
                stmtStartLine = lineNum;
            }
            currentStmt.append(trimmed);

            if (trimmed.endsWith(";")) {
                String stmtText = currentStmt.toString();
                stmtText = stmtText.substring(0, stmtText.length() - 1).trim();

                FreeFormatStatement stmt = new FreeFormatStatement();
                stmt.setLocation(new Location(stmtStartLine, lineNum));
                stmt.setRawSourceLine(stmtText);
                stmt.setStatementType(detectFallbackType(stmtText));
                stmt.setText(stmtText);
                statements.add(stmt);
                currentStmt.setLength(0);
            } else {
                currentStmt.append(" ");
            }
        }

        if (currentStmt.length() > 0) {
            String stmtText = currentStmt.toString().trim();
            if (!stmtText.isEmpty()) {
                FreeFormatStatement stmt = new FreeFormatStatement();
                stmt.setLocation(new Location(stmtStartLine, startLine + lines.size() - 1));
                stmt.setRawSourceLine(stmtText);
                stmt.setStatementType(detectFallbackType(stmtText));
                stmt.setText(stmtText);
                statements.add(stmt);
            }
        }

        return statements;
    }

    private String detectFallbackType(String text) {
        if (text == null || text.isEmpty()) return "unknown";
        String firstWord = text.toUpperCase().trim().split("[\\s(;]")[0];
        return switch (firstWord) {
            case "DCL-S" -> "dcl-s";
            case "DCL-DS" -> "dcl-ds";
            case "DCL-F" -> "dcl-f";
            case "DCL-PR" -> "dcl-pr";
            case "DCL-PI" -> "dcl-pi";
            case "DCL-C" -> "dcl-c";
            case "DCL-PROC" -> "dcl-proc";
            case "END-DS" -> "end-ds";
            case "END-PR" -> "end-pr";
            case "END-PI" -> "end-pi";
            case "END-PROC" -> "end-proc";
            case "CTL-OPT" -> "ctl-opt";
            case "IF" -> "if";
            case "ELSE", "ELSEIF" -> "else";
            case "ENDIF" -> "endif";
            case "DOW" -> "dow";
            case "DOU" -> "dou";
            case "ENDDO" -> "enddo";
            case "FOR" -> "for";
            case "ENDFOR" -> "endfor";
            case "SELECT" -> "select";
            case "WHEN" -> "when";
            case "OTHER" -> "other";
            case "ENDSL" -> "endsl";
            case "MONITOR" -> "monitor";
            case "ON-ERROR" -> "on-error";
            case "ENDMON" -> "endmon";
            case "RETURN" -> "return";
            case "BEGSR" -> "begsr";
            case "ENDSR" -> "endsr";
            case "EXSR" -> "exsr";
            case "READ" -> "read";
            case "CHAIN" -> "chain";
            case "WRITE" -> "write";
            case "UPDATE" -> "update";
            case "DELETE" -> "delete";
            case "EVAL" -> "eval";
            case "CALLP" -> "callp";
            case "CLEAR" -> "clear";
            case "RESET" -> "reset";
            default -> text.contains("=") && !text.startsWith("*") ? "eval" : "operation";
        };
    }

    private boolean isInsideString(String text, int position) {
        boolean inString = false;
        for (int i = 0; i < position && i < text.length(); i++) {
            if (text.charAt(i) == '\'') inString = !inString;
        }
        return inString;
    }

    // =========================================================================
    // ANTLR error collector
    // =========================================================================

    private class CollectingErrorListener extends BaseErrorListener {
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                int line, int charPositionInLine, String msg,
                                RecognitionException e) {
            errors.add(new ParseError(line, charPositionInLine, msg,
                    ParseError.Severity.WARNING));
        }
    }
}
