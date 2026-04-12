package com.as400parser.rpgle;

import com.as400parser.rpgle.model.*;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for RpgleFreeParser text-based statement extraction.
 */
class RpgleFreeParserTest {

    private RpgleContent parseFree(String source) {
        RpgleContent content = new RpgleContent();
        RpgleFreeParser parser = new RpgleFreeParser(content);
        parser.parse(source, 1);
        return content;
    }

    @Test
    void parsesCtlOpt() {
        String source = "**FREE\nCTL-OPT DATFMT(*ISO) TIMFMT(*ISO) DEBUG;";
        RpgleContent content = parseFree(source);

        assertThat(content.getControlSpecs()).hasSize(1);
        ControlSpec spec = content.getControlSpecs().get(0);
        assertThat(spec.getFormat()).isEqualTo("free");
        assertThat(spec.getKeywords()).contains("DATFMT");
    }

    @Test
    void parsesDclF() {
        String source = "**FREE\nDCL-F STUDNTPF DISK(*UPDATE) KEYED;";
        RpgleContent content = parseFree(source);

        assertThat(content.getFileSpecs()).hasSize(1);
        FileSpec fspec = content.getFileSpecs().get(0);
        assertThat(fspec.getFileName()).isEqualTo("STUDNTPF");
        assertThat(fspec.getFormat()).isEqualTo("free");
    }

    @Test
    void parsesDclS() {
        String source = "**FREE\nDCL-S Counter PACKED(5:0) INZ(0);";
        RpgleContent content = parseFree(source);

        assertThat(content.getDefinitionSpecs()).hasSize(1);
        DefinitionSpec dspec = content.getDefinitionSpecs().get(0);
        assertThat(dspec.getName()).isEqualTo("Counter");
        assertThat(dspec.getDefinitionType()).isEqualTo("S");
    }

    @Test
    void parsesDclC() {
        String source = "**FREE\nDCL-C MAX_ITEMS CONST(100);";
        RpgleContent content = parseFree(source);

        assertThat(content.getDefinitionSpecs()).hasSize(1);
        DefinitionSpec dspec = content.getDefinitionSpecs().get(0);
        assertThat(dspec.getDefinitionType()).isEqualTo("C");
        assertThat(dspec.getName()).isEqualTo("MAX_ITEMS");
    }

    @Test
    void parsesDclDs() {
        String source = "**FREE\nDCL-DS StudentDS;\n  StdName CHAR(30);\n  StdAge PACKED(3:0);\nEND-DS;";
        RpgleContent content = parseFree(source);

        assertThat(content.getDefinitionSpecs()).isNotEmpty();
        boolean hasDS = content.getDefinitionSpecs().stream()
                .anyMatch(d -> "DS".equals(d.getDefinitionType()));
        assertThat(hasDS).isTrue();
        assertThat(content.getDataStructures()).hasSize(1);
    }

    @Test
    void parsesDclProc() {
        String source = "**FREE\nDCL-PROC ProcessStudents EXPORT;\nEND-PROC;";
        RpgleContent content = parseFree(source);

        assertThat(content.getProcedureSpecs()).hasSize(2);
        assertThat(content.getProcedureSpecs().get(0).getBeginEnd()).isEqualTo("B");
        assertThat(content.getProcedureSpecs().get(0).getProcedureName()).isEqualTo("ProcessStudents");
        assertThat(content.getProcedureSpecs().get(1).getBeginEnd()).isEqualTo("E");
    }

    @Test
    void parsesIfStatement() {
        String source = "**FREE\nIF pAction = 'LOAD';\nEXSR LoadSR;\nENDIF;";
        RpgleContent content = parseFree(source);

        boolean hasIf = content.getFreeFormatStatements().stream()
                .anyMatch(f -> "if".equals(f.getNodeType()));
        assertThat(hasIf).isTrue();
    }

    @Test
    void parsesDowStatement() {
        String source = "**FREE\nDOW NOT %EOF(STUDNTPF);\nREAD STUDNTPF;\nENDDO;";
        RpgleContent content = parseFree(source);

        boolean hasDow = content.getFreeFormatStatements().stream()
                .anyMatch(f -> "doWhile".equals(f.getNodeType()));
        assertThat(hasDow).isTrue();
    }

    @Test
    void parsesComments() {
        String source = "**FREE\n// This is a comment\nDCL-S X CHAR(10);";
        RpgleContent content = parseFree(source);

        assertThat(content.getComments()).hasSize(1);
        assertThat(content.getComments().get(0).getText()).isEqualTo("This is a comment");
    }

    @Test
    void parsesCopyDirective() {
        String source = "**FREE\n/COPY QCPYSRC,STUDNTCPY;";
        RpgleContent content = new RpgleContent();
        RpgleFreeParser parser = new RpgleFreeParser(content);
        parser.parse(source, 1);

        assertThat(parser.getCopyDirectives()).hasSize(1);
        assertThat(parser.getCopyDirectives().get(0)).contains("/COPY");
    }

    @Test
    void parsesMultiLineStatement() {
        String source = "**FREE\nDCL-S VeryLongVariableName\n  CHAR(100)\n  INZ('Hello');";
        RpgleContent content = parseFree(source);

        assertThat(content.getDefinitionSpecs()).hasSize(1);
        assertThat(content.getDefinitionSpecs().get(0).getName()).isEqualTo("VeryLongVariableName");
    }
}
