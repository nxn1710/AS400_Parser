package com.as400parser.common.analyzer.model;

import com.google.gson.annotations.SerializedName;
import java.util.HashSet;
import java.util.Set;

/**
 * Details how a specific program interacts with an individual Database or Display file.
 */
public class FileUsage {
    
    /**
     * The physical/logical or display file name.
     */
    @SerializedName("fileName")
    private String fileName;

    /**
     * The high-level access type established on File Spec/DCL-F.
     * Common values: INPUT, OUTPUT, UPDATE, COMBINE.
     */
    @SerializedName("accessMode")
    private String accessMode;

    /**
     * Tracked specific input/output opcodes executed against the file in C-specs
     * (e.g. READ, WRITE, CHAIN, DELETE).
     */
    @SerializedName("operations")
    private Set<IOOperation> operations = new HashSet<>();

    public FileUsage() {}

    public FileUsage(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getAccessMode() { return accessMode; }
    public void setAccessMode(String accessMode) { this.accessMode = accessMode; }

    public Set<IOOperation> getOperations() { return operations; }
    public void setOperations(Set<IOOperation> operations) { this.operations = operations; }
}
