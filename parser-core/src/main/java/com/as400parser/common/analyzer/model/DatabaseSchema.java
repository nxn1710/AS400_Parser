package com.as400parser.common.analyzer.model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the relational database schema extracted from AS400 DDS files.
 * Physical Files (PF) are mapped to tables, and Logical Files (LF) are mapped to indexes.
 */
public class DatabaseSchema {
    
    /**
     * List of tables extracted from Physical Files (PF).
     */
    @SerializedName("tables")
    private List<TableDefinition> tables = new ArrayList<>();

    /**
     * List of indexes (or views) extracted from Logical Files (LF).
     */
    @SerializedName("indexes")
    private List<IndexDefinition> indexes = new ArrayList<>();

    public List<TableDefinition> getTables() { return tables; }
    public void setTables(List<TableDefinition> tables) { this.tables = tables; }

    public List<IndexDefinition> getIndexes() { return indexes; }
    public void setIndexes(List<IndexDefinition> indexes) { this.indexes = indexes; }
}
