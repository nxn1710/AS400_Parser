package com.as400parser.cl.model;

/**
 * Represents a parameter passed to a CL Command.
 * It can track whether the parameter was positional `(VALUE)` or keyword-driven `KEYWORD(VALUE)`.
 */
public class ClParameter {
    /** The parameter keyword name, if specified. */
    private String keyword;
    
    /** The parameter's assigned value. */
    private String value;
    
    /** True if the parameter did not declare a keyword explicitly. */
    private boolean isPositional;

    public ClParameter() {}

    public ClParameter(String keyword, String value, boolean isPositional) {
        this.keyword = keyword;
        this.value = value;
        this.isPositional = isPositional;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isPositional() {
        return isPositional;
    }

    public void setPositional(boolean positional) {
        isPositional = positional;
    }
}
