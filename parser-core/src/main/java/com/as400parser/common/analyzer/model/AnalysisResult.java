package com.as400parser.common.analyzer.model;

import com.google.gson.annotations.SerializedName;

/**
 * The root container for all analysis results generated from the parsed IR.
 * This includes the database schema extracted from DDS files and the cross-reference
 * graph extracted from RPG and CL programs.
 */
public class AnalysisResult {
    
    /**
     * The extracted database schema, containing tables and indexes.
     */
    @SerializedName("schema")
    private DatabaseSchema schema;

    /**
     * The application cross-reference graph, including program calls and file usage.
     */
    @SerializedName("crossReference")
    private CrossReference crossReference;

    public AnalysisResult() {
        this.schema = new DatabaseSchema();
        this.crossReference = new CrossReference();
    }

    public DatabaseSchema getSchema() { return schema; }
    public void setSchema(DatabaseSchema schema) { this.schema = schema; }

    public CrossReference getCrossReference() { return crossReference; }
    public void setCrossReference(CrossReference crossReference) { this.crossReference = crossReference; }
}
