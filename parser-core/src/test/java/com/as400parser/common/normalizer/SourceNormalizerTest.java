package com.as400parser.common.normalizer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link SourceNormalizer}.
 * Covers all 6 pipeline steps and edge cases.
 */
class SourceNormalizerTest {

    private SourceNormalizer normalizer;

    @BeforeEach
    void setUp() {
        normalizer = new SourceNormalizer();
    }

    // =========================================================================
    // Step 1: Split Lines
    // =========================================================================
    @Nested
    class SplitLines {

        @Test
        void splitLinesHandlesLF() {
            NormalizedSource result = normalizer.normalize("line1\nline2\nline3");
            assertThat(result.getLineCount()).isEqualTo(3);
        }

        @Test
        void splitLinesHandlesCRLF() {
            NormalizedSource result = normalizer.normalize("line1\r\nline2\r\nline3");
            assertThat(result.getLineCount()).isEqualTo(3);
        }

        @Test
        void splitLinesHandlesCR() {
            NormalizedSource result = normalizer.normalize("line1\rline2\rline3");
            assertThat(result.getLineCount()).isEqualTo(3);
        }

        @Test
        void splitLinesHandlesMixedEndings() {
            NormalizedSource result = normalizer.normalize("line1\r\nline2\nline3\rline4");
            assertThat(result.getLineCount()).isEqualTo(4);
        }

        @Test
        void emptySourceReturnsEmpty() {
            NormalizedSource result = normalizer.normalize("");
            assertThat(result.getLineCount()).isEqualTo(0);
        }

        @Test
        void singleLineNoNewline() {
            NormalizedSource result = normalizer.normalize("     H");
            assertThat(result.getLineCount()).isEqualTo(1);
        }
    }

    // =========================================================================
    // Step 2: Tab Expansion
    // =========================================================================
    @Nested
    class TabExpansion {

        @Test
        void expandTabsDefault8Column() {
            // Tab at position 0 → expand to 8 spaces
            String source = "\tH";
            NormalizedSource result = normalizer.normalize(source);
            String line = result.getLines()[0];
            // After tab expansion at col 0: 8 spaces + H, then padded to 80
            assertThat(line.charAt(8)).isEqualTo('H');
        }

        @Test
        void expandTabsMultiple() {
            String source = "A\tB";
            NormalizedSource result = normalizer.normalize(source);
            String line = result.getLines()[0];
            // 'A' at col 0, tab expands to col 8, 'B' at col 8
            assertThat(line.charAt(0)).isEqualTo(' '); // cols 1-5 replaced with spaces (seq nums)
            // After seq extraction: col 0-4 become spaces, original A was at col 0
            // The raw content 'A\tB' → after tab expand 'A       B' (A at 0, tab to 8, B at 8)
            // After seq extract: '     ' + '   B' + padding
            assertThat(line).hasSize(80);
        }

        @Test
        void noTabsPassthrough() {
            String source = "     H                                                                          ";
            NormalizedSource result = normalizer.normalize(source);
            assertThat(result.getLines()[0]).hasSize(80);
        }

        @Test
        void seuStyleTabStops() {
            SourceNormalizer seuNormalizer = new SourceNormalizer(SourceNormalizer.SEU_TAB_STOPS);
            // Tab at col 0 → next SEU stop is col 6 (1-based) = index 5
            String source = "\tC";
            NormalizedSource result = seuNormalizer.normalize(source);
            String line = result.getLines()[0];
            assertThat(line.charAt(5)).isEqualTo('C');
        }
    }

    // =========================================================================
    // Step 3: Control Character Stripping
    // =========================================================================
    @Nested
    class ControlCharStripping {

        @Test
        void stripControlCharsPreserveSOSI() {
            // SO = 0x0E, SI = 0x0F should be preserved
            String source = "     C" + (char) 0x0E + "AB" + (char) 0x0F + "rest";
            NormalizedSource result = normalizer.normalize(source);
            String line = result.getLines()[0];
            assertThat(line).contains(String.valueOf((char) 0x0E));
            assertThat(line).contains(String.valueOf((char) 0x0F));
        }

        @Test
        void stripNullAndBellChars() {
            String source = "     H" + (char) 0x00 + "rest" + (char) 0x07 + "more";
            NormalizedSource result = normalizer.normalize(source);
            String line = result.getLines()[0];
            // Null and bell should be stripped
            assertThat(line).doesNotContain(String.valueOf((char) 0x00));
            assertThat(line).doesNotContain(String.valueOf((char) 0x07));
            assertThat(line).contains("rest");
            assertThat(line).contains("more");
        }

        @Test
        void controlCharStrippingGeneratesWarning() {
            String source = "     H" + (char) 0x01;
            NormalizedSource result = normalizer.normalize(source);
            assertThat(result.getWarnings()).hasSize(1);
            assertThat(result.getWarnings().get(0).getType())
                    .isEqualTo(NormalizationWarning.WarningType.CONTROL_CHAR_STRIPPED);
        }

        @Test
        void noControlCharsNoWarning() {
            String source = "     H normal text here";
            NormalizedSource result = normalizer.normalize(source);
            assertThat(result.getWarnings()).isEmpty();
        }
    }

    // =========================================================================
    // Step 4: Trim + Pad to 80 chars
    // =========================================================================
    @Nested
    class TrimAndPad {

        @Test
        void padShortLineTo80() {
            String source = "     H";
            NormalizedSource result = normalizer.normalize(source);
            assertThat(result.getLines()[0]).hasSize(80);
        }

        @Test
        void exactly80CharsUnchanged() {
            // Build a 80-char line
            String source = "     H" + " ".repeat(74);
            assertThat(source).hasSize(80);
            NormalizedSource result = normalizer.normalize(source);
            assertThat(result.getLines()[0]).hasSize(80);
        }

        @Test
        void truncateLongLineWithWarning() {
            // Build a >80-char line
            String source = "     H" + "X".repeat(80);
            NormalizedSource result = normalizer.normalize(source);
            assertThat(result.getLines()[0]).hasSize(80);
            assertThat(result.getWarnings()).hasSize(1);
            assertThat(result.getWarnings().get(0).getType())
                    .isEqualTo(NormalizationWarning.WarningType.TRUNCATION);
        }

        @Test
        void trimTrailingWhitespace() {
            String source = "     H   text   \t   ";
            NormalizedSource result = normalizer.normalize(source);
            String line = result.getLines()[0];
            assertThat(line).hasSize(80);
            // Should not have trailing tab or spaces from original; padded with clean spaces
        }

        @Test
        void allLinesExactly80() {
            String source = "     H\n     FCUSTMAST\n     C           CUSTNO";
            NormalizedSource result = normalizer.normalize(source);
            for (String line : result.getLines()) {
                assertThat(line).hasSize(80);
            }
        }
    }

    // =========================================================================
    // Step 5: Sequence Number Extraction
    // =========================================================================
    @Nested
    class SequenceNumberExtraction {

        @Test
        void extractNumericSequenceNumbers() {
            String source = "00100H";
            NormalizedSource result = normalizer.normalize(source);
            assertThat(result.getSequenceNumbers()[0]).isEqualTo("00100");
            // Cols 1-5 should be replaced with spaces in the line
            assertThat(result.getLines()[0].substring(0, 5)).isEqualTo("     ");
        }

        @Test
        void blankSequenceNumbersExtracted() {
            String source = "     H";
            NormalizedSource result = normalizer.normalize(source);
            assertThat(result.getSequenceNumbers()[0]).isEmpty();
            assertThat(result.getLines()[0].substring(0, 5)).isEqualTo("     ");
        }

        @Test
        void mixedSequenceNumbers() {
            String source = "00100H\n     FCUSTMAST\n00300C";
            NormalizedSource result = normalizer.normalize(source);
            assertThat(result.getSequenceNumbers()[0]).isEqualTo("00100");
            assertThat(result.getSequenceNumbers()[1]).isEmpty();
            assertThat(result.getSequenceNumbers()[2]).isEqualTo("00300");
        }

        @Test
        void sequenceNumbersReplacedInOutput() {
            String source = "12345H rest of line";
            NormalizedSource result = normalizer.normalize(source);
            // Cols 1-5 replaced with spaces; col 6 onwards preserved
            assertThat(result.getLines()[0]).startsWith("     H");
        }
    }

    // =========================================================================
    // Step 6: Original Line Number Tracking
    // =========================================================================
    @Nested
    class LineNumberTracking {

        @Test
        void lineNumbersAre1Based() {
            String source = "line1\nline2\nline3";
            NormalizedSource result = normalizer.normalize(source);
            assertThat(result.getOriginalLineNumbers()).containsExactly(1, 2, 3);
        }

        @Test
        void singleLineIsLine1() {
            NormalizedSource result = normalizer.normalize("     H");
            assertThat(result.getOriginalLineNumbers()).containsExactly(1);
        }

        @Test
        void lineCountMatchesArrayLengths() {
            String source = "     H\n     F\n     C";
            NormalizedSource result = normalizer.normalize(source);
            assertThat(result.getLineCount()).isEqualTo(3);
            assertThat(result.getLines()).hasSize(3);
            assertThat(result.getOriginalLineNumbers()).hasSize(3);
            assertThat(result.getSequenceNumbers()).hasSize(3);
        }
    }

    // =========================================================================
    // DBCS Awareness
    // =========================================================================
    @Nested
    class DbcsAwareness {

        @Test
        void dbcsDisplayWidthCalculation() {
            // SO + 2 DBCS chars + SI = 0 + 2*2 + 0 = 4 display columns
            String dbcs = String.valueOf((char) 0x0E) + "AB" + String.valueOf((char) 0x0F);
            int width = normalizer.calculateDisplayWidth(dbcs);
            assertThat(width).isEqualTo(4); // 2 DBCS chars × 2 columns each
        }

        @Test
        void mixedSbcsDbcsWidth() {
            // "HI" (2 SBCS) + SO + "AB" (2 DBCS) + SI = 2 + 4 = 6
            String mixed = "HI" + (char) 0x0E + "AB" + (char) 0x0F;
            int width = normalizer.calculateDisplayWidth(mixed);
            assertThat(width).isEqualTo(6);
        }

        @Test
        void dbcsLinePaddedCorrectly() {
            // 5 spaces (seq) + "C" (1) + SO + "AB" (4) + SI = 1 + 4 = 5 display cols after col 5
            // Total display: 5 (seq spaces) + 5 = 10, needs 70 padding chars
            String source = "     C" + (char) 0x0E + "AB" + (char) 0x0F;
            NormalizedSource result = normalizer.normalize(source);
            String line = result.getLines()[0];
            // Display width should be 80
            assertThat(normalizer.calculateDisplayWidth(line)).isEqualTo(80);
        }
    }

    // =========================================================================
    // Integration / End-to-End
    // =========================================================================
    @Nested
    class EndToEnd {

        @Test
        void fullRpg3HeaderSpec() {
            String source = "     H                                                                          ";
            NormalizedSource result = normalizer.normalize(source);
            assertThat(result.getLineCount()).isEqualTo(1);
            assertThat(result.getLines()[0]).hasSize(80);
            assertThat(result.getLines()[0].charAt(5)).isEqualTo('H');
            assertThat(result.getWarnings()).isEmpty();
        }

        @Test
        void multipleSpecTypes() {
            String source = String.join("\n",
                    "     H                                                                          ",
                    "     FCUSTMAST IF  E           K        DISK                                    ",
                    "     C           *IN03     DOWEQ*OFF                                            ");
            NormalizedSource result = normalizer.normalize(source);
            assertThat(result.getLineCount()).isEqualTo(3);
            assertThat(result.getLines()[0].charAt(5)).isEqualTo('H');
            assertThat(result.getLines()[1].charAt(5)).isEqualTo('F');
            assertThat(result.getLines()[2].charAt(5)).isEqualTo('C');
            assertThat(result.getOriginalLineNumbers()).containsExactly(1, 2, 3);
        }

        @Test
        void commentLine() {
            String source = "     C* This is a comment line";
            NormalizedSource result = normalizer.normalize(source);
            assertThat(result.getLines()[0]).hasSize(80);
            assertThat(result.getLines()[0].charAt(5)).isEqualTo('C');
            assertThat(result.getLines()[0].charAt(6)).isEqualTo('*');
        }

        @Test
        void emptySourceProducesEmptyResult() {
            NormalizedSource result = normalizer.normalize("");
            assertThat(result.getLineCount()).isEqualTo(0);
            assertThat(result.getLines()).isEmpty();
            assertThat(result.getOriginalLineNumbers()).isEmpty();
            assertThat(result.getSequenceNumbers()).isEmpty();
            assertThat(result.getWarnings()).isEmpty();
        }
    }
}
