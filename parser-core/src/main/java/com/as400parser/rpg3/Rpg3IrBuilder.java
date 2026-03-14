package com.as400parser.rpg3;

import com.as400parser.common.model.*;
import com.as400parser.common.normalizer.NormalizedSource;
import com.as400parser.rpg3.generated.Rpg3Parser;
import com.as400parser.rpg3.generated.Rpg3ParserBaseVisitor;
import com.as400parser.rpg3.model.*;
import com.as400parser.rpg3.model.CalcSpec.*;
import com.as400parser.rpg3.model.Rpg3Content.*;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;

/**
 * ANTLR visitor that walks the RPG3 parse tree and builds the IR model.
 * <p>
 * Extends {@code Rpg3ParserBaseVisitor<Void>} — each visit method extracts
 * column-positional data from the parse tree context and populates the
 * corresponding IR model objects.
 * <p>
 * Tasks 3.1–3.15 from the planning document.
 */
public class Rpg3IrBuilder extends Rpg3ParserBaseVisitor<Void> {

    private final NormalizedSource normalizedSource;
    private final IrDocument document;
    private final Rpg3Content content;
    private final IrDocument.Dependencies dependencies;

    // Track current spec context for comments
    private String currentSpecContext = null;

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

    /** Get the built IR document after visiting. */
    public IrDocument getResult() {
        // Build sourceLines
        buildSourceLines();
        // Populate subroutine calledFrom cross-references
        populateSubroutineCalledFrom();
        return document;
    }

    // =========================================================================
    // Task 3.1: Top-level visitor
    // =========================================================================

    @Override
    public Void visitRpg3Program(Rpg3Parser.Rpg3ProgramContext ctx) {
        return visitChildren(ctx);
    }

    // =========================================================================
    // Task 3.2: H-spec → headerSpecs[]
    // =========================================================================

    @Override
    public Void visitHspec_fixed(Rpg3Parser.Hspec_fixedContext ctx) {
        currentSpecContext = "H";

        HeaderSpec spec = new HeaderSpec();
        String rawLine = getRawLine(ctx);
        spec.setRawSourceLine(rawLine);
        spec.setLocation(getLocation(ctx));

        // H-spec is mostly raw — extract what the grammar parsed
        // The grammar says: HS_FIXED hs_expression* (EOL|EOF)
        // For RPG3 H-specs, the raw line has the column-specific data
        content.getHeaderSpecs().add(spec);
        return null;
    }

    // =========================================================================
    // Task 3.3: F-spec → fileSpecs[] + dependencies.referencedFiles[]
    // =========================================================================

    @Override
    public Void visitFspec_fixed(Rpg3Parser.Fspec_fixedContext ctx) {
        currentSpecContext = "F";

        FileSpec spec = new FileSpec();
        String rawLine = getRawLine(ctx);
        spec.setRawSourceLine(rawLine);
        spec.setLocation(getLocation(ctx));

        // Extract from grammar tokens
        spec.setFileName(safeText(ctx.FS_RecordName()));
        spec.setFileType(safeText(ctx.FS_Type()));
        spec.setFileDesignation(safeText(ctx.FS_Designation()));
        spec.setEndOfFile(safeText(ctx.FS_EndOfFile()));
        spec.setFileAddition(safeText(ctx.FS_Addution()));
        spec.setSequence(safeText(ctx.FS_Sequence()));
        spec.setFileFormat(safeText(ctx.FS_Format()));
        spec.setRecordLength(safeInteger(ctx.FS_RecordLength()));
        spec.setLimits(safeText(ctx.FS_Limits()));
        spec.setKeyLength(safeInteger(ctx.FS_LengthOfKey()));
        spec.setRecordAddressType(safeText(ctx.FS_RecordAddressType()));
        spec.setFileOrganization(safeText(ctx.FS_Organization()));
        spec.setDevice(safeText(ctx.FS_Device()));

        // GAP-4: F-spec continuation lines
        // In RPG3, F-spec continuations have F at col 6 with cols 7-18 blank,
        // and keyword data in cols 19+. Scan raw source lines after this one.
        appendFSpecContinuationLines(spec, ctx);

        content.getFileSpecs().add(spec);

        // Add to dependencies
        String fileName = spec.getFileName();
        if (fileName != null && !fileName.isBlank()) {
            dependencies.getReferencedFiles().add(fileName.trim());
        }

        return null;
    }

    /** GAP-4: Scan subsequent source lines for F-spec continuation content. */
    private void appendFSpecContinuationLines(FileSpec spec, Rpg3Parser.Fspec_fixedContext ctx) {
        int startLine = ctx.getStart().getLine(); // 1-based
        String[] lines = normalizedSource.getLines();
        StringBuilder contBuilder = new StringBuilder();

        for (int i = startLine; i < lines.length; i++) { // startLine is already the NEXT line (0-based index)
            String line = lines[i];
            if (line.length() >= 6 && (line.charAt(5) == 'F' || line.charAt(5) == 'f')) {
                // Check if cols 7-18 (0-based 6-17) are blank — indicates continuation
                String recordArea = line.length() >= 18 ? line.substring(6, 18) : line.substring(6);
                if (recordArea.isBlank()) {
                    // Extract keyword content from col 19+ (0-based index 18)
                    if (line.length() > 18) {
                        String keyword = line.substring(18).trim();
                        if (!keyword.isEmpty()) {
                            if (contBuilder.length() > 0) contBuilder.append(" ");
                            contBuilder.append(keyword);
                        }
                    }
                } else {
                    break; // Next F-spec is a new file definition, not a continuation
                }
            } else {
                break; // Not an F-spec line
            }
        }

        if (contBuilder.length() > 0) {
            spec.setContinuationLines(contBuilder.toString());
        }
    }

    // =========================================================================
    // Task 3.4: E-spec → extensionSpecs[]
    // =========================================================================

    @Override
    public Void visitExtensionSpec(Rpg3Parser.ExtensionSpecContext ctx) {
        currentSpecContext = "E";

        ExtensionSpec spec = new ExtensionSpec();
        String rawLine = getRawLine(ctx);
        spec.setRawSourceLine(rawLine);
        spec.setLocation(getLocation(ctx));

        spec.setFromFileName(safeText(ctx.ES_FromFileName()));
        spec.setToFileName(safeText(ctx.ES_ToFileName()));
        spec.setArrayOrTableName(safeText(ctx.ES_TableName()));
        spec.setEntriesPerRecord(safeInteger(ctx.ES_EntriesPerRecord()));
        spec.setEntriesPerArray(safeInteger(ctx.ES_NumberOfEntries()));
        spec.setEntryLength(safeInteger(ctx.ES_EntryLength()));
        spec.setDataFormat(safeText(ctx.ES_DataFormat()));
        spec.setDecimalPositions(safeInteger(ctx.ES_DecimalPositions()));
        spec.setSequenceType(safeText(ctx.ES_Sequence()));
        spec.setAlternateArrayName(safeText(ctx.ES_AlternatingName()));
        spec.setAlternateEntryLength(safeInteger(ctx.ES_AlternatingLength()));
        spec.setAlternateDataFormat(safeText(ctx.ES_AlternatingDataFormat()));
        spec.setAlternateDecimalPositions(safeInteger(ctx.ES_AlternatingDecimalPositions()));

        content.getExtensionSpecs().add(spec);
        return null;
    }

    // =========================================================================
    // Task 3.5: L-spec → lineCounterSpecs[]
    // =========================================================================

    @Override
    public Void visitLineCounterSpec(Rpg3Parser.LineCounterSpecContext ctx) {
        currentSpecContext = "L";

        LineCounterSpec spec = new LineCounterSpec();
        String rawLine = getRawLine(ctx);
        spec.setRawSourceLine(rawLine);
        spec.setLocation(getLocation(ctx));

        spec.setFileName(safeText(ctx.LS_FileName()));
        spec.setLinesPerPage(safeInteger(ctx.LS_LinesPerPage()));
        spec.setOverflowLine(safeInteger(ctx.LS_OverflowLine()));

        content.getLineCounterSpecs().add(spec);
        return null;
    }

    // =========================================================================
    // Task 3.6: O-spec → outputSpecs[]
    // =========================================================================

    @Override
    public Void visitOspec_fixed(Rpg3Parser.Ospec_fixedContext ctx) {
        currentSpecContext = "O";

        OutputSpec spec = new OutputSpec();
        String rawLine = getRawLine(ctx);
        spec.setRawSourceLine(rawLine);
        spec.setLocation(getLocation(ctx));

        // Check if this is a record-level or field-level O-spec
        if (ctx.os_fixed_pgmfield() != null) {
            // Field level
            spec.setSpecLevel("fieldLevel");
            Rpg3Parser.Os_fixed_pgmfieldContext field = ctx.os_fixed_pgmfield();
            spec.setFieldName(safeText(field.OS_FieldName()));
            spec.setEditCode(safeText(field.OS_EditNames()));
            spec.setBlankAfter(safeText(field.OS_BlankAfter()));
            spec.setEndPosition(safeInteger(field.OS_EndPosition()));
            spec.setDataFormat(safeText(field.OS_DataFormat()));
        } else {
            // Record level
            spec.setSpecLevel("recordLevel");
            spec.setFileName(safeText(ctx.OS_RecordName()));
            spec.setType(safeText(ctx.OS_Type()));

            if (ctx.os_fixed_pgmdesc1() != null) {
                spec.setFetchOverflow(safeText(ctx.os_fixed_pgmdesc1().OS_FetchOverflow()));
                spec.setExceptName(safeText(ctx.os_fixed_pgmdesc1().OS_ExceptName()));
            } else if (ctx.os_fixed_pgmdesc2() != null) {
                spec.setExceptName(safeText(ctx.os_fixed_pgmdesc2().OS_ExceptName()));
            }
        }

        content.getOutputSpecs().add(spec);
        return null;
    }

    // =========================================================================
    // Task 3.7: I-spec → inputSpecs[] + dataStructures[]
    // =========================================================================

    @Override
    public Void visitIspec_fixed(Rpg3Parser.Ispec_fixedContext ctx) {
        currentSpecContext = "I";

        InputSpec spec = new InputSpec();
        String rawLine = getRawLine(ctx);
        spec.setRawSourceLine(rawLine);
        spec.setLocation(getLocation(ctx));

        if (ctx.IS_FileName() != null) {
            // Record identification or externally-described record
            spec.setFileName(safeText(ctx.IS_FileName()));

            if (ctx.is_rec() != null) {
                spec.setSpecLevel("recordIdentification");
                spec.setSequenceNumber(safeText(ctx.is_rec().IS_Sequence()));
                spec.setOption(safeText(ctx.is_rec().IS_Option()));

                // GAP-5: Extract record identification indicator
                Rpg3Parser.RecordIdIndicatorContext riCtx = ctx.is_rec().recordIdIndicator();
                if (riCtx != null) {
                    String indText = riCtx.getText().trim();
                    if (!indText.isEmpty()) {
                        spec.setRecordIdIndicator(indText);
                    }
                }

                // GAP-5: Data structure detection — IS_Option contains "DS"
                String option = spec.getOption();
                if (option != null && option.trim().equalsIgnoreCase("DS")) {
                    DataStructure ds = new DataStructure();
                    ds.setName(spec.getFileName() != null ? spec.getFileName().trim() : "");
                    ds.setLocation(getLocation(ctx));
                    ds.setType("dataStructure");
                    content.getDataStructures().add(ds);
                }
            } else if (ctx.is_external_rec() != null) {
                spec.setSpecLevel("recordIdentification");
            }
        } else if (ctx.is_external_field() != null) {
            // Field definition (externally described)
            spec.setSpecLevel("fieldDefinition");
            Rpg3Parser.Is_external_fieldContext field = ctx.is_external_field();
            spec.setFieldName(safeText(field.IF_FieldName()));
        } else if (ctx.IFD_FIELD_NAME() != null) {
            // Program-described field definition
            spec.setSpecLevel("fieldDefinition");
            spec.setFieldName(safeText(ctx.IFD_FIELD_NAME()));
            spec.setDataFormat(safeText(ctx.IFD_DATA_FORMAT()));

            // Extract positions from IFD_FIELD_LOCATION (contains from/to)
            String fieldLoc = safeText(ctx.IFD_FIELD_LOCATION());
            if (fieldLoc != null && !fieldLoc.isBlank()) {
                spec.setFromPosition(parseFieldPosition(fieldLoc, true));
                spec.setToPosition(parseFieldPosition(fieldLoc, false));
            }

            spec.setDecimalPositions(safeInteger(ctx.IFD_DECIMAL_POSITIONS()));
        }

        content.getInputSpecs().add(spec);
        return null;
    }

    // =========================================================================
    // Task 3.9: C-spec → calculationSpecs[]
    // =========================================================================

    @Override
    public Void visitCspec_fixed(Rpg3Parser.Cspec_fixedContext ctx) {
        currentSpecContext = "C";

        // Get the operation from cspec_fixed_standard
        Rpg3Parser.Cspec_fixed_standardContext stdCtx = ctx.cspec_fixed_standard();
        if (stdCtx == null) return null;

        Operation op = buildOperation(ctx, stdCtx);
        if (op == null) return null;

        // Special handling for TAG, GOTO, EXSR
        String opcode = op.getOpcode();
        if (opcode != null) {
            String upper = opcode.toUpperCase().trim();
            if (upper.equals("TAG")) {
                LabelNode label = new LabelNode();
                copyCalcNodeFields(op, label);
                label.setLabelName(op.getFactor1() != null ? op.getFactor1().getRawText() : "");
                content.getCalculationSpecs().add(label);
                return null;
            }
            if (upper.equals("GOTO")) {
                GotoNode gotoNode = new GotoNode();
                copyCalcNodeFields(op, gotoNode);
                gotoNode.setTargetLabel(op.getFactor2() != null ? op.getFactor2().getRawText() : "");
                content.getCalculationSpecs().add(gotoNode);
                return null;
            }
            if (upper.equals("EXSR")) {
                CallSubroutine callSr = new CallSubroutine();
                copyCalcNodeFields(op, callSr);
                String srName = op.getFactor2() != null ? op.getFactor2().getRawText() : "";
                callSr.setSubroutineName(srName);
                // Track for cross-reference
                exsrCalls.computeIfAbsent(srName.trim().toUpperCase(), k -> new ArrayList<>())
                    .add(op.getLocation());
                content.getCalculationSpecs().add(callSr);
                return null;
            }
            // Track CALL for dependencies
            if (upper.equals("CALL")) {
                if (op.getFactor2() != null) {
                    String pgmName = op.getFactor2().getRawText();
                    if (pgmName != null && !pgmName.isBlank()) {
                        // Strip quotes if literal
                        String cleaned = pgmName.trim().replace("'", "");
                        dependencies.getCalledPrograms().add(cleaned);
                    }
                }
            }
        }

        content.getCalculationSpecs().add(op);
        return null;
    }

    // =========================================================================
    // Task 3.10: Control flow blocks
    // =========================================================================

    @Override
    public Void visitBlock(Rpg3Parser.BlockContext ctx) {
        currentSpecContext = "C";

        // DOUxx block
        if (ctx.csDOUxx() != null) {
            DoUntilBlock block = buildDoUntilBlock(ctx.csDOUxx());
            for (Rpg3Parser.StatementContext stmt : ctx.statement()) {
                visitStatementIntoList(stmt, block.getBodyOps());
            }
            content.getCalculationSpecs().add(block);
            return null;
        }

        // DOWxx block
        if (ctx.csDOWxx() != null) {
            DoWhileBlock block = buildDoWhileBlock(ctx.csDOWxx());
            for (Rpg3Parser.StatementContext stmt : ctx.statement()) {
                visitStatementIntoList(stmt, block.getBodyOps());
            }
            content.getCalculationSpecs().add(block);
            return null;
        }

        // DO block (factor1 = start, factor2 = end, result = index)
        if (ctx.csDO() != null) {
            DoBlock block = new DoBlock();
            block.setRawSourceLine(getRawLine(ctx));
            block.setLocation(getLocation(ctx));

            Rpg3Parser.Cspec_fixed_standard_partsContext parts = ctx.csDO().cspec_fixed_standard_parts();
            if (ctx.factor1 != null) {
                block.setStartValue(buildFactorExpression(ctx.factor1, getLocation(ctx)));
            }
            if (parts != null) {
                block.setEndValue(buildFactorExpression(parts.factor2, getLocation(ctx)));
                block.setIndexVariable(buildResultExpression(parts.result, getLocation(ctx)));
            }

            // Extract conditioning indicators from the context (all 3 positions)
            extractConditioningIndicators(block, ctx.cs_controlLevel(), ctx.indicators, ctx.indicatorsOff,
                ctx.cspec_continuedIndicators());

            for (Rpg3Parser.StatementContext stmt : ctx.statement()) {
                visitStatementIntoList(stmt, block.getBodyOps());
            }
            content.getCalculationSpecs().add(block);
            return null;
        }

        // IFxx block or CASExx block handled by child rules
        return visitChildren(ctx);
    }

    @Override
    public Void visitIfstatement(Rpg3Parser.IfstatementContext ctx) {
        currentSpecContext = "C";

        ConditionalBlock block = buildConditionalBlock(ctx.csIFxx());

        // Then operations
        List<Rpg3Parser.StatementContext> stmts = ctx.statement();
        // Statements before elsestmt go to thenOps
        boolean inElse = false;
        for (int i = 0; i < stmts.size(); i++) {
            Rpg3Parser.StatementContext stmt = stmts.get(i);
            // Check if the elsestmt appears between statement groups
            if (!inElse && ctx.elsestmt() != null && isAfterElse(stmt, ctx.elsestmt())) {
                inElse = true;
            }
            if (inElse) {
                visitStatementIntoList(stmt, block.getElseOps());
            } else {
                visitStatementIntoList(stmt, block.getThenOps());
            }
        }

        content.getCalculationSpecs().add(block);
        return null;
    }

    @Override
    public Void visitCasestatement(Rpg3Parser.CasestatementContext ctx) {
        currentSpecContext = "C";

        CaseBlock block = new CaseBlock();
        block.setRawSourceLine(getRawLine(ctx));
        block.setLocation(getLocation(ctx));

        // Grammar: (CS_FIXED ... (csCASEQ|csCASNE|...))+
        // Each iteration is a case entry
        // The grammar bundles factor1 and the CAS opcode contexts
        // Parse all CASxx children
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof Rpg3Parser.CsCASEQContext ||
                child instanceof Rpg3Parser.CsCASNEContext ||
                child instanceof Rpg3Parser.CsCASLEContext ||
                child instanceof Rpg3Parser.CsCASLTContext ||
                child instanceof Rpg3Parser.CsCASGEContext ||
                child instanceof Rpg3Parser.CsCASGTContext ||
                child instanceof Rpg3Parser.CsCASContext) {

                CaseEntry entry = buildCaseEntry(child);
                if (entry != null) {
                    block.getEntries().add(entry);
                }
            }
        }

        content.getCalculationSpecs().add(block);
        return null;
    }

    // =========================================================================
    // Task 3.10: Subroutines
    // =========================================================================

    @Override
    public Void visitSubroutine(Rpg3Parser.SubroutineContext ctx) {
        currentSpecContext = "C";

        SubroutineBlock block = new SubroutineBlock();
        block.setRawSourceLine(getRawLine(ctx));
        block.setLocation(getLocation(ctx));

        // Get subroutine name from BEGSR's factor1
        if (ctx.begin != null && ctx.begin instanceof Rpg3Parser.BegsrContext) {
            Rpg3Parser.BegsrContext begsr = (Rpg3Parser.BegsrContext) ctx.begin;
            Rpg3Parser.CsBEGSRContext begsrCtx = begsr.csBEGSR();
            if (begsrCtx != null && begsrCtx.factor1 != null) {
                String name = getFactorText(begsrCtx.factor1);
                block.setSubroutineName(name != null ? name.trim() : "");
            }
        }

        // Visit body statements
        for (Rpg3Parser.StatementContext stmt : ctx.statement()) {
            visitStatementIntoList(stmt, block.getOperations());
        }

        content.getCalculationSpecs().add(block);

        // Also add to subroutines index
        Subroutine sub = new Subroutine();
        sub.setName(block.getSubroutineName());
        sub.setLocation(block.getLocation());
        content.getSubroutines().add(sub);

        return null;
    }

    // =========================================================================
    // Task 3.12: Directive → copyMembers[]
    // =========================================================================

    @Override
    public Void visitDirective(Rpg3Parser.DirectiveContext ctx) {
        if (ctx.dir_copy() != null) {
            Rpg3Parser.Dir_copyContext copyCtx = ctx.dir_copy();
            IrDocument.CopyMemberRef ref = new IrDocument.CopyMemberRef();
            ref.setLocation(getLocation(ctx));

            // Get the full directive text
            String directiveText = ctx.getText();
            ref.setDirective(directiveText);

            // Extract member, file, library from the copy context
            if (copyCtx.member != null) {
                ref.setMemberName(getCopyText(copyCtx.member));
            }
            if (copyCtx.file != null) {
                ref.setFileName(getCopyText(copyCtx.file));
            }
            if (copyCtx.library != null) {
                ref.setLibraryName(getCopyText(copyCtx.library));
            }

            dependencies.getCopyMembers().add(ref);
        }
        if (ctx.dir_include() != null) {
            Rpg3Parser.Dir_includeContext inclCtx = ctx.dir_include();
            IrDocument.CopyMemberRef ref = new IrDocument.CopyMemberRef();
            ref.setLocation(getLocation(ctx));
            ref.setDirective(ctx.getText());

            if (inclCtx.member != null) {
                ref.setMemberName(getCopyText(inclCtx.member));
            }
            if (inclCtx.file != null) {
                ref.setFileName(getCopyText(inclCtx.file));
            }
            if (inclCtx.library != null) {
                ref.setLibraryName(getCopyText(inclCtx.library));
            }

            dependencies.getCopyMembers().add(ref);
        }
        return null;
    }

    // =========================================================================
    // Task 3.13: Compile-time data → compileTimeData
    // =========================================================================

    @Override
    public Void visitCompileTimeData(Rpg3Parser.CompileTimeDataContext ctx) {
        if (ctx.endSource() == null) return null;

        CompileTimeData ctd = new CompileTimeData();
        List<CompileTimeBlock> blocks = new ArrayList<>();

        // Get all compile-time arrays from E-specs (in source order)
        List<String> ctdArrayNames = new ArrayList<>();
        for (ExtensionSpec espec : content.getExtensionSpecs()) {
            String fromFile = espec.getFromFileName();
            // Arrays with no fromFileName get their data from compile-time data
            if (fromFile == null || fromFile.isBlank()) {
                String name = espec.getArrayOrTableName();
                if (name != null && !name.isBlank()) {
                    ctdArrayNames.add(name.trim());
                }
            }
        }

        // Collect all the end source lines
        Rpg3Parser.EndSourceContext endSrc = ctx.endSource();
        List<String> dataLines = new ArrayList<>();
        for (Rpg3Parser.EndSourceLineContext line : endSrc.endSourceLine()) {
            dataLines.add(line.EOS_Text() != null ? line.EOS_Text().getText() : "");
        }

        // For now, treat all data lines as a single block associated with
        // the first compile-time array (simple handling)
        if (!dataLines.isEmpty()) {
            int arrayIndex = 0;
            CompileTimeBlock block = new CompileTimeBlock();
            block.setArrayName(arrayIndex < ctdArrayNames.size() ? ctdArrayNames.get(arrayIndex) : null);
            block.setData(dataLines);
            block.setLocation(getLocation(ctx));
            blocks.add(block);
        }

        ctd.setBlocks(blocks);
        content.setCompileTimeData(ctd);
        return null;
    }

    // =========================================================================
    // Task 3.14: Comments
    // =========================================================================

    @Override
    public Void visitStar_comments(Rpg3Parser.Star_commentsContext ctx) {
        Comment comment = new Comment();
        comment.setLineNumber(getOriginalLine(ctx));
        comment.setSpecContext(currentSpecContext);

        if (ctx.comments() != null && ctx.comments().COMMENTS_TEXT() != null) {
            comment.setText(ctx.comments().COMMENTS_TEXT().getText());
        } else {
            comment.setText("");
        }

        content.getComments().add(comment);
        return null;
    }

    // =========================================================================
    // Helper methods
    // =========================================================================

    /** Build an Operation from a cspec_fixed context. */
    private Operation buildOperation(Rpg3Parser.Cspec_fixedContext cspecCtx,
                                     Rpg3Parser.Cspec_fixed_standardContext stdCtx) {
        Operation op = new Operation();
        op.setRawSourceLine(getRawLine(cspecCtx));
        op.setLocation(getLocation(cspecCtx));

        // Control level and conditioning indicators (all 3 positions)
        extractConditioningIndicators(op, cspecCtx.cs_controlLevel(), cspecCtx.indicators, cspecCtx.indicatorsOff,
            cspecCtx.cspec_continuedIndicators());

        // Find the opcode — it's in one of the specific rule alternatives inside cspec_fixed_standard
        op.setOpcode(extractOpcode(stdCtx));
        op.setExtendedOpcode(extractExtendedOpcode(stdCtx));

        // Find cspec_fixed_standard_parts (contains factor2, result, len, decimal, indicators)
        Rpg3Parser.Cspec_fixed_standard_partsContext parts = findStandardParts(stdCtx);
        if (parts != null) {
            Location loc = getLocation(cspecCtx);
            op.setFactor2(buildFactorExpression(parts.factor2, loc));
            op.setResultField(buildResultExpression(parts.result, loc));
            op.setFieldLength(safeInteger(parts.len));
            op.setDecimalPositions(safeInteger(parts.decimalPositions));

            // Resulting indicators
            ResultingIndicators ri = new ResultingIndicators();
            ri.setHigh(safeIndicatorText(parts.hi));
            ri.setLow(safeIndicatorText(parts.lo));
            ri.setEqual(safeIndicatorText(parts.eq));
            op.setResultingIndicators(ri);

            // Inline comment
            if (parts.cs_fixed_comments() != null) {
                op.setInlineComment(parts.cs_fixed_comments().getText().trim());
            }
        }

        // Factor1 from the cspec_fixed context
        if (cspecCtx.factor1 != null) {
            op.setFactor1(buildFactorExpression(cspecCtx.factor1, getLocation(cspecCtx)));
        }

        return op;
    }

    /** Build a ConditionalBlock from an IFxx context. (Task 3.10 + 3.11) */
    private ConditionalBlock buildConditionalBlock(Rpg3Parser.CsIFxxContext ctx) {
        ConditionalBlock block = new ConditionalBlock();
        block.setRawSourceLine(getRawLine(ctx));
        block.setLocation(getLocation(ctx));

        // Get the comparison type from the opcode
        String compType = extractComparisonType(ctx);
        Location loc = getLocation(ctx);

        // Build initial condition from factor1 and factor2
        ExpressionNode factor1 = buildFactorExpression(ctx.factor1, loc);
        ExpressionNode factor2 = null;

        // Find the factor2 from the inner opcode rule's standard_parts
        Rpg3Parser.Cspec_fixed_standard_partsContext parts = findPartsInIFxx(ctx);
        if (parts != null) {
            factor2 = buildFactorExpression(parts.factor2, loc);

            ResultingIndicators ri = new ResultingIndicators();
            ri.setHigh(safeIndicatorText(parts.hi));
            ri.setLow(safeIndicatorText(parts.lo));
            ri.setEqual(safeIndicatorText(parts.eq));
        }

        // Build condition expression
        ExpressionNode condition = new BinaryOpNode(compType, factor1, factor2);

        // Task 3.11: Handle ANDxx/ORxx compound conditions
        List<Rpg3Parser.CsANDxxContext> andConds = ctx.csANDxx();
        List<Rpg3Parser.CsORxxContext> orConds = ctx.csORxx();

        if (andConds != null && !andConds.isEmpty()) {
            for (Rpg3Parser.CsANDxxContext andCtx : andConds) {
                String andType = extractComparisonType(andCtx);
                ExpressionNode andF1 = buildFactorExpression(andCtx.factor1, loc);
                ExpressionNode andF2 = buildAndOrFactor2(andCtx);
                ExpressionNode andCond = new BinaryOpNode(andType, andF1, andF2);
                condition = new BinaryOpNode("AND", condition, andCond);
            }
        }
        if (orConds != null && !orConds.isEmpty()) {
            for (Rpg3Parser.CsORxxContext orCtx : orConds) {
                String orType = extractComparisonType(orCtx);
                ExpressionNode orF1 = buildFactorExpression(orCtx.factor1, loc);
                ExpressionNode orF2 = buildAndOrFactor2(orCtx);
                ExpressionNode orCond = new BinaryOpNode(orType, orF1, orF2);
                condition = new BinaryOpNode("OR", condition, orCond);
            }
        }

        boolean isCompound = (andConds != null && !andConds.isEmpty())
                          || (orConds != null && !orConds.isEmpty());

        if (isCompound) {
            block.setCondition(condition);
            block.setComparisonType(null);
            block.setComparisonValue(null);
        } else {
            block.setCondition(factor1);
            block.setComparisonType(compType);
            block.setComparisonValue(factor2);
        }

        // Extract conditioning indicators (all 3 positions)
        extractConditioningIndicators(block, ctx.cs_controlLevel(), ctx.indicators, ctx.indicatorsOff,
            ctx.cspec_continuedIndicators());

        return block;
    }

    /** Build a DoWhileBlock from a DOWxx context. */
    private DoWhileBlock buildDoWhileBlock(Rpg3Parser.CsDOWxxContext ctx) {
        DoWhileBlock block = new DoWhileBlock();
        block.setRawSourceLine(getRawLine(ctx));
        block.setLocation(getLocation(ctx));

        String compType = extractComparisonType(ctx);
        Location loc = getLocation(ctx);

        ExpressionNode factor1 = buildFactorExpression(ctx.factor1, loc);
        ExpressionNode factor2 = buildDOWDOUFactor2(ctx);

        // Handle ANDxx/ORxx
        ExpressionNode condition = new BinaryOpNode(compType, factor1, factor2);
        boolean isCompound = false;
        List<Rpg3Parser.CsANDxxContext> dowAndConds = ctx.csANDxx();
        List<Rpg3Parser.CsORxxContext> dowOrConds = ctx.csORxx();

        if (dowAndConds != null && !dowAndConds.isEmpty()) {
            isCompound = true;
            for (Rpg3Parser.CsANDxxContext andCtx : dowAndConds) {
                String andType = extractComparisonType(andCtx);
                ExpressionNode andF1 = buildFactorExpression(andCtx.factor1, loc);
                ExpressionNode andF2 = buildAndOrFactor2(andCtx);
                condition = new BinaryOpNode("AND", condition, new BinaryOpNode(andType, andF1, andF2));
            }
        }
        if (dowOrConds != null && !dowOrConds.isEmpty()) {
            isCompound = true;
            for (Rpg3Parser.CsORxxContext orCtx : dowOrConds) {
                String orType = extractComparisonType(orCtx);
                ExpressionNode orF1 = buildFactorExpression(orCtx.factor1, loc);
                ExpressionNode orF2 = buildAndOrFactor2(orCtx);
                condition = new BinaryOpNode("OR", condition, new BinaryOpNode(orType, orF1, orF2));
            }
        }

        if (isCompound) {
            block.setCondition(condition);
            block.setComparisonType(null);
            block.setComparisonValue(null);
        } else {
            block.setCondition(factor1);
            block.setComparisonType(compType);
            block.setComparisonValue(factor2);
        }

        extractConditioningIndicators(block, ctx.cs_controlLevel(), ctx.indicators, ctx.indicatorsOff,
            ctx.cspec_continuedIndicators());
        return block;
    }

    /** Build a DoUntilBlock from a DOUxx context. */
    private DoUntilBlock buildDoUntilBlock(Rpg3Parser.CsDOUxxContext ctx) {
        DoUntilBlock block = new DoUntilBlock();
        block.setRawSourceLine(getRawLine(ctx));
        block.setLocation(getLocation(ctx));

        String compType = extractComparisonType(ctx);
        Location loc = getLocation(ctx);

        ExpressionNode factor1 = buildFactorExpression(ctx.factor1, loc);
        ExpressionNode factor2 = buildDOUDOWFactor2(ctx);

        // Handle ANDxx/ORxx
        ExpressionNode condition = new BinaryOpNode(compType, factor1, factor2);
        boolean isCompound = false;
        List<Rpg3Parser.CsANDxxContext> douAndConds = ctx.csANDxx();
        List<Rpg3Parser.CsORxxContext> douOrConds = ctx.csORxx();

        if (douAndConds != null && !douAndConds.isEmpty()) {
            isCompound = true;
            for (Rpg3Parser.CsANDxxContext andCtx : douAndConds) {
                String andType = extractComparisonType(andCtx);
                ExpressionNode andF1 = buildFactorExpression(andCtx.factor1, loc);
                ExpressionNode andF2 = buildAndOrFactor2(andCtx);
                condition = new BinaryOpNode("AND", condition, new BinaryOpNode(andType, andF1, andF2));
            }
        }
        if (douOrConds != null && !douOrConds.isEmpty()) {
            isCompound = true;
            for (Rpg3Parser.CsORxxContext orCtx : douOrConds) {
                String orType = extractComparisonType(orCtx);
                ExpressionNode orF1 = buildFactorExpression(orCtx.factor1, loc);
                ExpressionNode orF2 = buildAndOrFactor2(orCtx);
                condition = new BinaryOpNode("OR", condition, new BinaryOpNode(orType, orF1, orF2));
            }
        }

        if (isCompound) {
            block.setCondition(condition);
            block.setComparisonType(null);
            block.setComparisonValue(null);
        } else {
            block.setCondition(factor1);
            block.setComparisonType(compType);
            block.setComparisonValue(factor2);
        }

        extractConditioningIndicators(block, ctx.cs_controlLevel(), ctx.indicators, ctx.indicatorsOff,
            ctx.cspec_continuedIndicators());
        return block;
    }

    /** Build a CaseEntry from a CASxx context. */
    private CaseEntry buildCaseEntry(ParseTree child) {
        CaseEntry entry = new CaseEntry();
        entry.setLocation(getLocation(child));

        // The child is one of: csCASEQ, csCASNE, csCASLE, csCASLT, csCASGE, csCASGT, csCAS
        String compType = null;
        Rpg3Parser.Cspec_fixed_standard_partsContext parts = null;

        if (child instanceof Rpg3Parser.CsCASEQContext) { compType = "EQ"; parts = ((Rpg3Parser.CsCASEQContext)child).cspec_fixed_standard_parts(); }
        else if (child instanceof Rpg3Parser.CsCASNEContext) { compType = "NE"; parts = ((Rpg3Parser.CsCASNEContext)child).cspec_fixed_standard_parts(); }
        else if (child instanceof Rpg3Parser.CsCASLEContext) { compType = "LE"; parts = ((Rpg3Parser.CsCASLEContext)child).cspec_fixed_standard_parts(); }
        else if (child instanceof Rpg3Parser.CsCASLTContext) { compType = "LT"; parts = ((Rpg3Parser.CsCASLTContext)child).cspec_fixed_standard_parts(); }
        else if (child instanceof Rpg3Parser.CsCASGEContext) { compType = "GE"; parts = ((Rpg3Parser.CsCASGEContext)child).cspec_fixed_standard_parts(); }
        else if (child instanceof Rpg3Parser.CsCASGTContext) { compType = "GT"; parts = ((Rpg3Parser.CsCASGTContext)child).cspec_fixed_standard_parts(); }
        else if (child instanceof Rpg3Parser.CsCASContext) { compType = null; parts = ((Rpg3Parser.CsCASContext)child).cspec_fixed_standard_parts(); }
        else return null;

        entry.setComparisonType(compType);

        if (parts != null) {
            Location loc = getLocation(child);
            entry.setComparisonValue(buildFactorExpression(parts.factor2, loc));
            entry.setSubroutineName(buildResultExpression(parts.result, loc));

            ResultingIndicators ri = new ResultingIndicators();
            ri.setHigh(safeIndicatorText(parts.hi));
            ri.setLow(safeIndicatorText(parts.lo));
            ri.setEqual(safeIndicatorText(parts.eq));
            entry.setResultingIndicators(ri);
        }

        return entry;
    }

    /**
     * Visit a statement and add the resulting calc node(s) to a target list.
     * This is used for nested block bodies (thenOps, elseOps, bodyOps, etc.).
     */
    private void visitStatementIntoList(Rpg3Parser.StatementContext stmt, List<Object> targetList) {
        // Save current calc specs, visit, then capture new entries
        List<Object> currentSpecs = content.getCalculationSpecs();
        List<Object> tempList = new ArrayList<>();
        content.setCalculationSpecs(tempList);

        visit(stmt);

        // Move any generated items to the target list
        targetList.addAll(tempList);

        // Restore
        content.setCalculationSpecs(currentSpecs);
    }

    /** Extract the opcode string from a cspec_fixed_standard context. */
    private String extractOpcode(Rpg3Parser.Cspec_fixed_standardContext ctx) {
        // The opcode is in one of the child rules (csACQ, csADD, etc.)
        // Each has an 'operation' label
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof ParserRuleContext) {
                ParserRuleContext rule = (ParserRuleContext) child;
                // Look for the 'operation' token
                try {
                    var field = rule.getClass().getField("operation");
                    Object token = field.get(rule);
                    if (token instanceof org.antlr.v4.runtime.Token) {
                        return ((org.antlr.v4.runtime.Token) token).getText().trim();
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    // Not all rules have 'operation'
                }
            }
        }

        // Fallback: check CS_OperationAndExtender
        if (ctx.CS_OperationAndExtender() != null) {
            return ctx.CS_OperationAndExtender().getText().trim();
        }

        return null;
    }

    /** Extract extended opcode (operation extender). */
    private String extractExtendedOpcode(Rpg3Parser.Cspec_fixed_standardContext ctx) {
        if (ctx.cs_operationExtender() != null) {
            var ext = ctx.cs_operationExtender();
            if (ext.extender != null) {
                return ext.extender.getText().trim();
            }
        }
        // Check child rules
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof ParserRuleContext) {
                ParserRuleContext rule = (ParserRuleContext) child;
                try {
                    var field = rule.getClass().getField("operationExtender");
                    Object extObj = field.get(rule);
                    if (extObj instanceof Rpg3Parser.Cs_operationExtenderContext) {
                        var ext = (Rpg3Parser.Cs_operationExtenderContext) extObj;
                        if (ext.extender != null) {
                            return ext.extender.getText().trim();
                        }
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    // ignore
                }
            }
        }
        return "";
    }

    /** Find cspec_fixed_standard_parts from a cspec_fixed_standard context. */
    private Rpg3Parser.Cspec_fixed_standard_partsContext findStandardParts(
            Rpg3Parser.Cspec_fixed_standardContext ctx) {
        // It's in one of the child rule alternatives
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof Rpg3Parser.Cspec_fixed_standard_partsContext) {
                return (Rpg3Parser.Cspec_fixed_standard_partsContext) child;
            }
            if (child instanceof ParserRuleContext) {
                // Search recursively in the child
                for (int j = 0; j < child.getChildCount(); j++) {
                    ParseTree grandchild = child.getChild(j);
                    if (grandchild instanceof Rpg3Parser.Cspec_fixed_standard_partsContext) {
                        return (Rpg3Parser.Cspec_fixed_standard_partsContext) grandchild;
                    }
                }
            }
        }
        return null;
    }

    /** Find standard_parts inside an IFxx rule. */
    private Rpg3Parser.Cspec_fixed_standard_partsContext findPartsInIFxx(Rpg3Parser.CsIFxxContext ctx) {
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof ParserRuleContext) {
                ParserRuleContext rule = (ParserRuleContext) child;
                for (int j = 0; j < rule.getChildCount(); j++) {
                    if (rule.getChild(j) instanceof Rpg3Parser.Cspec_fixed_standard_partsContext) {
                        return (Rpg3Parser.Cspec_fixed_standard_partsContext) rule.getChild(j);
                    }
                }
            }
        }
        return null;
    }

    /** Extract comparison type from an IFxx/DOWxx/DOUxx/ANDxx/ORxx context. */
    private String extractComparisonType(ParserRuleContext ctx) {
        // Extract xx from CsIFxxContext → look at children for csIFEQ, csIFGT, etc.
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            String childName = child.getClass().getSimpleName();
            if (childName.contains("EQ")) return "EQ";
            if (childName.contains("NE")) return "NE";
            if (childName.contains("LE")) return "LE";
            if (childName.contains("LT")) return "LT";
            if (childName.contains("GE")) return "GE";
            if (childName.contains("GT")) return "GT";
        }
        return null;
    }

    /** Build factor2 for DOW/DOU from their inner opcode rule. */
    private ExpressionNode buildDOWDOUFactor2(Rpg3Parser.CsDOWxxContext ctx) {
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof ParserRuleContext) {
                for (int j = 0; j < child.getChildCount(); j++) {
                    if (child.getChild(j) instanceof Rpg3Parser.Cspec_fixed_standard_partsContext) {
                        Rpg3Parser.Cspec_fixed_standard_partsContext parts =
                            (Rpg3Parser.Cspec_fixed_standard_partsContext) child.getChild(j);
                        return buildFactorExpression(parts.factor2, getLocation(ctx));
                    }
                }
            }
        }
        return null;
    }

    private ExpressionNode buildDOUDOWFactor2(Rpg3Parser.CsDOUxxContext ctx) {
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof ParserRuleContext) {
                for (int j = 0; j < child.getChildCount(); j++) {
                    if (child.getChild(j) instanceof Rpg3Parser.Cspec_fixed_standard_partsContext) {
                        Rpg3Parser.Cspec_fixed_standard_partsContext parts =
                            (Rpg3Parser.Cspec_fixed_standard_partsContext) child.getChild(j);
                        return buildFactorExpression(parts.factor2, getLocation(ctx));
                    }
                }
            }
        }
        return null;
    }

    /** Build factor2 for ANDxx/ORxx from their standard_parts. */
    private ExpressionNode buildAndOrFactor2(ParserRuleContext ctx) {
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof ParserRuleContext) {
                for (int j = 0; j < child.getChildCount(); j++) {
                    if (child.getChild(j) instanceof Rpg3Parser.Cspec_fixed_standard_partsContext) {
                        Rpg3Parser.Cspec_fixed_standard_partsContext parts =
                            (Rpg3Parser.Cspec_fixed_standard_partsContext) child.getChild(j);
                        return buildFactorExpression(parts.factor2, getLocation(ctx));
                    }
                }
            }
        }
        return null;
    }

    /** Build an ExpressionNode from a factor context. */
    private ExpressionNode buildFactorExpression(Rpg3Parser.FactorContext factorCtx, Location loc) {
        if (factorCtx == null) return null;
        String text = getFactorText(factorCtx);
        return ExpressionBuilder.build(text, loc);
    }

    /** Build an ExpressionNode from a resultType context. */
    private ExpressionNode buildResultExpression(Rpg3Parser.ResultTypeContext resultCtx, Location loc) {
        if (resultCtx == null) return null;
        String text = resultCtx.getText();
        if (text == null || text.isBlank()) return null;
        return ExpressionBuilder.build(text.trim(), loc);
    }

    /** Get text from a factor context. */
    private String getFactorText(Rpg3Parser.FactorContext ctx) {
        if (ctx == null) return null;
        if (ctx.content != null) {
            return ctx.content.getText();
        }
        if (ctx.constant != null) {
            return ctx.constant.getText();
        }
        if (ctx.CS_BlankFactor() != null) {
            return "";
        }
        return ctx.getText();
    }

    /** Extract conditioning indicators from control level and indicator contexts.
     *  Handles up to 3 conditioning indicator positions per the RPG3 spec:
     *  - Position "first" from the main indicators context (cols 9-11)
     *  - Positions "second"/"third" from cspec_continuedIndicators (cols 12-14, 15-17)
     */
    private void extractConditioningIndicators(CalcNode node,
            Rpg3Parser.Cs_controlLevelContext ctrlLevel,
            Rpg3Parser.Cs_indicatorsContext indicators,
            Rpg3Parser.OnOffIndicatorsFlagContext indicatorsOff,
            List<Rpg3Parser.Cspec_continuedIndicatorsContext> continuedIndicators) {

        if (ctrlLevel != null) {
            String level = ctrlLevel.getText().trim();
            if (!level.isEmpty()) {
                node.setControlLevel(level);
            }
        }

        // Position "first" — main line indicators (cols 9-11)
        addIndicatorIfPresent(node, indicators, indicatorsOff, "first");

        // Positions "second", "third" — from cspec_continuedIndicators
        if (continuedIndicators != null) {
            String[] positionNames = {"second", "third", "fourth"};
            for (int i = 0; i < continuedIndicators.size() && i < positionNames.length; i++) {
                Rpg3Parser.Cspec_continuedIndicatorsContext contCtx = continuedIndicators.get(i);
                addIndicatorIfPresent(node, contCtx.indicators, contCtx.indicatorsOff, positionNames[i]);
            }
        }
    }

    /** Add a single conditioning indicator to the node if not blank. */
    private void addIndicatorIfPresent(CalcNode node,
            Rpg3Parser.Cs_indicatorsContext indicators,
            Rpg3Parser.OnOffIndicatorsFlagContext indicatorsOff,
            String position) {
        if (indicators != null) {
            String indText = indicators.getText().trim();
            if (!indText.isEmpty()) {
                boolean negated = false;
                if (indicatorsOff != null && indicatorsOff.NoFlag() != null) {
                    negated = true;
                }
                node.getConditioningIndicators().add(
                    new ConditioningIndicator(negated, indText, position)
                );
            }
        }
    }

    /** Get the raw line from the normalized source. */
    private String getRawLine(ParserRuleContext ctx) {
        int line = ctx.getStart().getLine();
        if (line > 0 && line <= normalizedSource.getLineCount()) {
            return normalizedSource.getLines()[line - 1];
        }
        return ctx.getText();
    }

    private String getRawLine(ParseTree tree) {
        if (tree instanceof ParserRuleContext) {
            return getRawLine((ParserRuleContext) tree);
        }
        return tree.getText();
    }

    /** Get the original line number for a context. */
    private int getOriginalLine(ParserRuleContext ctx) {
        int line = ctx.getStart().getLine();
        int[] mapping = normalizedSource.getOriginalLineNumbers();
        if (line > 0 && line <= mapping.length) {
            return mapping[line - 1];
        }
        return line;
    }

    /** Create a Location from a parse context. */
    private Location getLocation(ParserRuleContext ctx) {
        int startLine = getOriginalLine(ctx);
        int endLine = startLine;
        if (ctx.getStop() != null) {
            int stopLine = ctx.getStop().getLine();
            int[] mapping = normalizedSource.getOriginalLineNumbers();
            if (stopLine > 0 && stopLine <= mapping.length) {
                endLine = mapping[stopLine - 1];
            }
        }
        return new Location(startLine, endLine, 1, 80);
    }

    private Location getLocation(ParseTree tree) {
        if (tree instanceof ParserRuleContext) {
            return getLocation((ParserRuleContext) tree);
        }
        return Location.ofLine(1);
    }

    /** Copy common CalcNode fields from source to target. */
    private void copyCalcNodeFields(CalcNode source, CalcNode target) {
        target.setRawSourceLine(source.getRawSourceLine());
        target.setLocation(source.getLocation());
        target.setControlLevel(source.getControlLevel());
        target.setConditioningIndicators(source.getConditioningIndicators());
        target.setInlineComment(source.getInlineComment());
    }

    /** Safely get trimmed text from a terminal node. */
    private String safeText(TerminalNode node) {
        if (node == null) return null;
        String text = node.getText();
        return text != null ? text.trim() : null;
    }

    private String safeText(org.antlr.v4.runtime.Token token) {
        if (token == null) return null;
        return token.getText().trim();
    }

    /** Safely parse an integer from a terminal node. */
    private Integer safeInteger(TerminalNode node) {
        String text = safeText(node);
        if (text == null || text.isBlank()) return null;
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer safeInteger(org.antlr.v4.runtime.Token token) {
        if (token == null) return null;
        String text = token.getText().trim();
        if (text.isBlank()) return null;
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Get indicator text from a resultIndicator context. */
    private String safeIndicatorText(Rpg3Parser.ResultIndicatorContext ctx) {
        if (ctx == null) return "";
        String text = ctx.getText().trim();
        return text.isEmpty() ? "" : text;
    }

    /** Check if a statement comes after the ELSE in the context. */
    private boolean isAfterElse(Rpg3Parser.StatementContext stmt, Rpg3Parser.ElsestmtContext elseCtx) {
        return stmt.getStart().getTokenIndex() > elseCtx.getStop().getTokenIndex();
    }

    /** Get text from copyText context. */
    private String getCopyText(Rpg3Parser.CopyTextContext ctx) {
        if (ctx == null) return null;
        return ctx.getText().trim();
    }

    /** Parse field positions from I-spec IFD_FIELD_LOCATION. */
    private Integer parseFieldPosition(String fieldLoc, boolean isFrom) {
        if (fieldLoc == null || fieldLoc.isBlank()) return null;
        // Field location is typically a numeric value
        String trimmed = fieldLoc.trim();
        try {
            return Integer.parseInt(trimmed);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // =========================================================================
    // Task 3.14: Build sourceLines[]
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

            // Determine spec type from column 6
            String line = lines[i];
            if (line.length() >= 6) {
                char specChar = Character.toUpperCase(line.charAt(5));
                switch (specChar) {
                    case 'H': sl.setSpecType("H"); break;
                    case 'F': sl.setSpecType("F"); break;
                    case 'E': sl.setSpecType("E"); break;
                    case 'L': sl.setSpecType("L"); break;
                    case 'I': sl.setSpecType("I"); break;
                    case 'C': sl.setSpecType("C"); break;
                    case 'O': sl.setSpecType("O"); break;
                    case '*': sl.setSpecType("comment"); sl.setComment(true); break;
                    default: sl.setSpecType(null); break;
                }
            }

            // Check if blank
            sl.setBlank(line.trim().isEmpty());

            sourceLines.add(sl);
        }
        document.setSourceLines(sourceLines);
    }

    /** Populate subroutine calledFrom cross-references. */
    private void populateSubroutineCalledFrom() {
        for (Subroutine sub : content.getSubroutines()) {
            String name = sub.getName();
            if (name != null) {
                List<Location> calls = exsrCalls.get(name.trim().toUpperCase());
                if (calls != null) {
                    List<Integer> lineNumbers = new ArrayList<>();
                    for (Location loc : calls) {
                        lineNumbers.add(loc.getStartLine());
                    }
                    sub.setCalledFrom(lineNumbers);
                }
            }
        }
    }
}
