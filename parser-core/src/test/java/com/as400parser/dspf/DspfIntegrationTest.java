package com.as400parser.dspf;

import com.as400parser.common.model.IrDocument;
import com.as400parser.common.parser.ParseOptions;
import com.as400parser.dds.model.DdsKeyword;
import com.as400parser.dspf.model.*;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for DSPF parser — end-to-end parsing of sample DSPF files.
 */
class DspfIntegrationTest {

    private static final Path SAMPLE_DIR = Path.of("D:/Code/AS400_Parser/rpg3-student-mgmt/QDDSSRC");
    private final DspfParserFacade parser = new DspfParserFacade();
    private final ParseOptions opts = ParseOptions.defaults();

    // =========================================================================
    // MNUDSPF.dspf — Menu screen
    // =========================================================================

    @Test
    void parseMnudspf_metadata() {
        IrDocument doc = parser.parse(SAMPLE_DIR.resolve("MNUDSPF.dspf"), opts);

        assertThat(doc).isNotNull();
        assertThat(doc.getMetadata()).isNotNull();
        assertThat(doc.getMetadata().getSourceType()).isEqualTo("DSPF");
        assertThat(doc.getMetadata().getSourceMember()).isEqualTo("MNUDSPF");
        assertThat(doc.getMetadata().getSourceFile()).isEqualTo("QDDSSRC");
        assertThat(doc.getMetadata().getParseInfo()).isNotNull();
        assertThat(doc.getMetadata().getParseInfo().getParseStatus()).isEqualTo("complete");
    }

    @Test
    void parseMnudspf_fileKeywords() {
        IrDocument doc = parser.parse(SAMPLE_DIR.resolve("MNUDSPF.dspf"), opts);
        DspfContent content = (DspfContent) doc.getContent();

        assertThat(content.getFileKeywords()).isNotEmpty();
        // DSPSIZ, CA03, CA12
        List<String> kwNames = content.getFileKeywords().stream()
                .map(DdsKeyword::getName).toList();
        assertThat(kwNames).contains("DSPSIZ", "CA03", "CA12");
    }

    @Test
    void parseMnudspf_recordFormat() {
        IrDocument doc = parser.parse(SAMPLE_DIR.resolve("MNUDSPF.dspf"), opts);
        DspfContent content = (DspfContent) doc.getContent();

        assertThat(content.getRecordFormats()).hasSize(1);
        DspfRecordFormat rf = content.getRecordFormats().get(0);
        assertThat(rf.getName()).isEqualTo("MNUREC");
        assertThat(rf.getRecordType()).isEqualTo("normal");
        assertThat(rf.getSflControlFor()).isNull();
    }

    @Test
    void parseMnudspf_namedFields() {
        IrDocument doc = parser.parse(SAMPLE_DIR.resolve("MNUDSPF.dspf"), opts);
        DspfContent content = (DspfContent) doc.getContent();
        DspfRecordFormat rf = content.getRecordFormats().get(0);

        assertThat(rf.getFields()).hasSize(3);

        // REFCLCD — R B at 5 2 (REF field added by user)
        DspfFieldDefinition refclcd = rf.getFields().get(0);
        assertThat(refclcd.getName()).isEqualTo("REFCLCD");
        assertThat(refclcd.getReference()).isEqualTo("R");
        assertThat(refclcd.getUsage()).isEqualTo("B");
        assertThat(refclcd.getScreenLine()).isEqualTo(5);
        assertThat(refclcd.getScreenPosition()).isEqualTo(2);
        assertThat(refclcd.getSource()).isEqualTo("reference");

        // MNUOPT — 1A B at 15 34
        DspfFieldDefinition mnuopt = rf.getFields().get(1);
        assertThat(mnuopt.getName()).isEqualTo("MNUOPT");
        assertThat(mnuopt.getLength()).isEqualTo(1);
        assertThat(mnuopt.getDataType()).isEqualTo("A");
        assertThat(mnuopt.getUsage()).isEqualTo("B");
        assertThat(mnuopt.getScreenLine()).isEqualTo(15);
        assertThat(mnuopt.getScreenPosition()).isEqualTo(34);
        assertThat(mnuopt.getSource()).isEqualTo("direct");

        // MNUERR — 40A O at 18 2
        DspfFieldDefinition mnuerr = rf.getFields().get(2);
        assertThat(mnuerr.getName()).isEqualTo("MNUERR");
        assertThat(mnuerr.getLength()).isEqualTo(40);
        assertThat(mnuerr.getUsage()).isEqualTo("O");
        assertThat(mnuerr.getScreenLine()).isEqualTo(18);
        assertThat(mnuerr.getScreenPosition()).isEqualTo(2);
    }

    @Test
    void parseMnudspf_constants() {
        IrDocument doc = parser.parse(SAMPLE_DIR.resolve("MNUDSPF.dspf"), opts);
        DspfContent content = (DspfContent) doc.getContent();
        DspfRecordFormat rf = content.getRecordFormats().get(0);

        // Should have multiple constants (title, STUMGT, DATE, separator, menu items, etc.)
        assertThat(rf.getConstants()).size().isGreaterThan(5);

        // First constant: title at line 1 pos 25
        DspfConstant title = rf.getConstants().get(0);
        assertThat(title.getScreenLine()).isEqualTo(1);
        assertThat(title.getScreenPosition()).isEqualTo(25);
        assertThat(title.getText()).contains("学 生 管 理 シ ス テ ム");
        assertThat(title.getSystemKeyword()).isNull();
    }

    @Test
    void parseMnudspf_multiLineTextConstant() {
        IrDocument doc = parser.parse(SAMPLE_DIR.resolve("MNUDSPF.dspf"), opts);
        DspfContent content = (DspfContent) doc.getContent();
        DspfRecordFormat rf = content.getRecordFormats().get(0);

        // Find the 2-line text constant at screen line 4
        DspfConstant multiLine = rf.getConstants().stream()
                .filter(c -> c.getScreenLine() != null && c.getScreenLine() == 4)
                .findFirst().orElse(null);

        assertThat(multiLine).isNotNull();
        assertThat(multiLine.getScreenPosition()).isEqualTo(2);
        // Merged text from 2 continuation lines
        assertThat(multiLine.getText()).contains("このシステムは学生の登録");
        assertThat(multiLine.getText()).contains("会・更新・削除を行います");
        // Should NOT have a trailing '+' in text
        assertThat(multiLine.getText()).doesNotContain("+");
    }

    @Test
    void parseMnudspf_constantsNoSpuriousKeywords() {
        IrDocument doc = parser.parse(SAMPLE_DIR.resolve("MNUDSPF.dspf"), opts);
        DspfContent content = (DspfContent) doc.getContent();
        DspfRecordFormat rf = content.getRecordFormats().get(0);

        // Menu item constants like '1. 学生メンテナンス' should NOT have
        // spurious keywords like {"name": "1"} parsed from text after quotes
        DspfConstant menuItem = rf.getConstants().stream()
                .filter(c -> c.getText() != null && c.getText().contains("学生メンテナンス"))
                .findFirst().orElse(null);
        assertThat(menuItem).isNotNull();
        // Should have zero keywords (no spurious parsing)
        assertThat(menuItem.getKeywords()).isEmpty();
    }

    @Test
    void parseMnudspf_dateSystemKeyword() {
        IrDocument doc = parser.parse(SAMPLE_DIR.resolve("MNUDSPF.dspf"), opts);
        DspfContent content = (DspfContent) doc.getContent();
        DspfRecordFormat rf = content.getRecordFormats().get(0);

        // Find DATE constant
        DspfConstant dateConstant = rf.getConstants().stream()
                .filter(c -> "DATE".equals(c.getSystemKeyword()))
                .findFirst().orElse(null);

        assertThat(dateConstant).isNotNull();
        assertThat(dateConstant.getSystemKeyword()).isEqualTo("DATE");
        assertThat(dateConstant.getText()).isNull(); // mutually exclusive
        assertThat(dateConstant.getScreenLine()).isEqualTo(2);
        assertThat(dateConstant.getScreenPosition()).isEqualTo(70);
    }

    @Test
    void parseMnudspf_comments() {
        IrDocument doc = parser.parse(SAMPLE_DIR.resolve("MNUDSPF.dspf"), opts);
        DspfContent content = (DspfContent) doc.getContent();

        assertThat(content.getComments()).isNotEmpty();
    }

    @Test
    void parseMnudspf_sourceLines() {
        IrDocument doc = parser.parse(SAMPLE_DIR.resolve("MNUDSPF.dspf"), opts);
        DspfContent content = (DspfContent) doc.getContent();

        assertThat(content.getSourceLines()).hasSizeGreaterThanOrEqualTo(47);
    }

    @Test
    void parseMnudspf_dependencies() {
        IrDocument doc = parser.parse(SAMPLE_DIR.resolve("MNUDSPF.dspf"), opts);

        assertThat(doc.getDependencies()).isNotNull();
        // DSPF has REF(REFSAMPF) file keyword
        assertThat(doc.getDependencies().getReferencedFiles()).hasSize(1);
        assertThat(doc.getDependencies().getReferencedFiles().get(0).getName()).isEqualTo("REFSAMPF");
    }

    // =========================================================================
    // STUDSPF.dspf — Student maintenance screen (conditioned keywords)
    // =========================================================================

    @Test
    void parseStudspf_multipleRecordFormats() {
        IrDocument doc = parser.parse(SAMPLE_DIR.resolve("STUDSPF.dspf"), opts);
        DspfContent content = (DspfContent) doc.getContent();

        // STUSRCH, STUDETL, STUDEL
        assertThat(content.getRecordFormats()).hasSize(3);
        assertThat(content.getRecordFormats().get(0).getName()).isEqualTo("STUSRCH");
        assertThat(content.getRecordFormats().get(1).getName()).isEqualTo("STUDETL");
        assertThat(content.getRecordFormats().get(2).getName()).isEqualTo("STUDEL");
    }

    @Test
    void parseStudspf_conditionedKeywords() {
        IrDocument doc = parser.parse(SAMPLE_DIR.resolve("STUDSPF.dspf"), opts);
        DspfContent content = (DspfContent) doc.getContent();

        // STUDETL record → DTNAM field has conditioned keywords:
        // A N60 DSPATR(PR)  → indicator 60, negated
        // A  60 DSPATR(UL)  → indicator 60, not negated
        DspfRecordFormat studetl = content.getRecordFormats().get(1);
        DspfFieldDefinition dtnam = studetl.getFields().stream()
                .filter(f -> "DTNAM".equals(f.getName()))
                .findFirst().orElse(null);

        assertThat(dtnam).isNotNull();
        // Should have at least 2 conditioned keywords (DSPATR(PR) with N60, DSPATR(UL) with 60)
        List<ConditionedKeyword> dspatrKeywords = dtnam.getKeywords().stream()
                .filter(ck -> ck.getKeyword() != null && "DSPATR".equals(ck.getKeyword().getName()))
                .toList();
        assertThat(dspatrKeywords).hasSizeGreaterThanOrEqualTo(2);

        // Check for negated indicator
        boolean hasNegated = dspatrKeywords.stream()
                .flatMap(ck -> ck.getConditioningIndicators().stream())
                .anyMatch(ci -> ci.isNot() && "60".equals(ci.getIndicator()));
        assertThat(hasNegated).isTrue();
    }

    @Test
    void parseStudspf_fileKeywords() {
        IrDocument doc = parser.parse(SAMPLE_DIR.resolve("STUDSPF.dspf"), opts);
        DspfContent content = (DspfContent) doc.getContent();

        List<String> kwNames = content.getFileKeywords().stream()
                .map(DdsKeyword::getName).toList();
        assertThat(kwNames).contains("DSPSIZ", "CA03", "CA05", "CA06", "CA10", "CA12", "PRINT");
    }

    // =========================================================================
    // STULSTD.dspf — Subfile screen
    // =========================================================================

    @Test
    void parseStulstd_subfileRecordTypes() {
        IrDocument doc = parser.parse(SAMPLE_DIR.resolve("STULSTD.dspf"), opts);
        DspfContent content = (DspfContent) doc.getContent();

        assertThat(content.getRecordFormats()).hasSize(2);

        // SFL record
        DspfRecordFormat sfl = content.getRecordFormats().get(0);
        assertThat(sfl.getName()).isEqualTo("STUSFL");
        assertThat(sfl.getRecordType()).isEqualTo("sfl");
        assertThat(sfl.getSflControlFor()).isNull();

        // SFLCTL record
        DspfRecordFormat sflctl = content.getRecordFormats().get(1);
        assertThat(sflctl.getName()).isEqualTo("STUSFC");
        assertThat(sflctl.getRecordType()).isEqualTo("sflctl");
        assertThat(sflctl.getSflControlFor()).isEqualTo("STUSFL");
    }

    @Test
    void parseStulstd_subfileControlKeywords() {
        IrDocument doc = parser.parse(SAMPLE_DIR.resolve("STULSTD.dspf"), opts);
        DspfContent content = (DspfContent) doc.getContent();

        DspfRecordFormat sflctl = content.getRecordFormats().get(1);
        List<String> kwNames = sflctl.getKeywords().stream()
                .map(DdsKeyword::getName).toList();
        assertThat(kwNames).contains("SFLCTL", "SFLSIZ", "SFLPAG");
    }

    @Test
    void parseStulstd_conditionedSflSpecs() {
        IrDocument doc = parser.parse(SAMPLE_DIR.resolve("STULSTD.dspf"), opts);
        DspfContent content = (DspfContent) doc.getContent();

        DspfRecordFormat sflctl = content.getRecordFormats().get(1);
        // SFLDSP conditioned on indicator 40, SFLDSPCTL on 41, SFLCLR on 42, SFLEND on 43
        // These are conditioned keyword-only lines merged into the record format
        // After continuation merging, the SFLCTL format should have these keywords
        List<String> kwNames = sflctl.getKeywords().stream()
                .map(DdsKeyword::getName).toList();
        // SFLDSP, SFLDSPCTL, SFLCLR, SFLEND should be present
        assertThat(kwNames).contains("SFLDSP", "SFLDSPCTL", "SFLCLR", "SFLEND");
    }

    @Test
    void parseStulstd_sflFields() {
        IrDocument doc = parser.parse(SAMPLE_DIR.resolve("STULSTD.dspf"), opts);
        DspfContent content = (DspfContent) doc.getContent();

        DspfRecordFormat sfl = content.getRecordFormats().get(0);
        // SFOPT, SFSTID, SFNAM, SFSCL, SFGND, SFBDT, SFSTS, SFTEL
        assertThat(sfl.getFields()).hasSize(8);
        assertThat(sfl.getFields().get(0).getName()).isEqualTo("SFOPT");
        assertThat(sfl.getFields().get(0).getUsage()).isEqualTo("B");
    }

    // =========================================================================
    // All files — JSON structure consistency
    // =========================================================================

    @Test
    void allFiles_haveConsistentStructure() {
        String[] files = {"MNUDSPF.dspf", "STUDSPF.dspf", "STULSTD.dspf"};
        for (String file : files) {
            IrDocument doc = parser.parse(SAMPLE_DIR.resolve(file), opts);

            // Same IrDocument envelope as PF/LF
            assertThat(doc.getMetadata()).isNotNull();
            assertThat(doc.getContent()).isNotNull();
            assertThat(doc.getDependencies()).isNotNull();
            assertThat(doc.getErrors()).isNotNull();

            // Metadata
            assertThat(doc.getMetadata().getSourceType()).isEqualTo("DSPF");
            assertThat(doc.getMetadata().getParseInfo()).isNotNull();
            assertThat(doc.getMetadata().getParseInfo().getParseStatus()).isIn("complete", "partial");

            // Content is DspfContent
            assertThat(doc.getContent()).isInstanceOf(DspfContent.class);
            DspfContent content = (DspfContent) doc.getContent();
            assertThat(content.getSourceLines()).isNotEmpty();
            assertThat(content.getRecordFormats()).isNotEmpty();
            assertThat(content.getParseErrors()).isNotNull();
        }
    }
}
