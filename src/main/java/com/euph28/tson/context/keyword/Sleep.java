package com.euph28.tson.context.keyword;

import com.euph28.tson.context.TSONContext;
import com.euph28.tson.core.keyword.Keyword;
import com.euph28.tson.core.keyword.KeywordType;
import com.euph28.tson.interpreter.Statement;
import com.euph28.tson.reporter.TSONReporter;
import com.euph28.tson.reporter.report.ReportType;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Sleep extends Keyword {
    @Override
    public String getCode() {
        return "SLEEP";
    }

    @Override
    public String getLspDescriptionShort() {
        return "Wait for x milliseconds";
    }

    @Override
    public String getLspDescriptionLong() {
        return "Wait for x milliseconds before continuing to the next action";
    }

    @Override
    public KeywordType getKeywordType() {
        return KeywordType.UTILITY;
    }

    @Override
    public List<String> getLspTags() {
        List<String> tagList = super.getLspTags();
        tagList.add("Wait");
        return tagList;
    }

    @Override
    public boolean handle(TSONContext tsonContext, TSONReporter tsonReporter, Statement statement) {
        // Report
        tsonReporter.getReport().setReportFallbackTitle("Wait for " + statement.getValue() + " milliseconds");
        tsonReporter.getReport().setReportStep("Wait for " + statement.getValue() + " milliseconds");

        // Retrieve duration
        int duration;
        try {
            duration = Integer.parseInt(statement.getValue());
        } catch (NumberFormatException e) {
            tsonReporter.getReport().setReportType(ReportType.ERROR);
            tsonReporter.getReport().setReportDetail("Failed to convert value to duration: " + statement.getValue());
            return false;
        }

        try {
            TimeUnit.MILLISECONDS.sleep(duration);
        } catch (InterruptedException e) {
            tsonReporter.getReport().setReportType(ReportType.ERROR);
            tsonReporter.getReport().setReportDetail("Sleep interrupted. Exception: " + e.getMessage());
        }

        return true;
    }
}
