package com.as400parser.prtf;

import com.as400parser.common.model.Location;
import com.as400parser.common.model.ParseError;
import com.as400parser.common.model.SourceLine;
import com.as400parser.dds.DdsKeywordParser;
import com.as400parser.dds.model.DdsComment;
import com.as400parser.dds.model.DdsKeyword;
import com.as400parser.dspf.model.ConditionedKeyword;
import com.as400parser.dspf.model.ConditioningIndicator;
import com.as400parser.prtf.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Builds PRTF IR content model ({@link PrtfContent}) from normalized source lines.
 * <p>
 * Adapted from {@link com.as400parser.dspf.DspfIrBuilder} with PRTF-specific simplifications:
 * <ul>
 *   <li>No key field handling — 'K' in col 17 is not valid for printer files</li>
 *   <li>No subfile detection — no SFL/SFLCTL/recordType logic</li>
 *   <li>Screen coordinates renamed to print coordinates (printLine/printPosition)</li>
 *   <li>System keywords: DATE, TIME, PAGNBR, MSGCON (not SYSNAME/USER)</li>
 *   <li>Usage: blank/O = output-only, P = program-to-system only</li>
 * </ul>
 * <p>
 * 11-step single-pass processing pipeline.
 */
public class PrtfIrBuilder {

    private static final Set<String> SYSTEM_KEYWORDS = Set.of(
        "DATE", "TIME", "PAGNBR", "MSGCON"
    );

    private final DdsKeywordParser keywordParser = new DdsKeywordParser();

    /**
     * Build PRTF content from normalized source lines.
     *
     * @param normalizedLines lines padded to 80 chars
     * @return populated PrtfContent
     */
    public PrtfContent buildContent(List<String> normalizedLines) {
        return buildContent(normalizedLines, null);
    }

    /**
     * Build PRTF content from normalized source lines with sequence numbers.
     *
     * @param normalizedLines  lines padded to 80 chars
     * @param sequenceNumbers  original sequence numbers from cols 1-5 (may be null)
     * @return populated PrtfContent
     */
    public PrtfContent buildContent(List<String> normalizedLines, String[] sequenceNumbers) {
        List<SourceLine> sourceLines = new ArrayList<>();
        List<DdsKeyword> fileKeywords = new ArrayList<>();
        List<PrtfRecordFormat> recordFormats = new ArrayList<>();
        List<DdsComment> comments = new ArrayList<>();
        List<ParseError> errors = new ArrayList<>();

        // State
        boolean seenRecord = false;
        PrtfRecordFormat currentFormat = null;
        Object previousElement = null; // PrtfFieldDefinition or PrtfConstant
        int lastEndColumn = 0; // track end column of last field/constant for +n resolution
        int lastPrintLine = 0; // track print line of last field/constant for line inheritance
        List<String> continuationBuffer = new ArrayList<>();

        for (int i = 0; i < normalizedLines.size(); i++) {
            String line = normalizedLines.get(i);
            int lineNum = i + 1;

            // Step 1: build SourceLine
            String seqNum = (sequenceNumbers != null && i < sequenceNumbers.length)
                    ? sequenceNumbers[i] : null;
            SourceLine sl = buildSourceLine(line, lineNum, seqNum);
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

                // Step 3: Classify and process
                char nameType = line.length() > 16 ? line.charAt(16) : ' ';
                String nameArea = line.length() >= 28 ? line.substring(18, 28).trim() : "";
                boolean hasName = !nameArea.isEmpty();
                Integer printLine = parsePrintLine(line);
                Integer printPos = parsePrintPosition(line);
                boolean hasPos = hasPositionArea(line);
                String kwArea = keywordParser.extractKeywordArea(line);
                List<ConditioningIndicator> indicators = parseConditioningIndicators(line);
                boolean hasIndicators = !indicators.isEmpty();

                if (nameType == 'R' || nameType == 'r') {
                    // RECORD FORMAT
                    seenRecord = true;
                    PrtfRecordFormat rf = buildRecordFormat(line, lineNum, nameArea, indicators, kwArea);
                    recordFormats.add(rf);
                    currentFormat = rf;
                    previousElement = rf;
                    lastEndColumn = 0; // reset on new record format
                    lastPrintLine = 0;
                    sl.setSpecType("RECORD_FORMAT");

                    // No 'K' case — key fields are NOT valid for PRTF

                } else if (nameType == ' ') {
                    // Blank name type — field, constant, file keyword, conditioned keyword, or continuation
                    if (hasName) {
                        // NAMED FIELD
                        PrtfFieldDefinition field = buildFieldDefinition(line, lineNum, nameArea, indicators, kwArea);
                        // Inherit line from previous element if blank
                        if (field.getPrintLine() == null && lastPrintLine > 0) {
                            field.setPrintLine(lastPrintLine);
                        }
                        lastEndColumn = resolveFieldPlusPosition(field, lastEndColumn);
                        if (field.getPrintLine() != null) lastPrintLine = field.getPrintLine();
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
                               && printLine == null && !hasPos) {
                        // CONDITIONED KEYWORD-ONLY LINE
                        List<DdsKeyword> kws = keywordParser.parseKeywords(kwArea);
                        mergeConditionedKeywords(previousElement, kws, indicators, line);
                        sl.setSpecType("CONDITIONED_KEYWORD");

                    } else if (printLine != null || hasPos) {
                        // CONSTANT (has print position, no name)
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
                                    String contSeqNum = (sequenceNumbers != null && (i + 1) < sequenceNumbers.length)
                                            ? sequenceNumbers[i + 1] : null;
                                    SourceLine contSl = buildSourceLine(nextLine, i + 1, contSeqNum);
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

                        PrtfConstant constant = buildConstant(line, lineNum, indicators,
                                printLine, printPos, mergedKwArea);
                        // Inherit line from previous element if blank
                        if (constant.getPrintLine() == null && lastPrintLine > 0) {
                            constant.setPrintLine(lastPrintLine);
                        }
                        // Resolve +n position to absolute
                        int plusN = parsePlusN(constant.getPrintPositionRaw());
                        if (plusN >= 0 && lastEndColumn > 0) {
                            constant.setPrintPosition(lastEndColumn + plusN + 1);
                        }
                        if (constant.getPrintLine() != null) lastPrintLine = constant.getPrintLine();
                        constant.setRawSourceLines(constantRawLines);
                        if (currentFormat != null) {
                            currentFormat.getConstants().add(constant);
                        }
                        previousElement = constant;
                        lastEndColumn = computeConstantEndColumn(constant, lastEndColumn);
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
                    // Unrecognized name type (includes 'K' which is not valid for PRTF)
                    errors.add(ParseError.warning(lineNum, 17,
                            "Unrecognized name type '" + nameType + "' at col 17"
                            + (nameType == 'K' || nameType == 'k'
                                ? " (key fields not supported for printer files)" : "")));
                }

            } catch (Exception e) {
                errors.add(ParseError.error(lineNum, 1,
                        "Failed to parse line " + lineNum + ": " + e.getMessage()));
            }
        }

        // Build result
        PrtfContent content = new PrtfContent();
        content.setSourceLines(sourceLines);
        content.setFileKeywords(fileKeywords);
        content.setRecordFormats(recordFormats);
        content.setComments(comments);
        content.setParseErrors(errors);
        return content;
    }

    // ========================= Element Builders =========================

    private PrtfRecordFormat buildRecordFormat(String line, int lineNum, String name,
                                                List<ConditioningIndicator> indicators, String kwArea) {
        PrtfRecordFormat rf = new PrtfRecordFormat();
        rf.setLocation(Location.ofLine(lineNum));
        rf.getRawSourceLines().add(line.stripTrailing());
        rf.setConditioningIndicators(indicators);
        rf.setName(name);

        // Parse keywords and wrap as ConditionedKeyword
        List<DdsKeyword> kws = keywordParser.parseKeywords(kwArea);
        List<ConditionedKeyword> condKws = new ArrayList<>();
        for (DdsKeyword kw : kws) {
            condKws.add(new ConditionedKeyword(kw, new ArrayList<>()));
        }
        rf.setKeywords(condKws);

        // No recordType detection — PRTF has no SFL/SFLCTL

        // Extract TEXT(...)
        rf.setText(extractTextKeyword(condKws));

        return rf;
    }

    private PrtfFieldDefinition buildFieldDefinition(String line, int lineNum, String name,
                                                      List<ConditioningIndicator> indicators, String kwArea) {
        PrtfFieldDefinition field = new PrtfFieldDefinition();
        field.setLocation(Location.ofLine(lineNum));
        field.getRawSourceLines().add(line.stripTrailing());
        field.setConditioningIndicators(indicators);
        field.setName(name);
        field.setReference(charAtOrNull(line, 29));
        field.setLength(parseIntOrNull(extractColumnNullable(line, 30, 34)));
        field.setDataType(charAtOrNull(line, 35));
        field.setDecimalPositions(parseIntOrNull(extractColumnNullable(line, 36, 37)));
        field.setUsage(charAtOrNull(line, 38));
        field.setPrintLine(parsePrintLine(line));
        field.setPrintPosition(parsePrintPosition(line));
        field.setPrintPositionRaw(extractPositionRaw(line));

        // Parse keywords → ConditionedKeyword
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

    private PrtfConstant buildConstant(String line, int lineNum,
                                        List<ConditioningIndicator> indicators,
                                        Integer printLine, Integer printPos, String kwArea) {
        PrtfConstant constant = new PrtfConstant();
        constant.setLocation(Location.ofLine(lineNum));
        constant.getRawSourceLines().add(line.stripTrailing());
        constant.setConditioningIndicators(indicators);
        constant.setPrintLine(printLine);
        constant.setPrintPosition(printPos);
        constant.setPrintPositionRaw(extractPositionRaw(line));

        parseConstantKeywordArea(constant, kwArea);
        return constant;
    }

    /**
     * Parse keyword area for a constant, separating text/systemKeyword from real keywords.
     * <p>
     * For quoted text: extract text, then parse ONLY the remainder after closing quote.
     * For system keywords (DATE/TIME/PAGNBR/MSGCON): set systemKeyword, parse remainder.
     */
    private void parseConstantKeywordArea(PrtfConstant constant, String kwArea) {
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

    // ========================= Conditioning Indicators =========================

    /**
     * Parse conditioning indicators from DDS cols 8-16 (3 slots of 3 chars).
     * Each slot: optional 'N' prefix + 2-char indicator number.
     * Reuses same algorithm as DspfIrBuilder.
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
     * Lines like "A N31 SPACEA(1)" have indicators + keyword but no name/position.
     */
    private void mergeConditionedKeywords(Object previousElement,
                                           List<DdsKeyword> kws,
                                           List<ConditioningIndicator> indicators,
                                           String rawLine) {
        if (previousElement instanceof PrtfFieldDefinition field) {
            for (DdsKeyword kw : kws) {
                field.getKeywords().add(new ConditionedKeyword(kw, new ArrayList<>(indicators)));
            }
            field.getRawSourceLines().add(rawLine.stripTrailing());
        } else if (previousElement instanceof PrtfConstant constant) {
            for (DdsKeyword kw : kws) {
                constant.getKeywords().add(new ConditionedKeyword(kw, new ArrayList<>(indicators)));
            }
            constant.getRawSourceLines().add(rawLine.stripTrailing());
        } else if (previousElement instanceof PrtfRecordFormat rf) {
            // Record-level conditioned keywords
            for (DdsKeyword kw : kws) {
                rf.getKeywords().add(new ConditionedKeyword(kw, new ArrayList<>(indicators)));
            }
            rf.getRawSourceLines().add(rawLine.stripTrailing());
        }
    }

    /**
     * Append unconditioned keywords from continuation line to previous element.
     */
    private void appendKeywordsToPrevious(Object element, List<DdsKeyword> kws, String line) {
        if (element instanceof PrtfFieldDefinition field) {
            for (DdsKeyword kw : kws) {
                field.getKeywords().add(new ConditionedKeyword(kw, new ArrayList<>()));
            }
            field.getRawSourceLines().add(line.stripTrailing());
            populateFieldReference(field, kws);
        } else if (element instanceof PrtfConstant constant) {
            for (DdsKeyword kw : kws) {
                constant.getKeywords().add(new ConditionedKeyword(kw, new ArrayList<>()));
            }
            constant.getRawSourceLines().add(line.stripTrailing());
        } else if (element instanceof PrtfRecordFormat rf) {
            for (DdsKeyword kw : kws) {
                rf.getKeywords().add(new ConditionedKeyword(kw, new ArrayList<>()));
            }
            rf.getRawSourceLines().add(line.stripTrailing());
        }
        // No KeyDefinition case — PRTF has no key fields
    }

    /**
     * Apply merged keywords from continuation buffer to the pending element.
     */
    private void applyMergedKeywords(Object element, List<DdsKeyword> kws, List<String> continuationLines) {
        if (element instanceof PrtfFieldDefinition field) {
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
        } else if (element instanceof PrtfRecordFormat rf) {
            List<ConditionedKeyword> condKws = new ArrayList<>();
            for (DdsKeyword kw : kws) {
                condKws.add(new ConditionedKeyword(kw, new ArrayList<>()));
            }
            rf.setKeywords(condKws);
            rf.setText(extractTextKeyword(condKws));
            // No SFL/SFLCTL detection for PRTF
            // Add continuation lines to rawSourceLines
            for (int j = 1; j < continuationLines.size(); j++) {
                rf.getRawSourceLines().add(continuationLines.get(j).stripTrailing());
            }
        } else if (element instanceof PrtfConstant constant) {
            // For text continuation, merge keyword areas and re-parse text + keywords
            String mergedArea = keywordParser.mergeKeywordAreas(continuationLines);
            constant.getKeywords().clear();
            constant.setText(null);
            constant.setSystemKeyword(null);
            parseConstantKeywordArea(constant, mergedArea);
        }
    }

    // ========================= Continuation First Line Processing =========================

    private void processFirstContinuationLine(String line, int lineNum, SourceLine sl,
                                               boolean seenRecord,
                                               PrtfRecordFormat currentFormat,
                                               List<DdsKeyword> fileKeywords,
                                               List<PrtfRecordFormat> recordFormats,
                                               List<ParseError> errors) {
        char nameType = line.length() > 16 ? line.charAt(16) : ' ';
        String nameArea = line.length() >= 28 ? line.substring(18, 28).trim() : "";
        List<ConditioningIndicator> indicators = parseConditioningIndicators(line);

        if (nameType == 'R' || nameType == 'r') {
            // Record format — create with deferred keywords
            PrtfRecordFormat rf = new PrtfRecordFormat();
            rf.setLocation(Location.ofLine(lineNum));
            rf.getRawSourceLines().add(line.stripTrailing());
            rf.setConditioningIndicators(indicators);
            rf.setName(nameArea);
            rf.setKeywords(new ArrayList<>());
            recordFormats.add(rf);
            sl.setSpecType("RECORD_FORMAT");

        } else if (nameType == ' ' && !nameArea.isEmpty()) {
            // Named field — create with deferred keywords
            PrtfFieldDefinition field = new PrtfFieldDefinition();
            field.setLocation(Location.ofLine(lineNum));
            field.getRawSourceLines().add(line.stripTrailing());
            field.setConditioningIndicators(indicators);
            field.setName(nameArea);
            field.setReference(charAtOrNull(line, 29));
            field.setLength(parseIntOrNull(extractColumnNullable(line, 30, 34)));
            field.setDataType(charAtOrNull(line, 35));
            field.setDecimalPositions(parseIntOrNull(extractColumnNullable(line, 36, 37)));
            field.setUsage(charAtOrNull(line, 38));
            field.setPrintLine(parsePrintLine(line));
            field.setPrintPosition(parsePrintPosition(line));
            field.setPrintPositionRaw(extractPositionRaw(line));
            field.setSource("direct");
            if (currentFormat != null) {
                currentFormat.getFields().add(field);
            }
            sl.setSpecType("FIELD_DEFINITION");

        } else if (nameType == ' ' && nameArea.isEmpty() && !seenRecord) {
            // File keyword — defer
            sl.setSpecType("FILE_KEYWORD");

        } else if (nameType == ' ' && nameArea.isEmpty() && seenRecord) {
            // Could be a constant with print position or a keyword continuation
            Integer printLine = parsePrintLine(line);
            Integer printPos = parsePrintPosition(line);
            if (printLine != null || hasPositionArea(line)) {
                // CONSTANT with continuation — create and add to current format
                PrtfConstant constant = new PrtfConstant();
                constant.setLocation(Location.ofLine(lineNum));
                constant.getRawSourceLines().add(line.stripTrailing());
                constant.setConditioningIndicators(indicators);
                constant.setPrintLine(printLine);
                constant.setPrintPosition(printPos);
                constant.setPrintPositionRaw(extractPositionRaw(line));
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

    private Object getLastAddedElement(PrtfRecordFormat currentFormat, Object fallback) {
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

    private Integer parsePrintLine(String line) {
        return parseIntOrNull(extractColumnNullable(line, 39, 41));
    }

    private Integer parsePrintPosition(String line) {
        String raw = extractColumnNullable(line, 42, 44);
        if (raw != null && raw.contains("+")) return null;
        return parseIntOrNull(raw);
    }

    /**
     * Check if cols 42-44 have any non-blank position value (absolute or +n).
     */
    private boolean hasPositionArea(String line) {
        String raw = extractColumnNullable(line, 42, 44);
        return raw != null;
    }

    /**
     * Extract raw position text from cols 42-44 (e.g., "25", "+1"). null if blank.
     */
    private String extractPositionRaw(String line) {
        return extractColumnNullable(line, 42, 44);
    }

    /**
     * Parse a +n value from raw position text. Returns the n value, or -1 if not +n.
     */
    private int parsePlusN(String raw) {
        if (raw == null) return -1;
        raw = raw.trim();
        if (raw.startsWith("+")) {
            try {
                return Integer.parseInt(raw.substring(1).trim());
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    /**
     * Resolve +n for a field using lastEndColumn. Returns new lastEndColumn.
     */
    private int resolveFieldPlusPosition(PrtfFieldDefinition field, int lastEndColumn) {
        String raw = field.getPrintPositionRaw();
        int n = parsePlusN(raw);
        if (n >= 0 && lastEndColumn > 0) {
            field.setPrintPosition(lastEndColumn + n + 1);
        }
        return computeFieldEndColumn(field, lastEndColumn);
    }

    /**
     * Compute field end column: position + length - 1.
     */
    private int computeFieldEndColumn(PrtfFieldDefinition field, int fallback) {
        if (field.getPrintPosition() != null && field.getLength() != null) {
            return field.getPrintPosition() + field.getLength() - 1;
        }
        return fallback;
    }

    /**
     * Compute constant end column: position + display_length - 1.
     */
    private int computeConstantEndColumn(PrtfConstant constant, int fallback) {
        Integer pos = constant.getPrintPosition();
        if (pos != null) {
            int len = 0;
            if (constant.getText() != null) {
                len = constant.getText().length();
            } else if (constant.getSystemKeyword() != null) {
                len = switch (constant.getSystemKeyword()) {
                    case "DATE" -> 8;
                    case "TIME" -> 8;
                    case "PAGNBR" -> 4;
                    case "MSGCON" -> 8;
                    default -> 0;
                };
            }
            if (len > 0) {
                return pos + len - 1;
            }
        }
        return fallback;
    }

    // ========================= Utilities =========================

    private SourceLine buildSourceLine(String line, int lineNum, String seqNum) {
        boolean isBlank = line.isBlank();
        boolean isComment = line.length() > 6 && line.charAt(5) == 'A' && line.charAt(6) == '*';
        return new SourceLine(lineNum, line, null, seqNum != null ? seqNum : "", isComment, isBlank);
    }

    private String extractTextKeyword(List<ConditionedKeyword> keywords) {
        for (ConditionedKeyword ck : keywords) {
            if ("TEXT".equals(ck.getKeyword().getName())) return ck.getKeyword().getValue();
        }
        return null;
    }

    private String computeFieldSource(PrtfFieldDefinition field, List<DdsKeyword> kws) {
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
     * Populate convenience reference fields on PrtfFieldDefinition from REFFLD keyword.
     * Same logic as DspfIrBuilder.populateFieldReference.
     */
    private void populateFieldReference(PrtfFieldDefinition field, List<DdsKeyword> kws) {
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
     * Extract quoted text from keyword area. E.g., "'COMPANY REPORT' UNDERLINE" → "COMPANY REPORT".
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
     * E.g., "'COMPANY REPORT' UNDERLINE" returns " UNDERLINE".
     */
    private String getTextAfterQuotedString(String kwArea) {
        if (kwArea == null || !kwArea.startsWith("'")) return "";
        int pos = 1; // skip opening quote
        while (pos < kwArea.length()) {
            char c = kwArea.charAt(pos);
            if (c == '\'') {
                pos++;
                if (pos < kwArea.length() && kwArea.charAt(pos) == '\'') {
                    pos++; // skip second quote of escape pair
                } else {
                    return kwArea.substring(pos).trim();
                }
            } else {
                pos++;
            }
        }
        return ""; // No closing quote found
    }
}
