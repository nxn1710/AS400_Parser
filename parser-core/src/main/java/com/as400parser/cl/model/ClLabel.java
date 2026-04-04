package com.as400parser.cl.model;

/**
 * A marker representing a routing or executable target point within CL source.
 */
public class ClLabel {
    /** The verbatim name of the destination label without the trailing colon. */
    private String name;
    
    /** Reference to the line on which this label begins. */
    private int line;

    public ClLabel() {}

    public ClLabel(String name, int line) {
        this.name = name;
        this.line = line;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }
}
