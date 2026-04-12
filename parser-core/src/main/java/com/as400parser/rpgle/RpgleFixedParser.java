package com.as400parser.rpgle;

import com.as400parser.common.model.Location;
import com.as400parser.common.normalizer.NormalizedSource;
import com.as400parser.rpgle.model.*;
import com.as400parser.rpg3.model.ExpressionNode;
import com.as400parser.rpg3.model.IdentifierNode;
import com.as400parser.rpg3.model.LiteralNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Fixed-format RPGLE parser using column-position extraction.
 * <p>
 * Parses lines where col 6 is H/F/D/C/I/O/P using position-based extraction
 * per the rpgle-fixed-doc.json specification. Same approach as Rpg3IrBuilder.
 * <p>
 * Column positions are 1-based in the spec but 0-based in Java substring calls.
 */
public class RpgleFixedParser {

    private final NormalizedSource normalizedSource;
    private final RpgleContent content;
    private final List<String> copyDirectives = new ArrayList<>();

    // Current context for D-spec continuation tracking
    private RpgleContent.DataStructure currentDataStructure;

    public RpgleFixedParser(NormalizedSource normalizedSource, RpgleContent content) {
        this.normalizedSource = normalizedSource;
        this.content = content;
    }

    /**
     * Parse all fixed-format lines from the normalized source.
     * Lines are identified by the character at position 6 (0-based index 5).
     */
    public void parse() {
        String[] lines = normalizedSource.getLines();
        int[] origLineNums = normalizedSource.getOriginalLineNumbers();

        List<CSpecLine> cspecLines = new ArrayList<>();

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.length() < 6) continue;

            int origLine = origLineNums[i];
            char specChar = Character.toUpperCase(line.charAt(5));

            // Comment check: col 7 (0-based index 6) is '*'
            boolean isComment = line.length() > 6 && line.charAt(6) == '*';

            // Full-line comment marker: col 6 is '*'
            if (specChar == '*') {
                scanComment(line, origLine);
                continue;
            }

            // Directive check
            if (specChar == ' ' && line.length() > 6) {
                String directive = sub(line, 6, 80);
                if (directive != null) {
                    String trimmed = directive.trim().toUpperCase();
                    if (trimmed.startsWith("/COPY") || trimmed.startsWith("/INCLUDE")) {
                        handleCopyDirective(line, origLine);
                        continue;
                    }
                    if (trimmed.startsWith("/FREE") || trimmed.startsWith("/END-FREE")
                            || trimmed.startsWith("**FREE") || trimmed.startsWith("/EJECT")
                            || trimmed.startsWith("/TITLE") || trimmed.startsWith("/SPACE")) {
                        // Skip directives — these are handled by the IR builder
                        continue;
                    }
                }
            }

            // Route to spec-specific parser
            if (isComment) {
                scanComment(line, origLine);
                continue;
            }

            switch (specChar) {
                case 'H' -> scanHSpec(line, origLine, i);
                case 'F' -> scanFSpec(line, origLine, i);
                case 'D' -> scanDSpec(line, origLine, i);
                case 'C' -> cspecLines.add(new CSpecLine(line, origLine, i));
                case 'I' -> scanISpec(line, origLine, i);
                case 'O' -> scanOSpec(line, origLine, i);
                case 'P' -> scanPSpec(line, origLine, i);
                default -> { /* skip unknown spec types */ }
            }
        }

        // Process C-spec lines with control flow nesting
        processCSpecLines(cspecLines);
    }

    /**
     * Get copy directives found during parsing.
     */
    public List<String> getCopyDirectives() {
        return copyDirectives;
    }

    // =========================================================================
    // H-spec (Control/Header)
    // RPGLE H-spec: col 6='H', cols 7-80 = keywords
    // =========================================================================

    private void scanHSpec(String line, int origLine, int lineIndex) {
        ControlSpec spec = new ControlSpec();
        spec.setRawSourceLine(line);
        spec.setLocation(Location.ofLine(origLine));
        spec.setSourceSequence(normalizedSource.getSequenceNumbers()[lineIndex]);
        spec.setFormat("fixed");

        // RPGLE H-spec uses cols 7-80 for keywords (unlike RPG3 fixed columns)
        String keywords = sub(line, 6, 80);
        spec.setKeywords(keywords);

        // Parse recognized keywords
        if (keywords != null) {
            parseControlKeywords(spec, keywords);
        }

        // Inline comment from col 81+
        if (line.length() > 80) {
            String comment = line.substring(80).trim();
            if (!comment.isEmpty()) {
                spec.setInlineComment(comment);
            }
        }

        content.getControlSpecs().add(spec);
    }

    private void parseControlKeywords(ControlSpec spec, String keywords) {
        String upper = keywords.toUpperCase();
        if (upper.contains("DATFMT")) {
            spec.setDateFormat(extractKeywordValue(keywords, "DATFMT"));
        }
        if (upper.contains("TIMFMT")) {
            spec.setTimeFormat(extractKeywordValue(keywords, "TIMFMT"));
        }
        if (upper.contains("DATEDIT")) {
            spec.setDateEdit(extractKeywordValue(keywords, "DATEDIT"));
        }
        if (upper.contains("DECEDIT")) {
            spec.setDecimalEdit(extractKeywordValue(keywords, "DECEDIT"));
        }
        if (upper.contains("DEBUG")) {
            spec.setDebug(extractKeywordValue(keywords, "DEBUG"));
        }
        if (upper.contains("OPTION")) {
            spec.setOption(extractKeywordValue(keywords, "OPTION"));
        }
        if (upper.contains("ACTGRP")) {
            spec.setActGroup(extractKeywordValue(keywords, "ACTGRP"));
        }
        if (upper.contains("BNDDIR")) {
            spec.setBndDir(extractKeywordValue(keywords, "BNDDIR"));
        }
        if (upper.contains("DFTACTGRP")) {
            spec.setDftActGrp(extractKeywordValue(keywords, "DFTACTGRP"));
        }
        if (upper.contains("NOMAIN")) {
            spec.setNoMain("*YES");
        }
    }

    // =========================================================================
    // F-spec (File)
    // RPGLE F-spec: cols 7-16 filename, 17 type, 18 designation, 19 endOfFile,
    //   20 addition, 21 sequence, 22 format, 23-27 recLen, 28 limit,
    //   29-33 keyLen, 34 recAddrType, 35 fileOrg, 36-42 device, 43 reserved,
    //   44-80 keywords
    // =========================================================================

    private void scanFSpec(String line, int origLine, int lineIndex) {
        String fileName = sub(line, 6, 16);

        // Continuation line: blank fileName → add keywords to previous spec
        if (fileName == null || fileName.isBlank()) {
            String contKeyword = sub(line, 43, 80);
            if (contKeyword != null && !contKeyword.isBlank() && !content.getFileSpecs().isEmpty()) {
                FileSpec parent = content.getFileSpecs().get(content.getFileSpecs().size() - 1);
                String existing = parent.getKeywords();
                parent.setKeywords((existing != null ? existing + " " : "") + contKeyword.trim());
            }
            return;
        }

        FileSpec spec = new FileSpec();
        spec.setRawSourceLine(line);
        spec.setLocation(Location.ofLine(origLine));
        spec.setSourceSequence(normalizedSource.getSequenceNumbers()[lineIndex]);
        spec.setFormat("fixed");

        spec.setFileName(fileName);                                           // cols 7-16
        spec.setFileType(sub(line, 16, 17));                                  // col 17
        spec.setFileDesignation(sub(line, 17, 18));                           // col 18
        spec.setEndOfFile(sub(line, 18, 19));                                 // col 19
        spec.setFileAddition(sub(line, 19, 20));                              // col 20
        spec.setSequence(sub(line, 20, 21));                                  // col 21
        spec.setFileFormat(sub(line, 21, 22));                                // col 22
        spec.setRecordLength(safeInt(sub(line, 22, 27)));                     // cols 23-27
        spec.setProcessingMode(sub(line, 27, 28));                            // col 28
        spec.setKeyLength(safeInt(sub(line, 28, 33)));                        // cols 29-33
        spec.setRecordAddressType(sub(line, 33, 34));                         // col 34
        spec.setFileOrganization(sub(line, 34, 35));                          // col 35
        spec.setDevice(sub(line, 35, 42));                                    // cols 36-42
        spec.setKeywords(sub(line, 43, 80));                                  // cols 44-80

        // Inline comment from col 81+
        if (line.length() > 80) {
            String comment = line.substring(80).trim();
            if (!comment.isEmpty()) {
                spec.setInlineComment(comment);
            }
        }

        content.getFileSpecs().add(spec);
    }

    // =========================================================================
    // D-spec (Definition)
    // RPGLE D-spec: cols 7-21 name, 22 externally-described, 23 data-area,
    //   24-25 definition type (DS/S/C/PR/PI/blank), 26-32 from, 33-39 to/length,
    //   40 data type, 41-42 decimal positions, 43 reserved, 44-80 keywords
    // =========================================================================

    private void scanDSpec(String line, int origLine, int lineIndex) {
        String name = sub(line, 6, 21);
        String defType = sub(line, 23, 25);

        // Continuation line: blank name and blank definition type
        if ((name == null || name.isBlank()) && (defType == null || defType.isBlank())) {
            String contKeyword = sub(line, 43, 80);
            if (contKeyword != null && !contKeyword.isBlank()) {
                // Append to previous D-spec or DS subfield
                if (!content.getDefinitionSpecs().isEmpty()) {
                    DefinitionSpec parent = content.getDefinitionSpecs().get(
                            content.getDefinitionSpecs().size() - 1);
                    String existing = parent.getKeywords();
                    parent.setKeywords((existing != null ? existing + " " : "") + contKeyword.trim());
                }
            }
            return;
        }

        DefinitionSpec spec = new DefinitionSpec();
        spec.setRawSourceLine(line);
        spec.setLocation(Location.ofLine(origLine));
        spec.setSourceSequence(normalizedSource.getSequenceNumbers()[lineIndex]);
        spec.setFormat("fixed");

        spec.setName(name);
        spec.setExternallyDescribed(sub(line, 21, 22));                       // col 22
        spec.setDataAreaType(sub(line, 22, 23));                              // col 23
        spec.setDefinitionType(defType);                                      // cols 24-25
        spec.setFromPosition(sub(line, 25, 32));                              // cols 26-32
        spec.setToPosition(sub(line, 32, 39));                                // cols 33-39
        spec.setDataType(sub(line, 39, 40));                                  // col 40
        spec.setDecimalPositions(safeInt(sub(line, 40, 42)));                 // cols 41-42
        spec.setKeywords(sub(line, 43, 80));                                  // cols 44-80

        // Parse keywords for specific values
        String keywords = spec.getKeywords();
        if (keywords != null) {
            spec.setInzValue(extractKeywordValue(keywords, "INZ"));
            spec.setLikeField(extractKeywordValue(keywords, "LIKE"));
            spec.setDimValue(extractKeywordValue(keywords, "DIM"));
            spec.setExtName(extractKeywordValue(keywords, "EXTNAME"));
        }

        // Named constant value (type = 'C')
        if ("C".equals(defType) && keywords != null) {
            spec.setConstValue(extractConstantValue(keywords));
        }

        // Inline comment from col 81+
        if (line.length() > 80) {
            String comment = line.substring(80).trim();
            if (!comment.isEmpty()) {
                spec.setInlineComment(comment);
            }
        }

        content.getDefinitionSpecs().add(spec);

        // Track data structure context
        if ("DS".equals(defType)) {
            RpgleContent.DataStructure ds = new RpgleContent.DataStructure();
            ds.setName(name);
            ds.setRawSourceLine(line);
            ds.setLocation(Location.ofLine(origLine));
            ds.setSourceSequence(normalizedSource.getSequenceNumbers()[lineIndex]);
            ds.setType("dataStructure");
            ds.setKeywords(keywords);
            ds.setInlineComment(spec.getInlineComment());
            content.getDataStructures().add(ds);
            currentDataStructure = ds;
        } else if (defType != null && !defType.isBlank() && !"DS".equals(defType)) {
            // End DS context for non-subfield definition types
            if (!"".equals(defType.trim())) {
                currentDataStructure = null;
            }
        } else if ((defType == null || defType.isBlank()) && currentDataStructure != null) {
            // Subfield of current data structure
            currentDataStructure.getSubfields().add(spec);
        }
    }

    // =========================================================================
    // C-spec (Calculation)
    // RPGLE C-spec: cols 7-8 control level, 9-11 conditioning indicators,
    //   12-25 factor 1, 26-35 opcode + extender,
    //   36-49 factor 2 (traditional) or 36-80 extended factor 2
    //   50-63 result, 64-68 field length, 69-70 decimal positions,
    //   71-76 resulting indicators (HI/LO/EQ)
    // =========================================================================

    private record CSpecLine(String rawLine, int origLine, int arrayIndex) {}

    private void processCSpecLines(List<CSpecLine> lines) {
        processCalcBlock(lines, 0, lines.size(), content.getCalculationSpecs());
    }

    private int processCalcBlock(List<CSpecLine> lines, int start, int end, List<Object> target) {
        int i = start;
        while (i < end) {
            CSpecLine cline = lines.get(i);
            String opcode = extractOpcode(cline.rawLine);
            String upper = opcode.toUpperCase();

            if (upper.startsWith("IF")) {
                i = processIfBlock(lines, i, end, target);
            } else if (upper.equals("DO")) {
                i = processLoopBlock(lines, i, end, target, "doBlock");
            } else if (upper.startsWith("DOW")) {
                i = processLoopBlock(lines, i, end, target, "doWhileBlock");
            } else if (upper.startsWith("DOU")) {
                i = processLoopBlock(lines, i, end, target, "doUntilBlock");
            } else if (upper.equals("FOR")) {
                i = processForBlock(lines, i, end, target);
            } else if (upper.equals("SELECT")) {
                i = processSelectBlock(lines, i, end, target);
            } else if (upper.equals("MONITOR")) {
                i = processMonitorBlock(lines, i, end, target);
            } else if (upper.equals("BEGSR")) {
                i = processSubroutine(lines, i, end, target);
            } else {
                target.add(buildOperation(cline));
                i++;
            }
        }
        return i;
    }

    private CalcSpec.Operation buildOperation(CSpecLine cline) {
        String line = cline.rawLine;
        CalcSpec.Operation op = new CalcSpec.Operation();
        op.setRawSourceLine(line);
        op.setLocation(Location.ofLine(cline.origLine));
        op.setSourceSequence(normalizedSource.getSequenceNumbers()[cline.arrayIndex]);
        op.setFormat("fixed");

        op.setControlLevel(sub(line, 6, 8));                                  // cols 7-8

        // Conditioning indicators (cols 9-11)
        parseConditioningIndicators(line, op);

        // Factor 1 (cols 12-25)
        String f1 = sub(line, 11, 25);
        if (f1 != null && !f1.isEmpty()) {
            op.setFactor1(new IdentifierNode(f1));
        }

        // Opcode + extender (cols 26-35)
        String opcodeRaw = sub(line, 25, 35);
        if (opcodeRaw != null) {
            int parenStart = opcodeRaw.indexOf('(');
            if (parenStart > 0) {
                op.setOpcode(opcodeRaw.substring(0, parenStart).trim());
                int parenEnd = opcodeRaw.indexOf(')');
                if (parenEnd > parenStart) {
                    op.setExtendedOpcode(opcodeRaw.substring(parenStart + 1, parenEnd));
                }
            } else {
                op.setOpcode(opcodeRaw.trim());
            }
        }

        // Check for extended factor 2 (EVAL, IF, DOW, CALLP, etc. use cols 36-80)
        String upperOp = op.getOpcode() != null ? op.getOpcode().toUpperCase() : "";
        boolean isExtendedF2 = isExtendedFactor2Opcode(upperOp);

        if (isExtendedF2) {
            // Extended factor 2: cols 36-80
            String extF2 = sub(line, 35, 80);
            op.setExtendedFactor2(extF2);
        } else {
            // Traditional factor 2 (cols 36-49)
            String f2 = sub(line, 35, 49);
            if (f2 != null && !f2.isEmpty()) {
                op.setFactor2(new IdentifierNode(f2));
            }

            // Result field (cols 50-63)
            String result = sub(line, 49, 63);
            if (result != null && !result.isEmpty()) {
                op.setResultField(new IdentifierNode(result));
            }

            // Field length (cols 64-68)
            op.setFieldLength(safeInt(sub(line, 63, 68)));

            // Decimal positions (cols 69-70)
            op.setDecimalPositions(safeInt(sub(line, 68, 70)));

            // Resulting indicators (cols 71-76)
            String hiInd = sub(line, 70, 72);
            String loInd = sub(line, 72, 74);
            String eqInd = sub(line, 74, 76);
            if ((hiInd != null && !hiInd.isEmpty()) ||
                (loInd != null && !loInd.isEmpty()) ||
                (eqInd != null && !eqInd.isEmpty())) {
                CalcSpec.ResultingIndicators ri = new CalcSpec.ResultingIndicators();
                if (hiInd != null && !hiInd.isEmpty()) {
                    ri.setHigh(new CalcSpec.IndicatorRef(hiInd, classifyIndicator(hiInd)));
                }
                if (loInd != null && !loInd.isEmpty()) {
                    ri.setLow(new CalcSpec.IndicatorRef(loInd, classifyIndicator(loInd)));
                }
                if (eqInd != null && !eqInd.isEmpty()) {
                    ri.setEqual(new CalcSpec.IndicatorRef(eqInd, classifyIndicator(eqInd)));
                }
                op.setResultingIndicators(ri);
            }
        }

        // Inline comment from col 81+
        if (line.length() > 80) {
            String comment = line.substring(80).trim();
            if (!comment.isEmpty()) {
                op.setInlineComment(comment);
            }
        }

        return op;
    }

    private boolean isExtendedFactor2Opcode(String opcode) {
        return switch (opcode) {
            case "EVAL", "EVALR", "EVAL-CORR", "IF", "ELSEIF", "DOW", "DOU",
                 "FOR", "WHEN", "CALLP", "ON-ERROR", "RETURN", "DATA-INTO",
                 "XML-INTO", "XML-SAX", "SORTA" -> true;
            default -> false;
        };
    }

    // ---- Control flow blocks ----

    private int processIfBlock(List<CSpecLine> lines, int i, int end, List<Object> target) {
        CalcSpec.ConditionalBlock block = new CalcSpec.ConditionalBlock();
        CSpecLine ifLine = lines.get(i);
        CalcSpec.Operation ifOp = buildOperation(ifLine);
        block.setRawSourceLine(ifLine.rawLine);
        block.setLocation(Location.ofLine(ifLine.origLine));
        block.setFormat("fixed");

        String opcode = ifOp.getOpcode() != null ? ifOp.getOpcode().toUpperCase() : "";
        if (opcode.length() > 2) {
            block.setComparisonType(opcode.substring(2));
            block.setCondition(ifOp.getFactor1());
            block.setComparisonValue(ifOp.getFactor2());
        } else {
            block.setFreeExpression(ifOp.getExtendedFactor2());
        }

        i++;
        while (i < end) {
            String op = extractOpcode(lines.get(i).rawLine).toUpperCase();
            if (op.equals("ELSE")) {
                i++;
                while (i < end) {
                    String innerOp = extractOpcode(lines.get(i).rawLine).toUpperCase();
                    if (innerOp.equals("ENDIF") || innerOp.equals("END")) {
                        block.setEndStatement(buildOperation(lines.get(i)));
                        i++;
                        target.add(block);
                        return i;
                    }
                    i = processCalcBlock(lines, i, i + 1, block.getElseOps());
                }
            } else if (op.startsWith("ELSEIF")) {
                // Nest the ELSEIF as a new ConditionalBlock in the else branch
                i = processIfBlock(lines, i, end, block.getElseOps());
                target.add(block);
                return i;
            } else if (op.equals("ENDIF") || op.equals("END")) {
                block.setEndStatement(buildOperation(lines.get(i)));
                i++;
                target.add(block);
                return i;
            } else {
                i = processCalcBlock(lines, i, i + 1, block.getThenOps());
            }
        }

        target.add(block);
        return i;
    }

    private int processLoopBlock(List<CSpecLine> lines, int i, int end, List<Object> target, String blockType) {
        CalcSpec.CalcNode block;
        CSpecLine startLine = lines.get(i);
        CalcSpec.Operation startOp = buildOperation(startLine);

        if ("doWhileBlock".equals(blockType)) {
            CalcSpec.DoWhileBlock dwb = new CalcSpec.DoWhileBlock();
            dwb.setRawSourceLine(startLine.rawLine);
            dwb.setLocation(Location.ofLine(startLine.origLine));
            dwb.setFormat("fixed");
            String opcode = startOp.getOpcode() != null ? startOp.getOpcode().toUpperCase() : "";
            if (opcode.length() > 3) {
                dwb.setComparisonType(opcode.substring(3));
                dwb.setCondition(startOp.getFactor1());
                dwb.setComparisonValue(startOp.getFactor2());
            } else {
                dwb.setFreeExpression(startOp.getExtendedFactor2());
            }
            block = dwb;
            i++;
            while (i < end) {
                String op = extractOpcode(lines.get(i).rawLine).toUpperCase();
                if (op.equals("ENDDO") || op.equals("END")) {
                    dwb.setEndStatement(buildOperation(lines.get(i)));
                    i++;
                    break;
                }
                i = processCalcBlock(lines, i, i + 1, dwb.getBodyOps());
            }
        } else if ("doUntilBlock".equals(blockType)) {
            CalcSpec.DoUntilBlock dub = new CalcSpec.DoUntilBlock();
            dub.setRawSourceLine(startLine.rawLine);
            dub.setLocation(Location.ofLine(startLine.origLine));
            dub.setFormat("fixed");
            String opcode = startOp.getOpcode() != null ? startOp.getOpcode().toUpperCase() : "";
            if (opcode.length() > 3) {
                dub.setComparisonType(opcode.substring(3));
                dub.setCondition(startOp.getFactor1());
                dub.setComparisonValue(startOp.getFactor2());
            } else {
                dub.setFreeExpression(startOp.getExtendedFactor2());
            }
            block = dub;
            i++;
            while (i < end) {
                String op = extractOpcode(lines.get(i).rawLine).toUpperCase();
                if (op.equals("ENDDO") || op.equals("END")) {
                    dub.setEndStatement(buildOperation(lines.get(i)));
                    i++;
                    break;
                }
                i = processCalcBlock(lines, i, i + 1, dub.getBodyOps());
            }
        } else {
            // Plain DO block
            CalcSpec.DoWhileBlock dob = new CalcSpec.DoWhileBlock();
            dob.setRawSourceLine(startLine.rawLine);
            dob.setLocation(Location.ofLine(startLine.origLine));
            dob.setFormat("fixed");
            block = dob;
            i++;
            while (i < end) {
                String op = extractOpcode(lines.get(i).rawLine).toUpperCase();
                if (op.equals("ENDDO") || op.equals("END")) {
                    dob.setEndStatement(buildOperation(lines.get(i)));
                    i++;
                    break;
                }
                i = processCalcBlock(lines, i, i + 1, dob.getBodyOps());
            }
        }

        target.add(block);
        return i;
    }

    private int processForBlock(List<CSpecLine> lines, int i, int end, List<Object> target) {
        CalcSpec.ForBlock block = new CalcSpec.ForBlock();
        CSpecLine startLine = lines.get(i);
        block.setRawSourceLine(startLine.rawLine);
        block.setLocation(Location.ofLine(startLine.origLine));
        block.setFormat("fixed");

        CalcSpec.Operation startOp = buildOperation(startLine);
        // FOR uses extended factor 2 for the full expression
        String extF2 = startOp.getExtendedFactor2();
        if (extF2 != null) {
            block.setStartExpression(extF2);
        }

        i++;
        while (i < end) {
            String op = extractOpcode(lines.get(i).rawLine).toUpperCase();
            if (op.equals("ENDFOR") || op.equals("END")) {
                block.setEndStatement(buildOperation(lines.get(i)));
                i++;
                break;
            }
            i = processCalcBlock(lines, i, i + 1, block.getBodyOps());
        }

        target.add(block);
        return i;
    }

    private int processSelectBlock(List<CSpecLine> lines, int i, int end, List<Object> target) {
        CalcSpec.SelectBlock block = new CalcSpec.SelectBlock();
        CSpecLine startLine = lines.get(i);
        block.setRawSourceLine(startLine.rawLine);
        block.setLocation(Location.ofLine(startLine.origLine));
        block.setFormat("fixed");

        i++;
        CalcSpec.WhenClause currentWhen = null;

        while (i < end) {
            String op = extractOpcode(lines.get(i).rawLine).toUpperCase();
            if (op.equals("ENDSL") || op.equals("END")) {
                block.setEndStatement(buildOperation(lines.get(i)));
                i++;
                break;
            } else if (op.startsWith("WHEN")) {
                currentWhen = new CalcSpec.WhenClause();
                currentWhen.setLocation(Location.ofLine(lines.get(i).origLine));
                CalcSpec.Operation whenOp = buildOperation(lines.get(i));
                if (op.length() > 4) {
                    currentWhen.setComparisonType(op.substring(4));
                    currentWhen.setCondition(whenOp.getFactor1());
                    currentWhen.setComparisonValue(whenOp.getFactor2());
                } else {
                    currentWhen.setFreeExpression(whenOp.getExtendedFactor2());
                }
                block.getWhenClauses().add(currentWhen);
                i++;
            } else if (op.equals("OTHER")) {
                i++;
                while (i < end) {
                    String innerOp = extractOpcode(lines.get(i).rawLine).toUpperCase();
                    if (innerOp.equals("ENDSL") || innerOp.equals("END")) {
                        block.setEndStatement(buildOperation(lines.get(i)));
                        i++;
                        target.add(block);
                        return i;
                    }
                    i = processCalcBlock(lines, i, i + 1, block.getOtherOps());
                }
            } else if (currentWhen != null) {
                i = processCalcBlock(lines, i, i + 1, currentWhen.getBodyOps());
            } else {
                i++;
            }
        }

        target.add(block);
        return i;
    }

    private int processMonitorBlock(List<CSpecLine> lines, int i, int end, List<Object> target) {
        CalcSpec.MonitorBlock block = new CalcSpec.MonitorBlock();
        CSpecLine startLine = lines.get(i);
        block.setRawSourceLine(startLine.rawLine);
        block.setLocation(Location.ofLine(startLine.origLine));
        block.setFormat("fixed");

        i++;
        CalcSpec.OnErrorClause currentOnError = null;

        while (i < end) {
            String op = extractOpcode(lines.get(i).rawLine).toUpperCase();
            if (op.equals("ENDMON") || op.equals("END")) {
                block.setEndStatement(buildOperation(lines.get(i)));
                i++;
                break;
            } else if (op.equals("ON-ERROR")) {
                currentOnError = new CalcSpec.OnErrorClause();
                currentOnError.setLocation(Location.ofLine(lines.get(i).origLine));
                CalcSpec.Operation onErrOp = buildOperation(lines.get(i));
                currentOnError.setErrorCodes(onErrOp.getExtendedFactor2());
                block.getOnErrorClauses().add(currentOnError);
                i++;
            } else if (currentOnError != null) {
                i = processCalcBlock(lines, i, i + 1, currentOnError.getBodyOps());
            } else {
                i = processCalcBlock(lines, i, i + 1, block.getBodyOps());
            }
        }

        target.add(block);
        return i;
    }

    private int processSubroutine(List<CSpecLine> lines, int i, int end, List<Object> target) {
        CalcSpec.SubroutineBlock block = new CalcSpec.SubroutineBlock();
        CSpecLine startLine = lines.get(i);
        block.setRawSourceLine(startLine.rawLine);
        block.setLocation(Location.ofLine(startLine.origLine));
        block.setFormat("fixed");

        CalcSpec.Operation begsr = buildOperation(startLine);
        ExpressionNode f1 = begsr.getFactor1();
        if (f1 instanceof IdentifierNode id) {
            block.setSubroutineName(id.getName());
        }

        i++;
        while (i < end) {
            String op = extractOpcode(lines.get(i).rawLine).toUpperCase();
            if (op.equals("ENDSR")) {
                block.setEndStatement(buildOperation(lines.get(i)));
                i++;
                break;
            }
            i = processCalcBlock(lines, i, i + 1, block.getOperations());
        }

        target.add(block);
        content.getSubroutines().add(block);
        return i;
    }

    // ---- Conditioning indicators ----

    private void parseConditioningIndicators(String line, CalcSpec.Operation op) {
        String indArea = sub(line, 8, 11);
        if (indArea != null && !indArea.isBlank()) {
            // Simple 2-char indicator
            String ind = indArea.trim();
            if (!ind.isEmpty()) {
                boolean negated = ind.startsWith("N");
                String indName = negated ? ind.substring(1) : ind;
                if (!indName.isEmpty()) {
                    op.getConditioningIndicators().add(
                            new CalcSpec.ConditioningIndicator(negated, indName, "first"));
                }
            }
        }
    }

    // =========================================================================
    // I-spec (Input)
    // =========================================================================

    private void scanISpec(String line, int origLine, int lineIndex) {
        InputSpec spec = new InputSpec();
        spec.setRawSourceLine(line);
        spec.setLocation(Location.ofLine(origLine));
        spec.setSourceSequence(normalizedSource.getSequenceNumbers()[lineIndex]);

        String identifier = sub(line, 6, 22);
        if (identifier != null && !identifier.isBlank()) {
            spec.setSpecLevel("recordIdentification");
            spec.setFileName(identifier.trim());
        } else {
            spec.setSpecLevel("fieldDefinition");
            String fieldName = sub(line, 52, 58);
            spec.setFieldName(fieldName != null ? fieldName.trim() : null);
            spec.setFromPosition(safeInt(sub(line, 43, 47)));
            spec.setToPosition(safeInt(sub(line, 47, 51)));
            spec.setDecimalPositions(safeInt(sub(line, 51, 52)));
            spec.setDataFormat(sub(line, 42, 43));
        }

        content.getInputSpecs().add(spec);
    }

    // =========================================================================
    // O-spec (Output)
    // =========================================================================

    private void scanOSpec(String line, int origLine, int lineIndex) {
        OutputSpec spec = new OutputSpec();
        spec.setRawSourceLine(line);
        spec.setLocation(Location.ofLine(origLine));
        spec.setSourceSequence(normalizedSource.getSequenceNumbers()[lineIndex]);

        String recordName = sub(line, 6, 14);
        if (recordName != null && !recordName.isBlank()) {
            spec.setSpecLevel("recordLevel");
            spec.setFileName(recordName.trim());
            spec.setType(sub(line, 14, 15));
            spec.setSpaceBefore(sub(line, 16, 17));
            spec.setSpaceAfter(sub(line, 17, 18));
            spec.setSkipBefore(sub(line, 18, 20));
            spec.setSkipAfter(sub(line, 20, 22));
            spec.setOutputIndicators(sub(line, 22, 31));
            spec.setExceptName(sub(line, 31, 37));
        } else {
            spec.setSpecLevel("fieldLevel");
            spec.setOutputIndicators(sub(line, 22, 31));
            spec.setFieldName(sub(line, 31, 37));
            spec.setEditCode(sub(line, 37, 38));
            spec.setBlankAfter(sub(line, 38, 39));
            spec.setEndPosition(safeInt(sub(line, 39, 43)));
            spec.setDataFormat(sub(line, 43, 44));
            spec.setConstantOrEditWord(sub(line, 44, 70));
        }

        content.getOutputSpecs().add(spec);
    }

    // =========================================================================
    // P-spec (Procedure)
    // RPGLE P-spec: cols 7-21 name, col 24 begin/end (B/E), cols 44-80 keywords
    // =========================================================================

    private void scanPSpec(String line, int origLine, int lineIndex) {
        ProcedureSpec spec = new ProcedureSpec();
        spec.setRawSourceLine(line);
        spec.setLocation(Location.ofLine(origLine));
        spec.setSourceSequence(normalizedSource.getSequenceNumbers()[lineIndex]);
        spec.setFormat("fixed");

        spec.setProcedureName(sub(line, 6, 21));                              // cols 7-21
        spec.setBeginEnd(sub(line, 23, 24));                                  // col 24
        spec.setKeywords(sub(line, 43, 80));                                  // cols 44-80

        // Inline comment from col 81+
        if (line.length() > 80) {
            String comment = line.substring(80).trim();
            if (!comment.isEmpty()) {
                spec.setInlineComment(comment);
            }
        }

        content.getProcedureSpecs().add(spec);
    }

    // =========================================================================
    // Comments
    // =========================================================================

    private void scanComment(String line, int origLine) {
        RpgleContent.Comment comment = new RpgleContent.Comment();
        comment.setLineNumber(origLine);
        comment.setRawSourceLine(line);
        comment.setLocation(Location.ofLine(origLine));

        // Extract comment text
        if (line.length() > 7) {
            comment.setText(line.substring(7).trim());
        } else if (line.length() > 6) {
            comment.setText("");
        }

        // Set spec context
        if (line.length() > 5) {
            char specC = Character.toUpperCase(line.charAt(5));
            switch (specC) {
                case 'H' -> comment.setSpecContext("H");
                case 'F' -> comment.setSpecContext("F");
                case 'D' -> comment.setSpecContext("D");
                case 'C' -> comment.setSpecContext("C");
                case 'I' -> comment.setSpecContext("I");
                case 'O' -> comment.setSpecContext("O");
                case 'P' -> comment.setSpecContext("P");
                case '*' -> comment.setSpecContext("*");
                default -> comment.setSpecContext(null);
            }
        }

        content.getComments().add(comment);
    }

    // =========================================================================
    // /COPY and /INCLUDE directives
    // =========================================================================

    private void handleCopyDirective(String line, int origLine) {
        String fullDirective = sub(line, 6, 80);
        if (fullDirective == null) return;
        copyDirectives.add(fullDirective.trim());
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private String extractOpcode(String line) {
        String opcode = sub(line, 25, 35);
        if (opcode == null) return "";
        // Strip extender: EVAL(H) -> EVAL
        int paren = opcode.indexOf('(');
        return paren > 0 ? opcode.substring(0, paren).trim() : opcode.trim();
    }

    private String extractKeywordValue(String keywordArea, String keyword) {
        String upper = keywordArea.toUpperCase();
        int idx = upper.indexOf(keyword.toUpperCase());
        if (idx < 0) return null;

        int parenStart = keywordArea.indexOf('(', idx);
        if (parenStart < 0) return "";

        int depth = 1;
        int parenEnd = parenStart + 1;
        while (parenEnd < keywordArea.length() && depth > 0) {
            if (keywordArea.charAt(parenEnd) == '(') depth++;
            if (keywordArea.charAt(parenEnd) == ')') depth--;
            parenEnd++;
        }
        return keywordArea.substring(parenStart + 1, parenEnd - 1).trim();
    }

    private String extractConstantValue(String keywords) {
        // For named constants, the value is typically CONST('value') or just 'value'
        if (keywords.contains("CONST")) {
            return extractKeywordValue(keywords, "CONST");
        }
        // Otherwise the keyword area itself might be the constant value
        String trimmed = keywords.trim();
        if (trimmed.startsWith("'") && trimmed.endsWith("'")) {
            return trimmed.substring(1, trimmed.length() - 1);
        }
        return trimmed;
    }

    /** Safe substring extraction (0-based begin/end). Returns trimmed or null. */
    private String sub(String line, int begin, int end) {
        if (line == null || line.length() < end) {
            if (line != null && line.length() > begin) {
                return line.substring(begin).trim();
            }
            return null;
        }
        return line.substring(begin, end).trim();
    }

    private Integer safeInt(String text) {
        if (text == null || text.isBlank()) return null;
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String classifyIndicator(String name) {
        if (name == null || name.isEmpty()) return "unknown";
        String upper = name.toUpperCase();
        if (upper.matches("\\d{2}")) return "numeric";
        if (upper.matches("LR|MR|RT|1P|K[A-N]|U[1-8]")) return "special";
        if (upper.matches("H[1-9]")) return "halt";
        if (upper.matches("L[0-9]")) return "level";
        if (upper.matches("O[A-GV]")) return "overflow";
        return "unknown";
    }
}
