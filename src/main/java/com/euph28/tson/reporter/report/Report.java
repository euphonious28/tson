package com.euph28.tson.reporter.report;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
     * Map (Filename-Content) of attachments. Attachments are additional files that should be attached with the report
     */
    Map<String, String> reportAttachments = new HashMap<>();

    /**
     * Severity/importance of the report
     */
    ReportType reportType;

    /**
     * Source of the code that created the report
     */
    ReportSource source;

    /* ----- CONSTRUCTOR ------------------------------ */

    /**
     * Create a report entry
     *
     * @param reportType  Type of the report
     * @param reportTitle Title of the report from the user. This will be the information shown when seen in an overview.
     *                    Leave this as an empty String to use {@link #reportFallbackTitle}
     * @param source      Source of the code that created the report
     */
    Report(ReportType reportType, String reportTitle, ReportSource source) {
        this.reportType = reportType;
        this.reportTitle = reportTitle;
        this.source = source;
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
    public Report(ReportType reportType, String reportTitle, String reportDetail, String reportStep, ReportSource source) {
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

    public ReportSource getSource() {
        return source;
    }

    /* ----- ATTACHMENTS ------------------------------ */

    /**
     * Add an attachment to the report
     *
     * @param attachmentName Name of attachment (file name)
     * @param content        Content of attachment
     */
    public void addAttachment(String attachmentName, String content) {
        reportAttachments.put(attachmentName, content);
    }

    /**
     * Retrieve an attachment
     *
     * @param attachmentName Name of attachment to retrieve
     * @return Attachment content. Returns an empty String if there is no valid attachment
     */
    public String getAttachment(String attachmentName) {
        return reportAttachments.getOrDefault(attachmentName, "");
    }

    /**
     * Retrieve the set of attachment names
     *
     * @return Set of attachment names
     */
    public Set<String> getAttachmentNames() {
        return reportAttachments.keySet();
    }
}
