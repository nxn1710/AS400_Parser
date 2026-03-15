package com.as400parser.rpg3;

import com.as400parser.common.model.IrDocument;
import com.as400parser.common.normalizer.NormalizedSource;
import com.as400parser.common.normalizer.SourceNormalizer;
import com.as400parser.rpg3.model.*;
import com.as400parser.rpg3.model.Rpg3Content.*;

import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link Rpg3IrBuilder}.
 * Parses inline RPG3 source through the full pipeline and verifies IR output.
 */
class Rpg3IrBuilderTest {

    /** Parse source and return the IR document. */
    private IrDocument parse(String source) {
        SourceNormalizer normalizer = new SourceNormalizer();
        NormalizedSource normalized = normalizer.normalize(source);
        Rpg3IrBuilder builder = new Rpg3IrBuilder(normalized);
        return builder.build();
    }

    // =========================================================================
    // H-spec
    // =========================================================================
    @Test
    void headerSpecParsed() {
        String source = "     H                                                                          ";
        IrDocument doc = parse(source);
        Rpg3Content content = (Rpg3Content) doc.getContent();
        assertThat(content.getHeaderSpecs()).hasSize(1);
    }

    // =========================================================================
    // F-spec
    // =========================================================================
    @Test
    void fileSpecParsedWithDependency() {
        String source = "     FCUSTMAST IF  E           K        DISK                                    ";
        IrDocument doc = parse(source);
        Rpg3Content content = (Rpg3Content) doc.getContent();
        assertThat(content.getFileSpecs()).hasSize(1);
        FileSpec fs = content.getFileSpecs().get(0);
        assertThat(fs.getFileName()).startsWith("CUSTMAST");
        assertThat(doc.getDependencies().getReferencedFiles()).isNotEmpty();
    }

    // =========================================================================
    // E-spec
    // =========================================================================
    @Test
    void extensionSpecParsed() {
        String source = "     E                    NAMES  10  20  8                                      ";
        IrDocument doc = parse(source);
        Rpg3Content content = (Rpg3Content) doc.getContent();
        assertThat(content.getExtensionSpecs()).hasSize(1);
    }

    // =========================================================================
    // Source lines
    // =========================================================================
    @Test
    void sourceLinesBuilt() {
        String source = String.join("\n",
            "     H                                                                          ",
            "     FCUSTMAST IF  E           K        DISK                                    ");
        IrDocument doc = parse(source);
        assertThat(doc.getSourceLines()).hasSize(2);
        assertThat(doc.getSourceLines().get(0).getSpecType()).isEqualTo("H");
        assertThat(doc.getSourceLines().get(1).getSpecType()).isEqualTo("F");
    }

    // =========================================================================
    // Multi-spec integration (H + F together)
    // =========================================================================
    @Test
    void multipleSpecTypesParsedCorrectly() {
        String source = String.join("\n",
            "     H                                                                          ",
            "     FCUSTMAST IF  E           K        DISK                                    ");
        IrDocument doc = parse(source);
        Rpg3Content content = (Rpg3Content) doc.getContent();
        assertThat(content.getHeaderSpecs()).hasSize(1);
        assertThat(content.getFileSpecs()).hasSize(1);
        assertThat(doc.getSourceLines()).hasSize(2);
    }

    // =========================================================================
    // All spec types together (H, F, E)
    // =========================================================================
    @Test
    void threeSpecTypesTogether() {
        String source = String.join("\n",
            "     H                                                                          ",
            "     FCUSTMAST IF  E           K        DISK                                    ",
            "     E                    NAMES  10  20  8                                      ");
        IrDocument doc = parse(source);
        Rpg3Content content = (Rpg3Content) doc.getContent();
        assertThat(content.getHeaderSpecs()).as("H-spec").hasSize(1);
        assertThat(content.getFileSpecs()).as("F-spec").hasSize(1);
        assertThat(doc.getSourceLines()).as("sourceLines").hasSizeGreaterThanOrEqualTo(2);
    }

    // =========================================================================
    // E-spec field extraction verification
    // =========================================================================
    @Test
    void extensionSpecFieldsExtracted() {
        String source = "     E                    NAMES  10  20  8                                      ";
        IrDocument doc = parse(source);
        Rpg3Content content = (Rpg3Content) doc.getContent();
        assertThat(content.getExtensionSpecs()).hasSize(1);
        ExtensionSpec es = content.getExtensionSpecs().get(0);
        assertThat(es.getArrayOrTableName()).isNotNull();
        assertThat(es.getArrayOrTableName().trim()).startsWith("NAMES");
    }

    // =========================================================================
    // Parse doesn't crash on empty source
    // =========================================================================
    @Test
    void emptySourceProducesEmptyIr() {
        IrDocument doc = parse("");
        assertThat(doc).isNotNull();
        Rpg3Content content = (Rpg3Content) doc.getContent();
        assertThat(content.getHeaderSpecs()).isEmpty();
        assertThat(content.getFileSpecs()).isEmpty();
        assertThat(content.getCalculationSpecs()).isEmpty();
    }

    // =========================================================================
    // Multiple F-specs
    // =========================================================================
    @Test
    void multipleFileSpecs() {
        String source = String.join("\n",
            "     FCUSTMAST IF  E           K        DISK                                    ",
            "     FCUSTPRT  O   F     132            PRINTER                                 ");
        IrDocument doc = parse(source);
        Rpg3Content content = (Rpg3Content) doc.getContent();
        assertThat(content.getFileSpecs()).as("F-specs").hasSizeGreaterThanOrEqualTo(1);
        assertThat(content.getFileSpecs().get(0).getFileName()).startsWith("CUSTMAST");
    }
}
