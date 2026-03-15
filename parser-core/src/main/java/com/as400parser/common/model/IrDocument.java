package com.as400parser.common.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Top-level IR document — the output of parsing a source file.
 * Maps to the root of the IR JSON structure.
 */
public class IrDocument {
    private Metadata metadata;
    private Object content;       // Language-specific content (e.g., Rpg3Content)
    private Dependencies dependencies;
    private List<ParseError> errors = new ArrayList<>();

    public IrDocument() {}

    public Metadata getMetadata() { return metadata; }
    public void setMetadata(Metadata metadata) { this.metadata = metadata; }
    public Object getContent() { return content; }
    public void setContent(Object content) { this.content = content; }
    public Dependencies getDependencies() { return dependencies; }
    public void setDependencies(Dependencies dependencies) { this.dependencies = dependencies; }
    public List<ParseError> getErrors() { return errors; }
    public void setErrors(List<ParseError> errors) { this.errors = errors; }

    /**
     * Dependencies section of the IR document.
     * Uses rich DependencyRef objects per the design spec.
     */
    public static class Dependencies {
        private List<DependencyRef> referencedFiles = new ArrayList<>();
        private List<DependencyRef> calledPrograms = new ArrayList<>();
        private List<CopyMemberRef> copyMembers = new ArrayList<>();

        public List<DependencyRef> getReferencedFiles() { return referencedFiles; }
        public void setReferencedFiles(List<DependencyRef> referencedFiles) { this.referencedFiles = referencedFiles; }
        public List<DependencyRef> getCalledPrograms() { return calledPrograms; }
        public void setCalledPrograms(List<DependencyRef> calledPrograms) { this.calledPrograms = calledPrograms; }
        public List<CopyMemberRef> getCopyMembers() { return copyMembers; }
        public void setCopyMembers(List<CopyMemberRef> copyMembers) { this.copyMembers = copyMembers; }
    }

    /**
     * Rich dependency reference with name, type, and source locations.
     */
    public static class DependencyRef {
        private String name;
        private String referenceType;  // "read", "write", "update", "call", etc.
        private List<Location> locations = new ArrayList<>();

        public DependencyRef() {}
        public DependencyRef(String name, String referenceType) {
            this.name = name;
            this.referenceType = referenceType;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getReferenceType() { return referenceType; }
        public void setReferenceType(String referenceType) { this.referenceType = referenceType; }
        public List<Location> getLocations() { return locations; }
        public void setLocations(List<Location> locations) { this.locations = locations; }
    }

    /**
     * Reference to a /COPY member.
     */
    public static class CopyMemberRef {
        private String directive;
        private String memberName;
        private String fileName;
        private String libraryName;
        private boolean resolved;
        private String resolvedPath;
        private Location location;

        public String getDirective() { return directive; }
        public void setDirective(String directive) { this.directive = directive; }
        public String getMemberName() { return memberName; }
        public void setMemberName(String memberName) { this.memberName = memberName; }
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        public String getLibraryName() { return libraryName; }
        public void setLibraryName(String libraryName) { this.libraryName = libraryName; }
        public boolean isResolved() { return resolved; }
        public void setResolved(boolean resolved) { this.resolved = resolved; }
        public String getResolvedPath() { return resolvedPath; }
        public void setResolvedPath(String resolvedPath) { this.resolvedPath = resolvedPath; }
        public Location getLocation() { return location; }
        public void setLocation(Location location) { this.location = location; }
    }
}
