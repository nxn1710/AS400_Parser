package com.as400parser.rpg3.model;

/** Indicator reference (*INxx). */
public class IndicatorNode extends ExpressionNode {
    private String indicator;  // "03", "LR", "H1", etc.
    private String category;   // "numeric", "control", "halt", "overflow", etc.

    public IndicatorNode() { super("indicator"); }
    public IndicatorNode(String indicator, String category) {
        super("indicator");
        this.indicator = indicator;
        this.category = category;
    }

    public String getIndicator() { return indicator; }
    public void setIndicator(String indicator) { this.indicator = indicator; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
