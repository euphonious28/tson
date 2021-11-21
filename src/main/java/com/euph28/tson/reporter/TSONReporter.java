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

    /* ----- CONSTRUCTOR ------------------------------ */

    /**
     * Create a TSONReporter with no report content. This should be used only for the root of the reports.
     * Use {@link #TSONReporter(TSONReporter, Report)} if this should have a report entry
     */
    public TSONReporter() {
        this(null, new Report(ReportType.TRACE, "", "", "", ""));
    }

    /**
     * Create a TSONReporter with report content
     *
     * @param parent Parent TSONReporter of this report. Leave as {@code null} if it isn't created by another TSONReporter
     * @param report Report content to for this TSONReporter
     */
    public TSONReporter(TSONReporter parent, Report report) {
        this.report = report;
        this.parent = parent;
    }

    /* ----- METHODS: REPORTING ------------------------------ */

    /**
     * Create a sub-report entry
     *
     * @param report Content of the sub-report
     * @return Created reporter with the report entry. Use this to add sub-reports within the created report
     */
    public TSONReporter createSubReport(Report report) {
        TSONReporter reporter = new TSONReporter(this, report);
        subReportList.add(reporter);
        return reporter;
    }

    /**
     * Get the current report for editing the report content
     *
     * @return Get the current report
     */
    public Report getReport() {
        return report;
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

    /* ----- METHODS: OUTPUT ------------------------------ */

    /**
     * Get a basic output of the report(s) within the reporter. This method is temporary and will be replaced
     * in the future with better (more configurable) output
     *
     * @return Report split by line
     */
    public List<String> getReportAsBasicString() {
        List<String> result = new ArrayList<>();

        // Report as first entry
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
}
