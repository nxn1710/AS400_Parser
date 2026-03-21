package com.as400parser.dds;

import com.as400parser.common.model.Location;
import com.as400parser.common.model.ParseError;
import com.as400parser.common.model.SourceLine;
import com.as400parser.dds.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds DDS IR content models ({@link DdsPfContent} or {@link DdsLfContent})
 * from normalized source lines using a single-pass state machine.
 * <p>
 * Processing pipeline: classify → extract columns → parse keywords → build content.
 */
public class DdsIrBuilder {

    private final DdsLineClassifier classifier = new DdsLineClassifier();
    private final DdsKeywordParser keywordParser = new DdsKeywordParser();

    /**
     * Build content model from normalized lines.
     *
     * @param lines      normalized source lines (padded to 80 chars)
     * @param sourceType "DDS_PF" or "DDS_LF"
     * @return DdsPfContent or DdsLfContent
     */
    public Object buildContent(String[] lines, String sourceType) {
        boolean isPf = "DDS_PF".equals(sourceType);

        // Shared accumulators
        List<SourceLine> sourceLines = new ArrayList<>();
        List<DdsKeyword> fileKeywords = new ArrayList<>();
        List<DdsComment> comments = new ArrayList<>();

        // PF or LF record formats
        List<RecordFormat> pfFormats = isPf ? new ArrayList<>() : null;
        List<LfRecordFormat> lfFormats = isPf ? null : new ArrayList<>();

        // State machine
        boolean seenRecord = false;
        RecordFormat currentPfFormat = null;
        LfRecordFormat currentLfFormat = null;
        List<ParseError> errors = new ArrayList<>();

        // Track previous element for continuation lines
        Object pendingElement = null;

        // Collect continuation lines for merged keyword parsing
        List<String> continuationBuffer = new ArrayList<>();

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            int lineNum = i + 1;

            // Build sourceLines for IR
            SourceLine sl = buildSourceLine(line, lineNum);
            sourceLines.add(sl);

            try {
                // Classify
                DdsLineType type = classifier.classify(line, seenRecord);

                // --- Fix 3: Continuation line collection ---
                // If this line has a '+' at col 80, buffer it and continue
                if (keywordParser.hasContinuation(line) && type != DdsLineType.COMMENT && type != DdsLineType.BLANK) {
                    continuationBuffer.add(line);
                    sl.setSpecType(type.name());
                    // Process the non-keyword portion (e.g., field columns) on the first line
                    if (continuationBuffer.size() == 1) {
                        processLineExceptKeywords(type, line, lineNum, sl, isPf,
                                currentPfFormat, currentLfFormat, seenRecord,
                                fileKeywords, comments, pfFormats, lfFormats,
                                pendingElement);
                        // Update state from processLine
                        if (type == DdsLineType.RECORD_FORMAT) {
                            seenRecord = true;
                            if (isPf) {
                                currentPfFormat = pfFormats.get(pfFormats.size() - 1);
                            } else {
                                currentLfFormat = lfFormats.get(lfFormats.size() - 1);
                            }
                        }
                        pendingElement = getLastElement(type, isPf, currentPfFormat, currentLfFormat, pendingElement);
                    } else {
                        sl.setSpecType("CONTINUATION");
                    }
                    continue;
                }

                // If we had buffered continuation lines, finalize them now
                if (!continuationBuffer.isEmpty()) {
                    continuationBuffer.add(line); // add the final (non-continuation) line
                    sl.setSpecType("CONTINUATION");
                    // Merge all keyword areas and re-parse
                    List<DdsKeyword> mergedKws = keywordParser.parseKeywordsWithContinuation(continuationBuffer);
                    // Replace (not append) keywords on the pending element
                    replaceKeywordsOnElement(pendingElement, mergedKws, continuationBuffer);
                    continuationBuffer.clear();
                    continue;
                }

                switch (type) {
                    case BLANK:
                        break;

                    case COMMENT:
                        String commentText = line.length() > 7 ? line.substring(7).trim() : "";
                        comments.add(new DdsComment(lineNum, commentText));
                        sl.setComment(true);
                        sl.setSpecType("COMMENT");
                        break;

                    case FILE_KEYWORD: {
                        String kwArea = keywordParser.extractKeywordArea(line);
                        List<DdsKeyword> kws = keywordParser.parseKeywords(kwArea);
                        fileKeywords.addAll(kws);
                        sl.setSpecType("FILE_KEYWORD");
                        break;
                    }

                    case RECORD_FORMAT: {
                        seenRecord = true;
                        String name = extractColumn(line, 19, 28);
                        String condInd = extractColumnNullable(line, 8, 16);
                        String kwArea = keywordParser.extractKeywordArea(line);
                        List<DdsKeyword> kws = keywordParser.parseKeywords(kwArea);
                        Location loc = Location.ofLine(lineNum);

                        if (isPf) {
                            currentPfFormat = new RecordFormat();
                            currentPfFormat.setLocation(loc);
                            currentPfFormat.setRawSourceLine(line);
                            currentPfFormat.setConditioningIndicators(condInd);
                            currentPfFormat.setName(name);
                            currentPfFormat.setKeywords(kws);
                            currentPfFormat.setText(extractTextKeyword(kws));
                            pfFormats.add(currentPfFormat);
                        } else {
                            currentLfFormat = new LfRecordFormat();
                            currentLfFormat.setLocation(loc);
                            currentLfFormat.setRawSourceLine(line);
                            currentLfFormat.setConditioningIndicators(condInd);
                            currentLfFormat.setName(name);
                            currentLfFormat.setKeywords(kws);
                            currentLfFormat.setText(extractTextKeyword(kws));
                            // Extract PFILE/JFILE from keywords
                            populateLfFileRef(currentLfFormat, kws);
                            lfFormats.add(currentLfFormat);
                        }
                        sl.setSpecType("RECORD_FORMAT");
                        pendingElement = isPf ? currentPfFormat : currentLfFormat;
                        break;
                    }

                    case FIELD_DEFINITION: {
                        FieldDefinition field = buildFieldDefinition(line, lineNum);
                        if (isPf && currentPfFormat != null) {
                            currentPfFormat.getFields().add(field);
                        } else if (!isPf && currentLfFormat != null) {
                            currentLfFormat.getFields().add(field);
                        }
                        pendingElement = field;
                        sl.setSpecType("FIELD_DEFINITION");
                        break;
                    }

                    case KEY_FIELD: {
                        KeyDefinition key = buildKeyDefinition(line, lineNum);
                        if (isPf && currentPfFormat != null) {
                            currentPfFormat.getKeys().add(key);
                        } else if (!isPf && currentLfFormat != null) {
                            currentLfFormat.getKeys().add(key);
                        }
                        pendingElement = key;
                        sl.setSpecType("KEY_FIELD");
                        break;
                    }

                    case SELECT_FIELD:
                    case OMIT_FIELD: {
                        SelectOmitSpec spec = buildSelectOmitSpec(line, lineNum, type);
                        if (!isPf && currentLfFormat != null) {
                            currentLfFormat.getSelectOmit().add(spec);
                        }
                        pendingElement = spec;
                        sl.setSpecType(type == DdsLineType.SELECT_FIELD ? "SELECT_FIELD" : "OMIT_FIELD");
                        break;
                    }

                    case JOIN_SPEC: {
                        JoinSpec joinSpec = buildJoinSpec(line, lineNum);
                        if (!isPf && currentLfFormat != null) {
                            currentLfFormat.getJoinSpecs().add(joinSpec);
                        }
                        pendingElement = joinSpec;
                        sl.setSpecType("JOIN_SPEC");
                        break;
                    }

                    case CONTINUATION: {
                        // Standalone continuation (no prior '+' line) — parse and append
                        String kwArea = keywordParser.extractKeywordArea(line);
                        List<DdsKeyword> kws = keywordParser.parseKeywords(kwArea);
                        appendKeywordsToPrevious(pendingElement, kws, line);
                        sl.setSpecType("CONTINUATION");
                        break;
                    }
                }
            } catch (Exception e) {
                // Fix 2: Error recovery — record error and continue
                errors.add(ParseError.error(
                    lineNum, 1,
                    "Failed to parse line " + lineNum + ": " + e.getMessage()
                ));
            }
        }

        // Detect LF subtypes
        if (!isPf && lfFormats != null) {
            for (LfRecordFormat lf : lfFormats) {
                lf.setLfType(detectLfSubtype(lf, lfFormats.size()));
            }
        }

        // Build final content
        if (isPf) {
            DdsPfContent content = new DdsPfContent();
            content.setSourceLines(sourceLines);
            content.setFileKeywords(fileKeywords);
            content.setRecordFormats(pfFormats);
            content.setComments(comments);
            content.setParseErrors(errors);
            return content;
        } else {
            DdsLfContent content = new DdsLfContent();
            content.setSourceLines(sourceLines);
            content.setFileKeywords(fileKeywords);
            content.setRecordFormats(lfFormats);
            content.setComments(comments);
            content.setParseErrors(errors);
            return content;
        }
    }

    // --- Column Extraction Helpers ---

    /**
     * Extract and trim a column range from a line (1-indexed columns).
     */
    private String extractColumn(String line, int colStart, int colEnd) {
        int start = colStart - 1; // 0-indexed
        int end = Math.min(colEnd, line.length());
        if (start >= line.length()) return "";
        return line.substring(start, end).trim();
    }

    /**
     * Extract a column range, returning null if blank.
     */
    private String extractColumnNullable(String line, int colStart, int colEnd) {
        String val = extractColumn(line, colStart, colEnd);
        return val.isEmpty() ? null : val;
    }

    /**
     * Extract a single character at a column position (1-indexed), returning null if blank.
     */
    private String charAtOrNull(String line, int col) {
        int idx = col - 1; // 0-indexed
        if (idx >= line.length()) return null;
        char c = line.charAt(idx);
        return (c == ' ') ? null : String.valueOf(c);
    }

    /**
     * Parse an integer from a trimmed string, returning null if blank or not a number.
     */
    private Integer parseIntOrNull(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // --- Element Builders ---

    private FieldDefinition buildFieldDefinition(String line, int lineNum) {
        FieldDefinition field = new FieldDefinition();
        field.setLocation(Location.ofLine(lineNum));
        field.getRawSourceLines().add(line.stripTrailing());

        field.setConditioningIndicators(extractColumnNullable(line, 8, 16));
        field.setName(extractColumn(line, 19, 28));
        field.setReference(charAtOrNull(line, 29));
        field.setLength(parseIntOrNull(extractColumnNullable(line, 30, 34)));
        field.setDataType(charAtOrNull(line, 35));
        field.setDecimalPositions(parseIntOrNull(extractColumnNullable(line, 36, 37)));
        field.setUsage(charAtOrNull(line, 38));
        field.setFieldLocation(extractColumnNullable(line, 39, 44));

        // Parse keywords
        String kwArea = keywordParser.extractKeywordArea(line);
        List<DdsKeyword> kws = keywordParser.parseKeywords(kwArea);
        field.setKeywords(kws);

        // Compute source type
        field.setSource(computeFieldSource(field));

        // Populate reference convenience fields
        populateFieldReference(field);

        return field;
    }

    private KeyDefinition buildKeyDefinition(String line, int lineNum) {
        KeyDefinition key = new KeyDefinition();
        key.setLocation(Location.ofLine(lineNum));
        key.setRawSourceLine(line);

        key.setConditioningIndicators(extractColumnNullable(line, 8, 16));
        key.setFieldName(extractColumn(line, 19, 28));

        // Parse keywords
        String kwArea = keywordParser.extractKeywordArea(line);
        List<DdsKeyword> kws = keywordParser.parseKeywords(kwArea);
        key.setKeywords(kws);

        // Detect DESCEND
        for (DdsKeyword kw : kws) {
            if ("DESCEND".equals(kw.getName())) {
                key.setSortOrder("descending");
                break;
            }
        }

        return key;
    }

    private SelectOmitSpec buildSelectOmitSpec(String line, int lineNum, DdsLineType type) {
        SelectOmitSpec spec = new SelectOmitSpec();
        spec.setLocation(Location.ofLine(lineNum));
        spec.setRawSourceLine(line);

        spec.setConditioningIndicators(extractColumnNullable(line, 8, 16));
        spec.setType(type == DdsLineType.SELECT_FIELD ? "select" : "omit");
        spec.setFieldName(extractColumnNullable(line, 19, 28));

        // Parse keywords
        String kwArea = keywordParser.extractKeywordArea(line);
        List<DdsKeyword> kws = keywordParser.parseKeywords(kwArea);
        spec.setKeywords(kws);

        // Check for ALL rule
        for (DdsKeyword kw : kws) {
            if ("ALL".equals(kw.getName())) {
                spec.setRule("all");
                break;
            }
        }

        return spec;
    }

    private JoinSpec buildJoinSpec(String line, int lineNum) {
        JoinSpec joinSpec = new JoinSpec();
        joinSpec.setLocation(Location.ofLine(lineNum));
        joinSpec.setRawSourceLine(line);
        joinSpec.setConditioningIndicators(extractColumnNullable(line, 8, 16));

        // Parse keywords
        String kwArea = keywordParser.extractKeywordArea(line);
        List<DdsKeyword> kws = keywordParser.parseKeywords(kwArea);
        joinSpec.setKeywords(kws);

        // Extract JOIN(from to) and JFLD(fld1 fld2)
        for (DdsKeyword kw : kws) {
            if ("JOIN".equals(kw.getName()) && kw.getValues() != null && kw.getValues().size() >= 2) {
                joinSpec.setFromFile(kw.getValues().get(0));
                joinSpec.setToFile(kw.getValues().get(1));
            } else if ("JFLD".equals(kw.getName()) && kw.getValues() != null && kw.getValues().size() >= 2) {
                joinSpec.getJoinFields().add(
                    new JoinFieldPair(kw.getValues().get(0), kw.getValues().get(1))
                );
            }
        }

        return joinSpec;
    }

    // --- Utilities ---

    private SourceLine buildSourceLine(String line, int lineNum) {
        String seqNum = extractColumn(line, 1, 5);
        boolean isBlank = line.isBlank();
        boolean isComment = line.length() > 6 && line.charAt(5) == 'A' && line.charAt(6) == '*';
        return new SourceLine(lineNum, line, null, seqNum, isComment, isBlank);
    }

    /**
     * Extract TEXT('...') value from keywords for convenience.
     */
    private String extractTextKeyword(List<DdsKeyword> keywords) {
        for (DdsKeyword kw : keywords) {
            if ("TEXT".equals(kw.getName())) {
                return kw.getValue();
            }
        }
        return null;
    }

    /**
     * Populate LfRecordFormat pfile/jfile from keyword analysis.
     */
    private void populateLfFileRef(LfRecordFormat lf, List<DdsKeyword> keywords) {
        for (DdsKeyword kw : keywords) {
            if ("PFILE".equals(kw.getName())) {
                if (kw.getValue() != null) {
                    lf.setPfile(kw.getValue());
                } else if (kw.getValues() != null && !kw.getValues().isEmpty()) {
                    lf.setPfile(kw.getValues().get(0));
                }
            } else if ("JFILE".equals(kw.getName())) {
                if (kw.getValues() != null && !kw.getValues().isEmpty()) {
                    lf.setJfile(kw.getValues());
                } else if (kw.getValue() != null) {
                    List<String> jfiles = new ArrayList<>();
                    jfiles.add(kw.getValue());
                    lf.setJfile(jfiles);
                }
            }
        }
    }

    /**
     * Compute field source type: direct, reference, or derived.
     */
    private String computeFieldSource(FieldDefinition field) {
        if (field.getKeywords() != null) {
            for (DdsKeyword kw : field.getKeywords()) {
                String name = kw.getName();
                if ("CONCAT".equals(name) || "SST".equals(name) || "RENAME".equals(name)) {
                    return "derived";
                }
            }
        }
        if ("R".equals(field.getReference())) {
            return "reference";
        }
        if (field.getKeywords() != null) {
            for (DdsKeyword kw : field.getKeywords()) {
                if ("REFFLD".equals(kw.getName())) {
                    return "reference";
                }
            }
        }
        return "direct";
    }

    /**
     * Populate convenience reference fields on FieldDefinition from REFFLD keyword.
     * Also handles same-name references (R in col29, no REFFLD) — defaults to field's own name.
     */
    private void populateFieldReference(FieldDefinition field) {
        if (field.getKeywords() != null) {
            for (DdsKeyword kw : field.getKeywords()) {
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
     * Detect LF subtype based on keywords and format count.
     */
    private String detectLfSubtype(LfRecordFormat lf, int totalFormats) {
        // Check for JFILE keyword → join
        if (lf.getJfile() != null && !lf.getJfile().isEmpty()) {
            return "join";
        }
        // Check for JOIN keywords in joinSpecs
        if (!lf.getJoinSpecs().isEmpty()) {
            return "join";
        }
        // Multiple record formats → multipleFormat
        if (totalFormats > 1) {
            return "multipleFormat";
        }
        // Default → simple
        return "simple";
    }

    /**
     * Append continuation keywords to the previous element.
     */
    private void appendKeywordsToPrevious(Object element, List<DdsKeyword> kws, String line) {
        if (element instanceof FieldDefinition) {
            FieldDefinition fd = (FieldDefinition) element;
            fd.getKeywords().addAll(kws);
            fd.getRawSourceLines().add(line.stripTrailing());
        } else if (element instanceof KeyDefinition) {
            ((KeyDefinition) element).getKeywords().addAll(kws);
        } else if (element instanceof SelectOmitSpec) {
            ((SelectOmitSpec) element).getKeywords().addAll(kws);
        } else if (element instanceof JoinSpec) {
            JoinSpec js = (JoinSpec) element;
            js.getKeywords().addAll(kws);
            // Also extract JFLD from continuation
            for (DdsKeyword kw : kws) {
                if ("JFLD".equals(kw.getName()) && kw.getValues() != null && kw.getValues().size() >= 2) {
                    js.getJoinFields().add(
                        new JoinFieldPair(kw.getValues().get(0), kw.getValues().get(1))
                    );
                }
            }
        }
    }

    // --- Fix 3: Continuation helpers ---

    /**
     * Process a line's non-keyword data (columns 1-44) without parsing keywords.
     * Used for the first line in a continuation sequence — we extract field columns
     * but defer keyword parsing until all continuation lines are collected.
     */
    private void processLineExceptKeywords(DdsLineType type, String line, int lineNum,
                                            SourceLine sl, boolean isPf,
                                            RecordFormat currentPfFormat, LfRecordFormat currentLfFormat,
                                            boolean seenRecord,
                                            List<DdsKeyword> fileKeywords, List<DdsComment> comments,
                                            List<RecordFormat> pfFormats, List<LfRecordFormat> lfFormats,
                                            Object pendingElement) {
        switch (type) {
            case RECORD_FORMAT: {
                String name = extractColumn(line, 19, 28);
                String condInd = extractColumnNullable(line, 8, 16);
                Location loc = Location.ofLine(lineNum);
                if (isPf) {
                    RecordFormat rf = new RecordFormat();
                    rf.setLocation(loc);
                    rf.setRawSourceLine(line);
                    rf.setConditioningIndicators(condInd);
                    rf.setName(name);
                    rf.setKeywords(new ArrayList<>()); // keywords deferred
                    pfFormats.add(rf);
                } else {
                    LfRecordFormat lrf = new LfRecordFormat();
                    lrf.setLocation(loc);
                    lrf.setRawSourceLine(line);
                    lrf.setConditioningIndicators(condInd);
                    lrf.setName(name);
                    lrf.setKeywords(new ArrayList<>()); // keywords deferred
                    lfFormats.add(lrf);
                }
                break;
            }
            case FIELD_DEFINITION: {
                FieldDefinition field = new FieldDefinition();
                field.setLocation(Location.ofLine(lineNum));
                field.getRawSourceLines().add(line.stripTrailing());
                field.setConditioningIndicators(extractColumnNullable(line, 8, 16));
                field.setName(extractColumn(line, 19, 28));
                field.setReference(charAtOrNull(line, 29));
                field.setLength(parseIntOrNull(extractColumnNullable(line, 30, 34)));
                field.setDataType(charAtOrNull(line, 35));
                field.setDecimalPositions(parseIntOrNull(extractColumnNullable(line, 36, 37)));
                field.setUsage(charAtOrNull(line, 38));
                field.setFieldLocation(extractColumnNullable(line, 39, 44));
                field.setKeywords(new ArrayList<>()); // keywords deferred
                if (isPf && currentPfFormat != null) {
                    currentPfFormat.getFields().add(field);
                } else if (!isPf && currentLfFormat != null) {
                    currentLfFormat.getFields().add(field);
                }
                break;
            }
            case KEY_FIELD: {
                KeyDefinition key = new KeyDefinition();
                key.setLocation(Location.ofLine(lineNum));
                key.setRawSourceLine(line);
                key.setConditioningIndicators(extractColumnNullable(line, 8, 16));
                key.setFieldName(extractColumn(line, 19, 28));
                key.setKeywords(new ArrayList<>()); // keywords deferred
                if (isPf && currentPfFormat != null) {
                    currentPfFormat.getKeys().add(key);
                } else if (!isPf && currentLfFormat != null) {
                    currentLfFormat.getKeys().add(key);
                }
                break;
            }
            case SELECT_FIELD:
            case OMIT_FIELD: {
                SelectOmitSpec spec = new SelectOmitSpec();
                spec.setLocation(Location.ofLine(lineNum));
                spec.setRawSourceLine(line);
                spec.setConditioningIndicators(extractColumnNullable(line, 8, 16));
                spec.setType(type == DdsLineType.SELECT_FIELD ? "select" : "omit");
                spec.setFieldName(extractColumnNullable(line, 19, 28));
                spec.setKeywords(new ArrayList<>()); // keywords deferred
                if (!isPf && currentLfFormat != null) {
                    currentLfFormat.getSelectOmit().add(spec);
                }
                break;
            }
            case JOIN_SPEC: {
                JoinSpec js = new JoinSpec();
                js.setLocation(Location.ofLine(lineNum));
                js.setRawSourceLine(line);
                js.setConditioningIndicators(extractColumnNullable(line, 8, 16));
                js.setKeywords(new ArrayList<>()); // keywords deferred
                if (!isPf && currentLfFormat != null) {
                    currentLfFormat.getJoinSpecs().add(js);
                }
                break;
            }
            case FILE_KEYWORD: {
                // For file-level keywords with continuation, defer to merged parsing
                break;
            }
            default:
                break;
        }
    }

    /**
     * Get the last element added by processLineExceptKeywords, to use as pendingElement.
     */
    private Object getLastElement(DdsLineType type, boolean isPf,
                                   RecordFormat currentPfFormat, LfRecordFormat currentLfFormat,
                                   Object fallback) {
        return switch (type) {
            case RECORD_FORMAT -> isPf ? currentPfFormat : currentLfFormat;
            case FIELD_DEFINITION -> {
                if (isPf && currentPfFormat != null && !currentPfFormat.getFields().isEmpty()) {
                    yield currentPfFormat.getFields().get(currentPfFormat.getFields().size() - 1);
                } else if (!isPf && currentLfFormat != null && !currentLfFormat.getFields().isEmpty()) {
                    yield currentLfFormat.getFields().get(currentLfFormat.getFields().size() - 1);
                }
                yield fallback;
            }
            case KEY_FIELD -> {
                if (isPf && currentPfFormat != null && !currentPfFormat.getKeys().isEmpty()) {
                    yield currentPfFormat.getKeys().get(currentPfFormat.getKeys().size() - 1);
                } else if (!isPf && currentLfFormat != null && !currentLfFormat.getKeys().isEmpty()) {
                    yield currentLfFormat.getKeys().get(currentLfFormat.getKeys().size() - 1);
                }
                yield fallback;
            }
            case SELECT_FIELD, OMIT_FIELD -> {
                if (!isPf && currentLfFormat != null && !currentLfFormat.getSelectOmit().isEmpty()) {
                    yield currentLfFormat.getSelectOmit().get(currentLfFormat.getSelectOmit().size() - 1);
                }
                yield fallback;
            }
            case JOIN_SPEC -> {
                if (!isPf && currentLfFormat != null && !currentLfFormat.getJoinSpecs().isEmpty()) {
                    yield currentLfFormat.getJoinSpecs().get(currentLfFormat.getJoinSpecs().size() - 1);
                }
                yield fallback;
            }
            default -> fallback;
        };
    }

    /**
     * Replace keywords on an element after merged continuation parsing.
     * Sets keywords (replacing empty deferred list) and processes keyword-derived fields.
     */
    private void replaceKeywordsOnElement(Object element, List<DdsKeyword> kws, List<String> continuationLines) {
        if (element instanceof FieldDefinition fd) {
            fd.setKeywords(kws);
            fd.setSource(computeFieldSource(fd));
            // Populate reference convenience fields
            populateFieldReference(fd);
            // Add continuation lines to rawSourceLines
            for (int i = 1; i < continuationLines.size(); i++) {
                fd.getRawSourceLines().add(continuationLines.get(i).stripTrailing());
            }
        } else if (element instanceof RecordFormat rf) {
            rf.setKeywords(kws);
            rf.setText(extractTextKeyword(kws));
        } else if (element instanceof LfRecordFormat lrf) {
            lrf.setKeywords(kws);
            lrf.setText(extractTextKeyword(kws));
            populateLfFileRef(lrf, kws);
        } else if (element instanceof KeyDefinition key) {
            key.setKeywords(kws);
            for (DdsKeyword kw : kws) {
                if ("DESCEND".equals(kw.getName())) {
                    key.setSortOrder("descending");
                    break;
                }
            }
        } else if (element instanceof SelectOmitSpec spec) {
            spec.setKeywords(kws);
            for (DdsKeyword kw : kws) {
                if ("ALL".equals(kw.getName())) {
                    spec.setRule("all");
                    break;
                }
            }
        } else if (element instanceof JoinSpec js) {
            js.setKeywords(kws);
            for (DdsKeyword kw : kws) {
                if ("JOIN".equals(kw.getName()) && kw.getValues() != null && kw.getValues().size() >= 2) {
                    js.setFromFile(kw.getValues().get(0));
                    js.setToFile(kw.getValues().get(1));
                } else if ("JFLD".equals(kw.getName()) && kw.getValues() != null && kw.getValues().size() >= 2) {
                    js.getJoinFields().add(
                        new JoinFieldPair(kw.getValues().get(0), kw.getValues().get(1))
                    );
                }
            }
        }
    }
}
