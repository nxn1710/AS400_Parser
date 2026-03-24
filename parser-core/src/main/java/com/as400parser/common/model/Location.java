package com.as400parser.common.model;

/**
 * Source location reference in the original source file.
 */
public class Location {
    private int startLine;
    private int endLine;

    public Location() {}

    public Location(int startLine, int endLine) {
        this.startLine = startLine;
        this.endLine = endLine;
    }

    public static Location ofLine(int line) {
        return new Location(line, line);
    }

    public int getStartLine() { return startLine; }
    public void setStartLine(int startLine) { this.startLine = startLine; }
    public int getEndLine() { return endLine; }
    public void setEndLine(int endLine) { this.endLine = endLine; }
}
