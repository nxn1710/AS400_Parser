package com.as400parser.rpg3.model;

/** Unary operation (e.g., NOT). */
public class UnaryOpNode extends ExpressionNode {
    private String operator;
    private ExpressionNode operand;

    public UnaryOpNode() { super("unaryOp"); }
    public UnaryOpNode(String operator, ExpressionNode operand) {
        super("unaryOp");
        this.operator = operator;
        this.operand = operand;
    }

    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
    public ExpressionNode getOperand() { return operand; }
    public void setOperand(ExpressionNode operand) { this.operand = operand; }
}
