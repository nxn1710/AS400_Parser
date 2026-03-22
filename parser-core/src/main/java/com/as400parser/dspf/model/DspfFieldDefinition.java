package com.as400parser.dspf.model;

import com.as400parser.common.model.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a named field definition in a DSPF record format.
 * <p>
 * Extends the PF/LF FieldDefinition concept with DSPF-specific attributes:
 * screen coordinates, usage code, and per-keyword conditioning indicators.
 * <p>
 * Captures all DDS A-spec column positions:
 * cols 8-16 (conditioning), 19-28 (name), 29 (reference), 30-34 (length),
 * 35 (data type), 36-37 (decimal), 38 (usage), 39-41 (screen line),
 * 42-44 (screen position), 45-80 (keywords).
 */
public class DspfFieldDefinition {

    /** Source position (may span multiple lines if continuation). */
    private Location location;

    /** All source lines for this field (including continuations). */
    private List<String> rawSourceLines;

    /** Conditioning indicators on field definition line (cols 8-16). */
    private List<ConditioningIndicator> conditioningIndicators;

    /** Cols 19-28: field name (e.g., "MNUOPT", "SFSTID"). */
    private String name;

    /** Col 29: "R" if using REF/REFFLD, null otherwise. */
    private String reference;

    /** Cols 30-34: field length. null if inherited via REF. */
    private Integer length;

    /** Col 35: DDS data type code (A, S, P, B, F, Y, L, T, Z, O, G). null if blank. */
    private String dataType;

    /** Cols 36-37: decimal positions. null for character/non-numeric types. */
    private Integer decimalPositions;

    /** Col 38: usage code — "B" (Both), "I" (Input), "O" (Output), "H" (Hidden), "M" (Message), "P". null if blank. */
    private String usage;

    /** Cols 39-41: screen line number. null if not specified. */
    private Integer screenLine;

    /** Cols 42-44: screen column position. null if not specified. */
    private Integer screenPosition;

    /**
     * Computed field source type:
     * "direct" — defined inline with length/type;
     * "reference" — uses REF/REFFLD.
     */
    private String source;

    /** Referenced field name from REFFLD keyword, or own name for same-name ref. */
    private String referenceField;

    /** Referenced file name from REFFLD keyword (2nd or 3rd arg). */
    private String referenceFile;

    /** Referenced record format from REFFLD keyword (3-arg form). */
    private String referenceRecordFormat;

    /** Field-level keywords with optional conditioning indicators. */
    private List<ConditionedKeyword> keywords;

    public DspfFieldDefinition() {
        this.rawSourceLines = new ArrayList<>();
        this.conditioningIndicators = new ArrayList<>();
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

    public List<ConditioningIndicator> getConditioningIndicators() {
        return conditioningIndicators;
    }

    public void setConditioningIndicators(List<ConditioningIndicator> conditioningIndicators) {
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

    public Integer getScreenLine() {
        return screenLine;
    }

    public void setScreenLine(Integer screenLine) {
        this.screenLine = screenLine;
    }

    public Integer getScreenPosition() {
        return screenPosition;
    }

    public void setScreenPosition(Integer screenPosition) {
        this.screenPosition = screenPosition;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<ConditionedKeyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<ConditionedKeyword> keywords) {
        this.keywords = keywords;
    }

    public String getReferenceField() {
        return referenceField;
    }

    public void setReferenceField(String referenceField) {
        this.referenceField = referenceField;
    }

    public String getReferenceFile() {
        return referenceFile;
    }

    public void setReferenceFile(String referenceFile) {
        this.referenceFile = referenceFile;
    }

    public String getReferenceRecordFormat() {
        return referenceRecordFormat;
    }

    public void setReferenceRecordFormat(String referenceRecordFormat) {
        this.referenceRecordFormat = referenceRecordFormat;
    }
}
