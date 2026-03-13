package com.as400parser.rpg3.model;

/** Figurative constant (*BLANKS, *ZEROS, *HIVAL, *LOVAL, *ALL, *ON, *OFF). */
public class FigurativeConstantNode extends ExpressionNode {
    private String constant;

    public FigurativeConstantNode() { super("figurativeConstant"); }
    public FigurativeConstantNode(String constant) {
        super("figurativeConstant");
        this.constant = constant;
    }

    public String getConstant() { return constant; }
    public void setConstant(String constant) { this.constant = constant; }
}
