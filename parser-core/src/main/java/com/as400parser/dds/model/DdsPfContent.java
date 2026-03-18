package com.as400parser.dds.model;

import com.as400parser.common.model.ParseError;
import com.as400parser.common.model.SourceLine;

import java.util.ArrayList;
import java.util.List;

/**
 * Content model for Physical File (PF) DDS source.
 * Stored as "content" in IrDocument when sourceType = "DDS_PF".
 * PF typically has exactly 1 record format.
 */
public class DdsPfContent {

    /** All raw source lines (same structure as RPG3). */
    private List<SourceLine> sourceLines;

    /** File-level keywords: UNIQUE, REF, FIFO, LIFO, FCFO, ALTSEQ, REFACCPTH. */
    private List<DdsKeyword> fileKeywords;

    /** Record format definitions (PF typically has exactly 1). */
    private List<RecordFormat> recordFormats;

    /** Standalone comment lines. */
    private List<DdsComment> comments;

    /** Parse errors encountered during processing. */
    private List<ParseError> parseErrors;

    public DdsPfContent() {
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

    public List<RecordFormat> getRecordFormats() {
        return recordFormats;
    }

    public void setRecordFormats(List<RecordFormat> recordFormats) {
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

