package com.as400parser.dds;

import com.as400parser.dds.model.*;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link DdsIrBuilder}.
 * Tests the IR content building from normalized source lines.
 */
class DdsIrBuilderTest {

    private final DdsIrBuilder builder = new DdsIrBuilder();

    // ============================================================
    // Physical File content building
    // ============================================================

    @Test
    void buildPfContent_minimalFile() {
        String[] lines = {
            pad("     A                                      UNIQUE"),
            pad("     A          R TESTREC                   TEXT('Test Record')"),
            pad("     A            FIELD1        10A         TEXT('Field One')"),
            pad("     A          K FIELD1")
        };
        Object result = builder.buildContent(lines, "DDS_PF");
        assertThat(result).isInstanceOf(DdsPfContent.class);

        DdsPfContent content = (DdsPfContent) result;
        assertThat(content.getSourceLines()).hasSize(4);
        assertThat(content.getFileKeywords()).hasSize(1);
        assertThat(content.getFileKeywords().get(0).getName()).isEqualTo("UNIQUE");
        assertThat(content.getRecordFormats()).hasSize(1);

        RecordFormat rf = content.getRecordFormats().get(0);
        assertThat(rf.getName()).isEqualTo("TESTREC");
        assertThat(rf.getText()).isEqualTo("Test Record");
        assertThat(rf.getFields()).hasSize(1);
        assertThat(rf.getKeys()).hasSize(1);

        FieldDefinition field = rf.getFields().get(0);
        assertThat(field.getName()).isEqualTo("FIELD1");
        assertThat(field.getLength()).isEqualTo(10);
        assertThat(field.getDataType()).isEqualTo("A");

        KeyDefinition key = rf.getKeys().get(0);
        assertThat(key.getFieldName()).isEqualTo("FIELD1");
        assertThat(key.getSortOrder()).isEqualTo("ascending");
    }

    @Test
    void buildPfContent_commentsPreserved() {
        String[] lines = {
            pad("     A*This is a comment"),
            pad("     A          R REC1"),
            pad("     A*Another comment"),
            pad("     A            FLD1          5A")
        };
        DdsPfContent content = (DdsPfContent) builder.buildContent(lines, "DDS_PF");
        assertThat(content.getComments()).hasSize(2);
        assertThat(content.getComments().get(0).getText()).isEqualTo("This is a comment");
        assertThat(content.getComments().get(1).getText()).isEqualTo("Another comment");
    }

    @Test
    void buildPfContent_numericField() {
        String[] lines = {
            pad("     A          R REC1"),
            buildFieldLine("AMOUNT", "7", "S", "2", null)
        };
        DdsPfContent content = (DdsPfContent) builder.buildContent(lines, "DDS_PF");
        FieldDefinition field = content.getRecordFormats().get(0).getFields().get(0);
        assertThat(field.getName()).isEqualTo("AMOUNT");
        assertThat(field.getLength()).isEqualTo(7);
        assertThat(field.getDataType()).isEqualTo("S");
        assertThat(field.getDecimalPositions()).isEqualTo(2);
        assertThat(field.getSource()).isEqualTo("direct");
    }

    @Test
    void buildPfContent_referenceField() {
        String[] lines = {
            pad("     A          R REC1"),
            buildRefFieldLine("FLD1", "REFFLD(SRCFLD SRCPF)")
        };
        DdsPfContent content = (DdsPfContent) builder.buildContent(lines, "DDS_PF");
        FieldDefinition field = content.getRecordFormats().get(0).getFields().get(0);
        assertThat(field.getReference()).isEqualTo("R");
        assertThat(field.getSource()).isEqualTo("reference");
    }

    @Test
    void buildPfContent_descendingKey() {
        String[] lines = {
            pad("     A          R REC1"),
            pad("     A            FLD1          5A"),
            buildKeyLine("FLD1", "DESCEND")
        };
        DdsPfContent content = (DdsPfContent) builder.buildContent(lines, "DDS_PF");
        assertThat(content.getRecordFormats().get(0).getKeys().get(0).getSortOrder())
            .isEqualTo("descending");
    }

    // ============================================================
    // Logical File content building
    // ============================================================

    @Test
    void buildLfContent_simpleLf() {
        String[] lines = {
            pad("     A          R STUREC                    PFILE(STUDNTPF)"),
            pad("     A          K STUID")
        };
        Object result = builder.buildContent(lines, "DDS_LF");
        assertThat(result).isInstanceOf(DdsLfContent.class);

        DdsLfContent content = (DdsLfContent) result;
        assertThat(content.getRecordFormats()).hasSize(1);

        LfRecordFormat lrf = content.getRecordFormats().get(0);
        assertThat(lrf.getName()).isEqualTo("STUREC");
        assertThat(lrf.getPfile()).isEqualTo("STUDNTPF");
        assertThat(lrf.getLfType()).isEqualTo("simple");
        assertThat(lrf.getKeys()).hasSize(1);
    }

    @Test
    void buildLfContent_selectOmit() {
        String[] lines = {
            pad("     A          R STUREC                    PFILE(STUDNTPF)"),
            buildSelectLine("STUSTS", "COMP(EQ 'A')"),
            pad("     A          K STUID")
        };
        DdsLfContent content = (DdsLfContent) builder.buildContent(lines, "DDS_LF");
        LfRecordFormat lrf = content.getRecordFormats().get(0);
        assertThat(lrf.getSelectOmit()).hasSize(1);

        SelectOmitSpec spec = lrf.getSelectOmit().get(0);
        assertThat(spec.getType()).isEqualTo("select");
        assertThat(spec.getFieldName()).isEqualTo("STUSTS");
        assertThat(spec.getKeywords()).anyMatch(kw ->
            "COMP".equals(kw.getName()) && "EQ".equals(kw.getComparisonOperator()));
    }

    @Test
    void buildLfContent_multipleFormat() {
        String[] lines = {
            pad("     A          R REC1                      PFILE(PF1)"),
            pad("     A          K FLD1"),
            pad("     A          R REC2                      PFILE(PF2)"),
            pad("     A          K FLD2")
        };
        DdsLfContent content = (DdsLfContent) builder.buildContent(lines, "DDS_LF");
        assertThat(content.getRecordFormats()).hasSize(2);
        assertThat(content.getRecordFormats().get(0).getLfType()).isEqualTo("multipleFormat");
        assertThat(content.getRecordFormats().get(1).getLfType()).isEqualTo("multipleFormat");
    }

    @Test
    void buildLfContent_joinLf() {
        String[] lines = {
            pad("     A          R JOINREC                   JFILE(PF1 PF2)"),
            buildJoinLine("JOIN(PF1 PF2)"),
            buildJoinLine("JFLD(FLD1 FLD2)"),
            pad("     A          K FLD1")
        };
        DdsLfContent content = (DdsLfContent) builder.buildContent(lines, "DDS_LF");
        LfRecordFormat lrf = content.getRecordFormats().get(0);
        assertThat(lrf.getLfType()).isEqualTo("join");
        assertThat(lrf.getJfile()).containsExactly("PF1", "PF2");
        assertThat(lrf.getJoinSpecs()).isNotEmpty();
    }

    // ============================================================
    // Error recovery
    // ============================================================

    @Test
    void buildContent_errorRecovery() {
        // Even with bad data, should not throw exception
        String[] lines = {
            pad("     A          R REC1"),
            pad("     A            FLD1          5A"),
            "short line",  // bad line
            pad("     A            FLD2         10A")
        };
        DdsPfContent content = (DdsPfContent) builder.buildContent(lines, "DDS_PF");
        // Should still produce output
        assertThat(content).isNotNull();
        assertThat(content.getRecordFormats()).hasSize(1);
    }

    // ============================================================
    // Helpers
    // ============================================================

    private String pad(String s) {
        if (s.length() >= 80) return s.substring(0, 80);
        return s + " ".repeat(80 - s.length());
    }

    /**
     * Build a field definition line with specific column values.
     */
    private String buildFieldLine(String name, String length, String dataType, String decimal, String keywords) {
        StringBuilder sb = new StringBuilder(80);
        sb.append("     A            "); // cols 1-18
        // cols 19-28: name
        String paddedName = (name + "          ").substring(0, 10);
        sb.append(paddedName);
        // col 29: reference (blank)
        sb.append(' ');
        // cols 30-34: length (right-justified)
        String paddedLen = ("     " + (length == null ? "" : length));
        sb.append(paddedLen.substring(paddedLen.length() - 5));
        // col 35: data type
        sb.append(dataType == null ? ' ' : dataType.charAt(0));
        // cols 36-37: decimal
        String paddedDec = ("  " + (decimal == null ? "" : decimal));
        sb.append(paddedDec.substring(paddedDec.length() - 2));
        // col 38: usage (blank)
        sb.append(' ');
        // cols 39-44: location (blank)
        sb.append("      ");
        // cols 45-80: keywords
        String kw = keywords == null ? "" : keywords;
        if (kw.length() < 36) kw = kw + " ".repeat(36 - kw.length());
        sb.append(kw.substring(0, Math.min(36, kw.length())));
        // Pad to 80
        while (sb.length() < 80) sb.append(' ');
        return sb.toString().substring(0, 80);
    }

    private String buildRefFieldLine(String name, String keywords) {
        StringBuilder sb = new StringBuilder(80);
        sb.append("     A            "); // cols 1-18
        String paddedName = (name + "          ").substring(0, 10);
        sb.append(paddedName);
        sb.append('R'); // col 29: reference indicator
        sb.append("     "); // cols 30-34: length
        sb.append(' '); // col 35: data type
        sb.append("  "); // cols 36-37: decimal
        sb.append(' '); // col 38: usage
        sb.append("      "); // cols 39-44: location
        String kw = keywords == null ? "" : keywords;
        if (kw.length() < 36) kw = kw + " ".repeat(36 - kw.length());
        sb.append(kw.substring(0, Math.min(36, kw.length())));
        while (sb.length() < 80) sb.append(' ');
        return sb.toString().substring(0, 80);
    }

    private String buildKeyLine(String fieldName, String keywords) {
        StringBuilder sb = new StringBuilder(80);
        sb.append("     A          K "); // cols 1-18 with K at col 17
        String paddedName = (fieldName + "          ").substring(0, 10);
        sb.append(paddedName);
        sb.append("                "); // cols 29-44
        String kw = keywords == null ? "" : keywords;
        if (kw.length() < 36) kw = kw + " ".repeat(36 - kw.length());
        sb.append(kw.substring(0, Math.min(36, kw.length())));
        while (sb.length() < 80) sb.append(' ');
        return sb.toString().substring(0, 80);
    }

    private String buildSelectLine(String fieldName, String keywords) {
        StringBuilder sb = new StringBuilder(80);
        sb.append("     A          S "); // cols 1-18 with S at col 17
        String paddedName = (fieldName + "          ").substring(0, 10);
        sb.append(paddedName);
        sb.append("                "); // cols 29-44
        String kw = keywords == null ? "" : keywords;
        if (kw.length() < 36) kw = kw + " ".repeat(36 - kw.length());
        sb.append(kw.substring(0, Math.min(36, kw.length())));
        while (sb.length() < 80) sb.append(' ');
        return sb.toString().substring(0, 80);
    }

    private String buildJoinLine(String keywords) {
        StringBuilder sb = new StringBuilder(80);
        sb.append("     A          J "); // cols 1-18 with J at col 17
        sb.append("          "); // cols 19-28: no name
        sb.append("                "); // cols 29-44
        String kw = keywords == null ? "" : keywords;
        if (kw.length() < 36) kw = kw + " ".repeat(36 - kw.length());
        sb.append(kw.substring(0, Math.min(36, kw.length())));
        while (sb.length() < 80) sb.append(' ');
        return sb.toString().substring(0, 80);
    }
}
