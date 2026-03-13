package com.as400parser.rpg3.model;

import com.as400parser.common.model.Location;

/**
 * Base class for all expression nodes in the RPG3 AST.
 * <p>
 * 8 concrete subtypes: IdentifierNode, LiteralNode, ArrayElementNode,
 * BinaryOpNode, UnaryOpNode, IndicatorNode, FigurativeConstantNode, SpecialValueNode.
 */
public abstract class ExpressionNode {
    protected String nodeType;
    protected String rawText;
    protected Location location;

    protected ExpressionNode(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getNodeType() { return nodeType; }
    public String getRawText() { return rawText; }
    public void setRawText(String rawText) { this.rawText = rawText; }
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
}
