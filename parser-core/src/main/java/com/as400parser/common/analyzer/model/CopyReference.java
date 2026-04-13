package com.as400parser.common.analyzer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a reference to an included Copy member.
 */
public class CopyReference {
    
    /**
     * The name or identifier of the included Copy member.
     */
    @SerializedName("memberName")
    private String memberName;

    /**
     * The file or location the Copy member resolves to.
     */
    @SerializedName("resolvedPath")
    private String resolvedPath;

    /**
     * True if the copy member was successfully resolved by the parser.
     */
    @SerializedName("resolved")
    private boolean resolved;

    public CopyReference() {}

    public CopyReference(String memberName, String resolvedPath, boolean resolved) {
        this.memberName = memberName;
        this.resolvedPath = resolvedPath;
        this.resolved = resolved;
    }

    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    
    public String getResolvedPath() { return resolvedPath; }
    public void setResolvedPath(String resolvedPath) { this.resolvedPath = resolvedPath; }
}
