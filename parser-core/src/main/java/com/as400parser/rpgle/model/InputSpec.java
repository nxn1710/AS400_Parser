package com.as400parser.rpgle.model;

import com.as400parser.common.model.Location;

/**
 * RPGLE I-spec (Input) specification model.
 * Same column layout as RPG3 I-spec.
 */
public class InputSpec {
    private String rawSourceLine;
    private Location location;
    private String parseQuality = "full";
    private String sourceSequence;               // cols 1-5
    private String specLevel;                    // "recordIdentification" or "fieldDefinition"
    private String fileName;
    private String recordName;
    private String sequenceNumber;
    private String option;
    private String recordIdIndicator;
    // Field definition fields
    private String fieldName;
    private String externalFieldName;
    private Integer fromPosition;
    private Integer toPosition;
    private Integer decimalPositions;
    private String dataFormat;
    private String controlLevel;
    private String matchingFields;
    private String fieldRecordRelation;
    private String plusIndicator;
    private String minusIndicator;
    private String zeroBlankIndicator;
    private String fieldIndicators;
    private String inlineComment;

    public String getRawSourceLine() { return rawSourceLine; }
    public void setRawSourceLine(String s) { this.rawSourceLine = s != null ? s.stripTrailing() : null; }
    public Location getLocation() { return location; }
    public void setLocation(Location l) { this.location = l; }
    public String getParseQuality() { return parseQuality; }
    public void setParseQuality(String q) { this.parseQuality = q; }
    public String getSourceSequence() { return sourceSequence; }
    public void setSourceSequence(String v) { this.sourceSequence = v; }
    public String getSpecLevel() { return specLevel; }
    public void setSpecLevel(String v) { this.specLevel = v; }
    public String getFileName() { return fileName; }
    public void setFileName(String v) { this.fileName = v; }
    public String getRecordName() { return recordName; }
    public void setRecordName(String v) { this.recordName = v; }
    public String getSequenceNumber() { return sequenceNumber; }
    public void setSequenceNumber(String v) { this.sequenceNumber = v; }
    public String getOption() { return option; }
    public void setOption(String v) { this.option = v; }
    public String getRecordIdIndicator() { return recordIdIndicator; }
    public void setRecordIdIndicator(String v) { this.recordIdIndicator = v; }
    public String getFieldName() { return fieldName; }
    public void setFieldName(String v) { this.fieldName = v; }
    public String getExternalFieldName() { return externalFieldName; }
    public void setExternalFieldName(String v) { this.externalFieldName = v; }
    public Integer getFromPosition() { return fromPosition; }
    public void setFromPosition(Integer v) { this.fromPosition = v; }
    public Integer getToPosition() { return toPosition; }
    public void setToPosition(Integer v) { this.toPosition = v; }
    public Integer getDecimalPositions() { return decimalPositions; }
    public void setDecimalPositions(Integer v) { this.decimalPositions = v; }
    public String getDataFormat() { return dataFormat; }
    public void setDataFormat(String v) { this.dataFormat = v; }
    public String getControlLevel() { return controlLevel; }
    public void setControlLevel(String v) { this.controlLevel = v; }
    public String getMatchingFields() { return matchingFields; }
    public void setMatchingFields(String v) { this.matchingFields = v; }
    public String getFieldRecordRelation() { return fieldRecordRelation; }
    public void setFieldRecordRelation(String v) { this.fieldRecordRelation = v; }
    public String getPlusIndicator() { return plusIndicator; }
    public void setPlusIndicator(String v) { this.plusIndicator = v; }
    public String getMinusIndicator() { return minusIndicator; }
    public void setMinusIndicator(String v) { this.minusIndicator = v; }
    public String getZeroBlankIndicator() { return zeroBlankIndicator; }
    public void setZeroBlankIndicator(String v) { this.zeroBlankIndicator = v; }
    public String getFieldIndicators() { return fieldIndicators; }
    public void setFieldIndicators(String v) { this.fieldIndicators = v; }
    public String getInlineComment() { return inlineComment; }
    public void setInlineComment(String v) { this.inlineComment = v; }
}
