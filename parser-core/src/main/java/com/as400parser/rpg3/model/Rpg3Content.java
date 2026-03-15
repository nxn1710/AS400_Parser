package com.as400parser.rpg3.model;

import com.as400parser.common.model.Location;
import com.as400parser.common.model.SourceLine;

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
    private List<Subroutine> subroutines = new ArrayList<>();
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
    public List<Subroutine> getSubroutines() { return subroutines; }
    public void setSubroutines(List<Subroutine> subroutines) { this.subroutines = subroutines; }
    public CompileTimeData getCompileTimeData() { return compileTimeData; }
    public void setCompileTimeData(CompileTimeData compileTimeData) { this.compileTimeData = compileTimeData; }
    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
    public List<SourceLine> getSourceLines() { return sourceLines; }
    public void setSourceLines(List<SourceLine> sourceLines) { this.sourceLines = sourceLines; }

    // ---- Inner model classes for spec types ----

    /** H-spec */
    public static class HeaderSpec {
        private String rawSourceLine;
        private Location location;
        private String parseQuality = "full";
        private String debugOption;         // col 15
        private String currencySymbol;      // col 18
        private String dateFormat;          // col 19
        private String dateEdit;            // col 20
        private String decimalNotation;     // col 21
        private String invertedPrint;       // col 26

        public String getRawSourceLine() { return rawSourceLine; }
        public void setRawSourceLine(String s) { this.rawSourceLine = s; }
        public Location getLocation() { return location; }
        public void setLocation(Location l) { this.location = l; }
        public String getParseQuality() { return parseQuality; }
        public void setParseQuality(String q) { this.parseQuality = q; }
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
        public String getInvertedPrint() { return invertedPrint; }
        public void setInvertedPrint(String v) { this.invertedPrint = v; }
    }

    /** F-spec */
    public static class FileSpec {
        private String rawSourceLine;
        private Location location;
        private String parseQuality = "full";
        private String fileName;
        private String fileType;
        private String fileDesignation;
        private String endOfFile;
        private String fileAddition;
        private String sequence;
        private String fileFormat;
        private Integer recordLength;
        private String limits;
        private Integer keyLength;
        private String recordAddressType;
        private String fileOrganization;
        private String device;
        private List<FileKeyword> continuationLines;

        public String getRawSourceLine() { return rawSourceLine; }
        public void setRawSourceLine(String s) { this.rawSourceLine = s; }
        public Location getLocation() { return location; }
        public void setLocation(Location l) { this.location = l; }
        public String getParseQuality() { return parseQuality; }
        public void setParseQuality(String q) { this.parseQuality = q; }
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
        public String getLimits() { return limits; }
        public void setLimits(String v) { this.limits = v; }
        public Integer getKeyLength() { return keyLength; }
        public void setKeyLength(Integer v) { this.keyLength = v; }
        public String getRecordAddressType() { return recordAddressType; }
        public void setRecordAddressType(String v) { this.recordAddressType = v; }
        public String getFileOrganization() { return fileOrganization; }
        public void setFileOrganization(String v) { this.fileOrganization = v; }
        public String getDevice() { return device; }
        public void setDevice(String v) { this.device = v; }
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

    /** E-spec */
    public static class ExtensionSpec {
        private String rawSourceLine;
        private Location location;
        private String parseQuality = "full";
        private String fromFileName;
        private String toFileName;
        private String arrayOrTableName;
        private Integer entriesPerRecord;
        private Integer entriesPerArray;
        private Integer entryLength;
        private String dataFormat;
        private Integer decimalPositions;
        private String sequenceType;
        private String alternateArrayName;
        private Integer alternateEntryLength;
        private String alternateDataFormat;
        private Integer alternateDecimalPositions;

        public String getRawSourceLine() { return rawSourceLine; }
        public void setRawSourceLine(String s) { this.rawSourceLine = s; }
        public Location getLocation() { return location; }
        public void setLocation(Location l) { this.location = l; }
        public String getParseQuality() { return parseQuality; }
        public void setParseQuality(String q) { this.parseQuality = q; }
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
    }

    /** L-spec */
    public static class LineCounterSpec {
        private String rawSourceLine;
        private Location location;
        private String parseQuality = "full";
        private String fileName;
        private Integer linesPerPage;
        private Integer overflowLine;

        public String getRawSourceLine() { return rawSourceLine; }
        public void setRawSourceLine(String s) { this.rawSourceLine = s; }
        public Location getLocation() { return location; }
        public void setLocation(Location l) { this.location = l; }
        public String getParseQuality() { return parseQuality; }
        public void setParseQuality(String q) { this.parseQuality = q; }
        public String getFileName() { return fileName; }
        public void setFileName(String v) { this.fileName = v; }
        public Integer getLinesPerPage() { return linesPerPage; }
        public void setLinesPerPage(Integer v) { this.linesPerPage = v; }
        public Integer getOverflowLine() { return overflowLine; }
        public void setOverflowLine(Integer v) { this.overflowLine = v; }
    }

    /** I-spec */
    public static class InputSpec {
        private String rawSourceLine;
        private Location location;
        private String parseQuality = "full";
        private String specLevel;  // "recordIdentification" or "fieldDefinition"
        private String fileName;
        private String recordName;
        private String sequenceNumber;
        private String option;
        private String recordIdIndicator;
        // Field definition fields
        private String fieldName;
        private Integer fromPosition;
        private Integer toPosition;
        private Integer decimalPositions;
        private String dataFormat;
        private String fieldIndicators;
        private String initializationValue;  // DS subfield init: I 'constant'

        public String getRawSourceLine() { return rawSourceLine; }
        public void setRawSourceLine(String s) { this.rawSourceLine = s; }
        public Location getLocation() { return location; }
        public void setLocation(Location l) { this.location = l; }
        public String getParseQuality() { return parseQuality; }
        public void setParseQuality(String q) { this.parseQuality = q; }
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
        public Integer getFromPosition() { return fromPosition; }
        public void setFromPosition(Integer v) { this.fromPosition = v; }
        public Integer getToPosition() { return toPosition; }
        public void setToPosition(Integer v) { this.toPosition = v; }
        public Integer getDecimalPositions() { return decimalPositions; }
        public void setDecimalPositions(Integer v) { this.decimalPositions = v; }
        public String getDataFormat() { return dataFormat; }
        public void setDataFormat(String v) { this.dataFormat = v; }
        public String getFieldIndicators() { return fieldIndicators; }
        public void setFieldIndicators(String v) { this.fieldIndicators = v; }
        public String getInitializationValue() { return initializationValue; }
        public void setInitializationValue(String v) { this.initializationValue = v; }
    }

    /** O-spec */
    public static class OutputSpec {
        private String rawSourceLine;
        private Location location;
        private String parseQuality = "full";
        private String specLevel;  // "recordLevel" or "fieldLevel"
        private String fileName;
        private String type;
        private String fetchOverflow;
        private String exceptName;
        // Field-level
        private String fieldName;
        private Integer endPosition;
        private String editCode;
        private String blankAfter;
        private String dataFormat;

        public String getRawSourceLine() { return rawSourceLine; }
        public void setRawSourceLine(String s) { this.rawSourceLine = s; }
        public Location getLocation() { return location; }
        public void setLocation(Location l) { this.location = l; }
        public String getParseQuality() { return parseQuality; }
        public void setParseQuality(String q) { this.parseQuality = q; }
        public String getSpecLevel() { return specLevel; }
        public void setSpecLevel(String v) { this.specLevel = v; }
        public String getFileName() { return fileName; }
        public void setFileName(String v) { this.fileName = v; }
        public String getType() { return type; }
        public void setType(String v) { this.type = v; }
        public String getFetchOverflow() { return fetchOverflow; }
        public void setFetchOverflow(String v) { this.fetchOverflow = v; }
        public String getExceptName() { return exceptName; }
        public void setExceptName(String v) { this.exceptName = v; }
        public String getFieldName() { return fieldName; }
        public void setFieldName(String v) { this.fieldName = v; }
        public Integer getEndPosition() { return endPosition; }
        public void setEndPosition(Integer v) { this.endPosition = v; }
        public String getEditCode() { return editCode; }
        public void setEditCode(String v) { this.editCode = v; }
        public String getBlankAfter() { return blankAfter; }
        public void setBlankAfter(String v) { this.blankAfter = v; }
        public String getDataFormat() { return dataFormat; }
        public void setDataFormat(String v) { this.dataFormat = v; }
    }

    /** Data Structure (detected from I-spec with DS in cols 19-20) */
    public static class DataStructure {
        private String name;
        private Location location;
        private Integer length;
        private String type;  // "dataStructure"
        private List<DataStructureSubfield> subfields = new ArrayList<>();

        public String getName() { return name; }
        public void setName(String v) { this.name = v; }
        public Location getLocation() { return location; }
        public void setLocation(Location l) { this.location = l; }
        public Integer getLength() { return length; }
        public void setLength(Integer v) { this.length = v; }
        public String getType() { return type; }
        public void setType(String v) { this.type = v; }
        public List<DataStructureSubfield> getSubfields() { return subfields; }
        public void setSubfields(List<DataStructureSubfield> v) { this.subfields = v; }
    }

    public static class DataStructureSubfield {
        private String name;
        private Integer fromPosition;
        private Integer toPosition;
        private String dataType;
        private Integer decimalPositions;

        public String getName() { return name; }
        public void setName(String v) { this.name = v; }
        public Integer getFromPosition() { return fromPosition; }
        public void setFromPosition(Integer v) { this.fromPosition = v; }
        public Integer getToPosition() { return toPosition; }
        public void setToPosition(Integer v) { this.toPosition = v; }
        public String getDataType() { return dataType; }
        public void setDataType(String v) { this.dataType = v; }
        public Integer getDecimalPositions() { return decimalPositions; }
        public void setDecimalPositions(Integer v) { this.decimalPositions = v; }
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

    /** Subroutine index entry (operations live only in calculationSpecs SubroutineBlock) */
    public static class Subroutine {
        private String name;
        private int definedAtLine;
        private List<Integer> calledFrom = new ArrayList<>();

        public String getName() { return name; }
        public void setName(String v) { this.name = v; }
        public int getDefinedAtLine() { return definedAtLine; }
        public void setDefinedAtLine(int v) { this.definedAtLine = v; }
        public List<Integer> getCalledFrom() { return calledFrom; }
        public void setCalledFrom(List<Integer> v) { this.calledFrom = v; }
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
        public void setRawSourceLine(String v) { this.rawSourceLine = v; }
        public Location getLocation() { return location; }
        public void setLocation(Location v) { this.location = v; }
    }
}
