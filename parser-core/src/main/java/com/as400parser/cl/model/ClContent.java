package com.as400parser.cl.model;

import com.as400parser.common.model.ParseError;
import com.as400parser.common.model.SourceLine;

import java.util.ArrayList;
import java.util.List;

/**
 * Top-level content model for Control Language (CL, CLP, CLLE) source files.
 * Represents the structured Output of the parsing process.
 */
public class ClContent {
    /** The original parsed source lines with line and sequence numbers. */
    private List<SourceLine> sourceLines = new ArrayList<>();
    
    /** The inferred or explicit name of the program being parsed. */
    private String programName;
    
    /** DCL variable declarations found in the source. */
    private List<ClVariable> variables = new ArrayList<>();
    
    /** DCLF file declarations. */
    private List<ClFileDeclaration> fileDeclarations = new ArrayList<>();
    
    /** The core list of parsed sequential CL commands. */
    private List<ClCommand> commands = new ArrayList<>();
    
    /** Target labels (e.g., used by GOTO). */
    private List<ClLabel> labels = new ArrayList<>();
    
    /** Subroutine logic blocks (CLLE only). */
    private List<ClSubroutine> subroutines = new ArrayList<>();
    
    /** Extracted standalone and multi-line comments. */
    private List<ClComment> comments = new ArrayList<>();
    
    /** Any parse errors or warnings encountered. */
    private List<ParseError> parseErrors = new ArrayList<>();

    public List<SourceLine> getSourceLines() {
        return sourceLines;
    }

    public void setSourceLines(List<SourceLine> sourceLines) {
        this.sourceLines = sourceLines;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public List<ClVariable> getVariables() {
        return variables;
    }

    public void setVariables(List<ClVariable> variables) {
        this.variables = variables;
    }

    public List<ClFileDeclaration> getFileDeclarations() {
        return fileDeclarations;
    }

    public void setFileDeclarations(List<ClFileDeclaration> fileDeclarations) {
        this.fileDeclarations = fileDeclarations;
    }

    public List<ClCommand> getCommands() {
        return commands;
    }

    public void setCommands(List<ClCommand> commands) {
        this.commands = commands;
    }

    public List<ClLabel> getLabels() {
        return labels;
    }

    public void setLabels(List<ClLabel> labels) {
        this.labels = labels;
    }

    public List<ClSubroutine> getSubroutines() {
        return subroutines;
    }

    public void setSubroutines(List<ClSubroutine> subroutines) {
        this.subroutines = subroutines;
    }

    public List<ClComment> getComments() {
        return comments;
    }

    public void setComments(List<ClComment> comments) {
        this.comments = comments;
    }

    public List<ParseError> getParseErrors() {
        return parseErrors;
    }

    public void setParseErrors(List<ParseError> parseErrors) {
        this.parseErrors = parseErrors;
    }
}
