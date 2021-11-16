package com.euph28.tson.assertionengine.keyword.assertion;

import com.euph28.tson.assertionengine.TSONAssertionEngine;
import com.euph28.tson.assertionengine.result.AssertionResult;
import com.euph28.tson.context.TSONContext;
import com.euph28.tson.context.provider.JsonValueProvider;
import com.euph28.tson.core.keyword.Keyword;
import com.euph28.tson.interpreter.Statement;
import com.euph28.tson.reporter.ReportType;
import com.euph28.tson.reporter.TSONReporter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Base class of an Assertion Keyword
 */
public abstract class AssertionBase extends Keyword {

    /* ----- VARIABLES ------------------------------ */
    /**
     * Assertion engine for this {@link Keyword}. The engine is used for receiving shared values and returning results
     */
    TSONAssertionEngine tsonAssertionEngine;

    /**
     * Reporter for this {@link Keyword}. Used for reporting PASS/FAIL results
     */
    TSONReporter tsonReporter;

    /**
     * List of assertion results
     */
    private final List<AssertionResult> assertionResultList = new ArrayList<>();

    /* ----- CONSTRUCTOR ------------------------------ */

    /**
     * Base of an Assertion Keyword
     *
     * @param tsonAssertionEngine TSON Assertion Engine that should handle results of this assertion
     */
    AssertionBase(TSONAssertionEngine tsonAssertionEngine) {
        this.tsonAssertionEngine = tsonAssertionEngine;
    }

    /* ----- METHODS: UTILITY ------------------------------ */

    /**
     * Record a pass assertion
     *
     * @param stepDescription   Description of the assertion step that was done. The step is independent of the result
     *                          and is used for manual reproduction of the step
     * @param resultDescription Description on the result of the assertion. This should explain the actual value and
     *                          its accuracy to the expected value
     */
    protected void resultPass(String stepDescription, String resultDescription) {
        assertionResultList.add(new AssertionResult(this, true, resultDescription));
        tsonReporter.doReport(ReportType.PASS, stepDescription, resultDescription);
    }

    /**
     * Record a fail assertion
     *
     * @param stepDescription   Description of the assertion step that was done. The step is independent of the result
     *                          and is used for manual reproduction of the step
     * @param resultDescription Description on the result of the assertion. This should explain the actual value and
     *                          its accuracy to the expected value
     */
    protected void resultFail(String stepDescription, String resultDescription) {
        assertionResultList.add(new AssertionResult(this, false, resultDescription));
        tsonReporter.doReport(ReportType.FAIL, stepDescription, resultDescription);
    }

    /**
     * Retrieve value from a jsonContent and a jsonPath
     *
     * @param tsonContext Context class that stores the variables related to the current running state
     * @param jsonPath    Path to the value. Path is separated by colons (eg: body.item.0.value).
     *                    Wildcards can be used to retrieve all values in an array
     * @return Map of path-to-value of resolved values. Returns {@code null} if path was invalid
     */
    protected Map<String, String> getValueFromJson(TSONContext tsonContext, String jsonPath) {
        JsonValueProvider jsonValueProvider = new JsonValueProvider();
        return jsonValueProvider.getValuesFromJson(tsonContext, jsonPath);
    }

    /* ----- METHODS ------------------------------ */

    /**
     * Handle the assertion based on the provided data. Use {@link #resultPass(String, String)} and {@link #resultFail(String, String)}
     * to report assertion results
     *
     * @param tsonContext Context class that stores the variables related to the current running state
     * @param value       Additional value for this {@link Keyword} provided in the TSON file
     * @return Returns true if handle was successful
     */
    protected abstract boolean handleAssertion(TSONContext tsonContext, String value);

    /* ----- OVERRIDE: KEYWORD BASE ------------------------------ */

    /**
     * Calls {@link #handleAssertion(TSONContext, String)} and returns results to {@link #tsonAssertionEngine}. <br/>
     * Do not override unless you plan to alter how data is returned to {@link #tsonAssertionEngine}
     */
    @Override
    public boolean handle(TSONContext tsonContext, TSONReporter tsonReporter, String value) {
        // Store reporter
        this.tsonReporter = tsonReporter;

        // Reset result list
        assertionResultList.clear();

        // Perform assertion (and populate assertionResultList)
        boolean status = handleAssertion(tsonContext, value);

        // Report result to AssertionEngine
        tsonAssertionEngine.addAssertionResult(assertionResultList);

        // Check if AssertionEngine should publish result (publish if next action is not an assertion)
        Statement nextStatementAction = tsonContext.getTsonInterpreter().peekAction();
        if (nextStatementAction == null || !(nextStatementAction.getKeyword() instanceof AssertionBase)) {
            tsonAssertionEngine.publishCurrentAssertionResult();
        }

        return status;
    }

    @Override
    public boolean isAction() {
        return true;
    }
}
