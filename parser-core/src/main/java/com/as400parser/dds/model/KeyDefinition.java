package com.as400parser.dds.model;

import com.as400parser.common.model.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a key field specification in a DDS record format.
 * Entry type 'K' at column 17.
 */
public class KeyDefinition {

    private Location location;
    private String rawSourceLine;

    /** Cols 8-16: conditioning indicators, stored as raw string. */
    private String conditioningIndicators;

    /** Cols 19-28: key field name (e.g., "STUSCL"). */
    private String fieldName;

    /** "ascending" (default) or "descending" (if DESCEND keyword present). */
    private String sortOrder;

    /** Key-level keywords: DESCEND, SIGNED, UNSIGNED, ABSVAL, NOALTSEQ, DIGIT, ZONE. */
    private List<DdsKeyword> keywords;

    public KeyDefinition() {
        this.sortOrder = "ascending";
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

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public List<DdsKeyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<DdsKeyword> keywords) {
        this.keywords = keywords;
    }
}
