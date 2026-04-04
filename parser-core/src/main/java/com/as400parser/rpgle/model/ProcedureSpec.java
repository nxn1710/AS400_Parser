package com.as400parser.rpgle.model;

import com.as400parser.common.model.Location;

/**
 * Procedure Specification (P-spec).
 * Positions per ILE RPG Reference.
 */
public class ProcedureSpec {
    private Location location;
    private String rawSourceLine;

    private String name;          // 7-21
    private String beginEnd;      // 24 (B or E)
    private String keywords;      // 44-80

    public Location getLocation() { return location; }
    public void setLocation(Location v) { this.location = v; }
    public String getRawSourceLine() { return rawSourceLine; }
    public void setRawSourceLine(String v) { this.rawSourceLine = v; }

    public String getName() { return name; }
    public void setName(String v) { this.name = v; }
    public String getBeginEnd() { return beginEnd; }
    public void setBeginEnd(String v) { this.beginEnd = v; }
    public String getKeywords() { return keywords; }
    public void setKeywords(String v) { this.keywords = v; }
}
