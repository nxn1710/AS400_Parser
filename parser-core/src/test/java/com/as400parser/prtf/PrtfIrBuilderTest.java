package com.as400parser.prtf;

import com.as400parser.dds.model.DdsKeyword;
import com.as400parser.dspf.model.ConditionedKeyword;
import com.as400parser.prtf.model.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link PrtfIrBuilder} — isolated builder logic tests
 * using synthetic DDS lines (no file I/O).
 * <p>
 * Each test feeds normalized 80-char lines to {@code buildContent()}
 * and verifies the resulting {@link PrtfContent} structure.
 * <p>
 * PRTF-specific differences from DSPF:
 * <ul>
 *   <li>No key fields (K in col 17 is invalid)</li>
 *   <li>No subfile concept (no SFL/SFLCTL/recordType)</li>
 *   <li>System keywords: DATE, TIME, PAGNBR, MSGCON (not SYSNAME/USER)</li>
 *   <li>Usage: blank/O = output-only, P = program-to-system</li>
 *   <li>Print coordinates (printLine/printPosition) instead of screen coordinates</li>
 * </ul>
 * <p>
 * DDS A-spec column layout (1-indexed):
 * <pre>
 *   1-5:   Sequence number (ignored)
 *   6:     Form type ('A')
 *   7:     Comment ('*') or blank
 *   8-16:  Conditioning indicators (3 slots × 3 chars)
 *   17:    Name type ('R'=record, ' '=other; K is NOT valid for PRTF)
 *   18:    Reserved (blank)
 *   19-28: Name (10 chars)
 *   29:    Reference ('R' or blank)
 *   30-34: Length (5 digits)
 *   35:    Data type (S, A, F, L, T, Z, O, G — no P/B)
 *   36-37: Decimal positions
 *   38:    Usage (O or blank = output, P = program-to-system)
 *   39-41: Print line (3 digits, max 255)
 *   42-44: Print position (3 digits, max 255)
 *   45-80: Keywords (36 chars)
 * </pre>
 */
class PrtfIrBuilderTest {

    private final PrtfIrBuilder builder = new PrtfIrBuilder();

    /**
     * Pad a source line to 80 chars (simulates SourceNormalizer behavior).
     */
    private String pad(String line) {
        if (line.length() >= 80) return line.substring(0, 80);
        return line + " ".repeat(80 - line.length());
    }

    private PrtfContent build(String... lines) {
        return builder.buildContent(Arrays.stream(lines).map(this::pad).toList());
    }

    private static final String REC_HDRREC = "     A          R HDRREC";
    private static final String REC_DTLREC = "     A          R DTLREC";
    private static final String REC_FTRREC = "     A          R FTRREC";

    // =========================================================================
    // Line Classification
    // =========================================================================

    @Nested
    class LineClassification {

        @Test
        void blankLine_isSkipped() {
            PrtfContent c = build("", "     A*comment");
            assertThat(c.getSourceLines()).hasSize(2);
            assertThat(c.getSourceLines().get(0).isBlank()).isTrue();
        }

        @Test
        void nonASpec_isSkipped() {
            PrtfContent c = build("     H  some header");
            assertThat(c.getSourceLines()).hasSize(1);
            assertThat(c.getSourceLines().get(0).isBlank()).isTrue();
        }

        @Test
        void commentLine_captured() {
            PrtfContent c = build("     A* PRTF report header");
            assertThat(c.getComments()).hasSize(1);
            assertThat(c.getComments().get(0).getText()).isEqualTo("PRTF report header");
            assertThat(c.getSourceLines().get(0).getSpecType()).isEqualTo("COMMENT");
        }

        @Test
        void fileKeyword_beforeRecord() {
            PrtfContent c = build("     A                                      REF(FLDREFPF)");
            assertThat(c.getFileKeywords()).hasSize(1);
            assertThat(c.getFileKeywords().get(0).getName()).isEqualTo("REF");
            assertThat(c.getSourceLines().get(0).getSpecType()).isEqualTo("FILE_KEYWORD");
        }

        @Test
        void multipleFileKeywords() {
            PrtfContent c = build(
                "     A                                      REF(FLDREFPF)",
                "     A                                      INDARA",
                "     A                                      INDTXT(99 'Page overflow')"
            );
            assertThat(c.getFileKeywords()).hasSize(3);
            List<String> names = c.getFileKeywords().stream().map(DdsKeyword::getName).toList();
            assertThat(names).containsExactly("REF", "INDARA", "INDTXT");
        }

        @Test
        void recordFormat_classified() {
            PrtfContent c = build(REC_HDRREC);
            assertThat(c.getRecordFormats()).hasSize(1);
            assertThat(c.getRecordFormats().get(0).getName()).isEqualTo("HDRREC");
            assertThat(c.getSourceLines().get(0).getSpecType()).isEqualTo("RECORD_FORMAT");
        }
    }

    // =========================================================================
    // Record Formats (no SFL/SFLCTL for PRTF)
    // =========================================================================

    @Nested
    class RecordFormats {

        @Test
        void recordFormat_noRecordType() {
            PrtfContent c = build(REC_HDRREC);
            PrtfRecordFormat rf = c.getRecordFormats().get(0);
            // PRTF has no recordType (no SFL/SFLCTL)
            assertThat(rf.getName()).isEqualTo("HDRREC");
        }

        @Test
        void recordWithTextKeyword() {
            PrtfContent c = build("     A          R HDRREC                    TEXT('Page Header')");
            PrtfRecordFormat rf = c.getRecordFormats().get(0);
            assertThat(rf.getText()).isEqualTo("Page Header");
        }

        @Test
        void recordWithSpacingKeyword() {
            PrtfContent c = build("     A          R DTLREC                    SPACEA(1)");
            PrtfRecordFormat rf = c.getRecordFormats().get(0);
            assertThat(rf.getKeywords()).hasSize(1);
            assertThat(rf.getKeywords().get(0).getName()).isEqualTo("SPACEA");
            assertThat(rf.getKeywords().get(0).getValue()).isEqualTo("1");
        }

        @Test
        void multipleRecordFormats() {
            PrtfContent c = build(REC_HDRREC, REC_DTLREC, REC_FTRREC);
            assertThat(c.getRecordFormats()).hasSize(3);
            assertThat(c.getRecordFormats().get(0).getName()).isEqualTo("HDRREC");
            assertThat(c.getRecordFormats().get(1).getName()).isEqualTo("DTLREC");
            assertThat(c.getRecordFormats().get(2).getName()).isEqualTo("FTRREC");
        }
    }

    // =========================================================================
    // No Key Fields (K is invalid for PRTF)
    // =========================================================================

    @Nested
    class NoKeyFields {

        @Test
        void keyLine_generatesWarning() {
            PrtfContent c = build(
                REC_HDRREC,
                "     A            MYFIELD        5A  O  1  1",
                "     A          K MYFIELD"
            );
            // Should produce a warning for K in col 17
            assertThat(c.getParseErrors()).isNotEmpty();
            assertThat(c.getParseErrors().get(0).getMessage()).contains("key fields not supported");
        }
    }

    // =========================================================================
    // Field Definition (print-specific)
    // =========================================================================

    @Nested
    class FieldDefinitions {

        @Test
        void directField_allColumns() {
            PrtfContent c = build(
                REC_HDRREC,
                "     A            RPTFLD        10A     5 20"
            );
            PrtfFieldDefinition f = c.getRecordFormats().get(0).getFields().get(0);
            assertThat(f.getName()).isEqualTo("RPTFLD");
            assertThat(f.getLength()).isEqualTo(10);
            assertThat(f.getDataType()).isEqualTo("A");
            assertThat(f.getDecimalPositions()).isNull();
            assertThat(f.getUsage()).isNull(); // blank = output-only default
            assertThat(f.getPrintLine()).isEqualTo(5);
            assertThat(f.getPrintPosition()).isEqualTo(20);
            assertThat(f.getSource()).isEqualTo("direct");
        }

        @Test
        void outputField_explicitO() {
            PrtfContent c = build(
                REC_HDRREC,
                "     A            RPTFLD        10A  O  5 20"
            );
            PrtfFieldDefinition f = c.getRecordFormats().get(0).getFields().get(0);
            assertThat(f.getUsage()).isEqualTo("O");
        }

        @Test
        void programToSystemField_P_usage() {
            // P-usage fields don't print — no print position
            PrtfContent c = build(
                REC_HDRREC,
                "     A            BOXCTL         4S 0P"
            );
            PrtfFieldDefinition f = c.getRecordFormats().get(0).getFields().get(0);
            assertThat(f.getUsage()).isEqualTo("P");
            assertThat(f.getLength()).isEqualTo(4);
            assertThat(f.getDataType()).isEqualTo("S");
            assertThat(f.getDecimalPositions()).isEqualTo(0);
            assertThat(f.getPrintLine()).isNull();
            assertThat(f.getPrintPosition()).isNull();
        }

        @Test
        void referenceField_withR() {
            PrtfContent c = build(
                REC_HDRREC,
                "     A            RPTFLD    R        O  5 20"
            );
            PrtfFieldDefinition f = c.getRecordFormats().get(0).getFields().get(0);
            assertThat(f.getReference()).isEqualTo("R");
            assertThat(f.getSource()).isEqualTo("reference");
            assertThat(f.getReferenceField()).isEqualTo("RPTFLD"); // same-name ref
        }

        @Test
        void referenceField_withReffld() {
            PrtfContent c = build(
                REC_HDRREC,
                "     A            RPTFLD    R        O  5 20REFFLD(OTHFLD OTHFILE)"
            );
            PrtfFieldDefinition f = c.getRecordFormats().get(0).getFields().get(0);
            assertThat(f.getReferenceField()).isEqualTo("OTHFLD");
            assertThat(f.getReferenceFile()).isEqualTo("OTHFILE");
        }

        @Test
        void fieldWithKeywords() {
            PrtfContent c = build(
                REC_HDRREC,
                "     A            AMOUNT         9S 2O  5 20EDTCDE(J)"
            );
            PrtfFieldDefinition f = c.getRecordFormats().get(0).getFields().get(0);
            assertThat(f.getKeywords()).hasSize(1);
            assertThat(f.getKeywords().get(0).getKeyword().getName()).isEqualTo("EDTCDE");
            assertThat(f.getKeywords().get(0).getKeyword().getValue()).isEqualTo("J");
        }

        @Test
        void numericField_zonedDecimal() {
            PrtfContent c = build(
                REC_HDRREC,
                "     A            TOTAL         11S 2O 10 50"
            );
            PrtfFieldDefinition f = c.getRecordFormats().get(0).getFields().get(0);
            assertThat(f.getDataType()).isEqualTo("S");
            assertThat(f.getLength()).isEqualTo(11);
            assertThat(f.getDecimalPositions()).isEqualTo(2);
        }

        @Test
        void multipleFieldsUnderRecord() {
            PrtfContent c = build(
                REC_DTLREC,
                "     A            EMPNO          6A  O  1  1",
                "     A            EMPNAM        25A  O  1 10",
                "     A            SALARY         9S 2O  1 40"
            );
            PrtfRecordFormat rf = c.getRecordFormats().get(0);
            assertThat(rf.getFields()).hasSize(3);
            assertThat(rf.getFields().get(0).getName()).isEqualTo("EMPNO");
            assertThat(rf.getFields().get(1).getName()).isEqualTo("EMPNAM");
            assertThat(rf.getFields().get(2).getName()).isEqualTo("SALARY");
        }

        @Test
        void field_rawSourceLines() {
            PrtfContent c = build(
                REC_HDRREC,
                "     A            RPTFLD        10A  O  1  1"
            );
            PrtfFieldDefinition f = c.getRecordFormats().get(0).getFields().get(0);
            assertThat(f.getRawSourceLines()).hasSize(1);
            assertThat(f.getRawSourceLines().get(0)).contains("RPTFLD");
        }
    }

    // =========================================================================
    // Constants (PRTF system keywords)
    // =========================================================================

    @Nested
    class Constants {

        @Test
        void quotedTextConstant() {
            PrtfContent c = build(
                REC_HDRREC,
                "     A                                  1 25'COMPANY REPORT'"
            );
            PrtfRecordFormat rf = c.getRecordFormats().get(0);
            assertThat(rf.getConstants()).hasSize(1);
            PrtfConstant ct = rf.getConstants().get(0);
            assertThat(ct.getText()).isEqualTo("COMPANY REPORT");
            assertThat(ct.getSystemKeyword()).isNull();
            assertThat(ct.getPrintLine()).isEqualTo(1);
            assertThat(ct.getPrintPosition()).isEqualTo(25);
        }

        @Test
        void systemKeyword_DATE() {
            PrtfContent c = build(
                REC_HDRREC,
                "     A                                  1 70DATE"
            );
            PrtfConstant ct = c.getRecordFormats().get(0).getConstants().get(0);
            assertThat(ct.getSystemKeyword()).isEqualTo("DATE");
            assertThat(ct.getText()).isNull();
        }

        @Test
        void systemKeyword_TIME() {
            PrtfContent c = build(
                REC_HDRREC,
                "     A                                  1  1TIME"
            );
            PrtfConstant ct = c.getRecordFormats().get(0).getConstants().get(0);
            assertThat(ct.getSystemKeyword()).isEqualTo("TIME");
        }

        @Test
        void systemKeyword_PAGNBR() {
            PrtfContent c = build(
                REC_HDRREC,
                "     A                                  1 75PAGNBR"
            );
            PrtfConstant ct = c.getRecordFormats().get(0).getConstants().get(0);
            assertThat(ct.getSystemKeyword()).isEqualTo("PAGNBR");
        }

        @Test
        void systemKeyword_MSGCON() {
            // MSGCON with parameters is parsed as a keyword, not a standalone system keyword
            PrtfContent c = build(
                REC_HDRREC,
                "     A                                  1  1MSGCON(10 MYLIB/MYMSG MSG01)"
            );
            PrtfConstant ct = c.getRecordFormats().get(0).getConstants().get(0);
            // MSGCON(params) gets parsed as a regular keyword
            assertThat(ct.getKeywords()).isNotEmpty();
            assertThat(ct.getKeywords().get(0).getKeyword().getName()).isEqualTo("MSGCON");
        }

        @Test
        void constantWithKeywords() {
            PrtfContent c = build(
                REC_HDRREC,
                "     A                                  1 25'Title' UNDERLINE"
            );
            PrtfConstant ct = c.getRecordFormats().get(0).getConstants().get(0);
            assertThat(ct.getText()).isEqualTo("Title");
            assertThat(ct.getKeywords()).hasSize(1);
            assertThat(ct.getKeywords().get(0).getKeyword().getName()).isEqualTo("UNDERLINE");
        }

        @Test
        void systemKeywordDate_withEdtcde() {
            PrtfContent c = build(
                REC_HDRREC,
                "     A                                  1  1DATE EDTCDE(Y)"
            );
            PrtfConstant ct = c.getRecordFormats().get(0).getConstants().get(0);
            assertThat(ct.getSystemKeyword()).isEqualTo("DATE");
            assertThat(ct.getKeywords()).hasSize(1);
            assertThat(ct.getKeywords().get(0).getKeyword().getName()).isEqualTo("EDTCDE");
        }

        @Test
        void constant_rawSourceLines() {
            PrtfContent c = build(
                REC_HDRREC,
                "     A                                  1 25'Test'"
            );
            PrtfConstant ct = c.getRecordFormats().get(0).getConstants().get(0);
            assertThat(ct.getRawSourceLines()).hasSize(1);
            assertThat(ct.getRawSourceLines().get(0)).contains("'Test'");
        }
    }

    // =========================================================================
    // Conditioning Indicators
    // =========================================================================

    @Nested
    class CondIndicators {

        @Test
        void singleIndicator() {
            PrtfContent c = build(
                REC_HDRREC,
                "     A  40                              1 25'Conditional'"
            );
            PrtfConstant ct = c.getRecordFormats().get(0).getConstants().get(0);
            assertThat(ct.getConditioningIndicators()).hasSize(1);
            assertThat(ct.getConditioningIndicators().get(0).getIndicator()).isEqualTo("40");
            assertThat(ct.getConditioningIndicators().get(0).isNot()).isFalse();
        }

        @Test
        void negatedIndicator() {
            PrtfContent c = build(
                REC_HDRREC,
                "     A N60                              1 25'NotSixty'"
            );
            PrtfConstant ct = c.getRecordFormats().get(0).getConstants().get(0);
            assertThat(ct.getConditioningIndicators()).hasSize(1);
            assertThat(ct.getConditioningIndicators().get(0).getIndicator()).isEqualTo("60");
            assertThat(ct.getConditioningIndicators().get(0).isNot()).isTrue();
        }

        @Test
        void conditionedKeywordMerging() {
            PrtfContent c = build(
                REC_HDRREC,
                "     A            RPTFLD        10A  O  5 20",
                "     A N31                                    SPACEA(1)",
                "     A  31                                    SPACEA(2)"
            );
            PrtfFieldDefinition f = c.getRecordFormats().get(0).getFields().get(0);
            List<ConditionedKeyword> spaceas = f.getKeywords().stream()
                .filter(ck -> "SPACEA".equals(ck.getKeyword().getName()))
                .toList();
            assertThat(spaceas).hasSize(2);
        }
    }

    // =========================================================================
    // Continuation Lines
    // =========================================================================

    @Nested
    class ContinuationLines {

        @Test
        void fieldWithContinuationKeywords() {
            //                                                              col 45                                       col 80
            String line1 = "     A            RPTFLD        10A  O  5 20EDTCDE(J)                         +";
            String line2 = "     A                                      UNDERLINE";
            PrtfContent c = build(REC_HDRREC, line1, line2);
            PrtfFieldDefinition f = c.getRecordFormats().get(0).getFields().get(0);
            List<String> kwNames = f.getKeywords().stream()
                .map(ck -> ck.getKeyword().getName())
                .toList();
            assertThat(kwNames).contains("EDTCDE", "UNDERLINE");
        }

        @Test
        void fieldContinuation_rawSourceLines() {
            String line1 = "     A            RPTFLD        10A  O  5 20EDTCDE(J)                         +";
            String line2 = "     A                                      COLOR(BLU)";
            PrtfContent c = build(REC_HDRREC, line1, line2);
            PrtfFieldDefinition f = c.getRecordFormats().get(0).getFields().get(0);
            assertThat(f.getRawSourceLines()).hasSize(2);
        }
    }

    // =========================================================================
    // Source Lines & Comments
    // =========================================================================

    @Nested
    class SourceLinesAndComments {

        @Test
        void sourceLines_lineNumbersCorrect() {
            PrtfContent c = build(
                "     A* comment",
                REC_HDRREC,
                "     A            FIELD1        10A  O  1  1"
            );
            assertThat(c.getSourceLines()).hasSize(3);
            assertThat(c.getSourceLines().get(0).getLineNumber()).isEqualTo(1);
            assertThat(c.getSourceLines().get(1).getLineNumber()).isEqualTo(2);
            assertThat(c.getSourceLines().get(2).getLineNumber()).isEqualTo(3);
        }

        @Test
        void sourceLines_specTypes() {
            PrtfContent c = build(
                "     A                                      REF(FLDREFPF)",
                "     A* comment",
                REC_HDRREC,
                "     A            FIELD1        10A  O  1  1",
                "     A                                  2 25'Label'"
            );
            assertThat(c.getSourceLines().get(0).getSpecType()).isEqualTo("FILE_KEYWORD");
            assertThat(c.getSourceLines().get(1).getSpecType()).isEqualTo("COMMENT");
            assertThat(c.getSourceLines().get(2).getSpecType()).isEqualTo("RECORD_FORMAT");
            assertThat(c.getSourceLines().get(3).getSpecType()).isEqualTo("FIELD_DEFINITION");
            assertThat(c.getSourceLines().get(4).getSpecType()).isEqualTo("CONSTANT");
        }
    }

    // =========================================================================
    // Edge Cases
    // =========================================================================

    @Nested
    class EdgeCases {

        @Test
        void fieldsGroupedUnderCorrectRecord() {
            PrtfContent c = build(
                REC_HDRREC,
                "     A            F1            10A  O  1  1",
                REC_DTLREC,
                "     A            F2            10A  O  1  1",
                "     A            F3            10A  O  2  1"
            );
            assertThat(c.getRecordFormats().get(0).getFields()).hasSize(1);
            assertThat(c.getRecordFormats().get(0).getFields().get(0).getName()).isEqualTo("F1");
            assertThat(c.getRecordFormats().get(1).getFields()).hasSize(2);
            assertThat(c.getRecordFormats().get(1).getFields().get(0).getName()).isEqualTo("F2");
            assertThat(c.getRecordFormats().get(1).getFields().get(1).getName()).isEqualTo("F3");
        }

        @Test
        void constantsGroupedUnderCorrectRecord() {
            PrtfContent c = build(
                REC_HDRREC,
                "     A                                  1 25'Header'",
                REC_FTRREC,
                "     A                                  1 25'Footer'"
            );
            assertThat(c.getRecordFormats().get(0).getConstants()).hasSize(1);
            assertThat(c.getRecordFormats().get(0).getConstants().get(0).getText()).isEqualTo("Header");
            assertThat(c.getRecordFormats().get(1).getConstants()).hasSize(1);
            assertThat(c.getRecordFormats().get(1).getConstants().get(0).getText()).isEqualTo("Footer");
        }

        @Test
        void lowercaseASpec() {
            PrtfContent c = build("     a          R MYREC");
            assertThat(c.getRecordFormats()).hasSize(1);
            assertThat(c.getRecordFormats().get(0).getName()).isEqualTo("MYREC");
        }

        @Test
        void noErrorsOnValidInput() {
            PrtfContent c = build(
                REC_HDRREC,
                "     A            FIELD1        10A  O  1  1"
            );
            assertThat(c.getParseErrors()).isEmpty();
        }

        @Test
        void sequenceNumbers_passedThrough() {
            List<String> lines = List.of(
                pad("     A          R HDRREC"),
                pad("     A            FIELD1        10A  O  1  1")
            );
            String[] seqNums = {"00100", "00200"};
            PrtfContent c = builder.buildContent(lines, seqNums);
            assertThat(c.getSourceLines().get(0).getSequenceNumber()).isEqualTo("00100");
            assertThat(c.getSourceLines().get(1).getSequenceNumber()).isEqualTo("00200");
        }
    }
}
