package com.as400parser.prtf.model;

import com.as400parser.common.model.ParseError;
import com.as400parser.common.model.SourceLine;
import com.as400parser.dds.model.DdsComment;
import com.as400parser.dds.model.DdsKeyword;

import java.util.ArrayList;
import java.util.List;

/**
 * Content model for Printer File (PRTF) DDS source.
 * <p>
 * Stored as "content" in {@link com.as400parser.common.model.IrDocument}
 * when sourceType = "PRTF". Follows the same top-level structure as
 * {@link com.as400parser.dspf.model.DspfContent} for JSON consistency.
 */
public class PrtfContent {

    /** All raw source lines (same SourceLine structure as PF/LF, DSPF, RPG3). */
    private List<SourceLine> sourceLines;

    /** File-level keywords: REF, INDARA, INDTXT, CHRID, SKIPA/SKIPB (file-level), DFNCHR, etc. */
    private List<DdsKeyword> fileKeywords;

    /** Record format definitions. */
    private List<PrtfRecordFormat> recordFormats;

    /** Standalone comment lines. */
    private List<DdsComment> comments;

    /** Parse errors encountered during processing. */
    private List<ParseError> parseErrors;

    public PrtfContent() {
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

    public List<PrtfRecordFormat> getRecordFormats() {
        return recordFormats;
    }

    public void setRecordFormats(List<PrtfRecordFormat> recordFormats) {
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
