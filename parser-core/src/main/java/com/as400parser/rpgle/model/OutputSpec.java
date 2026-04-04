package com.as400parser.rpgle.model;

import com.as400parser.common.model.Location;

/**
 * Output Specification (O-spec).
 * Positions per ILE RPG Reference.
 */
public class OutputSpec {
    private Location location;
    private String rawSourceLine;
    private String specSubType; // "record" or "field"

    // Record identification and control entries
    private String fileName;             // 7-16
    private String type;                 // 17
    private String recordAddDel;         // 18-20
    private String conditioningIndicators; // 21-29
    private String exceptName;           // 30-39
    private String spaceSkip;            // 40-51

    // Field description entries
    private String fieldName;            // 30-43
    private String editCode;             // 44
    private String blankAfter;           // 45
    private String endPosition;          // 47-51
    private String dataFormat;           // 52
    private String constantEditWord;     // 53-80

    public Location getLocation() { return location; }
    public void setLocation(Location v) { this.location = v; }
    public String getRawSourceLine() { return rawSourceLine; }
    public void setRawSourceLine(String v) { this.rawSourceLine = v; }
    public String getSpecSubType() { return specSubType; }
    public void setSpecSubType(String v) { this.specSubType = v; }

    public String getFileName() { return fileName; }
    public void setFileName(String v) { this.fileName = v; }
    public String getType() { return type; }
    public void setType(String v) { this.type = v; }
    public String getRecordAddDel() { return recordAddDel; }
    public void setRecordAddDel(String v) { this.recordAddDel = v; }
    public String getConditioningIndicators() { return conditioningIndicators; }
    public void setConditioningIndicators(String v) { this.conditioningIndicators = v; }
    public String getExceptName() { return exceptName; }
    public void setExceptName(String v) { this.exceptName = v; }
    public String getSpaceSkip() { return spaceSkip; }
    public void setSpaceSkip(String v) { this.spaceSkip = v; }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String v) { this.fieldName = v; }
    public String getEditCode() { return editCode; }
    public void setEditCode(String v) { this.editCode = v; }
    public String getBlankAfter() { return blankAfter; }
    public void setBlankAfter(String v) { this.blankAfter = v; }
    public String getEndPosition() { return endPosition; }
    public void setEndPosition(String v) { this.endPosition = v; }
    public String getDataFormat() { return dataFormat; }
    public void setDataFormat(String v) { this.dataFormat = v; }
    public String getConstantEditWord() { return constantEditWord; }
    public void setConstantEditWord(String v) { this.constantEditWord = v; }
}
