package com.as400parser.rpgle.model;

import com.as400parser.common.model.Location;

/**
 * RPGLE D-spec (Definition) specification model.
 * <p>
 * Fixed-format: col 6='D'
 *   cols 7-21:  Name (up to 15 chars)
 *   col 22:     Externally described (E/blank)
 *   col 23:     Data area (U/blank)
 *   cols 24-25: Definition type (DS/S/C/PR/PI/blank = subfield)
 *   cols 26-32: From position (DS subfields only)
 *   cols 33-39: To position / length
 *   col 40:     Data type (A/B/C/D/F/G/I/N/O/P/S/T/U/Z/*)
 *   cols 41-42: Decimal positions
 *   col 43:     Reserved
 *   cols 44-80: Keywords
 * <p>
 * Free-format: DCL-S, DCL-DS, DCL-C, DCL-PR, DCL-PI
 */
public class DefinitionSpec {
    private String rawSourceLine;
    private Location location;
    private String parseQuality = "full";
    private String sourceSequence;               // cols 1-5
    private String format;                       // "fixed" or "free"

    // D-spec identification
    private String name;                         // cols 7-21
    private String externallyDescribed;          // col 22 (E/blank)
    private String dataAreaType;                 // col 23 (U/blank)
    private String definitionType;               // cols 24-25 (DS/S/C/PR/PI/blank)

    // Position and type
    private String fromPosition;                 // cols 26-32
    private String toPosition;                   // cols 33-39
    private String dataType;                     // col 40
    private Integer decimalPositions;            // cols 41-42

    // Keywords
    private String keywords;                     // cols 44-80 (raw text)
    private String inlineComment;

    // Parsed keyword values
    private String inzValue;       // INZ(value)
    private String likeField;      // LIKE(field)
    private String dimValue;       // DIM(n)
    private String extName;        // EXTNAME(file)
    private String constValue;     // constant value for C type

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
    public String getName() { return name; }
    public void setName(String v) { this.name = v; }
    public String getExternallyDescribed() { return externallyDescribed; }
    public void setExternallyDescribed(String v) { this.externallyDescribed = v; }
    public String getDataAreaType() { return dataAreaType; }
    public void setDataAreaType(String v) { this.dataAreaType = v; }
    public String getDefinitionType() { return definitionType; }
    public void setDefinitionType(String v) { this.definitionType = v; }
    public String getFromPosition() { return fromPosition; }
    public void setFromPosition(String v) { this.fromPosition = v; }
    public String getToPosition() { return toPosition; }
    public void setToPosition(String v) { this.toPosition = v; }
    public String getDataType() { return dataType; }
    public void setDataType(String v) { this.dataType = v; }
    public Integer getDecimalPositions() { return decimalPositions; }
    public void setDecimalPositions(Integer v) { this.decimalPositions = v; }
    public String getKeywords() { return keywords; }
    public void setKeywords(String v) { this.keywords = v; }
    public String getInlineComment() { return inlineComment; }
    public void setInlineComment(String v) { this.inlineComment = v; }
    public String getInzValue() { return inzValue; }
    public void setInzValue(String v) { this.inzValue = v; }
    public String getLikeField() { return likeField; }
    public void setLikeField(String v) { this.likeField = v; }
    public String getDimValue() { return dimValue; }
    public void setDimValue(String v) { this.dimValue = v; }
    public String getExtName() { return extName; }
    public void setExtName(String v) { this.extName = v; }
    public String getConstValue() { return constValue; }
    public void setConstValue(String v) { this.constValue = v; }
}
