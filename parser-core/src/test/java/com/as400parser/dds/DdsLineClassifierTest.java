package com.as400parser.dds;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link DdsLineClassifier}.
 * Tests all 10 DDS line types with proper column-position data.
 */
class DdsLineClassifierTest {

    private final DdsLineClassifier classifier = new DdsLineClassifier();

    // ============================================================
    // Comment lines (col 7 = '*')
    // ============================================================

    @Test
    void classifyComment() {
        assertThat(classifier.classify(pad("     A*Comment text"), false))
            .isEqualTo(DdsLineType.COMMENT);
        assertThat(classifier.classify(pad("     A*Comment text"), true))
            .isEqualTo(DdsLineType.COMMENT);
    }

    // ============================================================
    // Record format (col 17 = 'R')
    // ============================================================

    @Test
    void classifyRecordFormat() {
        // "     A          R STUREC" — col 17 = R
        String line = buildLine(' ', 'R', "STUREC", "");
        assertThat(classifier.classify(line, false)).isEqualTo(DdsLineType.RECORD_FORMAT);
        assertThat(classifier.classify(line, true)).isEqualTo(DdsLineType.RECORD_FORMAT);
    }

    // ============================================================
    // Key field (col 17 = 'K')
    // ============================================================

    @Test
    void classifyKeyField() {
        String line = buildLine(' ', 'K', "STUID", "");
        assertThat(classifier.classify(line, true)).isEqualTo(DdsLineType.KEY_FIELD);
    }

    // ============================================================
    // Select field (col 17 = 'S')
    // ============================================================

    @Test
    void classifySelectField() {
        String line = buildLine(' ', 'S', "STUSTS", "COMP(EQ 'A')");
        assertThat(classifier.classify(line, true)).isEqualTo(DdsLineType.SELECT_FIELD);
    }

    // ============================================================
    // Omit field (col 17 = 'O')
    // ============================================================

    @Test
    void classifyOmitField() {
        String line = buildLine(' ', 'O', "STUSTS", "COMP(EQ 'D')");
        assertThat(classifier.classify(line, true)).isEqualTo(DdsLineType.OMIT_FIELD);
    }

    // ============================================================
    // Join spec (col 17 = 'J')
    // ============================================================

    @Test
    void classifyJoinSpec() {
        String line = buildLine(' ', 'J', "", "JOIN(STUDNTPF SCHOOLPF)");
        assertThat(classifier.classify(line, true)).isEqualTo(DdsLineType.JOIN_SPEC);
    }

    // ============================================================
    // Field definition (col 17 = blank, has name in 19-28)
    // ============================================================

    @Test
    void classifyFieldDefinition() {
        // "     A            STUID      6A" — col 17 blank, name present
        String line = buildLine(' ', ' ', "STUID", "TEXT('Field')");
        assertThat(classifier.classify(line, true)).isEqualTo(DdsLineType.FIELD_DEFINITION);
    }

    // ============================================================
    // File keyword (col 17 = blank, no name, BEFORE record format)
    // ============================================================

    @Test
    void classifyFileKeyword_beforeRecord() {
        String line = buildLine(' ', ' ', "", "UNIQUE");
        assertThat(classifier.classify(line, false)).isEqualTo(DdsLineType.FILE_KEYWORD);
    }

    // ============================================================
    // Continuation (col 17 = blank, no name, AFTER record format)
    // ============================================================

    @Test
    void classifyContinuation_afterRecord() {
        String line = buildLine(' ', ' ', "", "COLHDG('Header')");
        assertThat(classifier.classify(line, true)).isEqualTo(DdsLineType.CONTINUATION);
    }

    // ============================================================
    // Blank lines
    // ============================================================

    @Test
    void classifyBlank() {
        assertThat(classifier.classify("", false)).isEqualTo(DdsLineType.BLANK);
        assertThat(classifier.classify("        ", false)).isEqualTo(DdsLineType.BLANK);
    }

    // ============================================================
    // Edge cases
    // ============================================================

    @Test
    void classifyShortLine() {
        assertThat(classifier.classify("     A", true)).isIn(DdsLineType.BLANK, DdsLineType.FILE_KEYWORD, DdsLineType.CONTINUATION);
    }

    @Test
    void classifyNonAFormType() {
        // col 6 not 'A' — treated as blank or comment depending on implementation
        String line = pad("     H*Header spec");
        DdsLineType type = classifier.classify(line, false);
        // Should not crash
        assertThat(type).isNotNull();
    }

    // ============================================================
    // Helpers
    // ============================================================

    /**
     * Build a 80-char DDS line with specified entry type and name.
     * @param commentChar col 7 char ('*' for comment, ' ' for normal)
     * @param entryType   col 17 char (R/K/S/O/J/' ')
     * @param name        cols 19-28 (field/record name)
     * @param keywords    cols 45-80 (keyword area)
     */
    private String buildLine(char commentChar, char entryType, String name, String keywords) {
        StringBuilder sb = new StringBuilder(80);
        // Cols 1-5: sequence
        sb.append("     ");
        // Col 6: form type
        sb.append('A');
        // Col 7: comment indicator
        sb.append(commentChar);
        // Cols 8-16: conditioning indicators (blank)
        sb.append("         ");
        // Col 17: entry type
        sb.append(entryType);
        // Col 18: reserved
        sb.append(' ');
        // Cols 19-28: name (right-padded to 10)
        String paddedName = name.length() >= 10 ? name.substring(0, 10) : name + " ".repeat(10 - name.length());
        sb.append(paddedName);
        // Cols 29-44: ref, length, type, decimal, usage, location (all blank)
        sb.append("                ");
        // Cols 45-80: keywords
        String kwPadded = keywords.length() >= 36 ? keywords.substring(0, 36) : keywords + " ".repeat(36 - keywords.length());
        sb.append(kwPadded);
        return sb.toString();
    }

    private String pad(String s) {
        if (s.length() >= 80) return s.substring(0, 80);
        return s + " ".repeat(80 - s.length());
    }
}
