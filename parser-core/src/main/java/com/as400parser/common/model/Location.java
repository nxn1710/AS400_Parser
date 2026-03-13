package com.as400parser.common.model;

/**
 * Source location reference in the original source file.
 */
public class Location {
    private int startLine;
    private int endLine;
    private int startColumn;
    private int endColumn;

    public Location() {}

    public Location(int startLine, int endLine, int startColumn, int endColumn) {
        this.startLine = startLine;
        this.endLine = endLine;
        this.startColumn = startColumn;
        this.endColumn = endColumn;
    }

    public static Location ofLine(int line) {
        return new Location(line, line, 1, 80);
    }

    public int getStartLine() { return startLine; }
    public void setStartLine(int startLine) { this.startLine = startLine; }
    public int getEndLine() { return endLine; }
    public void setEndLine(int endLine) { this.endLine = endLine; }
    public int getStartColumn() { return startColumn; }
    public void setStartColumn(int startColumn) { this.startColumn = startColumn; }
    public int getEndColumn() { return endColumn; }
    public void setEndColumn(int endColumn) { this.endColumn = endColumn; }
}
