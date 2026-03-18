package com.as400parser.dds.model;

/**
 * Represents a standalone comment line in DDS source.
 * Comment lines have '*' at column 7.
 */
public class DdsComment {

    /** Source line number (1-based). */
    private int lineNumber;

    /** Comment text (after "A*", trimmed). */
    private String text;

    public DdsComment() {
    }

    public DdsComment(int lineNumber, String text) {
        this.lineNumber = lineNumber;
        this.text = text;
    }

    // --- Getters & Setters ---

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
