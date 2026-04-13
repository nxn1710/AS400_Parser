package com.as400parser.common.analyzer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Defines a key field for an Index or Table, including its sort order.
 */
public class KeyFieldDef {
    
    /**
     * The name of the field that constructs part of the key.
     */
    @SerializedName("fieldName")
    private String fieldName;

    /**
     * The sort order of the key field (e.g., "ASC" or "DESC").
     */
    @SerializedName("sortOrder")
    private String sortOrder;

    public KeyFieldDef() {}

    public KeyFieldDef(String fieldName, String sortOrder) {
        this.fieldName = fieldName;
        this.sortOrder = sortOrder;
    }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }
    public String getSortOrder() { return sortOrder; }
    public void setSortOrder(String sortOrder) { this.sortOrder = sortOrder; }
}
