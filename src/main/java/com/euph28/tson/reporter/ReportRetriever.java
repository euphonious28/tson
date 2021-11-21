package com.euph28.tson.reporter;

import java.util.List;

/**
 * Interface for retrieving the report from a TSONReporter
 */
public interface ReportRetriever<ReportDataType> {
    /**
     * Retrieve the report of the current {@link TSONReporter} based on its contained data. This method is executed
     * recursively until the root generates an output
     *
     * @param derivedReportType Highest {@link ReportType#severity} {@link ReportType} within the sub-reports
     * @param report            Current report within the {@link TSONReporter}
     * @param subReportList     List of sub-reports within the {@link TSONReporter}
     * @param layer             The current layer of this report (layer starts at 0 with root report)
     * @param index             The current index of this report (index starts at 0 with first sub-report)
     * @return Formatted output to be passed to parent
     */
    ReportDataType getReport(ReportType derivedReportType, Report report, List<ReportDataType> subReportList, int layer, int index);

    /**
     * Filter to determine if a report should be used or ignored
     *
     * @param derivedReportType Highest {@link ReportType#severity} {@link ReportType} within the sub-reports
     * @param report            Current report within the {@link TSONReporter}
     * @return Return {@code true} if the report should be used for generation. Return {@code false} if report should be skipped
     */
    boolean enableReport(ReportType derivedReportType, Report report);
}
