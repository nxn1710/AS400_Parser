package com.as400parser.dds;

import com.as400parser.dds.model.DdsKeyword;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses the keyword area (columns 45-80) of DDS A-spec lines into {@link DdsKeyword} objects.
 * <p>
 * Handles 16 parsing patterns for 50 keywords including:
 * <ul>
 *   <li>No-arg keywords (UNIQUE, FIFO, etc.)</li>
 *   <li>Single-value keywords (PFILE, TEXT, DATFMT, etc.)</li>
 *   <li>Multi-value keywords (VALUES, CONCAT, JFILE, etc.)</li>
 *   <li>COMP/CMP with operator syntax</li>
 *   <li>RANGE with two values</li>
 *   <li>Hex literals X'...' and special values *NULL, *DESCEND</li>
 *   <li>Qualified names LIB/FILE</li>
 *   <li>Japanese text in quotes</li>
 * </ul>
 * CMP is normalized to COMP during parsing.
 */
public class DdsKeywordParser {

    /**
     * Extract the keyword area from a normalized DDS line.
     * Columns 45-80 (0-indexed: 44-79), trimmed.
     */
    public String extractKeywordArea(String line) {
        if (line == null || line.length() < 45) {
            return "";
        }
        int end = Math.min(line.length(), 80);
        String area = line.substring(44, end);
        // Strip continuation indicator '+' if at col 80
        if (area.length() >= 36 && area.charAt(35) == '+') {
            area = area.substring(0, 35);
        }
        return area.trim();
    }

    /**
     * Check if a line has a continuation indicator ('+' at column 80).
     */
    public boolean hasContinuation(String line) {
        return line != null && line.length() >= 80 && line.charAt(79) == '+';
    }

    /**
     * Merge keyword areas from multiple continuation lines.
     * Lines joined by '+' at column 80.
     */
    public String mergeKeywordAreas(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            return "";
        }
        StringBuilder merged = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String area = extractKeywordArea(line);
            merged.append(area);
            // Add space between merged areas if not empty
            if (i < lines.size() - 1 && !area.isEmpty()) {
                merged.append(' ');
            }
        }
        return merged.toString().trim();
    }

    /**
     * Parse keyword area from multiple continuation lines into keyword objects.
     */
    public List<DdsKeyword> parseKeywordsWithContinuation(List<String> keywordLines) {
        String merged = mergeKeywordAreas(keywordLines);
        return parseKeywords(merged);
    }

    /**
     * Parse a single keyword area string into a list of keywords.
     * Input is the raw text from columns 45-80 (trimmed, continuations already merged).
     */
    public List<DdsKeyword> parseKeywords(String keywordArea) {
        List<DdsKeyword> keywords = new ArrayList<>();
        if (keywordArea == null || keywordArea.isBlank()) {
            return keywords;
        }

        int pos = 0;
        int len = keywordArea.length();

        while (pos < len) {
            // Skip whitespace
            while (pos < len && keywordArea.charAt(pos) == ' ') {
                pos++;
            }
            if (pos >= len) break;

            // Read keyword name (uppercase alpha characters)
            int nameStart = pos;
            while (pos < len && isKeywordNameChar(keywordArea.charAt(pos))) {
                pos++;
            }
            if (pos == nameStart) {
                // Skip unrecognized character
                pos++;
                continue;
            }

            String name = keywordArea.substring(nameStart, pos).toUpperCase();

            // Normalize CMP → COMP
            if ("CMP".equals(name)) {
                name = "COMP";
            }

            DdsKeyword kw = new DdsKeyword();
            kw.setName(name);

            // Check for parameters in parentheses
            if (pos < len && keywordArea.charAt(pos) == '(') {
                pos++; // skip '('

                // Find matching closing paren, respecting quotes
                String content = extractParenContent(keywordArea, pos);
                pos = pos + content.length() + 1; // +1 for closing ')'

                // Set raw text
                kw.setRawText(name + "(" + content + ")");

                // Parse the parameter content based on keyword name
                parseKeywordParameters(kw, name, content);
            } else {
                // No-arg keyword
                kw.setRawText(name);
            }

            keywords.add(kw);
        }

        return keywords;
    }

    /**
     * Extract content between parentheses, respecting nested quotes.
     * Starting position is just after the opening '('.
     * Returns the content string (not including the outer parens).
     */
    private String extractParenContent(String text, int startPos) {
        StringBuilder content = new StringBuilder();
        int pos = startPos;
        int depth = 1;

        while (pos < text.length() && depth > 0) {
            char c = text.charAt(pos);

            if (c == '\'') {
                // Quoted string — read until closing quote
                content.append(c);
                pos++;
                while (pos < text.length()) {
                    char qc = text.charAt(pos);
                    content.append(qc);
                    pos++;
                    if (qc == '\'') {
                        // Check for escaped quote ('')
                        if (pos < text.length() && text.charAt(pos) == '\'') {
                            content.append('\'');
                            pos++;
                        } else {
                            break;
                        }
                    }
                }
            } else if (c == '(') {
                depth++;
                content.append(c);
                pos++;
            } else if (c == ')') {
                depth--;
                if (depth > 0) {
                    content.append(c);
                }
                pos++;
            } else {
                content.append(c);
                pos++;
            }
        }

        return content.toString();
    }

    /**
     * Parse keyword parameters based on keyword name and content.
     */
    private void parseKeywordParameters(DdsKeyword kw, String name, String content) {
        content = content.trim();

        if (content.isEmpty()) {
            return;
        }

        // Special keyword handling
        switch (name) {
            case "COMP":
                parseCompParameters(kw, content);
                return;
            case "RANGE":
                parseRangeParameters(kw, content);
                return;
            case "REFFLD":
                parseReffldParameters(kw, content);
                return;
            case "REF":
                parseRefParameters(kw, content);
                return;
            default:
                // Generic parameter parsing
                break;
        }

        // Tokenize content into a list of values
        List<String> tokens = tokenizeParameters(content);

        if (tokens.size() == 1) {
            kw.setValue(tokens.get(0));
        } else if (tokens.size() > 1) {
            kw.setValues(tokens);
        }
    }

    /**
     * Parse COMP keyword parameters: COMP(operator value)
     * e.g., COMP(EQ 'A') → comparisonOperator="EQ", comparisonValue="A"
     */
    private void parseCompParameters(DdsKeyword kw, String content) {
        List<String> tokens = tokenizeParameters(content);
        if (tokens.size() >= 2) {
            kw.setComparisonOperator(tokens.get(0).toUpperCase());
            kw.setComparisonValue(tokens.get(1));
        } else if (tokens.size() == 1) {
            // Just operator, no value
            kw.setComparisonOperator(tokens.get(0).toUpperCase());
        }
    }

    /**
     * Parse RANGE keyword parameters: RANGE(low high)
     * e.g., RANGE('100' '999') → rangeFrom="100", rangeTo="999"
     */
    private void parseRangeParameters(DdsKeyword kw, String content) {
        List<String> tokens = tokenizeParameters(content);
        if (tokens.size() >= 2) {
            kw.setRangeFrom(tokens.get(0));
            kw.setRangeTo(tokens.get(1));
        } else if (tokens.size() == 1) {
            kw.setRangeFrom(tokens.get(0));
        }
    }

    /**
     * Parse REFFLD keyword parameters:
     *   REFFLD(field)             → referenceField only
     *   REFFLD(field file)        → referenceField + referenceFile
     *   REFFLD(field recfmt file) → referenceField + referenceRecordFormat + referenceFile
     *
     * Special: REFFLD(field *SRC) → referenceField + referenceFile="*SRC"
     */
    private void parseReffldParameters(DdsKeyword kw, String content) {
        List<String> tokens = tokenizeParameters(content);
        if (tokens.isEmpty()) return;

        // Arg 1 is always the field name
        kw.setReferenceField(tokens.get(0));

        switch (tokens.size()) {
            case 1:
                // REFFLD(field)
                kw.setValue(tokens.get(0));
                break;
            case 2:
                // REFFLD(field file) — 2nd arg is the file
                kw.setReferenceFile(tokens.get(1));
                kw.setValue(null);
                kw.setValues(tokens);
                break;
            default:
                // REFFLD(field recfmt file) — 3-arg form
                kw.setReferenceRecordFormat(tokens.get(1));
                kw.setReferenceFile(tokens.get(2));
                kw.setValue(null);
                kw.setValues(tokens);
                break;
        }
    }

    /**
     * Parse REF keyword parameters: REF(filename) or REF(filename recordformat)
     * Supports qualified names: REF(LIB/FILENAME) or REF(*LIBL/FILENAME RECFMT)
     * e.g., REF(FLDREFPF) → value="FLDREFPF"
     *       REF(FLDREFPF REFREC) → value="FLDREFPF", referenceRecordFormat="REFREC"
     *       REF(MYLIB/FLDREFPF) → value="MYLIB/FLDREFPF"
     */
    private void parseRefParameters(DdsKeyword kw, String content) {
        List<String> tokens = tokenizeParameters(content);
        if (tokens.size() >= 1) {
            kw.setValue(tokens.get(0));
        }
        if (tokens.size() >= 2) {
            kw.setReferenceRecordFormat(tokens.get(1));
        }
    }

    /**
     * Tokenize parameter content into individual values.
     * Handles:
     *   - 'quoted strings' → strip quotes
     *   - X'hex values' → keep as-is including X prefix
     *   - *SPECIAL_VALUES → starts with *, keep as-is
     *   - LIB/FILE → qualified names with /, keep as-is
     *   - FIELD1 → unquoted identifiers
     *   - 123 → unquoted numbers
     *   Spaces are delimiters between parameters.
     */
    private List<String> tokenizeParameters(String content) {
        List<String> tokens = new ArrayList<>();
        int pos = 0;
        int len = content.length();

        while (pos < len) {
            // Skip whitespace
            while (pos < len && content.charAt(pos) == ' ') {
                pos++;
            }
            if (pos >= len) break;

            char c = content.charAt(pos);

            if (c == 'X' && pos + 1 < len && content.charAt(pos + 1) == '\'') {
                // Hex literal: X'...'
                StringBuilder hexVal = new StringBuilder("X'");
                pos += 2; // skip X'
                while (pos < len && content.charAt(pos) != '\'') {
                    hexVal.append(content.charAt(pos));
                    pos++;
                }
                if (pos < len) {
                    hexVal.append('\'');
                    pos++; // skip closing '
                }
                tokens.add(hexVal.toString());
            } else if (c == '\'') {
                // Quoted string: '...'
                pos++; // skip opening '
                StringBuilder quoted = new StringBuilder();
                while (pos < len) {
                    char qc = content.charAt(pos);
                    if (qc == '\'') {
                        pos++;
                        // Check for escaped quote ('')
                        if (pos < len && content.charAt(pos) == '\'') {
                            quoted.append('\'');
                            pos++;
                        } else {
                            break;
                        }
                    } else {
                        quoted.append(qc);
                        pos++;
                    }
                }
                tokens.add(quoted.toString());
            } else {
                // Unquoted token: identifier, number, *special, LIB/FILE
                StringBuilder token = new StringBuilder();
                while (pos < len && content.charAt(pos) != ' ' && content.charAt(pos) != ')') {
                    token.append(content.charAt(pos));
                    pos++;
                }
                if (token.length() > 0) {
                    tokens.add(token.toString());
                }
            }
        }

        return tokens;
    }

    /**
     * Check if a character is valid in a keyword name.
     */
    private boolean isKeywordNameChar(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9');
    }
}
