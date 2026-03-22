package com.as400parser.dspf.model;

/**
 * Represents a conditioning indicator from DDS columns 8-16.
 * <p>
 * DSPF uses conditioning indicators extensively to control field/keyword visibility.
 * Each A-spec line has 3 indicator slots (cols 8-10, 11-13, 14-16), each with
 * an optional 'N' prefix (negation) and 2-char indicator number.
 */
public class ConditioningIndicator {

    /** Whether the indicator is negated ('N' prefix, e.g., N60). */
    private boolean not;

    /** Indicator number or code (e.g., "60", "03", "40"). */
    private String indicator;

    public ConditioningIndicator() {}

    public ConditioningIndicator(boolean not, String indicator) {
        this.not = not;
        this.indicator = indicator;
    }

    // --- Getters & Setters ---

    public boolean isNot() {
        return not;
    }

    public void setNot(boolean not) {
        this.not = not;
    }

    public String getIndicator() {
        return indicator;
    }

    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }
}
