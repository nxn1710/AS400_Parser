package com.as400parser.common.normalizer;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

/**
 * Result of source normalization.
 * <p>
 * Contains the normalized lines (at least 80 characters, short lines padded),
 * original line number mapping, extracted sequence numbers, and any
 * warnings generated during normalization.
 * <p>
 * Lines longer than 80 characters are preserved in full — the parser can
 * access columns beyond 80 directly (e.g. for spec-type-specific comments).
 */
public class NormalizedSource {

    private final String[] lines;
    private final int[] originalLineNumbers;
    private final String[] sequenceNumbers;
    private final List<NormalizationWarning> warnings;
    private Charset detectedCharset;

    public NormalizedSource(String[] lines, int[] originalLineNumbers,
                           String[] sequenceNumbers, List<NormalizationWarning> warnings) {
        this.lines = lines;
        this.originalLineNumbers = originalLineNumbers;
        this.sequenceNumbers = sequenceNumbers;
        this.warnings = Collections.unmodifiableList(warnings);
    }

    /** Normalized lines, at least 80 characters (right-padded). Lines longer than 80 are kept in full. */
    public String[] getLines() {
        return lines;
    }

    /** 1-based original line numbers for each normalized line. */
    public int[] getOriginalLineNumbers() {
        return originalLineNumbers;
    }

    /** Sequence numbers extracted from columns 1-5 of each line. */
    public String[] getSequenceNumbers() {
        return sequenceNumbers;
    }

    /** Warnings generated during normalization. */
    public List<NormalizationWarning> getWarnings() {
        return warnings;
    }

    /** Number of normalized lines. */
    public int getLineCount() {
        return lines.length;
    }

    /** The charset used to decode the source file (null if parsed from string). */
    public Charset getDetectedCharset() {
        return detectedCharset;
    }

    public void setDetectedCharset(Charset detectedCharset) {
        this.detectedCharset = detectedCharset;
    }
}

