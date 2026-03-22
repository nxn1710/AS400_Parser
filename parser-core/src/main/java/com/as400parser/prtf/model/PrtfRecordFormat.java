package com.as400parser.prtf.model;

import com.as400parser.common.model.Location;
import com.as400parser.dds.model.DdsKeyword;
import com.as400parser.dspf.model.ConditioningIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a record format in a Printer File (PRTF).
 * <p>
 * Entry type 'R' at column 17. PRTF record formats are simpler than DSPF:
 * <ul>
 *   <li>No subfile concept — no recordType/sflControlFor</li>
 *   <li>No key fields — K in col 17 is not valid for printer files</li>
 *   <li>Record-level keywords include spacing/skipping (SPACEA/B, SKIPA/B),
 *       page control (FORCE, ENDPAGE), graphical elements (BOX, LINE, GDF,
 *       OVERLAY, PAGSEG, AFPRSC), and formatting (FONT, CPI, LPI)</li>
 * </ul>
 */
public class PrtfRecordFormat {

    /** Source position. */
    private Location location;

    /** Original source text. */
    private String rawSourceLine;

    /** Conditioning indicators on the record format line (cols 8-16). */
    private List<ConditioningIndicator> conditioningIndicators;

    /** Cols 19-28: record format name. */
    private String name;

    /** Convenience: extracted from TEXT(...) keyword. null if absent. */
    private String text;

    /** Record-level keywords (SPACEA, SPACEB, SKIPA, SKIPB, FORCE, BOX, LINE, OVERLAY, etc.). */
    private List<DdsKeyword> keywords;

    /** Named field definitions. */
    private List<PrtfFieldDefinition> fields;

    /** Unnamed print constants (text labels, system keywords like DATE/TIME/PAGNBR). */
    private List<PrtfConstant> constants;

    public PrtfRecordFormat() {
        this.conditioningIndicators = new ArrayList<>();
        this.keywords = new ArrayList<>();
        this.fields = new ArrayList<>();
        this.constants = new ArrayList<>();
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

    public List<PrtfFieldDefinition> getFields() {
        return fields;
    }

    public void setFields(List<PrtfFieldDefinition> fields) {
        this.fields = fields;
    }

    public List<PrtfConstant> getConstants() {
        return constants;
    }

    public void setConstants(List<PrtfConstant> constants) {
        this.constants = constants;
    }
}
