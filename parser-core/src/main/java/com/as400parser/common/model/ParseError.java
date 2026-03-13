package com.as400parser.common.model;

/**
 * Parse error or warning from the parsing process.
 */
public class ParseError {

    public enum Severity {
        ERROR, WARNING, INFO
    }

    private int line;
    private int column;
    private String message;
    private Severity severity;

    public ParseError() {}

    public ParseError(int line, int column, String message, Severity severity) {
        this.line = line;
        this.column = column;
        this.message = message;
        this.severity = severity;
    }

    public static ParseError error(int line, int column, String message) {
        return new ParseError(line, column, message, Severity.ERROR);
    }

    public static ParseError warning(int line, int column, String message) {
        return new ParseError(line, column, message, Severity.WARNING);
    }

    public int getLine() { return line; }
    public void setLine(int line) { this.line = line; }
    public int getColumn() { return column; }
    public void setColumn(int column) { this.column = column; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }
}
