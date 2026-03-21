package com.as400parser.rpg3.model;

import com.as400parser.common.model.Location;
import com.as400parser.common.model.SourceLine;
import com.as400parser.rpg3.model.CalcSpec.SubroutineBlock;

import java.util.ArrayList;
import java.util.List;

/**
 * RPG3-specific content section of the IR document.
 * Contains all 7 spec type arrays plus supporting structures.
 */
public class Rpg3Content {
    private List<HeaderSpec> headerSpecs = new ArrayList<>();
    private List<FileSpec> fileSpecs = new ArrayList<>();
    private List<ExtensionSpec> extensionSpecs = new ArrayList<>();
    private List<LineCounterSpec> lineCounterSpecs = new ArrayList<>();
    private List<InputSpec> inputSpecs = new ArrayList<>();
    private List<Object> calculationSpecs = new ArrayList<>();  // CalcSpec or control flow blocks
    private List<OutputSpec> outputSpecs = new ArrayList<>();
    private List<DataStructure> dataStructures = new ArrayList<>();
    private List<SymbolEntry> symbolTable = new ArrayList<>();
    private List<SubroutineBlock> subroutines = new ArrayList<>();
    private CompileTimeData compileTimeData;
    private List<Comment> comments = new ArrayList<>();
    private List<SourceLine> sourceLines = new ArrayList<>();

    // Getters and setters
    public List<HeaderSpec> getHeaderSpecs() { return headerSpecs; }
    public void setHeaderSpecs(List<HeaderSpec> headerSpecs) { this.headerSpecs = headerSpecs; }
    public List<FileSpec> getFileSpecs() { return fileSpecs; }
    public void setFileSpecs(List<FileSpec> fileSpecs) { this.fileSpecs = fileSpecs; }
    public List<ExtensionSpec> getExtensionSpecs() { return extensionSpecs; }
    public void setExtensionSpecs(List<ExtensionSpec> extensionSpecs) { this.extensionSpecs = extensionSpecs; }
    public List<LineCounterSpec> getLineCounterSpecs() { return lineCounterSpecs; }
    public void setLineCounterSpecs(List<LineCounterSpec> lineCounterSpecs) { this.lineCounterSpecs = lineCounterSpecs; }
    public List<InputSpec> getInputSpecs() { return inputSpecs; }
    public void setInputSpecs(List<InputSpec> inputSpecs) { this.inputSpecs = inputSpecs; }
    public List<Object> getCalculationSpecs() { return calculationSpecs; }
    public void setCalculationSpecs(List<Object> calculationSpecs) { this.calculationSpecs = calculationSpecs; }
    public List<OutputSpec> getOutputSpecs() { return outputSpecs; }
    public void setOutputSpecs(List<OutputSpec> outputSpecs) { this.outputSpecs = outputSpecs; }
    public List<DataStructure> getDataStructures() { return dataStructures; }
    public void setDataStructures(List<DataStructure> dataStructures) { this.dataStructures = dataStructures; }
    public List<SymbolEntry> getSymbolTable() { return symbolTable; }
    public void setSymbolTable(List<SymbolEntry> symbolTable) { this.symbolTable = symbolTable; }
    public List<SubroutineBlock> getSubroutines() { return subroutines; }
    public void setSubroutines(List<SubroutineBlock> subroutines) { this.subroutines = subroutines; }
    public CompileTimeData getCompileTimeData() { return compileTimeData; }
    public void setCompileTimeData(CompileTimeData compileTimeData) { this.compileTimeData = compileTimeData; }
    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
    public List<SourceLine> getSourceLines() { return sourceLines; }
    public void setSourceLines(List<SourceLine> sourceLines) { this.sourceLines = sourceLines; }

    // ---- Inner model classes for spec types ----

    /** H-spec — RPG III Control Specification column layout:
     *  Col 1-5:   Sequence number / Comment
     *  Col 6:     Form type (H)
     *  Col 7-14:  Reserved
     *  Col 15:    Debug
     *  Col 16-17: Reserved
     *  Col 18:    Currency symbol
     *  Col 19:    Date format
     *  Col 20:    Date edit
     *  Col 21:    Decimal notation
     *  Col 22-25: Reserved
     *  Col 26:    Alternate collating sequence
     *  Col 27-39: Reserved
     *  Col 40:    Sign handling
     *  Col 41:    Forms alignment (inverted print)
     *  Col 42:    Reserved
     *  Col 43:    File translation
     *  Col 44-56: Reserved
     *  Col 57:    Transparency check
     *  Col 58-74: Reserved
     *  Col 75-80: Program identification
     */
    public static class HeaderSpec {
        private String rawSourceLine;
        private Location location;
        private String parseQuality = "full";
        private String sourceSequence;               // cols 1-5
        private String debugOption;                  // col 15
        private String currencySymbol;               // col 18
        private String dateFormat;                   // col 19
        private String dateEdit;                     // col 20
        private String decimalNotation;              // col 21
        private String alternateCollatingSequence;   // col 26
        private String signHandling;                 // col 40
        private String formsAlignment;               // col 41 (inverted print)
        private String fileTranslation;              // col 43
        private String transparencyCheck;            // col 57
        private String inlineComment;                // cols 58-74
        private String programIdentification;        // cols 75-80

        public String getRawSourceLine() { return rawSourceLine; }
        public void setRawSourceLine(String s) { this.rawSourceLine = s != null ? s.stripTrailing() : null; }
        public Location getLocation() { return location; }
        public void setLocation(Location l) { this.location = l; }
        public String getParseQuality() { return parseQuality; }
        public void setParseQuality(String q) { this.parseQuality = q; }
        public String getSourceSequence() { return sourceSequence; }
        public void setSourceSequence(String v) { this.sourceSequence = v; }
        public String getDebugOption() { return debugOption; }
        public void setDebugOption(String v) { this.debugOption = v; }
        public String getCurrencySymbol() { return currencySymbol; }
        public void setCurrencySymbol(String v) { this.currencySymbol = v; }
        public String getDateFormat() { return dateFormat; }
        public void setDateFormat(String v) { this.dateFormat = v; }
        public String getDateEdit() { return dateEdit; }
        public void setDateEdit(String v) { this.dateEdit = v; }
        public String getDecimalNotation() { return decimalNotation; }
        public void setDecimalNotation(String v) { this.decimalNotation = v; }
        public String getAlternateCollatingSequence() { return alternateCollatingSequence; }
        public void setAlternateCollatingSequence(String v) { this.alternateCollatingSequence = v; }
        public String getSignHandling() { return signHandling; }
        public void setSignHandling(String v) { this.signHandling = v; }
        public String getFormsAlignment() { return formsAlignment; }
        public void setFormsAlignment(String v) { this.formsAlignment = v; }
        public String getFileTranslation() { return fileTranslation; }
        public void setFileTranslation(String v) { this.fileTranslation = v; }
        public String getTransparencyCheck() { return transparencyCheck; }
        public void setTransparencyCheck(String v) { this.transparencyCheck = v; }
        public String getInlineComment() { return inlineComment; }
        public void setInlineComment(String v) { this.inlineComment = v; }
        public String getProgramIdentification() { return programIdentification; }
        public void setProgramIdentification(String v) { this.programIdentification = v; }
    }

    /** F-spec */
    /** F-spec — RPG III File Specification column layout:
     *  Col 1-5:   Sequence number / Comment
     *  Col 6:     Form type (F)
     *  Col 7-14:  File name
     *  Col 15:    File type (I/O/U/C/D)
     *  Col 16:    File designation (P/S/R/T/F/blank)
     *  Col 17:    End of file (E/blank)
     *  Col 18:    Sequence (A/D/blank)
     *  Col 19:    File format (F/E/blank)
     *  Col 20-23: Record length
     *  Col 24:    Limits processing (L/blank)
     *  Col 25-27: Length of key / record address
     *  Col 28:    Record address type (A/P/K/blank)
     *  Col 29:    File organization (I/T/blank)
     *  Col 30-32: (reserved)
     *  Col 33-34: Overflow indicator
     *  Col 35-38: Key field starting position
     *  Col 39:    Extension code (E/L/blank)
     *  Col 40-46: Device (DISK/PRINTER/WORKSTN/SPECIAL/SEQ)
     *  Col 47-52: (reserved / continuation keyword area)
     *  Col 53:    Continuation lines indicator
     *  Col 54-65: (reserved)
     *  Col 66:    File addition (A/blank)
     *  Col 67-70: (reserved)
     *  Col 71-72: File condition (U1-U8, UC)
     *  Col 73-74: (reserved)
     *  Col 75+:   Comment
     */
    public static class FileSpec {
        private String rawSourceLine;
        private Location location;
        private String parseQuality = "full";
        private String sourceSequence;               // cols 1-5
        private String fileName;                     // cols 7-14
        private String fileType;                     // col 15
        private String fileDesignation;              // col 16
        private String endOfFile;                    // col 17
        private String sequence;                     // col 18
        private String fileFormat;                   // col 19
        private Integer recordLength;                // cols 20-23
        private String limits;                       // col 24
        private Integer keyLength;                   // cols 25-27
        private String recordAddressType;            // col 28
        private String fileOrganization;             // col 29
        private String overflowIndicator;            // cols 33-34
        private Integer keyFieldStartPosition;       // cols 35-38
        private String extensionCode;                // col 39
        private String device;                       // cols 40-46
        private String continuationIndicator;        // col 53
        private String fileAddition;                 // col 66
        private String fileCondition;                // cols 71-72
        private String inlineComment;                // col 75+
        private List<FileKeyword> continuationLines;

        public String getRawSourceLine() { return rawSourceLine; }
        public void setRawSourceLine(String s) { this.rawSourceLine = s != null ? s.stripTrailing() : null; }
        public Location getLocation() { return location; }
        public void setLocation(Location l) { this.location = l; }
        public String getParseQuality() { return parseQuality; }
        public void setParseQuality(String q) { this.parseQuality = q; }
        public String getSourceSequence() { return sourceSequence; }
        public void setSourceSequence(String v) { this.sourceSequence = v; }
        public String getFileName() { return fileName; }
        public void setFileName(String v) { this.fileName = v; }
        public String getFileType() { return fileType; }
        public void setFileType(String v) { this.fileType = v; }
        public String getFileDesignation() { return fileDesignation; }
        public void setFileDesignation(String v) { this.fileDesignation = v; }
        public String getEndOfFile() { return endOfFile; }
        public void setEndOfFile(String v) { this.endOfFile = v; }
        public String getSequence() { return sequence; }
        public void setSequence(String v) { this.sequence = v; }
        public String getFileFormat() { return fileFormat; }
        public void setFileFormat(String v) { this.fileFormat = v; }
        public Integer getRecordLength() { return recordLength; }
        public void setRecordLength(Integer v) { this.recordLength = v; }
        public String getLimits() { return limits; }
        public void setLimits(String v) { this.limits = v; }
        public Integer getKeyLength() { return keyLength; }
        public void setKeyLength(Integer v) { this.keyLength = v; }
        public String getRecordAddressType() { return recordAddressType; }
        public void setRecordAddressType(String v) { this.recordAddressType = v; }
        public String getFileOrganization() { return fileOrganization; }
        public void setFileOrganization(String v) { this.fileOrganization = v; }
        public String getOverflowIndicator() { return overflowIndicator; }
        public void setOverflowIndicator(String v) { this.overflowIndicator = v; }
        public Integer getKeyFieldStartPosition() { return keyFieldStartPosition; }
        public void setKeyFieldStartPosition(Integer v) { this.keyFieldStartPosition = v; }
        public String getExtensionCode() { return extensionCode; }
        public void setExtensionCode(String v) { this.extensionCode = v; }
        public String getDevice() { return device; }
        public void setDevice(String v) { this.device = v; }
        public String getContinuationIndicator() { return continuationIndicator; }
        public void setContinuationIndicator(String v) { this.continuationIndicator = v; }
        public String getFileAddition() { return fileAddition; }
        public void setFileAddition(String v) { this.fileAddition = v; }
        public String getFileCondition() { return fileCondition; }
        public void setFileCondition(String v) { this.fileCondition = v; }
        public String getInlineComment() { return inlineComment; }
        public void setInlineComment(String v) { this.inlineComment = v; }
        public List<FileKeyword> getContinuationLines() { return continuationLines; }
        public void setContinuationLines(List<FileKeyword> v) { this.continuationLines = v; }
    }

    /** F-spec continuation keyword (RENAME, IGNORE, INCLUDE, SFILE, etc.) */
    public static class FileKeyword {
        private String keyword;       // "RENAME", "IGNORE", "INCLUDE", "SFILE"
        private String originalName;  // original record name (before colon)
        private String newName;       // new record name (after colon)
        private String rawText;       // full raw text for fallback

        public FileKeyword() {}
        public FileKeyword(String keyword, String originalName, String newName, String rawText) {
            this.keyword = keyword;
            this.originalName = originalName;
            this.newName = newName;
            this.rawText = rawText;
        }

        public String getKeyword() { return keyword; }
        public void setKeyword(String v) { this.keyword = v; }
        public String getOriginalName() { return originalName; }
        public void setOriginalName(String v) { this.originalName = v; }
        public String getNewName() { return newName; }
        public void setNewName(String v) { this.newName = v; }
        public String getRawText() { return rawText; }
        public void setRawText(String v) { this.rawText = v; }
    }

    /** E-spec — RPG III Extension Specification column layout:
     *  Col 1-5:   Sequence number / Comment
     *  Col 6:     Form type (E)
     *  Col 7-10:  Reserved
     *  Col 11-18: From file name
     *  Col 19-26: To file name
     *  Col 27-32: Array or table name
     *  Col 33-35: Entries per record
     *  Col 36-39: Entries per array or table
     *  Col 40-42: Length of entry
     *  Col 43:    Data format
     *  Col 44:    Decimal positions
     *  Col 45:    Sequence
     *  Col 46-51: Alternate array or table name
     *  Col 52-54: Alternate entry length
     *  Col 55:    Alternate data format
     *  Col 56:    Alternate decimal positions
     *  Col 57:    Alternate sequence
     *  Col 58+:   Comment
     */
    public static class ExtensionSpec {
        private String rawSourceLine;
        private Location location;
        private String parseQuality = "full";
        private String sourceSequence;               // cols 1-5
        private String fromFileName;                 // cols 11-18
        private String toFileName;                   // cols 19-26
        private String arrayOrTableName;             // cols 27-32
        private Integer entriesPerRecord;            // cols 33-35
        private Integer entriesPerArray;             // cols 36-39
        private Integer entryLength;                 // cols 40-42
        private String dataFormat;                   // col 43
        private Integer decimalPositions;            // col 44
        private String sequenceType;                 // col 45
        private String alternateArrayName;           // cols 46-51
        private Integer alternateEntryLength;        // cols 52-54
        private String alternateDataFormat;          // col 55
        private Integer alternateDecimalPositions;   // col 56
        private String alternateSequenceType;        // col 57
        private String inlineComment;                // col 58+

        public String getRawSourceLine() { return rawSourceLine; }
        public void setRawSourceLine(String s) { this.rawSourceLine = s != null ? s.stripTrailing() : null; }
        public Location getLocation() { return location; }
        public void setLocation(Location l) { this.location = l; }
        public String getParseQuality() { return parseQuality; }
        public void setParseQuality(String q) { this.parseQuality = q; }
        public String getSourceSequence() { return sourceSequence; }
        public void setSourceSequence(String v) { this.sourceSequence = v; }
        public String getFromFileName() { return fromFileName; }
        public void setFromFileName(String v) { this.fromFileName = v; }
        public String getToFileName() { return toFileName; }
        public void setToFileName(String v) { this.toFileName = v; }
        public String getArrayOrTableName() { return arrayOrTableName; }
        public void setArrayOrTableName(String v) { this.arrayOrTableName = v; }
        public Integer getEntriesPerRecord() { return entriesPerRecord; }
        public void setEntriesPerRecord(Integer v) { this.entriesPerRecord = v; }
        public Integer getEntriesPerArray() { return entriesPerArray; }
        public void setEntriesPerArray(Integer v) { this.entriesPerArray = v; }
        public Integer getEntryLength() { return entryLength; }
        public void setEntryLength(Integer v) { this.entryLength = v; }
        public String getDataFormat() { return dataFormat; }
        public void setDataFormat(String v) { this.dataFormat = v; }
        public Integer getDecimalPositions() { return decimalPositions; }
        public void setDecimalPositions(Integer v) { this.decimalPositions = v; }
        public String getSequenceType() { return sequenceType; }
        public void setSequenceType(String v) { this.sequenceType = v; }
        public String getAlternateArrayName() { return alternateArrayName; }
        public void setAlternateArrayName(String v) { this.alternateArrayName = v; }
        public Integer getAlternateEntryLength() { return alternateEntryLength; }
        public void setAlternateEntryLength(Integer v) { this.alternateEntryLength = v; }
        public String getAlternateDataFormat() { return alternateDataFormat; }
        public void setAlternateDataFormat(String v) { this.alternateDataFormat = v; }
        public Integer getAlternateDecimalPositions() { return alternateDecimalPositions; }
        public void setAlternateDecimalPositions(Integer v) { this.alternateDecimalPositions = v; }
        public String getAlternateSequenceType() { return alternateSequenceType; }
        public void setAlternateSequenceType(String v) { this.alternateSequenceType = v; }
        public String getInlineComment() { return inlineComment; }
        public void setInlineComment(String v) { this.inlineComment = v; }
    }

    /** L-spec */
    public static class LineCounterSpec {
        private String rawSourceLine;
        private Location location;
        private String parseQuality = "full";
        private String sourceSequence;               // cols 1-5
        private String fileName;
        private Integer linesPerPage;
        private Integer overflowLine;

        public String getRawSourceLine() { return rawSourceLine; }
        public void setRawSourceLine(String s) { this.rawSourceLine = s != null ? s.stripTrailing() : null; }
        public Location getLocation() { return location; }
        public void setLocation(Location l) { this.location = l; }
        public String getParseQuality() { return parseQuality; }
        public void setParseQuality(String q) { this.parseQuality = q; }
        public String getSourceSequence() { return sourceSequence; }
        public void setSourceSequence(String v) { this.sourceSequence = v; }
        public String getFileName() { return fileName; }
        public void setFileName(String v) { this.fileName = v; }
        public Integer getLinesPerPage() { return linesPerPage; }
        public void setLinesPerPage(Integer v) { this.linesPerPage = v; }
        public Integer getOverflowLine() { return overflowLine; }
        public void setOverflowLine(Integer v) { this.overflowLine = v; }
    }

    /** I-spec — RPG III Input Specification column layout:
     *  Col 1-5:   Sequence number / Comment
     *  Col 6:     Form type (I)
     *  Col 7:     Reserved
     *  Col 8:     Initialization option
     *  Col 9-20:  Reserved (record ID area on record-level lines)
     *  Col 21-30: External field name (externally-described rename)
     *  Col 31-42: Reserved
     *  Col 43:    Internal data format (P/B/L/R/blank)
     *  Col 44-47: From position
     *  Col 48-51: To position
     *  Col 52:    Decimal positions
     *  Col 53-58: Subfield/field name
     *  Col 59-60: Control level (L1-L9)
     *  Col 61-62: Matching fields (M1-M9)
     *  Col 63-64: Field record relation
     *  Col 65-66: Plus indicator (field > 0)
     *  Col 67-68: Minus indicator (field < 0)
     *  Col 69-70: Zero/blank indicator (field = 0 or blank)
     *  Col 71-74: Reserved
     *  Col 75+:   Comment
     */
    public static class InputSpec {
        private String rawSourceLine;
        private Location location;
        private String parseQuality = "full";
        private String sourceSequence;               // cols 1-5
        private String specLevel;  // "recordIdentification" or "fieldDefinition"
        private String fileName;
        private String recordName;
        private String sequenceNumber;
        private String option;
        private String recordIdIndicator;
        // Field definition fields
        private String fieldName;
        private String externalFieldName;  // externally-described field rename: original name (cols 21-30)
        private Integer fromPosition;          // cols 44-47
        private Integer toPosition;            // cols 48-51
        private Integer decimalPositions;      // col 52
        private String dataFormat;             // col 43
        private String controlLevel;           // cols 59-60 (L1-L9)
        private String matchingFields;         // cols 61-62 (M1-M9)
        private String fieldRecordRelation;    // cols 63-64
        private String plusIndicator;          // cols 65-66 (field > 0)
        private String minusIndicator;         // cols 67-68 (field < 0)
        private String zeroBlankIndicator;     // cols 69-70 (field = 0 or blank)
        private String fieldIndicators;        // legacy: raw cols 65-70
        private String initializationValue;    // DS subfield init: I 'constant'
        private String inlineComment;          // col 75+ (from normalizer lineComments)

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
        public String getInitializationValue() { return initializationValue; }
        public void setInitializationValue(String v) { this.initializationValue = v; }
        public String getInlineComment() { return inlineComment; }
        public void setInlineComment(String v) { this.inlineComment = v; }
    }

    /** O-spec */
    /** O-spec — RPG III Output Specification column layout:
     *
     *  Record-level (file name or record name in cols 7-14):
     *  Col 7-14:  File/record name
     *  Col 14-16: Logical relationship (AND/OR)
     *  Col 15:    Type (H=heading, D=detail, T=total, E=exception)
     *  Col 16-18: Record addition/deletion (ADD/DEL)
     *  Col 16:    Fetch overflow / Release
     *  Col 17:    Space before
     *  Col 18:    Space after
     *  Col 19-20: Skip before
     *  Col 21-22: Skip after
     *  Col 23-31: Output indicators (3 slots × 3 cols)
     *  Col 32-37: EXCPT name
     *  Col 75+:   Comment
     *
     *  Field-level (cols 7-22 blank):
     *  Col 23-31: Field output indicators (3 slots × 3 cols)
     *  Col 32-37: Field name
     *  Col 38:    Edit code
     *  Col 39:    Blank after (B)
     *  Col 40-43: End position
     *  Col 44:    Data format (P/B/L/R)
     *  Col 45-70: Constant or edit word
     *  Col 75+:   Comment
     */
    public static class OutputSpec {
        private String rawSourceLine;
        private Location location;
        private String parseQuality = "full";
        private String sourceSequence;               // cols 1-5
        private String specLevel;                    // "recordLevel" or "fieldLevel"

        // Record-level fields
        private String fileName;                     // cols 7-14
        private String logicalRelationship;          // cols 14-16 (AND/OR)
        private String type;                         // col 15 (H/D/T/E)
        private String addDel;                       // cols 16-18 (ADD/DEL)
        private String fetchOverflow;                // col 16
        private String spaceBefore;                  // col 17
        private String spaceAfter;                   // col 18
        private String skipBefore;                   // cols 19-20
        private String skipAfter;                    // cols 21-22
        private String outputIndicators;             // cols 23-31 (raw 9-char string)
        private String exceptName;                   // cols 32-37

        // Field-level fields
        private String fieldName;                    // cols 32-37
        private String editCode;                     // col 38
        private String blankAfter;                   // col 39
        private Integer endPosition;                 // cols 40-43
        private String dataFormat;                   // col 44
        private String constantOrEditWord;           // cols 45-70

        // Common
        private String inlineComment;                // col 75+

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

    /** Data Structure (detected from I-spec with DS/SDS in cols 19-20) */
    public static class DataStructure {
        private String name;
        private String rawSourceLine;
        private Location location;
        private String sourceSequence;
        private Integer length;
        private String type;  // "dataStructure" or "programStatusDS"
        private String inlineComment;
        private List<DataStructureSubfield> subfields = new ArrayList<>();

        public String getName() { return name; }
        public void setName(String v) { this.name = v; }
        public String getRawSourceLine() { return rawSourceLine; }
        public void setRawSourceLine(String v) { this.rawSourceLine = v != null ? v.stripTrailing() : null; }
        public Location getLocation() { return location; }
        public void setLocation(Location l) { this.location = l; }
        public String getSourceSequence() { return sourceSequence; }
        public void setSourceSequence(String v) { this.sourceSequence = v; }
        public Integer getLength() { return length; }
        public void setLength(Integer v) { this.length = v; }
        public String getType() { return type; }
        public void setType(String v) { this.type = v; }
        public String getInlineComment() { return inlineComment; }
        public void setInlineComment(String v) { this.inlineComment = v; }
        public List<DataStructureSubfield> getSubfields() { return subfields; }
        public void setSubfields(List<DataStructureSubfield> v) { this.subfields = v; }
    }

    /** Data Structure subfield — full field info matching InputSpec */
    public static class DataStructureSubfield {
        private String rawSourceLine;
        private Location location;
        private String sourceSequence;
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
        private String initializationValue;
        private String inlineComment;

        public String getRawSourceLine() { return rawSourceLine; }
        public void setRawSourceLine(String v) { this.rawSourceLine = v != null ? v.stripTrailing() : null; }
        public Location getLocation() { return location; }
        public void setLocation(Location l) { this.location = l; }
        public String getSourceSequence() { return sourceSequence; }
        public void setSourceSequence(String v) { this.sourceSequence = v; }
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
        public String getInitializationValue() { return initializationValue; }
        public void setInitializationValue(String v) { this.initializationValue = v; }
        public String getInlineComment() { return inlineComment; }
        public void setInlineComment(String v) { this.inlineComment = v; }
    }

    /** Symbol table entry */
    public static class SymbolEntry {
        private String name;
        private String dataType;
        private Integer length;
        private Integer decimalPositions;
        private String definedIn;  // "I-spec", "C-spec", "E-spec", "DS"
        private Location definitionLocation;
        private boolean isDataStructure;
        private String dataStructureName;  // parent DS name if this is a subfield

        public String getName() { return name; }
        public void setName(String v) { this.name = v; }
        public String getDataType() { return dataType; }
        public void setDataType(String v) { this.dataType = v; }
        public Integer getLength() { return length; }
        public void setLength(Integer v) { this.length = v; }
        public Integer getDecimalPositions() { return decimalPositions; }
        public void setDecimalPositions(Integer v) { this.decimalPositions = v; }
        public String getDefinedIn() { return definedIn; }
        public void setDefinedIn(String v) { this.definedIn = v; }
        public Location getDefinitionLocation() { return definitionLocation; }
        public void setDefinitionLocation(Location v) { this.definitionLocation = v; }
        public boolean isDataStructure() { return isDataStructure; }
        public void setDataStructure(boolean v) { this.isDataStructure = v; }
        public String getDataStructureName() { return dataStructureName; }
        public void setDataStructureName(String v) { this.dataStructureName = v; }
    }



    /** Compile-time data (** blocks) */
    public static class CompileTimeData {
        private List<CompileTimeBlock> blocks = new ArrayList<>();

        public List<CompileTimeBlock> getBlocks() { return blocks; }
        public void setBlocks(List<CompileTimeBlock> v) { this.blocks = v; }
    }

    public static class CompileTimeBlock {
        private String arrayName;
        private List<String> data = new ArrayList<>();
        private Location location;

        public String getArrayName() { return arrayName; }
        public void setArrayName(String v) { this.arrayName = v; }
        public List<String> getData() { return data; }
        public void setData(List<String> v) { this.data = v; }
        public Location getLocation() { return location; }
        public void setLocation(Location l) { this.location = l; }
    }

    /** Comment */
    public static class Comment {
        private String text;
        private int lineNumber;
        private String specContext;
        private String rawSourceLine;
        private Location location;

        public String getText() { return text; }
        public void setText(String v) { this.text = v; }
        public int getLineNumber() { return lineNumber; }
        public void setLineNumber(int v) { this.lineNumber = v; }
        public String getSpecContext() { return specContext; }
        public void setSpecContext(String v) { this.specContext = v; }
        public String getRawSourceLine() { return rawSourceLine; }
        public void setRawSourceLine(String v) { this.rawSourceLine = v != null ? v.stripTrailing() : null; }
        public Location getLocation() { return location; }
        public void setLocation(Location v) { this.location = v; }
    }
}
