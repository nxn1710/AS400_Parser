package com.as400parser.rpg3.model;

/** Binary operation (comparisons, AND/OR compound conditions). */
public class BinaryOpNode extends ExpressionNode {
    private String operator;  // GT, LT, EQ, NE, GE, LE, AND, OR
    private ExpressionNode left;
    private ExpressionNode right;

    public BinaryOpNode() { super("binaryOp"); }
    public BinaryOpNode(String operator, ExpressionNode left, ExpressionNode right) {
        super("binaryOp");
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
    public ExpressionNode getLeft() { return left; }
    public void setLeft(ExpressionNode left) { this.left = left; }
    public ExpressionNode getRight() { return right; }
    public void setRight(ExpressionNode right) { this.right = right; }
}
