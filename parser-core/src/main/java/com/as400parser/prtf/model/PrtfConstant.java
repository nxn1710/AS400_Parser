package com.as400parser.prtf.model;

import com.as400parser.common.model.Location;
import com.as400parser.dspf.model.ConditionedKeyword;
import com.as400parser.dspf.model.ConditioningIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an unnamed print element in a PRTF record format.
 * <p>
 * Constants are either <b>quoted literal text</b> (column headers, labels)
 * or <b>system keyword constants</b> (DATE, TIME, PAGNBR, MSGCON).
 * The {@code text} and {@code systemKeyword} fields are mutually exclusive —
 * exactly one is non-null.
 * <p>
 * Example DDS lines:
 * <pre>
 *   A                                 1 25'COMPANY REPORT'
 *   A                                 1 50DATE  EDTCDE(Y)
 *   A                                60 50PAGNBR  EDTCDE(Z)
 * </pre>
 */
public class PrtfConstant {

    /** Source position. */
    private Location location;

    /** Original source text lines (may span multiple lines for continuations). */
    private List<String> rawSourceLines;

    /** Conditioning indicators controlling output. */
    private List<ConditioningIndicator> conditioningIndicators;

    /** Print line number (cols 39-41, max 255). null if not specified. */
    private Integer printLine;

    /** Print column position (cols 42-44, max 255). null if not specified or if +n relative. */
    private Integer printPosition;

    /** Cols 42-44: raw position text (e.g., "25" or "+1"). null if blank. */
    private String printPositionRaw;

    /** Literal text content (stripped of quotes). null for system keyword constants. */
    private String text;

    /** System keyword: "DATE", "TIME", "PAGNBR", "MSGCON". null for quoted text. */
    private String systemKeyword;

    /** Keywords on the constant (e.g., EDTCDE, UNDERLINE, DATFMT). */
    private List<ConditionedKeyword> keywords;

    public PrtfConstant() {
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

    public Integer getPrintLine() {
        return printLine;
    }

    public void setPrintLine(Integer printLine) {
        this.printLine = printLine;
    }

    public Integer getPrintPosition() {
        return printPosition;
    }

    public void setPrintPosition(Integer printPosition) {
        this.printPosition = printPosition;
    }

    public String getPrintPositionRaw() {
        return printPositionRaw;
    }

    public void setPrintPositionRaw(String printPositionRaw) {
        this.printPositionRaw = printPositionRaw;
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
