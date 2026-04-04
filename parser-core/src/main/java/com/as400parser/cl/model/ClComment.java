package com.as400parser.cl.model;

/**
 * Represents a parsed CL comment block.
 * Multiline comments are resolved with newline characters bridging physical lines.
 */
public class ClComment {
    /** The content of the comment missing delimited slash-asterisks. */
    private String text;
    
    /** Start line in the physical source file. */
    private int line;

    public ClComment() {}

    public ClComment(String text, int line) {
        this.text = text;
        this.line = line;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }
}
