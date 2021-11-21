package com.euph28.tson.context.keyword;

import com.euph28.tson.context.TSONContext;
import com.euph28.tson.context.VariableType;
import com.euph28.tson.core.keyword.Keyword;
import com.euph28.tson.core.keyword.KeywordType;
import com.euph28.tson.reporter.report.Report;
import com.euph28.tson.reporter.report.ReportType;
import com.euph28.tson.reporter.TSONReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility keyword: Description Property
 * <p>
 * Set the Test description property
 */
public class PropertyDescription extends Keyword {
    /* ----- VARIABLES ------------------------------ */
    Logger logger = LoggerFactory.getLogger(PropertyDescription.class);

    /* ----- OVERRIDE: KEYWORD ------------------------------ */
    @Override
    public String getCode() {
        return "DESC";
    }

    @Override
    public String getDescriptionShort() {
        return "Set the description of this test";
    }

    @Override
    public String getDescriptionLong() {
        return "Set the description of this test. Subsequent calls will override the existing description";
    }

    @Override
    public KeywordType getKeywordType() {
        return KeywordType.NO_IMPACT;
    }

    @Override
    public boolean handle(TSONContext tsonContext, TSONReporter tsonReporter, String value) {
        String key = "desc";

        // Add variable
        tsonContext.addVariable(VariableType.PROPERTY, key, value);

        // Report
        Report report = tsonReporter.getReport();
        report.setReportType(ReportType.TRACE);
        report.setReportFallbackTitle("Set test description to: " + value);
        report.setReportDetail(String.format("Create property variable \"%s\" with value \"%s\"", key, value));
        return true;
    }
}
