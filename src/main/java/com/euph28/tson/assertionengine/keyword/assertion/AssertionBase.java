package com.euph28.tson.assertionengine.keyword.assertion;

import com.euph28.tson.assertionengine.TSONAssertionEngine;
import com.euph28.tson.assertionengine.reporting.ReproductionReportRetriever;
import com.euph28.tson.context.TSONContext;
import com.euph28.tson.context.provider.JsonValueProvider;
import com.euph28.tson.core.keyword.Keyword;
import com.euph28.tson.core.keyword.KeywordType;
import com.euph28.tson.interpreter.Statement;
import com.euph28.tson.reporter.Report;
import com.euph28.tson.reporter.ReportType;
import com.euph28.tson.reporter.TSONReporter;

import java.util.Arrays;
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
     * @param resultDescription Description on the result of the assertion. This should explain the actual value and
     *                          its accuracy to the expected value
     * @param stepDescription   Description of the assertion step that was done. The step is independent of the result
     *                          and is used for manual reproduction of the step
     * @param resultExpression  Assertion expression that this result was using (eg: <path>=<expectedValue>)
     */
    protected void resultPass(String resultDescription, String stepDescription, String resultExpression) {
        tsonReporter.createSubReport(new Report(
                ReportType.PASS,
                resultExpression,
                resultDescription,
                stepDescription,
                tsonReporter.getReport().getSource()
        ));
    }

    /**
     * Record a fail assertion
     *
     * @param stepDescription   Description of the assertion step that was done. The step is independent of the result
     *                          and is used for manual reproduction of the step
     * @param resultDescription Description on the result of the assertion. This should explain the actual value and
     *                          its accuracy to the expected value
     * @param resultExpression  Assertion expression that this result was using (eg: <path>=<expectedValue>)
     */
    protected void resultFail(String resultDescription, String stepDescription, String resultExpression) {
        // Create report
        Report report = new Report(
                ReportType.FAIL,
                resultExpression,
                resultDescription,
                stepDescription,
                tsonReporter.getReport().getSource()
        );

        // Attach report to reporter as sub-report
        tsonReporter.createSubReport(report);

        // Update report with reproduction scenario (do this after attaching to include this step)
        report.setReportDetail(report.getReportDetail() + "\n\n" + tsonReporter.getGeneratedReport(new ReproductionReportRetriever()));
    }

    /**
     * Retrieve value from a jsonContent and a jsonPath
     *
     * @param tsonContext Context class that stores the variables related to the current running state
     * @param jsonPath    Path to the value. Path is separated by colons (eg: body.item.0.value).
     *                    Wildcards can be used to retrieve all values in an array
     * @return Map of path-to-value of resolved values. Returns {@code null} if path was invalid
     */
    // TODO: Refactor this to be a general getValue that can use Context's ContentProvider
    protected Map<String, String> getValueFromJson(TSONContext tsonContext, String jsonPath) {
        JsonValueProvider jsonValueProvider = new JsonValueProvider();
        return jsonValueProvider.getValuesFromJson(tsonContext, jsonPath);
    }

    /* ----- METHODS ------------------------------ */

    /**
     * Handle the assertion based on the provided data. Use {@link #resultPass(String, String, String)}
     * and {@link #resultFail(String, String, String)} to report assertion results
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
        this.tsonReporter = tsonAssertionEngine.getReporter(tsonReporter);

        // Generate report of current step
        Report report = this.tsonReporter.getReport();
        report.setReportFallbackTitle(this.getCode());
        report.setReportStep("Perform assertions");

        // Perform assertion (and report assertion results)
        boolean status = handleAssertion(tsonContext, value);

        // Check if AssertionEngine should publish result (publish if upcoming keyword is action and not assertion)
        Statement nextStatement = tsonContext.getTsonInterpreter().peekType(
                Arrays.asList(KeywordType.ACTION, KeywordType.ASSERTION)
        );
        if (nextStatement == null || nextStatement.getKeyword().getKeywordType() == KeywordType.ACTION) {
            tsonAssertionEngine.doCompleteReport();
        }

        return status;
    }

    @Override
    public KeywordType getKeywordType() {
        return KeywordType.ASSERTION;
    }
}
