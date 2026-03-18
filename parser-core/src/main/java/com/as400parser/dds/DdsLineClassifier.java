package com.as400parser.dds;

/**
 * Classifies each normalized DDS line into a {@link DdsLineType}.
 * <p>
 * Classification is based on fixed column positions in the DDS A-spec:
 * <ul>
 *   <li>Col 6 — form type (must be 'A')</li>
 *   <li>Col 7 — comment indicator ('*')</li>
 *   <li>Col 17 — entry type (R/K/S/O/J/blank)</li>
 *   <li>Cols 19-28 — name area</li>
 * </ul>
 * The {@code seenRecord} flag distinguishes file-level keywords (before first R)
 * from continuation lines (after).
 */
public class DdsLineClassifier {

    /**
     * Classify a single normalized DDS line.
     *
     * @param line       the normalized source line (padded to 80 chars)
     * @param seenRecord whether a record format ('R') has been seen previously
     * @return the classified line type
     */
    public DdsLineType classify(String line, boolean seenRecord) {
        if (line == null || line.isBlank()) {
            return DdsLineType.BLANK;
        }
        if (line.length() < 7) {
            return DdsLineType.BLANK;
        }

        // Col 6: form type (0-indexed: 5) — must be 'A'
        char formType = line.charAt(5);
        if (formType != 'A' && formType != 'a') {
            return DdsLineType.BLANK;
        }

        // Col 7: comment indicator (0-indexed: 6)
        char commentIndicator = line.charAt(6);
        if (commentIndicator == '*') {
            return DdsLineType.COMMENT;
        }

        // Col 17: entry type (0-indexed: 16)
        char nameType = (line.length() > 16) ? line.charAt(16) : ' ';
        switch (nameType) {
            case 'R': return DdsLineType.RECORD_FORMAT;
            case 'K': return DdsLineType.KEY_FIELD;
            case 'S': return DdsLineType.SELECT_FIELD;
            case 'O': return DdsLineType.OMIT_FIELD;
            case 'J': return DdsLineType.JOIN_SPEC;
        }

        // Blank name type → field definition or file-level keyword or continuation
        // Check cols 19-28 for name (0-indexed: 18-27)
        String nameArea = (line.length() >= 28) ? line.substring(18, 28).trim() : "";
        if (!nameArea.isEmpty()) {
            return DdsLineType.FIELD_DEFINITION;
        }

        // No name → keyword-only line
        if (!seenRecord) {
            return DdsLineType.FILE_KEYWORD;
        }
        return DdsLineType.CONTINUATION;
    }
}
