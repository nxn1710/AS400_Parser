package com.as400parser.rpg3;

import com.as400parser.common.model.*;
import com.as400parser.common.normalizer.NormalizedSource;
import com.as400parser.rpg3.model.*;
import com.as400parser.rpg3.model.CalcSpec.*;
import com.as400parser.rpg3.model.Rpg3Content.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Builds the RPG3 IR model from normalized source lines using
 * <b>raw-line column extraction</b> exclusively.
 * <p>
 * The ANTLR parse tree is NOT used because the grammar's catch-all rules
 * consume entire line content before positional tokens can fire.
 * Instead, this builder scans lines directly, identifying spec types from
 * column 6 and extracting fields by column position per the RPG III Reference.
 * <p>
 * C-spec control flow (IF/DO/DOW/DOU/CAS/BEGSR blocks) is detected by
 * opcode matching and a simple block-nesting state machine.
 */
public class Rpg3IrBuilder {

    private final NormalizedSource normalizedSource;
    private final IrDocument document;
    private final Rpg3Content content;
    private final IrDocument.Dependencies dependencies;

    // Track subroutine EXSR calls for cross-reference
    private final Map<String, List<Location>> exsrCalls = new LinkedHashMap<>();

    public Rpg3IrBuilder(NormalizedSource normalizedSource) {
        this.normalizedSource = normalizedSource;
        this.document = new IrDocument();
        this.content = new Rpg3Content();
        this.dependencies = new IrDocument.Dependencies();
        document.setContent(content);
        document.setDependencies(dependencies);
    }

    // =========================================================================
    // Public API — replaces ANTLR visitor pattern
    // =========================================================================

    /**
     * Build the IR from normalized source. Call this instead of visit().
     */
    public IrDocument build() {
        scanAllLines();
        buildSourceLines();
        populateSubroutineCalledFrom();
        return document;
    }

    /**
     * Legacy compatibility — still called by Rpg3ParserFacade via visitor.visit(tree).
     * Delegates to build() so the parse tree is effectively ignored.
     */
    public Void visit(Object tree) {
        build();
        return null;
    }

    /** Get the built IR document. Legacy accessor. */
    public IrDocument getResult() {
        return document;
    }

    // =========================================================================
    // Main line scanner
    // =========================================================================

    private void scanAllLines() {
        String[] lines = normalizedSource.getLines();
        int[] origLineNums = normalizedSource.getOriginalLineNumbers();

        // First pass: collect all C-spec lines for control flow processing
        List<CSpecLine> cspecLines = new ArrayList<>();

        // Collect E-spec array names that use compile-time data (no fromFileName)
        List<String> ctdArrayNames = new ArrayList<>();

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.length() < 6) continue;

            int origLine = origLineNums[i];

            // Check for compile-time data separator: ** at columns 1-2
            // (normalizer extracts cols 1-5 into sequenceNumbers, so ** is there)
            String seqNum = normalizedSource.getSequenceNumbers()[i];
            if (seqNum != null && seqNum.startsWith("**")) {
                // Enter compile-time data mode — process all remaining lines
                processCompileTimeData(lines, origLineNums, i, ctdArrayNames);
                break; // no more spec lines after **
            }

            char specChar = Character.toUpperCase(line.charAt(5));

            // Inline comment check (column 7 = '*')
            boolean isInlineComment = line.length() > 6 && line.charAt(6) == '*';

            switch (specChar) {
                case 'H' -> {
                    if (isInlineComment) scanComment(line, origLine);
                    else scanHeaderSpec(line, origLine);
                }
                case 'F' -> {
                    if (isInlineComment) scanComment(line, origLine);
                    else scanFileSpec(line, origLine);
                }
                case 'E' -> {
                    if (isInlineComment) scanComment(line, origLine);
                    else {
                        scanExtensionSpec(line, origLine);
                        // Track array names for compile-time data association
                        String fromFile = sub(line, 10, 18);
                        if (fromFile == null || fromFile.isBlank()) {
                            String arrName = sub(line, 26, 32);
                            if (arrName != null && !arrName.isBlank()) {
                                ctdArrayNames.add(arrName.trim());
                            }
                        }
                    }
                }
                case 'L' -> scanLineCounterSpec(line, origLine);
                case 'I' -> {
                    if (isInlineComment) scanComment(line, origLine);
                    else scanInputSpec(line, origLine);
                }
                case 'C' -> {
                    if (isInlineComment) {
                        scanComment(line, origLine);
                    } else {
                        cspecLines.add(new CSpecLine(line, origLine, i));
                    }
                }
                case 'O' -> {
                    if (isInlineComment) scanComment(line, origLine);
                    else scanOutputSpec(line, origLine);
                }
                case '*' -> scanComment(line, origLine);
                default -> { /* skip non-spec lines (blank, directives, etc.) */ }
            }
        }

        // Process C-spec lines with control-flow nesting
        processCSpecLines(cspecLines);
    }

    private void processCompileTimeData(String[] lines, int[] origLineNums, int separatorIdx, List<String> ctdArrayNames) {
        CompileTimeData ctd = new CompileTimeData();
        List<CompileTimeBlock> blocks = new ArrayList<>();
        CompileTimeBlock currentBlock = null;
        int arrayIdx = 0;

        for (int i = separatorIdx; i < lines.length; i++) {
            String line = lines[i];
            String seqNum = normalizedSource.getSequenceNumbers()[i];
            int origLine = origLineNums[i];

            if (seqNum != null && seqNum.startsWith("**")) {
                // Start a new block
                if (currentBlock != null) {
                    blocks.add(currentBlock);
                }
                currentBlock = new CompileTimeBlock();
                currentBlock.setLocation(Location.ofLine(origLine));
                // Associate with E-spec array name by order
                if (arrayIdx < ctdArrayNames.size()) {
                    currentBlock.setArrayName(ctdArrayNames.get(arrayIdx));
                }
                arrayIdx++;
            } else if (currentBlock != null) {
                // Add data line to current block
                currentBlock.getData().add(line);
            }
        }

        // Don't forget the last block
        if (currentBlock != null) {
            blocks.add(currentBlock);
        }

        if (!blocks.isEmpty()) {
            ctd.setBlocks(blocks);
            content.setCompileTimeData(ctd);
        }
    }

    // =========================================================================
    // H-spec
    // =========================================================================

    private void scanHeaderSpec(String line, int origLine) {
        HeaderSpec spec = new HeaderSpec();
        spec.setRawSourceLine(line);
        spec.setLocation(Location.ofLine(origLine));
        content.getHeaderSpecs().add(spec);
    }

    // =========================================================================
    // F-spec (File Specification)
    // RPG III cols: 7-14  FileName
    //              15     FileType (I/O/U/C/D)
    //              16     FileDesignation (P/S/R/T/F/blank)
    //              17     EndOfFile (E/blank)
    //              18     Sequence (A/D/blank)
    //              19     FileFormat (F/E/blank)
    //              20-23  RecordLength
    //              24     Limits (L/blank)
    //              25-27  Length of Key/Record Address
    //              28     Record Address Type (A/P/K/blank)
    //              29     File Organization (I/T/blank)
    //              33-34  Overflow Indicator
    //              35-38  Key Field Starting Position
    //              39     Extension Code (E/L/blank)
    //              40-46  Device (DISK/PRINTER/WORKSTN/SPECIAL/SEQ)
    //              53-80  Continuation
    // =========================================================================

    private void scanFileSpec(String line, int origLine) {
        String fileName = sub(line, 6, 14);

        // Continuation line: blank fileName → merge into previous F-spec
        if (fileName == null || fileName.isBlank()) {
            String contKeyword = sub(line, 46, 80); // cols 47-80: keyword + data (e.g., RENAME(STUREC:S1REC))
            if (contKeyword != null && !contKeyword.isBlank() && !content.getFileSpecs().isEmpty()) {
                FileSpec parent = content.getFileSpecs().get(content.getFileSpecs().size() - 1);
                List<String> existing = parent.getContinuationLines() != null
                        ? new ArrayList<>(parent.getContinuationLines())
                        : new ArrayList<>();
                existing.add(contKeyword.trim());
                parent.setContinuationLines(existing);
            }
            return; // don't create a new FileSpec for continuation
        }

        FileSpec spec = new FileSpec();
        spec.setRawSourceLine(line);
        spec.setLocation(Location.ofLine(origLine));

        spec.setFileName(fileName);
        spec.setFileType(sub(line, 14, 15));
        spec.setFileDesignation(sub(line, 15, 16));
        spec.setEndOfFile(sub(line, 16, 17));
        spec.setFileAddition(sub(line, 17, 18));
        spec.setSequence(sub(line, 18, 19));
        spec.setFileFormat(sub(line, 19, 20));
        spec.setRecordLength(safeInt(sub(line, 19, 23)));
        spec.setLimits(sub(line, 23, 24));
        spec.setKeyLength(safeInt(sub(line, 24, 27)));
        spec.setRecordAddressType(sub(line, 30, 31));
        spec.setFileOrganization(sub(line, 31, 32));
        spec.setDevice(sub(line, 39, 46));

        // Continuation on same line (cols 47-80)
        String cont = sub(line, 46, 80);
        if (cont != null && !cont.isBlank()) {
            spec.setContinuationLines(new ArrayList<>(List.of(cont.trim())));
        }

        content.getFileSpecs().add(spec);

        String refType = "I".equals(spec.getFileType()) ? "input" : "O".equals(spec.getFileType()) ? "output" : "combined";
        IrDocument.DependencyRef ref = new IrDocument.DependencyRef(fileName.trim(), refType);
        ref.getLocations().add(spec.getLocation());
        dependencies.getReferencedFiles().add(ref);
    }

    // =========================================================================
    // E-spec (Extension Specification)
    // RPG III cols: 7-10   Reserved
    //              11-18  From File Name
    //              19-26  To File Name
    //              27-32  Array/Table Name
    //              33-35  Entries Per Record
    //              36-39  Entries Per Array/Table
    //              40-42  Entry Length
    //              43     Data Format
    //              44     Decimal Positions
    //              45     Sequence
    //              46-51  Alternating Array/Table Name
    //              52-54  Alternating Entry Length
    //              55     Alternating Data Format
    //              56     Alternating Decimal Positions
    //              57     Alternating Sequence
    //              58-80  Comments
    // =========================================================================

    private void scanExtensionSpec(String line, int origLine) {
        ExtensionSpec spec = new ExtensionSpec();
        spec.setRawSourceLine(line);
        spec.setLocation(Location.ofLine(origLine));

        spec.setFromFileName(sub(line, 10, 18));
        spec.setToFileName(sub(line, 18, 26));
        spec.setArrayOrTableName(sub(line, 26, 32));
        spec.setEntriesPerRecord(safeInt(sub(line, 32, 35)));
        spec.setEntriesPerArray(safeInt(sub(line, 35, 39)));
        spec.setEntryLength(safeInt(sub(line, 39, 42)));
        spec.setDataFormat(sub(line, 42, 43));
        spec.setDecimalPositions(safeInt(sub(line, 43, 44)));
        spec.setSequenceType(sub(line, 44, 45));

        String altName = sub(line, 45, 51);
        if (altName != null && !altName.isEmpty()) {
            spec.setAlternateArrayName(altName);
            spec.setAlternateEntryLength(safeInt(sub(line, 51, 54)));
            spec.setAlternateDataFormat(sub(line, 54, 55));
            spec.setAlternateDecimalPositions(safeInt(sub(line, 55, 56)));
        }

        content.getExtensionSpecs().add(spec);
    }

    // =========================================================================
    // I-spec (Input Specification)
    // =========================================================================

    private void scanInputSpec(String line, int origLine) {
        InputSpec spec = new InputSpec();
        spec.setRawSourceLine(line);
        spec.setLocation(Location.ofLine(origLine));

        // cols 7-22: identifier area (file name, DS/SDS keyword, etc.)
        // SDS keyword appears at columns 19-21 (0-indexed: 18-20)
        String identifier = sub(line, 6, 22);
        if (identifier != null && !identifier.isBlank()) {
            String trimmed = identifier.trim();
            // /COPY or /INCLUDE compiler directive — read full text from col 7 onward
            if (trimmed.startsWith("/COPY") || trimmed.startsWith("/INCLUDE")) {
                spec.setSpecLevel("compilerDirective");
                String fullDirective = sub(line, 6, 80);
                if (fullDirective != null) {
                    String[] parts = fullDirective.trim().split("\\s+", 2);
                    spec.setOption(parts[0]);  // /COPY or /INCLUDE
                    if (parts.length > 1) {
                        spec.setFileName(parts[1].trim());  // QCPYSRC,STUDNTCPY
                    }
                }
            } else if (trimmed.equalsIgnoreCase("DS") || trimmed.equalsIgnoreCase("SDS")) {
                spec.setSpecLevel("recordIdentification");
                spec.setOption(trimmed);

                DataStructure ds = new DataStructure();
                ds.setName(trimmed);
                ds.setLocation(Location.ofLine(origLine));
                ds.setType("dataStructure");
                content.getDataStructures().add(ds);
            } else if (isInitializationConstant(trimmed)) {
                // DS subfield with initialization: I   I    'constant value'
                // Extract constant from full cols 7-42 area (not truncated identifier)
                spec.setSpecLevel("fieldDefinition");
                String initArea = sub(line, 6, 42);
                spec.setInitializationValue(extractInitConstant(initArea != null ? initArea : trimmed));
            } else {
                spec.setSpecLevel("recordIdentification");
                spec.setFileName(trimmed);
            }
        } else {
            spec.setSpecLevel("fieldDefinition");
        }

        // Field detail
        String fieldArea = sub(line, 42, 70);
        if (fieldArea != null && !fieldArea.isBlank()) {
            spec.setDataFormat(sub(line, 42, 43));
            spec.setFromPosition(safeInt(sub(line, 43, 47)));
            spec.setToPosition(safeInt(sub(line, 47, 51)));
            spec.setDecimalPositions(safeInt(sub(line, 51, 52)));
            spec.setFieldName(sub(line, 52, 58));
        }

        content.getInputSpecs().add(spec);
    }

    /** Detect I-spec DS initialization pattern: starts with I and contains a quoted constant. */
    private boolean isInitializationConstant(String trimmedIdentifier) {
        return trimmedIdentifier.startsWith("I") && trimmedIdentifier.contains("'");
    }

    /** Extract the constant value from between single quotes. */
    private String extractInitConstant(String text) {
        int first = text.indexOf('\'');
        int last = text.lastIndexOf('\'');
        if (first >= 0 && last > first) {
            return text.substring(first + 1, last);
        }
        return text;
    }

    // =========================================================================
    // O-spec (Output Specification)
    // =========================================================================

    private void scanOutputSpec(String line, int origLine) {
        OutputSpec spec = new OutputSpec();
        spec.setRawSourceLine(line);
        spec.setLocation(Location.ofLine(origLine));

        String recordName = sub(line, 6, 14);
        if (recordName != null && !recordName.isBlank()) {
            spec.setSpecLevel("recordLevel");
            spec.setFileName(recordName.trim());
            spec.setType(sub(line, 14, 15));
            spec.setExceptName(sub(line, 31, 37));
        } else {
            spec.setSpecLevel("fieldLevel");
            String remaining = sub(line, 22, 80);
            if (remaining != null && !remaining.isBlank()) {
                spec.setFieldName(remaining.trim());
            }
        }

        content.getOutputSpecs().add(spec);
    }

    // =========================================================================
    // L-spec (Line Counter Specification) — minimal support
    // =========================================================================

    private void scanLineCounterSpec(String line, int origLine) {
        // L-spec not fully supported but we record it
        // Could add to a lineCounterSpecs list if needed
    }

    // =========================================================================
    // Comments
    // =========================================================================

    private void scanComment(String line, int origLine) {
        Comment comment = new Comment();
        comment.setLineNumber(origLine);
        comment.setText(sub(line, 7, 80));  // skip col 7 '*' marker
        comment.setRawSourceLine(line);
        comment.setLocation(new Location(origLine, origLine, 1, 80));

        if (line.length() > 5) {
            char specC = Character.toUpperCase(line.charAt(5));
            switch (specC) {
                case 'H' -> comment.setSpecContext("H");
                case 'F' -> comment.setSpecContext("F");
                case 'E' -> comment.setSpecContext("E");
                case 'I' -> comment.setSpecContext("I");
                case 'C' -> comment.setSpecContext("C");
                case 'O' -> comment.setSpecContext("O");
                case '*' -> comment.setSpecContext("*");
                default -> comment.setSpecContext(null);
            }
        }

        content.getComments().add(comment);
    }

    // =========================================================================
    // C-spec processing with control-flow nesting
    // =========================================================================

    private record CSpecLine(String rawLine, int origLine, int arrayIndex) {}

    private void processCSpecLines(List<CSpecLine> lines) {
        processCalcBlock(lines, 0, lines.size(), content.getCalculationSpecs());
    }

    /**
     * Process a range of C-spec lines, building nested control-flow blocks.
     * Returns the index after the last line consumed.
     */
    private int processCalcBlock(List<CSpecLine> lines, int start, int end, List<Object> target) {
        int i = start;
        while (i < end) {
            CSpecLine cline = lines.get(i);
            String opcode = extractOpcode(cline.rawLine);
            String upper = opcode.toUpperCase();

            if (upper.startsWith("IF")) {
                i = processIfBlock(lines, i, end, target);
            } else if (upper.equals("DO")) {
                i = processDoBlock(lines, i, end, target);
            } else if (upper.startsWith("DOW")) {
                i = processDowBlock(lines, i, end, target);
            } else if (upper.startsWith("DOU")) {
                i = processDouBlock(lines, i, end, target);
            } else if (upper.startsWith("CAS")) {
                i = processCaseBlock(lines, i, end, target);
            } else if (upper.equals("BEGSR")) {
                i = processSubroutine(lines, i, end, target);
            } else if (upper.equals("TAG")) {
                target.add(buildLabelNode(cline));
                i++;
            } else if (upper.equals("GOTO")) {
                target.add(buildGotoNode(cline));
                i++;
            } else {
                // Simple operation
                target.add(buildOperation(cline));
                i++;
            }
        }
        return i;
    }

    private CalcSpec.LabelNode buildLabelNode(CSpecLine cline) {
        CalcSpec.LabelNode node = new CalcSpec.LabelNode();
        node.setRawSourceLine(cline.rawLine);
        node.setLocation(Location.ofLine(cline.origLine));
        String f1 = extractFactor1(cline.rawLine);
        node.setLabelName(f1 != null ? f1.trim() : "");
        return node;
    }

    private CalcSpec.GotoNode buildGotoNode(CSpecLine cline) {
        CalcSpec.GotoNode node = new CalcSpec.GotoNode();
        node.setRawSourceLine(cline.rawLine);
        node.setLocation(Location.ofLine(cline.origLine));
        String f2 = extractFactor2Raw(cline.rawLine);
        node.setTargetLabel(f2 != null ? f2.trim() : "");
        return node;
    }


    private int processIfBlock(List<CSpecLine> lines, int start, int end, List<Object> target) {
        CSpecLine ifLine = lines.get(start);
        ConditionalBlock block = new ConditionalBlock();
        block.setRawSourceLine(ifLine.rawLine);
        block.setLocation(Location.ofLine(ifLine.origLine));

        String opcode = extractOpcode(ifLine.rawLine);
        block.setComparisonType(extractComparisonSuffix(opcode, "IF"));
        block.setCondition(buildFactor1(ifLine.rawLine, block.getLocation()));
        block.setComparisonValue(buildFactor2(ifLine.rawLine, block.getLocation()));
        extractControlLevelAndIndicators(block, ifLine.rawLine);

        // Consume ANDxx/ORxx lines to build compound conditions
        int bodyStart = consumeAndOrLines(lines, start + 1, end,
            block::getCondition, block::setCondition,
            block::getComparisonType, block::setComparisonType,
            block::getComparisonValue, block::setComparisonValue);

        int nesting = 1;
        int elseIdx = -1;
        int endIdx = -1;

        for (int i = bodyStart; i < end; i++) {
            String op = extractOpcode(lines.get(i).rawLine).toUpperCase();
            if (isBlockStart(op)) nesting++;
            else if (op.equals("ELSE") && nesting == 1) elseIdx = i;
            else if (isBlockEnd(op)) {
                nesting--;
                if (nesting == 0) { endIdx = i; break; }
            }
        }
        if (endIdx == -1) endIdx = end; // unclosed block

        // Then body
        int thenEnd = (elseIdx >= 0) ? elseIdx : endIdx;
        processCalcBlock(lines, bodyStart, thenEnd, block.getThenOps());

        // Else body
        if (elseIdx >= 0) {
            processCalcBlock(lines, elseIdx + 1, endIdx, block.getElseOps());
        }

        target.add(block);
        return endIdx + 1;
    }

    /**
     * Consume sequential ANDxx/ORxx lines after a block-opening opcode (IFxx/DOWxx/DOUxx),
     * building a compound BinaryOpNode tree. Returns the index of the first non-ANDxx/ORxx line.
     */
    private int consumeAndOrLines(List<CSpecLine> lines, int start, int end,
                                   Supplier<ExpressionNode> getCondition, Consumer<ExpressionNode> setCondition,
                                   Supplier<String> getCompType, Consumer<String> setCompType,
                                   Supplier<ExpressionNode> getCompValue, Consumer<ExpressionNode> setCompValue) {
        int i = start;
        while (i < end) {
            String nextOp = extractOpcode(lines.get(i).rawLine).toUpperCase();
            String logicalOp = null;
            String compSuffix = null;

            if (nextOp.startsWith("AND") && nextOp.length() > 3) {
                logicalOp = "AND";
                compSuffix = nextOp.substring(3); // e.g. "LT", "EQ"
            } else if (nextOp.startsWith("OR") && nextOp.length() > 2) {
                logicalOp = "OR";
                compSuffix = nextOp.substring(2);
            }

            if (logicalOp == null) break; // not an ANDxx/ORxx line

            CSpecLine andOrLine = lines.get(i);
            Location loc = Location.ofLine(andOrLine.origLine);

            // Build the new comparison: BinaryOpNode(compSuffix, factor1, factor2)
            ExpressionNode f1 = buildFactor1(andOrLine.rawLine, loc);
            ExpressionNode f2 = buildFactor2(andOrLine.rawLine, loc);
            BinaryOpNode newComp = new BinaryOpNode();
            newComp.setOperator(compSuffix);
            newComp.setLeft(f1);
            newComp.setRight(f2);
            newComp.setRawText(compSuffix);
            newComp.setLocation(loc);

            // Build initial comparison from condition/comparisonValue if this is the first ANDxx/ORxx
            ExpressionNode existingCondition = getCondition.get();
            if (existingCondition != null && getCompType.get() != null) {
                // Convert the simple comparison to a BinaryOpNode first
                BinaryOpNode initialComp = new BinaryOpNode();
                initialComp.setOperator(getCompType.get());
                initialComp.setLeft(existingCondition);
                initialComp.setRight(getCompValue.get());
                initialComp.setRawText(getCompType.get());
                initialComp.setLocation(existingCondition.getLocation());
                existingCondition = initialComp;
            }

            // Wrap: BinaryOpNode(logicalOp, existingCondition, newComp)
            BinaryOpNode compound = new BinaryOpNode();
            compound.setOperator(logicalOp);
            compound.setLeft(existingCondition);
            compound.setRight(newComp);
            compound.setRawText(logicalOp);
            compound.setLocation(loc);

            // Update the block: condition = compound, comparisonType/Value = null
            setCondition.accept(compound);
            setCompType.accept(null);
            setCompValue.accept(null);

            i++;
        }
        return i;
    }

    private int processDoBlock(List<CSpecLine> lines, int start, int end, List<Object> target) {
        CSpecLine doLine = lines.get(start);
        DoBlock block = new DoBlock();
        block.setRawSourceLine(doLine.rawLine);
        block.setLocation(Location.ofLine(doLine.origLine));
        block.setStartValue(buildFactor1(doLine.rawLine, block.getLocation()));
        block.setEndValue(buildFactor2(doLine.rawLine, block.getLocation()));
        block.setIndexVariable(buildResultField(doLine.rawLine, block.getLocation()));
        extractControlLevelAndIndicators(block, doLine.rawLine);

        int endIdx = findBlockEnd(lines, start + 1, end);
        processCalcBlock(lines, start + 1, endIdx, block.getBodyOps());

        target.add(block);
        return endIdx + 1;
    }

    private int processDowBlock(List<CSpecLine> lines, int start, int end, List<Object> target) {
        CSpecLine dowLine = lines.get(start);
        DoWhileBlock block = new DoWhileBlock();
        block.setRawSourceLine(dowLine.rawLine);
        block.setLocation(Location.ofLine(dowLine.origLine));

        String opcode = extractOpcode(dowLine.rawLine);
        block.setComparisonType(extractComparisonSuffix(opcode, "DOW"));
        block.setCondition(buildFactor1(dowLine.rawLine, block.getLocation()));
        block.setComparisonValue(buildFactor2(dowLine.rawLine, block.getLocation()));
        extractControlLevelAndIndicators(block, dowLine.rawLine);

        // Consume ANDxx/ORxx
        int bodyStart = consumeAndOrLines(lines, start + 1, end,
            block::getCondition, block::setCondition,
            block::getComparisonType, block::setComparisonType,
            block::getComparisonValue, block::setComparisonValue);

        int endIdx = findBlockEnd(lines, bodyStart, end);
        processCalcBlock(lines, bodyStart, endIdx, block.getBodyOps());

        target.add(block);
        return endIdx + 1;
    }

    private int processDouBlock(List<CSpecLine> lines, int start, int end, List<Object> target) {
        CSpecLine douLine = lines.get(start);
        DoUntilBlock block = new DoUntilBlock();
        block.setRawSourceLine(douLine.rawLine);
        block.setLocation(Location.ofLine(douLine.origLine));

        String opcode = extractOpcode(douLine.rawLine);
        block.setComparisonType(extractComparisonSuffix(opcode, "DOU"));
        block.setCondition(buildFactor1(douLine.rawLine, block.getLocation()));
        block.setComparisonValue(buildFactor2(douLine.rawLine, block.getLocation()));
        extractControlLevelAndIndicators(block, douLine.rawLine);

        // Consume ANDxx/ORxx
        int bodyStart = consumeAndOrLines(lines, start + 1, end,
            block::getCondition, block::setCondition,
            block::getComparisonType, block::setComparisonType,
            block::getComparisonValue, block::setComparisonValue);

        int endIdx = findBlockEnd(lines, bodyStart, end);
        processCalcBlock(lines, bodyStart, endIdx, block.getBodyOps());

        target.add(block);
        return endIdx + 1;
    }

    private int processCaseBlock(List<CSpecLine> lines, int start, int end, List<Object> target) {
        CSpecLine casLine = lines.get(start);
        CaseBlock block = new CaseBlock();
        block.setRawSourceLine(casLine.rawLine);
        block.setLocation(Location.ofLine(casLine.origLine));

        // First CASxx entry
        CaseEntry entry = buildCaseEntry(casLine.rawLine, block.getLocation());
        if (entry != null) block.getEntries().add(entry);

        // Consume subsequent CASxx and CAS lines
        int i = start + 1;
        while (i < end) {
            String op = extractOpcode(lines.get(i).rawLine).toUpperCase();
            if (op.startsWith("CAS") && !op.equals("CASE")) {
                CSpecLine cl = lines.get(i);
                CaseEntry e = buildCaseEntry(cl.rawLine, Location.ofLine(cl.origLine));
                if (e != null) block.getEntries().add(e);
                i++;
            } else if (isBlockEnd(op)) {
                i++; // consume END
                break;
            } else {
                break; // unexpected — stop
            }
        }

        target.add(block);
        return i;
    }

    private int processSubroutine(List<CSpecLine> lines, int start, int end, List<Object> target) {
        CSpecLine begsrLine = lines.get(start);
        SubroutineBlock block = new SubroutineBlock();
        block.setRawSourceLine(begsrLine.rawLine);
        block.setLocation(Location.ofLine(begsrLine.origLine));

        String factor1 = extractFactor1(begsrLine.rawLine);
        block.setSubroutineName(factor1 != null ? factor1.trim() : "");

        // Find matching ENDSR
        int endIdx = -1;
        for (int i = start + 1; i < end; i++) {
            String op = extractOpcode(lines.get(i).rawLine).toUpperCase();
            if (op.equals("ENDSR")) {
                endIdx = i;
                break;
            }
        }
        if (endIdx == -1) endIdx = end;

        processCalcBlock(lines, start + 1, endIdx, block.getOperations());

        target.add(block);

        Subroutine sub = new Subroutine();
        sub.setName(block.getSubroutineName());
        sub.setDefinedAtLine(begsrLine.origLine);
        content.getSubroutines().add(sub);

        return endIdx + 1;
    }

    // =========================================================================
    // Block-end detection helpers
    // =========================================================================

    private boolean isBlockStart(String opcode) {
        switch (opcode) {
            case "IF", "IFEQ", "IFNE", "IFLE", "IFLT", "IFGE", "IFGT",
                 "DO",
                 "DOW", "DOWEQ", "DOWNE", "DOWLE", "DOWLT", "DOWGE", "DOWGT",
                 "DOU", "DOUEQ", "DOUNE", "DOULE", "DOULT", "DOUGE", "DOUGT",
                 "CAS", "CASEQ", "CASNE", "CASLE", "CASLT", "CASGE", "CASGT":
                return true;
            default:
                return false;
        }
    }

    private boolean isBlockEnd(String opcode) {
        return opcode.equals("END") || opcode.equals("ENDIF")
            || opcode.equals("ENDDO") || opcode.equals("ENDCS")
            || opcode.equals("ENDSR");
    }

    private int findBlockEnd(List<CSpecLine> lines, int start, int end) {
        int nesting = 1;
        for (int i = start; i < end; i++) {
            String op = extractOpcode(lines.get(i).rawLine).toUpperCase();
            if (isBlockStart(op)) nesting++;
            else if (isBlockEnd(op)) {
                nesting--;
                if (nesting == 0) return i;
            }
        }
        return end; // unclosed block
    }

    // =========================================================================
    // C-spec field extraction
    // =========================================================================

    /**
     * Build an Operation from raw C-spec line:
     *   Cols 7-8   : Control level
     *   Cols 9-11  : Conditioning indicator
     *   Cols 12-25 : Factor 1
     *   Cols 26-27 : Operation extender
     *   Cols 28-32 : Operation code
     *   Cols 33-42 : Factor 2
     *   Cols 43-48 : Result field
     *   Cols 49-51 : Field length
     *   Col  52    : Decimal positions
     *   Col  53    : Half adjust
     *   Cols 54-55 : Resulting indicator (high)
     *   Cols 56-57 : Resulting indicator (low)
     *   Cols 58-59 : Resulting indicator (equal)
     *   Cols 60-80 : Comments
     */
    private Operation buildOperation(CSpecLine cline) {
        String rawLine = cline.rawLine;
        Location loc = Location.ofLine(cline.origLine);

        Operation op = new Operation();
        op.setRawSourceLine(rawLine);
        op.setLocation(loc);

        // Control level (cols 7-8)
        String ctrlLevel = sub(rawLine, 6, 8);
        if (ctrlLevel != null && !ctrlLevel.isBlank()) {
            op.setControlLevel(ctrlLevel.trim());
        }

        // Conditioning indicators (cols 9-11)
        extractConditioningIndicators(op, rawLine);

        // Factor 1 (cols 12-25)
        String f1 = extractFactor1(rawLine);
        if (f1 != null && !f1.isBlank()) {
            op.setFactor1(ExpressionBuilder.build(f1.trim(), loc));
        }

        // Operation extender (cols 26-27)
        op.setExtendedOpcode(sub(rawLine, 25, 27));

        // Operation code (cols 28-32)
        String opcode = extractOpcode(rawLine);
        op.setOpcode(opcode);

        // Factor 2 (cols 33-42)
        String f2 = extractFactor2Raw(rawLine);
        if (f2 != null && !f2.isBlank()) {
            op.setFactor2(ExpressionBuilder.build(f2.trim(), loc));
        }

        // Result field (cols 43-48)
        String result = extractResultRaw(rawLine);
        if (result != null && !result.isBlank()) {
            op.setResultField(ExpressionBuilder.build(result.trim(), loc));
        }

        // Field length (cols 49-51)
        op.setFieldLength(safeInt(sub(rawLine, 48, 51)));

        // Decimal positions (col 52)
        op.setDecimalPositions(safeInt(sub(rawLine, 51, 52)));

        // Propagate length/decimal into result field IdentifierNode
        if (op.getResultField() instanceof IdentifierNode idNode) {
            if (op.getFieldLength() != null) idNode.setLength(op.getFieldLength());
            if (op.getDecimalPositions() != null) idNode.setDecimalPositions(op.getDecimalPositions());
        }

        // Half adjust (col 53)
        String halfAdj = sub(rawLine, 52, 53);
        if (halfAdj != null && halfAdj.equalsIgnoreCase("H")) {
            op.setExtendedOpcode("H");
        }

        // Resulting indicators (cols 54-59)
        String ri1 = sub(rawLine, 53, 55);
        String ri2 = sub(rawLine, 55, 57);
        String ri3 = sub(rawLine, 57, 59);
        if ((ri1 != null && !ri1.isBlank()) || (ri2 != null && !ri2.isBlank()) || (ri3 != null && !ri3.isBlank())) {
            ResultingIndicators ri = new ResultingIndicators();
            ri.setHigh(toIndicatorRef(ri1));
            ri.setLow(toIndicatorRef(ri2));
            ri.setEqual(toIndicatorRef(ri3));
            op.setResultingIndicators(ri);
            applyIndicatorMeanings(op.getOpcode(), ri);
        }

        // Handle special opcodes
        handleSpecialOperation(op, loc);

        return op;
    }

    private void handleSpecialOperation(Operation op, Location loc) {
        String upper = op.getOpcode() != null ? op.getOpcode().toUpperCase().trim() : "";
        switch (upper) {
            case "EXSR" -> {
                String srName = op.getFactor2() != null ? op.getFactor2().getRawText() : "";
                exsrCalls.computeIfAbsent(srName.trim().toUpperCase(), k -> new ArrayList<>())
                        .add(loc);
            }
            case "CALL" -> {
                if (op.getFactor2() != null) {
                    String pgmName = op.getFactor2().getRawText();
                    if (pgmName != null && !pgmName.isBlank()) {
                        String cleanName = pgmName.trim().replace("'", "");
                        IrDocument.DependencyRef ref = new IrDocument.DependencyRef(cleanName, "call");
                        ref.getLocations().add(op.getLocation());
                        dependencies.getCalledPrograms().add(ref);
                    }
                }
            }
        }
    }

    // =========================================================================
    // C-spec raw field extractors
    // =========================================================================

    private String extractOpcode(String rawLine) {
        String op = sub(rawLine, 27, 32);
        return op != null ? op.trim() : "";
    }

    private String extractFactor1(String rawLine) {
        return sub(rawLine, 11, 25);
    }

    private String extractFactor2Raw(String rawLine) {
        return sub(rawLine, 32, 42);
    }

    private String extractResultRaw(String rawLine) {
        return sub(rawLine, 42, 48);
    }

    private String extractComparisonSuffix(String opcode, String prefix) {
        if (opcode == null) return null;
        String upper = opcode.trim().toUpperCase();
        if (upper.length() > prefix.length()) {
            return upper.substring(prefix.length());
        }
        return null;
    }

    private ExpressionNode buildFactor1(String line, Location loc) {
        String f1 = extractFactor1(line);
        if (f1 != null && !f1.isBlank()) {
            return ExpressionBuilder.build(f1.trim(), loc);
        }
        return null;
    }

    private ExpressionNode buildFactor2(String line, Location loc) {
        String f2 = extractFactor2Raw(line);
        if (f2 != null && !f2.isBlank()) {
            return ExpressionBuilder.build(f2.trim(), loc);
        }
        return null;
    }

    private ExpressionNode buildResultField(String line, Location loc) {
        String result = extractResultRaw(line);
        if (result != null && !result.isBlank()) {
            return ExpressionBuilder.build(result.trim(), loc);
        }
        return null;
    }

    private void extractControlLevelAndIndicators(CalcNode node, String rawLine) {
        String ctrlLevel = sub(rawLine, 6, 8);
        if (ctrlLevel != null && !ctrlLevel.isBlank()) {
            node.setControlLevel(ctrlLevel.trim());
        }
        extractConditioningIndicators(node, rawLine);
    }

    private void extractConditioningIndicators(CalcNode node, String rawLine) {
        String indArea = sub(rawLine, 8, 11);
        if (indArea != null && !indArea.isBlank()) {
            boolean negated = indArea.charAt(0) == 'N';
            String indText = negated ? indArea.substring(1).trim() : indArea.trim();
            if (!indText.isEmpty()) {
                node.getConditioningIndicators().add(
                        new ConditioningIndicator(negated, indText, "first"));
            }
        }
    }

    private CaseEntry buildCaseEntry(String rawLine, Location loc) {
        CaseEntry entry = new CaseEntry();
        entry.setLocation(loc);

        String opcode = extractOpcode(rawLine);
        entry.setComparisonType(extractComparisonSuffix(opcode, "CAS"));
        entry.setComparisonValue(buildFactor2(rawLine, loc));

        String result = extractResultRaw(rawLine);
        if (result != null && !result.isBlank()) {
            entry.setSubroutineName(ExpressionBuilder.build(result.trim(), loc));
        }

        return entry;
    }

    // =========================================================================
    // Safe substring / integer extraction
    // =========================================================================

    private String sub(String line, int begin, int end) {
        if (line == null || line.length() < end) {
            if (line != null && line.length() > begin) {
                return line.substring(begin).trim();
            }
            return null;
        }
        return line.substring(begin, end).trim();
    }

    private IndicatorRef toIndicatorRef(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String name = raw.trim();
        return new IndicatorRef(name, classifyIndicator(name));
    }

    // =========================================================================
    // Opcode-specific indicator meanings
    // Key = opcode, Value = String[3] { highMeaning, lowMeaning, equalMeaning }
    // =========================================================================
    private static final Map<String, String[]> OPCODE_INDICATOR_MEANINGS = Map.ofEntries(
        // File operations
        Map.entry("CHAIN", new String[]{"recordNotFound", "error", null}),
        Map.entry("READ",  new String[]{"eof", "error", null}),
        Map.entry("READE", new String[]{"eof", "error", null}),
        Map.entry("READP", new String[]{"bof", "error", null}),
        Map.entry("READPE",new String[]{"bof", "error", null}),
        Map.entry("SETLL", new String[]{"recordNotFound", null, "recordExists"}),
        Map.entry("SETGT", new String[]{"recordNotFound", null, null}),
        Map.entry("WRITE", new String[]{null, "error", null}),
        Map.entry("UPDAT", new String[]{null, "error", null}),
        Map.entry("DELET", new String[]{"recordNotFound", "error", null}),
        // Arithmetic
        Map.entry("ADD",   new String[]{"plus", "minus", "zero"}),
        Map.entry("SUB",   new String[]{"plus", "minus", "zero"}),
        Map.entry("MULT",  new String[]{"plus", "minus", "zero"}),
        Map.entry("DIV",   new String[]{"plus", "minus", "zero"}),
        Map.entry("MVR",   new String[]{"plus", "minus", "zero"}),
        Map.entry("SQRT",  new String[]{"plus", "minus", "zero"}),
        Map.entry("Z-ADD", new String[]{"plus", "minus", "zero"}),
        Map.entry("Z-SUB", new String[]{"plus", "minus", "zero"}),
        Map.entry("XFOOT", new String[]{"plus", "minus", "zero"}),
        // Compare
        Map.entry("COMP",  new String[]{"greater", "less", "equal"}),
        // Lookup
        Map.entry("LOKUP", new String[]{"highMatch", "lowMatch", "equalMatch"}),
        // String
        Map.entry("SCAN",  new String[]{null, null, "found"}),
        Map.entry("SUBST", new String[]{null, "error", null}),
        Map.entry("XLATE", new String[]{null, "error", null}),
        // Move / assignment
        Map.entry("MOVEL", new String[]{"plus", "minus", "zero"}),
        Map.entry("MOVE",  new String[]{"plus", "minus", "zero"}),
        // Call
        Map.entry("CALL",  new String[]{null, "error", null}),
        // Test
        Map.entry("TESTN", new String[]{"numeric", "leadingBlanks", "allBlanks"}),
        Map.entry("TESTB", new String[]{"allOff", "mixed", "allOn"}),
        Map.entry("TESTZ", new String[]{"aToI", "jToR", "sToZ"}),
        // Check
        Map.entry("CHECK", new String[]{null, null, "found"}),
        Map.entry("CHEKR", new String[]{null, null, "found"}),
        // Set indicators
        Map.entry("SETON", new String[]{"set", "set", "set"}),
        Map.entry("SETOF", new String[]{"set", "set", "set"})
    );

    private void applyIndicatorMeanings(String opcode, ResultingIndicators ri) {
        if (opcode == null || ri == null) return;
        String[] meanings = OPCODE_INDICATOR_MEANINGS.get(opcode.toUpperCase());
        if (meanings == null) return;
        if (ri.getHigh() != null && meanings[0] != null) ri.getHigh().setMeaning(meanings[0]);
        if (ri.getLow()  != null && meanings[1] != null) ri.getLow().setMeaning(meanings[1]);
        if (ri.getEqual()!= null && meanings[2] != null) ri.getEqual().setMeaning(meanings[2]);
    }

    static String classifyIndicator(String name) {
        if (name == null || name.isEmpty()) return "unknown";
        String upper = name.toUpperCase();
        // Numeric: 01-99
        if (upper.length() == 2 && Character.isDigit(upper.charAt(0)) && Character.isDigit(upper.charAt(1))) {
            return "numeric";
        }
        // Special: LR, MR, RT, KA-KN, 1P, U1-U8
        if (upper.matches("LR|MR|RT|1P|K[A-N]|U[1-8]")) {
            return "special";
        }
        // Halt: H1-H9
        if (upper.matches("H[1-9]")) {
            return "halt";
        }
        // Level: L0-L9
        if (upper.matches("L[0-9]")) {
            return "level";
        }
        // Overflow: OA-OG, OV
        if (upper.matches("O[A-GV]")) {
            return "overflow";
        }
        return "unknown";
    }

    private Integer safeInt(String text) {
        if (text == null || text.isBlank()) return null;
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // =========================================================================
    // Build sourceLines[]
    // =========================================================================

    private void buildSourceLines() {
        String[] lines = normalizedSource.getLines();
        int[] lineNumbers = normalizedSource.getOriginalLineNumbers();
        String[] seqNumbers = normalizedSource.getSequenceNumbers();

        List<SourceLine> sourceLines = new ArrayList<>();
        for (int i = 0; i < lines.length; i++) {
            SourceLine sl = new SourceLine();
            sl.setLineNumber(lineNumbers[i]);
            sl.setRawText(lines[i]);
            sl.setSequenceNumber(i < seqNumbers.length ? seqNumbers[i] : "");

            String line = lines[i];
            if (line.length() >= 6) {
                char specChar = Character.toUpperCase(line.charAt(5));
                switch (specChar) {
                    case 'H' -> sl.setSpecType("H");
                    case 'F' -> sl.setSpecType("F");
                    case 'E' -> sl.setSpecType("E");
                    case 'L' -> sl.setSpecType("L");
                    case 'I' -> sl.setSpecType("I");
                    case 'C' -> sl.setSpecType("C");
                    case 'O' -> sl.setSpecType("O");
                    case '*' -> {
                        sl.setSpecType("comment");
                        sl.setComment(true);
                    }
                    default -> sl.setSpecType(null);
                }
            }

            sl.setBlank(line.trim().isEmpty());
            sourceLines.add(sl);
        }
        content.setSourceLines(sourceLines);
    }

    private void populateSubroutineCalledFrom() {
        for (Subroutine sub : content.getSubroutines()) {
            String name = sub.getName();
            if (name != null) {
                List<Location> calls = exsrCalls.get(name.trim().toUpperCase());
                if (calls != null) {
                    List<Integer> lineNums = new ArrayList<>();
                    for (Location loc : calls) {
                        lineNums.add(loc.getStartLine());
                    }
                    sub.setCalledFrom(lineNums);
                }
            }
        }
    }
}
