package com.as400parser.rpgle;

import com.as400parser.common.normalizer.NormalizedSource;
import com.as400parser.common.normalizer.SourceNormalizer;
import com.as400parser.rpgle.model.*;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for RpgleFixedParser position-based column extraction.
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

    // ---- F-spec tests ----

    @Test
    void parseFSpec_extractsFileAttributes() {
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

    // ---- C-spec tests ----

    @Test
    void parseCSpec_simpleOperation() {
        String source = "     C                   SETON                                        LR";
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
}
