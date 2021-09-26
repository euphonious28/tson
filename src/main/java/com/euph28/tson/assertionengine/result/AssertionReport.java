package com.euph28.tson.assertionengine.result;

import java.util.ArrayList;
import java.util.List;

/**
 * Class containing a full report of a collection of {@link AssertionResult}
 */
public class AssertionReport {

    /* ----- VARIABLES ------------------------------ */
    /**
     * List of results for this report
     */
    List<AssertionResult> assertionResultList = new ArrayList<>();

    /**
     * Title of the report
     */
    String reportTitle;

    /**
     * Count of number of {@link AssertionResult} that pass
     */
    int countPass = 0;

    /**
     * Count of number of {@link AssertionResult} that fail
     */
    int countFail = 0;

    /* ----- CONSTRUCTOR ------------------------------ */

    /**
     * Report containing a collection of {@link AssertionResult}
     *
     * @param resultList List of results to initialize the report with
     */
    public AssertionReport(List<AssertionResult> resultList) {
        this();
        addResults(resultList);
    }

    /**
     * Report containing a collection of {@link AssertionResult}
     */
    public AssertionReport() {
        reportTitle = "";
    }

    /* ----- METHODS: INTERNAL ------------------------------ */

    /**
     * Evaluate results and update counters ({@link #countPass} and {@link #countFail})
     */
    void evaluateResults() {
        countPass = (int) assertionResultList
                .stream()
                .filter(AssertionResult::isPass)
                .count();

        countFail = assertionResultList.size() - countPass;
    }

    /* ----- METHODS ------------------------------ */

    /**
     * Add results to the report
     *
     * @param resultList List of results
     */
    public void addResults(List<AssertionResult> resultList) {
        assertionResultList.addAll(resultList);
        evaluateResults();
    }

    /* ----- SETTERS ------------------------------ */

    /**
     * Set the title of this report
     *
     * @param reportTitle Title of this report
     */
    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    /* ----- GETTERS ------------------------------ */

    /**
     * Retrieve the pass state of the report
     *
     * @return Returns {@code true} if there is no failure result
     */
    public boolean isPass() {
        return countFail == 0;
    }

    /**
     * Retrieve the number of results that pass
     *
     * @return Number of results that pass
     */
    public int getCountPass() {
        return countPass;
    }

    /**
     * Retrieve the number of results that fail
     *
     * @return Number of results that fail
     */
    public int getCountFail() {
        return countFail;
    }

    /**
     * Retrieve the title of this report
     *
     * @return Title of this report
     */
    public String getReportTitle() {
        return reportTitle;
    }

    /**
     * Retrieve the report of the results of this report. Report contains description of failures
     *
     * @return Report of the results
     */
    public String getReport() {
        StringBuilder stringBuilder = new StringBuilder();
        assertionResultList.forEach(result -> stringBuilder.append(result.getDescription()).append(System.lineSeparator()));
        return stringBuilder.toString();
    }
}
