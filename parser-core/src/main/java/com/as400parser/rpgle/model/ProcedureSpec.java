package com.as400parser.rpgle.model;

import com.as400parser.common.model.Location;

/**
 * RPGLE P-spec (Procedure) specification model.
 * <p>
 * Fixed-format: col 6='P'
 *   cols 7-21:  Procedure name
 *   col 24:     Begin/End (B/E)
 *   cols 44-80: Keywords (EXPORT, etc.)
 * <p>
 * Free-format: DCL-PROC name ... ; / END-PROC ;
 */
public class ProcedureSpec {
    private String rawSourceLine;
    private Location location;
    private String parseQuality = "full";
    private String sourceSequence;               // cols 1-5
    private String format;                       // "fixed" or "free"
    private String procedureName;                // cols 7-21
    private String beginEnd;                     // col 24 (B=begin, E=end)
    private String keywords;                     // cols 44-80
    private String inlineComment;

    public String getRawSourceLine() { return rawSourceLine; }
    public void setRawSourceLine(String s) { this.rawSourceLine = s != null ? s.stripTrailing() : null; }
    public Location getLocation() { return location; }
    public void setLocation(Location l) { this.location = l; }
    public String getParseQuality() { return parseQuality; }
    public void setParseQuality(String q) { this.parseQuality = q; }
    public String getSourceSequence() { return sourceSequence; }
    public void setSourceSequence(String v) { this.sourceSequence = v; }
    public String getFormat() { return format; }
    public void setFormat(String v) { this.format = v; }
    public String getProcedureName() { return procedureName; }
    public void setProcedureName(String v) { this.procedureName = v; }
    public String getBeginEnd() { return beginEnd; }
    public void setBeginEnd(String v) { this.beginEnd = v; }
    public String getKeywords() { return keywords; }
    public void setKeywords(String v) { this.keywords = v; }
    public String getInlineComment() { return inlineComment; }
    public void setInlineComment(String v) { this.inlineComment = v; }
}
