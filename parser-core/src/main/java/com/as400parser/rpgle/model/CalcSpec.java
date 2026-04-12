package com.as400parser.rpgle.model;

import com.as400parser.common.model.Location;
import com.as400parser.rpg3.model.ExpressionNode;

import java.util.ArrayList;
import java.util.List;

/**
 * RPGLE C-spec (Calculation) specification model.
 * <p>
 * Reuses RPG3 CalcSpec node hierarchy for IR compatibility.
 * Adds RPGLE-specific operations: EVAL, EVALR, CALLP, FOR, MONITOR, etc.
 * <p>
 * Fixed-format C-spec layout:
 *   cols 7-8:   Control level indicator (L0-L9, LR, SR)
 *   cols 9-11:  Conditioning indicators (N01-N99, etc.)
 *   cols 12-25: Factor 1
 *   cols 26-35: Operation code + extender
 *   cols 36-49: Factor 2 (traditional) or cols 36-80: Extended factor 2
 *   cols 50-63: Result field
 *   cols 64-68: Field length
 *   cols 69-70: Decimal positions
 *   cols 71-76: Resulting indicators (HI/LO/EQ)
 * <p>
 * Discriminated by {@code nodeType} field for JSON serialization.
 */
public class CalcSpec {

    // ---- Common base for all calc nodes ----

    public static abstract class CalcNode {
        protected String nodeType;
        protected String rawSourceLine;
        protected Location location;
        protected String parseQuality = "full";
        protected String sourceSequence;
        protected String controlLevel;
        protected List<ConditioningIndicator> conditioningIndicators = new ArrayList<>();
        protected String inlineComment;
        protected String format;  // "fixed" or "free"

        protected CalcNode(String nodeType) {
            this.nodeType = nodeType;
        }

        public String getNodeType() { return nodeType; }
        public String getRawSourceLine() { return rawSourceLine; }
        public void setRawSourceLine(String s) { this.rawSourceLine = s != null ? s.stripTrailing() : null; }
        public Location getLocation() { return location; }
        public void setLocation(Location l) { this.location = l; }
        public String getParseQuality() { return parseQuality; }
        public void setParseQuality(String q) { this.parseQuality = q; }
        public String getSourceSequence() { return sourceSequence; }
        public void setSourceSequence(String v) { this.sourceSequence = v; }
        public String getControlLevel() { return controlLevel; }
        public void setControlLevel(String v) { this.controlLevel = v; }
        public List<ConditioningIndicator> getConditioningIndicators() { return conditioningIndicators; }
        public void setConditioningIndicators(List<ConditioningIndicator> v) { this.conditioningIndicators = v; }
        public String getInlineComment() { return inlineComment; }
        public void setInlineComment(String v) { this.inlineComment = v; }
        public String getFormat() { return format; }
        public void setFormat(String v) { this.format = v; }
    }

    // ---- Flat operation (most C-spec lines) ----

    public static class Operation extends CalcNode {
        private String opcode;
        private String extendedOpcode;
        private ExpressionNode factor1;
        private ExpressionNode factor2;
        private String extendedFactor2;  // RPGLE extended factor 2 (cols 36-80)
        private ExpressionNode resultField;
        private Integer fieldLength;
        private Integer decimalPositions;
        private ResultingIndicators resultingIndicators;

        public Operation() { super("operation"); }

        public String getOpcode() { return opcode; }
        public void setOpcode(String v) { this.opcode = v; }
        public String getExtendedOpcode() { return extendedOpcode; }
        public void setExtendedOpcode(String v) { this.extendedOpcode = v; }
        public ExpressionNode getFactor1() { return factor1; }
        public void setFactor1(ExpressionNode v) { this.factor1 = v; }
        public ExpressionNode getFactor2() { return factor2; }
        public void setFactor2(ExpressionNode v) { this.factor2 = v; }
        public String getExtendedFactor2() { return extendedFactor2; }
        public void setExtendedFactor2(String v) { this.extendedFactor2 = v; }
        public ExpressionNode getResultField() { return resultField; }
        public void setResultField(ExpressionNode v) { this.resultField = v; }
        public Integer getFieldLength() { return fieldLength; }
        public void setFieldLength(Integer v) { this.fieldLength = v; }
        public Integer getDecimalPositions() { return decimalPositions; }
        public void setDecimalPositions(Integer v) { this.decimalPositions = v; }
        public ResultingIndicators getResultingIndicators() { return resultingIndicators; }
        public void setResultingIndicators(ResultingIndicators v) { this.resultingIndicators = v; }
    }

    // ---- Control flow blocks ----

    public static class ConditionalBlock extends CalcNode {
        private String comparisonType;
        private ExpressionNode condition;
        private ExpressionNode comparisonValue;
        private String freeExpression;  // free-format IF expression
        private List<Object> thenOps = new ArrayList<>();
        private List<Object> elseOps = new ArrayList<>();
        private Operation endStatement;

        public ConditionalBlock() { super("conditionalBlock"); }

        public String getComparisonType() { return comparisonType; }
        public void setComparisonType(String v) { this.comparisonType = v; }
        public ExpressionNode getCondition() { return condition; }
        public void setCondition(ExpressionNode v) { this.condition = v; }
        public ExpressionNode getComparisonValue() { return comparisonValue; }
        public void setComparisonValue(ExpressionNode v) { this.comparisonValue = v; }
        public String getFreeExpression() { return freeExpression; }
        public void setFreeExpression(String v) { this.freeExpression = v; }
        public List<Object> getThenOps() { return thenOps; }
        public void setThenOps(List<Object> v) { this.thenOps = v; }
        public List<Object> getElseOps() { return elseOps; }
        public void setElseOps(List<Object> v) { this.elseOps = v; }
        public Operation getEndStatement() { return endStatement; }
        public void setEndStatement(Operation v) { this.endStatement = v; }
    }

    public static class DoWhileBlock extends CalcNode {
        private String comparisonType;
        private ExpressionNode condition;
        private ExpressionNode comparisonValue;
        private String freeExpression;
        private List<Object> bodyOps = new ArrayList<>();
        private Operation endStatement;

        public DoWhileBlock() { super("doWhileBlock"); }

        public String getComparisonType() { return comparisonType; }
        public void setComparisonType(String v) { this.comparisonType = v; }
        public ExpressionNode getCondition() { return condition; }
        public void setCondition(ExpressionNode v) { this.condition = v; }
        public ExpressionNode getComparisonValue() { return comparisonValue; }
        public void setComparisonValue(ExpressionNode v) { this.comparisonValue = v; }
        public String getFreeExpression() { return freeExpression; }
        public void setFreeExpression(String v) { this.freeExpression = v; }
        public List<Object> getBodyOps() { return bodyOps; }
        public void setBodyOps(List<Object> v) { this.bodyOps = v; }
        public Operation getEndStatement() { return endStatement; }
        public void setEndStatement(Operation v) { this.endStatement = v; }
    }

    public static class DoUntilBlock extends CalcNode {
        private String comparisonType;
        private ExpressionNode condition;
        private ExpressionNode comparisonValue;
        private String freeExpression;
        private List<Object> bodyOps = new ArrayList<>();
        private Operation endStatement;

        public DoUntilBlock() { super("doUntilBlock"); }

        public String getComparisonType() { return comparisonType; }
        public void setComparisonType(String v) { this.comparisonType = v; }
        public ExpressionNode getCondition() { return condition; }
        public void setCondition(ExpressionNode v) { this.condition = v; }
        public ExpressionNode getComparisonValue() { return comparisonValue; }
        public void setComparisonValue(ExpressionNode v) { this.comparisonValue = v; }
        public String getFreeExpression() { return freeExpression; }
        public void setFreeExpression(String v) { this.freeExpression = v; }
        public List<Object> getBodyOps() { return bodyOps; }
        public void setBodyOps(List<Object> v) { this.bodyOps = v; }
        public Operation getEndStatement() { return endStatement; }
        public void setEndStatement(Operation v) { this.endStatement = v; }
    }

    public static class ForBlock extends CalcNode {
        private String indexVariable;
        private String startExpression;
        private String endExpression;
        private String incrementExpression;
        private List<Object> bodyOps = new ArrayList<>();
        private Operation endStatement;

        public ForBlock() { super("forBlock"); }

        public String getIndexVariable() { return indexVariable; }
        public void setIndexVariable(String v) { this.indexVariable = v; }
        public String getStartExpression() { return startExpression; }
        public void setStartExpression(String v) { this.startExpression = v; }
        public String getEndExpression() { return endExpression; }
        public void setEndExpression(String v) { this.endExpression = v; }
        public String getIncrementExpression() { return incrementExpression; }
        public void setIncrementExpression(String v) { this.incrementExpression = v; }
        public List<Object> getBodyOps() { return bodyOps; }
        public void setBodyOps(List<Object> v) { this.bodyOps = v; }
        public Operation getEndStatement() { return endStatement; }
        public void setEndStatement(Operation v) { this.endStatement = v; }
    }

    public static class SelectBlock extends CalcNode {
        private List<WhenClause> whenClauses = new ArrayList<>();
        private List<Object> otherOps = new ArrayList<>();
        private Operation endStatement;

        public SelectBlock() { super("selectBlock"); }

        public List<WhenClause> getWhenClauses() { return whenClauses; }
        public void setWhenClauses(List<WhenClause> v) { this.whenClauses = v; }
        public List<Object> getOtherOps() { return otherOps; }
        public void setOtherOps(List<Object> v) { this.otherOps = v; }
        public Operation getEndStatement() { return endStatement; }
        public void setEndStatement(Operation v) { this.endStatement = v; }
    }

    public static class WhenClause {
        private String comparisonType;
        private ExpressionNode condition;
        private ExpressionNode comparisonValue;
        private String freeExpression;
        private List<Object> bodyOps = new ArrayList<>();
        private Location location;

        public String getComparisonType() { return comparisonType; }
        public void setComparisonType(String v) { this.comparisonType = v; }
        public ExpressionNode getCondition() { return condition; }
        public void setCondition(ExpressionNode v) { this.condition = v; }
        public ExpressionNode getComparisonValue() { return comparisonValue; }
        public void setComparisonValue(ExpressionNode v) { this.comparisonValue = v; }
        public String getFreeExpression() { return freeExpression; }
        public void setFreeExpression(String v) { this.freeExpression = v; }
        public List<Object> getBodyOps() { return bodyOps; }
        public void setBodyOps(List<Object> v) { this.bodyOps = v; }
        public Location getLocation() { return location; }
        public void setLocation(Location l) { this.location = l; }
    }

    public static class MonitorBlock extends CalcNode {
        private List<Object> bodyOps = new ArrayList<>();
        private List<OnErrorClause> onErrorClauses = new ArrayList<>();
        private Operation endStatement;

        public MonitorBlock() { super("monitorBlock"); }

        public List<Object> getBodyOps() { return bodyOps; }
        public void setBodyOps(List<Object> v) { this.bodyOps = v; }
        public List<OnErrorClause> getOnErrorClauses() { return onErrorClauses; }
        public void setOnErrorClauses(List<OnErrorClause> v) { this.onErrorClauses = v; }
        public Operation getEndStatement() { return endStatement; }
        public void setEndStatement(Operation v) { this.endStatement = v; }
    }

    public static class OnErrorClause {
        private String errorCodes;
        private List<Object> bodyOps = new ArrayList<>();
        private Location location;

        public String getErrorCodes() { return errorCodes; }
        public void setErrorCodes(String v) { this.errorCodes = v; }
        public List<Object> getBodyOps() { return bodyOps; }
        public void setBodyOps(List<Object> v) { this.bodyOps = v; }
        public Location getLocation() { return location; }
        public void setLocation(Location l) { this.location = l; }
    }

    public static class SubroutineBlock extends CalcNode {
        private String subroutineName;
        private List<Object> operations = new ArrayList<>();
        private Operation endStatement;
        private List<Integer> calledFrom = new ArrayList<>();

        public SubroutineBlock() { super("subroutineBlock"); }

        public String getSubroutineName() { return subroutineName; }
        public void setSubroutineName(String v) { this.subroutineName = v; }
        public List<Object> getOperations() { return operations; }
        public void setOperations(List<Object> v) { this.operations = v; }
        public Operation getEndStatement() { return endStatement; }
        public void setEndStatement(Operation v) { this.endStatement = v; }
        public List<Integer> getCalledFrom() { return calledFrom; }
        public void setCalledFrom(List<Integer> v) { this.calledFrom = v; }
    }

    // ---- Supporting types (reuse RPG3 pattern) ----

    public static class ConditioningIndicator {
        private boolean negated;
        private String indicator;
        private String position;

        public ConditioningIndicator() {}
        public ConditioningIndicator(boolean negated, String indicator, String position) {
            this.negated = negated;
            this.indicator = indicator;
            this.position = position;
        }

        public boolean isNegated() { return negated; }
        public void setNegated(boolean v) { this.negated = v; }
        public String getIndicator() { return indicator; }
        public void setIndicator(String v) { this.indicator = v; }
        public String getPosition() { return position; }
        public void setPosition(String v) { this.position = v; }
    }

    public static class ResultingIndicators {
        private IndicatorRef high;
        private IndicatorRef low;
        private IndicatorRef equal;

        public ResultingIndicators() {}
        public ResultingIndicators(IndicatorRef high, IndicatorRef low, IndicatorRef equal) {
            this.high = high;
            this.low = low;
            this.equal = equal;
        }

        public IndicatorRef getHigh() { return high; }
        public void setHigh(IndicatorRef v) { this.high = v; }
        public IndicatorRef getLow() { return low; }
        public void setLow(IndicatorRef v) { this.low = v; }
        public IndicatorRef getEqual() { return equal; }
        public void setEqual(IndicatorRef v) { this.equal = v; }
    }

    public static class IndicatorRef {
        private String name;
        private String type;
        private String meaning;

        public IndicatorRef() {}
        public IndicatorRef(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() { return name; }
        public void setName(String v) { this.name = v; }
        public String getType() { return type; }
        public void setType(String v) { this.type = v; }
        public String getMeaning() { return meaning; }
        public void setMeaning(String v) { this.meaning = v; }
    }
}
