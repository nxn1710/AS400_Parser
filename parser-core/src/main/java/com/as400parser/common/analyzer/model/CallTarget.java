package com.as400parser.common.analyzer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Defines a programmatic invocation (edge) in the cross-reference connection graph.
 */
public class CallTarget {
    
    /**
     * The name or identifier of the program/procedure being called.
     */
    @SerializedName("targetName")
    private String targetName;

    /**
     * The type mechanism of the call (e.g., 'CALL', 'CALLP', 'CALLB', 'EXSR').
     */
    @SerializedName("callType")
    private String callType;

    /**
     * The original source line or context where this call originated.
     */
    @SerializedName("sourceContext")
    private String sourceContext;

    public CallTarget() {}

    public CallTarget(String targetName, String callType, String sourceContext) {
        this.targetName = targetName;
        this.callType = callType;
        this.sourceContext = sourceContext;
    }

    public String getTargetName() { return targetName; }
    public void setTargetName(String targetName) { this.targetName = targetName; }

    public String getCallType() { return callType; }
    public void setCallType(String callType) { this.callType = callType; }
}
