package com.as400parser.rpgle;

import com.as400parser.common.normalizer.NormalizedSource;
import com.as400parser.common.normalizer.SourceNormalizer;
import com.as400parser.rpgle.model.*;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for RpgleFixedParser position-based column extraction.
 * <p>
 * Column positions in test strings are 1-based per RPGLE spec:
 *   cols 1-5:  sequence number
 *   col 6:     spec type
 *   col 7+:    spec-specific fields
 * <p>
 * Java 0-based substring: sub(line, beginIdx, endIdx) where beginIdx = specPos - 1.
 */
class RpgleFixedParserTest {

    private RpgleContent parseFixed(String source) {
        SourceNormalizer normalizer = new SourceNormalizer();
        NormalizedSource normalized = normalizer.normalize(source);
        RpgleContent content = new RpgleContent();
        RpgleFixedParser parser = new RpgleFixedParser(normalized, content);
        parser.parse();
        return content;
    }

    private RpgleFixedParser parseFixedReturnParser(String source) {
        SourceNormalizer normalizer = new SourceNormalizer();
        NormalizedSource normalized = normalizer.normalize(source);
        RpgleContent content = new RpgleContent();
        RpgleFixedParser parser = new RpgleFixedParser(normalized, content);
        parser.parse();
        return parser;
    }

    /**
     * Pad string to exactly 80 chars (positions 1-80).
     */
    private static String pad80(String s) {
        if (s.length() >= 80) return s;
        return s + " ".repeat(80 - s.length());
    }

    // ---- H-spec tests ----

    @Test
    void parseHSpec_extractsKeywords() {
        String source = "     H DATFMT(*ISO) TIMFMT(*ISO) DEBUG";
        RpgleContent content = parseFixed(source);

        assertThat(content.getControlSpecs()).hasSize(1);
        ControlSpec hspec = content.getControlSpecs().get(0);
        assertThat(hspec.getFormat()).isEqualTo("fixed");
        assertThat(hspec.getKeywords()).contains("DATFMT");
        assertThat(hspec.getDateFormat()).isEqualTo("*ISO");
        assertThat(hspec.getTimeFormat()).isEqualTo("*ISO");
    }

    @Test
    void parseHSpec_extractsMainKeyword() {
        String source = "     H MAIN(MYPGM)";
        RpgleContent content = parseFixed(source);

        assertThat(content.getControlSpecs()).hasSize(1);
        ControlSpec hspec = content.getControlSpecs().get(0);
        assertThat(hspec.getMain()).isEqualTo("MYPGM");
        assertThat(hspec.getNoMain()).isNull();
    }

    @Test
    void parseHSpec_noMainDoesNotSetMain() {
        String source = "     H NOMAIN";
        RpgleContent content = parseFixed(source);

        assertThat(content.getControlSpecs()).hasSize(1);
        ControlSpec hspec = content.getControlSpecs().get(0);
        assertThat(hspec.getNoMain()).isEqualTo("*YES");
        assertThat(hspec.getMain()).isNull();
    }

    @Test
    void parseHSpec_extractsCopyrightAndAltSeq() {
        String source = "     H COPYRIGHT('My Company 2026') ALTSEQ(*EXT)";
        RpgleContent content = parseFixed(source);

        assertThat(content.getControlSpecs()).hasSize(1);
        ControlSpec hspec = content.getControlSpecs().get(0);
        assertThat(hspec.getCopyright()).isEqualTo("'My Company 2026'");
        assertThat(hspec.getAltSequence()).isEqualTo("*EXT");
    }

    // ---- F-spec tests ----

    @Test
    void parseFSpec_extractsFileAttributes() {
        // F-spec: col 6=F, 7-16=filename, 17=type, 18=desig, 19=eof, 20=add, 21=seq, 22=fmt
        String source = "     FSTUDNTPF  IF   E           K DISK";
        RpgleContent content = parseFixed(source);

        assertThat(content.getFileSpecs()).hasSize(1);
        FileSpec fspec = content.getFileSpecs().get(0);
        assertThat(fspec.getFormat()).isEqualTo("fixed");
        assertThat(fspec.getFileName()).isEqualTo("STUDNTPF");
        assertThat(fspec.getFileType()).isEqualTo("I");
    }

    @Test
    void parseFSpec_continuationLine() {
        String source =
                "     FSTUDNTPF  UF   E           K DISK\n" +
                "     F                                     RENAME(OLD:NEW)";
        RpgleContent content = parseFixed(source);

        assertThat(content.getFileSpecs()).hasSize(1);
        FileSpec fspec = content.getFileSpecs().get(0);
        assertThat(fspec.getKeywords()).contains("RENAME");
    }

    @Test
    void parseFSpec_extractsDeviceAndFormat() {
        // F-spec with WORKSTN device
        // Cols:  1234567890123456789012345678901234567890123456
        //        .....|FFFFFFFFFF|t|d|e|a|s|f|lllll|m|kkkkk|r|o|DDDDDDD
        String source = "     FMNUDSPF   CF   E             WORKSTN";
        RpgleContent content = parseFixed(source);

        assertThat(content.getFileSpecs()).hasSize(1);
        FileSpec fspec = content.getFileSpecs().get(0);
        assertThat(fspec.getFileName()).isEqualTo("MNUDSPF");
        assertThat(fspec.getFileType()).isEqualTo("C");
        assertThat(fspec.getFileDesignation()).isEqualTo("F");
        assertThat(fspec.getFileFormat()).isEqualTo("E");
        assertThat(fspec.getDevice()).isEqualTo("WORKSTN");
    }

    @Test
    void parseFSpec_extractsRecordLenKeyLenAddrTypeAndOrg() {
        // Build F-spec with exact column positions:
        //   col 6   = F
        //   col 7-16 = MYFILE    (10 chars)
        //   col 17  = I (input)
        //   col 18  = F (full procedural)
        //   col 19  = blank
        //   col 20  = blank
        //   col 21  = blank
        //   col 22  = F (program described)
        //   col 23-27 = record len 100 (right-justified in 5 chars: "  100")
        //   col 28  = blank
        //   col 29-33 = key len 5 (right-justified: "    5")
        //   col 34  = A (character key)
        //   col 35  = I (indexed)
        //   col 36-42 = DISK   (7 chars)
        //     5    |MYFILE    |I|F| | | |F|  100| |    5|A|I|DISK
        String source = "     FMYFILE    IF   F  100     5AI  DISK";
        RpgleContent content = parseFixed(source);

        assertThat(content.getFileSpecs()).hasSize(1);
        FileSpec fspec = content.getFileSpecs().get(0);
        assertThat(fspec.getFileName()).isEqualTo("MYFILE");
        assertThat(fspec.getFileFormat()).isEqualTo("F");
        assertThat(fspec.getRecordLength()).isEqualTo(100);
        assertThat(fspec.getKeyLength()).isEqualTo(5);
        assertThat(fspec.getRecordAddressType()).isEqualTo("A");
        assertThat(fspec.getFileOrganization()).isEqualTo("I");
        assertThat(fspec.getDevice()).isEqualTo("DISK");
    }

    // ---- D-spec tests ----

    @Test
    void parseDSpec_standaloneVariable() {
        String source = "     D Counter         S              5P 0 INZ(0)";
        RpgleContent content = parseFixed(source);

        assertThat(content.getDefinitionSpecs()).hasSize(1);
        DefinitionSpec dspec = content.getDefinitionSpecs().get(0);
        assertThat(dspec.getName()).isEqualTo("Counter");
        assertThat(dspec.getDefinitionType()).isEqualTo("S");
        assertThat(dspec.getInzValue()).isEqualTo("0");
    }

    @Test
    void parseDSpec_namedConstant() {
        String source = "     D MAX_ITEMS       C                   CONST(100)";
        RpgleContent content = parseFixed(source);

        assertThat(content.getDefinitionSpecs()).hasSize(1);
        DefinitionSpec dspec = content.getDefinitionSpecs().get(0);
        assertThat(dspec.getDefinitionType()).isEqualTo("C");
        assertThat(dspec.getConstValue()).isEqualTo("100");
    }

    @Test
    void parseDSpec_dataStructure() {
        String source =
                "     D StudentDS       DS\n" +
                "     D  StdName                      30A\n" +
                "     D  StdAge                        3P 0";
        RpgleContent content = parseFixed(source);

        assertThat(content.getDefinitionSpecs()).hasSize(3);
        assertThat(content.getDataStructures()).hasSize(1);
        assertThat(content.getDataStructures().get(0).getName()).isEqualTo("StudentDS");
    }

    @Test
    void parseDSpec_likeAndDimKeywords() {
        String source = "     D Arr             S                   LIKE(Counter) DIM(10)";
        RpgleContent content = parseFixed(source);

        assertThat(content.getDefinitionSpecs()).hasSize(1);
        DefinitionSpec dspec = content.getDefinitionSpecs().get(0);
        assertThat(dspec.getLikeField()).isEqualTo("Counter");
        assertThat(dspec.getDimValue()).isEqualTo("10");
    }

    @Test
    void parseDSpec_extNameKeyword() {
        String source = "     D MasterDS      E DS                  EXTNAME(MASTERPF)";
        RpgleContent content = parseFixed(source);

        assertThat(content.getDefinitionSpecs()).hasSize(1);
        DefinitionSpec dspec = content.getDefinitionSpecs().get(0);
        assertThat(dspec.getExtName()).isEqualTo("MASTERPF");
        assertThat(dspec.getExternallyDescribed()).isEqualTo("E");
    }

    @Test
    void parseDSpec_continuationLine() {
        String source =
                "     D LongName        S             50A\n" +
                "     D                                     INZ('Hello')";
        RpgleContent content = parseFixed(source);

        assertThat(content.getDefinitionSpecs()).hasSize(1);
        DefinitionSpec dspec = content.getDefinitionSpecs().get(0);
        assertThat(dspec.getKeywords()).contains("INZ");
    }

    // ---- C-spec tests ----

    @Test
    void parseCSpec_simpleOperation() {
        // C-spec: col 6=C, cols 26-35=opcode, cols 71-76=resulting indicators
        // SETON with LR indicator in the EQ position (cols 75-76)
        //       123456789012345678901234567890123456789012345678901234567890123456789012345678
        String source = pad80("     C                   SETON                                        LR");
        RpgleContent content = parseFixed(source);

        assertThat(content.getCalculationSpecs()).hasSize(1);
        assertThat(content.getCalculationSpecs().get(0)).isInstanceOf(CalcSpec.Operation.class);
        CalcSpec.Operation op = (CalcSpec.Operation) content.getCalculationSpecs().get(0);
        assertThat(op.getOpcode()).isEqualTo("SETON");
    }

    @Test
    void parseCSpec_evalExtendedFactor2() {
        String source = "     C                   EVAL      Counter = Counter + 1";
        RpgleContent content = parseFixed(source);

        assertThat(content.getCalculationSpecs()).hasSize(1);
        CalcSpec.Operation op = (CalcSpec.Operation) content.getCalculationSpecs().get(0);
        assertThat(op.getOpcode()).isEqualTo("EVAL");
        assertThat(op.getExtendedFactor2()).contains("Counter");
    }

    @Test
    void parseCSpec_resultingIndicators() {
        // READ with resulting indicator 99 in the EQ position (cols 75-76)
        // Build precisely: cols 1-5 blank, col 6='C', cols 7-25 blank, cols 26-35='READ      ',
        //   cols 36-49='STUDREC       ', cols 50-74 blank, cols 75-76='99'
        StringBuilder sb = new StringBuilder();
        sb.append("     C                   READ      STUDREC       ");  // cols 1-49
        sb.append("              ");  // cols 50-63 (result blank)
        sb.append("     ");  // cols 64-68 (field len blank)
        sb.append("  ");  // cols 69-70 (dec pos blank)
        sb.append("    ");  // cols 71-74 (HI and LO blank)
        sb.append("99");  // cols 75-76 (EQ indicator)
        String source = sb.toString();
        RpgleContent content = parseFixed(source);

        assertThat(content.getCalculationSpecs()).hasSize(1);
        CalcSpec.Operation op = (CalcSpec.Operation) content.getCalculationSpecs().get(0);
        assertThat(op.getOpcode()).isEqualTo("READ");
        assertThat(op.getResultingIndicators()).isNotNull();
        assertThat(op.getResultingIndicators().getEqual()).isNotNull();
        assertThat(op.getResultingIndicators().getEqual().getName()).isEqualTo("99");
    }

    @Test
    void parseCSpec_resultingIndicatorsHiLoEq() {
        // COMP with HI=50 (cols 71-72), LO=55 (cols 73-74), EQ=60 (cols 75-76)
        StringBuilder sb = new StringBuilder();
        sb.append("     C                   COMP      FieldB        ");  // cols 1-49
        sb.append("              ");  // cols 50-63 (result blank)
        sb.append("     ");  // cols 64-68 (field len blank)
        sb.append("  ");  // cols 69-70 (dec pos blank)
        sb.append("50");  // cols 71-72 (HI)
        sb.append("55");  // cols 73-74 (LO)
        sb.append("60");  // cols 75-76 (EQ)
        String source = sb.toString();
        RpgleContent content = parseFixed(source);

        assertThat(content.getCalculationSpecs()).hasSize(1);
        CalcSpec.Operation op = (CalcSpec.Operation) content.getCalculationSpecs().get(0);
        assertThat(op.getResultingIndicators()).isNotNull();
        assertThat(op.getResultingIndicators().getHigh()).isNotNull();
        assertThat(op.getResultingIndicators().getHigh().getName()).isEqualTo("50");
        assertThat(op.getResultingIndicators().getLow()).isNotNull();
        assertThat(op.getResultingIndicators().getLow().getName()).isEqualTo("55");
        assertThat(op.getResultingIndicators().getEqual()).isNotNull();
        assertThat(op.getResultingIndicators().getEqual().getName()).isEqualTo("60");
    }

    @Test
    void parseCSpec_operationExtender() {
        String source = "     C                   EVAL(H)   Result = Value * 1.5";
        RpgleContent content = parseFixed(source);

        assertThat(content.getCalculationSpecs()).hasSize(1);
        CalcSpec.Operation op = (CalcSpec.Operation) content.getCalculationSpecs().get(0);
        assertThat(op.getOpcode()).isEqualTo("EVAL");
        assertThat(op.getExtendedOpcode()).isEqualTo("H");
        assertThat(op.getExtendedFactor2()).contains("Result");
    }

    @Test
    void parseCSpec_conditioningIndicatorWithNegation() {
        // Conditioning indicator N50 at cols 9-11 (0-based 8-10)
        // Cols: 12345678901
        //       .....C  N50
        String source = pad80("     C  N50              SETON                                        LR");
        RpgleContent content = parseFixed(source);

        assertThat(content.getCalculationSpecs()).hasSize(1);
        CalcSpec.Operation op = (CalcSpec.Operation) content.getCalculationSpecs().get(0);
        assertThat(op.getConditioningIndicators()).hasSize(1);
        assertThat(op.getConditioningIndicators().get(0).isNegated()).isTrue();
        assertThat(op.getConditioningIndicators().get(0).getIndicator()).isEqualTo("50");
    }

    @Test
    void parseCSpec_ifBlock() {
        String source =
                "     C     *IN03         IFEQ      *ON\n" +
                "     C                   EXSR      LOADSR\n" +
                "     C                   ENDIF";
        RpgleContent content = parseFixed(source);

        assertThat(content.getCalculationSpecs()).hasSize(1);
        assertThat(content.getCalculationSpecs().get(0)).isInstanceOf(CalcSpec.ConditionalBlock.class);
        CalcSpec.ConditionalBlock block = (CalcSpec.ConditionalBlock) content.getCalculationSpecs().get(0);
        assertThat(block.getComparisonType()).isEqualTo("EQ");
        assertThat(block.getThenOps()).hasSize(1);
    }

    @Test
    void parseCSpec_elseIfChaining() {
        String source =
                "     C                   IF        x = 1\n" +
                "     C                   EVAL      y = 10\n" +
                "     C                   ELSEIF    x = 2\n" +
                "     C                   EVAL      y = 20\n" +
                "     C                   ELSE\n" +
                "     C                   EVAL      y = 30\n" +
                "     C                   ENDIF";
        RpgleContent content = parseFixed(source);

        assertThat(content.getCalculationSpecs()).hasSize(1);
        CalcSpec.ConditionalBlock outer = (CalcSpec.ConditionalBlock) content.getCalculationSpecs().get(0);
        assertThat(outer.getThenOps()).hasSize(1);
        // ELSEIF is nested as a ConditionalBlock in the else branch
        assertThat(outer.getElseOps()).hasSize(1);
        assertThat(outer.getElseOps().get(0)).isInstanceOf(CalcSpec.ConditionalBlock.class);
        CalcSpec.ConditionalBlock inner = (CalcSpec.ConditionalBlock) outer.getElseOps().get(0);
        assertThat(inner.getThenOps()).hasSize(1);
        assertThat(inner.getElseOps()).hasSize(1);
    }

    @Test
    void parseCSpec_dowBlock() {
        String source =
                "     C     *INLR         DOWEQ     *OFF\n" +
                "     C                   READ      STUDREC                              99\n" +
                "     C                   ENDDO";
        RpgleContent content = parseFixed(source);

        assertThat(content.getCalculationSpecs()).hasSize(1);
        assertThat(content.getCalculationSpecs().get(0)).isInstanceOf(CalcSpec.DoWhileBlock.class);
        CalcSpec.DoWhileBlock block = (CalcSpec.DoWhileBlock) content.getCalculationSpecs().get(0);
        assertThat(block.getComparisonType()).isEqualTo("EQ");
        assertThat(block.getBodyOps()).hasSize(1);
    }

    @Test
    void parseCSpec_douBlock() {
        String source =
                "     C     *INLR         DOUEQ     *ON\n" +
                "     C                   READ      STUDREC                              99\n" +
                "     C                   ENDDO";
        RpgleContent content = parseFixed(source);

        assertThat(content.getCalculationSpecs()).hasSize(1);
        assertThat(content.getCalculationSpecs().get(0)).isInstanceOf(CalcSpec.DoUntilBlock.class);
        CalcSpec.DoUntilBlock block = (CalcSpec.DoUntilBlock) content.getCalculationSpecs().get(0);
        assertThat(block.getComparisonType()).isEqualTo("EQ");
        assertThat(block.getBodyOps()).hasSize(1);
    }

    @Test
    void parseCSpec_forBlock() {
        String source =
                "     C                   FOR       i = 1 TO 10\n" +
                "     C                   EVAL      Total = Total + Arr(i)\n" +
                "     C                   ENDFOR";
        RpgleContent content = parseFixed(source);

        assertThat(content.getCalculationSpecs()).hasSize(1);
        assertThat(content.getCalculationSpecs().get(0)).isInstanceOf(CalcSpec.ForBlock.class);
        CalcSpec.ForBlock block = (CalcSpec.ForBlock) content.getCalculationSpecs().get(0);
        assertThat(block.getStartExpression()).contains("i = 1");
        assertThat(block.getBodyOps()).hasSize(1);
    }

    @Test
    void parseCSpec_monitorBlock() {
        String source =
                "     C                   MONITOR\n" +
                "     C                   EVAL      x = y / z\n" +
                "     C                   ON-ERROR  00102\n" +
                "     C                   EVAL      x = 0\n" +
                "     C                   ENDMON";
        RpgleContent content = parseFixed(source);

        assertThat(content.getCalculationSpecs()).hasSize(1);
        assertThat(content.getCalculationSpecs().get(0)).isInstanceOf(CalcSpec.MonitorBlock.class);
        CalcSpec.MonitorBlock block = (CalcSpec.MonitorBlock) content.getCalculationSpecs().get(0);
        assertThat(block.getBodyOps()).hasSize(1);
        assertThat(block.getOnErrorClauses()).hasSize(1);
        assertThat(block.getOnErrorClauses().get(0).getErrorCodes()).contains("00102");
        assertThat(block.getOnErrorClauses().get(0).getBodyOps()).hasSize(1);
    }

    @Test
    void parseCSpec_selectBlock() {
        String source =
                "     C                   SELECT\n" +
                "     C     Action        WHENEQ    'ADD'\n" +
                "     C                   EXSR      ADDSR\n" +
                "     C                   OTHER\n" +
                "     C                   EXSR      DFLTSR\n" +
                "     C                   ENDSL";
        RpgleContent content = parseFixed(source);

        assertThat(content.getCalculationSpecs()).hasSize(1);
        assertThat(content.getCalculationSpecs().get(0)).isInstanceOf(CalcSpec.SelectBlock.class);
        CalcSpec.SelectBlock block = (CalcSpec.SelectBlock) content.getCalculationSpecs().get(0);
        assertThat(block.getWhenClauses()).hasSize(1);
        assertThat(block.getOtherOps()).hasSize(1);
    }

    @Test
    void parseCSpec_subroutine() {
        String source =
                "     C     LOADSR        BEGSR\n" +
                "     C                   CHAIN     StdKey        STUDREC               50\n" +
                "     C                   ENDSR";
        RpgleContent content = parseFixed(source);

        assertThat(content.getCalculationSpecs()).hasSize(1);
        assertThat(content.getCalculationSpecs().get(0)).isInstanceOf(CalcSpec.SubroutineBlock.class);
        CalcSpec.SubroutineBlock block = (CalcSpec.SubroutineBlock) content.getCalculationSpecs().get(0);
        assertThat(block.getSubroutineName()).isEqualTo("LOADSR");
        assertThat(block.getOperations()).hasSize(1);
    }


    // ---- P-spec tests ----


    @Test
    void parsePSpec_procedureBoundary() {
        String source =
                "     P ProcessData     B                   EXPORT\n" +
                "     P ProcessData     E";
        RpgleContent content = parseFixed(source);

        assertThat(content.getProcedureSpecs()).hasSize(2);
        assertThat(content.getProcedureSpecs().get(0).getBeginEnd()).isEqualTo("B");
        assertThat(content.getProcedureSpecs().get(0).getProcedureName()).isEqualTo("ProcessData");
        assertThat(content.getProcedureSpecs().get(1).getBeginEnd()).isEqualTo("E");
    }

    // ---- Comment tests ----

    @Test
    void parsesFullLineComments() {
        String source =
                "     ** This is a comment\n" +
                "     H DATFMT(*ISO)";
        RpgleContent content = parseFixed(source);

        assertThat(content.getComments()).hasSize(1);
    }

    @Test
    void parsesInlineSpecComments() {
        String source = "     C* This is a C-spec comment";
        RpgleContent content = parseFixed(source);

        assertThat(content.getComments()).hasSize(1);
        assertThat(content.getComments().get(0).getSpecContext()).isEqualTo("C");
    }

    // ---- /COPY directive tests ----

    @Test
    void parsesCopyDirective() {
        String source = "      /COPY QCPYSRC,SCHOOLCPY";
        RpgleFixedParser parser = parseFixedReturnParser(source);

        assertThat(parser.getCopyDirectives()).hasSize(1);
        assertThat(parser.getCopyDirectives().get(0)).contains("COPY");
        assertThat(parser.getCopyDirectives().get(0)).contains("SCHOOLCPY");
    }

    @Test
    void parsesIncludeDirective() {
        String source = "      /INCLUDE QCPYSRC,STUDNTCPY";
        RpgleFixedParser parser = parseFixedReturnParser(source);

        assertThat(parser.getCopyDirectives()).hasSize(1);
        assertThat(parser.getCopyDirectives().get(0)).contains("INCLUDE");
    }

    // ---- Short-line edge cases ----

    @Test
    void shortLineShorterThan80Chars_doesNotCrash() {
        String source = "     D ShortFld        S              5P 0";
        RpgleContent content = parseFixed(source);

        assertThat(content.getDefinitionSpecs()).hasSize(1);
        DefinitionSpec dspec = content.getDefinitionSpecs().get(0);
        assertThat(dspec.getName()).isEqualTo("ShortFld");
        assertThat(dspec.getDefinitionType()).isEqualTo("S");
    }

    @Test
    void lineShorterThan6Chars_isSkipped() {
        String source = "    ";
        RpgleContent content = parseFixed(source);

        assertThat(content.getControlSpecs()).isEmpty();
        assertThat(content.getFileSpecs()).isEmpty();
        assertThat(content.getDefinitionSpecs()).isEmpty();
        assertThat(content.getCalculationSpecs()).isEmpty();
    }
}
