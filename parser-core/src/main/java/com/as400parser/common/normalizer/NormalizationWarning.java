package com.as400parser.common.normalizer;

/**
 * Warning generated during source normalization.
 * Contains the line number, column, message, and type of the warning.
 */
public class NormalizationWarning {

    public enum WarningType {
        /** Line exceeded 80 characters and was truncated */
        TRUNCATION,
        /** Control character was stripped */
        CONTROL_CHAR_STRIPPED,
        /** Tab expansion may have shifted columns */
        TAB_EXPANSION,
        /** DBCS encoding issue */
        DBCS_WARNING,
        /** General normalization warning */
        GENERAL
    }

    private final int line;
    private final int column;
    private final String message;
    private final WarningType type;

    public NormalizationWarning(int line, int column, String message, WarningType type) {
        this.line = line;
        this.column = column;
        this.message = message;
        this.type = type;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getMessage() {
        return message;
    }

    public WarningType getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("Line %d, Col %d [%s]: %s", line, column, type, message);
    }
}
