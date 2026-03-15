package com.as400parser.rpg3;

import com.as400parser.common.model.IrDocument;
import com.as400parser.common.model.SourceLine;
import com.as400parser.common.normalizer.NormalizedSource;
import com.as400parser.common.normalizer.SourceNormalizer;
import com.as400parser.rpg3.model.*;
import com.as400parser.rpg3.model.CalcSpec.*;
import com.as400parser.rpg3.model.Rpg3Content.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive unit tests for {@link Rpg3IrBuilder}.
 * Organized by spec type and feature area per the testing plan
 * in docs/ai/testing/feature-rpg3-parser.md.
 */
class Rpg3IrBuilderTest {

    /** Parse source and return the IR document. */
    private IrDocument parse(String source) {
        SourceNormalizer normalizer = new SourceNormalizer();
        NormalizedSource normalized = normalizer.normalize(source);
        Rpg3IrBuilder builder = new Rpg3IrBuilder(normalized);
        return builder.build();
    }

    private Rpg3Content content(IrDocument doc) {
        return (Rpg3Content) doc.getContent();
    }

    // =========================================================================
    // H-spec
    // =========================================================================

    @Nested
    class HSpec {
        @Test
        void headerSpecParsed() {
            String source = "     H                                                                          ";
            IrDocument doc = parse(source);
            assertThat(content(doc).getHeaderSpecs()).hasSize(1);
        }
    }

    // =========================================================================
    // F-spec
    // =========================================================================

    @Nested
    class FSpec {
        @Test
        void fileSpecParsedWithDependency() {
            String source = "     FCUSTMAST IF  E           K        DISK                                    ";
            IrDocument doc = parse(source);
            Rpg3Content c = content(doc);
            assertThat(c.getFileSpecs()).hasSize(1);
            FileSpec fs = c.getFileSpecs().get(0);
            assertThat(fs.getFileName()).startsWith("CUSTMAST");
            assertThat(doc.getDependencies().getReferencedFiles()).isNotEmpty();
        }

        @Test
        void multipleFileSpecs() {
            String source = String.join("\n",
                "     FCUSTMAST IF  E           K        DISK                                    ",
                "     FCUSTPRT  O   F     132            PRINTER                                 ");
            IrDocument doc = parse(source);
            assertThat(content(doc).getFileSpecs()).as("F-specs").hasSizeGreaterThanOrEqualTo(1);
            assertThat(content(doc).getFileSpecs().get(0).getFileName()).startsWith("CUSTMAST");
        }

        @Test
        void fspecCreatesDependencyRef() {
            // Correct RPG3 layout: CUSTMAST(8)I(1) at cols 7-15
            String source = "     FCUSTMASTI   E           K        DISK                                    ";
            IrDocument doc = parse(source);
            var refs = doc.getDependencies().getReferencedFiles();
            assertThat(refs).hasSize(1);
            IrDocument.DependencyRef ref = refs.get(0);
            assertThat(ref.getName()).isEqualTo("CUSTMAST");
            assertThat(ref.getReferenceType()).isEqualTo("input");
            assertThat(ref.getLocations()).isNotEmpty();
        }

        @Test
        void outputFileDependencyRef() {
            // Correct RPG3 layout: CUSTPRT(7+space)O at col 15
            String source = "     FCUSTPRT O   F     132            PRINTER                                 ";
            IrDocument doc = parse(source);
            var refs = doc.getDependencies().getReferencedFiles();
            assertThat(refs).isNotEmpty();
            assertThat(refs.get(0).getReferenceType()).isEqualTo("output");
        }
    }

    // =========================================================================
    // E-spec
    // =========================================================================

    @Nested
    class ESpec {
        @Test
        void extensionSpecParsed() {
            String source = "     E                    NAMES  10  20  8                                      ";
            IrDocument doc = parse(source);
            assertThat(content(doc).getExtensionSpecs()).hasSize(1);
        }

        @Test
        void extensionSpecFieldsExtracted() {
            String source = "     E                    NAMES  10  20  8                                      ";
            IrDocument doc = parse(source);
            ExtensionSpec es = content(doc).getExtensionSpecs().get(0);
            assertThat(es.getArrayOrTableName()).isNotNull();
            assertThat(es.getArrayOrTableName().trim()).startsWith("NAMES");
        }
    }

    // =========================================================================
    // I-spec
    // =========================================================================

    @Nested
    class ISpec {
        @Test
        void ispecRecordIdentificationParsed() {
            //        cols: 123456789012345678901234567890...
            String source = "     ICUSTMAST                                                                   ";
            IrDocument doc = parse(source);
            Rpg3Content c = content(doc);
            assertThat(c.getInputSpecs()).isNotEmpty();
            InputSpec is = c.getInputSpecs().get(0);
            assertThat(is.getSpecLevel()).isEqualTo("recordIdentification");
            assertThat(is.getFileName()).isEqualTo("CUSTMAST");
        }

        @Test
        void ispecFieldDefinitionParsed() {
            // Field definition line: blank identifier, field area in cols 43-58
            //                 1234567890123456789012345678901234567890123456789012345678901234567890
            String source = "     I                                         1   5 CUSTNO                      ";
            IrDocument doc = parse(source);
            Rpg3Content c = content(doc);
            assertThat(c.getInputSpecs()).isNotEmpty();
            InputSpec is = c.getInputSpecs().get(0);
            assertThat(is.getSpecLevel()).isEqualTo("fieldDefinition");
            assertThat(is.getFieldName()).isNotNull();
        }

        @Test
        void ispecDataStructureDetected() {
            // DS keyword at cols 19-20 (0-indexed 18-19)
            String source = "     I            DS                                                             ";
            IrDocument doc = parse(source);
            Rpg3Content c = content(doc);
            assertThat(c.getInputSpecs()).isNotEmpty();
            assertThat(c.getInputSpecs().get(0).getOption()).isEqualTo("DS");
            // Also creates DataStructure entry
            assertThat(c.getDataStructures()).isNotEmpty();
            DataStructure ds = c.getDataStructures().get(0);
            assertThat(ds.getType()).isEqualTo("dataStructure");
        }

        @Test
        void ispecSdsDetected() {
            String source = "     I            SDS                                                            ";
            IrDocument doc = parse(source);
            Rpg3Content c = content(doc);
            assertThat(c.getInputSpecs()).isNotEmpty();
            assertThat(c.getInputSpecs().get(0).getOption()).isEqualTo("SDS");
            assertThat(c.getDataStructures()).isNotEmpty();
        }
    }

    // =========================================================================
    // O-spec
    // =========================================================================

    @Nested
    class OSpec {
        @Test
        void ospecRecordLevelParsed() {
            // Record-level O-spec: file name in cols 7-14, type in col 15
            String source = "     OCUSTPRT H                                                                  ";
            IrDocument doc = parse(source);
            Rpg3Content c = content(doc);
            assertThat(c.getOutputSpecs()).isNotEmpty();
            OutputSpec os = c.getOutputSpecs().get(0);
            assertThat(os.getSpecLevel()).isEqualTo("recordLevel");
            assertThat(os.getFileName()).isEqualTo("CUSTPRT");
            assertThat(os.getType()).isEqualTo("H");
        }

        @Test
        void ospecFieldLevelParsed() {
            // Field-level O-spec: blank cols 7-22, field name in cols 23+
            String source = "     O                      CUSTNO                                               ";
            IrDocument doc = parse(source);
            Rpg3Content c = content(doc);
            assertThat(c.getOutputSpecs()).isNotEmpty();
            OutputSpec os = c.getOutputSpecs().get(0);
            assertThat(os.getSpecLevel()).isEqualTo("fieldLevel");
            assertThat(os.getFieldName()).isNotNull();
        }
    }

    // =========================================================================
    // C-spec — Simple Operations
    // =========================================================================

    @Nested
    class CSpecOperations {
        @Test
        void simpleOperationParsed() {
            // SETON operation with resulting indicator LR
            //            1234567890123456789012345678901234567890123456789012345678901234567890
            String source = "     C                     SETON                     LR                         ";
            IrDocument doc = parse(source);
            Rpg3Content c = content(doc);
            assertThat(c.getCalculationSpecs()).isNotEmpty();
            Object first = c.getCalculationSpecs().get(0);
            assertThat(first).isInstanceOf(Operation.class);
            Operation op = (Operation) first;
            assertThat(op.getOpcode().trim()).isEqualTo("SETON");
        }

        @Test
        void operationWithFactorsAndResult() {
            // MOVEL with factor2 and result
            //            1234567890123456789012345678901234567890123456789012345678901234567890
            String source = "     C                     MOVELCUSTNM   DSPNAM 25                              ";
            IrDocument doc = parse(source);
            Rpg3Content c = content(doc);
            assertThat(c.getCalculationSpecs()).isNotEmpty();
            Operation op = (Operation) c.getCalculationSpecs().get(0);
            assertThat(op.getOpcode().trim()).isEqualTo("MOVEL");
            assertThat(op.getFactor2()).isNotNull();
            assertThat(op.getResultField()).isNotNull();
        }

        @Test
        void callPopulatesCalledPrograms() {
            //            1234567890123456789012345678901234567890123456789012345678901234567890
            String source = "     C                     CALL 'STUPRG'                                        ";
            IrDocument doc = parse(source);
            var calledPrograms = doc.getDependencies().getCalledPrograms();
            assertThat(calledPrograms).isNotEmpty();
            assertThat(calledPrograms.get(0).getName()).isEqualTo("STUPRG");
            assertThat(calledPrograms.get(0).getReferenceType()).isEqualTo("call");
        }
    }

    // =========================================================================
    // C-spec — Control Flow Blocks
    // =========================================================================

    @Nested
    class CSpecControlFlow {
        @Test
        void ifEndifBlock() {
            String source = String.join("\n",
                "     C           X         IFEQ '1'                                            ",
                "     C                     SETON                     LR                         ",
                "     C                     END                                                  ");
            IrDocument doc = parse(source);
            Rpg3Content c = content(doc);
            assertThat(c.getCalculationSpecs()).isNotEmpty();
            Object block = c.getCalculationSpecs().get(0);
            assertThat(block).isInstanceOf(ConditionalBlock.class);
            ConditionalBlock cb = (ConditionalBlock) block;
            assertThat(cb.getComparisonType()).isEqualTo("EQ");
            assertThat(cb.getThenOps()).isNotEmpty();
        }

        @Test
        void ifElseEndifBlock() {
            String source = String.join("\n",
                "     C           X         IFEQ '1'                                            ",
                "     C                     SETON                     LR                         ",
                "     C                     ELSE                                                 ",
                "     C                     SETOF                     LR                         ",
                "     C                     END                                                  ");
            IrDocument doc = parse(source);
            ConditionalBlock cb = (ConditionalBlock) content(doc).getCalculationSpecs().get(0);
            assertThat(cb.getThenOps()).isNotEmpty();
            assertThat(cb.getElseOps()).isNotEmpty();
        }

        @Test
        void dowBlock() {
            String source = String.join("\n",
                "     C           X         DOWEQ'1'                                            ",
                "     C                     SETON                     LR                         ",
                "     C                     END                                                  ");
            IrDocument doc = parse(source);
            Object block = content(doc).getCalculationSpecs().get(0);
            assertThat(block).isInstanceOf(DoWhileBlock.class);
            DoWhileBlock dwb = (DoWhileBlock) block;
            assertThat(dwb.getComparisonType()).isEqualTo("EQ");
            assertThat(dwb.getBodyOps()).isNotEmpty();
        }

        @Test
        void douBlock() {
            String source = String.join("\n",
                "     C           X         DOUEQ'1'                                            ",
                "     C                     SETON                     LR                         ",
                "     C                     END                                                  ");
            IrDocument doc = parse(source);
            Object block = content(doc).getCalculationSpecs().get(0);
            assertThat(block).isInstanceOf(DoUntilBlock.class);
            DoUntilBlock dub = (DoUntilBlock) block;
            assertThat(dub.getComparisonType()).isEqualTo("EQ");
            assertThat(dub.getBodyOps()).isNotEmpty();
        }

        @Test
        void doBlock() {
            String source = String.join("\n",
                "     C           1         DO   10        X                                     ",
                "     C                     SETON                     LR                         ",
                "     C                     END                                                  ");
            IrDocument doc = parse(source);
            Object block = content(doc).getCalculationSpecs().get(0);
            assertThat(block).isInstanceOf(DoBlock.class);
            DoBlock db = (DoBlock) block;
            assertThat(db.getBodyOps()).isNotEmpty();
        }

        @Test
        void subroutineBlock() {
            String source = String.join("\n",
                "     C           SR01      BEGSR                                                ",
                "     C                     SETON                     LR                         ",
                "     C                     ENDSR                                                ");
            IrDocument doc = parse(source);
            Rpg3Content c = content(doc);
            // SubroutineBlock in calculationSpecs
            Object block = c.getCalculationSpecs().get(0);
            assertThat(block).isInstanceOf(SubroutineBlock.class);
            SubroutineBlock sb = (SubroutineBlock) block;
            assertThat(sb.getSubroutineName()).isEqualTo("SR01");
            assertThat(sb.getOperations()).isNotEmpty();
        }

        @Test
        void subroutineConvenienceIndex() {
            String source = String.join("\n",
                "     C           SR01      BEGSR                                                ",
                "     C                     SETON                     LR                         ",
                "     C                     ENDSR                                                ");
            IrDocument doc = parse(source);
            Rpg3Content c = content(doc);
            // Subroutine convenience entry
            assertThat(c.getSubroutines()).hasSize(1);
            Subroutine sub = c.getSubroutines().get(0);
            assertThat(sub.getName()).isEqualTo("SR01");
            assertThat(sub.getDefinedAtLine()).isGreaterThan(0);
        }

        @Test
        void exsrPopulatesCalledFrom() {
            String source = String.join("\n",
                "     C                     EXSR SR01                                            ",
                "     C           SR01      BEGSR                                                ",
                "     C                     SETON                     LR                         ",
                "     C                     ENDSR                                                ");
            IrDocument doc = parse(source);
            Rpg3Content c = content(doc);
            assertThat(c.getSubroutines()).isNotEmpty();
            Subroutine sub = c.getSubroutines().get(0);
            assertThat(sub.getCalledFrom()).isNotEmpty();
        }

        @Test
        void casBlock() {
            String source = String.join("\n",
                "     C           X         CASEQ'1'      SR01                                   ",
                "     C           X         CASEQ'2'      SR02                                   ",
                "     C                     END                                                  ");
            IrDocument doc = parse(source);
            Object block = content(doc).getCalculationSpecs().get(0);
            assertThat(block).isInstanceOf(CaseBlock.class);
            CaseBlock cb = (CaseBlock) block;
            assertThat(cb.getEntries()).hasSizeGreaterThanOrEqualTo(2);
            assertThat(cb.getEntries().get(0).getComparisonType()).isEqualTo("EQ");
        }

        @Test
        void tagProducesLabelNode() {
            String source = "     C           LOOP      TAG                                                   ";
            IrDocument doc = parse(source);
            Object node = content(doc).getCalculationSpecs().get(0);
            assertThat(node).isInstanceOf(LabelNode.class);
            LabelNode ln = (LabelNode) node;
            assertThat(ln.getLabelName()).isEqualTo("LOOP");
            assertThat(ln.getNodeType()).isEqualTo("labelNode");
        }

        @Test
        void gotoProducesGotoNode() {
            String source = "     C                     GOTO LOOP                                             ";
            IrDocument doc = parse(source);
            Object node = content(doc).getCalculationSpecs().get(0);
            assertThat(node).isInstanceOf(GotoNode.class);
            GotoNode gn = (GotoNode) node;
            assertThat(gn.getNodeType()).isEqualTo("gotoNode");
        }

        @Test
        void andxxCompoundCondition() {
            // IFGT + ANDLT → BinaryOpNode("AND", BinaryOpNode("GT",...), BinaryOpNode("LT",...))
            String source = String.join("\n",
                "     C           FLDA      IFGT FLDB                                            ",
                "     C           FLDC      ANDLTFLDD                                            ",
                "     C                     SETON                     LR                         ",
                "     C                     END                                                  ");
            IrDocument doc = parse(source);
            Object block = content(doc).getCalculationSpecs().get(0);
            assertThat(block).isInstanceOf(ConditionalBlock.class);
            ConditionalBlock cb = (ConditionalBlock) block;
            // comparisonType/Value should be null when compound
            assertThat(cb.getComparisonType()).isNull();
            assertThat(cb.getComparisonValue()).isNull();
            // condition should be a BinaryOpNode("AND",...)
            assertThat(cb.getCondition()).isInstanceOf(BinaryOpNode.class);
            BinaryOpNode andNode = (BinaryOpNode) cb.getCondition();
            assertThat(andNode.getOperator()).isEqualTo("AND");
            assertThat(andNode.getLeft()).isInstanceOf(BinaryOpNode.class);
            assertThat(andNode.getRight()).isInstanceOf(BinaryOpNode.class);
            BinaryOpNode left = (BinaryOpNode) andNode.getLeft();
            assertThat(left.getOperator()).isEqualTo("GT");
            BinaryOpNode right = (BinaryOpNode) andNode.getRight();
            assertThat(right.getOperator()).isEqualTo("LT");
        }

        @Test
        void orxxCompoundCondition() {
            String source = String.join("\n",
                "     C           FLDA      IFGT FLDB                                            ",
                "     C           FLDE      OREQ FLDF                                            ",
                "     C                     SETON                     LR                         ",
                "     C                     END                                                  ");
            IrDocument doc = parse(source);
            ConditionalBlock cb = (ConditionalBlock) content(doc).getCalculationSpecs().get(0);
            assertThat(cb.getCondition()).isInstanceOf(BinaryOpNode.class);
            BinaryOpNode orNode = (BinaryOpNode) cb.getCondition();
            assertThat(orNode.getOperator()).isEqualTo("OR");
        }

        @Test
        void mixedAndOrCompoundCondition() {
            // IFGT + ANDLT + OREQ → OR(AND(GT,LT), EQ)
            String source = String.join("\n",
                "     C           FLDA      IFGT FLDB                                            ",
                "     C           FLDC      ANDLTFLDD                                            ",
                "     C           FLDE      OREQ FLDF                                            ",
                "     C                     SETON                     LR                         ",
                "     C                     END                                                  ");
            IrDocument doc = parse(source);
            ConditionalBlock cb = (ConditionalBlock) content(doc).getCalculationSpecs().get(0);
            assertThat(cb.getCondition()).isInstanceOf(BinaryOpNode.class);
            BinaryOpNode orNode = (BinaryOpNode) cb.getCondition();
            assertThat(orNode.getOperator()).isEqualTo("OR");
            // left should be AND(GT, LT)
            assertThat(orNode.getLeft()).isInstanceOf(BinaryOpNode.class);
            BinaryOpNode andNode = (BinaryOpNode) orNode.getLeft();
            assertThat(andNode.getOperator()).isEqualTo("AND");
        }

        @Test
        void nestedThreeLevelBlocks() {
            // BEGSR containing DOW containing IF — 3-level nesting
            String source = String.join("\n",
                "     C           SR01      BEGSR                                                ",
                "     C           X         DOWEQ'1'                                            ",
                "     C           Y         IFEQ '1'                                            ",
                "     C                     SETON                     LR                         ",
                "     C                     END                                                  ",
                "     C                     END                                                  ",
                "     C                     ENDSR                                                ");
            IrDocument doc = parse(source);
            Rpg3Content c = content(doc);
            // Top level: SubroutineBlock
            Object top = c.getCalculationSpecs().get(0);
            assertThat(top).isInstanceOf(SubroutineBlock.class);
            SubroutineBlock sr = (SubroutineBlock) top;

            // Level 2: DoWhileBlock inside SR
            assertThat(sr.getOperations()).isNotEmpty();
            Object level2 = sr.getOperations().get(0);
            assertThat(level2).isInstanceOf(DoWhileBlock.class);
            DoWhileBlock dow = (DoWhileBlock) level2;

            // Level 3: ConditionalBlock inside DOW
            assertThat(dow.getBodyOps()).isNotEmpty();
            Object level3 = dow.getBodyOps().get(0);
            assertThat(level3).isInstanceOf(ConditionalBlock.class);
            ConditionalBlock ifb = (ConditionalBlock) level3;
            assertThat(ifb.getThenOps()).isNotEmpty();
        }

        @Test
        void endMatchesCorrectOpeningBlock() {
            // IF + END followed by DOW + END — each END closes its own block
            String source = String.join("\n",
                "     C           X         IFEQ '1'                                            ",
                "     C                     SETON                     LR                         ",
                "     C                     END                                                  ",
                "     C           Y         DOWEQ'1'                                            ",
                "     C                     SETOF                     LR                         ",
                "     C                     END                                                  ");
            IrDocument doc = parse(source);
            var specs = content(doc).getCalculationSpecs();
            assertThat(specs).hasSize(2);
            assertThat(specs.get(0)).isInstanceOf(ConditionalBlock.class);
            assertThat(specs.get(1)).isInstanceOf(DoWhileBlock.class);
        }
    }

    // =========================================================================
    // C-spec Column Extraction
    // =========================================================================

    @Nested
    class CSpecColumnExtraction {

        // RPG3 C-spec column layout (1-based):
        //   7-8   Control level
        //   9-11  Conditioning indicators (NOT + 2-digit indicator)
        //   12-25 Factor 1
        //   28-32 Opcode
        //   33-42 Factor 2
        //   43-48 Result field
        //   49-51 Field length
        //   52    Decimal positions
        //   53    Half adjust
        //   54-55 Resulting indicator high
        //   56-57 Resulting indicator low
        //   58-59 Resulting indicator equal

        @Test
        void controlLevelExtracted() {
            //            1234567890123456789012345678901234567890...
            String source = "     CL1                   SETON                     LR                         ";
            IrDocument doc = parse(source);
            Operation op = (Operation) content(doc).getCalculationSpecs().get(0);
            assertThat(op.getControlLevel()).isEqualTo("L1");
        }

        @Test
        void conditioningIndicatorExtracted() {
            //            1234567890123456789012345678901234567890...
            //            cols 9-11: N50 (negated indicator 50)
            String source = "     C  N50                SETON                     LR                         ";
            IrDocument doc = parse(source);
            Operation op = (Operation) content(doc).getCalculationSpecs().get(0);
            assertThat(op.getConditioningIndicators()).isNotEmpty();
            var ci = op.getConditioningIndicators().get(0);
            assertThat(ci.isNegated()).isTrue();
            assertThat(ci.getIndicator()).isEqualTo("50");
        }

        @Test
        void factor1Extracted() {
            //            cols 12-25: Factor1 = "FIELD1"
            String source = "     C           FIELD1    COMP FIELD2                                          ";
            IrDocument doc = parse(source);
            Operation op = (Operation) content(doc).getCalculationSpecs().get(0);
            assertThat(op.getFactor1()).isNotNull();
            assertThat(op.getFactor1().getRawText()).isEqualTo("FIELD1");
        }

        @Test
        void opcodeExtracted() {
            //              cols 28-32  cols 33-42
            //              opcode      factor2
            String source = "     C                     READ CUSTMAST                 50                      ";
            IrDocument doc = parse(source);
            Operation op = (Operation) content(doc).getCalculationSpecs().get(0);
            assertThat(op.getOpcode().trim()).isEqualTo("READ");
        }

        @Test
        void factor2Extracted() {
            String source = "     C                     READ CUSTMAST                 50                      ";
            IrDocument doc = parse(source);
            Operation op = (Operation) content(doc).getCalculationSpecs().get(0);
            assertThat(op.getFactor2()).isNotNull();
        }

        @Test
        void resultFieldExtracted() {
            //            cols 43-48: result = "DSPNAM"
            String source = "     C                     MOVELCUSTNM   DSPNAM 25                              ";
            IrDocument doc = parse(source);
            Operation op = (Operation) content(doc).getCalculationSpecs().get(0);
            assertThat(op.getResultField()).isNotNull();
        }

        @Test
        void fieldLengthExtracted() {
            //            cols 49-51: "25 "
            String source = "     C                     MOVELCUSTNM   DSPNAM 25                              ";
            IrDocument doc = parse(source);
            Operation op = (Operation) content(doc).getCalculationSpecs().get(0);
            assertThat(op.getFieldLength()).isEqualTo(25);
        }

        @Test
        void resultingIndicatorsExtracted() {
            //              cols 58-59 equal="50"
            String source = "     C                     READ CUSTMAST                 50                      ";
            IrDocument doc = parse(source);
            Operation op = (Operation) content(doc).getCalculationSpecs().get(0);
            assertThat(op.getResultingIndicators()).isNotNull();
            assertThat(op.getResultingIndicators().getEqual()).isNotNull();
            assertThat(op.getResultingIndicators().getEqual().getName()).isEqualTo("50");
            assertThat(op.getResultingIndicators().getEqual().getType()).isEqualTo("numeric");
        }

        @Test
        void setonResultingIndicatorsEqualSlot() {
            // SETON with LR at resulting indicator equal slot: cols 58-59 (0-idx 57-58)
            // 6 prefix + 21 spaces + SETON(5) + 25 spaces + LR(2) + 21 spaces = 80 chars
            String source = "     C                     SETON                         LR                     ";
            IrDocument doc = parse(source);
            Operation op = (Operation) content(doc).getCalculationSpecs().get(0);
            assertThat(op.getResultingIndicators()).isNotNull();
            assertThat(op.getResultingIndicators().getEqual().getName()).isEqualTo("LR");
            assertThat(op.getResultingIndicators().getEqual().getType()).isEqualTo("special");
        }
    }

    // =========================================================================
    // Comments
    // =========================================================================

    @Nested
    class Comments {
        @Test
        void commentHasLocationAndRawSource() {
            String source = "     C* This is a comment                                                       ";
            IrDocument doc = parse(source);
            Rpg3Content c = content(doc);
            assertThat(c.getComments()).isNotEmpty();
            Comment comment = c.getComments().get(0);
            assertThat(comment.getText()).isNotNull();
            assertThat(comment.getRawSourceLine()).isNotNull();
            assertThat(comment.getLocation()).isNotNull();
            assertThat(comment.getLineNumber()).isGreaterThan(0);
        }

        @Test
        void commentSpecContextDetected() {
            String source = "     C* This is a C-spec comment                                                ";
            IrDocument doc = parse(source);
            Comment comment = content(doc).getComments().get(0);
            assertThat(comment.getSpecContext()).isEqualTo("C");
        }

        @Test
        void fullLineCommentDetected() {
            String source = "     ** Full line comment                                                        ";
            IrDocument doc = parse(source);
            Rpg3Content c = content(doc);
            assertThat(c.getComments()).isNotEmpty();
            assertThat(c.getComments().get(0).getSpecContext()).isEqualTo("*");
        }
    }

    // =========================================================================
    // Source Lines
    // =========================================================================

    @Nested
    class SourceLines {
        @Test
        void sourceLinesBuilt() {
            String source = String.join("\n",
                "     H                                                                          ",
                "     FCUSTMAST IF  E           K        DISK                                    ");
            IrDocument doc = parse(source);
            Rpg3Content c = content(doc);
            assertThat(c.getSourceLines()).hasSize(2);
            assertThat(c.getSourceLines().get(0).getSpecType()).isEqualTo("H");
            assertThat(c.getSourceLines().get(1).getSpecType()).isEqualTo("F");
        }

        @Test
        void sourceLinesInsideContent() {
            String source = "     H                                                                          ";
            IrDocument doc = parse(source);
            // sourceLines is on Rpg3Content, not IrDocument
            Rpg3Content c = content(doc);
            List<SourceLine> lines = c.getSourceLines();
            assertThat(lines).isNotEmpty();
            assertThat(lines.get(0).getLineNumber()).isEqualTo(1);
        }

        @Test
        void commentLineMarkedInSourceLines() {
            String source = "     ** Comment line                                                             ";
            IrDocument doc = parse(source);
            Rpg3Content c = content(doc);
            assertThat(c.getSourceLines()).hasSize(1);
            SourceLine sl = c.getSourceLines().get(0);
            assertThat(sl.getSpecType()).isEqualTo("comment");
            assertThat(sl.isComment()).isTrue();
        }
    }

    // =========================================================================
    // Data Structures
    // =========================================================================

    @Nested
    class DataStructures {
        @Test
        void dataStructureAddedToContentList() {
            String source = "     I            DS                                                             ";
            IrDocument doc = parse(source);
            Rpg3Content c = content(doc);
            assertThat(c.getDataStructures()).hasSize(1);
            DataStructure ds = c.getDataStructures().get(0);
            assertThat(ds.getName()).isEqualTo("DS");
            assertThat(ds.getType()).isEqualTo("dataStructure");
            assertThat(ds.getLocation()).isNotNull();
        }
    }

    // =========================================================================
    // Multi-spec integration
    // =========================================================================

    @Nested
    class MultiSpec {
        @Test
        void multipleSpecTypesParsedCorrectly() {
            String source = String.join("\n",
                "     H                                                                          ",
                "     FCUSTMAST IF  E           K        DISK                                    ");
            IrDocument doc = parse(source);
            Rpg3Content c = content(doc);
            assertThat(c.getHeaderSpecs()).hasSize(1);
            assertThat(c.getFileSpecs()).hasSize(1);
            assertThat(c.getSourceLines()).hasSize(2);
        }

        @Test
        void threeSpecTypesTogether() {
            String source = String.join("\n",
                "     H                                                                          ",
                "     FCUSTMAST IF  E           K        DISK                                    ",
                "     E                    NAMES  10  20  8                                      ");
            IrDocument doc = parse(source);
            Rpg3Content c = content(doc);
            assertThat(c.getHeaderSpecs()).as("H-spec").hasSize(1);
            assertThat(c.getFileSpecs()).as("F-spec").hasSize(1);
            assertThat(c.getSourceLines()).as("sourceLines").hasSizeGreaterThanOrEqualTo(2);
        }
    }

    // =========================================================================
    // Edge cases
    // =========================================================================

    @Nested
    class EdgeCases {
        @Test
        void emptySourceProducesEmptyIr() {
            IrDocument doc = parse("");
            assertThat(doc).isNotNull();
            Rpg3Content c = content(doc);
            assertThat(c.getHeaderSpecs()).isEmpty();
            assertThat(c.getFileSpecs()).isEmpty();
            assertThat(c.getCalculationSpecs()).isEmpty();
        }
    }

    // =========================================================================
    // Compile-Time Data
    // =========================================================================

    @Nested
    class CompileTimeDataTests {
        @Test
        void singleBlockParsed() {
            String source = String.join("\n",
                "     E                    ARR     5  10  5                                      ",
                "**                                                                              ",
                "ABCDE                                                                           ",
                "FGHIJ                                                                           ");
            IrDocument doc = parse(source);
            Rpg3Content c = content(doc);
            assertThat(c.getCompileTimeData()).isNotNull();
            assertThat(c.getCompileTimeData().getBlocks()).hasSize(1);
            assertThat(c.getCompileTimeData().getBlocks().get(0).getArrayName()).isEqualTo("ARR");
            assertThat(c.getCompileTimeData().getBlocks().get(0).getData()).hasSize(2);
        }

        @Test
        void multiBlocksAssociatedByOrder() {
            String source = String.join("\n",
                "     E                    ARR1    5  10  5                                      ",
                "     E                    ARR2    5  10  5                                      ",
                "**                                                                              ",
                "AAAAA                                                                           ",
                "**                                                                              ",
                "BBBBB                                                                           ");
            IrDocument doc = parse(source);
            Rpg3Content c = content(doc);
            assertThat(c.getCompileTimeData()).isNotNull();
            assertThat(c.getCompileTimeData().getBlocks()).hasSize(2);
            assertThat(c.getCompileTimeData().getBlocks().get(0).getArrayName()).isEqualTo("ARR1");
            assertThat(c.getCompileTimeData().getBlocks().get(1).getArrayName()).isEqualTo("ARR2");
        }

        @Test
        void noCompileTimeDataReturnsNull() {
            String source = "     H                                                                          ";
            IrDocument doc = parse(source);
            assertThat(content(doc).getCompileTimeData()).isNull();
        }
    }
}
