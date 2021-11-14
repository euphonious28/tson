package com.euph28.tson.reporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Main access point for reporting action results. Reports are automatically logged
 */
public class TSONReporter {

    /* ----- VARIABLES ------------------------------ */
    Logger logger = LoggerFactory.getLogger(TSONReporter.class);

    /**
     * List of reports
     */
    List<Report> reportList = new ArrayList<>();

    /* ----- METHODS: REPORTING ------------------------------ */

    /**
     * Create a report entry
     *
     * @param reportType   Type of the report
     * @param reportTitle  Title of the report. This will be the information shown when seen in an overview
     * @param reportDetail Details within the report. This will be hidden by default. Use {@code reportTitle} for content that
     *                     must be shown in the overview
     * @param source       Source of the code that created the report
     */
    public void doReport(ReportType reportType, String reportTitle, String reportDetail, String source) {
        reportList.add(new Report(reportType, reportTitle, reportDetail, source));
    }

    /**
     * Create a report entry
     *
     * @param reportType   Type of the report
     * @param reportTitle  Title of the report. This will be the information shown when seen in an overview
     * @param reportDetail Details within the report. This will be hidden by default. Use {@code reportTitle} for content that
     *                     must be shown in the overview
     */
    public void doReport(ReportType reportType, String reportTitle, String reportDetail) {
        reportList.add(new Report(reportType, reportTitle, reportDetail));
    }

    /**
     * Create a report entry
     *
     * @param reportType  Type of the report
     * @param reportTitle Title of the report. This will be the information shown when seen in an overview
     */
    public void doReport(ReportType reportType, String reportTitle) {
        reportList.add(new Report(reportType, reportTitle));
    }

    /**
     * Create a sub-report entry and retrieve the Reporter for reporting sub-reports to. Items that are unrelated can
     * be left empty (String)
     *
     * @param rootReportType   Type of the root report
     * @param rootReportTitle  Title of the root report. This will be the information shown when seen in an overview
     * @param rootReportDetail Details within the root report. This will be hidden by default. Use {@code reportTitle} for content that
     *                         must be shown in the overview
     * @param rootSource       Source of the code that created the root report
     * @return Reporter for writing the sub-reports to
     */
    public TSONReporter doSubReport(ReportType rootReportType, String rootReportTitle, String rootReportDetail, String rootSource) {
        Report report = new Report(rootReportType, rootReportTitle, rootReportDetail, rootSource);
        reportList.add(report);
        return report.getSubReport();
    }

    /**
     * Create a sub-report entry and retrieve the Reporter for reporting sub-reports to. Items that are unrelated can
     * be left empty (String). </br>
     * If the sub-report only has one report, it will replace this report instead. Otherwise, the provided values will
     * be used.
     *
     * @param defaultReportType   Default type of the root report
     * @param defaultReportTitle  Default title of the root report. This will be the information shown when seen in an overview
     * @param defaultReportDetail Default details within the root report. This will be hidden by default. Use {@code reportTitle} for content that
     *                            must be shown in the overview
     * @param defaultSource       Default source of the code that created the root report
     * @return Reporter for writing the sub-report/actual report to
     */
    public TSONReporter doReportWithDefault(ReportType defaultReportType, String defaultReportTitle, String defaultReportDetail, String defaultSource) {
        Report report = new Report(defaultReportType, defaultReportTitle, defaultReportDetail, defaultSource);
        report.setAutoMerge(true);
        reportList.add(report);
        return report.getSubReport();
    }

    /* ----- METHODS: REPORTING ------------------------------ */
}
