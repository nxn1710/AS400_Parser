package com.as400parser.cl;

import com.as400parser.cl.model.*;
import com.as400parser.common.model.Location;
import com.as400parser.common.model.ParseError;
import com.as400parser.common.model.SourceLine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Core parser for CL (Control Language) source files.
 * <p>
 * Implements a two-pass strategy:
 * <ol>
 *   <li>Tokenization: join continuation lines, strip block comments → produces LogicalLines</li>
 *   <li>Parsing: classify each LogicalLine into commands, labels, variables, file declarations,
 *       subroutines, and monitor messages</li>
 * </ol>
 */
public class ClIrBuilder {

    /**
     * A LogicalLine represents a single CL statement that may span multiple
     * physical lines due to line continuation markers (+ or -).
     */
    private static class LogicalLine {
        /** Final merged text of the entire statement. */
        String text;
        /** First physical line number where this statement begins. */
        int startLineNum;
        /** Last physical line number where this statement ends. */
        int endLineNum;
        /** All original physical source lines contributing to this statement. */
        List<String> rawLines = new ArrayList<>();
    }

    /**
     * Entry point: parse normalized source lines into a fully populated {@link ClContent}.
     *
     * @param normalizedLines source lines (may include SEU sequence numbers or be raw)
     * @param sequenceNumbers optional array of SEU sequence numbers (cols 1-5), may be null
     * @return populated ClContent
     */
    public ClContent buildContent(List<String> normalizedLines, String[] sequenceNumbers) {
        ClContent content = new ClContent();

        // Pass 1: build the SourceLine registry
        List<SourceLine> sourceLines = new ArrayList<>();
        Map<Integer, SourceLine> lineMap = new HashMap<>();
        for (int i = 0; i < normalizedLines.size(); i++) {
            SourceLine sl = new SourceLine();
            sl.setLineNumber(i + 1);
            sl.setSequenceNumber(sequenceNumbers != null && i < sequenceNumbers.length
                    ? sequenceNumbers[i] : null);
            sl.setRawText(normalizedLines.get(i));
            if (normalizedLines.get(i).trim().isEmpty()) {
                sl.setBlank(true);
            }
            sourceLines.add(sl);
            lineMap.put(i + 1, sl);
        }
        content.setSourceLines(sourceLines);

        // Pass 2: tokenize the stream — join continuations, extract comments
        List<LogicalLine> logicalLines = extractLogicalLines(normalizedLines, content, lineMap);

        // Pass 3: parse each logical line into commands and structural elements
        ClSubroutine currentSubroutine = null;
        for (LogicalLine ll : logicalLines) {
            currentSubroutine = parseCommandStatement(ll, content, currentSubroutine);
        }

        return content;
    }

    // ========================= Pass 2: Tokenization =========================

    /**
     * Tokenizes lines into LogicalLines by joining continuation lines and extracting
     * block comments. Uses a character-level scanner to correctly handle quoted strings
     * (which may contain {@code /*}, {@code +}, etc. that must not be treated as syntax).
     *
     * @param lines   raw source lines
     * @param content mutable context; extracted comments are added to {@code content.comments}
     * @param lineMap map from 1-based line number to SourceLine, for specType tagging
     * @return ordered list of fully assembled LogicalLines
     */
    private List<LogicalLine> extractLogicalLines(List<String> lines, ClContent content,
                                                   Map<Integer, SourceLine> lineMap) {
        List<LogicalLine> result = new ArrayList<>();

        boolean inComment = false;
        StringBuilder currentComment = new StringBuilder();
        int commentStartLine = -1;

        StringBuilder currentLineText = new StringBuilder();
        List<String> currentRawLines = new ArrayList<>();
        int currentStartLine = -1;
        boolean inQuote = false;
        char pendingContinuationType = '\0';

        for (int i = 0; i < lines.size(); i++) {
            String rawLine = lines.get(i);
            int lineNum = i + 1;

            // Start tracking a new logical line on first non-comment physical line
            if (!inComment && currentStartLine == -1) {
                currentStartLine = lineNum;
            }

            currentRawLines.add(rawLine);

            int len = rawLine.length();
            int j = 0;
            
            // Handle + continuation (skip leading spaces on this line)
            if (pendingContinuationType == '+' && !inComment) {
                while (j < len && rawLine.charAt(j) == ' ') {
                    j++;
                }
                pendingContinuationType = '\0';
            } else if (pendingContinuationType == '-') {
                pendingContinuationType = '\0'; // '-' means do NOT skip
            }

            boolean continuationFound = false;

            while (j < len) {
                char c = rawLine.charAt(j);

                // --- Inside a block comment ---
                if (inComment) {
                    if (c == '*' && j + 1 < len && rawLine.charAt(j + 1) == '/') {
                        // End of block comment: '*/'
                        inComment = false;
                        currentComment.append("*/");
                        ClComment commentObj = new ClComment(currentComment.toString(), commentStartLine);
                        content.getComments().add(commentObj);
                        tagLine(lineMap, commentStartLine, lineNum, "COMMENT");
                        currentComment.setLength(0);
                        j += 2;
                        // After comment, currentStartLine may need refresh for next statement
                        if (currentStartLine == commentStartLine && currentLineText.length() == 0) {
                            currentStartLine = -1;
                        }
                        continue;
                    } else {
                        currentComment.append(c);
                        j++;
                        continue;
                    }
                }

                // Check for line continuation '+' or '-' as the last non-blank char on the line
                if (c == '+' || c == '-') {
                    boolean onlyBlanksLeft = true;
                    for (int k = j + 1; k < len; k++) {
                        if (rawLine.charAt(k) != ' ') {
                            onlyBlanksLeft = false;
                            break;
                        }
                    }
                    if (onlyBlanksLeft) {
                        // Mark all physical lines in this block as CONTINUATION
                        tagLine(lineMap, lineNum, lineNum, "CONTINUATION");
                        continuationFound = true;
                        pendingContinuationType = c;
                        break;
                    }
                }

                // --- Inside a quoted string literal — ignore syntax chars ---
                if (inQuote) {
                    if (c == '\'') inQuote = false;
                    currentLineText.append(c);
                    j++;
                    continue;
                }

                // Check for start of quoted string
                if (c == '\'') {
                    inQuote = true;
                    currentLineText.append(c);
                    j++;
                    continue;
                }

                // Check for start of block comment: '/*'
                if (c == '/' && j + 1 < len && rawLine.charAt(j + 1) == '*') {
                    inComment = true;
                    commentStartLine = lineNum;
                    currentComment.append("/*");
                    j += 2;
                    continue;
                }

                currentLineText.append(c);
                j++;
            }

            // If we're still inside a comment at end of line, capture the newline and continue
            if (inComment) {
                currentComment.append('\n');
                continue;
            }

            // If a continuation was found, loop to next line to build more text
            if (continuationFound) {
                continue;
            }

            // End of logical line: finalize it
            String text = currentLineText.toString().trim();
            if (!text.isEmpty()) {
                LogicalLine ll = new LogicalLine();
                ll.text = text;
                ll.startLineNum = currentStartLine;
                ll.endLineNum = lineNum;
                ll.rawLines = new ArrayList<>(currentRawLines);
                result.add(ll);
            } else {
                // Blank physical line — tag it
                tagLine(lineMap, lineNum, lineNum, null); // stays blank
            }

            // Reset buffers for next logical line
            currentLineText.setLength(0);
            currentRawLines.clear();
            currentStartLine = -1;
        }

        // Failsafe: unclosed block comment at EOF
        if (inComment && currentComment.length() > 0) {
            content.getComments().add(new ClComment(currentComment.toString(), commentStartLine));
            content.getParseErrors().add(
                    ParseError.warning(commentStartLine, 1, "Unterminated comment block at end of file"));
        }

        // Failsafe: unterminated continuation at EOF (emit partial logical line)
        if (currentLineText.toString().trim().length() > 0) {
            LogicalLine ll = new LogicalLine();
            ll.text = currentLineText.toString().trim();
            ll.startLineNum = currentStartLine != -1 ? currentStartLine : lines.size();
            ll.endLineNum = lines.size();
            ll.rawLines = new ArrayList<>(currentRawLines);
            result.add(ll);
        }

        return result;
    }

    // ========================= Pass 3: Command Parsing =========================

    /**
     * Parses a single LogicalLine into commands, labels, variables, file declarations,
     * subroutines, and MONMSG entries.
     *
     * @param ll                the logical line to parse
     * @param content           the mutable ClContent being built
     * @param currentSubroutine the currently open SUBR block, or null if at top level
     * @return the (possibly changed) active subroutine context
     */
    private ClSubroutine parseCommandStatement(LogicalLine ll, ClContent content,
                                                ClSubroutine currentSubroutine) {
        String text = ll.text;

        // 1. Check for a label prefix: "LABELNAME: COMMAND ..."
        int colonIdx = text.indexOf(':');
        if (colonIdx > 0) {
            String candidate = text.substring(0, colonIdx);
            // A label must be a single plain word (no spaces or parens)
            if (!candidate.contains(" ") && !candidate.contains("(")) {
                ClLabel label = new ClLabel(candidate, ll.startLineNum);
                content.getLabels().add(label);
                tagLine(content, ll.startLineNum, "LABEL");
                text = text.substring(colonIdx + 1).trim(); // strip label prefix
            }
        }

        if (text.isEmpty()) return currentSubroutine;

        // 2. Extract command name (first token before ' ' or '(')
        String cmdName = extractCommandName(text);
        String paramText = text.length() > cmdName.length()
                ? text.substring(cmdName.length()).trim() : "";
        cmdName = cmdName.toUpperCase();

        // 3. Parse parameters
        List<ClParameter> params = parseParameters(paramText);

        // 4. Handle CLLE subroutine boundaries
        if ("SUBR".equals(cmdName)) {
            ClSubroutine sub = new ClSubroutine();
            String subName = getParamValue(params, "SUBR", 0);
            if ((subName == null || subName.isBlank()) && !content.getLabels().isEmpty()) {
                subName = content.getLabels().get(content.getLabels().size() - 1).getName();
            }
            sub.setName(subName);
            sub.setLocation(new Location(ll.startLineNum, ll.endLineNum));
            content.getSubroutines().add(sub);
            tagLines(content, ll, "COMMAND");
            return sub; // enter subroutine scope
        }
        if ("ENDSUBR".equals(cmdName)) {
            tagLines(content, ll, "COMMAND");
            return null; // exit subroutine scope
        }

        // 5. Build the ClCommand
        ClCommand cmd = new ClCommand();
        cmd.setName(cmdName);
        cmd.setParameters(params);
        cmd.setLocation(new Location(ll.startLineNum, ll.endLineNum));
        cmd.setRawSourceLines(ll.rawLines);

        // 6. Dispatch special commands for structured population
        switch (cmdName) {
            case "PGM" -> {
                // PGM PARM(&VAR1 &VAR2): extract program name from filename later;
                // record this as the PGM command and note any documented program-level parms
                String pgmName = getParamValue(params, "PGM", 0);
                if (pgmName != null && content.getProgramName() == null) {
                    content.setProgramName(pgmName);
                }
                tagLines(content, ll, "COMMAND");
                return currentSubroutine;
            }
            case "ENDPGM" -> {
                tagLines(content, ll, "COMMAND");
                return currentSubroutine;
            }
            case "DCL" -> {
                // DCL VAR(&NAME) TYPE(*CHAR) LEN(10)
                ClVariable var = buildVariable(params, ll.startLineNum, ll.endLineNum);
                content.getVariables().add(var);
                tagLines(content, ll, "COMMAND");
                return currentSubroutine;
            }
            case "DCLF" -> {
                // DCLF FILE(LIBNAME/FILENAME) OPNID(MYFILE)
                ClFileDeclaration fd = buildFileDeclaration(params, ll.startLineNum, ll.endLineNum);
                content.getFileDeclarations().add(fd);
                tagLines(content, ll, "COMMAND");
                return currentSubroutine;
            }
            case "MONMSG" -> {
                // MONMSG MSGID(CPF9898) EXEC(GOTO LABEL)
                ClMonitorMessage monitor = new ClMonitorMessage();
                monitor.setMsgId(getParamValue(params, "MSGID", 0));
                monitor.setCmpData(getParamValue(params, "CMPDTA", -1));

                String execText = getParamValue(params, "EXEC", -1);
                if (execText != null && !execText.trim().isEmpty()) {
                    ClCommand execCmd = parseNestedCommand(execText.trim(), ll.startLineNum,
                            ll.endLineNum, ll.rawLines);
                    if (execCmd != null) {
                        cmd.getExecCommands().add(execCmd);
                        monitor.getExecCommands().add(execCmd);
                    }
                }
                cmd.setMonitorMessage(monitor);
            }
        }

        // 7. Tag source lines with specType
        tagLines(content, ll, "COMMAND");

        // 8. Add to the active scope (subroutine or top-level)
        if (currentSubroutine != null) {
            currentSubroutine.getCommands().add(cmd);
        } else {
            content.getCommands().add(cmd);
        }

        return currentSubroutine;
    }

    // ========================= Parameter Parsing =========================

    /**
     * Parses a CL parameter string into a list of {@link ClParameter} objects.
     * <p>
     * Handles two forms:
     * <ul>
     *   <li>{@code KEYWORD(value)} — keyword parameter</li>
     *   <li>{@code (value)} — positional parameter</li>
     * </ul>
     * Quoted strings and nested parentheses inside values are handled correctly.
     *
     * @param paramText the raw text after the command name
     * @return list of parsed parameters (may be empty)
     */
    List<ClParameter> parseParameters(String paramText) {
        List<ClParameter> result = new ArrayList<>();
        if (paramText == null || paramText.isBlank()) return result;

        int i = 0;
        int len = paramText.length();

        while (i < len) {
            // Skip whitespace between parameters
            while (i < len && paramText.charAt(i) == ' ') i++;
            if (i >= len) break;

            // Determine if this is KEYWORD(...) or (...) positional
            if (paramText.charAt(i) == '(') {
                // Positional parameter: (value)
                String value = extractParenValue(paramText, i);
                if (value == null) break; // malformed
                result.add(new ClParameter(null, value, true));
                i += value.length() + 2; // +2 for '(' and ')'

            } else {
                // Read ahead to find where keyword ends (before '(' or eof)
                int keyStart = i;
                while (i < len && paramText.charAt(i) != '(' && paramText.charAt(i) != ' ') i++;

                String keyword = paramText.substring(keyStart, i).trim().toUpperCase();
                if (keyword.isEmpty()) continue;

                // Skip whitespace between keyword and '('
                while (i < len && paramText.charAt(i) == ' ') i++;

                if (i < len && paramText.charAt(i) == '(') {
                    // KEYWORD(value) — normal keyword param
                    String value = extractParenValue(paramText, i);
                    if (value == null) break; // malformed
                    result.add(new ClParameter(keyword, value, false));
                    i += value.length() + 2;
                } else {
                    // Bare keyword with no value — may be a flag keyword like *NOMAX
                    result.add(new ClParameter(keyword, null, false));
                }
            }
        }
        return result;
    }

    /**
     * Extracts the raw string value between balanced parentheses starting at {@code parenStart}.
     * Correctly handles nested parentheses and quoted strings inside the value.
     *
     * @param text       the full text to scan
     * @param parenStart index of the opening '('
     * @return the content between the balanced parens, or null if unbalanced
     */
    private String extractParenValue(String text, int parenStart) {
        if (parenStart >= text.length() || text.charAt(parenStart) != '(') return null;

        int depth = 0;
        boolean inQuote = false;
        int start = parenStart + 1;

        for (int i = parenStart; i < text.length(); i++) {
            char c = text.charAt(i);
            if (inQuote) {
                if (c == '\'') inQuote = false;
                continue;
            }
            if (c == '\'') { inQuote = true; continue; }
            if (c == '(') depth++;
            if (c == ')') {
                depth--;
                if (depth == 0) return text.substring(start, i);
            }
        }
        return null; // unbalanced parentheses
    }

    // ========================= Command Name Extraction =========================

    /**
     * Extracts the base command name from the start of a text string.
     * The name ends at the first space or '(' character.
     */
    private String extractCommandName(String text) {
        int end = 0;
        while (end < text.length() && text.charAt(end) != ' ' && text.charAt(end) != '(') {
            end++;
        }
        return text.substring(0, end);
    }

    // ========================= Command-Specific Builders =========================

    /**
     * Builds a {@link ClVariable} from parsed DCL parameters.
     * DCL VAR(&amp;NAME) TYPE(*CHAR) LEN(10) VALUE('default')
     */
    private ClVariable buildVariable(List<ClParameter> params, int startLine, int endLine) {
        ClVariable var = new ClVariable();
        var.setName(getParamValue(params, "VAR", 0));
        var.setType(getParamValue(params, "TYPE", 1));

        String lenStr = getParamValue(params, "LEN", 2);
        if (lenStr != null) {
            try { var.setLength(Integer.parseInt(lenStr.trim())); }
            catch (NumberFormatException ignored) {}
        }

        String decStr = getParamValue(params, "DEC", -1);
        if (decStr != null) {
            try { var.setDecimalPositions(Integer.parseInt(decStr.trim())); }
            catch (NumberFormatException ignored) {}
        }

        var.setInitialValue(getParamValue(params, "VALUE", -1));
        var.setLocation(new Location(startLine, endLine));
        return var;
    }

    /**
     * Builds a {@link ClFileDeclaration} from parsed DCLF parameters.
     * DCLF FILE(LIB/FILE) OPNID(ID)
     */
    private ClFileDeclaration buildFileDeclaration(List<ClParameter> params,
                                                    int startLine, int endLine) {
        ClFileDeclaration fd = new ClFileDeclaration();
        fd.setFileName(getParamValue(params, "FILE", 0));
        fd.setOpenId(getParamValue(params, "OPNID", -1));
        fd.setLocation(new Location(startLine, endLine));
        return fd;
    }

    /**
     * Parses a nested command string (e.g., the value of an EXEC parameter).
     * Returns a {@link ClCommand} for the nested command, or null if the text is empty.
     */
    private ClCommand parseNestedCommand(String text, int startLine, int endLine,
                                          List<String> rawLines) {
        if (text.isBlank()) return null;
        String name = extractCommandName(text).toUpperCase();
        String rest = text.length() > name.length() ? text.substring(name.length()).trim() : "";
        ClCommand cmd = new ClCommand();
        cmd.setName(name);
        cmd.setParameters(parseParameters(rest));
        cmd.setLocation(new Location(startLine, endLine));
        cmd.setRawSourceLines(rawLines);
        return cmd;
    }

    // ========================= Parameter Lookup Helpers =========================

    /**
     * Retrieves a parameter value by keyword name first, falling back to positional index.
     *
     * @param params          the list of parsed parameters
     * @param keyword         the keyword to search for (case-insensitive)
     * @param positionalIndex 0-based positional fallback index; use -1 to skip positional lookup
     * @return the raw value string, or null if not found
     */
    String getParamValue(List<ClParameter> params, String keyword, int positionalIndex) {
        // Try keyword match first
        for (ClParameter p : params) {
            if (keyword.equalsIgnoreCase(p.getKeyword())) return p.getValue();
        }
        // Fall back to positional order
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

    // ========================= SourceLine SpecType Tagging =========================

    /**
     * Tags a range of source lines in the content's sourceLines list with a specType.
     */
    private void tagLine(Map<Integer, SourceLine> lineMap, int from, int to, String specType) {
        for (int ln = from; ln <= to && specType != null; ln++) {
            SourceLine sl = lineMap.get(ln);
            if (sl != null && sl.getSpecType() == null) {
                sl.setSpecType(specType);
                if ("COMMENT".equals(specType)) sl.setComment(true);
            }
        }
    }

    /**
     * Tags source lines for a logical line's range in the content's sourceLines list.
     */
    private void tagLines(ClContent content, LogicalLine ll, String specType) {
        List<SourceLine> sourceLines = content.getSourceLines();
        for (int ln = ll.startLineNum; ln <= ll.endLineNum; ln++) {
            int idx = ln - 1;
            if (idx >= 0 && idx < sourceLines.size()) {
                SourceLine sl = sourceLines.get(idx);
                if (sl.getSpecType() == null) {
                    sl.setSpecType(ln == ll.startLineNum ? specType : "CONTINUATION");
                }
            }
        }
    }

    /**
     * Tags a single line by its line number in the content's sourceLines list.
     */
    private void tagLine(ClContent content, int lineNum, String specType) {
        List<SourceLine> sourceLines = content.getSourceLines();
        int idx = lineNum - 1;
        if (idx >= 0 && idx < sourceLines.size()) {
            SourceLine sl = sourceLines.get(idx);
            if (sl.getSpecType() == null) sl.setSpecType(specType);
        }
    }
}
