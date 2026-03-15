package com.as400parser.rpg3.model;

import com.as400parser.common.model.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * C-spec calculation specification models.
 * <p>
 * {@code calculationSpecs} is a polymorphic list — each entry is one of:
 * {@link Operation}, {@link ConditionalBlock}, {@link DoWhileBlock},
 * {@link DoUntilBlock}, {@link DoBlock}, {@link CaseBlock},
 * {@link SubroutineBlock}, {@link LabelNode}, {@link GotoNode},
 * {@link CallSubroutine}.
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
        protected String controlLevel;
        protected List<ConditioningIndicator> conditioningIndicators = new ArrayList<>();
        protected String inlineComment;

        protected CalcNode(String nodeType) {
            this.nodeType = nodeType;
        }

        public String getNodeType() { return nodeType; }
        public String getRawSourceLine() { return rawSourceLine; }
        public void setRawSourceLine(String s) { this.rawSourceLine = s; }
        public Location getLocation() { return location; }
        public void setLocation(Location l) { this.location = l; }
        public String getParseQuality() { return parseQuality; }
        public void setParseQuality(String q) { this.parseQuality = q; }
        public String getControlLevel() { return controlLevel; }
        public void setControlLevel(String v) { this.controlLevel = v; }
        public List<ConditioningIndicator> getConditioningIndicators() { return conditioningIndicators; }
        public void setConditioningIndicators(List<ConditioningIndicator> v) { this.conditioningIndicators = v; }
        public String getInlineComment() { return inlineComment; }
        public void setInlineComment(String v) { this.inlineComment = v; }
    }

    // ---- Flat operation (most C-spec lines) ----

    public static class Operation extends CalcNode {
        private String opcode;
        private String extendedOpcode;  // H, N, P, or ""
        private ExpressionNode factor1;
        private ExpressionNode factor2;
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
        private String comparisonType;   // GT, LT, EQ, NE, GE, LE — or null if compound
        private ExpressionNode condition;
        private ExpressionNode comparisonValue;
        private List<Object> thenOps = new ArrayList<>();
        private List<Object> elseOps = new ArrayList<>();

        public ConditionalBlock() { super("conditionalBlock"); }

        public String getComparisonType() { return comparisonType; }
        public void setComparisonType(String v) { this.comparisonType = v; }
        public ExpressionNode getCondition() { return condition; }
        public void setCondition(ExpressionNode v) { this.condition = v; }
        public ExpressionNode getComparisonValue() { return comparisonValue; }
        public void setComparisonValue(ExpressionNode v) { this.comparisonValue = v; }
        public List<Object> getThenOps() { return thenOps; }
        public void setThenOps(List<Object> v) { this.thenOps = v; }
        public List<Object> getElseOps() { return elseOps; }
        public void setElseOps(List<Object> v) { this.elseOps = v; }
    }

    public static class DoWhileBlock extends CalcNode {
        private String comparisonType;
        private ExpressionNode condition;
        private ExpressionNode comparisonValue;
        private List<Object> bodyOps = new ArrayList<>();

        public DoWhileBlock() { super("doWhileBlock"); }

        public String getComparisonType() { return comparisonType; }
        public void setComparisonType(String v) { this.comparisonType = v; }
        public ExpressionNode getCondition() { return condition; }
        public void setCondition(ExpressionNode v) { this.condition = v; }
        public ExpressionNode getComparisonValue() { return comparisonValue; }
        public void setComparisonValue(ExpressionNode v) { this.comparisonValue = v; }
        public List<Object> getBodyOps() { return bodyOps; }
        public void setBodyOps(List<Object> v) { this.bodyOps = v; }
    }

    public static class DoUntilBlock extends CalcNode {
        private String comparisonType;
        private ExpressionNode condition;
        private ExpressionNode comparisonValue;
        private List<Object> bodyOps = new ArrayList<>();

        public DoUntilBlock() { super("doUntilBlock"); }

        public String getComparisonType() { return comparisonType; }
        public void setComparisonType(String v) { this.comparisonType = v; }
        public ExpressionNode getCondition() { return condition; }
        public void setCondition(ExpressionNode v) { this.condition = v; }
        public ExpressionNode getComparisonValue() { return comparisonValue; }
        public void setComparisonValue(ExpressionNode v) { this.comparisonValue = v; }
        public List<Object> getBodyOps() { return bodyOps; }
        public void setBodyOps(List<Object> v) { this.bodyOps = v; }
    }

    public static class DoBlock extends CalcNode {
        private ExpressionNode startValue;
        private ExpressionNode endValue;
        private ExpressionNode indexVariable;
        private List<Object> bodyOps = new ArrayList<>();

        public DoBlock() { super("doBlock"); }

        public ExpressionNode getStartValue() { return startValue; }
        public void setStartValue(ExpressionNode v) { this.startValue = v; }
        public ExpressionNode getEndValue() { return endValue; }
        public void setEndValue(ExpressionNode v) { this.endValue = v; }
        public ExpressionNode getIndexVariable() { return indexVariable; }
        public void setIndexVariable(ExpressionNode v) { this.indexVariable = v; }
        public List<Object> getBodyOps() { return bodyOps; }
        public void setBodyOps(List<Object> v) { this.bodyOps = v; }
    }

    public static class CaseBlock extends CalcNode {
        private List<CaseEntry> entries = new ArrayList<>();

        public CaseBlock() { super("caseBlock"); }

        public List<CaseEntry> getEntries() { return entries; }
        public void setEntries(List<CaseEntry> v) { this.entries = v; }
    }

    public static class CaseEntry {
        private String comparisonType; // EQ, NE, LE, LT, GE, GT, or null for default CAS
        private ExpressionNode condition;
        private ExpressionNode comparisonValue;
        private ExpressionNode subroutineName; // result field = subroutine to call
        private ResultingIndicators resultingIndicators;
        private Location location;

        public String getComparisonType() { return comparisonType; }
        public void setComparisonType(String v) { this.comparisonType = v; }
        public ExpressionNode getCondition() { return condition; }
        public void setCondition(ExpressionNode v) { this.condition = v; }
        public ExpressionNode getComparisonValue() { return comparisonValue; }
        public void setComparisonValue(ExpressionNode v) { this.comparisonValue = v; }
        public ExpressionNode getSubroutineName() { return subroutineName; }
        public void setSubroutineName(ExpressionNode v) { this.subroutineName = v; }
        public ResultingIndicators getResultingIndicators() { return resultingIndicators; }
        public void setResultingIndicators(ResultingIndicators v) { this.resultingIndicators = v; }
        public Location getLocation() { return location; }
        public void setLocation(Location l) { this.location = l; }
    }

    public static class SubroutineBlock extends CalcNode {
        private String subroutineName;
        private List<Object> operations = new ArrayList<>();

        public SubroutineBlock() { super("subroutineBlock"); }

        public String getSubroutineName() { return subroutineName; }
        public void setSubroutineName(String v) { this.subroutineName = v; }
        public List<Object> getOperations() { return operations; }
        public void setOperations(List<Object> v) { this.operations = v; }
    }

    public static class LabelNode extends CalcNode {
        private String labelName;

        public LabelNode() { super("labelNode"); }

        public String getLabelName() { return labelName; }
        public void setLabelName(String v) { this.labelName = v; }
    }

    public static class GotoNode extends CalcNode {
        private String targetLabel;

        public GotoNode() { super("gotoNode"); }

        public String getTargetLabel() { return targetLabel; }
        public void setTargetLabel(String v) { this.targetLabel = v; }
    }

    public static class CallSubroutine extends CalcNode {
        private String subroutineName;

        public CallSubroutine() { super("callSubroutine"); }

        public String getSubroutineName() { return subroutineName; }
        public void setSubroutineName(String v) { this.subroutineName = v; }
    }

    // ---- Supporting types ----

    public static class ConditioningIndicator {
        private boolean negated;
        private String indicator;
        private String position; // "first", "second", "third"

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

    /** Typed indicator reference with classification. */
    public static class IndicatorRef {
        private String name;     // "03", "LR", "H1", "L2", "OA"
        private String type;     // "numeric", "special", "halt", "level", "overflow"
        private String meaning;  // "recordNotFound", "error", "eof", "plus", "minus", "zero", etc.

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
