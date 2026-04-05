package com.as400parser.rpgle.model;

import com.as400parser.common.model.Location;

/**
 * Calculation Specification (C-spec).
 * Supports both traditional and extended factor 2 syntax.
 * Positions per ILE RPG Reference.
 * <p>
 * Field names aligned with RPG3 CalcSpec for consistent JSON output.
 */
public class CalcSpec {
    private Location location;
    private String rawSourceLine;
    private boolean extendedFactor2; // true if using extended factor 2 (columns 36-80)

    private String controlLevel;        // 7-8
    private String conditioningIndicators; // 9-11
    private String factor1;             // 12-25
    private String opcode;              // 26-35 operation code only (without extender)
    private String operationExtender;   // operation extender (e.g., H, N, P, E)
    private String operationAndExtender; // 26-35 full raw operation + extender
    private String factor2;             // 36-49 (traditional) or 36-80 (extended)
    private String resultField;         // 50-63
    private String fieldLength;         // 64-68
    private String decimalPositions;    // 69-70
    private String hiIndicator;         // 71-72
    private String loIndicator;         // 73-74
    private String eqIndicator;         // 75-76

    public Location getLocation() { return location; }
    public void setLocation(Location v) { this.location = v; }
    public String getRawSourceLine() { return rawSourceLine; }
    public void setRawSourceLine(String v) { this.rawSourceLine = v; }
    public boolean isExtendedFactor2() { return extendedFactor2; }
    public void setExtendedFactor2(boolean v) { this.extendedFactor2 = v; }

    public String getControlLevel() { return controlLevel; }
    public void setControlLevel(String v) { this.controlLevel = v; }
    public String getConditioningIndicators() { return conditioningIndicators; }
    public void setConditioningIndicators(String v) { this.conditioningIndicators = v; }
    public String getFactor1() { return factor1; }
    public void setFactor1(String v) { this.factor1 = v; }

    /** Operation code without extender (e.g., "EVAL", "CALL"). Aligned with RPG3's opcode field. */
    public String getOpcode() { return opcode; }
    public void setOpcode(String v) { this.opcode = v; }

    /** Operation extender only (e.g., "H", "N", "E"). */
    public String getOperationExtender() { return operationExtender; }
    public void setOperationExtender(String v) { this.operationExtender = v; }

    /** Full raw operation + extender string (e.g., "EVAL(H)"). For backward compatibility. */
    public String getOperationAndExtender() { return operationAndExtender; }
    public void setOperationAndExtender(String v) { this.operationAndExtender = v; }

    /**
     * @deprecated Use {@link #getOpcode()} instead. Kept for backward compatibility.
     */
    @Deprecated
    public String getOperation() { return operationAndExtender; }
    /**
     * @deprecated Use {@link #setOpcode(String)} instead. Kept for backward compatibility.
     */
    @Deprecated
    public void setOperation(String v) { this.operationAndExtender = v; }

    public String getFactor2() { return factor2; }
    public void setFactor2(String v) { this.factor2 = v; }
    public String getResultField() { return resultField; }
    public void setResultField(String v) { this.resultField = v; }
    public String getFieldLength() { return fieldLength; }
    public void setFieldLength(String v) { this.fieldLength = v; }
    public String getDecimalPositions() { return decimalPositions; }
    public void setDecimalPositions(String v) { this.decimalPositions = v; }
    public String getHiIndicator() { return hiIndicator; }
    public void setHiIndicator(String v) { this.hiIndicator = v; }
    public String getLoIndicator() { return loIndicator; }
    public void setLoIndicator(String v) { this.loIndicator = v; }
    public String getEqIndicator() { return eqIndicator; }
    public void setEqIndicator(String v) { this.eqIndicator = v; }
}
