package com.as400parser.rpgle.model;

import com.as400parser.common.model.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a free-format RPGLE statement parsed by ANTLR.
 * <p>
 * Used for free-format operations like EVAL, IF, DOW, CALLP, etc.
 * that don't map directly to fixed-format column extraction.
 */
public class FreeFormatStatement {
    private String nodeType;       // "eval", "if", "dow", "dou", "for", "select", "monitor", "callp", etc.
    private String rawSourceLine;
    private Location location;
    private String parseQuality = "full";
    private String operation;      // The operation keyword
    private String expression;     // The full expression text
    private String extender;       // Operation extender (H, N, P, etc.)
    private List<Object> children = new ArrayList<>();  // nested statements for blocks
    private String inlineComment;

    public FreeFormatStatement() {}

    public FreeFormatStatement(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getNodeType() { return nodeType; }
    public void setNodeType(String v) { this.nodeType = v; }
    public String getRawSourceLine() { return rawSourceLine; }
    public void setRawSourceLine(String s) { this.rawSourceLine = s != null ? s.stripTrailing() : null; }
    public Location getLocation() { return location; }
    public void setLocation(Location l) { this.location = l; }
    public String getParseQuality() { return parseQuality; }
    public void setParseQuality(String q) { this.parseQuality = q; }
    public String getOperation() { return operation; }
    public void setOperation(String v) { this.operation = v; }
    public String getExpression() { return expression; }
    public void setExpression(String v) { this.expression = v; }
    public String getExtender() { return extender; }
    public void setExtender(String v) { this.extender = v; }
    public List<Object> getChildren() { return children; }
    public void setChildren(List<Object> v) { this.children = v; }
    public String getInlineComment() { return inlineComment; }
    public void setInlineComment(String v) { this.inlineComment = v; }
}
