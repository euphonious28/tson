package com.euph28.tson.assertionengine.reporting;

import com.euph28.tson.reporter.Report;
import com.euph28.tson.reporter.ReportRetriever;
import com.euph28.tson.reporter.ReportType;

import java.util.List;

/**
 * Report retriever that retrieves the reproduction scenario from {@link com.euph28.tson.reporter.TSONReporter}
 */
public class ReproductionReportRetriever implements ReportRetriever<String> {
    @Override
    public String getReport(ReportType derivedReportType, Report report, List<String> subReportList, int layer, int index) {
        // Create output of current report
        StringBuilder result = new StringBuilder();

        // Indent current report
        for (int i = 1; i < layer; i++) {
            result.append("  ");
        }

        // Add number and append output of current report
        if (layer != 0) {   // Skip if it's the root
            result.append(index + 1).append(". ").append(report.getReportStep());
        }

        // Add output of sub-reports
        for (String subReport : subReportList) {
            result.append("\n").append(subReport);
        }

        return result.toString();
    }

    @Override
    public boolean enableReport(ReportType derivedReportType, Report report) {
        return true;
    }
}