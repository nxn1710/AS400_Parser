package com.as400parser.common.analyzer.model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single column in a relational table, mapped from a DDS field definition.
 */
public class ColumnDefinition {
    
    /**
     * The name of the column (field).
     */
    @SerializedName("name")
    private String name;

    /**
     * The original DDS data type (e.g., A, P, S, B, L, T, Z).
     */
    @SerializedName("ddsType")
    private String ddsType;

    /**
     * The mapped SQL data type (e.g., VARCHAR, NUMERIC, INTEGER, DATE).
     */
    @SerializedName("sqlType")
    private String sqlType;

    /**
     * The length of the field data.
     */
    @SerializedName("length")
    private int length;

    /**
     * For numeric types, the number of decimal positions. Null if not applicable.
     */
    @SerializedName("decimalPositions")
    private Integer decimalPositions;

    /**
     * Indicates whether the column can contain null values.
     */
    @SerializedName("nullable")
    private boolean nullable;

    /**
     * The default value for the column, extracted from the DFT keyword.
     */
    @SerializedName("defaultValue")
    private String defaultValue;

    /**
     * A description of the column, extracted from the TEXT keyword.
     */
    @SerializedName("description")
    private String description;

    /**
     * The column heading for display purposes, extracted from the COLHDG keyword.
     */
    @SerializedName("columnHeading")
    private String columnHeading;

    /**
     * A list of constraints on the column, extracted from the VALUES or CHECK keywords.
     */
    @SerializedName("constraints")
    private List<String> constraints = new ArrayList<>();

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDdsType() { return ddsType; }
    public void setDdsType(String ddsType) { this.ddsType = ddsType; }
    public String getSqlType() { return sqlType; }
    public void setSqlType(String sqlType) { this.sqlType = sqlType; }
    public int getLength() { return length; }
    public void setLength(int length) { this.length = length; }
    public Integer getDecimalPositions() { return decimalPositions; }
    public void setDecimalPositions(Integer decimalPositions) { this.decimalPositions = decimalPositions; }
    public boolean isNullable() { return nullable; }
    public void setNullable(boolean nullable) { this.nullable = nullable; }
    public String getDefaultValue() { return defaultValue; }
    public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getColumnHeading() { return columnHeading; }
    public void setColumnHeading(String columnHeading) { this.columnHeading = columnHeading; }
    public List<String> getConstraints() { return constraints; }
    public void setConstraints(List<String> constraints) { this.constraints = constraints; }
}
