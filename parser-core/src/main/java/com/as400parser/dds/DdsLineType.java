package com.as400parser.dds;

/**
 * Enumeration of DDS line types, determined by column-position analysis.
 */
public enum DdsLineType {
    /** Col 7 = '*' — comment line. */
    COMMENT,
    /** Entirely blank or too short to classify. */
    BLANK,
    /** No name type, no field name, has keywords (before first R). File-level keyword. */
    FILE_KEYWORD,
    /** Col 17 = 'R' — record format definition. */
    RECORD_FORMAT,
    /** Col 17 = blank, has name in cols 19-28 — field definition. */
    FIELD_DEFINITION,
    /** Col 17 = 'K' — key field specification. */
    KEY_FIELD,
    /** Col 17 = 'S' — select field (LF only). */
    SELECT_FIELD,
    /** Col 17 = 'O' — omit field (LF only). */
    OMIT_FIELD,
    /** Col 17 = 'J' — join specification (Join LF only). */
    JOIN_SPEC,
    /** No name, no name type, has keywords (after a named line). Continuation. */
    CONTINUATION
}
