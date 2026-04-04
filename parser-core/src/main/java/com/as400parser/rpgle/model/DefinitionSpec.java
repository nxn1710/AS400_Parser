package com.as400parser.rpgle.model;

import com.as400parser.common.model.Location;

/**
 * Definition Specification (D-spec).
 * Positions per ILE RPG Reference.
 */
public class DefinitionSpec {
    private Location location;
    private String rawSourceLine;

    private String name;              // 7-21
    private String externalDescription; // 22
    private String dsType;            // 23
    private String definitionType;    // 24-25
    private String fromPosition;      // 26-32
    private String toPositionLength;  // 33-39
    private String internalDataType;  // 40
    private String decimalPositions;  // 41-42
    private String keywords;          // 44-80

    public Location getLocation() { return location; }
    public void setLocation(Location v) { this.location = v; }
    public String getRawSourceLine() { return rawSourceLine; }
    public void setRawSourceLine(String v) { this.rawSourceLine = v; }

    public String getName() { return name; }
    public void setName(String v) { this.name = v; }
    public String getExternalDescription() { return externalDescription; }
    public void setExternalDescription(String v) { this.externalDescription = v; }
    public String getDsType() { return dsType; }
    public void setDsType(String v) { this.dsType = v; }
    public String getDefinitionType() { return definitionType; }
    public void setDefinitionType(String v) { this.definitionType = v; }
    public String getFromPosition() { return fromPosition; }
    public void setFromPosition(String v) { this.fromPosition = v; }
    public String getToPositionLength() { return toPositionLength; }
    public void setToPositionLength(String v) { this.toPositionLength = v; }
    public String getInternalDataType() { return internalDataType; }
    public void setInternalDataType(String v) { this.internalDataType = v; }
    public String getDecimalPositions() { return decimalPositions; }
    public void setDecimalPositions(String v) { this.decimalPositions = v; }
    public String getKeywords() { return keywords; }
    public void setKeywords(String v) { this.keywords = v; }
}
