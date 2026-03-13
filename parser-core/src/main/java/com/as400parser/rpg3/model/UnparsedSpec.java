package com.as400parser.rpg3.model;

import com.as400parser.common.model.Location;

import java.util.Map;

/**
 * Fallback node for lines that could not be fully parsed.
 * Part of the zero-data-loss strategy.
 * <p>
 * parseQuality will be "columnOnly" or "raw".
 */
public class UnparsedSpec {
    private String nodeType = "unparsedSpec";
    private String rawText;
    private String formType;
    private String parseQuality;  // "columnOnly" or "raw"
    private Map<String, String> columnMap;
    private Location location;
    private String errorMessage;

    public UnparsedSpec() {}

    public String getNodeType() { return nodeType; }
    public String getRawText() { return rawText; }
    public void setRawText(String rawText) { this.rawText = rawText; }
    public String getFormType() { return formType; }
    public void setFormType(String formType) { this.formType = formType; }
    public String getParseQuality() { return parseQuality; }
    public void setParseQuality(String parseQuality) { this.parseQuality = parseQuality; }
    public Map<String, String> getColumnMap() { return columnMap; }
    public void setColumnMap(Map<String, String> columnMap) { this.columnMap = columnMap; }
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
