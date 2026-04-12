package com.as400parser.rpgle.model;

import com.as400parser.common.model.Location;
import com.as400parser.common.model.SourceLine;

import java.util.ArrayList;
import java.util.List;

/**
 * RPGLE-specific content section of the IR document.
 * Contains all spec type arrays plus supporting structures.
 * Mirrors Rpg3Content structure for IR compatibility.
 */
public class RpgleContent {
    private List<ControlSpec> controlSpecs = new ArrayList<>();
    private List<FileSpec> fileSpecs = new ArrayList<>();
    private List<DefinitionSpec> definitionSpecs = new ArrayList<>();
    private List<Object> calculationSpecs = new ArrayList<>();  // CalcSpec nodes or control flow blocks
    private List<InputSpec> inputSpecs = new ArrayList<>();
    private List<OutputSpec> outputSpecs = new ArrayList<>();
    private List<ProcedureSpec> procedureSpecs = new ArrayList<>();
    private List<DataStructure> dataStructures = new ArrayList<>();
    private List<SymbolEntry> symbolTable = new ArrayList<>();
    private List<CalcSpec.SubroutineBlock> subroutines = new ArrayList<>();
    private List<FreeFormatStatement> freeFormatStatements = new ArrayList<>();
    private List<Comment> comments = new ArrayList<>();
    private List<SourceLine> sourceLines = new ArrayList<>();

    // Getters and setters
    public List<ControlSpec> getControlSpecs() { return controlSpecs; }
    public void setControlSpecs(List<ControlSpec> v) { this.controlSpecs = v; }
    public List<FileSpec> getFileSpecs() { return fileSpecs; }
    public void setFileSpecs(List<FileSpec> v) { this.fileSpecs = v; }
    public List<DefinitionSpec> getDefinitionSpecs() { return definitionSpecs; }
    public void setDefinitionSpecs(List<DefinitionSpec> v) { this.definitionSpecs = v; }
    public List<Object> getCalculationSpecs() { return calculationSpecs; }
    public void setCalculationSpecs(List<Object> v) { this.calculationSpecs = v; }
    public List<InputSpec> getInputSpecs() { return inputSpecs; }
    public void setInputSpecs(List<InputSpec> v) { this.inputSpecs = v; }
    public List<OutputSpec> getOutputSpecs() { return outputSpecs; }
    public void setOutputSpecs(List<OutputSpec> v) { this.outputSpecs = v; }
    public List<ProcedureSpec> getProcedureSpecs() { return procedureSpecs; }
    public void setProcedureSpecs(List<ProcedureSpec> v) { this.procedureSpecs = v; }
    public List<DataStructure> getDataStructures() { return dataStructures; }
    public void setDataStructures(List<DataStructure> v) { this.dataStructures = v; }
    public List<SymbolEntry> getSymbolTable() { return symbolTable; }
    public void setSymbolTable(List<SymbolEntry> v) { this.symbolTable = v; }
    public List<CalcSpec.SubroutineBlock> getSubroutines() { return subroutines; }
    public void setSubroutines(List<CalcSpec.SubroutineBlock> v) { this.subroutines = v; }
    public List<FreeFormatStatement> getFreeFormatStatements() { return freeFormatStatements; }
    public void setFreeFormatStatements(List<FreeFormatStatement> v) { this.freeFormatStatements = v; }
    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> v) { this.comments = v; }
    public List<SourceLine> getSourceLines() { return sourceLines; }
    public void setSourceLines(List<SourceLine> v) { this.sourceLines = v; }

    // ---- Inner model classes ----

    /** Data Structure (from D-spec DS definition) */
    public static class DataStructure {
        private String name;
        private String rawSourceLine;
        private Location location;
        private String sourceSequence;
        private String type;  // "dataStructure", "programStatusDS", "fileInfoDS"
        private Integer length;
        private String keywords;
        private String inlineComment;
        private List<DefinitionSpec> subfields = new ArrayList<>();

        public String getName() { return name; }
        public void setName(String v) { this.name = v; }
        public String getRawSourceLine() { return rawSourceLine; }
        public void setRawSourceLine(String v) { this.rawSourceLine = v != null ? v.stripTrailing() : null; }
        public Location getLocation() { return location; }
        public void setLocation(Location l) { this.location = l; }
        public String getSourceSequence() { return sourceSequence; }
        public void setSourceSequence(String v) { this.sourceSequence = v; }
        public String getType() { return type; }
        public void setType(String v) { this.type = v; }
        public Integer getLength() { return length; }
        public void setLength(Integer v) { this.length = v; }
        public String getKeywords() { return keywords; }
        public void setKeywords(String v) { this.keywords = v; }
        public String getInlineComment() { return inlineComment; }
        public void setInlineComment(String v) { this.inlineComment = v; }
        public List<DefinitionSpec> getSubfields() { return subfields; }
        public void setSubfields(List<DefinitionSpec> v) { this.subfields = v; }
    }

    /** Symbol table entry */
    public static class SymbolEntry {
        private String name;
        private String dataType;
        private Integer length;
        private Integer decimalPositions;
        private String definedIn;  // "D-spec", "I-spec", "C-spec", "DS"
        private Location definitionLocation;
        private boolean isDataStructure;
        private String dataStructureName;

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
