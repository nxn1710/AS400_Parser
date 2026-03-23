package com.as400parser.dspf;

import com.as400parser.dds.model.DdsKeyword;
import com.as400parser.dspf.model.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link DspfIrBuilder} — isolated builder logic tests
 * using synthetic DDS lines (no file I/O).
 * <p>
 * Each test feeds normalized 80-char lines to {@code buildContent()}
 * and verifies the resulting {@link DspfContent} structure.
 * <p>
 * DDS A-spec column layout (1-indexed):
 * <pre>
 *   1-5:   Sequence number (ignored)
 *   6:     Form type ('A')
 *   7:     Comment ('*') or blank
 *   8-16:  Conditioning indicators (3 slots × 3 chars)
 *   17:    Name type ('R'=record, 'K'=key, ' '=other)
 *   18:    Reserved (blank)
 *   19-28: Name (10 chars)
 *   29:    Reference ('R' or blank)
 *   30-34: Length (5 digits)
 *   35:    Data type (A, S, P, B, etc.)
 *   36-37: Decimal positions
 *   38:    Usage (B, I, O, H, M, P)
 *   39-41: Screen line (3 digits)
 *   42-44: Screen position (3 digits)
 *   45-80: Keywords (36 chars)
 * </pre>
 */
class DspfIrBuilderTest {

    private final DspfIrBuilder builder = new DspfIrBuilder();

    /**
     * Pad a source line to 80 chars (simulates SourceNormalizer behavior).
     */
    private String pad(String line) {
        if (line.length() >= 80) return line.substring(0, 80);
        return line + " ".repeat(80 - line.length());
    }

    private DspfContent build(String... lines) {
        return builder.buildContent(Arrays.stream(lines).map(this::pad).toList());
    }

    // DDS line helpers — use exact column positions
    //                     1234567890123456789012345678901234567890123456789012345678901234567890123456789012345
    //                              ci       typ  name        ref len  dt dc us ln  pos keywords
    // Example real line: "     A            MNUOPT         1A  B 15 34"
    //                     ^^^^^^ ^^^^^^^^^^ ^ ^^^^^^^^^^ ^ ^^^^^ ^ ^^ ^ ^^^ ^^^ ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

    private static final String REC_MYREC = "     A          R MYREC";
    private static final String REC_REC1  = "     A          R REC1";
    private static final String REC_REC2  = "     A          R REC2";
    private static final String REC_REC3  = "     A          R REC3";

    // =========================================================================
    // Line Classification
    // =========================================================================

    @Nested
    class LineClassification {

        @Test
        void blankLine_isSkipped() {
            DspfContent c = build("", "     A*comment");
            assertThat(c.getSourceLines()).hasSize(2);
            assertThat(c.getSourceLines().get(0).isBlank()).isTrue();
        }

        @Test
        void nonASpec_isSkipped() {
            DspfContent c = build("     H  some header");
            assertThat(c.getSourceLines()).hasSize(1);
            assertThat(c.getSourceLines().get(0).isBlank()).isTrue();
        }

        @Test
        void commentLine_captured() {
            DspfContent c = build("     A* This is a comment");
            assertThat(c.getComments()).hasSize(1);
            assertThat(c.getComments().get(0).getText()).isEqualTo("This is a comment");
            assertThat(c.getSourceLines().get(0).getSpecType()).isEqualTo("COMMENT");
        }

        @Test
        void fileKeyword_beforeRecord() {
            DspfContent c = build("     A                                      DSPSIZ(24 80 *DS3)");
            assertThat(c.getFileKeywords()).hasSize(1);
            assertThat(c.getFileKeywords().get(0).getName()).isEqualTo("DSPSIZ");
            assertThat(c.getSourceLines().get(0).getSpecType()).isEqualTo("FILE_KEYWORD");
        }

        @Test
        void multipleFileKeywords() {
            DspfContent c = build(
                "     A                                      DSPSIZ(24 80 *DS3)",
                "     A                                      CA03(03 'Exit')",
                "     A                                      CA12(12 'Cancel')"
            );
            assertThat(c.getFileKeywords()).hasSize(3);
            List<String> names = c.getFileKeywords().stream().map(DdsKeyword::getName).toList();
            assertThat(names).containsExactly("DSPSIZ", "CA03", "CA12");
        }

        @Test
        void recordFormat_classified() {
            DspfContent c = build(REC_MYREC);
            assertThat(c.getRecordFormats()).hasSize(1);
            assertThat(c.getRecordFormats().get(0).getName()).isEqualTo("MYREC");
            assertThat(c.getSourceLines().get(0).getSpecType()).isEqualTo("RECORD_FORMAT");
        }
    }

    // =========================================================================
    // Record Formats
    // =========================================================================

    @Nested
    class RecordFormats {

        @Test
        void normalRecordType() {
            DspfContent c = build(REC_MYREC);
            DspfRecordFormat rf = c.getRecordFormats().get(0);
            assertThat(rf.getRecordType()).isEqualTo("normal");
            assertThat(rf.getSflControlFor()).isNull();
        }

        @Test
        void sflRecordType() {
            DspfContent c = build("     A          R SFLREC                    SFL");
            DspfRecordFormat rf = c.getRecordFormats().get(0);
            assertThat(rf.getRecordType()).isEqualTo("sfl");
        }

        @Test
        void sflctlRecordType() {
            DspfContent c = build("     A          R CTLREC                    SFLCTL(SFLREC)");
            DspfRecordFormat rf = c.getRecordFormats().get(0);
            assertThat(rf.getRecordType()).isEqualTo("sflctl");
            assertThat(rf.getSflControlFor()).isEqualTo("SFLREC");
        }

        @Test
        void recordWithTextKeyword() {
            DspfContent c = build("     A          R MYREC                     TEXT('My Record')");
            DspfRecordFormat rf = c.getRecordFormats().get(0);
            assertThat(rf.getText()).isEqualTo("My Record");
        }

        @Test
        void multipleRecordFormats() {
            DspfContent c = build(REC_REC1, REC_REC2, REC_REC3);
            assertThat(c.getRecordFormats()).hasSize(3);
            assertThat(c.getRecordFormats().get(0).getName()).isEqualTo("REC1");
            assertThat(c.getRecordFormats().get(1).getName()).isEqualTo("REC2");
            assertThat(c.getRecordFormats().get(2).getName()).isEqualTo("REC3");
        }
    }

    // =========================================================================
    // Field Definition
    // =========================================================================

    @Nested
    class FieldDefinitions {

        @Test
        void directField_allColumns() {
            //                        1234567890123456789012345678901234567890123456789012345678
            //                                 ci       typ  name       ref len  dt dc us ln  pos kw
            DspfContent c = build(
                REC_MYREC,
                "     A            MYFIELD       10A 0B  5 20"
            );
            DspfFieldDefinition f = c.getRecordFormats().get(0).getFields().get(0);
            assertThat(f.getName()).isEqualTo("MYFIELD");
            assertThat(f.getLength()).isEqualTo(10);
            assertThat(f.getDataType()).isEqualTo("A");
            assertThat(f.getDecimalPositions()).isEqualTo(0);
            assertThat(f.getUsage()).isEqualTo("B");
            assertThat(f.getScreenLine()).isEqualTo(5);
            assertThat(f.getScreenPosition()).isEqualTo(20);
            assertThat(f.getSource()).isEqualTo("direct");
        }

        @Test
        void referenceField_withR() {
            //                                          28=R
            DspfContent c = build(
                REC_MYREC,
                "     A            MYFIELD   R        B  5 20"
            );
            DspfFieldDefinition f = c.getRecordFormats().get(0).getFields().get(0);
            assertThat(f.getName()).isEqualTo("MYFIELD");
            assertThat(f.getReference()).isEqualTo("R");
            assertThat(f.getSource()).isEqualTo("reference");
            assertThat(f.getReferenceField()).isEqualTo("MYFIELD"); // same-name ref
            assertThat(f.getLength()).isNull(); // not directly specified
        }

        @Test
        void referenceField_withReffld() {
            DspfContent c = build(
                REC_MYREC,
                "     A            MYFIELD   R        B  5 20REFFLD(OTHFLD)"
            );
            DspfFieldDefinition f = c.getRecordFormats().get(0).getFields().get(0);
            assertThat(f.getReferenceField()).isEqualTo("OTHFLD");
            assertThat(f.getSource()).isEqualTo("reference");
        }

        @Test
        void fieldWithKeywords() {
            // Use real-style layout: MNUOPT         1A  B 15 34DSPATR(HI)
            DspfContent c = build(
                REC_MYREC,
                "     A            MYFIELD        5A  B  5 20DSPATR(HI)"
            );
            DspfFieldDefinition f = c.getRecordFormats().get(0).getFields().get(0);
            assertThat(f.getKeywords()).hasSize(1);
            assertThat(f.getKeywords().get(0).getKeyword().getName()).isEqualTo("DSPATR");
            assertThat(f.getKeywords().get(0).getKeyword().getValue()).isEqualTo("HI");
        }

        @Test
        void fieldWithUsageTypes() {
            // Input — usage at col 38 (idx 37)
            DspfContent ci = build(REC_MYREC, "     A            F1             5A  I  1  1");
            assertThat(ci.getRecordFormats().get(0).getFields().get(0).getUsage()).isEqualTo("I");

            // Output
            DspfContent co = build(REC_MYREC, "     A            F1             5A  O  1  1");
            assertThat(co.getRecordFormats().get(0).getFields().get(0).getUsage()).isEqualTo("O");

            // Hidden (no screen position)
            DspfContent ch = build(REC_MYREC, "     A            F1             5A  H");
            assertThat(ch.getRecordFormats().get(0).getFields().get(0).getUsage()).isEqualTo("H");
        }

        @Test
        void multipleFieldsUnderRecord() {
            DspfContent c = build(
                REC_MYREC,
                "     A            FIELD1         5A  B  1  1",
                "     A            FIELD2        10A  O  2  1",
                "     A            FIELD3         3S 0I  3  1"
            );
            DspfRecordFormat rf = c.getRecordFormats().get(0);
            assertThat(rf.getFields()).hasSize(3);
            assertThat(rf.getFields().get(0).getName()).isEqualTo("FIELD1");
            assertThat(rf.getFields().get(1).getName()).isEqualTo("FIELD2");
            assertThat(rf.getFields().get(2).getName()).isEqualTo("FIELD3");
            assertThat(rf.getFields().get(2).getDataType()).isEqualTo("S");
            assertThat(rf.getFields().get(2).getDecimalPositions()).isEqualTo(0);
        }

        @Test
        void field_rawSourceLines() {
            DspfContent c = build(
                REC_MYREC,
                "     A            MYFIELD        5A  B  1  1"
            );
            DspfFieldDefinition f = c.getRecordFormats().get(0).getFields().get(0);
            assertThat(f.getRawSourceLines()).hasSize(1);
            assertThat(f.getRawSourceLines().get(0)).contains("MYFIELD");
        }
    }

    // =========================================================================
    // Constants
    // =========================================================================

    @Nested
    class Constants {

        @Test
        void quotedTextConstant() {
            DspfContent c = build(
                REC_MYREC,
                "     A                                  1 25'Hello World'"
            );
            DspfRecordFormat rf = c.getRecordFormats().get(0);
            assertThat(rf.getConstants()).hasSize(1);
            DspfConstant ct = rf.getConstants().get(0);
            assertThat(ct.getText()).isEqualTo("Hello World");
            assertThat(ct.getSystemKeyword()).isNull();
            assertThat(ct.getScreenLine()).isEqualTo(1);
            assertThat(ct.getScreenPosition()).isEqualTo(25);
        }

        @Test
        void systemKeyword_DATE() {
            DspfContent c = build(
                REC_MYREC,
                "     A                                  2 70DATE"
            );
            DspfConstant ct = c.getRecordFormats().get(0).getConstants().get(0);
            assertThat(ct.getSystemKeyword()).isEqualTo("DATE");
            assertThat(ct.getText()).isNull();
            assertThat(ct.getScreenLine()).isEqualTo(2);
            assertThat(ct.getScreenPosition()).isEqualTo(70);
        }

        @Test
        void systemKeyword_TIME() {
            DspfContent c = build(
                REC_MYREC,
                "     A                                  1  1TIME"
            );
            DspfConstant ct = c.getRecordFormats().get(0).getConstants().get(0);
            assertThat(ct.getSystemKeyword()).isEqualTo("TIME");
            assertThat(ct.getText()).isNull();
        }

        @Test
        void systemKeyword_USER() {
            DspfContent c = build(
                REC_MYREC,
                "     A                                  1  1USER"
            );
            DspfConstant ct = c.getRecordFormats().get(0).getConstants().get(0);
            assertThat(ct.getSystemKeyword()).isEqualTo("USER");
        }

        @Test
        void systemKeyword_SYSNAME() {
            DspfContent c = build(
                REC_MYREC,
                "     A                                  1  1SYSNAME"
            );
            DspfConstant ct = c.getRecordFormats().get(0).getConstants().get(0);
            assertThat(ct.getSystemKeyword()).isEqualTo("SYSNAME");
        }

        @Test
        void constantWithKeywords() {
            DspfContent c = build(
                REC_MYREC,
                "     A                                  1 25'Title' DSPATR(HI)"
            );
            DspfConstant ct = c.getRecordFormats().get(0).getConstants().get(0);
            assertThat(ct.getText()).isEqualTo("Title");
            assertThat(ct.getKeywords()).hasSize(1);
            assertThat(ct.getKeywords().get(0).getKeyword().getName()).isEqualTo("DSPATR");
        }

        @Test
        void constant_noSpuriousKeywordsFromQuotedText() {
            DspfContent c = build(
                REC_MYREC,
                "     A                                  8 25'1. Menu Item'"
            );
            DspfConstant ct = c.getRecordFormats().get(0).getConstants().get(0);
            assertThat(ct.getText()).isEqualTo("1. Menu Item");
            assertThat(ct.getKeywords()).isEmpty();
        }

        @Test
        void constant_rawSourceLines() {
            DspfContent c = build(
                REC_MYREC,
                "     A                                  1 25'Test'"
            );
            DspfConstant ct = c.getRecordFormats().get(0).getConstants().get(0);
            assertThat(ct.getRawSourceLines()).hasSize(1);
            assertThat(ct.getRawSourceLines().get(0)).contains("'Test'");
        }

        @Test
        void systemKeywordConstant_withFollowingKeywords() {
            DspfContent c = build(
                REC_MYREC,
                "     A                                  1  1DATE EDTCDE(Y)"
            );
            DspfConstant ct = c.getRecordFormats().get(0).getConstants().get(0);
            assertThat(ct.getSystemKeyword()).isEqualTo("DATE");
            assertThat(ct.getKeywords()).hasSize(1);
            assertThat(ct.getKeywords().get(0).getKeyword().getName()).isEqualTo("EDTCDE");
        }
    }

    // =========================================================================
    // Conditioning Indicators
    // =========================================================================

    @Nested
    class CondIndicators {

        @Test
        void singleIndicator() {
            DspfContent c = build(
                REC_MYREC,
                "     A  40                              1 25'Visible'"
            );
            DspfConstant ct = c.getRecordFormats().get(0).getConstants().get(0);
            assertThat(ct.getConditioningIndicators()).hasSize(1);
            assertThat(ct.getConditioningIndicators().get(0).getIndicator()).isEqualTo("40");
            assertThat(ct.getConditioningIndicators().get(0).isNot()).isFalse();
        }

        @Test
        void negatedIndicator() {
            DspfContent c = build(
                REC_MYREC,
                "     A N60                              1 25'Not60'"
            );
            DspfConstant ct = c.getRecordFormats().get(0).getConstants().get(0);
            assertThat(ct.getConditioningIndicators()).hasSize(1);
            assertThat(ct.getConditioningIndicators().get(0).getIndicator()).isEqualTo("60");
            assertThat(ct.getConditioningIndicators().get(0).isNot()).isTrue();
        }

        @Test
        void multipleIndicators() {
            DspfContent c = build(
                REC_MYREC,
                "     A  40N50 60                        1 25'Multi'"
            );
            DspfConstant ct = c.getRecordFormats().get(0).getConstants().get(0);
            assertThat(ct.getConditioningIndicators()).hasSize(3);
            // Slot 1: 40
            assertThat(ct.getConditioningIndicators().get(0).getIndicator()).isEqualTo("40");
            assertThat(ct.getConditioningIndicators().get(0).isNot()).isFalse();
            // Slot 2: N50
            assertThat(ct.getConditioningIndicators().get(1).getIndicator()).isEqualTo("50");
            assertThat(ct.getConditioningIndicators().get(1).isNot()).isTrue();
            // Slot 3: 60
            assertThat(ct.getConditioningIndicators().get(2).getIndicator()).isEqualTo("60");
            assertThat(ct.getConditioningIndicators().get(2).isNot()).isFalse();
        }

        @Test
        void conditionedKeywordMerging() {
            DspfContent c = build(
                REC_MYREC,
                "     A            MYFIELD        5A  B  5 20",
                "     A N60                                    DSPATR(PR)",
                "     A  60                                    DSPATR(UL)"
            );
            DspfFieldDefinition f = c.getRecordFormats().get(0).getFields().get(0);
            List<ConditionedKeyword> dspatrs = f.getKeywords().stream()
                .filter(ck -> "DSPATR".equals(ck.getKeyword().getName()))
                .toList();
            assertThat(dspatrs).hasSize(2);

            // N60 DSPATR(PR)
            ConditionedKeyword pr = dspatrs.stream()
                .filter(ck -> "PR".equals(ck.getKeyword().getValue()))
                .findFirst().orElse(null);
            assertThat(pr).isNotNull();
            assertThat(pr.getConditioningIndicators()).hasSize(1);
            assertThat(pr.getConditioningIndicators().get(0).isNot()).isTrue();
            assertThat(pr.getConditioningIndicators().get(0).getIndicator()).isEqualTo("60");

            // 60 DSPATR(UL)
            ConditionedKeyword ul = dspatrs.stream()
                .filter(ck -> "UL".equals(ck.getKeyword().getValue()))
                .findFirst().orElse(null);
            assertThat(ul).isNotNull();
            assertThat(ul.getConditioningIndicators()).hasSize(1);
            assertThat(ul.getConditioningIndicators().get(0).isNot()).isFalse();
            assertThat(ul.getConditioningIndicators().get(0).getIndicator()).isEqualTo("60");
        }

        @Test
        void conditionedRecordKeyword_hasIndicators() {
            // Simulates STULSTD.dspf STUSFC record with conditioned SFL keywords
            DspfContent c = build(
                "     A          R STUSFC                     SFLCTL(STUSFL)",
                "     A  40                                    SFLDSP",
                "     A  41                                    SFLDSPCTL",
                "     A  42                                    SFLCLR",
                "     A  43                                    SFLEND(*MORE)"
            );
            DspfRecordFormat rf = c.getRecordFormats().get(0);

            // SFLDSP should have indicator 40
            ConditionedKeyword sfldsp = rf.getKeywords().stream()
                .filter(ck -> "SFLDSP".equals(ck.getKeyword().getName()))
                .findFirst().orElse(null);
            assertThat(sfldsp).isNotNull();
            assertThat(sfldsp.getConditioningIndicators()).hasSize(1);
            assertThat(sfldsp.getConditioningIndicators().get(0).getIndicator()).isEqualTo("40");
            assertThat(sfldsp.getConditioningIndicators().get(0).isNot()).isFalse();

            // SFLCLR should have indicator 42
            ConditionedKeyword sflclr = rf.getKeywords().stream()
                .filter(ck -> "SFLCLR".equals(ck.getKeyword().getName()))
                .findFirst().orElse(null);
            assertThat(sflclr).isNotNull();
            assertThat(sflclr.getConditioningIndicators()).hasSize(1);
            assertThat(sflclr.getConditioningIndicators().get(0).getIndicator()).isEqualTo("42");

            // SFLEND should have indicator 43
            ConditionedKeyword sflend = rf.getKeywords().stream()
                .filter(ck -> "SFLEND".equals(ck.getKeyword().getName()))
                .findFirst().orElse(null);
            assertThat(sflend).isNotNull();
            assertThat(sflend.getConditioningIndicators().get(0).getIndicator()).isEqualTo("43");
        }

        @Test
        void unconditionedRecordKeyword_hasEmptyIndicators() {
            DspfContent c = build(
                "     A          R MYREC                      SFLCTL(MYSFL)"
            );
            DspfRecordFormat rf = c.getRecordFormats().get(0);
            ConditionedKeyword sflctl = rf.getKeywords().stream()
                .filter(ck -> "SFLCTL".equals(ck.getKeyword().getName()))
                .findFirst().orElse(null);
            assertThat(sflctl).isNotNull();
            assertThat(sflctl.getConditioningIndicators()).isEmpty();
        }
    }

    // =========================================================================
    // Continuation Lines
    // =========================================================================

    @Nested
    class ContinuationLines {

        @Test
        void fieldWithContinuationKeywords() {
            // '+' must be at col 80 (idx 79) for continuation detection
            // Col 45-79 = 35 chars for keywords + '+' at col 80
            //                                                              col 45                                       col 80
            String line1 = "     A            MYFIELD       10A  B  5 20DSPATR(HI)                        +";
            String line2 = "     A                                      DSPATR(UL)";
            DspfContent c = build(REC_MYREC, line1, line2);
            DspfFieldDefinition f = c.getRecordFormats().get(0).getFields().get(0);
            List<String> kwNames = f.getKeywords().stream()
                .map(ck -> ck.getKeyword().getName() + "(" + ck.getKeyword().getValue() + ")")
                .toList();
            assertThat(kwNames).contains("DSPATR(HI)", "DSPATR(UL)");
        }

        @Test
        void fieldContinuation_rawSourceLines() {
            String line1 = "     A            MYFIELD       10A  B  5 20DSPATR(HI)                        +";
            String line2 = "     A                                      COLOR(WHT)";
            DspfContent c = build(REC_MYREC, line1, line2);
            DspfFieldDefinition f = c.getRecordFormats().get(0).getFields().get(0);
            assertThat(f.getRawSourceLines()).hasSize(2);
        }

        @Test
        void recordFormat_rawSourceLines_includesContinuationKeywords() {
            // Record format with continuation keyword lines
            DspfContent c = build(
                "     A          R STUSFC                     SFLCTL(STUSFL)",
                "     A  40                                    SFLDSP",
                "     A  41                                    SFLDSPCTL",
                "     A  42                                    SFLCLR"
            );
            DspfRecordFormat rf = c.getRecordFormats().get(0);
            // R-line + 3 conditioned keyword lines = 4 rawSourceLines
            assertThat(rf.getRawSourceLines()).hasSize(4);
            assertThat(rf.getRawSourceLines().get(0)).contains("STUSFC");
            assertThat(rf.getRawSourceLines().get(1)).contains("SFLDSP");
            assertThat(rf.getRawSourceLines().get(2)).contains("SFLDSPCTL");
            assertThat(rf.getRawSourceLines().get(3)).contains("SFLCLR");
        }
    }

    // =========================================================================
    // Key Definitions
    // =========================================================================

    @Nested
    class KeyDefs {

        @Test
        void keyField() {
            DspfContent c = build(
                REC_MYREC,
                "     A            MYFIELD        5A  B  1  1",
                "     A          K MYFIELD"
            );
            DspfRecordFormat rf = c.getRecordFormats().get(0);
            assertThat(rf.getKeys()).hasSize(1);
            assertThat(rf.getKeys().get(0).getFieldName()).isEqualTo("MYFIELD");
            assertThat(rf.getKeys().get(0).getSortOrder()).isEqualTo("ascending");
        }

        @Test
        void keyField_descending() {
            DspfContent c = build(
                REC_MYREC,
                "     A            MYFIELD        5A  B  1  1",
                "     A          K MYFIELD                      DESCEND"
            );
            assertThat(c.getRecordFormats().get(0).getKeys().get(0).getSortOrder()).isEqualTo("descending");
        }
    }

    // =========================================================================
    // Source Lines & Comments
    // =========================================================================

    @Nested
    class SourceLinesAndComments {

        @Test
        void sourceLines_lineNumbersCorrect() {
            DspfContent c = build(
                "     A* comment",
                REC_MYREC,
                "     A            FIELD1         5A  B  1  1"
            );
            assertThat(c.getSourceLines()).hasSize(3);
            assertThat(c.getSourceLines().get(0).getLineNumber()).isEqualTo(1);
            assertThat(c.getSourceLines().get(1).getLineNumber()).isEqualTo(2);
            assertThat(c.getSourceLines().get(2).getLineNumber()).isEqualTo(3);
        }

        @Test
        void sourceLines_specTypes() {
            DspfContent c = build(
                "     A                                      DSPSIZ(24 80 *DS3)",
                "     A* comment",
                REC_MYREC,
                "     A            FIELD1         5A  B  1  1",
                "     A                                  2 25'Label'",
                "     A          K FIELD1"
            );
            assertThat(c.getSourceLines().get(0).getSpecType()).isEqualTo("FILE_KEYWORD");
            assertThat(c.getSourceLines().get(1).getSpecType()).isEqualTo("COMMENT");
            assertThat(c.getSourceLines().get(2).getSpecType()).isEqualTo("RECORD_FORMAT");
            assertThat(c.getSourceLines().get(3).getSpecType()).isEqualTo("FIELD_DEFINITION");
            assertThat(c.getSourceLines().get(4).getSpecType()).isEqualTo("CONSTANT");
            assertThat(c.getSourceLines().get(5).getSpecType()).isEqualTo("KEY_FIELD");
        }

        @Test
        void emptyCommentLine() {
            DspfContent c = build("     A*");
            assertThat(c.getComments()).hasSize(1);
            assertThat(c.getComments().get(0).getText()).isEmpty();
        }
    }

    // =========================================================================
    // Field Source Detection
    // =========================================================================

    @Nested
    class FieldSourceDetection {

        @Test
        void directField_noReference() {
            DspfContent c = build(
                REC_MYREC,
                "     A            MYFIELD        5A  B  1  1"
            );
            DspfFieldDefinition f = c.getRecordFormats().get(0).getFields().get(0);
            assertThat(f.getSource()).isEqualTo("direct");
            assertThat(f.getReferenceField()).isNull();
        }

        @Test
        void referenceField_sameNameRef() {
            DspfContent c = build(
                REC_MYREC,
                "     A            MYFIELD   R        B  1  1"
            );
            DspfFieldDefinition f = c.getRecordFormats().get(0).getFields().get(0);
            assertThat(f.getSource()).isEqualTo("reference");
            assertThat(f.getReferenceField()).isEqualTo("MYFIELD");
            assertThat(f.getReferenceFile()).isNull();
        }

        @Test
        void referenceField_reffldWithFile() {
            DspfContent c = build(
                REC_MYREC,
                "     A            MYFIELD   R        B  1  1REFFLD(OTHFLD OTHFILE)"
            );
            DspfFieldDefinition f = c.getRecordFormats().get(0).getFields().get(0);
            assertThat(f.getReferenceField()).isEqualTo("OTHFLD");
            assertThat(f.getReferenceFile()).isEqualTo("OTHFILE");
        }
    }

    // =========================================================================
    // Parse Errors
    // =========================================================================

    @Nested
    class ParseErrors {

        @Test
        void noErrorsOnValidInput() {
            DspfContent c = build(
                REC_MYREC,
                "     A            FIELD1         5A  B  1  1"
            );
            assertThat(c.getParseErrors()).isEmpty();
        }
    }

    // =========================================================================
    // Edge Cases
    // =========================================================================

    @Nested
    class EdgeCases {

        @Test
        void fieldWithNoScreenPosition() {
            // Hidden field: usage 'H' at col 38, no screen position
            DspfContent c = build(
                REC_MYREC,
                "     A            MYFIELD        5A  H"
            );
            DspfFieldDefinition f = c.getRecordFormats().get(0).getFields().get(0);
            assertThat(f.getScreenLine()).isNull();
            assertThat(f.getScreenPosition()).isNull();
            assertThat(f.getUsage()).isEqualTo("H");
        }

        @Test
        void fieldsGroupedUnderCorrectRecord() {
            DspfContent c = build(
                REC_REC1,
                "     A            F1             5A  B  1  1",
                REC_REC2,
                "     A            F2             5A  B  1  1",
                "     A            F3             5A  B  2  1"
            );
            assertThat(c.getRecordFormats().get(0).getFields()).hasSize(1);
            assertThat(c.getRecordFormats().get(0).getFields().get(0).getName()).isEqualTo("F1");
            assertThat(c.getRecordFormats().get(1).getFields()).hasSize(2);
            assertThat(c.getRecordFormats().get(1).getFields().get(0).getName()).isEqualTo("F2");
            assertThat(c.getRecordFormats().get(1).getFields().get(1).getName()).isEqualTo("F3");
        }

        @Test
        void constantsGroupedUnderCorrectRecord() {
            DspfContent c = build(
                REC_REC1,
                "     A                                  1 25'Label1'",
                REC_REC2,
                "     A                                  2 25'Label2'"
            );
            assertThat(c.getRecordFormats().get(0).getConstants()).hasSize(1);
            assertThat(c.getRecordFormats().get(0).getConstants().get(0).getText()).isEqualTo("Label1");
            assertThat(c.getRecordFormats().get(1).getConstants()).hasSize(1);
            assertThat(c.getRecordFormats().get(1).getConstants().get(0).getText()).isEqualTo("Label2");
        }

        @Test
        void lowercaseASpec() {
            DspfContent c = build("     a          R MYREC");
            assertThat(c.getRecordFormats()).hasSize(1);
            assertThat(c.getRecordFormats().get(0).getName()).isEqualTo("MYREC");
        }

        @Test
        void fieldWithPlusPosition() {
            // First field: pos=1, length=5 -> end column = 5
            // Second field: +1 -> resolved to 5 + 1 + 1 = 7
            DspfContent c = build(
                REC_MYREC,
                "     A            FIELD1         5A  B  5  1",
                "     A            FIELD2         3A  B  5 +1"
            );
            DspfFieldDefinition f1 = c.getRecordFormats().get(0).getFields().get(0);
            assertThat(f1.getScreenPosition()).isEqualTo(1);

            DspfFieldDefinition f2 = c.getRecordFormats().get(0).getFields().get(1);
            assertThat(f2.getScreenLine()).isEqualTo(5);
            assertThat(f2.getScreenPosition()).isEqualTo(7); // resolved: 5 + 1 + 1
            assertThat(f2.getScreenPositionRaw()).isEqualTo("+1");
        }

        @Test
        void fieldWithAbsolutePosition_hasRaw() {
            DspfContent c = build(
                REC_MYREC,
                "     A            MYFIELD        5A  B  5 25"
            );
            DspfFieldDefinition f = c.getRecordFormats().get(0).getFields().get(0);
            assertThat(f.getScreenPosition()).isEqualTo(25);
            assertThat(f.getScreenPositionRaw()).isEqualTo("25");
        }

        @Test
        void constantWithPlusPosition_notMisclassified() {
            // Field at pos 1, length 5 -> end col 5
            // Constant +5 -> resolved to 5 + 5 + 1 = 11
            DspfContent c = build(
                REC_MYREC,
                "     A            FIELD1         5A  B  3  1",
                "     A                                  3 +5'Label'"
            );
            assertThat(c.getRecordFormats().get(0).getConstants()).hasSize(1);
            DspfConstant ct = c.getRecordFormats().get(0).getConstants().get(0);
            assertThat(ct.getText()).isEqualTo("Label");
            assertThat(ct.getScreenLine()).isEqualTo(3);
            assertThat(ct.getScreenPosition()).isEqualTo(11); // resolved: 5 + 5 + 1
            assertThat(ct.getScreenPositionRaw()).isEqualTo("+5");
        }

        @Test
        void fieldInheritsLineFromPreviousField_plusPosition() {
            // F1: line=5, pos=10, length=5 -> end=14
            // F2: line=blank(inherit 5), pos=+5 -> resolved to 14+5+1=20
            DspfContent c = build(
                REC_MYREC,
                "     A            FIELD1         5A  B  5 10",
                "     A            FIELD2         3A  B    +5"
            );
            DspfFieldDefinition f2 = c.getRecordFormats().get(0).getFields().get(1);
            assertThat(f2.getScreenLine()).isEqualTo(5);  // inherited
            assertThat(f2.getScreenPosition()).isEqualTo(20); // resolved
        }

        @Test
        void fieldInheritsLineFromPreviousField_absolutePosition() {
            // F1: line=5, pos=10
            // F2: line=blank(inherit 5), pos=20
            DspfContent c = build(
                REC_MYREC,
                "     A            FIELD1         5A  B  5 10",
                "     A            FIELD2         3A  B    20"
            );
            DspfFieldDefinition f2 = c.getRecordFormats().get(0).getFields().get(1);
            assertThat(f2.getScreenLine()).isEqualTo(5);  // inherited
            assertThat(f2.getScreenPosition()).isEqualTo(20);
        }
    }
}
