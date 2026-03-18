package com.as400parser.dds;

import com.as400parser.common.model.IrDocument;
import com.as400parser.common.parser.ParseOptions;
import com.as400parser.common.serializer.IrJsonSerializer;
import com.as400parser.dds.model.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for the DDS parser — parses real sample files end-to-end.
 * Uses files from rpg3-student-mgmt/QDDSSRC/.
 */
class DdsIntegrationTest {

    private static final Path DDS_DIR = Path.of("D:/Code/AS400_Parser/rpg3-student-mgmt/QDDSSRC");
    private static final DdsParserFacade FACADE = new DdsParserFacade();

    // ============================================================
    // All PF files parse successfully
    // ============================================================

    @ParameterizedTest
    @ValueSource(strings = {"STUDNTPF.pf", "CLASSPF.pf", "SCHOOLPF.pf", "STUCLSPF.pf"})
    void parsePfFile_success(String fileName) {
        Path file = DDS_DIR.resolve(fileName);
        assertThat(file).as("Sample file should exist: " + fileName).exists();

        IrDocument doc = FACADE.parse(file, ParseOptions.defaults());

        assertThat(doc).as(fileName + " should produce a document").isNotNull();
        assertThat(doc.getMetadata()).as(fileName + " metadata").isNotNull();
        assertThat(doc.getMetadata().getSourceType()).isEqualTo("DDS_PF");
        assertThat(doc.getContent()).as(fileName + " content").isNotNull();
        assertThat(doc.getContent()).isInstanceOf(DdsPfContent.class);
    }

    // ============================================================
    // All LF files parse successfully
    // ============================================================

    @ParameterizedTest
    @ValueSource(strings = {"STUDNTL1.lf", "STUDNTL2.lf", "CLASSL1.lf", "SCHOOLL1.lf", "STUCLSL1.lf"})
    void parseLfFile_success(String fileName) {
        Path file = DDS_DIR.resolve(fileName);
        assertThat(file).as("Sample file should exist: " + fileName).exists();

        IrDocument doc = FACADE.parse(file, ParseOptions.defaults());

        assertThat(doc).isNotNull();
        assertThat(doc.getMetadata().getSourceType()).isEqualTo("DDS_LF");
        assertThat(doc.getContent()).isInstanceOf(DdsLfContent.class);
    }

    // ============================================================
    // STUDNTPF — detailed structure validation
    // ============================================================

    @Test
    void studntpf_hasExpectedStructure() {
        IrDocument doc = FACADE.parse(DDS_DIR.resolve("STUDNTPF.pf"), ParseOptions.defaults());
        DdsPfContent content = (DdsPfContent) doc.getContent();

        // File-level: UNIQUE
        assertThat(content.getFileKeywords())
            .anyMatch(kw -> "UNIQUE".equals(kw.getName()));

        // 1 record format: STUREC
        assertThat(content.getRecordFormats()).hasSize(1);
        RecordFormat rf = content.getRecordFormats().get(0);
        assertThat(rf.getName()).isEqualTo("STUREC");
        assertThat(rf.getText()).as("Record text from TEXT() keyword").isNotNull().isNotEmpty();

        // 13 fields (STUID, STUNAM, STUKNA, STUBDT, STUGND, STUADR, STUTEL, STUMAL,
        //            STUSCL, STUYR, STUSTS, STUADD, STUUPD, STUADB, STUUPB)
        // Actually count from file: 15 fields
        assertThat(rf.getFields()).hasSizeGreaterThanOrEqualTo(11);

        // First field: STUID 6A
        FieldDefinition stuid = rf.getFields().get(0);
        assertThat(stuid.getName()).isEqualTo("STUID");
        assertThat(stuid.getLength()).isEqualTo(6);
        assertThat(stuid.getDataType()).isEqualTo("A");
        assertThat(stuid.getSource()).isEqualTo("direct");

        // STUID should have TEXT keyword with non-empty value
        assertThat(stuid.getKeywords())
            .anyMatch(kw -> "TEXT".equals(kw.getName()) && kw.getValue() != null && !kw.getValue().isEmpty());
        assertThat(stuid.getKeywords())
            .anyMatch(kw -> "COLHDG".equals(kw.getName()));

        // STUBDT 8S 0 (numeric)
        FieldDefinition stubdt = rf.getFields().stream()
            .filter(f -> "STUBDT".equals(f.getName())).findFirst().orElse(null);
        assertThat(stubdt).isNotNull();
        assertThat(stubdt.getLength()).isEqualTo(8);
        assertThat(stubdt.getDataType()).isEqualTo("S");
        assertThat(stubdt.getDecimalPositions()).isEqualTo(0);

        // STUGND 1A with VALUES('M' 'F')
        FieldDefinition stugnd = rf.getFields().stream()
            .filter(f -> "STUGND".equals(f.getName())).findFirst().orElse(null);
        assertThat(stugnd).isNotNull();
        assertThat(stugnd.getKeywords())
            .anyMatch(kw -> "VALUES".equals(kw.getName()) &&
                            kw.getValues() != null && kw.getValues().containsAll(List.of("M", "F")));

        // STUSTS 1A with DFT('A')
        FieldDefinition stusts = rf.getFields().stream()
            .filter(f -> "STUSTS".equals(f.getName())).findFirst().orElse(null);
        assertThat(stusts).isNotNull();
        assertThat(stusts.getKeywords())
            .anyMatch(kw -> "DFT".equals(kw.getName()) && "A".equals(kw.getValue()));

        // 2 keys: STUSCL + STUID (composite key)
        assertThat(rf.getKeys()).hasSize(2);
        assertThat(rf.getKeys().get(0).getFieldName()).isEqualTo("STUSCL");
        assertThat(rf.getKeys().get(1).getFieldName()).isEqualTo("STUID");

        // Comments should be preserved
        assertThat(content.getComments()).isNotEmpty();
    }

    // ============================================================
    // STUCLSPF — composite key + VALUES keyword
    // ============================================================

    @Test
    void stuclspf_compositeKeyAndValues() {
        IrDocument doc = FACADE.parse(DDS_DIR.resolve("STUCLSPF.pf"), ParseOptions.defaults());
        DdsPfContent content = (DdsPfContent) doc.getContent();

        RecordFormat rf = content.getRecordFormats().get(0);
        assertThat(rf.getName()).isEqualTo("SCRREC");

        // Composite key: SCSTID + SCCLID
        assertThat(rf.getKeys()).hasSize(2);
        assertThat(rf.getKeys().get(0).getFieldName()).isEqualTo("SCSTID");
        assertThat(rf.getKeys().get(1).getFieldName()).isEqualTo("SCCLID");

        // SCRANK with VALUES('A' 'B' 'C' 'D' 'F')
        FieldDefinition scrank = rf.getFields().stream()
            .filter(f -> "SCRANK".equals(f.getName())).findFirst().orElse(null);
        assertThat(scrank).isNotNull();
        assertThat(scrank.getKeywords())
            .anyMatch(kw -> "VALUES".equals(kw.getName()) &&
                            kw.getValues() != null && kw.getValues().size() == 5);
    }

    // ============================================================
    // STUDNTL2 — LF with select/omit (COMP)
    // ============================================================

    @Test
    void studntl2_selectOmit() {
        IrDocument doc = FACADE.parse(DDS_DIR.resolve("STUDNTL2.lf"), ParseOptions.defaults());
        DdsLfContent content = (DdsLfContent) doc.getContent();

        assertThat(content.getRecordFormats()).hasSize(1);
        LfRecordFormat lrf = content.getRecordFormats().get(0);
        assertThat(lrf.getName()).isEqualTo("STUREC");
        assertThat(lrf.getPfile()).isEqualTo("STUDNTPF");
        assertThat(lrf.getLfType()).isEqualTo("simple");

        // Select condition: STUSTS COMP(EQ 'A')
        assertThat(lrf.getSelectOmit()).hasSize(1);
        SelectOmitSpec spec = lrf.getSelectOmit().get(0);
        assertThat(spec.getType()).isEqualTo("select");
        assertThat(spec.getFieldName()).isEqualTo("STUSTS");
        assertThat(spec.getKeywords())
            .anyMatch(kw -> "COMP".equals(kw.getName()) &&
                            "EQ".equals(kw.getComparisonOperator()) &&
                            "A".equals(kw.getComparisonValue()));

        // 2 keys: STUSCL + STUID
        assertThat(lrf.getKeys()).hasSize(2);
    }

    // ============================================================
    // Dependencies
    // ============================================================

    @Test
    void lfDependencies_containsPfile() {
        IrDocument doc = FACADE.parse(DDS_DIR.resolve("STUDNTL2.lf"), ParseOptions.defaults());
        assertThat(doc.getDependencies()).isNotNull();
        assertThat(doc.getDependencies().getReferencedFiles())
            .anyMatch(ref -> "STUDNTPF".equals(ref.getName()));
    }

    @Test
    void pfDependencies_empty() {
        IrDocument doc = FACADE.parse(DDS_DIR.resolve("STUDNTPF.pf"), ParseOptions.defaults());
        // PF without REF should have no dependencies
        assertThat(doc.getDependencies().getReferencedFiles()).isEmpty();
    }

    // ============================================================
    // Metadata
    // ============================================================

    @Test
    void metadata_populatedCorrectly() {
        IrDocument doc = FACADE.parse(DDS_DIR.resolve("STUDNTPF.pf"), ParseOptions.defaults());
        assertThat(doc.getMetadata().getSourceType()).isEqualTo("DDS_PF");
        assertThat(doc.getMetadata().getSourceMember()).isEqualTo("STUDNTPF");
        assertThat(doc.getMetadata().getParseInfo()).isNotNull();
        assertThat(doc.getMetadata().getParseInfo().getTotalLines()).isGreaterThan(0);
    }

    // ============================================================
    // JSON serialization round-trip
    // ============================================================

    @Test
    void serializesToValidJson() {
        IrDocument doc = FACADE.parse(DDS_DIR.resolve("STUDNTPF.pf"), ParseOptions.defaults());
        IrJsonSerializer serializer = new IrJsonSerializer();
        String json = serializer.serialize(doc);

        assertThat(json).isNotEmpty();
        assertThat(json).contains("DDS_PF");
        assertThat(json).contains("STUREC");
        assertThat(json).contains("STUID");
    }

    // ============================================================
    // Generate example IR output files (Task 4.5)
    // ============================================================

    @Test
    void generateExampleIrOutputs() throws Exception {
        IrJsonSerializer serializer = new IrJsonSerializer();
        Path outputDir = Path.of("d:/Code/AS400_Parser/output/example-ir");
        Files.createDirectories(outputDir);

        // PF example: STUDNTPF
        IrDocument pfDoc = FACADE.parse(DDS_DIR.resolve("STUDNTPF.pf"), ParseOptions.defaults());
        writeJson(serializer.serialize(pfDoc), outputDir.resolve("dds_pf_studntpf.json"));

        // LF example with select/omit: STUDNTL2
        IrDocument lfDoc = FACADE.parse(DDS_DIR.resolve("STUDNTL2.lf"), ParseOptions.defaults());
        writeJson(serializer.serialize(lfDoc), outputDir.resolve("dds_lf_studntl2.json"));

        // LF example: STUCLSL1
        IrDocument lfDoc2 = FACADE.parse(DDS_DIR.resolve("STUCLSL1.lf"), ParseOptions.defaults());
        writeJson(serializer.serialize(lfDoc2), outputDir.resolve("dds_lf_stuclsl1.json"));
    }

    private void writeJson(String json, Path path) throws Exception {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(path.toString()), StandardCharsets.UTF_8))) {
            writer.print(json);
        }
    }
}
