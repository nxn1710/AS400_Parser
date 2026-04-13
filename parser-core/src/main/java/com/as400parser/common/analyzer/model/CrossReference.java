package com.as400parser.common.analyzer.model;

import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the complete cross-reference information mapped across all files
 * inside an application, storing program calls and file I/O usages.
 */
public class CrossReference {
    
    /**
     * A map describing the graph of program calls. 
     * Key: The name of the calling Program.
     * Value: A list of targets the Program invokes (e.g., via CALL, EXSR, or CALLP).
     */
    @SerializedName("callGraph")
    private Map<String, List<CallTarget>> callGraph = new HashMap<>();

    /**
     * A map describing the usage modes for Database/Display files across Programs.
     * Key: The name of the Program executing the file operations.
     * Value: A list denoting what files are opened and how they are accessed.
     */
    @SerializedName("fileUsage")
    private Map<String, List<FileUsage>> fileUsage = new HashMap<>();

    /**
     * A map detailing the source include directives resolved during parsing.
     * Key: The Program or file encompassing the "/COPY".
     * Value: A list of the included Copy member references.
     */
    @SerializedName("copyMembers")
    private Map<String, List<CopyReference>> copyMembers = new HashMap<>();

    /**
     * A map tracking indicator flow throughout the program logic.
     * Key: The name of the Program.
     * Value: A list of indicator set/check usages.
     */
    @SerializedName("indicatorUsage")
    private Map<String, List<IndicatorUsage>> indicatorUsage = new HashMap<>();

    public Map<String, List<CallTarget>> getCallGraph() { return callGraph; }
    public void setCallGraph(Map<String, List<CallTarget>> callGraph) { this.callGraph = callGraph; }

    public Map<String, List<FileUsage>> getFileUsage() { return fileUsage; }
    public void setFileUsage(Map<String, List<FileUsage>> fileUsage) { this.fileUsage = fileUsage; }

    public Map<String, List<CopyReference>> getCopyMembers() { return copyMembers; }
    public void setCopyMembers(Map<String, List<CopyReference>> copyMembers) { this.copyMembers = copyMembers; }

    public Map<String, List<IndicatorUsage>> getIndicatorUsage() { return indicatorUsage; }
    public void setIndicatorUsage(Map<String, List<IndicatorUsage>> indicatorUsage) { this.indicatorUsage = indicatorUsage; }
}
