package com.euph28.tson.reporter.report;

import com.euph28.tson.core.keyword.Keyword;

/**
 * Details regarding the source of a report
 */
public final class ReportSource {

    /* ----- VARIABLES ------------------------------ */
    /**
     * Keyword that was used for the source
     */
    final Keyword keyword;

    /**
     * Value provided along with keyword
     */
    final String value;

    /* ----- CONSTRUCTOR ------------------------------ */
    /**
     * Create an empty source
     */
    public ReportSource() {
        this(null, "");
    }

    public ReportSource(Keyword keyword, String value) {
        this.keyword = keyword;
        this.value = value;
    }

    /* ----- GETTERS ------------------------------ */
    public Keyword getKeyword() {
        return keyword;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format(
                "[%s] %s",
                keyword != null ? keyword.getCode() : "-",
                value
        );
    }
}
