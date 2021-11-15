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

    /* ----- CONSTRUCTOR ------------------------------ */

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
        this.reportTitle = reportTitle;
        this.reportType = reportType;
        this.reportDetail = reportDetail;
        this.source = source;
    }
}
