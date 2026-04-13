package com.as400parser.common.analyzer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Defines a rule that filters the dataset accessible by an index/logical-file.
 */
public class SelectOmitRule {
    
    /**
     * Identifies whether this is a "SELECT" ('S') or "OMIT" ('O') instruction.
     */
    @SerializedName("type")
    private String type;

    /**
     * The field name evaluated by this rule.
     */
    @SerializedName("fieldName")
    private String fieldName;

    /**
     * The condition expected (e.g., COMP, RANGE, VALUES).
     */
    @SerializedName("condition")
    private String condition;

    /**
     * The specific value(s) the condition is tested against.
     */
    @SerializedName("value")
    private String value;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}
