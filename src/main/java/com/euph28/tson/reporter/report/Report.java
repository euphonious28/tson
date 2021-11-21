package com.euph28.tson.reporter.report;

/**
 * An individual report entry
 */
public class Report {

    /* ----- VARIABLES ------------------------------ */
    /**
     * Title of the report. This will be the information shown when seen in an overview. The report title should be
     * the title provided by the user in the test script. For a fallback title if a user-provided title was not
     * given, use {@link #reportFallbackTitle}
     */
    String reportTitle;

    /**
     * Fallback title to be used if a {@link #reportTitle} was not provided
     */
    String reportFallbackTitle;

    /**
     * Details within the report. This will be hidden by default. Use {@link #reportTitle} for content that
     * must be shown in the overview
     */
    String reportDetail;

    /**
     * Reproduction step for this report. This should be easy to understand and recreate. Additional details
     * (eg: explanation on why the step is done) should be shown in the {@link #reportTitle} or {@link #reportDetail}
     * instead
     */
    String reportStep;

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
     * @param reportType  Type of the report
     * @param reportTitle Title of the report from the user. This will be the information shown when seen in an overview.
     *                    Leave this as an empty String to use {@link #reportFallbackTitle}
     */
    Report(ReportType reportType, String reportTitle) {
        this.reportType = reportType;
        this.reportTitle = reportTitle;
    }

    /**
     * Create a report entry
     *
     * @param reportType   Type of the report
     * @param reportTitle  Title of the report from the user. This will be the information shown when seen in an overview.
     *                     Leave this as an empty String to use {@link #reportFallbackTitle}
     * @param reportDetail Details within the report. This will be hidden by default. Use {@code reportTitle} for content that
     *                     must be shown in the overview
     * @param reportStep   Reproduction steps that provides a way to manually reproduce the step
     * @param source       Source of the code that created the report
     */
    public Report(ReportType reportType, String reportTitle, String reportDetail, String reportStep, String source) {
        this.reportType = reportType;
        this.reportTitle = reportTitle;
        this.reportDetail = reportDetail;
        this.reportStep = reportStep;
        this.source = source;
    }

    /* ----- SETTERS & GETTERS ------------------------------ */

    public String getReportTitle() {
        return reportTitle.equals("") ? reportFallbackTitle : reportTitle;
    }

    public void setReportFallbackTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public String getReportDetail() {
        return reportDetail;
    }

    public void setReportDetail(String reportDetail) {
        this.reportDetail = reportDetail;
    }

    public String getReportStep() {
        return reportStep;
    }

    public void setReportStep(String reportStep) {
        this.reportStep = reportStep;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
