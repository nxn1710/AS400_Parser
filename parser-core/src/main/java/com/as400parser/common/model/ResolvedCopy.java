package com.as400parser.common.model;

/**
 * Result of resolving a /COPY directive.
 */
public class ResolvedCopy {
    private boolean found;
    private String resolvedPath;
    private String content;
    private String memberName;
    private String fileName;
    private String libraryName;
    private String qualifiedPath;
    private String warning;

    public ResolvedCopy() {}

    public static ResolvedCopy notFound(String memberName, String warning) {
        ResolvedCopy rc = new ResolvedCopy();
        rc.found = false;
        rc.memberName = memberName;
        rc.warning = warning;
        return rc;
    }

    public static ResolvedCopy found(String resolvedPath, String content,
                                     String memberName, String fileName, String libraryName) {
        ResolvedCopy rc = new ResolvedCopy();
        rc.found = true;
        rc.resolvedPath = resolvedPath;
        rc.content = content;
        rc.memberName = memberName;
        rc.fileName = fileName;
        rc.libraryName = libraryName;
        rc.qualifiedPath = libraryName != null
                ? libraryName + "/" + fileName + "," + memberName
                : fileName + "," + memberName;
        return rc;
    }

    public boolean isFound() { return found; }
    public String getResolvedPath() { return resolvedPath; }
    public String getContent() { return content; }
    public String getMemberName() { return memberName; }
    public String getFileName() { return fileName; }
    public String getLibraryName() { return libraryName; }
    public String getQualifiedPath() { return qualifiedPath; }
    public String getWarning() { return warning; }
}
