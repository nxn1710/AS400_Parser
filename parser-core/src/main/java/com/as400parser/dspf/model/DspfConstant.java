package com.as400parser.dspf.model;

import com.as400parser.common.model.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an unnamed display element on the DSPF screen.
 * <p>
 * Constants are either <b>quoted literal text</b> (screen labels/headings)
 * or <b>system keyword constants</b> (DATE, TIME, SYSNAME, USER, PAGNBR).
 * The {@code text} and {@code systemKeyword} fields are mutually exclusive —
 * exactly one is non-null.
 * <p>
 * Example DDS lines:
 * <pre>
 *   A                                      1 25'学 生 管 理 シ ス テ ム'
 *   A                                      2 70DATE
 * </pre>
 */
public class DspfConstant {

    /** Source position. */
    private Location location;

    /** Original source text lines (may span multiple lines for continuations). */
    private List<String> rawSourceLines;

    /** Conditioning indicators controlling visibility. */
    private List<ConditioningIndicator> conditioningIndicators;

    /** Screen line number (cols 39-41). null if not specified. */
    private Integer screenLine;

    /** Screen column position (cols 42-44). null if not specified or if +n relative. */
    private Integer screenPosition;

    /** Cols 42-44: raw position text (e.g., "25" or "+1"). null if blank. */
    private String screenPositionRaw;

    /** Literal text content (stripped of quotes). null for system keyword constants. */
    private String text;

    /** System keyword name: "DATE", "TIME", "SYSNAME", "USER", "PAGNBR". null for quoted text. */
    private String systemKeyword;

    /** Keywords on the constant (e.g., DSPATR, COLOR, EDTCDE). */
    private List<ConditionedKeyword> keywords;

    public DspfConstant() {
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

    public String getScreenPositionRaw() {
        return screenPositionRaw;
    }

    public void setScreenPositionRaw(String screenPositionRaw) {
        this.screenPositionRaw = screenPositionRaw;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSystemKeyword() {
        return systemKeyword;
    }

    public void setSystemKeyword(String systemKeyword) {
        this.systemKeyword = systemKeyword;
    }

    public List<ConditionedKeyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<ConditionedKeyword> keywords) {
        this.keywords = keywords;
    }
}
