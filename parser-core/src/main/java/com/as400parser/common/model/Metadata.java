package com.as400parser.common.model;

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
    private Map<String, Object> parseInfo;

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
    public Map<String, Object> getParseInfo() { return parseInfo; }
    public void setParseInfo(Map<String, Object> parseInfo) { this.parseInfo = parseInfo; }
}
