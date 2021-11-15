package com.euph28.tson.reporter;

/**
 * Report type for indicating severity or type of report
 */
public enum ReportType {

    /* ----- ENUM TYPES ------------------------------ */
    /**
     * Lowest importance report. Reports of this level is hidden by default and is only used for debugging
     */
    TRACE(-1),

    /**
     * Low importance report for indicating an event has happened normally
     */
    INFO(0),

    /**
     * Warning report for indicating an event that did not occur normally but has no impact
     */
    WARN(10),

    /**
     * Error report for indicating an event that did not occur normally and could/will have an impact
     */
    ERROR(11),

    /**
     * Critical error report for indicating an event that did not occur normally and will have a breaking impact
     */
    CRITICAL(12),

    /**
     * Report that test passed
     */
    PASS(1),

    /**
     * Report that test failed
     */
    FAIL(20);

    /* ----- VARIABLES ------------------------------ */
    /**
     * Severity level of the report type, higher value means the item is more severe (and should override the lower
     * severity levels if they are to be merged)
     */
    int severity;

    /* ----- CONSTRUCTOR ------------------------------ */
    ReportType(int severity) {
        this.severity = severity;
    }
}
