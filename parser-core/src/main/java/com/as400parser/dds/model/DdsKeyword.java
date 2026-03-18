package com.as400parser.dds.model;


import java.util.List;

/**
 * Unified representation for all DDS keywords at any level
 * (file, record, field, key, select/omit, join).
 * <p>
 * Keyword area is columns 45–80 of the DDS A-spec.
 * 50 keywords recognized across 5 categories.
 */
public class DdsKeyword {

    /** Keyword name, always UPPERCASE (e.g., "TEXT", "UNIQUE", "COMP"). CMP is normalized to COMP. */
    private String name;

    /** Single value: DFT('A') → "A"; PFILE(STUDNTPF) → "STUDNTPF". null for no-arg keywords. */
    private String value;

    /** Multi-value: VALUES('A' 'B') → ["A","B"]; CONCAT(FLD1 FLD2) → ["FLD1","FLD2"]. */
    private List<String> values;

    /** Full raw keyword text for round-tripping (e.g., "TEXT('学生ID')"). */
    private String rawText;

    /** COMP only: comparison operator "EQ","NE","GT","GE","LT","LE". null for non-COMP keywords. */
    private String comparisonOperator;

    /** COMP only: the comparison value. null for non-COMP keywords. */
    private String comparisonValue;

    /** RANGE only: low value. null for non-RANGE keywords. */
    private String rangeFrom;

    /** RANGE only: high value. null for non-RANGE keywords. */
    private String rangeTo;

    public DdsKeyword() {
    }

    // --- Getters & Setters ---

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public String getComparisonOperator() {
        return comparisonOperator;
    }

    public void setComparisonOperator(String comparisonOperator) {
        this.comparisonOperator = comparisonOperator;
    }

    public String getComparisonValue() {
        return comparisonValue;
    }

    public void setComparisonValue(String comparisonValue) {
        this.comparisonValue = comparisonValue;
    }

    public String getRangeFrom() {
        return rangeFrom;
    }

    public void setRangeFrom(String rangeFrom) {
        this.rangeFrom = rangeFrom;
    }

    public String getRangeTo() {
        return rangeTo;
    }

    public void setRangeTo(String rangeTo) {
        this.rangeTo = rangeTo;
    }
}
