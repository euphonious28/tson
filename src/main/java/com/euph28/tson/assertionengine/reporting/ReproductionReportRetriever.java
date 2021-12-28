package com.euph28.tson.assertionengine.reporting;

import com.euph28.tson.core.keyword.KeywordType;
import com.euph28.tson.reporter.TSONReporter;
import com.euph28.tson.reporter.report.Report;
import com.euph28.tson.reporter.ReportRetriever;
import com.euph28.tson.reporter.report.ReportType;

import java.util.List;

/**
 * Report retriever that retrieves the reproduction scenario from {@link com.euph28.tson.reporter.TSONReporter}
 */
public class ReproductionReportRetriever implements ReportRetriever<String> {
    @Override
    public String getReport(TSONReporter reporter, List<String> subReportList, int layer, int index) {
        // Create output of current report
        StringBuilder result = new StringBuilder();

        // Indent current report
        for (int i = 1; i < layer; i++) {
            result.append("  ");
        }

        // Add number and append output of current report
        if (layer != 0) {   // Skip if it's the root
            result.append(index + 1).append(". ").append(reporter.getReport().getReportStep());
        }

        // Add output of sub-reports
        for (String subReport : subReportList) {
            result.append("\n").append(subReport);
        }

        return result.toString();
    }

    @Override
    public boolean enableReport(ReportType derivedReportType, Report report) {
        return report.getSource().getKeyword().getKeywordType() == KeywordType.ACTION
                || report.getSource().getKeyword().getKeywordType() == KeywordType.UTILITY;
    }
}