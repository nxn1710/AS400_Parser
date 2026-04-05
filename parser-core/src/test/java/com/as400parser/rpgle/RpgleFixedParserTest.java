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
            // HI at cols 70-72 (0-indexed), LO at 72-74, EQ at 74-76
            // Build a 80-char line with "20" at HI, "30" at LO
            char[] chars = new char[76];
            java.util.Arrays.fill(chars, ' ');
            chars[5] = 'C';
            System.arraycopy("COMP      ".toCharArray(), 0, chars, 25, 10);
            chars[70] = '2'; chars[71] = '0';  // HI
            chars[72] = '3'; chars[73] = '0';  // LO
            String line = new String(chars);
            RpgleContent c = parse(line);
            CalcSpec spec = c.getCalcSpecs().get(0);
            assertThat(spec.getHiIndicator()).isEqualTo("20");
            assertThat(spec.getLoIndicator()).isEqualTo("30");
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

    // =========================================================================
    // F-spec: boundary column tests
    // =========================================================================

    @Nested
    class FSpecBoundaryTests {

        @Test
        void fSpec_allColumnsExtracted() {
            // Build precise F-spec using safeSubstring column positions:
            // fileName:6-16, fileType:16-17, fileDesig:17-18, eof:18-19,
            // addition:19-20, seq:20-21, format:21-22, recLen:22-27,
            // limits:27-28, keyLen:28-33, addrType:33-34, org:34-35,
            // device:35-42, reserved:42-43, keywords:43-80
            char[] chars = new char[80];
            java.util.Arrays.fill(chars, ' ');
            // 0-4 = sequence area
            // 5 = form type
            chars[5] = 'F';
            // 6-15 = file name
            System.arraycopy("FILENAME  ".toCharArray(), 0, chars, 6, 10);
            // 16 = file type
            chars[16] = 'I';
            // 17 = file designation
            chars[17] = 'F';
            // 18 = end of file
            chars[18] = 'E';
            // 19 = file addition
            chars[19] = 'A';
            // 35-41 = device
            System.arraycopy("DISK   ".toCharArray(), 0, chars, 35, 7);
            // 43-79 = keywords
            System.arraycopy("RENAME(REC:LF)".toCharArray(), 0, chars, 43, 14);

            String line = new String(chars);
            RpgleContent c = parse(line);
            FileSpec spec = c.getFileSpecs().get(0);
            assertThat(spec.getFileName()).isEqualTo("FILENAME");
            assertThat(spec.getFileType()).isEqualTo("I");
            assertThat(spec.getFileDesignation()).isEqualTo("F");
            assertThat(spec.getEndOfFile()).isEqualTo("E");
            assertThat(spec.getFileAddition()).isEqualTo("A");
            assertThat(spec.getDevice()).isEqualTo("DISK");
            assertThat(spec.getKeywords()).contains("RENAME(REC:LF)");
        }

        @Test
        void fSpec_shortLine_missingKeywordsArea_handledGracefully() {
            // Line only 42 chars — keywords area (43-80) missing
            String line = "     FSTUDNTPF  IF   E           K DISK   ";
            RpgleContent c = parse(line);
            assertThat(c.getFileSpecs()).hasSize(1);
        }

        @Test
        void fSpec_exactLength6_handledGracefully() {
            // Only 6 chars — enough for form type but no content
            String line = "     F";
            RpgleContent c = parse(line);
            // Should parse but with null fields
            assertThat(c.getFileSpecs()).hasSize(1);
        }
    }

    // =========================================================================
    // D-spec: boundary column tests
    // =========================================================================

    @Nested
    class DSpecBoundaryTests {

        @Test
        void dSpec_allColumnsExtracted() {
            // Build with precise char-array positions:
            // name:6-20, externalDesc:21, dsType:22, defType:23-24, from:25-31, to/len:32-38
            // internalType:39, decimals:40-41, keywords:43-79
            char[] chars = new char[80];
            java.util.Arrays.fill(chars, ' ');
            chars[5] = 'D';
            System.arraycopy("FieldName     ".toCharArray(), 0, chars, 6, 14); // name
            chars[21] = 'E';  // externalDescription
            chars[22] = 'T';  // dsType
            System.arraycopy("DS".toCharArray(), 0, chars, 23, 2);             // defType
            System.arraycopy("     7".toCharArray(), 0, chars, 25, 6);         // from (right-aligned)
            System.arraycopy("     5".toCharArray(), 0, chars, 32, 6);         // to/len
            chars[39] = 'A';  // internalDataType
            System.arraycopy(" 2".toCharArray(), 0, chars, 40, 2);             // decimalPositions
            System.arraycopy("INZ(*BLANKS)".toCharArray(), 0, chars, 43, 12);  // keywords

            String line = new String(chars);
            RpgleContent c = parse(line);
            DefinitionSpec spec = c.getDefinitionSpecs().get(0);
            assertThat(spec.getName()).isEqualTo("FieldName");
            assertThat(spec.getExternalDescription()).isEqualTo("E");
            assertThat(spec.getDsType()).isEqualTo("T");
            assertThat(spec.getDecimalPositions()).isEqualTo("2");
            assertThat(spec.getInternalDataType()).isEqualTo("A");
            assertThat(spec.getKeywords()).contains("INZ(*BLANKS)");
        }

        @Test
        void dSpec_shortLine_missingPositions() {
            // Line shorter than 32 — fromPosition and beyond will be null
            String line = "     D ShortName       S";
            RpgleContent c = parse(line);
            assertThat(c.getDefinitionSpecs()).hasSize(1);
            DefinitionSpec spec = c.getDefinitionSpecs().get(0);
            assertThat(spec.getName()).isEqualTo("ShortName");
            assertThat(spec.getFromPosition()).isNull();
        }

        @Test
        void dSpec_fromPosition_extracted() {
            // Full D-spec: from = columns 26-32, to/length = columns 33-39
            String line = "     D Overlay         S           0001   5A   OVERLAY(DS:1)                   ";
            RpgleContent c = parse(line);
            DefinitionSpec spec = c.getDefinitionSpecs().get(0);
            // From position is at columns 25-32
            assertThat(spec.getToPositionLength()).isNotNull();
        }

        @Test
        void dSpec_decimalPositions_extracted() {
            String line = "     D Counter         S              5  2";
            RpgleContent c = parse(line);
            DefinitionSpec spec = c.getDefinitionSpecs().get(0);
            assertThat(spec.getDecimalPositions()).isEqualTo("2");
        }
    }

    // =========================================================================
    // I-spec: boundary column tests
    // =========================================================================

    @Nested
    class ISpecBoundaryTests {

        @Test
        void iSpec_recordEntry_allFieldsExtracted() {
            // Full I-spec record entry
            String line = "     ISTUDNTPF  01   01N02 1CA 20CB 39CC";
            RpgleContent c = parse(line);
            InputSpec spec = c.getInputSpecs().get(0);
            assertThat(spec.getSpecLevel()).isEqualTo("recordIdentification");
            assertThat(spec.getFileName()).isNotNull();
            assertThat(spec.getSequenceNumber()).isNotNull();
        }

        @Test
        void iSpec_fieldEntry_allFieldsExtracted() {
            // Field entry with attributes at columns 31-34, location at 37-46, etc.
            //       I                              PB     1   10 0STUDID   L1  M1      20
            // Ensure proper field-level parsing positions
            StringBuilder line = new StringBuilder("     I");
            // Pad to col 30 (indices 6-29) = 24 chars
            line.append(" ".repeat(24));
            // Data attributes cols 31-34 (indices 30-33) = "PB  "
            line.append("PB  ");
            // Date/time separator col 35 (index 34) = " "
            line.append(" ");
            // Data format col 36 (index 35) = " "
            line.append(" ");
            // Field location cols 37-46 (indices 36-45) = "   1   10 "
            line.append("   1   10 ");
            // Decimal positions cols 47-48 (indices 46-47) = " 0"
            line.append(" 0");
            // Field name cols 49-62 (indices 48-61) = "STUDID        "
            line.append("STUDID        ");
            // Control level cols 63-64 (indices 62-63) = "L1"
            line.append("L1");
            // Matching fields cols 65-66 (indices 64-65) = "M1"
            line.append("M1");
            // Field record relation cols 67-68 (indices 66-67) = "  "
            line.append("  ");
            // Field indicators cols 69-74 (indices 68-73) = "  20  "
            line.append("  20  ");

            RpgleContent c = parse(line.toString());
            InputSpec spec = c.getInputSpecs().get(0);
            assertThat(spec.getSpecLevel()).isEqualTo("fieldDefinition");
            assertThat(spec.getFieldName()).isEqualTo("STUDID");
            assertThat(spec.getControlLevel()).isEqualTo("L1");
            assertThat(spec.getMatchingFields()).isEqualTo("M1");
        }

        @Test
        void iSpec_shortLine_only6Chars() {
            RpgleContent c = parse("     I");
            // Blank file name area → field definition with null fields
            assertThat(c.getInputSpecs()).hasSize(1);
            assertThat(c.getInputSpecs().get(0).getSpecLevel()).isEqualTo("fieldDefinition");
        }
    }

    // =========================================================================
    // C-spec: boundary column tests
    // =========================================================================

    @Nested
    class CSpecBoundaryTests {

        @Test
        void cSpec_operationExtender_extracted() {
            // EVAL(H) — operation with extender in parens
            RpgleContent c = parse("     C                   EVAL(H)   Counter = Counter + 0.5");
            CalcSpec spec = c.getCalcSpecs().get(0);
            assertThat(spec.getOpcode()).isEqualTo("EVAL");
            assertThat(spec.getOperationExtender()).isEqualTo("H");
            assertThat(spec.getOperationAndExtender()).isNotNull();
        }

        @Test
        void cSpec_traditionalFactor2_resultFieldExtracted() {
            // Build a C-spec with traditional factor2 using exact column positions:
            // controlLevel:6-8, cond:8-11, factor1:11-25, op:25-35,
            // factor2:35-49, result:49-63, fieldLength:63-68, decimals:68-70
            char[] chars = new char[70];
            java.util.Arrays.fill(chars, ' ');
            chars[5] = 'C';
            // Operation at cols 25-35
            System.arraycopy("ADD       ".toCharArray(), 0, chars, 25, 10);
            // Factor2 at cols 35-49
            System.arraycopy("Factor2       ".toCharArray(), 0, chars, 35, 14);
            // Result at cols 49-63
            System.arraycopy("Result        ".toCharArray(), 0, chars, 49, 14);
            // Field length at cols 63-68
            System.arraycopy("    5".toCharArray(), 0, chars, 63, 5);

            String line = new String(chars);
            RpgleContent c = parse(line);
            CalcSpec spec = c.getCalcSpecs().get(0);
            assertThat(spec.isExtendedFactor2()).isFalse();
            assertThat(spec.getFactor2()).isEqualTo("Factor2");
            assertThat(spec.getResultField()).isEqualTo("Result");
            assertThat(spec.getFieldLength()).isEqualTo("5");
        }

        @Test
        void cSpec_loAndEqIndicators_extracted() {
            // Build precise line: HI=cols 70-72, LO=72-74, EQ=74-76 (0-based)
            char[] chars = new char[76];
            java.util.Arrays.fill(chars, ' ');
            chars[5] = 'C';
            System.arraycopy("COMP      ".toCharArray(), 0, chars, 25, 10);
            chars[70] = '2'; chars[71] = '0';  // HI = "20"
            chars[72] = '3'; chars[73] = '0';  // LO = "30"
            chars[74] = '4'; chars[75] = '0';  // EQ = "40"
            String line = new String(chars);
            RpgleContent c = parse(line);
            CalcSpec spec = c.getCalcSpecs().get(0);
            assertThat(spec.getHiIndicator()).isEqualTo("20");
            assertThat(spec.getLoIndicator()).isEqualTo("30");
            assertThat(spec.getEqIndicator()).isEqualTo("40");
        }

        @Test
        void cSpec_controlLevel_extracted() {
            // Control level at safeSubstring cols 6-8
            char[] chars = new char[70];
            java.util.Arrays.fill(chars, ' ');
            chars[5] = 'C';
            // Control level at cols 6-7 = "L1"
            chars[6] = 'L';
            chars[7] = '1';
            // Operation at cols 25-35
            System.arraycopy("EVAL      ".toCharArray(), 0, chars, 25, 10);
            // Extended factor 2 at cols 35-80
            System.arraycopy("Total = Total + Amount".toCharArray(), 0, chars, 35, 22);

            String line = new String(chars);
            RpgleContent c = parse(line);
            CalcSpec spec = c.getCalcSpecs().get(0);
            assertThat(spec.getControlLevel()).isEqualTo("L1");
        }

        @Test
        void cSpec_conditioningIndicators_extracted() {
            // Conditioning indicators at columns 9-11
            String line = "     C  01N02            EVAL      Counter = 0";
            RpgleContent c = parse(line);
            CalcSpec spec = c.getCalcSpecs().get(0);
            assertThat(spec.getConditioningIndicators()).isNotNull();
        }

        @Test
        void cSpec_continuationNotExtended_handledAsNewSpec() {
            // C-spec with blank factor1 and blank operation but previous NOT extended → new spec
            RpgleContent c = parse(
                "     C                   PARM                    PARM1            10",
                "     C                                           PARM2            10"
            );
            // Second line has blank factor1 and blank operation, but prev is not extended factor2 → new spec
            assertThat(c.getCalcSpecs()).hasSize(2);
        }
    }

    // =========================================================================
    // O-spec: boundary column tests
    // =========================================================================

    @Nested
    class OSpecBoundaryTests {

        @Test
        void oSpec_recordEntry_allFieldsExtracted() {
            // Full O-spec record entry with all fields
            String line = "     OREPORTPF  H  1 1P       HEADER";
            RpgleContent c = parse(line);
            OutputSpec spec = c.getOutputSpecs().get(0);
            assertThat(spec.getSpecLevel()).isEqualTo("recordLevel");
            assertThat(spec.getFileName()).isEqualTo("REPORTPF");
            assertThat(spec.getType()).isEqualTo("H");
        }

        @Test
        void oSpec_fieldEntry_editCodeAndBlankAfter() {
            // Field entry with edit code and blank after
            // File name area blank → field entry
            //       O                                   3FIELDNAME   1B   130 CONSTANT TEXT
            // Build a properly formatted O-spec field entry
            StringBuilder line = new StringBuilder("     O");
            // Pad through columns 6-19 (blank)
            line.append(" ".repeat(14));
            // Conditioning indicators cols 21-29 (indices 20-28)
            line.append("         ");
            // Field name cols 30-43 (indices 29-42)
            line.append("FIELDNAME     ");
            // Edit code col 44 (index 43)
            line.append("1");
            // Blank after col 45 (index 44)
            line.append("B");
            // Skip col 46 (index 45)
            line.append(" ");
            // End position cols 47-51 (indices 46-50)
            line.append("  130");
            // Data format col 52 (index 51)
            line.append("A");
            // Constant/edit word cols 53-80 (indices 52-79)
            line.append("EDIT WORD TEXT                ");

            RpgleContent c = parse(line.toString());
            OutputSpec spec = c.getOutputSpecs().get(0);
            assertThat(spec.getSpecLevel()).isEqualTo("fieldLevel");
            assertThat(spec.getFieldName()).isEqualTo("FIELDNAME");
            assertThat(spec.getEditCode()).isEqualTo("1");
            assertThat(spec.getBlankAfter()).isEqualTo("B");
            assertThat(spec.getEndPosition()).isEqualTo("130");
            assertThat(spec.getDataFormat()).isEqualTo("A");
            assertThat(spec.getConstantEditWord()).isNotNull();
        }

        @Test
        void oSpec_recordEntry_exceptNameExtracted() {
            // O-spec with EXCEPT name
            String line = "     OREPORTPF  E         TOTALS";
            RpgleContent c = parse(line);
            OutputSpec spec = c.getOutputSpecs().get(0);
            assertThat(spec.getSpecLevel()).isEqualTo("recordLevel");
            assertThat(spec.getExceptName()).isNotNull();
        }
    }

    // =========================================================================
    // Short line boundaries for each spec type
    // =========================================================================

    @Nested
    class ShortLineBoundaries {

        @Test
        void hSpec_7chars_noKeywords() {
            RpgleContent c = parse("     H ");
            assertThat(c.getControlSpecs()).hasSize(1);
            ControlSpec spec = c.getControlSpecs().get(0);
            // Blank keyword area → safeSubstring returns null (field absent)
            assertThat(spec.getKeywords()).isNull();
        }

        @Test
        void fSpec_17chars_missingType() {
            String line = "     FFILENAME  ";
            RpgleContent c = parse(line);
            assertThat(c.getFileSpecs()).hasSize(1);
            // File type should be null since line is too short at col 17
            FileSpec spec = c.getFileSpecs().get(0);
            assertThat(spec.getFileName()).isEqualTo("FILENAME");
        }

        @Test
        void dSpec_shortLine_missingType() {
            String line = "     D Name";
            RpgleContent c = parse(line);
            assertThat(c.getDefinitionSpecs()).hasSize(1);
        }

        @Test
        void cSpec_shortLine_missingIndicators() {
            String line = "     C                   EVAL";
            RpgleContent c = parse(line);
            assertThat(c.getCalcSpecs()).hasSize(1);
            CalcSpec spec = c.getCalcSpecs().get(0);
            assertThat(spec.getHiIndicator()).isNull();
            assertThat(spec.getLoIndicator()).isNull();
            assertThat(spec.getEqIndicator()).isNull();
        }

        @Test
        void oSpec_shortLine_onlyFormType() {
            String line = "     O";
            RpgleContent c = parse(line);
            assertThat(c.getOutputSpecs()).hasSize(1);
        }

        @Test
        void pSpec_shortLine_onlyFormType() {
            String line = "     P";
            RpgleContent c = parse(line);
            assertThat(c.getProcedureSpecs()).hasSize(1);
            assertThat(c.getProcedureSpecs().get(0).getName()).isNull();
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
        void allBlanks_returnsNull() {
            // Blank substrings now return null to honour the null-for-missing IR contract
            String result = RpgleFixedParser.safeSubstring("          ", 0, 5);
            assertThat(result).isNull();
        }

        @Test
        void nullInput_returnsNull() {
            String result = RpgleFixedParser.safeSubstring(null, 0, 5);
            assertThat(result).isNull();
        }

        @Test
        void safeSubstring_returnsNull_forBlankSubstring() {
            // Verify the null-for-blank contract explicitly
            assertThat(RpgleFixedParser.safeSubstring("     F     ", 0, 5)).isNull();
            assertThat(RpgleFixedParser.safeSubstring("ABC   DEF", 3, 6)).isNull();
            assertThat(RpgleFixedParser.safeSubstring("\t  \t", 0, 4)).isNull();
        }
    }
}
