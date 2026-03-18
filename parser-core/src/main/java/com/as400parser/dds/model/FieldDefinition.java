package com.as400parser.dds.model;

import com.as400parser.common.model.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a field definition in a DDS record format.
 * <p>
 * Captures all column positions (1–80) from the DDS A-spec:
 * cols 8-16 (conditioning), 19-28 (name), 29 (reference), 30-34 (length),
 * 35 (data type), 36-37 (decimal), 38 (usage), 39-44 (location), 45-80 (keywords).
 */
public class FieldDefinition {

    /** Source position (may span multiple lines if continuation). */
    private Location location;

    /** All source lines for this field (including continuations). */
    private List<String> rawSourceLines;

    /** Cols 8-16: conditioning indicators, stored as raw string. null if blank. */
    private String conditioningIndicators;

    /** Cols 19-28: field name (e.g., "STUID"). */
    private String name;

    /** Col 29: "R" if using REF/REFFLD, null otherwise. */
    private String reference;

    /** Cols 30-34: field length. null if inherited via REF. */
    private Integer length;

    /** Col 35: DDS data type code (A, P, S, B, F, H, L, T, Z, G). null if blank. */
    private String dataType;

    /** Cols 36-37: decimal positions. null for character/non-numeric types. */
    private Integer decimalPositions;

    /** Col 38: usage code (B, I, O, N, H, M). null if blank. */
    private String usage;

    /** Cols 39-44: location / comment area. DSPF: line/position; PF/LF: comment area. null if blank. */
    private String fieldLocation;

    /**
     * Computed field source type:
     * "direct" — defined inline with length/type;
     * "reference" — uses REF/REFFLD;
     * "derived" — uses CONCAT/SST/RENAME.
     */
    private String source;

    /** Field-level keywords from cols 45-80 (parsed by DdsKeywordParser). */
    private List<DdsKeyword> keywords;

    public FieldDefinition() {
        this.rawSourceLines = new ArrayList<>();
        this.keywords = new ArrayList<>();
    }

    // --- Getters & Setters ---

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<String> getRawSourceLines() {
        return rawSourceLines;
    }

    public void setRawSourceLines(List<String> rawSourceLines) {
        this.rawSourceLines = rawSourceLines;
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

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Integer getDecimalPositions() {
        return decimalPositions;
    }

    public void setDecimalPositions(Integer decimalPositions) {
        this.decimalPositions = decimalPositions;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getFieldLocation() {
        return fieldLocation;
    }

    public void setFieldLocation(String fieldLocation) {
        this.fieldLocation = fieldLocation;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<DdsKeyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<DdsKeyword> keywords) {
        this.keywords = keywords;
    }
}
