package com.as400parser.rpgle.model;

import com.as400parser.common.model.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * RPGLE F-spec (File) specification model.
 * <p>
 * Fixed-format: col 6='F'
 *   cols 7-16:  File name
 *   col 17:     File type (I/O/U/C)
 *   col 18:     File designation (P/S/R/T/F/blank)
 *   col 19:     End of file (E/blank)
 *   col 20:     File addition (A/blank)
 *   col 21:     Sequence (A/D/blank)
 *   col 22:     File format (F/E)
 *   cols 23-27: Record length
 *   col 28:     Processing mode (L/blank)
 *   cols 29-33: Key length
 *   col 34:     Record address type (A/P/G/K/D/F/T/Z)
 *   col 35:     File organization (I/T/blank)
 *   cols 36-42: Device type (DISK/PRINTER/WORKSTN/SEQ/SPECIAL)
 *   col 43:     Reserved
 *   cols 44-80: Keywords
 * <p>
 * Free-format: DCL-F filename keyword1 keyword2 ... ;
 */
public class FileSpec {
    private String rawSourceLine;
    private Location location;
    private String parseQuality = "full";
    private String sourceSequence;               // cols 1-5
    private String format;                       // "fixed" or "free"
    private String fileName;                     // cols 7-16
    private String fileType;                     // col 17 (I/O/U/C)
    private String fileDesignation;              // col 18 (P/S/R/T/F)
    private String endOfFile;                    // col 19 (E)
    private String fileAddition;                 // col 20 (A)
    private String sequence;                     // col 21 (A/D)
    private String fileFormat;                   // col 22 (F/E)
    private Integer recordLength;                // cols 23-27
    private String processingMode;               // col 28 (L)
    private Integer keyLength;                   // cols 29-33
    private String recordAddressType;            // col 34
    private String fileOrganization;             // col 35 (I/T)
    private String device;                       // cols 36-42
    private String keywords;                     // cols 44-80 (fixed) or all keywords (free)
    private String inlineComment;
    private List<FileKeyword> parsedKeywords = new ArrayList<>();

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
    public Integer getRecordLength() { return recordLength; }
    public void setRecordLength(Integer v) { this.recordLength = v; }
    public String getProcessingMode() { return processingMode; }
    public void setProcessingMode(String v) { this.processingMode = v; }
    public Integer getKeyLength() { return keyLength; }
    public void setKeyLength(Integer v) { this.keyLength = v; }
    public String getRecordAddressType() { return recordAddressType; }
    public void setRecordAddressType(String v) { this.recordAddressType = v; }
    public String getFileOrganization() { return fileOrganization; }
    public void setFileOrganization(String v) { this.fileOrganization = v; }
    public String getDevice() { return device; }
    public void setDevice(String v) { this.device = v; }
    public String getKeywords() { return keywords; }
    public void setKeywords(String v) { this.keywords = v; }
    public String getInlineComment() { return inlineComment; }
    public void setInlineComment(String v) { this.inlineComment = v; }
    public List<FileKeyword> getParsedKeywords() { return parsedKeywords; }
    public void setParsedKeywords(List<FileKeyword> v) { this.parsedKeywords = v; }

    /** Parsed keyword from F-spec keyword area */
    public static class FileKeyword {
        private String keyword;
        private String value;
        private String rawText;

        public FileKeyword() {}
        public FileKeyword(String keyword, String value, String rawText) {
            this.keyword = keyword;
            this.value = value;
            this.rawText = rawText;
        }

        public String getKeyword() { return keyword; }
        public void setKeyword(String v) { this.keyword = v; }
        public String getValue() { return value; }
        public void setValue(String v) { this.value = v; }
        public String getRawText() { return rawText; }
        public void setRawText(String v) { this.rawText = v; }
    }
}
