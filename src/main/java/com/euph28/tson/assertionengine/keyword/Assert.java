package com.euph28.tson.assertionengine.keyword;

import com.euph28.tson.assertionengine.TSONAssertionEngine;
import com.euph28.tson.context.TSONContext;
import com.euph28.tson.core.keyword.Keyword;
import com.euph28.tson.core.keyword.KeywordType;
import com.euph28.tson.reporter.report.Report;
import com.euph28.tson.reporter.report.ReportType;
import com.euph28.tson.reporter.TSONReporter;

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
    public String getLspDescriptionShort() {
        return "Group subsequent assertions";
    }

    @Override
    public String getLspDescriptionLong() {
        return "Groups all subsequent assertions into the same report";
    }

    @Override
    public KeywordType getKeywordType() {
        return KeywordType.ASSERTION;
    }

    @Override
    public boolean handle(TSONContext tsonContext, TSONReporter tsonReporter, String value) {
        // Report
        Report report = tsonReporter.getReport();
        report.setReportType(ReportType.INFO);
        report.setReportFallbackTitle(value);
        report.setReportDetail(value);
        report.setReportStep("Perform assertions");

        // Store reporter in AssertionEngine
        tsonAssertionEngine.setReporter(tsonReporter);
        return true;
    }
}