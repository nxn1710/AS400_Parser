package com.as400parser.dspf;

import com.as400parser.common.model.Location;
import com.as400parser.common.model.ParseError;
import com.as400parser.common.model.SourceLine;
import com.as400parser.dds.DdsKeywordParser;
import com.as400parser.dds.model.DdsComment;
import com.as400parser.dds.model.DdsKeyword;
import com.as400parser.dds.model.KeyDefinition;
import com.as400parser.dspf.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Builds DSPF IR content model ({@link DspfContent}) from normalized source lines.
 * <p>
 * 12-step single-pass processing pipeline:
 * <ol>
 *   <li>Build SourceLine entries</li>
 *   <li>Classify lines (comment, record, key, field, constant, system keyword, conditioned keyword, continuation)</li>
 *   <li>Extract conditioning indicators from cols 8-16</li>
 *   <li>Merge continuation lines (col 80 = '+')</li>
 *   <li>Merge conditioned keyword-only lines into preceding field/constant</li>
 *   <li>Parse keywords via DdsKeywordParser</li>
 *   <li>Group fields/constants under record formats</li>
 *   <li>Detect record type (sfl/sflctl/normal)</li>
 *   <li>Extract sflControlFor from SFLCTL(name)</li>
 *   <li>Parse screen coordinates from cols 39-44</li>
 *   <li>Detect system keyword constants (DATE/TIME/SYSNAME/USER/PAGNBR)</li>
 *   <li>Build sourceLines, comments, parseErrors arrays</li>
 * </ol>
 */
public class DspfIrBuilder {

    private static final Set<String> SYSTEM_KEYWORDS = Set.of(
        "DATE", "TIME", "SYSNAME", "USER", "PAGNBR"
    );

    private final DdsKeywordParser keywordParser = new DdsKeywordParser();

    /**
     * Build DSPF content from normalized source lines.
     *
     * @param normalizedLines lines padded to 80 chars
     * @return populated DspfContent
     */
    public DspfContent buildContent(List<String> normalizedLines) {
        List<SourceLine> sourceLines = new ArrayList<>();
        List<DdsKeyword> fileKeywords = new ArrayList<>();
        List<DspfRecordFormat> recordFormats = new ArrayList<>();
        List<DdsComment> comments = new ArrayList<>();
        List<ParseError> errors = new ArrayList<>();

        // State
        boolean seenRecord = false;
        DspfRecordFormat currentFormat = null;
        Object previousElement = null; // DspfFieldDefinition or DspfConstant
        List<String> continuationBuffer = new ArrayList<>();

        for (int i = 0; i < normalizedLines.size(); i++) {
            String line = normalizedLines.get(i);
            int lineNum = i + 1;

            // Step 1: build SourceLine
            SourceLine sl = buildSourceLine(line, lineNum);
            sourceLines.add(sl);

            try {
                // Skip blank lines
                if (line.isBlank() || line.length() < 7) {
                    sl.setBlank(true);
                    continue;
                }

                // Check form type
                char formType = line.charAt(5);
                if (formType != 'A' && formType != 'a') {
                    sl.setBlank(true);
                    continue;
                }

                // Step 2: Comment check
                if (line.charAt(6) == '*') {
                    String commentText = line.length() > 7 ? line.substring(7).trim() : "";
                    comments.add(new DdsComment(lineNum, commentText));
                    sl.setComment(true);
                    sl.setSpecType("COMMENT");
                    continue;
                }

                // Continuation handling (col 80 = '+')
                if (keywordParser.hasContinuation(line)) {
                    if (continuationBuffer.isEmpty()) {
                        // First line of continuation sequence — process columns but defer keywords
                        continuationBuffer.add(line);
                        processFirstContinuationLine(line, lineNum, sl, seenRecord,
                                currentFormat, fileKeywords, recordFormats, errors);
                        // Update state
                        if (isRecordFormat(line)) {
                            seenRecord = true;
                            currentFormat = recordFormats.get(recordFormats.size() - 1);
                            previousElement = currentFormat;
                        } else {
                            previousElement = getLastAddedElement(currentFormat, previousElement);
                        }
                    } else {
                        continuationBuffer.add(line);
                        sl.setSpecType("CONTINUATION");
                    }
                    continue;
                }

                // If we had buffered continuation lines, finalize them
                if (!continuationBuffer.isEmpty()) {
                    continuationBuffer.add(line);
                    sl.setSpecType("CONTINUATION");
                    List<DdsKeyword> mergedKws = keywordParser.parseKeywordsWithContinuation(continuationBuffer);
                    applyMergedKeywords(previousElement, mergedKws, continuationBuffer);
                    continuationBuffer.clear();
                    continue;
                }

                // Step 2: Classify and process
                char nameType = line.length() > 16 ? line.charAt(16) : ' ';
                String nameArea = line.length() >= 28 ? line.substring(18, 28).trim() : "";
                boolean hasName = !nameArea.isEmpty();
                Integer screenLine = parseScreenLine(line);
                Integer screenPos = parseScreenPosition(line);
                String kwArea = keywordParser.extractKeywordArea(line);
                List<ConditioningIndicator> indicators = parseConditioningIndicators(line);
                boolean hasIndicators = !indicators.isEmpty();

                if (nameType == 'R' || nameType == 'r') {
                    // RECORD FORMAT
                    seenRecord = true;
                    DspfRecordFormat rf = buildRecordFormat(line, lineNum, nameArea, indicators, kwArea);
                    recordFormats.add(rf);
                    currentFormat = rf;
                    previousElement = rf;
                    sl.setSpecType("RECORD_FORMAT");

                } else if (nameType == 'K' || nameType == 'k') {
                    // KEY FIELD (rare in DSPF)
                    KeyDefinition key = buildKeyDefinition(line, lineNum, nameArea, kwArea);
                    if (currentFormat != null) {
                        currentFormat.getKeys().add(key);
                    }
                    previousElement = key;
                    sl.setSpecType("KEY_FIELD");

                } else if (nameType == ' ') {
                    // Blank name type — field, constant, file keyword, conditioned keyword, or continuation
                    if (hasName) {
                        // NAMED FIELD
                        DspfFieldDefinition field = buildFieldDefinition(line, lineNum, nameArea, indicators, kwArea);
                        if (currentFormat != null) {
                            currentFormat.getFields().add(field);
                        }
                        previousElement = field;
                        sl.setSpecType("FIELD_DEFINITION");

                    } else if (!seenRecord && !kwArea.isEmpty()) {
                        // FILE-LEVEL KEYWORD (before any record format)
                        List<DdsKeyword> kws = keywordParser.parseKeywords(kwArea);
                        fileKeywords.addAll(kws);
                        sl.setSpecType("FILE_KEYWORD");

                    } else if (seenRecord && hasIndicators && !kwArea.isEmpty()
                               && screenLine == null && screenPos == null) {
                        // CONDITIONED KEYWORD-ONLY LINE
                        // e.g., A N60 DSPATR(PR) — has indicators + keyword, no name/position
                        List<DdsKeyword> kws = keywordParser.parseKeywords(kwArea);
                        mergeConditionedKeywords(previousElement, kws, indicators);
                        sl.setSpecType("CONDITIONED_KEYWORD");

                    } else if (screenLine != null || screenPos != null) {
                        // CONSTANT (has screen position, no name)
                        // Check for unterminated quoted text needing continuation
                        String mergedKwArea = kwArea;
                        List<String> constantRawLines = new ArrayList<>();
                        constantRawLines.add(line.stripTrailing());
                        if (hasUnterminatedQuote(kwArea)) {
                            // Look ahead for continuation lines to complete the text
                            StringBuilder sb = new StringBuilder(kwArea);
                            while (i + 1 < normalizedLines.size()) {
                                String nextLine = normalizedLines.get(i + 1);
                                if (nextLine.length() >= 7 && (nextLine.charAt(5) == 'A' || nextLine.charAt(5) == 'a')
                                        && nextLine.charAt(6) != '*') {
                                    String nextKwArea = keywordParser.extractKeywordArea(nextLine);
                                    // Strip trailing '+' from current area before merging
                                    String current = sb.toString();
                                    if (current.endsWith("+")) {
                                        sb.setLength(sb.length() - 1);
                                    }
                                    sb.append(nextKwArea);
                                    i++;
                                    constantRawLines.add(nextLine.stripTrailing());
                                    SourceLine contSl = buildSourceLine(nextLine, i + 1);
                                    contSl.setSpecType("CONTINUATION");
                                    sourceLines.add(contSl);
                                    if (!hasUnterminatedQuote(sb.toString())) {
                                        break; // Quote is now terminated
                                    }
                                } else {
                                    break; // Next line is not a valid continuation
                                }
                            }
                            mergedKwArea = sb.toString();
                        }

                        DspfConstant constant = buildConstant(line, lineNum, indicators,
                                screenLine, screenPos, mergedKwArea);
                        constant.setRawSourceLines(constantRawLines);
                        if (currentFormat != null) {
                            currentFormat.getConstants().add(constant);
                        }
                        previousElement = constant;
                        sl.setSpecType("CONSTANT");

                    } else if (!kwArea.isEmpty()) {
                        // CONTINUATION (keyword-only, no indicators, after record)
                        List<DdsKeyword> kws = keywordParser.parseKeywords(kwArea);
                        appendKeywordsToPrevious(previousElement, kws, line);
                        sl.setSpecType("CONTINUATION");

                    } else {
                        // Blank/unknown
                        sl.setBlank(true);
                    }
                } else {
                    // Unrecognized name type
                    errors.add(ParseError.warning(lineNum, 17,
                            "Unrecognized name type '" + nameType + "' at col 17"));
                }

            } catch (Exception e) {
                errors.add(ParseError.error(lineNum, 1,
                        "Failed to parse line " + lineNum + ": " + e.getMessage()));
            }
        }

        // Build result
        DspfContent content = new DspfContent();
        content.setSourceLines(sourceLines);
        content.setFileKeywords(fileKeywords);
        content.setRecordFormats(recordFormats);
        content.setComments(comments);
        content.setParseErrors(errors);
        return content;
    }

    // ========================= Element Builders =========================

    private DspfRecordFormat buildRecordFormat(String line, int lineNum, String name,
                                                List<ConditioningIndicator> indicators, String kwArea) {
        DspfRecordFormat rf = new DspfRecordFormat();
        rf.setLocation(Location.ofLine(lineNum));
        rf.setRawSourceLine(line);
        rf.setConditioningIndicators(indicators);
        rf.setName(name);

        // Parse keywords
        List<DdsKeyword> kws = keywordParser.parseKeywords(kwArea);
        rf.setKeywords(kws);

        // Detect record type from keywords
        rf.setRecordType(detectRecordType(kws));

        // Extract sflControlFor from SFLCTL(name)
        for (DdsKeyword kw : kws) {
            if ("SFLCTL".equals(kw.getName())) {
                rf.setSflControlFor(kw.getValue());
                break;
            }
        }

        // Extract TEXT(...)
        rf.setText(extractTextKeyword(kws));

        return rf;
    }

    private DspfFieldDefinition buildFieldDefinition(String line, int lineNum, String name,
                                                      List<ConditioningIndicator> indicators, String kwArea) {
        DspfFieldDefinition field = new DspfFieldDefinition();
        field.setLocation(Location.ofLine(lineNum));
        field.getRawSourceLines().add(line.stripTrailing());
        field.setConditioningIndicators(indicators);
        field.setName(name);
        field.setReference(charAtOrNull(line, 29));
        field.setLength(parseIntOrNull(extractColumnNullable(line, 30, 34)));
        field.setDataType(charAtOrNull(line, 35));
        field.setDecimalPositions(parseIntOrNull(extractColumnNullable(line, 36, 37)));
        field.setUsage(charAtOrNull(line, 38));
        field.setScreenLine(parseScreenLine(line));
        field.setScreenPosition(parseScreenPosition(line));

        // Parse keywords → ConditionedKeyword (no indicators on the field itself for keyword-level)
        List<DdsKeyword> kws = keywordParser.parseKeywords(kwArea);
        for (DdsKeyword kw : kws) {
            field.getKeywords().add(new ConditionedKeyword(kw, new ArrayList<>()));
        }

        // Source detection
        if ("R".equals(field.getReference()) || hasKeyword(kws, "REFFLD")) {
            field.setSource("reference");
        } else {
            field.setSource("direct");
        }

        // Populate reference convenience fields from REFFLD keyword
        populateFieldReference(field, kws);

        return field;
    }

    private DspfConstant buildConstant(String line, int lineNum,
                                        List<ConditioningIndicator> indicators,
                                        Integer screenLine, Integer screenPos, String kwArea) {
        DspfConstant constant = new DspfConstant();
        constant.setLocation(Location.ofLine(lineNum));
        constant.getRawSourceLines().add(line.stripTrailing());
        constant.setConditioningIndicators(indicators);
        constant.setScreenLine(screenLine);
        constant.setScreenPosition(screenPos);

        parseConstantKeywordArea(constant, kwArea);
        return constant;
    }

    /**
     * Parse keyword area for a constant, separating text/systemKeyword from real keywords.
     * <p>
     * For quoted text: extract text, then parse ONLY the remainder after closing quote.
     * For system keywords (DATE/TIME/etc.): set systemKeyword, parse remainder.
     */
    private void parseConstantKeywordArea(DspfConstant constant, String kwArea) {
        String trimmedKwArea = kwArea.trim();
        if (trimmedKwArea.isEmpty()) return;

        if (trimmedKwArea.startsWith("'")) {
            // Quoted text constant — extract text and find where it ends
            String text = extractQuotedText(trimmedKwArea);
            constant.setText(text);
            // Skip past the closing quote to find remaining keywords
            String remaining = getTextAfterQuotedString(trimmedKwArea);
            if (!remaining.isEmpty()) {
                List<DdsKeyword> remainingKws = keywordParser.parseKeywords(remaining);
                for (DdsKeyword kw : remainingKws) {
                    constant.getKeywords().add(new ConditionedKeyword(kw, new ArrayList<>()));
                }
            }
        } else {
            // Check for system keyword constant
            String firstToken = trimmedKwArea.split("\\s+")[0].toUpperCase();
            if (SYSTEM_KEYWORDS.contains(firstToken)) {
                constant.setSystemKeyword(firstToken);
                String remaining = trimmedKwArea.substring(firstToken.length()).trim();
                List<DdsKeyword> remainingKws = keywordParser.parseKeywords(remaining);
                for (DdsKeyword kw : remainingKws) {
                    constant.getKeywords().add(new ConditionedKeyword(kw, new ArrayList<>()));
                }
            } else {
                // Fallback: treat entire keyword area as keywords
                List<DdsKeyword> kws = keywordParser.parseKeywords(kwArea);
                for (DdsKeyword kw : kws) {
                    constant.getKeywords().add(new ConditionedKeyword(kw, new ArrayList<>()));
                }
            }
        }
    }

    private KeyDefinition buildKeyDefinition(String line, int lineNum, String name, String kwArea) {
        KeyDefinition key = new KeyDefinition();
        key.setLocation(Location.ofLine(lineNum));
        key.setRawSourceLine(line);
        key.setConditioningIndicators(extractColumnNullable(line, 8, 16));
        key.setFieldName(name);

        List<DdsKeyword> kws = keywordParser.parseKeywords(kwArea);
        key.setKeywords(kws);

        for (DdsKeyword kw : kws) {
            if ("DESCEND".equals(kw.getName())) {
                key.setSortOrder("descending");
                break;
            }
        }

        return key;
    }

    // ========================= Conditioning Indicators =========================

    /**
     * Parse conditioning indicators from DDS cols 8-16 (3 slots of 3 chars).
     * Each slot: optional 'N' prefix + 2-char indicator number.
     */
    List<ConditioningIndicator> parseConditioningIndicators(String line) {
        List<ConditioningIndicator> indicators = new ArrayList<>();
        if (line.length() < 16) return indicators;

        // 3 slots: cols 8-10 (idx 7-9), cols 11-13 (idx 10-12), cols 14-16 (idx 13-15)
        int[][] slots = {{7, 10}, {10, 13}, {13, 16}};
        for (int[] slot : slots) {
            int start = slot[0];
            int end = Math.min(slot[1], line.length());
            if (start >= line.length()) break;
            String slotStr = line.substring(start, end);

            if (slotStr.isBlank()) continue;

            slotStr = slotStr.trim();
            if (slotStr.isEmpty()) continue;

            boolean negated = false;
            String indicator;

            if (slotStr.toUpperCase().startsWith("N")) {
                negated = true;
                indicator = slotStr.substring(1).trim();
            } else {
                indicator = slotStr;
            }

            if (!indicator.isEmpty()) {
                indicators.add(new ConditioningIndicator(negated, indicator));
            }
        }
        return indicators;
    }

    // ========================= Keyword Merging =========================

    /**
     * Merge conditioned keyword-only lines into the preceding field/constant.
     * Lines like "A N60 DSPATR(PR)" have indicators + keyword but no name/position.
     */
    private void mergeConditionedKeywords(Object previousElement,
                                           List<DdsKeyword> kws,
                                           List<ConditioningIndicator> indicators) {
        if (previousElement instanceof DspfFieldDefinition field) {
            for (DdsKeyword kw : kws) {
                field.getKeywords().add(new ConditionedKeyword(kw, new ArrayList<>(indicators)));
            }
        } else if (previousElement instanceof DspfConstant constant) {
            for (DdsKeyword kw : kws) {
                constant.getKeywords().add(new ConditionedKeyword(kw, new ArrayList<>(indicators)));
            }
        } else if (previousElement instanceof DspfRecordFormat rf) {
            // Record-level conditioned keywords (e.g., A 40 SFLDSP)
            rf.getKeywords().addAll(kws);
        }
    }

    /**
     * Append unconditioned keywords from continuation line to previous element.
     */
    private void appendKeywordsToPrevious(Object element, List<DdsKeyword> kws, String line) {
        if (element instanceof DspfFieldDefinition field) {
            for (DdsKeyword kw : kws) {
                field.getKeywords().add(new ConditionedKeyword(kw, new ArrayList<>()));
            }
            field.getRawSourceLines().add(line.stripTrailing());
            populateFieldReference(field, kws);
        } else if (element instanceof DspfConstant constant) {
            for (DdsKeyword kw : kws) {
                constant.getKeywords().add(new ConditionedKeyword(kw, new ArrayList<>()));
            }
        } else if (element instanceof DspfRecordFormat rf) {
            rf.getKeywords().addAll(kws);
        } else if (element instanceof KeyDefinition key) {
            key.getKeywords().addAll(kws);
        }
    }

    /**
     * Apply merged keywords from continuation buffer to the pending element.
     */
    private void applyMergedKeywords(Object element, List<DdsKeyword> kws, List<String> continuationLines) {
        if (element instanceof DspfFieldDefinition field) {
            // Replace deferred keywords
            field.getKeywords().clear();
            for (DdsKeyword kw : kws) {
                field.getKeywords().add(new ConditionedKeyword(kw, new ArrayList<>()));
            }
            field.setSource(computeFieldSource(field, kws));
            populateFieldReference(field, kws);
            // Add continuation lines
            for (int j = 1; j < continuationLines.size(); j++) {
                field.getRawSourceLines().add(continuationLines.get(j).stripTrailing());
            }
        } else if (element instanceof DspfRecordFormat rf) {
            rf.setKeywords(kws);
            rf.setRecordType(detectRecordType(kws));
            rf.setText(extractTextKeyword(kws));
            for (DdsKeyword kw : kws) {
                if ("SFLCTL".equals(kw.getName())) {
                    rf.setSflControlFor(kw.getValue());
                    break;
                }
            }
        } else if (element instanceof DspfConstant constant) {
            // For text continuation, merge keyword areas and re-parse text + keywords
            String mergedArea = keywordParser.mergeKeywordAreas(continuationLines);
            constant.getKeywords().clear();
            constant.setText(null); // Reset text for re-parse from merged area
            constant.setSystemKeyword(null);
            parseConstantKeywordArea(constant, mergedArea);
        } else if (element instanceof KeyDefinition key) {
            key.setKeywords(kws);
            for (DdsKeyword kw : kws) {
                if ("DESCEND".equals(kw.getName())) {
                    key.setSortOrder("descending");
                    break;
                }
            }
        }
    }

    // ========================= Continuation First Line Processing =========================

    private void processFirstContinuationLine(String line, int lineNum, SourceLine sl,
                                               boolean seenRecord,
                                               DspfRecordFormat currentFormat,
                                               List<DdsKeyword> fileKeywords,
                                               List<DspfRecordFormat> recordFormats,
                                               List<ParseError> errors) {
        char nameType = line.length() > 16 ? line.charAt(16) : ' ';
        String nameArea = line.length() >= 28 ? line.substring(18, 28).trim() : "";
        List<ConditioningIndicator> indicators = parseConditioningIndicators(line);

        if (nameType == 'R' || nameType == 'r') {
            // Record format — create with deferred keywords
            DspfRecordFormat rf = new DspfRecordFormat();
            rf.setLocation(Location.ofLine(lineNum));
            rf.setRawSourceLine(line);
            rf.setConditioningIndicators(indicators);
            rf.setName(nameArea);
            rf.setKeywords(new ArrayList<>());
            recordFormats.add(rf);
            sl.setSpecType("RECORD_FORMAT");

        } else if (nameType == ' ' && !nameArea.isEmpty()) {
            // Named field — create with deferred keywords
            DspfFieldDefinition field = new DspfFieldDefinition();
            field.setLocation(Location.ofLine(lineNum));
            field.getRawSourceLines().add(line.stripTrailing());
            field.setConditioningIndicators(indicators);
            field.setName(nameArea);
            field.setReference(charAtOrNull(line, 29));
            field.setLength(parseIntOrNull(extractColumnNullable(line, 30, 34)));
            field.setDataType(charAtOrNull(line, 35));
            field.setDecimalPositions(parseIntOrNull(extractColumnNullable(line, 36, 37)));
            field.setUsage(charAtOrNull(line, 38));
            field.setScreenLine(parseScreenLine(line));
            field.setScreenPosition(parseScreenPosition(line));
            field.setSource("direct");
            if (currentFormat != null) {
                currentFormat.getFields().add(field);
            }
            sl.setSpecType("FIELD_DEFINITION");

        } else if (nameType == ' ' && nameArea.isEmpty() && !seenRecord) {
            // File keyword — defer
            sl.setSpecType("FILE_KEYWORD");

        } else if (nameType == ' ' && nameArea.isEmpty() && seenRecord) {
            // Could be a constant with screen position or a keyword continuation
            Integer screenLine = parseScreenLine(line);
            Integer screenPos = parseScreenPosition(line);
            if (screenLine != null || screenPos != null) {
                // CONSTANT with continuation — create and add to current format
                DspfConstant constant = new DspfConstant();
                constant.setLocation(Location.ofLine(lineNum));
                constant.getRawSourceLines().add(line.stripTrailing());
                constant.setConditioningIndicators(indicators);
                constant.setScreenLine(screenLine);
                constant.setScreenPosition(screenPos);
                // Don't parse keywords yet — they span continuation lines
                if (currentFormat != null) {
                    currentFormat.getConstants().add(constant);
                }
                sl.setSpecType("CONSTANT");
            } else {
                sl.setSpecType("CONTINUATION");
            }
        } else {
            sl.setSpecType("CONTINUATION");
        }
    }

    private boolean isRecordFormat(String line) {
        return line.length() > 16 && (line.charAt(16) == 'R' || line.charAt(16) == 'r');
    }

    private Object getLastAddedElement(DspfRecordFormat currentFormat, Object fallback) {
        if (currentFormat == null) return fallback;
        if (!currentFormat.getFields().isEmpty()) {
            return currentFormat.getFields().get(currentFormat.getFields().size() - 1);
        }
        if (!currentFormat.getConstants().isEmpty()) {
            return currentFormat.getConstants().get(currentFormat.getConstants().size() - 1);
        }
        return fallback;
    }

    // ========================= Column Extraction Helpers =========================

    private String extractColumn(String line, int colStart, int colEnd) {
        int start = colStart - 1;
        int end = Math.min(colEnd, line.length());
        if (start >= line.length()) return "";
        return line.substring(start, end).trim();
    }

    private String extractColumnNullable(String line, int colStart, int colEnd) {
        String val = extractColumn(line, colStart, colEnd);
        return val.isEmpty() ? null : val;
    }

    private String charAtOrNull(String line, int col) {
        int idx = col - 1;
        if (idx >= line.length()) return null;
        char c = line.charAt(idx);
        return (c == ' ') ? null : String.valueOf(c);
    }

    private Integer parseIntOrNull(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseScreenLine(String line) {
        return parseIntOrNull(extractColumnNullable(line, 39, 41));
    }

    private Integer parseScreenPosition(String line) {
        return parseIntOrNull(extractColumnNullable(line, 42, 44));
    }

    // ========================= Utilities =========================

    private SourceLine buildSourceLine(String line, int lineNum) {
        String seqNum = extractColumn(line, 1, 5);
        boolean isBlank = line.isBlank();
        boolean isComment = line.length() > 6 && line.charAt(5) == 'A' && line.charAt(6) == '*';
        return new SourceLine(lineNum, line, null, seqNum, isComment, isBlank);
    }

    /**
     * Detect record type from keywords: sfl, sflctl, or normal.
     */
    private String detectRecordType(List<DdsKeyword> keywords) {
        boolean hasSfl = false;
        for (DdsKeyword kw : keywords) {
            if ("SFLCTL".equals(kw.getName())) return "sflctl";
            if ("SFL".equals(kw.getName())) hasSfl = true;
        }
        return hasSfl ? "sfl" : "normal";
    }

    private String extractTextKeyword(List<DdsKeyword> keywords) {
        for (DdsKeyword kw : keywords) {
            if ("TEXT".equals(kw.getName())) return kw.getValue();
        }
        return null;
    }

    private String computeFieldSource(DspfFieldDefinition field, List<DdsKeyword> kws) {
        if ("R".equals(field.getReference())) return "reference";
        for (DdsKeyword kw : kws) {
            if ("REFFLD".equals(kw.getName())) return "reference";
        }
        return "direct";
    }

    private boolean hasKeyword(List<DdsKeyword> keywords, String name) {
        for (DdsKeyword kw : keywords) {
            if (name.equals(kw.getName())) return true;
        }
        return false;
    }

    /**
     * Populate convenience reference fields on DspfFieldDefinition from REFFLD keyword.
     * Same logic as DdsIrBuilder.populateFieldReference for PF/LF.
     */
    private void populateFieldReference(DspfFieldDefinition field, List<DdsKeyword> kws) {
        if (kws != null) {
            for (DdsKeyword kw : kws) {
                if ("REFFLD".equals(kw.getName())) {
                    field.setReferenceField(kw.getReferenceField());
                    field.setReferenceFile(kw.getReferenceFile());
                    field.setReferenceRecordFormat(kw.getReferenceRecordFormat());
                    return;
                }
            }
        }
        // Same-name reference: R in col29, no REFFLD → reference own name from REF file
        if ("R".equals(field.getReference()) && field.getReferenceField() == null) {
            field.setReferenceField(field.getName());
        }
    }

    /**
     * Extract quoted text from keyword area. E.g., "'Hello World' DSPATR(HI)" → "Hello World".
     */
    private String extractQuotedText(String kwArea) {
        if (kwArea == null || !kwArea.startsWith("'")) return null;
        StringBuilder text = new StringBuilder();
        int pos = 1; // skip opening quote
        while (pos < kwArea.length()) {
            char c = kwArea.charAt(pos);
            if (c == '\'') {
                pos++;
                // Check for escaped quote ''
                if (pos < kwArea.length() && kwArea.charAt(pos) == '\'') {
                    text.append('\'');
                    pos++;
                } else {
                    break;
                }
            } else {
                text.append(c);
                pos++;
            }
        }
        return text.toString();
    }

    /**
     * Check if a keyword area has an unterminated quoted string.
     * Returns true if it starts with (or contains) an opening quote
     * with no matching closing quote.
     */
    private boolean hasUnterminatedQuote(String kwArea) {
        String trimmed = kwArea.trim();
        if (!trimmed.contains("'")) return false;
        int quoteStart = trimmed.indexOf('\'');
        int pos = quoteStart + 1;
        while (pos < trimmed.length()) {
            char c = trimmed.charAt(pos);
            if (c == '\'') {
                pos++;
                // Check escaped quote ''
                if (pos < trimmed.length() && trimmed.charAt(pos) == '\'') {
                    pos++; // skip escaped quote
                } else {
                    return false; // Found closing quote
                }
            } else {
                pos++;
            }
        }
        return true; // No closing quote found
    }

    /**
     * Get text remaining after the closing quote in a keyword area.
     * E.g., "'Hello World' DSPATR(HI)" returns " DSPATR(HI)".
     * E.g., "'Hello World'" returns "".
     */
    private String getTextAfterQuotedString(String kwArea) {
        if (kwArea == null || !kwArea.startsWith("'")) return "";
        int pos = 1; // skip opening quote
        while (pos < kwArea.length()) {
            char c = kwArea.charAt(pos);
            if (c == '\'') {
                pos++;
                // Check for escaped quote ''
                if (pos < kwArea.length() && kwArea.charAt(pos) == '\'') {
                    pos++; // skip second quote of escape pair
                } else {
                    // Found closing quote — return remainder
                    return kwArea.substring(pos).trim();
                }
            } else {
                pos++;
            }
        }
        return ""; // No closing quote found
    }
}
