package com.euph28.tson.context.keyword;

import com.euph28.tson.context.TSONContext;
import com.euph28.tson.context.VariableType;
import com.euph28.tson.core.keyword.Keyword;
import com.euph28.tson.core.keyword.KeywordType;
import com.euph28.tson.interpreter.Statement;
import com.euph28.tson.reporter.report.Report;
import com.euph28.tson.reporter.report.ReportType;
import com.euph28.tson.reporter.TSONReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility keyword: ID Property
 * <p>
 * Set the Test ID property
 */
public class PropertyId extends Keyword {
    /* ----- VARIABLES ------------------------------ */
    Logger logger = LoggerFactory.getLogger(PropertyId.class);

    /* ----- OVERRIDE: KEYWORD ------------------------------ */
    @Override
    public String getCode() {
        return "ID";
    }

    @Override
    public String getLspDescriptionShort() {
        return "Set the ID of this test";
    }

    @Override
    public String getLspDescriptionLong() {
        return "Set the ID of this test. Subsequent calls will override the existing ID";
    }

    @Override
    public KeywordType getKeywordType() {
        return KeywordType.NO_IMPACT;
    }

    @Override
    public boolean handle(TSONContext tsonContext, TSONReporter tsonReporter, Statement statement) {
        String key = "id";

        // Add variable
        tsonContext.addVariable(VariableType.PROPERTY, key, statement.getValue());

        // Report
        Report report = tsonReporter.getReport();
        report.setReportType(ReportType.TRACE);
        report.setReportFallbackTitle("Set test ID to: " + statement.getValue());
        report.setReportDetail(String.format("Create property variable \"%s\" with value \"%s\"", key, statement.getValue()));
        return true;
    }
}
