package com.as400parser.rpg3;

import com.as400parser.common.model.ParseError;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ANTLR error listener that collects parse errors with original line mapping.
 * <p>
 * Maps ANTLR's internal line numbers (which refer to the normalized source)
 * back to original source line numbers using the mapping from
 * {@link com.as400parser.common.normalizer.NormalizedSource}.
 */
public class Rpg3ErrorListener extends BaseErrorListener {

    private final int[] originalLineNumbers;
    private final List<ParseError> errors = new ArrayList<>();

    /**
     * @param originalLineNumbers 1-based original line numbers from NormalizedSource.
     *                           Index 0 = original line for normalized line 1, etc.
     */
    public Rpg3ErrorListener(int[] originalLineNumbers) {
        this.originalLineNumbers = originalLineNumbers;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                            int line, int charPositionInLine,
                            String msg, RecognitionException e) {
        int originalLine = mapToOriginalLine(line);
        errors.add(ParseError.error(originalLine, charPositionInLine + 1, msg));
    }

    /**
     * Map ANTLR's 1-based line number to the original source line number.
     */
    private int mapToOriginalLine(int antlrLine) {
        int index = antlrLine - 1;
        if (originalLineNumbers != null && index >= 0 && index < originalLineNumbers.length) {
            return originalLineNumbers[index];
        }
        return antlrLine; // fallback: use as-is
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
