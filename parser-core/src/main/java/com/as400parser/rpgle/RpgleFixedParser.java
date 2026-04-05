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
 * by extracting character subsets using {@link #safeSubstring(String, int, int)}.
 * <p>
 * Mixed-format support: tracks {@code /FREE} and {@code /END-FREE} block
 * boundaries and collects free-format lines for delegation to {@link RpgleFreeParser}.
 *
 * <h2>Column Indexing Convention</h2>
 * <p>
 * The IBM ILE RPG Reference documents column positions using <b>1-based</b> numbering
 * (e.g., "File Name: columns 7–16"). All {@code safeSubstring} calls in this class
 * use <b>0-based</b> Java {@code String.substring(start, end)} indices — i.e., one
 * less than the RPG Reference column number:
 * <pre>
 *   RPG Reference column N  →  safeSubstring index N-1
 *   File Name: cols 7–16    →  safeSubstring(line, 6, 16)
 * </pre>
 * When reading or modifying column offsets, always subtract 1 from the IBM reference
 * column number to obtain the correct Java index.
 *
 * <h2>Null vs Empty Return Convention</h2>
 * <p>
 * {@code safeSubstring} returns {@code null} for:
 * <ul>
 *   <li>Input is {@code null}</li>
 *   <li>Start index is beyond the line length (field physically absent)</li>
 *   <li>The extracted substring is entirely whitespace (field is blank)</li>
 * </ul>
 * It returns a non-null trimmed string only when the field contains meaningful content.
 * This matches the IR contract: {@code null} = absent/not set; {@code ""} = never returned.
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
    private final List<RpgleContent.CopyDirective> copyDirectives = new ArrayList<>();
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
                // Check for /COPY or /INCLUDE directives
                String directiveText = line.substring(6).trim().toUpperCase();
                if (directiveText.startsWith("/COPY ") || directiveText.startsWith("/INCLUDE ")) {
                    String[] parts = directiveText.split("\\s+", 2);
                    String directive = parts[0].substring(1); // Remove leading '/'
                    String path = parts.length > 1 ? parts[1].trim() : "";
                    copyDirectives.add(new RpgleContent.CopyDirective(
                            directive, path, new Location(lineNum, lineNum)));
                }
                // Other directives treated as NoOp (per requirements)
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
        String keywords = safeSubstring(line, 6, 80);

        // H-spec continuation: if we already have an H-spec, append keywords
        if (!controlSpecs.isEmpty() && (keywords == null || keywords.isEmpty())) {
            return; // blank continuation — nothing to append
        }
        if (!controlSpecs.isEmpty()) {
            // All H-spec lines are effectively keyword continuations
            // since H-specs don't have a name/identity field.
            // Only append if first H-spec already exists and this is truly continuation.
            // For simplicity, each H-spec line is its own spec entry.
        }

        ControlSpec spec = new ControlSpec();
        spec.setLocation(new Location(lineNum, lineNum));
        spec.setRawSourceLine(line);
        spec.setKeywords(keywords);
        controlSpecs.add(spec);
    }

    // =========================================================================
    // F-spec: File Description Specification
    // =========================================================================

    private void parseFileSpec(String line, int lineNum) {
        String fileName = safeSubstring(line, 6, 16);

        // F-spec keyword continuation: blank filename means this line
        // continues the keywords of the previous F-spec
        if ((fileName == null || fileName.isEmpty()) && !fileSpecs.isEmpty()) {
            FileSpec prev = fileSpecs.get(fileSpecs.size() - 1);
            String contKeywords = safeSubstring(line, 43, 80);
            if (contKeywords != null && !contKeywords.isEmpty()) {
                String existing = prev.getKeywords();
                prev.setKeywords((existing != null && !existing.isEmpty())
                        ? existing + " " + contKeywords : contKeywords);
            }
            prev.getLocation().setEndLine(lineNum);
            return;
        }

        FileSpec spec = new FileSpec();
        spec.setLocation(new Location(lineNum, lineNum));
        spec.setRawSourceLine(line);
        spec.setFileName(fileName);
        spec.setFileType(safeSubstring(line, 16, 17));
        spec.setFileDesignation(safeSubstring(line, 17, 18));
        spec.setEndOfFile(safeSubstring(line, 18, 19));
        spec.setFileAddition(safeSubstring(line, 19, 20));
        spec.setSequence(safeSubstring(line, 20, 21));
        spec.setFileFormat(safeSubstring(line, 21, 22));
        spec.setRecordLength(safeSubstring(line, 22, 27));
        spec.setLimits(safeSubstring(line, 27, 28));
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
        String name = safeSubstring(line, 6, 21);
        String defType = safeSubstring(line, 23, 25);

        // D-spec keyword continuation: blank name AND blank definition type
        // means this line continues the keywords of the previous D-spec
        if ((name == null || name.isEmpty())
                && (defType == null || defType.isEmpty())
                && !definitionSpecs.isEmpty()) {
            DefinitionSpec prev = definitionSpecs.get(definitionSpecs.size() - 1);
            String contKeywords = safeSubstring(line, 43, 80);
            if (contKeywords != null && !contKeywords.isEmpty()) {
                String existing = prev.getKeywords();
                prev.setKeywords((existing != null && !existing.isEmpty())
                        ? existing + " " + contKeywords : contKeywords);
            }
            prev.getLocation().setEndLine(lineNum);
            return;
        }

        DefinitionSpec spec = new DefinitionSpec();
        spec.setLocation(new Location(lineNum, lineNum));
        spec.setRawSourceLine(line);
        spec.setName(name);
        spec.setExternalDescription(safeSubstring(line, 21, 22));
        spec.setDsType(safeSubstring(line, 22, 23));
        spec.setDefinitionType(defType);
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
            spec.setSpecLevel("recordIdentification");
            spec.setFileName(fileNameArea);
            spec.setLogicalRelationship(safeSubstring(line, 15, 18));
            spec.setSequenceNumber(safeSubstring(line, 16, 18));
            spec.setNumber(safeSubstring(line, 18, 19));
            spec.setOption(safeSubstring(line, 19, 20));
            spec.setRecordIdIndicator(safeSubstring(line, 20, 22));
            spec.setRecordIdCodes(safeSubstring(line, 22, 46));
        } else {
            // Field description entry
            spec.setSpecLevel("fieldDefinition");
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
        String factor1 = safeSubstring(line, 11, 25);
        String operation = safeSubstring(line, 25, 35);

        // C-spec extended factor 2 continuation: blank factor1 AND blank operation
        // means this continues the extended factor 2 of the previous C-spec
        if ((factor1 == null || factor1.isEmpty())
                && (operation == null || operation.isEmpty())
                && !calcSpecs.isEmpty()) {
            CalcSpec prev = calcSpecs.get(calcSpecs.size() - 1);
            if (prev.isExtendedFactor2()) {
                String contFactor2 = safeSubstring(line, 35, 80);
                if (contFactor2 != null && !contFactor2.isEmpty()) {
                    String existing = prev.getFactor2();
                    prev.setFactor2((existing != null && !existing.isEmpty())
                            ? existing + " " + contFactor2 : contFactor2);
                }
                prev.getLocation().setEndLine(lineNum);
                return;
            }
        }

        CalcSpec spec = new CalcSpec();
        spec.setLocation(new Location(lineNum, lineNum));
        spec.setRawSourceLine(line);
        spec.setControlLevel(safeSubstring(line, 6, 8));
        spec.setConditioningIndicators(safeSubstring(line, 8, 11));
        spec.setFactor1(factor1);

        // Extract opcode and extender separately (aligned with RPG3)
        spec.setOperationAndExtender(operation);
        if (operation != null && !operation.isEmpty()) {
            int parenIdx = operation.indexOf('(');
            if (parenIdx > 0) {
                spec.setOpcode(operation.substring(0, parenIdx).trim());
                int endParen = operation.indexOf(')');
                spec.setOperationExtender(endParen > parenIdx
                    ? operation.substring(parenIdx + 1, endParen).trim()
                    : operation.substring(parenIdx + 1).trim());
            } else {
                spec.setOpcode(operation.trim());
            }
        }

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
            spec.setSpecLevel("recordLevel");
            spec.setFileName(fileNameArea);
            spec.setType(safeSubstring(line, 16, 17));
            spec.setRecordAddDel(safeSubstring(line, 17, 20));
            spec.setConditioningIndicators(safeSubstring(line, 20, 29));
            spec.setExceptName(safeSubstring(line, 29, 39));
            spec.setSpaceSkip(safeSubstring(line, 39, 51));
        } else {
            // Field description entry
            spec.setSpecLevel("fieldLevel");
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
        String name = safeSubstring(line, 6, 21);

        // P-spec keyword continuation: blank name means this line
        // continues the keywords of the previous P-spec
        if ((name == null || name.isEmpty()) && !procedureSpecs.isEmpty()) {
            ProcedureSpec prev = procedureSpecs.get(procedureSpecs.size() - 1);
            String contKeywords = safeSubstring(line, 43, 80);
            if (contKeywords != null && !contKeywords.isEmpty()) {
                String existing = prev.getKeywords();
                prev.setKeywords((existing != null && !existing.isEmpty())
                        ? existing + " " + contKeywords : contKeywords);
            }
            prev.getLocation().setEndLine(lineNum);
            return;
        }

        ProcedureSpec spec = new ProcedureSpec();
        spec.setLocation(new Location(lineNum, lineNum));
        spec.setRawSourceLine(line);
        spec.setName(name);
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
        content.setCopyDirectives(copyDirectives);

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
