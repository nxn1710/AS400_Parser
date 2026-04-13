package com.as400parser.common.analyzer.model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an Index (or SQL View) definition extracted from a Logical File (LF).
 */
public class IndexDefinition {

    /**
     * The name of the index or logical file.
     */
    @SerializedName("name")
    private String name;

    /**
     * The parent table name this index is built upon (extracted from the PFILE keyword).
     */
    @SerializedName("tableName")
    private String tableName;

    /**
     * The index type (e.g., SIMPLE, SELECT_OMIT, JOIN).
     */
    @SerializedName("type")
    private String type;

    /**
     * The set of fields defining the access path and sort order for the index.
     */
    @SerializedName("keyFields")
    private List<KeyFieldDef> keyFields = new ArrayList<>();

    /**
     * Conditions filtering the index structure (extracted from Select/Omit specs).
     */
    @SerializedName("selectOmitRules")
    private List<SelectOmitRule> selectOmitRules = new ArrayList<>();

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public List<KeyFieldDef> getKeyFields() { return keyFields; }
    public void setKeyFields(List<KeyFieldDef> keyFields) { this.keyFields = keyFields; }
    public List<SelectOmitRule> getSelectOmitRules() { return selectOmitRules; }
    public void setSelectOmitRules(List<SelectOmitRule> selectOmitRules) { this.selectOmitRules = selectOmitRules; }
}
