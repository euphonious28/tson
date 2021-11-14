package com.euph28.tson.reporter;

/**
 * Report type for indicating severity or type of report
 */
public enum ReportType {

    /* ----- ENUM TYPES ------------------------------ */
    /**
     * Lowest importance report for indicating an event has happened normally
     */
    INFO,

    /**
     * Warning report for indicating an event that did not occur normally but has no impact
     */
    WARN,

    /**
     * Error report for indicating an event that did not occur normally and could/will have an impact
     */
    ERROR,

    /**
     * Critical error report for indicating an event that did not occur normally and will have a breaking impact
     */
    CRITICAL,

    /**
     * Report that test passed
     */
    PASS,

    /**
     * Report that test failed
     */
    FAIL
}
