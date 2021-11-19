package com.euph28.tson.reporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Main access point for reporting action results. This should be used for reporting the result of actions taken.
 * For logging details within(or outside) an action, use the logging interface instead. Reports are automatically
 * logged under this class. <br/>
 * //TODO: Consider updating logging policy. Do we want to log errors as well (what if its execution error but not framework error)
 * <br/>
 * This TSONReporter internally stores one report and 0-many sub-TSONReporter(s), resulting in a tree structure
 */
public class TSONReporter {

    /* ----- VARIABLES ------------------------------ */
    Logger logger = LoggerFactory.getLogger(TSONReporter.class);

    /**
     * List of sub-reports
     */
    List<TSONReporter> subReportList = new ArrayList<>();

    /**
     * The report of this reporter
     */
    Report report;

    /**
     * Parent reporter of this reporter
     */
    TSONReporter parent;

    /**
     * Property to indicate if entries with only one sub-report should be merged with the sub-report.
     * If merged, the sub-report's values will be used instead of this. Value of this {@link Report#source} will
     * be used if the sub-report does not have a value for it
     */
    boolean isAutoMergeSingleEntries = false;

    /* ----- CONSTRUCTOR ------------------------------ */

    /**
     * Create a TSONReporter with no report content. This should be used only for the root of the reports.
     * Use {@link #TSONReporter(TSONReporter, ReportType, String, String, String)} if this should have a report entry
     */
    public TSONReporter() {
        this(null, ReportType.TRACE, "", "", "");
    }

    /**
     * Create a TSONReporter with report content
     *
     * @param parent       Parent TSONReporter of this report. Leave as {@code null} if it isn't created by another TSONReporter
     * @param reportType   Type of the report
     * @param reportTitle  Title of the report. This will be the information shown when seen in an overview
     * @param reportDetail Details within the report. This will be hidden by default. Use {@code reportTitle} for content that
     *                     must be shown in the overview
     * @param source       Source of the code that created the report
     */
    public TSONReporter(TSONReporter parent, ReportType reportType, String reportTitle, String reportDetail, String source) {
        report = new Report(reportType, reportTitle, reportDetail, source);
        this.parent = parent;
    }

    /* ----- METHODS: REPORTING ------------------------------ */

    /**
     * Create a report entry
     *
     * @param reportType   Type of the report
     * @param reportTitle  Title of the report. This will be the information shown when seen in an overview
     * @param reportDetail Details within the report. This will be hidden by default. Use {@code reportTitle} for content that
     *                     must be shown in the overview
     * @param source       Source of the code that created the report
     * @return Created reporter with the report entry. Use this to add sub-reports within the created report
     */
    public TSONReporter doReport(ReportType reportType, String reportTitle, String reportDetail, String source) {
        return createSubReport(reportType, reportTitle, reportDetail, source);
    }

    /**
     * Create a report entry
     *
     * @param reportType   Type of the report
     * @param reportTitle  Title of the report. This will be the information shown when seen in an overview
     * @param reportDetail Details within the report. This will be hidden by default. Use {@code reportTitle} for content that
     *                     must be shown in the overview
     * @return Created reporter with the report entry. Use this to add sub-reports within the created report
     */
    public TSONReporter doReport(ReportType reportType, String reportTitle, String reportDetail) {
        return createSubReport(reportType, reportTitle, reportDetail, "");
    }

    /**
     * Create a report entry
     *
     * @param reportType  Type of the report
     * @param reportTitle Title of the report. This will be the information shown when seen in an overview
     * @return Created reporter with the report entry. Use this to add sub-reports within the created report
     */
    public TSONReporter doReport(ReportType reportType, String reportTitle) {
        return createSubReport(reportType, reportTitle, "", "");
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
        TSONReporter reporter = createSubReport(defaultReportType, defaultReportTitle, defaultReportDetail, defaultSource);
        reporter.setAutoMergeSingleEntries(true);
        return reporter;
    }

    /**
     * Delete this report from parent
     */
    public void delete() {
        if (parent != null) {
            parent.deleteSubReport(this);
            parent = null;
        } else {
            logger.error("Unable to delete reporter from parent. TSONReporter does not have a parent");
        }
    }

    /* ----- METHODS: INTERNAL ------------------------------ */

    /**
     * Remove sub-report from {@link #subReportList}
     *
     * @param subReporter Sub-report to be deleted
     */
    protected void deleteSubReport(TSONReporter subReporter) {
        subReportList.remove(subReporter);
    }

    protected TSONReporter createSubReport(ReportType reportType, String reportTitle, String reportDetail, String source) {
        TSONReporter reporter = new TSONReporter(this, reportType, reportTitle, reportDetail, source);
        subReportList.add(reporter);
        return reporter;
    }

    /* ----- METHODS: OUTPUT ------------------------------ */

    /**
     * Get a basic output of the report(s) within the reporter. This method is temporary and will be replaced
     * in the future with better (more configurable) output
     *
     * @return Report split by line
     */
    public List<String> getReportAsBasicString() {
        List<String> result = new ArrayList<>();

        // If auto-merge and sub-report has size==1 (eligible for auto-merge), automatically return sub-report's output
        if (isAutoMergeSingleEntries && subReportList.size() == 1) {
            return subReportList.get(0).getReportAsBasicString();
        }

        // Otherwise, default: Report as first entry
        result.add(String.format("[%s] %s", getDerivedReportType(), report.reportTitle));

        // Add sub-reports into result with indentation
        for (TSONReporter reporter : subReportList) {
            for (String subResult : reporter.getReportAsBasicString()) {
                result.add("  " + subResult);
            }
        }

        // Return result
        return result;
    }

    /**
     * Retrieve the report type with the highest severity within the reporter
     *
     * @return Report type with the highest severity
     */
    ReportType getDerivedReportType() {
        ReportType result = report.reportType;

        for (TSONReporter reporter : subReportList) {
            result = reporter.getDerivedReportType().severity > result.severity
                    ? reporter.getDerivedReportType()
                    : result;
        }

        return result;
    }

    /* ----- METHODS: PROPERTIES ------------------------------ */

    /**
     * Set the property for indicating if entries with only one sub-report should be merged with the sub-report.
     * If merged, the sub-report's values will be used instead of this. Value of this {@link Report#source} will
     * be used if the sub-report does not have a value for it
     *
     * @param autoMergeSingleEntries New value for the property
     */
    public void setAutoMergeSingleEntries(boolean autoMergeSingleEntries) {
        isAutoMergeSingleEntries = autoMergeSingleEntries;
    }
}
