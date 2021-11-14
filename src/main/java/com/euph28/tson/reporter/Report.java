package com.euph28.tson.reporter;

/**
 * An individual report entry
 */
public class Report {

    /* ----- VARIABLES ------------------------------ */
    /**
     * Title of the report. This will be the information shown when seen in an overview
     */
    String reportTitle;

    /**
     * Details within the report. This will be hidden by default. Use {@link #reportTitle} for content that
     * must be shown in the overview
     */
    String reportDetail;

    /**
     * Severity/importance of the report
     */
    ReportType reportType;

    /**
     * Source of the code that created the report
     */
    String source;

    /**
     * Sub-report belonging to this base report
     */
    TSONReporter subReport;

    /**
     * Auto merge property. If enabled, sub-reports that only has one report will automatically be merged into this
     * (this report will inherit the values of the sub-report)
     */
    boolean isAutoMerge = false;

    /* ----- CONSTRUCTOR ------------------------------ */

    /**
     * Create a report entry
     *
     * @param reportType  Type of the report
     * @param reportTitle Title of the report. This will be the information shown when seen in an overview
     */
    Report(ReportType reportType, String reportTitle) {
        this.reportTitle = reportTitle;
        this.reportType = reportType;
    }

    /**
     * Create a report entry
     *
     * @param reportType   Type of the report
     * @param reportTitle  Title of the report. This will be the information shown when seen in an overview
     * @param reportDetail Details within the report. This will be hidden by default. Use {@code reportTitle} for content that
     *                     must be shown in the overview
     */
    Report(ReportType reportType, String reportTitle, String reportDetail) {
        this(reportType, reportTitle);
        this.reportDetail = reportDetail;
    }

    /**
     * Create a report entry
     *
     * @param reportType   Type of the report
     * @param reportTitle  Title of the report. This will be the information shown when seen in an overview
     * @param reportDetail Details within the report. This will be hidden by default. Use {@code reportTitle} for content that
     *                     must be shown in the overview
     * @param source       Source of the code that created the report
     */
    Report(ReportType reportType, String reportTitle, String reportDetail, String source) {
        this(reportType, reportTitle, reportDetail);
        this.source = source;
    }

    /* ----- METHODS ------------------------------ */

    /**
     * Get the sub-report reporter
     *
     * @return Reporter for adding sub-report entries
     */
    public TSONReporter getSubReport() {
        if (subReport == null) {
            subReport = new TSONReporter();
        }

        return subReport;
    }

    /**
     * Set the auto-merge property. If enabled, sub-reports that only has one report will automatically be merged into this
     * (this report will inherit the values of the sub-report)
     *
     * @param autoMerge New property of the auto-merge
     */
    public void setAutoMerge(boolean autoMerge) {
        isAutoMerge = autoMerge;
    }
}
