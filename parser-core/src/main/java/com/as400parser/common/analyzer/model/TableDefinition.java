package com.as400parser.common.analyzer.model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Defines a relational table structure extracted from an AS400 Physical File (PF).
 */
public class TableDefinition {
    
    /**
     * The name of the table (usually the name of the PF member).
     */
    @SerializedName("name")
    private String name;

    /**
     * The DDS record format name associated with this file.
     */
    @SerializedName("recordFormat")
    private String recordFormat;

    /**
     * A human-readable description of the table, extracted from the DDS TEXT keyword.
     */
    @SerializedName("description")
    private String description;

    /**
     * The list of columns (fields) defined in the table.
     */
    @SerializedName("columns")
    private List<ColumnDefinition> columns = new ArrayList<>();

    /**
     * The ordered list of column names that make up the primary key.
     */
    @SerializedName("primaryKey")
    private List<String> primaryKey = new ArrayList<>();

    /**
     * Indicates whether the primary key enforces uniqueness (from the UNIQUE keyword).
     */
    @SerializedName("unique")
    private boolean unique;

    /**
     * Any file-level keywords found in the DDS source.
     */
    @SerializedName("fileKeywords")
    private Map<String, String> fileKeywords = new HashMap<>();

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRecordFormat() { return recordFormat; }
    public void setRecordFormat(String recordFormat) { this.recordFormat = recordFormat; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<ColumnDefinition> getColumns() { return columns; }
    public void setColumns(List<ColumnDefinition> columns) { this.columns = columns; }

    public List<String> getPrimaryKey() { return primaryKey; }
    public void setPrimaryKey(List<String> primaryKey) { this.primaryKey = primaryKey; }

    public boolean isUnique() { return unique; }
    public void setUnique(boolean unique) { this.unique = unique; }

    public Map<String, String> getFileKeywords() { return fileKeywords; }
    public void setFileKeywords(Map<String, String> fileKeywords) { this.fileKeywords = fileKeywords; }
}
