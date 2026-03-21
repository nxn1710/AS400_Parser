package com.as400parser.common.parser;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Configuration options for parsing.
 */
public class ParseOptions {

    private List<String> copyPaths;
    private String sourceRoot;
    private boolean resolveCopies;
    private Charset charset;
    private int[] tabStops;

    private ParseOptions(Builder builder) {
        this.copyPaths = builder.copyPaths;
        this.sourceRoot = builder.sourceRoot;
        this.resolveCopies = builder.resolveCopies;
        this.charset = builder.charset;
        this.tabStops = builder.tabStops;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static ParseOptions defaults() {
        return builder().build();
    }

    public List<String> getCopyPaths() {
        return copyPaths;
    }

    public String getSourceRoot() {
        return sourceRoot;
    }

    public boolean isResolveCopies() {
        return resolveCopies;
    }

    public Charset getCharset() {
        return charset;
    }

    public int[] getTabStops() {
        return tabStops;
    }

    public static class Builder {
        private List<String> copyPaths = List.of();
        private String sourceRoot = ".";
        private boolean resolveCopies = true;
        private Charset charset = null; // null = auto-detect encoding
        private int[] tabStops = new int[0];

        public Builder copyPaths(List<String> copyPaths) {
            this.copyPaths = copyPaths;
            return this;
        }

        public Builder sourceRoot(String sourceRoot) {
            this.sourceRoot = sourceRoot;
            return this;
        }

        public Builder resolveCopies(boolean resolveCopies) {
            this.resolveCopies = resolveCopies;
            return this;
        }

        public Builder charset(Charset charset) {
            this.charset = charset;
            return this;
        }

        public Builder tabStops(int[] tabStops) {
            this.tabStops = tabStops;
            return this;
        }

        public ParseOptions build() {
            return new ParseOptions(this);
        }
    }
}
