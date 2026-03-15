package com.as400parser.common.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Metadata about the parsed source and parsing process.
 */
public class Metadata {
    private String irVersion;
    private String sourceType;
    private String sourceMember;
    private String sourceFile;
    private String sourceLibrary;
    private String description;
    private Integer recordLength;
    private ParseInfo parseInfo;

    public Metadata() {}

    public String getIrVersion() { return irVersion; }
    public void setIrVersion(String irVersion) { this.irVersion = irVersion; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public String getSourceMember() { return sourceMember; }
    public void setSourceMember(String sourceMember) { this.sourceMember = sourceMember; }
    public String getSourceFile() { return sourceFile; }
    public void setSourceFile(String sourceFile) { this.sourceFile = sourceFile; }
    public String getSourceLibrary() { return sourceLibrary; }
    public void setSourceLibrary(String sourceLibrary) { this.sourceLibrary = sourceLibrary; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getRecordLength() { return recordLength; }
    public void setRecordLength(Integer recordLength) { this.recordLength = recordLength; }
    public ParseInfo getParseInfo() { return parseInfo; }
    public void setParseInfo(ParseInfo parseInfo) { this.parseInfo = parseInfo; }

    /**
     * Typed parseInfo object per IR design spec.
     */
    public static class ParseInfo {
        private String parserVersion;
        private String parsedAt;        // ISO 8601 timestamp
        private String parseStatus;     // "complete", "partial", "failed"
        private int totalLines;
        private List<Map<String, Object>> errors = new ArrayList<>();
        private List<Map<String, Object>> warnings = new ArrayList<>();

        public String getParserVersion() { return parserVersion; }
        public void setParserVersion(String v) { this.parserVersion = v; }
        public String getParsedAt() { return parsedAt; }
        public void setParsedAt(String v) { this.parsedAt = v; }
        public String getParseStatus() { return parseStatus; }
        public void setParseStatus(String v) { this.parseStatus = v; }
        public int getTotalLines() { return totalLines; }
        public void setTotalLines(int v) { this.totalLines = v; }
        public List<Map<String, Object>> getErrors() { return errors; }
        public void setErrors(List<Map<String, Object>> v) { this.errors = v; }
        public List<Map<String, Object>> getWarnings() { return warnings; }
        public void setWarnings(List<Map<String, Object>> v) { this.warnings = v; }
    }
}
