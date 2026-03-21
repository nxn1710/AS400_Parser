package com.as400parser.common.model;

/**
 * Represents a single source line in the IR output.
 */
public class SourceLine {
    private int lineNumber;
    private String rawText;
    private String specType;
    private String sequenceNumber;
    private boolean isComment;
    private boolean isBlank;

    public SourceLine() {}

    public SourceLine(int lineNumber, String rawText, String specType,
                      String sequenceNumber, boolean isComment, boolean isBlank) {
        this.lineNumber = lineNumber;
        this.rawText = rawText != null ? rawText.stripTrailing() : null;
        this.specType = specType;
        this.sequenceNumber = sequenceNumber;
        this.isComment = isComment;
        this.isBlank = isBlank;
    }

    public int getLineNumber() { return lineNumber; }
    public void setLineNumber(int lineNumber) { this.lineNumber = lineNumber; }
    public String getRawText() { return rawText; }
    public void setRawText(String rawText) { this.rawText = rawText != null ? rawText.stripTrailing() : null; }
    public String getSpecType() { return specType; }
    public void setSpecType(String specType) { this.specType = specType; }
    public String getSequenceNumber() { return sequenceNumber; }
    public void setSequenceNumber(String sequenceNumber) { this.sequenceNumber = sequenceNumber; }
    public boolean isComment() { return isComment; }
    public void setComment(boolean comment) { isComment = comment; }
    public boolean isBlank() { return isBlank; }
    public void setBlank(boolean blank) { isBlank = blank; }
}
