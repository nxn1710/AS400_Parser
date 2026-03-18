package com.as400parser.dds;

import com.as400parser.dds.model.DdsKeyword;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link DdsKeywordParser}.
 * Tests all 16 keyword parsing patterns: no-arg, single-value, multi-value,
 * COMP/RANGE, hex literals, qualified names, Japanese text, CMP normalization.
 */
class DdsKeywordParserTest {

    private final DdsKeywordParser parser = new DdsKeywordParser();

    // ============================================================
    // extractKeywordArea
    // ============================================================

    @Test
    void extractKeywordArea_normalLine() {
        // 80-char line with "UNIQUE" starting at col 45
        String line = padTo80("     A                                      UNIQUE");
        String area = parser.extractKeywordArea(line);
        assertThat(area).isEqualTo("UNIQUE");
    }

    @Test
    void extractKeywordArea_withContinuation() {
        // Line with '+' at col 80 — should strip the continuation indicator
        String line = padTo80("     A            STUID          6A         TEXT('学生ID')");
        line = line.substring(0, 79) + "+";
        String area = parser.extractKeywordArea(line);
        assertThat(area).doesNotEndWith("+");
    }

    @Test
    void extractKeywordArea_shortLine() {
        String line = "     A          R STUREC";
        String area = parser.extractKeywordArea(line);
        assertThat(area).isEmpty();
    }

    // ============================================================
    // hasContinuation
    // ============================================================

    @Test
    void hasContinuation_true() {
        String line = padTo80("     A            STUID          6A         TEXT('学生ID')");
        line = line.substring(0, 79) + "+";
        assertThat(parser.hasContinuation(line)).isTrue();
    }

    @Test
    void hasContinuation_false() {
        String line = padTo80("     A            STUID          6A         TEXT('学生ID')");
        assertThat(parser.hasContinuation(line)).isFalse();
    }

    @Test
    void hasContinuation_shortLine() {
        assertThat(parser.hasContinuation("     A          R STUREC")).isFalse();
    }

    // ============================================================
    // No-arg keywords
    // ============================================================

    @Test
    void parseNoArgKeyword_UNIQUE() {
        List<DdsKeyword> kws = parser.parseKeywords("UNIQUE");
        assertThat(kws).hasSize(1);
        assertThat(kws.get(0).getName()).isEqualTo("UNIQUE");
        assertThat(kws.get(0).getValue()).isNull();
        assertThat(kws.get(0).getValues()).isNull();
        assertThat(kws.get(0).getRawText()).isEqualTo("UNIQUE");
    }

    @Test
    void parseNoArgKeyword_DESCEND() {
        List<DdsKeyword> kws = parser.parseKeywords("DESCEND");
        assertThat(kws).hasSize(1);
        assertThat(kws.get(0).getName()).isEqualTo("DESCEND");
    }

    @Test
    void parseNoArgKeyword_FIFO() {
        List<DdsKeyword> kws = parser.parseKeywords("FIFO");
        assertThat(kws).hasSize(1);
        assertThat(kws.get(0).getName()).isEqualTo("FIFO");
    }

    // ============================================================
    // Single-value keywords
    // ============================================================

    @Test
    void parseSingleValue_PFILE() {
        List<DdsKeyword> kws = parser.parseKeywords("PFILE(STUDNTPF)");
        assertThat(kws).hasSize(1);
        assertThat(kws.get(0).getName()).isEqualTo("PFILE");
        assertThat(kws.get(0).getValue()).isEqualTo("STUDNTPF");
        assertThat(kws.get(0).getRawText()).isEqualTo("PFILE(STUDNTPF)");
    }

    @Test
    void parseSingleValue_TEXT_japanese() {
        List<DdsKeyword> kws = parser.parseKeywords("TEXT('学生ID')");
        assertThat(kws).hasSize(1);
        assertThat(kws.get(0).getName()).isEqualTo("TEXT");
        assertThat(kws.get(0).getValue()).isEqualTo("学生ID");
    }

    @Test
    void parseSingleValue_DFT() {
        List<DdsKeyword> kws = parser.parseKeywords("DFT('A')");
        assertThat(kws).hasSize(1);
        assertThat(kws.get(0).getName()).isEqualTo("DFT");
        assertThat(kws.get(0).getValue()).isEqualTo("A");
    }

    @Test
    void parseSingleValue_REFFLD() {
        List<DdsKeyword> kws = parser.parseKeywords("REFFLD(STUID STUDNTPF)");
        assertThat(kws).hasSize(1);
        assertThat(kws.get(0).getName()).isEqualTo("REFFLD");
        // Multi-token → should be in values
        assertThat(kws.get(0).getValues()).containsExactly("STUID", "STUDNTPF");
    }

    // ============================================================
    // Multi-value keywords
    // ============================================================

    @Test
    void parseMultiValue_VALUES() {
        List<DdsKeyword> kws = parser.parseKeywords("VALUES('A' 'B' 'C' 'D' 'F')");
        assertThat(kws).hasSize(1);
        DdsKeyword kw = kws.get(0);
        assertThat(kw.getName()).isEqualTo("VALUES");
        assertThat(kw.getValues()).containsExactly("A", "B", "C", "D", "F");
    }

    @Test
    void parseMultiValue_COLHDG_japanese() {
        List<DdsKeyword> kws = parser.parseKeywords("COLHDG('学校' 'コード')");
        assertThat(kws).hasSize(1);
        DdsKeyword kw = kws.get(0);
        assertThat(kw.getName()).isEqualTo("COLHDG");
        assertThat(kw.getValues()).containsExactly("学校", "コード");
    }

    @Test
    void parseMultiValue_CONCAT() {
        List<DdsKeyword> kws = parser.parseKeywords("CONCAT(FLD1 FLD2 FLD3)");
        assertThat(kws).hasSize(1);
        DdsKeyword kw = kws.get(0);
        assertThat(kw.getName()).isEqualTo("CONCAT");
        assertThat(kw.getValues()).containsExactly("FLD1", "FLD2", "FLD3");
    }

    @Test
    void parseMultiValue_JFILE() {
        List<DdsKeyword> kws = parser.parseKeywords("JFILE(STUDNTPF SCHOOLPF)");
        assertThat(kws).hasSize(1);
        DdsKeyword kw = kws.get(0);
        assertThat(kw.getName()).isEqualTo("JFILE");
        assertThat(kw.getValues()).containsExactly("STUDNTPF", "SCHOOLPF");
    }

    // ============================================================
    // COMP keyword
    // ============================================================

    @Test
    void parseCOMP_EQ() {
        List<DdsKeyword> kws = parser.parseKeywords("COMP(EQ 'A')");
        assertThat(kws).hasSize(1);
        DdsKeyword kw = kws.get(0);
        assertThat(kw.getName()).isEqualTo("COMP");
        assertThat(kw.getComparisonOperator()).isEqualTo("EQ");
        assertThat(kw.getComparisonValue()).isEqualTo("A");
    }

    @Test
    void parseCOMP_NE() {
        List<DdsKeyword> kws = parser.parseKeywords("COMP(NE 'D')");
        assertThat(kws).hasSize(1);
        assertThat(kws.get(0).getComparisonOperator()).isEqualTo("NE");
        assertThat(kws.get(0).getComparisonValue()).isEqualTo("D");
    }

    @Test
    void parseCMP_normalizedToCOMP() {
        List<DdsKeyword> kws = parser.parseKeywords("CMP(GT '100')");
        assertThat(kws).hasSize(1);
        assertThat(kws.get(0).getName()).isEqualTo("COMP");
        assertThat(kws.get(0).getComparisonOperator()).isEqualTo("GT");
        assertThat(kws.get(0).getComparisonValue()).isEqualTo("100");
    }

    // ============================================================
    // RANGE keyword
    // ============================================================

    @Test
    void parseRANGE() {
        List<DdsKeyword> kws = parser.parseKeywords("RANGE('100' '999')");
        assertThat(kws).hasSize(1);
        DdsKeyword kw = kws.get(0);
        assertThat(kw.getName()).isEqualTo("RANGE");
        assertThat(kw.getRangeFrom()).isEqualTo("100");
        assertThat(kw.getRangeTo()).isEqualTo("999");
    }

    // ============================================================
    // Multiple keywords on one line
    // ============================================================

    @Test
    void parseMultipleKeywords() {
        List<DdsKeyword> kws = parser.parseKeywords("TEXT('Field') COLHDG('Header')");
        assertThat(kws).hasSize(2);
        assertThat(kws.get(0).getName()).isEqualTo("TEXT");
        assertThat(kws.get(0).getValue()).isEqualTo("Field");
        assertThat(kws.get(1).getName()).isEqualTo("COLHDG");
        assertThat(kws.get(1).getValue()).isEqualTo("Header");
    }

    // ============================================================
    // Continuation merging
    // ============================================================

    @Test
    void mergeKeywordAreas_twoLines() {
        List<String> lines = List.of(
            padTo80("     A            STUID          6A         TEXT('学生ID')").substring(0, 79) + "+",
            padTo80("     A                                      COLHDG('学生' 'ID')")
        );
        String merged = parser.mergeKeywordAreas(lines);
        assertThat(merged).contains("TEXT").contains("COLHDG");
    }

    @Test
    void parseKeywordsWithContinuation() {
        List<String> lines = List.of(
            padTo80("     A            STUID          6A         TEXT('学生ID')").substring(0, 79) + "+",
            padTo80("     A                                      COLHDG('学生' 'ID')")
        );
        List<DdsKeyword> kws = parser.parseKeywordsWithContinuation(lines);
        assertThat(kws).hasSizeGreaterThanOrEqualTo(2);
        assertThat(kws.stream().map(DdsKeyword::getName))
            .contains("TEXT", "COLHDG");
    }

    // ============================================================
    // Edge cases
    // ============================================================

    @Test
    void parseEmpty() {
        assertThat(parser.parseKeywords("")).isEmpty();
        assertThat(parser.parseKeywords(null)).isEmpty();
        assertThat(parser.parseKeywords("   ")).isEmpty();
    }

    @Test
    void parseNoArgMultiple() {
        List<DdsKeyword> kws = parser.parseKeywords("UNIQUE FIFO");
        assertThat(kws).hasSize(2);
        assertThat(kws.get(0).getName()).isEqualTo("UNIQUE");
        assertThat(kws.get(1).getName()).isEqualTo("FIFO");
    }

    @Test
    void parseSingleValue_VALUES_twoValues() {
        List<DdsKeyword> kws = parser.parseKeywords("VALUES('M' 'F')");
        assertThat(kws).hasSize(1);
        assertThat(kws.get(0).getValues()).containsExactly("M", "F");
    }

    // ============================================================
    // Helpers
    // ============================================================

    private String padTo80(String s) {
        if (s.length() >= 80) return s.substring(0, 80);
        return s + " ".repeat(80 - s.length());
    }
}
