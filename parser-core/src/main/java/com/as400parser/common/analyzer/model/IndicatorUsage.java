package com.as400parser.common.analyzer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Details the usage of an AS400 indicator (e.g. *IN03, *INLR) within a program.
 */
public class IndicatorUsage {
    
    /**
     * The indicator name (e.g., "03", "LR", "50", "99").
     */
    @SerializedName("indicator")
    private String indicator;

    /**
     * The type of usage: "SET" (modified) or "CHECK" (read/conditional).
     */
    @SerializedName("usageType")
    private String usageType;

    /**
     * The location in the source where this usage occurs.
     */
    @SerializedName("specType")
    private String specType; // e.g., "C", "I", "O"

    public IndicatorUsage() {}

    public IndicatorUsage(String indicator, String usageType, String specType) {
        this.indicator = indicator;
        this.usageType = usageType;
        this.specType = specType;
    }

    public String getIndicator() { return indicator; }
    public void setIndicator(String indicator) { this.indicator = indicator; }

    public String getUsageType() { return usageType; }
    public void setUsageType(String usageType) { this.usageType = usageType; }

    public String getSpecType() { return specType; }
    public void setSpecType(String specType) { this.specType = specType; }
}
