package com.as400parser.rpg3.model;

/** Literal value (string or numeric). */
public class LiteralNode extends ExpressionNode {
    private Object value;
    private String dataType;  // "A" for string, "S" for numeric

    public LiteralNode() { super("literal"); }
    public LiteralNode(Object value, String dataType) {
        super("literal");
        this.value = value;
        this.dataType = dataType;
    }

    public Object getValue() { return value; }
    public void setValue(Object value) { this.value = value; }
    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }
}
