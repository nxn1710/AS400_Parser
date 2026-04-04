package com.as400parser.rpgle.model;

import com.as400parser.common.model.Location;

/**
 * Control Specification (H-spec).
 * Columns 7-80: Keywords.
 */
public class ControlSpec {
    private Location location;
    private String rawSourceLine;
    private String keywords;  // columns 7-80

    public Location getLocation() { return location; }
    public void setLocation(Location v) { this.location = v; }
    public String getRawSourceLine() { return rawSourceLine; }
    public void setRawSourceLine(String v) { this.rawSourceLine = v; }
    public String getKeywords() { return keywords; }
    public void setKeywords(String v) { this.keywords = v; }
}
