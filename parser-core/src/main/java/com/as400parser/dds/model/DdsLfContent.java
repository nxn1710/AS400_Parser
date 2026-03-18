package com.as400parser.dds.model;

import com.as400parser.common.model.ParseError;
import com.as400parser.common.model.SourceLine;

import java.util.ArrayList;
import java.util.List;

/**
 * Content model for Logical File (LF) DDS source.
 * Stored as "content" in IrDocument when sourceType = "DDS_LF".
 * LF may have multiple record formats (multi-format LF).
 */
public class DdsLfContent {

    /** All raw source lines (same structure as RPG3). */
    private List<SourceLine> sourceLines;

    /** File-level keywords: UNIQUE, FIFO, LIFO, FCFO, ALTSEQ, DYNSLT. */
    private List<DdsKeyword> fileKeywords;

    /** LF record format definitions (may have multiple for multi-format LF). */
    private List<LfRecordFormat> recordFormats;

    /** Standalone comment lines. */
    private List<DdsComment> comments;

    /** Parse errors encountered during processing. */
    private List<ParseError> parseErrors;

    public DdsLfContent() {
        this.sourceLines = new ArrayList<>();
        this.fileKeywords = new ArrayList<>();
        this.recordFormats = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.parseErrors = new ArrayList<>();
    }

    // --- Getters & Setters ---

    public List<SourceLine> getSourceLines() {
        return sourceLines;
    }

    public void setSourceLines(List<SourceLine> sourceLines) {
        this.sourceLines = sourceLines;
    }

    public List<DdsKeyword> getFileKeywords() {
        return fileKeywords;
    }

    public void setFileKeywords(List<DdsKeyword> fileKeywords) {
        this.fileKeywords = fileKeywords;
    }

    public List<LfRecordFormat> getRecordFormats() {
        return recordFormats;
    }

    public void setRecordFormats(List<LfRecordFormat> recordFormats) {
        this.recordFormats = recordFormats;
    }

    public List<DdsComment> getComments() {
        return comments;
    }

    public void setComments(List<DdsComment> comments) {
        this.comments = comments;
    }

    public List<ParseError> getParseErrors() {
        return parseErrors;
    }

    public void setParseErrors(List<ParseError> parseErrors) {
        this.parseErrors = parseErrors;
    }
}

