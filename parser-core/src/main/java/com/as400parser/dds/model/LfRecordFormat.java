package com.as400parser.dds.model;

import com.as400parser.common.model.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a record format in a Logical File (LF).
 * Extends the PF RecordFormat concept with LF-specific fields:
 * pfile, jfile, lfType, selectOmit, joinSpecs.
 */
public class LfRecordFormat {

    private Location location;
    private String rawSourceLine;

    /** Cols 8-16: conditioning indicators, stored as raw string. */
    private String conditioningIndicators;

    /** Cols 19-28: record format name. */
    private String name;

    /** LF subtype: "simple", "multipleFormat", or "join". */
    private String lfType;

    /** Physical file name from PFILE(name) keyword. null for join LFs. */
    private String pfile;

    /** Physical file names from JFILE(pf1 pf2 ...). null for simple/multiple LFs. */
    private List<String> jfile;

    /** Convenience: extracted from TEXT() keyword. */
    private String text;

    /** Record-level keywords: PFILE, JFILE, TEXT, FORMAT, DYNSLT, etc. */
    private List<DdsKeyword> keywords;

    /** Field definitions/overrides. LFs may inherit from PF, or define derived fields. */
    private List<FieldDefinition> fields;

    /** Key fields (defines the access path). */
    private List<KeyDefinition> keys;

    /** Select/omit criteria (simple/multiple LF only). */
    private List<SelectOmitSpec> selectOmit;

    /** Join specifications (join LF only). */
    private List<JoinSpec> joinSpecs;

    public LfRecordFormat() {
        this.keywords = new ArrayList<>();
        this.fields = new ArrayList<>();
        this.keys = new ArrayList<>();
        this.selectOmit = new ArrayList<>();
        this.joinSpecs = new ArrayList<>();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLfType() {
        return lfType;
    }

    public void setLfType(String lfType) {
        this.lfType = lfType;
    }

    public String getPfile() {
        return pfile;
    }

    public void setPfile(String pfile) {
        this.pfile = pfile;
    }

    public List<String> getJfile() {
        return jfile;
    }

    public void setJfile(List<String> jfile) {
        this.jfile = jfile;
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

    public List<SelectOmitSpec> getSelectOmit() {
        return selectOmit;
    }

    public void setSelectOmit(List<SelectOmitSpec> selectOmit) {
        this.selectOmit = selectOmit;
    }

    public List<JoinSpec> getJoinSpecs() {
        return joinSpecs;
    }

    public void setJoinSpecs(List<JoinSpec> joinSpecs) {
        this.joinSpecs = joinSpecs;
    }
}
