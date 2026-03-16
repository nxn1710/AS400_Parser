package com.as400parser.rpg3;

import com.as400parser.common.model.ParseError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Collects parse errors/warnings with original line mapping.
 * <p>
 * Maps normalized-source line numbers back to original source line numbers
 * using the mapping from
 * {@link com.as400parser.common.normalizer.NormalizedSource}.
 */
public class Rpg3ErrorListener {

    private final int[] originalLineNumbers;
    private final List<ParseError> errors = new ArrayList<>();

    /**
     * @param originalLineNumbers 1-based original line numbers from NormalizedSource.
     *                           Index 0 = original line for normalized line 1, etc.
     */
    public Rpg3ErrorListener(int[] originalLineNumbers) {
        this.originalLineNumbers = originalLineNumbers;
    }

    /**
     * Record a parse error at the given normalized line/column.
     */
    public void addError(int line, int column, String message) {
        int originalLine = mapToOriginalLine(line);
        errors.add(ParseError.error(originalLine, column, message));
    }

    /**
     * Map a normalized line number to the original source line number.
     */
    private int mapToOriginalLine(int normalizedLine) {
        int index = normalizedLine - 1;
        if (originalLineNumbers != null && index >= 0 && index < originalLineNumbers.length) {
            return originalLineNumbers[index];
        }
        return normalizedLine; // fallback: use as-is
    }

    /** Collected parse errors. */
    public List<ParseError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    /** Whether any errors were collected. */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
