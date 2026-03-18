package com.as400parser.dds.model;

/**
 * Represents a single join field pair from a JFLD keyword.
 * JFLD(fromField toField) maps a field in the "from" file to one in the "to" file.
 */
public class JoinFieldPair {

    /** Field from the "from" file. */
    private String fromField;

    /** Corresponding field in the "to" file. */
    private String toField;

    public JoinFieldPair() {
    }

    public JoinFieldPair(String fromField, String toField) {
        this.fromField = fromField;
        this.toField = toField;
    }

    // --- Getters & Setters ---

    public String getFromField() {
        return fromField;
    }

    public void setFromField(String fromField) {
        this.fromField = fromField;
    }

    public String getToField() {
        return toField;
    }

    public void setToField(String toField) {
        this.toField = toField;
    }
}
