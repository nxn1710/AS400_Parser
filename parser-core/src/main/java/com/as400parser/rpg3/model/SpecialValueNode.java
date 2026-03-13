package com.as400parser.rpg3.model;

/** Special value (*IN indicator array, UDATE, UMONTH, UDAY, UYEAR, PAGE). */
public class SpecialValueNode extends ExpressionNode {
    private String value;

    public SpecialValueNode() { super("specialValue"); }
    public SpecialValueNode(String value) {
        super("specialValue");
        this.value = value;
    }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}
