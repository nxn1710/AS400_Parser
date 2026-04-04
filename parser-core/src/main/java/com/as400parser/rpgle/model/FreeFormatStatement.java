package com.as400parser.rpgle.model;

import com.as400parser.common.model.Location;

/**
 * Free-format statement — parsed via ANTLR grammar.
 * Represents a single free-format operation or declaration.
 */
public class FreeFormatStatement {
    private Location location;
    private String rawSourceLine;
    private String statementType; // e.g., "dcl-s", "dcl-ds", "dcl-f", "dcl-pr", "eval", "if", "dou", etc.
    private String text;          // the full parsed statement text

    public Location getLocation() { return location; }
    public void setLocation(Location v) { this.location = v; }
    public String getRawSourceLine() { return rawSourceLine; }
    public void setRawSourceLine(String v) { this.rawSourceLine = v; }
    public String getStatementType() { return statementType; }
    public void setStatementType(String v) { this.statementType = v; }
    public String getText() { return text; }
    public void setText(String v) { this.text = v; }
}
