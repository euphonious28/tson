package com.euph28.tson.assertionengine.keyword;

import com.euph28.tson.assertionengine.TSONAssertionEngine;
import com.euph28.tson.context.TSONContext;
import com.euph28.tson.interpreter.keyword.Keyword;

/**
 * Keyword: Assert
 * <p>
 * Groups subsequent assertions into the same report
 */
public class Assert extends Keyword {

    /* ----- VARIABLES ------------------------------ */
    /**
     * Assertion engine for this {@link Keyword} to interact with
     */
    TSONAssertionEngine tsonAssertionEngine;

    /* ----- CONSTRUCTOR ------------------------------ */

    /**
     * Assert block which groups subsequent assertions into the same report
     *
     * @param tsonAssertionEngine TSON Assertion Engine that should handle results of this assertion
     */
    public Assert(TSONAssertionEngine tsonAssertionEngine) {
        this.tsonAssertionEngine = tsonAssertionEngine;
    }

    /* ----- OVERRIDE: Keyword ------------------------------ */
    @Override
    public String getCode() {
        return "ASSERT";
    }

    @Override
    public String getDescriptionShort() {
        return "Group subsequent assertions";
    }

    @Override
    public String getDescriptionLong() {
        return "Groups all subsequent assertions into the same report";
    }

    @Override
    public boolean isAction() {
        return false;
    }

    @Override
    public boolean handle(TSONContext tsonContext, String value) {
        tsonAssertionEngine.publishCurrentAssertionResult();
        tsonAssertionEngine.setCurrentAssertionReportTitle(value);
        return true;
    }
}