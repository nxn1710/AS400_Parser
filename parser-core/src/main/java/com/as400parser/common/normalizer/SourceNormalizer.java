package com.as400parser.common.normalizer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Normalizes raw RPG source text into a standardized format suitable for parsing.
 * <p>
 * Processing pipeline (steps applied in exact order):
 * <ol>
 *   <li>Split lines — handle \r\n, \r, \n</li>
 *   <li>Expand tabs — configurable tab stops (default: every 8 columns)</li>
 *   <li>Strip control characters — remove 0x00–0x1F except SO (0x0E) and SI (0x0F) for DBCS</li>
 *   <li>Trim trailing whitespace + right-pad to 80 chars (DBCS-aware)</li>
 *   <li>Extract sequence numbers — cols 1–5 → sequenceNumbers[], replace with spaces</li>
 *   <li>Track original line number mapping — 1-based</li>
 * </ol>
 */
public class SourceNormalizer {

    /** Standard RPG source line width */
    private static final int LINE_WIDTH = 80;

    /** SO (Shift Out) — start of DBCS sequence */
    private static final char SO = 0x0E;

    /** SI (Shift In) — end of DBCS sequence */
    private static final char SI = 0x0F;

    /** Default tab stops: every 8 columns */
    private static final int[] DEFAULT_TAB_STOPS = {};

    /** SEU-style tab stops for RPG */
    public static final int[] SEU_TAB_STOPS = {6, 7, 17, 27, 32, 42, 48, 51, 53, 59, 74};

    private final int[] tabStops;
    private final boolean stripSequenceNumbers;

    /**
     * Creates a normalizer with default 8-column tab stops and sequence number stripping enabled.
     */
    public SourceNormalizer() {
        this(DEFAULT_TAB_STOPS, true);
    }

    /**
     * Creates a normalizer with custom tab stops and sequence number stripping enabled.
     *
     * @param tabStops array of 1-based column positions for tab stops.
     *                 Empty array means standard 8-column tab stops.
     */
    public SourceNormalizer(int[] tabStops) {
        this(tabStops, true);
    }

    /**
     * Creates a normalizer with custom tab stops and sequence number stripping flag.
     *
     * @param tabStops array of 1-based column positions for tab stops.
     * @param stripSequenceNumbers if true, columns 1-5 are extracted as sequence numbers and replaced with spaces.
     */
    public SourceNormalizer(int[] tabStops, boolean stripSequenceNumbers) {
        this.tabStops = tabStops;
        this.stripSequenceNumbers = stripSequenceNumbers;
    }

    /**
     * Normalize source from a file path.
     *
     * @param sourcePath path to the source file
     * @param charset    character encoding to use
     * @return normalized source
     * @throws IOException if the file cannot be read
     */
    public NormalizedSource normalize(Path sourcePath, Charset charset) throws IOException {
        String content = Files.readString(sourcePath, charset);
        NormalizedSource result = normalize(content);
        result.setDetectedCharset(charset);
        return result;
    }

    /**
     * Normalize source from a file path with automatic encoding detection.
     * Detects UTF-8, Shift-JIS, EUC-JP via BOM and byte-pattern heuristics.
     *
     * @param sourcePath path to the source file
     * @return normalized source
     * @throws IOException if the file cannot be read
     */
    public NormalizedSource normalize(Path sourcePath) throws IOException {
        Charset detected = EncodingDetector.detect(sourcePath);
        return normalize(sourcePath, detected);
    }

    /**
     * Normalize source from a string.
     *
     * @param source the raw source text
     * @return normalized source
     */
    public NormalizedSource normalize(String source) {
        List<NormalizationWarning> warnings = new ArrayList<>();

        // Step 1: Split lines — handle \r\n, \r, \n
        String[] rawLines = splitLines(source);

        String[] normalizedLines = new String[rawLines.length];
        int[] originalLineNumbers = new int[rawLines.length];
        String[] sequenceNumbers = new String[rawLines.length];

        for (int i = 0; i < rawLines.length; i++) {
            int lineNum = i + 1; // 1-based
            String line = rawLines[i];

            // Step 2: Expand tabs
            line = expandTabs(line);

            // Step 3: Strip control characters (preserve SO/SI for DBCS)
            line = stripControlChars(line, lineNum, warnings);

            // Step 4: Trim trailing whitespace + right-pad to at least 80 chars
            //         Lines longer than 80 are kept in full (parser accesses col 81+ for comments)
            line = trimAndPad(line, lineNum, warnings);

            // Step 5: Extract sequence numbers (cols 1-5)
            if (stripSequenceNumbers) {
                sequenceNumbers[i] = line.substring(0, 5).trim();
                // Replace cols 1-5 with spaces
                line = "     " + line.substring(5);
            } else {
                sequenceNumbers[i] = "";
            }

            // Step 6: Track original line number mapping
            originalLineNumbers[i] = lineNum;

            normalizedLines[i] = line;
        }

        return new NormalizedSource(normalizedLines, originalLineNumbers, sequenceNumbers, warnings);
    }

    /**
     * Step 1: Split source text into lines.
     * Handles \r\n, \r, and \n line endings.
     * Preserves empty lines.
     */
    String[] splitLines(String source) {
        if (source == null || source.isEmpty()) {
            return new String[0];
        }
        // Split on \r\n, \r, or \n, preserving empty trailing lines
        // Use a manual approach to ensure consistent behavior
        List<String> lines = new ArrayList<>();
        int start = 0;
        int len = source.length();

        for (int i = 0; i < len; i++) {
            char c = source.charAt(i);
            if (c == '\r') {
                lines.add(source.substring(start, i));
                // Skip \n if \r\n pair
                if (i + 1 < len && source.charAt(i + 1) == '\n') {
                    i++;
                }
                start = i + 1;
            } else if (c == '\n') {
                lines.add(source.substring(start, i));
                start = i + 1;
            }
        }
        // Add the last segment (text after the final newline, or the only line with no newline)
        if (start <= len) {
            String lastLine = source.substring(start);
            // Only add if there's content or if the source ended with a newline
            if (!lastLine.isEmpty() || start < len) {
                lines.add(lastLine);
            }
        }

        // Remove trailing empty line if source ended with a newline
        if (!lines.isEmpty() && lines.get(lines.size() - 1).isEmpty() && source.endsWith("\n") || source.endsWith("\r")) {
            lines.remove(lines.size() - 1);
        }

        return lines.toArray(new String[0]);
    }

    /**
     * Step 2: Expand tab characters to spaces.
     * Uses configured tab stops or standard 8-column tabs.
     */
    String expandTabs(String line) {
        if (line.indexOf('\t') < 0) {
            return line; // Fast path: no tabs
        }

        StringBuilder sb = new StringBuilder(LINE_WIDTH);
        int col = 0; // 0-based column position

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\t') {
                int nextStop = getNextTabStop(col);
                int spaces = nextStop - col;
                for (int s = 0; s < spaces; s++) {
                    sb.append(' ');
                }
                col = nextStop;
            } else {
                sb.append(c);
                col++;
            }
        }

        return sb.toString();
    }

    /**
     * Get the next tab stop position after the given 0-based column.
     */
    private int getNextTabStop(int col) {
        if (tabStops == null || tabStops.length == 0) {
            // Standard 8-column tab stops
            return ((col / 8) + 1) * 8;
        }

        // Custom tab stops (1-based in config, convert to 0-based for comparison)
        for (int stop : tabStops) {
            int zeroBased = stop - 1;
            if (zeroBased > col) {
                return zeroBased;
            }
        }

        // Beyond last defined tab stop: fall back to 8-column stops
        return ((col / 8) + 1) * 8;
    }

    /**
     * Step 3: Strip control characters (0x00–0x1F) except:
     * - SO (0x0E) — start of DBCS sequence
     * - SI (0x0F) — end of DBCS sequence
     */
    String stripControlChars(String line, int lineNum, List<NormalizationWarning> warnings) {
        StringBuilder sb = new StringBuilder(line.length());
        boolean modified = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c < 0x20 && c != SO && c != SI) {
                // Strip this control character
                modified = true;
            } else {
                sb.append(c);
            }
        }

        if (modified) {
            warnings.add(new NormalizationWarning(
                    lineNum, 0,
                    "Control characters stripped from line",
                    NormalizationWarning.WarningType.CONTROL_CHAR_STRIPPED
            ));
        }

        return modified ? sb.toString() : line;
    }

    /**
     * Step 4: Trim trailing whitespace and right-pad to at least 80 chars.
     * Lines longer than 80 chars are preserved in full.
     * DBCS-aware: characters between SO/SI count as 2 column positions each.
     */
    String trimAndPad(String line, int lineNum, List<NormalizationWarning> warnings) {
        // Trim trailing whitespace
        String trimmed = trimTrailingWhitespace(line);

        // Calculate display width (DBCS-aware)
        int displayWidth = calculateDisplayWidth(trimmed);

        // Right-pad to at least 80 chars (don't truncate longer lines)
        if (displayWidth < LINE_WIDTH) {
            StringBuilder sb = new StringBuilder(trimmed);
            for (int i = displayWidth; i < LINE_WIDTH; i++) {
                sb.append(' ');
            }
            return sb.toString();
        }

        return trimmed;
    }

    /**
     * Trim trailing whitespace characters.
     */
    private String trimTrailingWhitespace(String line) {
        int end = line.length();
        while (end > 0 && line.charAt(end - 1) <= ' ' && line.charAt(end - 1) != SO && line.charAt(end - 1) != SI) {
            end--;
        }
        return end < line.length() ? line.substring(0, end) : line;
    }

    /**
     * Calculate the display width of a line, accounting for DBCS characters.
     * Characters between SO (0x0E) and SI (0x0F) occupy 2 column positions each.
     * SO and SI themselves occupy 0 column positions (they are shift codes).
     */
    int calculateDisplayWidth(String line) {
        int width = 0;
        boolean inDbcs = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == SO) {
                inDbcs = true;
                // SO itself doesn't occupy a display column
            } else if (c == SI) {
                inDbcs = false;
                // SI itself doesn't occupy a display column
            } else if (inDbcs) {
                width += 2; // DBCS character = 2 column positions
            } else if (isFullWidthChar(c)) {
                width += 2; // Unicode full-width character = 2 column positions
            } else {
                width += 1; // SBCS character = 1 column position
            }
        }

        return width;
    }

    /**
     * Determine if a Unicode character is full-width (occupies 2 column positions).
     * This covers CJK Unified Ideographs, Katakana, Hiragana, CJK symbols, and
     * other common fullwidth ranges used in Japanese/Chinese/Korean text.
     */
    static boolean isFullWidthChar(char c) {
        // CJK Unified Ideographs (U+4E00–U+9FFF)
        if (c >= 0x4E00 && c <= 0x9FFF) return true;
        // CJK Unified Ideographs Extension A (U+3400–U+4DBF)
        if (c >= 0x3400 && c <= 0x4DBF) return true;
        // CJK Compatibility Ideographs (U+F900–U+FAFF)
        if (c >= 0xF900 && c <= 0xFAFF) return true;
        // Hiragana (U+3040–U+309F)
        if (c >= 0x3040 && c <= 0x309F) return true;
        // Katakana (U+30A0–U+30FF)
        if (c >= 0x30A0 && c <= 0x30FF) return true;
        // Katakana Phonetic Extensions (U+31F0–U+31FF)
        if (c >= 0x31F0 && c <= 0x31FF) return true;
        // CJK Symbols and Punctuation (U+3000–U+303F)
        if (c >= 0x3000 && c <= 0x303F) return true;
        // Fullwidth Latin and Halfwidth Katakana (U+FF00–U+FFEF)
        // Fullwidth ASCII variants (U+FF01–U+FF60) are fullwidth
        if (c >= 0xFF01 && c <= 0xFF60) return true;
        // Hangul Syllables (U+AC00–U+D7AF)
        if (c >= 0xAC00 && c <= 0xD7AF) return true;
        // Enclosed CJK Letters and Months (U+3200–U+32FF)
        if (c >= 0x3200 && c <= 0x32FF) return true;
        // CJK Compatibility (U+3300–U+33FF)
        if (c >= 0x3300 && c <= 0x33FF) return true;
        // Bopomofo (U+3100–U+312F)
        if (c >= 0x3100 && c <= 0x312F) return true;
        return false;
    }
}
