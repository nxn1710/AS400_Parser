package com.as400parser.rpgle.model;

import com.as400parser.common.model.Location;
import com.as400parser.common.model.ParseError;
import com.as400parser.common.model.SourceLine;

import java.util.ArrayList;
import java.util.List;

/**
 * Top-level RPGLE content model.
 * <p>
 * Set on {@code IrDocument.setContent()} to hold all parsed RPGLE specification
 * data.
 * Supports all 7 RPG IV specification types plus source lines and comments.
 */
public class RpgleContent {

    // =========================================================================
    // Source metadata
    // =========================================================================
    private List<SourceLine> sourceLines = new ArrayList<>();
    private List<String> comments = new ArrayList<>();
    private String formatType; // "free", "fixed", "mixed"

    // =========================================================================
    // Specification data — mapped from fixed-format positional parsing
    // =========================================================================
    private List<ControlSpec> controlSpecs = new ArrayList<>(); // H-specs
    private List<FileSpec> fileSpecs = new ArrayList<>(); // F-specs
    private List<DefinitionSpec> definitionSpecs = new ArrayList<>(); // D-specs
    private List<InputSpec> inputSpecs = new ArrayList<>(); // I-specs
    private List<CalcSpec> calcSpecs = new ArrayList<>(); // C-specs
    private List<OutputSpec> outputSpecs = new ArrayList<>(); // O-specs
    private List<ProcedureSpec> procedureSpecs = new ArrayList<>(); // P-specs

    // =========================================================================
    // Free-format data — mapped from ANTLR parse tree
    // =========================================================================
    private List<FreeFormatStatement> freeFormatStatements = new ArrayList<>();

    // =========================================================================
    // Parse errors
    // =========================================================================
    private List<ParseError> parseErrors = new ArrayList<>();

    // =========================================================================
    // Accessors
    // =========================================================================
    public List<SourceLine> getSourceLines() {
        return sourceLines;
    }

    public void setSourceLines(List<SourceLine> v) {
        this.sourceLines = v;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> v) {
        this.comments = v;
    }

    public String getFormatType() {
        return formatType;
    }

    public void setFormatType(String v) {
        this.formatType = v;
    }

    public List<ControlSpec> getControlSpecs() {
        return controlSpecs;
    }

    public void setControlSpecs(List<ControlSpec> v) {
        this.controlSpecs = v;
    }

    public List<FileSpec> getFileSpecs() {
        return fileSpecs;
    }

    public void setFileSpecs(List<FileSpec> v) {
        this.fileSpecs = v;
    }

    public List<DefinitionSpec> getDefinitionSpecs() {
        return definitionSpecs;
    }

    public void setDefinitionSpecs(List<DefinitionSpec> v) {
        this.definitionSpecs = v;
    }

    public List<InputSpec> getInputSpecs() {
        return inputSpecs;
    }

    public void setInputSpecs(List<InputSpec> v) {
        this.inputSpecs = v;
    }

    public List<CalcSpec> getCalcSpecs() {
        return calcSpecs;
    }

    public void setCalcSpecs(List<CalcSpec> v) {
        this.calcSpecs = v;
    }

    public List<OutputSpec> getOutputSpecs() {
        return outputSpecs;
    }

    public void setOutputSpecs(List<OutputSpec> v) {
        this.outputSpecs = v;
    }

    public List<ProcedureSpec> getProcedureSpecs() {
        return procedureSpecs;
    }

    public void setProcedureSpecs(List<ProcedureSpec> v) {
        this.procedureSpecs = v;
    }

    public List<FreeFormatStatement> getFreeFormatStatements() {
        return freeFormatStatements;
    }

    public void setFreeFormatStatements(List<FreeFormatStatement> v) {
        this.freeFormatStatements = v;
    }

    public List<ParseError> getParseErrors() {
        return parseErrors;
    }

    public void setParseErrors(List<ParseError> v) {
        this.parseErrors = v;
    }

    // =========================================================================
    // Copy directives (/COPY and /INCLUDE)
    // =========================================================================
    private List<CopyDirective> copyDirectives = new ArrayList<>();

    public List<CopyDirective> getCopyDirectives() {
        return copyDirectives;
    }

    public void setCopyDirectives(List<CopyDirective> v) {
        this.copyDirectives = v;
    }

    /**
     * Represents a /COPY or /INCLUDE directive found in source.
     */
    public record CopyDirective(String directive, String path, Location location) {}
}
