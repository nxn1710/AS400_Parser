package com.as400parser.dspf.model;

import com.as400parser.dds.model.DdsKeyword;

import java.util.ArrayList;
import java.util.List;

/**
 * Wraps a {@link DdsKeyword} with its per-keyword conditioning indicators.
 * <p>
 * In DSPF, individual keywords can be conditioned by indicators on preceding lines.
 * For example, {@code A N60 DSPATR(PR)} means the DSPATR(PR) keyword is only active
 * when indicator 60 is OFF. Conditioned keyword-only lines (lines with indicators +
 * keyword but no name/position) are merged into the preceding field/constant's
 * keyword list as ConditionedKeyword entries.
 */
public class ConditionedKeyword {

    /** The keyword itself (reusing unified DdsKeyword from dds.model). */
    private DdsKeyword keyword;

    /** Conditioning indicators for this specific keyword. Empty if unconditional. */
    private List<ConditioningIndicator> conditioningIndicators;

    public ConditionedKeyword() {
        this.conditioningIndicators = new ArrayList<>();
    }

    public ConditionedKeyword(DdsKeyword keyword, List<ConditioningIndicator> conditioningIndicators) {
        this.keyword = keyword;
        this.conditioningIndicators = conditioningIndicators != null ? conditioningIndicators : new ArrayList<>();
    }

    // --- Getters & Setters ---

    public DdsKeyword getKeyword() {
        return keyword;
    }

    public void setKeyword(DdsKeyword keyword) {
        this.keyword = keyword;
    }

    public List<ConditioningIndicator> getConditioningIndicators() {
        return conditioningIndicators;
    }

    public void setConditioningIndicators(List<ConditioningIndicator> conditioningIndicators) {
        this.conditioningIndicators = conditioningIndicators;
    }
}
