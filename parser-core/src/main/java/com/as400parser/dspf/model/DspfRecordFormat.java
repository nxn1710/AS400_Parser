package com.as400parser.dspf.model;

import com.as400parser.common.model.Location;
import com.as400parser.dds.model.KeyDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a record format in a Display File (DSPF).
 * <p>
 * Entry type 'R' at column 17. DSPF record formats have a {@code recordType}
 * that distinguishes normal records, subfile records (SFL), and subfile control
 * records (SFLCTL).
 * <p>
 * Record type is detected from keywords:
 * <ul>
 *   <li>{@code "sfl"} — has SFL keyword (but not SFLCTL)</li>
 *   <li>{@code "sflctl"} — has SFLCTL keyword</li>
 *   <li>{@code "normal"} — neither SFL nor SFLCTL</li>
 * </ul>
 */
public class DspfRecordFormat {

    /** Source position. */
    private Location location;

    /** Original source lines (R-line + continuation keyword lines). */
    private List<String> rawSourceLines;

    /** Conditioning indicators on the record format line (cols 8-16). */
    private List<ConditioningIndicator> conditioningIndicators;

    /** Cols 19-28: record format name (e.g., "MNUREC", "STUSFL", "STUSFC"). */
    private String name;

    /** DSPF record type: "normal", "sfl" (subfile), "sflctl" (subfile control). */
    private String recordType;

    /**
     * For sflctl type: name of the SFL record format it controls
     * (extracted from SFLCTL(name) keyword). null for non-SFLCTL records.
     */
    private String sflControlFor;

    /** Convenience: extracted from TEXT(...) keyword. null if absent. */
    private String text;

    /** Record-level keywords, each with optional conditioning indicators. */
    private List<ConditionedKeyword> keywords;

    /** Named field definitions. */
    private List<DspfFieldDefinition> fields;

    /** Unnamed display constants (text labels, system keywords). */
    private List<DspfConstant> constants;

    /** Key field specifications (rare in DSPF; reuses KeyDefinition from dds.model). */
    private List<KeyDefinition> keys;

    public DspfRecordFormat() {
        this.rawSourceLines = new ArrayList<>();
        this.conditioningIndicators = new ArrayList<>();
        this.keywords = new ArrayList<>();
        this.fields = new ArrayList<>();
        this.constants = new ArrayList<>();
        this.keys = new ArrayList<>();
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

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public String getSflControlFor() {
        return sflControlFor;
    }

    public void setSflControlFor(String sflControlFor) {
        this.sflControlFor = sflControlFor;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<ConditionedKeyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<ConditionedKeyword> keywords) {
        this.keywords = keywords;
    }

    public List<DspfFieldDefinition> getFields() {
        return fields;
    }

    public void setFields(List<DspfFieldDefinition> fields) {
        this.fields = fields;
    }

    public List<DspfConstant> getConstants() {
        return constants;
    }

    public void setConstants(List<DspfConstant> constants) {
        this.constants = constants;
    }

    public List<KeyDefinition> getKeys() {
        return keys;
    }

    public void setKeys(List<KeyDefinition> keys) {
        this.keys = keys;
    }
}
