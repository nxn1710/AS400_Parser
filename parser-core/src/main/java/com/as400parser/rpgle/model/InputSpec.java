package com.as400parser.rpgle.model;

import com.as400parser.common.model.Location;

/**
 * Input Specification (I-spec).
 * Positions per ILE RPG Reference.
 */
public class InputSpec {
    private Location location;
    private String rawSourceLine;
    private String specLevel; // "recordIdentification" or "fieldDefinition" (aligned with RPG3)

    // Record identification entries
    private String fileName;           // 7-16
    private String logicalRelationship;// 16-18
    private String sequenceNumber;     // 17-18
    private String number;             // 19
    private String option;             // 20
    private String recordIdIndicator;  // 21-22
    private String recordIdCodes;      // 23-46

    // Field description entries
    private String dataAttributes;     // 31-34
    private String dateTimeSeparator;  // 35
    private String dataFormat;         // 36
    private String fieldLocation;      // 37-46
    private String decimalPositions;   // 47-48
    private String fieldName;          // 49-62
    private String controlLevel;       // 63-64
    private String matchingFields;     // 65-66
    private String fieldRecordRelation;// 67-68
    private String fieldIndicators;    // 69-74

    public Location getLocation() { return location; }
    public void setLocation(Location v) { this.location = v; }
    public String getRawSourceLine() { return rawSourceLine; }
    public void setRawSourceLine(String v) { this.rawSourceLine = v; }
    public String getSpecLevel() { return specLevel; }
    public void setSpecLevel(String v) { this.specLevel = v; }

    public String getFileName() { return fileName; }
    public void setFileName(String v) { this.fileName = v; }
    public String getLogicalRelationship() { return logicalRelationship; }
    public void setLogicalRelationship(String v) { this.logicalRelationship = v; }
    public String getSequenceNumber() { return sequenceNumber; }
    public void setSequenceNumber(String v) { this.sequenceNumber = v; }
    public String getNumber() { return number; }
    public void setNumber(String v) { this.number = v; }
    public String getOption() { return option; }
    public void setOption(String v) { this.option = v; }
    public String getRecordIdIndicator() { return recordIdIndicator; }
    public void setRecordIdIndicator(String v) { this.recordIdIndicator = v; }
    public String getRecordIdCodes() { return recordIdCodes; }
    public void setRecordIdCodes(String v) { this.recordIdCodes = v; }

    public String getDataAttributes() { return dataAttributes; }
    public void setDataAttributes(String v) { this.dataAttributes = v; }
    public String getDateTimeSeparator() { return dateTimeSeparator; }
    public void setDateTimeSeparator(String v) { this.dateTimeSeparator = v; }
    public String getDataFormat() { return dataFormat; }
    public void setDataFormat(String v) { this.dataFormat = v; }
    public String getFieldLocation() { return fieldLocation; }
    public void setFieldLocation(String v) { this.fieldLocation = v; }
    public String getDecimalPositions() { return decimalPositions; }
    public void setDecimalPositions(String v) { this.decimalPositions = v; }
    public String getFieldName() { return fieldName; }
    public void setFieldName(String v) { this.fieldName = v; }
    public String getControlLevel() { return controlLevel; }
    public void setControlLevel(String v) { this.controlLevel = v; }
    public String getMatchingFields() { return matchingFields; }
    public void setMatchingFields(String v) { this.matchingFields = v; }
    public String getFieldRecordRelation() { return fieldRecordRelation; }
    public void setFieldRecordRelation(String v) { this.fieldRecordRelation = v; }
    public String getFieldIndicators() { return fieldIndicators; }
    public void setFieldIndicators(String v) { this.fieldIndicators = v; }
}
