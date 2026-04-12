package com.as400parser.rpgle.model;

import com.as400parser.common.model.Location;

/**
 * RPGLE H-spec (Control/Header) specification model.
 * <p>
 * Fixed-format: col 6='H', cols 7-80 contain keywords.
 * Free-format: CTL-OPT keyword1 keyword2 ... ;
 * <p>
 * Unlike RPG3 H-specs which use fixed columns for individual options,
 * RPGLE H-specs use a keyword-based approach in the keyword area.
 */
public class ControlSpec {
    private String rawSourceLine;
    private Location location;
    private String parseQuality = "full";
    private String sourceSequence;               // cols 1-5
    private String keywords;                     // cols 7-80 (fixed) or full keyword text (free)
    private String format;                       // "fixed" or "free"
    private String inlineComment;

    // Individual recognized keywords (extracted from keyword area)
    private String dateFormat;      // DATFMT
    private String dateEdit;        // DATEDIT
    private String timeFormat;      // TIMFMT
    private String decimalEdit;     // DECEDIT
    private String debug;           // DEBUG
    private String option;          // OPTION
    private String copyright;       // COPYRIGHT
    private String altSequence;     // ALTSEQ
    private String actGroup;        // ACTGRP
    private String bndDir;          // BNDDIR
    private String dftActGrp;       // DFTACTGRP
    private String main;            // MAIN
    private String noMain;          // NOMAIN

    public String getRawSourceLine() { return rawSourceLine; }
    public void setRawSourceLine(String s) { this.rawSourceLine = s != null ? s.stripTrailing() : null; }
    public Location getLocation() { return location; }
    public void setLocation(Location l) { this.location = l; }
    public String getParseQuality() { return parseQuality; }
    public void setParseQuality(String q) { this.parseQuality = q; }
    public String getSourceSequence() { return sourceSequence; }
    public void setSourceSequence(String v) { this.sourceSequence = v; }
    public String getKeywords() { return keywords; }
    public void setKeywords(String v) { this.keywords = v; }
    public String getFormat() { return format; }
    public void setFormat(String v) { this.format = v; }
    public String getInlineComment() { return inlineComment; }
    public void setInlineComment(String v) { this.inlineComment = v; }
    public String getDateFormat() { return dateFormat; }
    public void setDateFormat(String v) { this.dateFormat = v; }
    public String getDateEdit() { return dateEdit; }
    public void setDateEdit(String v) { this.dateEdit = v; }
    public String getTimeFormat() { return timeFormat; }
    public void setTimeFormat(String v) { this.timeFormat = v; }
    public String getDecimalEdit() { return decimalEdit; }
    public void setDecimalEdit(String v) { this.decimalEdit = v; }
    public String getDebug() { return debug; }
    public void setDebug(String v) { this.debug = v; }
    public String getOption() { return option; }
    public void setOption(String v) { this.option = v; }
    public String getCopyright() { return copyright; }
    public void setCopyright(String v) { this.copyright = v; }
    public String getAltSequence() { return altSequence; }
    public void setAltSequence(String v) { this.altSequence = v; }
    public String getActGroup() { return actGroup; }
    public void setActGroup(String v) { this.actGroup = v; }
    public String getBndDir() { return bndDir; }
    public void setBndDir(String v) { this.bndDir = v; }
    public String getDftActGrp() { return dftActGrp; }
    public void setDftActGrp(String v) { this.dftActGrp = v; }
    public String getMain() { return main; }
    public void setMain(String v) { this.main = v; }
    public String getNoMain() { return noMain; }
    public void setNoMain(String v) { this.noMain = v; }
}
