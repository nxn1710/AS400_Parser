package com.as400parser.rpg3;

import com.as400parser.common.model.IrDocument;
import com.as400parser.common.model.Metadata;
import com.as400parser.common.parser.ParseOptions;
import com.as400parser.rpg3.model.Rpg3Content;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.assertj.core.api.Assertions.*;

class Rpg3ParserFacadeTest {

    private final Rpg3ParserFacade facade = new Rpg3ParserFacade();

    // =========================================================================
    // Basic interface conformance
    // =========================================================================

    @Test
    void sourceTypeIsRpg3() {
        assertThat(facade.getSourceType()).isEqualTo("rpg3");
    }

    @Test
    void supportedExtensionsIncludeRpg() {
        assertThat(facade.getSupportedExtensions())
                .contains(".rpg", ".rpg3");
    }

    // =========================================================================
    // Minimal source parsing
    // =========================================================================

    @Nested
    class MinimalParsing {

        @Test
        void parsesMinimalHeaderSpec() {
            // H-spec only — simplest valid RPG3 program
            String source = "     H                                                                         ";
            IrDocument doc = facade.parse(source, ParseOptions.defaults());

            assertThat(doc).isNotNull();
            assertThat(doc.getMetadata()).isNotNull();
            assertThat(doc.getMetadata().getSourceType()).isEqualTo("rpg3");
            assertThat(doc.getMetadata().getIrVersion()).isEqualTo("1.0.0");
        }

        @Test
        void parsesFileAndCalcSpec() {
            String source = String.join("\n",
                "     FCUSTMAST IF  E           K        DISK                                    ",
                "     C                     READCUSTMAST                 50                      "
            );
            IrDocument doc = facade.parse(source, ParseOptions.defaults());

            assertThat(doc).isNotNull();
            Object content = doc.getContent();
            assertThat(content).isInstanceOf(Rpg3Content.class);

            Rpg3Content rpg3 = (Rpg3Content) content;
            assertThat(rpg3.getFileSpecs()).isNotEmpty();
            assertThat(rpg3.getCalculationSpecs()).isNotEmpty();
        }

        @Test
        void metadataHasParseInfo() {
            String source = "     H                                                                         ";
            IrDocument doc = facade.parse(source, ParseOptions.defaults());

            Metadata meta = doc.getMetadata();
            assertThat(meta.getParseInfo()).isNotNull();
            assertThat(meta.getParseInfo()).containsKey("parseDate");
            assertThat(meta.getParseInfo()).containsKey("parseStatus");
            assertThat(meta.getParseInfo()).containsKey("totalLines");
            assertThat(meta.getParseInfo().get("totalLines")).isEqualTo(1);
        }

        @Test
        void completeParseHasCompleteStatus() {
            String source = "     H                                                                         ";
            IrDocument doc = facade.parse(source, ParseOptions.defaults());
            assertThat(doc.getMetadata().getParseInfo().get("parseStatus")).isEqualTo("complete");
        }
    }

    // =========================================================================
    // Error handling
    // =========================================================================

    @Nested
    class ErrorHandling {

        @Test
        void invalidSourceProducesPartialIr() {
            // Completely garbled source — parser should still produce a document
            String source = "ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ";
            IrDocument doc = facade.parse(source, ParseOptions.defaults());

            assertThat(doc).isNotNull();
            assertThat(doc.getMetadata()).isNotNull();
            // Should still have metadata even if parsing failed/partial
            assertThat(doc.getMetadata().getIrVersion()).isEqualTo("1.0.0");
        }
    }

    // =========================================================================
    // Symbol table integration
    // =========================================================================

    @Nested
    class SymbolTableIntegration {

        @Test
        void symbolTablePopulatedFromFullPipeline() {
            String source = String.join("\n",
                "     FCUSTMAST IF  E           K        DISK                                    ",
                "     I                                        1   5 CUSTNO                      ",
                "     C                     MOVELCUSTNM   DSPNAM 25                              "
            );
            IrDocument doc = facade.parse(source, ParseOptions.defaults());

            Rpg3Content rpg3 = (Rpg3Content) doc.getContent();
            // Symbol table should be populated by the pipeline
            assertThat(rpg3.getSymbolTable()).isNotEmpty();
        }
    }
}
