package com.as400parser.common.normalizer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Detects the character encoding of a source file using BOM detection
 * and byte-pattern heuristics.
 * <p>
 * Detection priority:
 * <ol>
 *   <li>BOM (Byte Order Mark) — highest confidence</li>
 *   <li>UTF-8 multibyte validation</li>
 *   <li>Shift-JIS byte patterns (0x81–0x9F / 0xE0–0xEF lead bytes)</li>
 *   <li>EUC-JP byte patterns (0xA1–0xFE lead + 0xA1–0xFE trail)</li>
 *   <li>Pure ASCII → UTF-8</li>
 *   <li>Fallback → UTF-8</li>
 * </ol>
 */
public class EncodingDetector {

    /** Maximum bytes to scan for heuristic detection */
    private static final int SCAN_LIMIT = 8192;

    private static final Charset SHIFT_JIS = Charset.forName("Shift_JIS");
    private static final Charset EUC_JP = Charset.forName("EUC-JP");

    /**
     * Detect the encoding of a file.
     *
     * @param file path to the source file
     * @return detected charset
     * @throws IOException if the file cannot be read
     */
    public static Charset detect(Path file) throws IOException {
        byte[] data = Files.readAllBytes(file);
        return detect(data);
    }

    /**
     * Detect the encoding of raw byte data.
     *
     * @param data raw bytes
     * @return detected charset
     */
    public static Charset detect(byte[] data) {
        if (data == null || data.length == 0) {
            return StandardCharsets.UTF_8;
        }

        // 1. Check BOM
        Charset bomCharset = detectBom(data);
        if (bomCharset != null) {
            return bomCharset;
        }

        // Limit scan to first SCAN_LIMIT bytes
        int scanLen = Math.min(data.length, SCAN_LIMIT);

        // 2. Check if valid UTF-8 with multibyte sequences
        //    (pure ASCII is also valid UTF-8, but we check for multibyte to be sure)
        boolean hasHighBytes = false;
        for (int i = 0; i < scanLen; i++) {
            if ((data[i] & 0x80) != 0) {
                hasHighBytes = true;
                break;
            }
        }

        if (!hasHighBytes) {
            // Pure ASCII — safe to use UTF-8
            return StandardCharsets.UTF_8;
        }

        // 3. Score each encoding candidate
        int utf8Score = scoreUtf8(data, scanLen);
        int sjisScore = scoreShiftJis(data, scanLen);
        int eucjpScore = scoreEucJp(data, scanLen);

        // Pick the highest score
        if (utf8Score >= sjisScore && utf8Score >= eucjpScore && utf8Score > 0) {
            return StandardCharsets.UTF_8;
        } else if (sjisScore >= eucjpScore && sjisScore > 0) {
            return SHIFT_JIS;
        } else if (eucjpScore > 0) {
            return EUC_JP;
        }

        // Fallback
        return StandardCharsets.UTF_8;
    }

    /**
     * Detect encoding from BOM (Byte Order Mark).
     */
    private static Charset detectBom(byte[] data) {
        if (data.length >= 3 &&
            (data[0] & 0xFF) == 0xEF &&
            (data[1] & 0xFF) == 0xBB &&
            (data[2] & 0xFF) == 0xBF) {
            return StandardCharsets.UTF_8;
        }
        if (data.length >= 2 &&
            (data[0] & 0xFF) == 0xFF &&
            (data[1] & 0xFF) == 0xFE) {
            return StandardCharsets.UTF_16LE;
        }
        if (data.length >= 2 &&
            (data[0] & 0xFF) == 0xFE &&
            (data[1] & 0xFF) == 0xFF) {
            return StandardCharsets.UTF_16BE;
        }
        return null;
    }

    /**
     * Score how likely the data is valid UTF-8.
     * Higher = more likely. Returns 0 if invalid UTF-8 sequences found.
     */
    private static int scoreUtf8(byte[] data, int len) {
        int score = 0;
        int i = 0;
        while (i < len) {
            int b = data[i] & 0xFF;
            if (b <= 0x7F) {
                // ASCII
                i++;
                continue;
            }

            int seqLen;
            if ((b & 0xE0) == 0xC0) {
                seqLen = 2; // 110xxxxx
            } else if ((b & 0xF0) == 0xE0) {
                seqLen = 3; // 1110xxxx — CJK range
                score += 3; // Bonus: 3-byte = likely CJK
            } else if ((b & 0xF8) == 0xF0) {
                seqLen = 4; // 11110xxx
            } else {
                return 0; // Invalid UTF-8 lead byte
            }

            // Validate continuation bytes
            if (i + seqLen > len) {
                return 0; // Truncated sequence
            }
            for (int j = 1; j < seqLen; j++) {
                if ((data[i + j] & 0xC0) != 0x80) {
                    return 0; // Invalid continuation byte
                }
            }

            score += seqLen;
            i += seqLen;
        }
        return score;
    }

    /**
     * Score how likely the data is Shift-JIS encoded.
     * Shift-JIS lead bytes: 0x81–0x9F, 0xE0–0xEF
     * Trail bytes: 0x40–0x7E, 0x80–0xFC
     * Half-width katakana: 0xA1–0xDF (single byte)
     */
    private static int scoreShiftJis(byte[] data, int len) {
        int score = 0;
        int i = 0;
        while (i < len) {
            int b = data[i] & 0xFF;

            if (b <= 0x7F) {
                // ASCII
                i++;
                continue;
            }

            // Half-width katakana (single byte, 0xA1–0xDF)
            if (b >= 0xA1 && b <= 0xDF) {
                score += 1;
                i++;
                continue;
            }

            // Double-byte lead: 0x81–0x9F or 0xE0–0xEF
            if ((b >= 0x81 && b <= 0x9F) || (b >= 0xE0 && b <= 0xEF)) {
                if (i + 1 >= len) {
                    return 0; // Truncated
                }
                int trail = data[i + 1] & 0xFF;
                if ((trail >= 0x40 && trail <= 0x7E) || (trail >= 0x80 && trail <= 0xFC)) {
                    score += 2;
                    i += 2;
                    continue;
                } else {
                    return 0; // Invalid trail byte
                }
            }

            // Other high bytes not valid in Shift-JIS
            return 0;
        }
        return score;
    }

    /**
     * Score how likely the data is EUC-JP encoded.
     * EUC-JP: lead 0xA1–0xFE, trail 0xA1–0xFE (two-byte)
     * SS2 (0x8E) + 0xA1–0xDF  (half-width katakana)
     * SS3 (0x8F) + 0xA1–0xFE + 0xA1–0xFE (JIS X 0212)
     */
    private static int scoreEucJp(byte[] data, int len) {
        int score = 0;
        int i = 0;
        while (i < len) {
            int b = data[i] & 0xFF;

            if (b <= 0x7F) {
                // ASCII
                i++;
                continue;
            }

            // SS2: half-width katakana
            if (b == 0x8E) {
                if (i + 1 >= len) return 0;
                int next = data[i + 1] & 0xFF;
                if (next >= 0xA1 && next <= 0xDF) {
                    score += 1;
                    i += 2;
                    continue;
                }
                return 0;
            }

            // SS3: JIS X 0212 (3 bytes)
            if (b == 0x8F) {
                if (i + 2 >= len) return 0;
                int b2 = data[i + 1] & 0xFF;
                int b3 = data[i + 2] & 0xFF;
                if (b2 >= 0xA1 && b2 <= 0xFE && b3 >= 0xA1 && b3 <= 0xFE) {
                    score += 3;
                    i += 3;
                    continue;
                }
                return 0;
            }

            // Standard two-byte
            if (b >= 0xA1 && b <= 0xFE) {
                if (i + 1 >= len) return 0;
                int trail = data[i + 1] & 0xFF;
                if (trail >= 0xA1 && trail <= 0xFE) {
                    score += 2;
                    i += 2;
                    continue;
                }
                return 0;
            }

            // Invalid byte for EUC-JP
            return 0;
        }
        return score;
    }
}
