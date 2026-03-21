package com.as400parser.dds.model;

import com.as400parser.common.model.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a join specification in a Join Logical File.
 * Entry type 'J' at column 17.
 */
public class JoinSpec {

    private Location location;
    private String rawSourceLine;

    /** Cols 8-16: conditioning indicators, stored as raw string. */
    private String conditioningIndicators;

    /** From-file in the join (first arg of JOIN keyword). */
    private String fromFile;

    /** To-file in the join (second arg of JOIN keyword). */
    private String toFile;

    /** Join field pairs from JFLD keywords. */
    private List<JoinFieldPair> joinFields;

    /** Join-level keywords: JOIN, JFLD, JDUPSEQ, JDFTVAL, JREF. */
    private List<DdsKeyword> keywords;

    public JoinSpec() {
        this.joinFields = new ArrayList<>();
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

    public String getFromFile() {
        return fromFile;
    }

    public void setFromFile(String fromFile) {
        this.fromFile = fromFile;
    }

    public String getToFile() {
        return toFile;
    }

    public void setToFile(String toFile) {
        this.toFile = toFile;
    }

    public List<JoinFieldPair> getJoinFields() {
        return joinFields;
    }

    public void setJoinFields(List<JoinFieldPair> joinFields) {
        this.joinFields = joinFields;
    }

    public List<DdsKeyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<DdsKeyword> keywords) {
        this.keywords = keywords;
    }
}
