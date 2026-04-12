package com.as400parser.rpgle;

import com.as400parser.common.model.IrDocument;
import com.as400parser.common.cli.As400ParserCli;
import com.as400parser.common.parser.As400Parser;
import com.as400parser.common.parser.ParseOptions;
import com.as400parser.rpgle.model.RpgleContent;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

import java.nio.file.Path;

/**
 * Integration tests for the full RPGLE parser pipeline.
 * Tests RpgleParserFacade end-to-end with real source files.
 */
class RpgleParserIntegrationTest {

    private final RpgleParserFacade facade = new RpgleParserFacade();

    // ---- Fixed-format tests ----

    @Test
    void parsesFixedFormatFile() {
        Path source = Path.of("src/test/resources/rpgle/simple_fixed.rpgle");
        IrDocument doc = facade.parse(source, ParseOptions.defaults());

        assertThat(doc).isNotNull();
        assertThat(doc.getContent()).isInstanceOf(RpgleContent.class);
        assertThat(doc.getMetadata()).isNotNull();
        assertThat(doc.getMetadata().getSourceType()).isEqualTo("RPGLE");
        assertThat(doc.getMetadata().getParseInfo()).isNotNull();
        assertThat(doc.getMetadata().getParseInfo().getTotalLines()).isGreaterThan(0);

        RpgleContent content = (RpgleContent) doc.getContent();
        assertThat(content.getControlSpecs()).isNotEmpty();
        assertThat(content.getFileSpecs()).isNotEmpty();
        assertThat(content.getDefinitionSpecs()).isNotEmpty();
        assertThat(content.getCalculationSpecs()).isNotEmpty();
        assertThat(content.getOutputSpecs()).isNotEmpty();
        assertThat(content.getSourceLines()).isNotEmpty();
    }

    @Test
    void fixedFormat_extractsSymbolTable() {
        Path source = Path.of("src/test/resources/rpgle/simple_fixed.rpgle");
        IrDocument doc = facade.parse(source, ParseOptions.defaults());

        RpgleContent content = (RpgleContent) doc.getContent();
        assertThat(content.getSymbolTable()).isNotEmpty();

        // Should have entries for D-spec definitions
        boolean hasCounter = content.getSymbolTable().stream()
                .anyMatch(s -> "Counter".equals(s.getName()));
        assertThat(hasCounter).isTrue();
    }

    @Test
    void fixedFormat_extractsDependencies() {
        Path source = Path.of("src/test/resources/rpgle/simple_fixed.rpgle");
        IrDocument doc = facade.parse(source, ParseOptions.defaults());

        assertThat(doc.getDependencies()).isNotNull();
        assertThat(doc.getDependencies().getReferencedFiles()).isNotEmpty();

        boolean hasStudntPF = doc.getDependencies().getReferencedFiles().stream()
                .anyMatch(r -> "STUDNTPF".equals(r.getName()));
        assertThat(hasStudntPF).isTrue();
    }

    @Test
    void fixedFormat_extractsSubroutines() {
        Path source = Path.of("src/test/resources/rpgle/simple_fixed.rpgle");
        IrDocument doc = facade.parse(source, ParseOptions.defaults());

        RpgleContent content = (RpgleContent) doc.getContent();
        assertThat(content.getSubroutines()).isNotEmpty();
        assertThat(content.getSubroutines().get(0).getSubroutineName()).isEqualTo("LOADSR");
    }

    // ---- Free-format tests ----

    @Test
    void parsesFullyFreeFormatFile() {
        Path source = Path.of("src/test/resources/rpgle/fully_free.rpgle");
        IrDocument doc = facade.parse(source, ParseOptions.defaults());

        assertThat(doc).isNotNull();
        assertThat(doc.getContent()).isInstanceOf(RpgleContent.class);
        assertThat(doc.getMetadata().getSourceType()).isEqualTo("RPGLE");

        RpgleContent content = (RpgleContent) doc.getContent();
        assertThat(content.getControlSpecs()).isNotEmpty();
        assertThat(content.getFileSpecs()).isNotEmpty();
        assertThat(content.getDefinitionSpecs()).isNotEmpty();
        assertThat(content.getProcedureSpecs()).isNotEmpty();
    }

    @Test
    void fullyFree_extractsDataStructures() {
        Path source = Path.of("src/test/resources/rpgle/fully_free.rpgle");
        IrDocument doc = facade.parse(source, ParseOptions.defaults());

        RpgleContent content = (RpgleContent) doc.getContent();
        assertThat(content.getDataStructures()).isNotEmpty();
        assertThat(content.getDataStructures().get(0).getName()).isEqualTo("StudentDS");
    }

    @Test
    void fullyFree_extractsProcedures() {
        Path source = Path.of("src/test/resources/rpgle/fully_free.rpgle");
        IrDocument doc = facade.parse(source, ParseOptions.defaults());

        RpgleContent content = (RpgleContent) doc.getContent();
        assertThat(content.getProcedureSpecs()).hasSizeGreaterThanOrEqualTo(2);

        boolean hasBegin = content.getProcedureSpecs().stream()
                .anyMatch(p -> "B".equals(p.getBeginEnd()) && "ProcessStudents".equals(p.getProcedureName()));
        assertThat(hasBegin).isTrue();
    }

    // ---- Mixed-format tests ----

    @Test
    void parsesMixedFormatFile() {
        Path source = Path.of("src/test/resources/rpgle/mixed_format.rpgle");
        IrDocument doc = facade.parse(source, ParseOptions.defaults());

        assertThat(doc).isNotNull();
        assertThat(doc.getContent()).isInstanceOf(RpgleContent.class);

        RpgleContent content = (RpgleContent) doc.getContent();
        // Should have fixed H-spec, F-spec, D-spec, and C-spec
        assertThat(content.getControlSpecs()).isNotEmpty();
        assertThat(content.getFileSpecs()).isNotEmpty();
        assertThat(content.getDefinitionSpecs()).isNotEmpty();
        // Should have free-format statements from /FREE block
        assertThat(content.getFreeFormatStatements()).isNotEmpty();
    }

    // ---- String-based parsing ----

    @Test
    void parsesFromString() {
        String source = "     H DATFMT(*ISO)\n     D MyVar          S             10A";
        IrDocument doc = facade.parse(source, ParseOptions.defaults());

        assertThat(doc).isNotNull();

        RpgleContent content = (RpgleContent) doc.getContent();
        assertThat(content.getControlSpecs()).hasSize(1);
        assertThat(content.getDefinitionSpecs()).hasSize(1);
    }

    // ---- Facade metadata tests ----

    @Test
    void getSourceType_returnsRPGLE() {
        assertThat(facade.getSourceType()).isEqualTo("RPGLE");
    }

    @Test
    void getSupportedExtensions_includesRpgle() {
        assertThat(facade.getSupportedExtensions()).contains(".rpgle", ".sqlrpgle", ".rpgleinc");
    }

    @Test
    void cliSelectParser_choosesRpgleParserForRpgleExtensions() {
        As400Parser rpgle = As400ParserCli.selectParser(Path.of("MYPGM.rpgle"));
        As400Parser include = As400ParserCli.selectParser(Path.of("COPYLIB.rpgleinc"));

        assertThat(rpgle).isInstanceOf(RpgleParserFacade.class);
        assertThat(include).isInstanceOf(RpgleParserFacade.class);
    }

    // ---- JSON Serialization round-trip ----

    @Test
    void producesValidJson() {
        String source = "     H DATFMT(*ISO)\n     D X              S              5P 0";
        IrDocument doc = facade.parse(source, ParseOptions.defaults());

        com.as400parser.common.serializer.IrJsonSerializer serializer =
                new com.as400parser.common.serializer.IrJsonSerializer();
        String json = serializer.serialize(doc);

        assertThat(json).isNotBlank();
        assertThat(json).contains("\"sourceType\"");
        assertThat(json).contains("\"RPGLE\"");
        assertThat(json).contains("\"controlSpecs\"");
        assertThat(json).contains("\"definitionSpecs\"");
    }
}
