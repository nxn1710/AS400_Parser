package com.as400parser.dds.model;

import com.as400parser.common.model.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a select or omit specification in a Logical File.
 * Entry type 'S' (select) or 'O' (omit) at column 17.
 */
public class SelectOmitSpec {

    private Location location;
    private String rawSourceLine;

    /** Cols 8-16: conditioning indicators, stored as raw string. */
    private String conditioningIndicators;

    /** "select" or "omit" (from S/O in col 17). */
    private String type;

    /** Cols 19-28: field name being tested. null for ALL rule. */
    private String fieldName;

    /** "all" if ALL keyword is present (catch-all rule), null otherwise. */
    private String rule;

    /** Select/omit keywords: COMP, RANGE, VALUES, ALL. */
    private List<DdsKeyword> keywords;

    public SelectOmitSpec() {
        this.keywords = new ArrayList<>();
    }

    // --- Getters & Setters ---

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getRawSourceLine() {
        return rawSourceLine;
    }

    public void setRawSourceLine(String rawSourceLine) {
        this.rawSourceLine = rawSourceLine != null ? rawSourceLine.stripTrailing() : null;
    }

    public String getConditioningIndicators() {
        return conditioningIndicators;
    }

    public void setConditioningIndicators(String conditioningIndicators) {
        this.conditioningIndicators = conditioningIndicators;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public List<DdsKeyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<DdsKeyword> keywords) {
        this.keywords = keywords;
    }
}
