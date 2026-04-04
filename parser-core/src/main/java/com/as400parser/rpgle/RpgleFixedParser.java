package com.as400parser.rpgle;

import com.as400parser.common.model.Location;
import com.as400parser.common.model.ParseError;
import com.as400parser.rpgle.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Fixed-format RPGLE parser using strict positional column extraction.
 * <p>
 * <b>DOES NOT USE ANTLR.</b> Parses all 7 specification types (H, F, D, I, C, O, P)
 * by extracting character subsets using {@code String.substring()}.
 * <p>
 * Mixed-format support: tracks {@code /FREE} and {@code /END-FREE} block
 * boundaries and collects free-format lines for delegation to {@link RpgleFreeParser}.
 */
public class RpgleFixedParser {

    private final List<ControlSpec> controlSpecs = new ArrayList<>();
    private final List<FileSpec> fileSpecs = new ArrayList<>();
    private final List<DefinitionSpec> definitionSpecs = new ArrayList<>();
    private final List<InputSpec> inputSpecs = new ArrayList<>();
    private final List<CalcSpec> calcSpecs = new ArrayList<>();
    private final List<OutputSpec> outputSpecs = new ArrayList<>();
    private final List<ProcedureSpec> procedureSpecs = new ArrayList<>();
    private final List<String> comments = new ArrayList<>();
    private final List<ParseError> errors = new ArrayList<>();

    // Mixed-format tracking
    private final List<FreeFormatStatement> freeFormatStatements = new ArrayList<>();
    private boolean inFreeBlock = false;
    private final List<String> freeBlockLines = new ArrayList<>();
    private int freeBlockStartLine = -1;

    /**
     * Parse all lines of a fixed/mixed-format RPGLE source.
     *
     * @param lines      normalized source lines
     * @param seqNumbers sequence numbers (parallel array, may be empty)
     * @return populated {@link RpgleContent}
     */
    public RpgleContent parse(List<String> lines, String[] seqNumbers) {
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            int lineNum = i + 1;

            if (line == null || line.isEmpty()) continue;

            // Check for /FREE and /END-FREE directives (mixed-format)
            String trimmed = line.trim().toUpperCase();
            if (trimmed.equals("/FREE")) {
                inFreeBlock = true;
                freeBlockStartLine = lineNum;
                freeBlockLines.clear();
                continue;
            }
            if (trimmed.equals("/END-FREE")) {
                flushFreeBlock();
                inFreeBlock = false;
                continue;
            }

            // If inside a /FREE block, collect for ANTLR delegation
            if (inFreeBlock) {
                freeBlockLines.add(line);
                continue;
            }

            // Fixed-format: need at least 6 characters for form type
            if (line.length() < 6) continue;

            char formType = Character.toUpperCase(line.charAt(5));

            // Check for comment (column 7 = '*')
            if (line.length() > 6 && line.charAt(6) == '*') {
                comments.add(line);
                continue;
            }

            // Compiler directives (column 7 = '/')
            if (line.length() > 6 && line.charAt(6) == '/') {
                // Treat as NoOp for now (per requirements: compiler directives ignored)
                continue;
            }

            try {
                switch (formType) {
                    case 'H' -> parseControlSpec(line, lineNum);
                    case 'F' -> parseFileSpec(line, lineNum);
                    case 'D' -> parseDefinitionSpec(line, lineNum);
                    case 'I' -> parseInputSpec(line, lineNum);
                    case 'C' -> parseCalcSpec(line, lineNum);
                    case 'O' -> parseOutputSpec(line, lineNum);
                    case 'P' -> parseProcedureSpec(line, lineNum);
                    default -> {
                        // Unknown form type — skip silently
                    }
                }
            } catch (Exception e) {
                errors.add(new ParseError(
                        lineNum, 0, e.getMessage(), ParseError.Severity.WARNING));
            }
        }

        // Flush any remaining free block (unterminated /FREE)
        if (inFreeBlock && !freeBlockLines.isEmpty()) {
            flushFreeBlock();
        }

        return buildContent();
    }

    // =========================================================================
    // H-spec: Control Specification
    // =========================================================================

    private void parseControlSpec(String line, int lineNum) {
        ControlSpec spec = new ControlSpec();
        spec.setLocation(new Location(lineNum, lineNum));
        spec.setRawSourceLine(line);
        spec.setKeywords(safeSubstring(line, 6, 80));
        controlSpecs.add(spec);
    }

    // =========================================================================
    // F-spec: File Description Specification
    // =========================================================================

    private void parseFileSpec(String line, int lineNum) {
        FileSpec spec = new FileSpec();
        spec.setLocation(new Location(lineNum, lineNum));
        spec.setRawSourceLine(line);
        spec.setFileName(safeSubstring(line, 6, 16));
        spec.setFileType(safeSubstring(line, 16, 17));
        spec.setFileDesignation(safeSubstring(line, 17, 18));
        spec.setEndOfFile(safeSubstring(line, 18, 19));
        spec.setFileAddition(safeSubstring(line, 19, 20));
        spec.setSequence(safeSubstring(line, 20, 21));
        spec.setFileFormat(safeSubstring(line, 21, 22));
        spec.setRecordLength(safeSubstring(line, 22, 27));
        spec.setLimitsProcessing(safeSubstring(line, 27, 28));
        spec.setKeyLength(safeSubstring(line, 28, 33));
        spec.setRecordAddressType(safeSubstring(line, 33, 34));
        spec.setFileOrganization(safeSubstring(line, 34, 35));
        spec.setDevice(safeSubstring(line, 35, 42));
        spec.setReserved(safeSubstring(line, 42, 43));
        spec.setKeywords(safeSubstring(line, 43, 80));
        fileSpecs.add(spec);
    }

    // =========================================================================
    // D-spec: Definition Specification
    // =========================================================================

    private void parseDefinitionSpec(String line, int lineNum) {
        DefinitionSpec spec = new DefinitionSpec();
        spec.setLocation(new Location(lineNum, lineNum));
        spec.setRawSourceLine(line);
        spec.setName(safeSubstring(line, 6, 21));
        spec.setExternalDescription(safeSubstring(line, 21, 22));
        spec.setDsType(safeSubstring(line, 22, 23));
        spec.setDefinitionType(safeSubstring(line, 23, 25));
        spec.setFromPosition(safeSubstring(line, 25, 32));
        spec.setToPositionLength(safeSubstring(line, 32, 39));
        spec.setInternalDataType(safeSubstring(line, 39, 40));
        spec.setDecimalPositions(safeSubstring(line, 40, 42));
        spec.setKeywords(safeSubstring(line, 43, 80));
        definitionSpecs.add(spec);
    }

    // =========================================================================
    // I-spec: Input Specification
    // =========================================================================

    private void parseInputSpec(String line, int lineNum) {
        InputSpec spec = new InputSpec();
        spec.setLocation(new Location(lineNum, lineNum));
        spec.setRawSourceLine(line);

        // Determine if this is a record or field entry
        String fileNameArea = safeSubstring(line, 6, 16);
        if (fileNameArea != null && !fileNameArea.isBlank()) {
            // Record identification entry
            spec.setSpecSubType("record");
            spec.setFileName(fileNameArea);
            spec.setLogicalRelationship(safeSubstring(line, 15, 18));
            spec.setSequenceNumber(safeSubstring(line, 16, 18));
            spec.setNumber(safeSubstring(line, 18, 19));
            spec.setOption(safeSubstring(line, 19, 20));
            spec.setRecordIdIndicator(safeSubstring(line, 20, 22));
            spec.setRecordIdCodes(safeSubstring(line, 22, 46));
        } else {
            // Field description entry
            spec.setSpecSubType("field");
            spec.setDataAttributes(safeSubstring(line, 30, 34));
            spec.setDateTimeSeparator(safeSubstring(line, 34, 35));
            spec.setDataFormat(safeSubstring(line, 35, 36));
            spec.setFieldLocation(safeSubstring(line, 36, 46));
            spec.setDecimalPositions(safeSubstring(line, 46, 48));
            spec.setFieldName(safeSubstring(line, 48, 62));
            spec.setControlLevel(safeSubstring(line, 62, 64));
            spec.setMatchingFields(safeSubstring(line, 64, 66));
            spec.setFieldRecordRelation(safeSubstring(line, 66, 68));
            spec.setFieldIndicators(safeSubstring(line, 68, 74));
        }
        inputSpecs.add(spec);
    }

    // =========================================================================
    // C-spec: Calculation Specification
    // =========================================================================

    private void parseCalcSpec(String line, int lineNum) {
        CalcSpec spec = new CalcSpec();
        spec.setLocation(new Location(lineNum, lineNum));
        spec.setRawSourceLine(line);
        spec.setControlLevel(safeSubstring(line, 6, 8));
        spec.setConditioningIndicators(safeSubstring(line, 8, 11));
        spec.setFactor1(safeSubstring(line, 11, 25));
        spec.setOperation(safeSubstring(line, 25, 35));

        // Detect extended factor 2: if result field area (50-63) is blank,
        // use columns 36-80 as extended factor 2
        String resultArea = safeSubstring(line, 49, 63);
        if (resultArea == null || resultArea.isBlank()) {
            spec.setExtendedFactor2(true);
            spec.setFactor2(safeSubstring(line, 35, 80));
        } else {
            spec.setExtendedFactor2(false);
            spec.setFactor2(safeSubstring(line, 35, 49));
            spec.setResultField(resultArea);
            spec.setFieldLength(safeSubstring(line, 63, 68));
            spec.setDecimalPositions(safeSubstring(line, 68, 70));
        }

        spec.setHiIndicator(safeSubstring(line, 70, 72));
        spec.setLoIndicator(safeSubstring(line, 72, 74));
        spec.setEqIndicator(safeSubstring(line, 74, 76));
        calcSpecs.add(spec);
    }

    // =========================================================================
    // O-spec: Output Specification
    // =========================================================================

    private void parseOutputSpec(String line, int lineNum) {
        OutputSpec spec = new OutputSpec();
        spec.setLocation(new Location(lineNum, lineNum));
        spec.setRawSourceLine(line);

        String fileNameArea = safeSubstring(line, 6, 16);
        if (fileNameArea != null && !fileNameArea.isBlank()) {
            // Record identification entry
            spec.setSpecSubType("record");
            spec.setFileName(fileNameArea);
            spec.setType(safeSubstring(line, 16, 17));
            spec.setRecordAddDel(safeSubstring(line, 17, 20));
            spec.setConditioningIndicators(safeSubstring(line, 20, 29));
            spec.setExceptName(safeSubstring(line, 29, 39));
            spec.setSpaceSkip(safeSubstring(line, 39, 51));
        } else {
            // Field description entry
            spec.setSpecSubType("field");
            spec.setConditioningIndicators(safeSubstring(line, 20, 29));
            spec.setFieldName(safeSubstring(line, 29, 43));
            spec.setEditCode(safeSubstring(line, 43, 44));
            spec.setBlankAfter(safeSubstring(line, 44, 45));
            spec.setEndPosition(safeSubstring(line, 46, 51));
            spec.setDataFormat(safeSubstring(line, 51, 52));
            spec.setConstantEditWord(safeSubstring(line, 52, 80));
        }
        outputSpecs.add(spec);
    }

    // =========================================================================
    // P-spec: Procedure Specification
    // =========================================================================

    private void parseProcedureSpec(String line, int lineNum) {
        ProcedureSpec spec = new ProcedureSpec();
        spec.setLocation(new Location(lineNum, lineNum));
        spec.setRawSourceLine(line);
        spec.setName(safeSubstring(line, 6, 21));
        spec.setBeginEnd(safeSubstring(line, 23, 24));
        spec.setKeywords(safeSubstring(line, 43, 80));
        procedureSpecs.add(spec);
    }

    // =========================================================================
    // Mixed-format: flush /FREE block to ANTLR
    // =========================================================================

    private void flushFreeBlock() {
        if (freeBlockLines.isEmpty()) return;

        // Delegate to RpgleFreeParser
        RpgleFreeParser freeParser = new RpgleFreeParser();
        List<FreeFormatStatement> stmts = freeParser.parse(freeBlockLines, freeBlockStartLine);
        freeFormatStatements.addAll(stmts);
        errors.addAll(freeParser.getErrors());
        freeBlockLines.clear();
    }

    // =========================================================================
    // Build content model
    // =========================================================================

    private RpgleContent buildContent() {
        RpgleContent content = new RpgleContent();
        content.setFormatType("fixed");
        content.setControlSpecs(controlSpecs);
        content.setFileSpecs(fileSpecs);
        content.setDefinitionSpecs(definitionSpecs);
        content.setInputSpecs(inputSpecs);
        content.setCalcSpecs(calcSpecs);
        content.setOutputSpecs(outputSpecs);
        content.setProcedureSpecs(procedureSpecs);
        content.setFreeFormatStatements(freeFormatStatements);
        content.setComments(comments);
        content.setParseErrors(errors);

        // If we had any free-format statements, it's mixed
        if (!freeFormatStatements.isEmpty()) {
            content.setFormatType("mixed");
        }

        return content;
    }

    // =========================================================================
    // Safe substring — bounds-checked, returns null on out-of-bounds
    // =========================================================================

    static String safeSubstring(String line, int start, int end) {
        if (line == null || start >= line.length()) return null;
        int safeEnd = Math.min(end, line.length());
        String result = line.substring(start, safeEnd);
        return result.isBlank() ? null : result.trim();
    }
}
