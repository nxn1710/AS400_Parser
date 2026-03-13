package com.as400parser.rpg3.model;

/** Array element access (e.g., ARR,X). */
public class ArrayElementNode extends ExpressionNode {
    private String arrayName;
    private ExpressionNode index;

    public ArrayElementNode() { super("arrayElement"); }
    public ArrayElementNode(String arrayName, ExpressionNode index) {
        super("arrayElement");
        this.arrayName = arrayName;
        this.index = index;
    }

    public String getArrayName() { return arrayName; }
    public void setArrayName(String arrayName) { this.arrayName = arrayName; }
    public ExpressionNode getIndex() { return index; }
    public void setIndex(ExpressionNode index) { this.index = index; }
}
