package com.as400parser.rpg3.model;

/** Identifier reference (field name). */
public class IdentifierNode extends ExpressionNode {
    private String name;
    private boolean isArray;
    private String dataType;         // A/S/P/B/D/T/Z/G/O
    private Integer length;
    private Integer decimalPositions;

    public IdentifierNode() { super("identifier"); }
    public IdentifierNode(String name) { super("identifier"); this.name = name; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isArray() { return isArray; }
    public void setArray(boolean array) { isArray = array; }
    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }
    public Integer getLength() { return length; }
    public void setLength(Integer length) { this.length = length; }
    public Integer getDecimalPositions() { return decimalPositions; }
    public void setDecimalPositions(Integer decimalPositions) { this.decimalPositions = decimalPositions; }
}
