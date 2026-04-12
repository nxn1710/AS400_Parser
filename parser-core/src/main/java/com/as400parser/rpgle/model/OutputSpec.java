package com.as400parser.rpgle.model;

import com.as400parser.common.model.Location;

/**
 * RPGLE O-spec (Output) specification model.
 * Same column layout as RPG3 O-spec.
 */
public class OutputSpec {
    private String rawSourceLine;
    private Location location;
    private String parseQuality = "full";
    private String sourceSequence;               // cols 1-5
    private String specLevel;                    // "recordLevel" or "fieldLevel"

    // Record-level fields
    private String fileName;                     // cols 7-14
    private String logicalRelationship;          // AND/OR
    private String type;                         // H/D/T/E
    private String addDel;                       // ADD/DEL
    private String fetchOverflow;
    private String spaceBefore;
    private String spaceAfter;
    private String skipBefore;
    private String skipAfter;
    private String outputIndicators;
    private String exceptName;

    // Field-level fields
    private String fieldName;
    private String editCode;
    private String blankAfter;
    private Integer endPosition;
    private String dataFormat;
    private String constantOrEditWord;

    // Common
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
    public String getLogicalRelationship() { return logicalRelationship; }
    public void setLogicalRelationship(String v) { this.logicalRelationship = v; }
    public String getType() { return type; }
    public void setType(String v) { this.type = v; }
    public String getAddDel() { return addDel; }
    public void setAddDel(String v) { this.addDel = v; }
    public String getFetchOverflow() { return fetchOverflow; }
    public void setFetchOverflow(String v) { this.fetchOverflow = v; }
    public String getSpaceBefore() { return spaceBefore; }
    public void setSpaceBefore(String v) { this.spaceBefore = v; }
    public String getSpaceAfter() { return spaceAfter; }
    public void setSpaceAfter(String v) { this.spaceAfter = v; }
    public String getSkipBefore() { return skipBefore; }
    public void setSkipBefore(String v) { this.skipBefore = v; }
    public String getSkipAfter() { return skipAfter; }
    public void setSkipAfter(String v) { this.skipAfter = v; }
    public String getOutputIndicators() { return outputIndicators; }
    public void setOutputIndicators(String v) { this.outputIndicators = v; }
    public String getExceptName() { return exceptName; }
    public void setExceptName(String v) { this.exceptName = v; }
    public String getFieldName() { return fieldName; }
    public void setFieldName(String v) { this.fieldName = v; }
    public String getEditCode() { return editCode; }
    public void setEditCode(String v) { this.editCode = v; }
    public String getBlankAfter() { return blankAfter; }
    public void setBlankAfter(String v) { this.blankAfter = v; }
    public Integer getEndPosition() { return endPosition; }
    public void setEndPosition(Integer v) { this.endPosition = v; }
    public String getDataFormat() { return dataFormat; }
    public void setDataFormat(String v) { this.dataFormat = v; }
    public String getConstantOrEditWord() { return constantOrEditWord; }
    public void setConstantOrEditWord(String v) { this.constantOrEditWord = v; }
    public String getInlineComment() { return inlineComment; }
    public void setInlineComment(String v) { this.inlineComment = v; }
}
