package com.as400parser.rpgle.model;

import com.as400parser.common.model.Location;

/**
 * File Description Specification (F-spec).
 * Positions per ILE RPG Reference.
 */
public class FileSpec {
    private Location location;
    private String rawSourceLine;

    private String fileName;         // 7-16
    private String fileType;         // 17 (I, O, U, C)
    private String fileDesignation;  // 18
    private String endOfFile;        // 19
    private String fileAddition;     // 20
    private String sequence;         // 21
    private String fileFormat;       // 22
    private String recordLength;     // 23-27
    private String limitsProcessing; // 28
    private String keyLength;        // 29-33
    private String recordAddressType;// 34
    private String fileOrganization; // 35
    private String device;           // 36-42
    private String reserved;         // 43
    private String keywords;         // 44-80

    public Location getLocation() { return location; }
    public void setLocation(Location v) { this.location = v; }
    public String getRawSourceLine() { return rawSourceLine; }
    public void setRawSourceLine(String v) { this.rawSourceLine = v; }

    public String getFileName() { return fileName; }
    public void setFileName(String v) { this.fileName = v; }
    public String getFileType() { return fileType; }
    public void setFileType(String v) { this.fileType = v; }
    public String getFileDesignation() { return fileDesignation; }
    public void setFileDesignation(String v) { this.fileDesignation = v; }
    public String getEndOfFile() { return endOfFile; }
    public void setEndOfFile(String v) { this.endOfFile = v; }
    public String getFileAddition() { return fileAddition; }
    public void setFileAddition(String v) { this.fileAddition = v; }
    public String getSequence() { return sequence; }
    public void setSequence(String v) { this.sequence = v; }
    public String getFileFormat() { return fileFormat; }
    public void setFileFormat(String v) { this.fileFormat = v; }
    public String getRecordLength() { return recordLength; }
    public void setRecordLength(String v) { this.recordLength = v; }
    public String getLimitsProcessing() { return limitsProcessing; }
    public void setLimitsProcessing(String v) { this.limitsProcessing = v; }
    public String getKeyLength() { return keyLength; }
    public void setKeyLength(String v) { this.keyLength = v; }
    public String getRecordAddressType() { return recordAddressType; }
    public void setRecordAddressType(String v) { this.recordAddressType = v; }
    public String getFileOrganization() { return fileOrganization; }
    public void setFileOrganization(String v) { this.fileOrganization = v; }
    public String getDevice() { return device; }
    public void setDevice(String v) { this.device = v; }
    public String getReserved() { return reserved; }
    public void setReserved(String v) { this.reserved = v; }
    public String getKeywords() { return keywords; }
    public void setKeywords(String v) { this.keywords = v; }
}
