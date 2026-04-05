package com.as400parser.rpgle;

import com.as400parser.rpgle.model.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link RpgleFixedParser} — isolated fixed-format parsing.
 * <p>
 * Feeds raw RPGLE source lines directly to {@code parse()} and verifies
 * the resulting {@link RpgleContent} IR structure.
 */
class RpgleFixedParserTest {

    /**
     * Parses varargs fixed-format lines into RpgleContent (no sequence numbers).
     */
    private RpgleContent parse(String... lines) {
        RpgleFixedParser parser = new RpgleFixedParser();
        return parser.parse(Arrays.asList(lines), new String[0]);
    }

    // =========================================================================
    // H-spec: Control Specification
    // =========================================================================

    @Nested
    class HSpecParsing {

        @Test
        void singleHSpec_parsedWithKeywords() {
            RpgleContent c = parse("     H DFTACTGRP(*NO) ACTGRP('MYGRP')");
            assertThat(c.getControlSpecs()).hasSize(1);
            ControlSpec spec = c.getControlSpecs().get(0);
            assertThat(spec.getKeywords()).contains("DFTACTGRP(*NO)");
            assertThat(spec.getKeywords()).contains("ACTGRP('MYGRP')");
        }

        @Test
        void hSpec_locationSetCorrectly() {
            RpgleContent c = parse("     H DFTACTGRP(*NO)");
            assertThat(c.getControlSpecs().get(0).getLocation().getStartLine()).isEqualTo(1);
            assertThat(c.getControlSpecs().get(0).getLocation().getEndLine()).isEqualTo(1);
        }

        @Test
        void multipleHSpecs_allCaptured() {
            RpgleContent c = parse(
                "     H DFTACTGRP(*NO)",
                "     H DEBUG(*YES)"
            );
            assertThat(c.getControlSpecs()).hasSize(2);
        }

        @Test
        void hSpec_rawSourceLinePreserved() {
            String line = "     H DFTACTGRP(*NO) ACTGRP('MYGRP')";
            RpgleContent c = parse(line);
            assertThat(c.getControlSpecs().get(0).getRawSourceLine()).isEqualTo(line);
        }
    }

    // =========================================================================
    // F-spec: File Specification
    // =========================================================================

    @Nested
    class FSpecParsing {

        @Test
        void singleFSpec_fileNameExtracted() {
            RpgleContent c = parse("     FSTUDNTPF  IF   E           K DISK");
            assertThat(c.getFileSpecs()).hasSize(1);
            FileSpec spec = c.getFileSpecs().get(0);
            assertThat(spec.getFileName()).isEqualTo("STUDNTPF");
        }

        @Test
        void fSpec_fileType_inputFile() {
            RpgleContent c = parse("     FSTUDNTPF  IF   E           K DISK");
            assertThat(c.getFileSpecs().get(0).getFileType()).isEqualTo("I");
        }

        @Test
        void fSpec_fileType_updateFile() {
            RpgleContent c = parse("     FSTUDNTPF  UF   E           K DISK");
            assertThat(c.getFileSpecs().get(0).getFileType()).isEqualTo("U");
        }

        @Test
        void fSpec_fileType_outputFile() {
            RpgleContent c = parse("     FREPORTPF  O    F  132        PRINTER");
            assertThat(c.getFileSpecs().get(0).getFileType()).isEqualTo("O");
        }

        @Test
        void fSpec_device_extracted() {
            RpgleContent c = parse("     FSTUDNTPF  IF   E           K DISK");
            assertThat(c.getFileSpecs().get(0).getDevice()).isEqualTo("DISK");
        }

        @Test
        void multipleFSpecs_allCaptured() {
            RpgleContent c = parse(
                "     FSTUDNTPF  IF   E           K DISK",
                "     FREPORTPF  O    F  132        PRINTER"
            );
            assertThat(c.getFileSpecs()).hasSize(2);
        }

        @Test
        void fSpec_continuationLine_keywordsAppended() {
            RpgleContent c = parse(
                "     FSTUDNTPF  IF   E           K DISK    RENAME(STUDREC:STUDLF)",
                "     F                                            INFDS(FLDS)"
            );
            assertThat(c.getFileSpecs()).hasSize(1);
            FileSpec spec = c.getFileSpecs().get(0);
            assertThat(spec.getKeywords()).contains("INFDS(FLDS)");
        }

        @Test
        void fSpec_continuationLine_endLineUpdated() {
            RpgleContent c = parse(
                "     FSTUDNTPF  IF   E           K DISK    RENAME(STUDREC:STUDLF)",
                "     F                                            INFDS(FLDS)"
            );
            assertThat(c.getFileSpecs().get(0).getLocation().getEndLine()).isEqualTo(2);
        }

        @Test
        void fSpec_locationSetCorrectly() {
            RpgleContent c = parse(
                "     H DFTACTGRP(*NO)",
                "     FSTUDNTPF  IF   E           K DISK"
            );
            assertThat(c.getFileSpecs().get(0).getLocation().getStartLine()).isEqualTo(2);
        }
    }

    // =========================================================================
    // D-spec: Definition Specification
    // =========================================================================

    @Nested
    class DSpecParsing {

        @Test
        void standalone_nameExtracted() {
            RpgleContent c = parse("     D Counter         S              5  0");
            assertThat(c.getDefinitionSpecs()).hasSize(1);
            DefinitionSpec spec = c.getDefinitionSpecs().get(0);
            assertThat(spec.getName()).isEqualTo("Counter");
        }

        @Test
        void standalone_definitionTypeExtracted() {
            RpgleContent c = parse("     D Counter         S              5  0");
            assertThat(c.getDefinitionSpecs().get(0).getDefinitionType()).isEqualTo("S");
        }

        @Test
        void constant_definitionTypeC() {
            RpgleContent c = parse("     D MaxRec          C                   CONST(100)");
            DefinitionSpec spec = c.getDefinitionSpecs().get(0);
            assertThat(spec.getName()).isEqualTo("MaxRec");
            assertThat(spec.getDefinitionType()).isEqualTo("C");
        }

        @Test
        void dataStructure_definitionTypeDS() {
            RpgleContent c = parse("     D MyDS            DS");
            assertThat(c.getDefinitionSpecs().get(0).getDefinitionType()).isEqualTo("DS");
        }

        @Test
        void multipleDSpecs_allCaptured() {
            RpgleContent c = parse(
                "     D Counter         S              5  0",
                "     D FullName        S             30A",
                "     D MaxRec          C                   CONST(100)"
            );
            assertThat(c.getDefinitionSpecs()).hasSize(3);
        }

        @Test
        void dSpec_continuationLine_keywordsAppended() {
            RpgleContent c = parse(
                "     D Counter         S              5  0",
                "     D                                     INZ(0)"
            );
            assertThat(c.getDefinitionSpecs()).hasSize(1);
            assertThat(c.getDefinitionSpecs().get(0).getKeywords()).contains("INZ(0)");
        }

        @Test
        void dSpec_continuationLine_endLineUpdated() {
            RpgleContent c = parse(
                "     D Counter         S              5  0",
                "     D                                     INZ(0)"
            );
            assertThat(c.getDefinitionSpecs().get(0).getLocation().getEndLine()).isEqualTo(2);
        }

        @Test
        void dSpec_internalDataType_extracted() {
            RpgleContent c = parse("     D FullName        S             30A");
            assertThat(c.getDefinitionSpecs().get(0).getInternalDataType()).isEqualTo("A");
        }
    }

    // =========================================================================
    // I-spec: Input Specification
    // =========================================================================

    @Nested
    class ISpecParsing {

        @Test
        void recordEntry_fileNameExtracted() {
            RpgleContent c = parse("     ISTUDNTPF  NS");
            assertThat(c.getInputSpecs()).hasSize(1);
            InputSpec spec = c.getInputSpecs().get(0);
            assertThat(spec.getSpecLevel()).isEqualTo("recordIdentification");
            assertThat(spec.getFileName()).isEqualTo("STUDNTPF");
        }

        @Test
        void fieldEntry_fieldNameExtracted() {
            // I-spec field name at columns 49-62 (0-indexed: 48-62)
            // Build a properly padded line with STUDID at position 48
            // "     I" + 42 spaces (positions 6-47) + "STUDID" (positions 48-53)
            String line = "     I" + " ".repeat(42) + "STUDID";
            RpgleContent c = parse(line);
            assertThat(c.getInputSpecs()).hasSize(1);
            InputSpec spec = c.getInputSpecs().get(0);
            assertThat(spec.getSpecLevel()).isEqualTo("fieldDefinition");
            assertThat(spec.getFieldName()).isEqualTo("STUDID");
        }

        @Test
        void multipleISpecs_recordAndField() {
            RpgleContent c = parse(
                "     ISTUDNTPF  NS",
                "     I                                  1   10 STUDID",
                "     I                                 11   30 STUDNM"
            );
            assertThat(c.getInputSpecs()).hasSize(3);
            assertThat(c.getInputSpecs().get(0).getSpecLevel()).isEqualTo("recordIdentification");
            assertThat(c.getInputSpecs().get(1).getSpecLevel()).isEqualTo("fieldDefinition");
            assertThat(c.getInputSpecs().get(2).getSpecLevel()).isEqualTo("fieldDefinition");
        }
    }

    // =========================================================================
    // C-spec: Calculation Specification
    // =========================================================================

    @Nested
    class CSpecParsing {

        @Test
        void eval_operationExtracted() {
            RpgleContent c = parse("     C                   EVAL      Counter = 0");
            assertThat(c.getCalcSpecs()).hasSize(1);
            CalcSpec spec = c.getCalcSpecs().get(0);
            assertThat(spec.getOpcode()).isEqualTo("EVAL");
        }

        @Test
        void eval_extendedFactor2_detected() {
            RpgleContent c = parse("     C                   EVAL      Counter = 0");
            assertThat(c.getCalcSpecs().get(0).isExtendedFactor2()).isTrue();
        }

        @Test
        void eval_factor2_containsExpression() {
            RpgleContent c = parse("     C                   EVAL      Counter = 0");
            assertThat(c.getCalcSpecs().get(0).getFactor2()).contains("Counter = 0");
        }

        @Test
        void call_operationExtracted() {
            RpgleContent c = parse("     C                   CALL      'RPTPGM'");
            assertThat(c.getCalcSpecs()).hasSize(1);
            CalcSpec spec = c.getCalcSpecs().get(0);
            assertThat(spec.getOpcode()).isEqualTo("CALL");
        }

        @Test
        void callp_operationExtracted() {
            RpgleContent c = parse("     C                   CALLP     UpdateLog(Counter)");
            CalcSpec spec = c.getCalcSpecs().get(0);
            assertThat(spec.getOpcode()).isEqualTo("CALLP");
        }

        @Test
        void plist_factor1Extracted() {
            RpgleContent c = parse("     C     *ENTRY        PLIST");
            CalcSpec spec = c.getCalcSpecs().get(0);
            assertThat(spec.getFactor1()).isEqualTo("*ENTRY");
            assertThat(spec.getOpcode()).isEqualTo("PLIST");
        }

        @Test
        void parm_traditionalFormat_resultFieldExtracted() {
            RpgleContent c = parse("     C                   PARM                    PARM1            10");
            CalcSpec spec = c.getCalcSpecs().get(0);
            assertThat(spec.getOpcode()).isEqualTo("PARM");
            assertThat(spec.getResultField()).isEqualTo("PARM1");
        }

        @Test
        void read_operationExtracted() {
            RpgleContent c = parse("     C                   READ      STUDREC");
            assertThat(c.getCalcSpecs().get(0).getOpcode()).isEqualTo("READ");
        }

        @Test
        void multipleCSpecs_allCaptured() {
            RpgleContent c = parse(
                "     C                   EVAL      Counter = 0",
                "     C                   READ      STUDREC",
                "     C                   CALL      'RPTPGM'"
            );
            assertThat(c.getCalcSpecs()).hasSize(3);
        }

        @Test
        void cSpec_extendedFactor2Continuation_appended() {
            // Extended factor 2 continuation: blank factor1 and blank operation, prev was extended
            RpgleContent c = parse(
                "     C                   EVAL      Counter =",
                "     C                                       Counter + 1"
            );
            assertThat(c.getCalcSpecs()).hasSize(1);
            assertThat(c.getCalcSpecs().get(0).getFactor2()).contains("Counter + 1");
        }

        @Test
        void cSpec_indicators_onShortLine_areNull() {
            // Line shorter than 71 chars — indicators should be null
            RpgleContent c = parse("     C                   EVAL      *INLR = *ON");
            CalcSpec spec = c.getCalcSpecs().get(0);
            assertThat(spec.getHiIndicator()).isNull();
        }

        @Test
        void cSpec_indicators_onFullLine_extracted() {
            // Full 80-char line with indicators at columns 71-76
            //                   1         2         3         4         5         6         7         8
            // col:     12345678901234567890123456789012345678901234567890123456789012345678901234567890
            RpgleContent c = parse("     C                   COMP      X             Y                         2021");
            CalcSpec spec = c.getCalcSpecs().get(0);
            // HI indicator at cols 71-72 = "20", LO at 73-74 = "21"
            assertThat(spec.getHiIndicator()).isNotNull();
        }

        @Test
        void cSpec_location_correct() {
            RpgleContent c = parse(
                "     H DFTACTGRP(*NO)",
                "     C                   EVAL      Counter = 0"
            );
            assertThat(c.getCalcSpecs().get(0).getLocation().getStartLine()).isEqualTo(2);
        }
    }

    // =========================================================================
    // O-spec: Output Specification
    // =========================================================================

    @Nested
    class OSpecParsing {

        @Test
        void recordEntry_fileNameExtracted() {
            RpgleContent c = parse("     OREPORTPF  H    1P");
            assertThat(c.getOutputSpecs()).hasSize(1);
            OutputSpec spec = c.getOutputSpecs().get(0);
            assertThat(spec.getSpecLevel()).isEqualTo("recordLevel");
            assertThat(spec.getFileName()).isEqualTo("REPORTPF");
        }

        @Test
        void fieldEntry_fieldNameExtracted() {
            RpgleContent c = parse("     O          D    1");
            assertThat(c.getOutputSpecs()).hasSize(1);
            // This line has a filename area blank → field entry
        }

        @Test
        void recordEntry_typeExtracted() {
            RpgleContent c = parse("     OREPORTPF  H    1P");
            assertThat(c.getOutputSpecs().get(0).getType()).isEqualTo("H");
        }
    }

    // =========================================================================
    // P-spec: Procedure Specification
    // =========================================================================

    @Nested
    class PSpecParsing {

        @Test
        void procedureBegin_nameExtracted() {
            RpgleContent c = parse("     P MyProc          B                   EXPORT");
            assertThat(c.getProcedureSpecs()).hasSize(1);
            ProcedureSpec spec = c.getProcedureSpecs().get(0);
            assertThat(spec.getName()).isEqualTo("MyProc");
            assertThat(spec.getBeginEnd()).isEqualTo("B");
        }

        @Test
        void procedureEnd_nameExtracted() {
            RpgleContent c = parse("     P MyProc          E");
            assertThat(c.getProcedureSpecs()).hasSize(1);
            assertThat(c.getProcedureSpecs().get(0).getBeginEnd()).isEqualTo("E");
        }

        @Test
        void procedure_keywords_extracted() {
            RpgleContent c = parse("     P MyProc          B                   EXPORT");
            assertThat(c.getProcedureSpecs().get(0).getKeywords()).contains("EXPORT");
        }

        @Test
        void pSpec_continuationLine_keywordsAppended() {
            RpgleContent c = parse(
                "     P MyProc          B                   EXPORT",
                "     P                                            SERIALIZE"
            );
            assertThat(c.getProcedureSpecs()).hasSize(1);
            assertThat(c.getProcedureSpecs().get(0).getKeywords()).contains("SERIALIZE");
        }

        @Test
        void pSpec_continuationLine_endLineUpdated() {
            RpgleContent c = parse(
                "     P MyProc          B                   EXPORT",
                "     P                                            SERIALIZE"
            );
            assertThat(c.getProcedureSpecs().get(0).getLocation().getEndLine()).isEqualTo(2);
        }
    }

    // =========================================================================
    // Comments
    // =========================================================================

    @Nested
    class CommentParsing {

        @Test
        void comment_isCaptured() {
            RpgleContent c = parse("     C* This is a comment");
            assertThat(c.getComments()).hasSize(1);
            assertThat(c.getComments().get(0)).contains("This is a comment");
        }

        @Test
        void multipleComments_allCaptured() {
            RpgleContent c = parse(
                "     C* Comment 1",
                "     C* Comment 2",
                "     D* Comment 3"
            );
            assertThat(c.getComments()).hasSize(3);
        }
    }

    // =========================================================================
    // /COPY and /INCLUDE directives
    // =========================================================================

    @Nested
    class CopyDirectiveParsing {

        @Test
        void copy_directiveExtracted() {
            RpgleContent c = parse("      /COPY QRPGLESRC,COPYMBR1");
            assertThat(c.getCopyDirectives()).hasSize(1);
            RpgleContent.CopyDirective cd = c.getCopyDirectives().get(0);
            assertThat(cd.directive()).isEqualTo("COPY");
            assertThat(cd.path()).isEqualTo("QRPGLESRC,COPYMBR1");
        }

        @Test
        void include_directiveExtracted() {
            RpgleContent c = parse("      /INCLUDE MYLIB/QRPGLESRC,COPYMBR2");
            assertThat(c.getCopyDirectives()).hasSize(1);
            RpgleContent.CopyDirective cd = c.getCopyDirectives().get(0);
            assertThat(cd.directive()).isEqualTo("INCLUDE");
            assertThat(cd.path()).isEqualTo("MYLIB/QRPGLESRC,COPYMBR2");
        }

        @Test
        void copyMemberOnly_directiveExtracted() {
            RpgleContent c = parse("      /COPY SIMPLEMEM");
            assertThat(c.getCopyDirectives()).hasSize(1);
            assertThat(c.getCopyDirectives().get(0).path()).isEqualTo("SIMPLEMEM");
        }

        @Test
        void multipleCopyDirectives_allCaptured() {
            RpgleContent c = parse(
                "      /COPY QRPGLESRC,COPYMBR1",
                "      /INCLUDE MYLIB/QRPGLESRC,COPYMBR2",
                "      /COPY SIMPLEMEM"
            );
            assertThat(c.getCopyDirectives()).hasSize(3);
        }

        @Test
        void copyDirective_locationSetCorrectly() {
            RpgleContent c = parse(
                "     H DFTACTGRP(*NO)",
                "      /COPY QRPGLESRC,COPYMBR1"
            );
            assertThat(c.getCopyDirectives().get(0).location().getStartLine()).isEqualTo(2);
        }
    }

    // =========================================================================
    // Mixed-format: /FREE.../END-FREE
    // =========================================================================

    @Nested
    class MixedFormatParsing {

        @Test
        void freeBlock_parsedIntoFreeStatements() {
            RpgleContent c = parse(
                "      /FREE",
                "        Counter = 0;",
                "        read STUDREC;",
                "      /END-FREE"
            );
            assertThat(c.getFreeFormatStatements()).isNotEmpty();
        }

        @Test
        void freeBlock_formatTypeIsMixed() {
            RpgleContent c = parse(
                "     FSTUDNTPF  IF   E           K DISK",
                "      /FREE",
                "        Counter = 0;",
                "      /END-FREE"
            );
            assertThat(c.getFormatType()).isEqualTo("mixed");
        }

        @Test
        void noFreeBlock_formatTypeIsFixed() {
            RpgleContent c = parse(
                "     H DFTACTGRP(*NO)",
                "     FSTUDNTPF  IF   E           K DISK"
            );
            assertThat(c.getFormatType()).isEqualTo("fixed");
        }

        @Test
        void unterminatedFreeBlock_stillFlushed() {
            // /FREE without /END-FREE — should flush remaining lines
            RpgleContent c = parse(
                "      /FREE",
                "        Counter = 0;"
            );
            assertThat(c.getFreeFormatStatements()).isNotEmpty();
        }
    }

    // =========================================================================
    // Edge Cases
    // =========================================================================

    @Nested
    class EdgeCases {

        @Test
        void emptyInput_producesEmptyContent() {
            RpgleContent c = parse();
            assertThat(c.getControlSpecs()).isEmpty();
            assertThat(c.getFileSpecs()).isEmpty();
            assertThat(c.getDefinitionSpecs()).isEmpty();
            assertThat(c.getCalcSpecs()).isEmpty();
            assertThat(c.getOutputSpecs()).isEmpty();
            assertThat(c.getProcedureSpecs()).isEmpty();
            assertThat(c.getInputSpecs()).isEmpty();
        }

        @Test
        void nullLines_skipped() {
            RpgleFixedParser parser = new RpgleFixedParser();
            RpgleContent c = parser.parse(
                Arrays.asList(null, "", "     H DFTACTGRP(*NO)"), new String[0]);
            assertThat(c.getControlSpecs()).hasSize(1);
        }

        @Test
        void shortLine_skippedGracefully() {
            RpgleContent c = parse("XY");
            assertThat(c.getControlSpecs()).isEmpty();
            assertThat(c.getFileSpecs()).isEmpty();
        }

        @Test
        void unknownFormType_skippedSilently() {
            RpgleContent c = parse("     X THIS IS UNKNOWN FORM TYPE");
            assertThat(c.getControlSpecs()).isEmpty();
            assertThat(c.getFileSpecs()).isEmpty();
            assertThat(c.getDefinitionSpecs()).isEmpty();
        }

        @Test
        void compilerDirective_otherThanCopy_skipped() {
            RpgleContent c = parse("      /EJECT");
            assertThat(c.getCopyDirectives()).isEmpty();
            assertThat(c.getControlSpecs()).isEmpty();
        }
    }

    // =========================================================================
    // safeSubstring utility
    // =========================================================================

    @Nested
    class SafeSubstring {

        @Test
        void normalRange_returnsExpected() {
            String result = RpgleFixedParser.safeSubstring("HELLO WORLD", 0, 5);
            assertThat(result).isEqualTo("HELLO");
        }

        @Test
        void startBeyondLength_returnsNull() {
            String result = RpgleFixedParser.safeSubstring("ABC", 10, 20);
            assertThat(result).isNull();
        }

        @Test
        void endBeyondLength_clampedSafely() {
            String result = RpgleFixedParser.safeSubstring("HELLO", 0, 100);
            assertThat(result).isEqualTo("HELLO");
        }

        @Test
        void allBlanks_returnsEmpty() {
            String result = RpgleFixedParser.safeSubstring("          ", 0, 5);
            assertThat(result).isEqualTo("");
        }

        @Test
        void nullInput_returnsNull() {
            String result = RpgleFixedParser.safeSubstring(null, 0, 5);
            assertThat(result).isNull();
        }
    }
}
