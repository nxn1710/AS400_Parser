package com.as400parser.dds.model;

import com.as400parser.common.model.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a record format in a Physical File (PF).
 * Entry type 'R' at column 17.
 * PF typically has exactly 1 record format.
 */
public class RecordFormat {

    private Location location;
    private String rawSourceLine;

    /** Cols 8-16: conditioning indicators, stored as raw string. */
    private String conditioningIndicators;

    /** Cols 19-28: record format name (e.g., "STUREC"). */
    private String name;

    /** Convenience: extracted from TEXT() keyword. */
    private String text;

    /** Record-level keywords. */
    private List<DdsKeyword> keywords;

    /** Field definitions within this format. */
    private List<FieldDefinition> fields;

    /** Key field specifications. */
    private List<KeyDefinition> keys;

    public RecordFormat() {
        this.keywords = new ArrayList<>();
        this.fields = new ArrayList<>();
        this.keys = new ArrayList<>();
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
        this.rawSourceLine = rawSourceLine;
    }

    public String getConditioningIndicators() {
        return conditioningIndicators;
    }

    public void setConditioningIndicators(String conditioningIndicators) {
        this.conditioningIndicators = conditioningIndicators;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<DdsKeyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<DdsKeyword> keywords) {
        this.keywords = keywords;
    }

    public List<FieldDefinition> getFields() {
        return fields;
    }

    public void setFields(List<FieldDefinition> fields) {
        this.fields = fields;
    }

    public List<KeyDefinition> getKeys() {
        return keys;
    }

    public void setKeys(List<KeyDefinition> keys) {
        this.keys = keys;
    }
}
