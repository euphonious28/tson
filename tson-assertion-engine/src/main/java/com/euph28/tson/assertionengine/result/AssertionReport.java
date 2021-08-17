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

    int countPass, countFail = 0;

    /* ----- CONSTRUCTOR ------------------------------ */
    public AssertionReport(List<AssertionResult> resultList) {
        addResults(resultList);
    }

    /* ----- METHODS: INTERNAL ------------------------------ */
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

    /* ----- GETTERS ------------------------------ */

    public boolean isPass() {
        return countFail == 0;
    }

    public int getCountPass() {
        return countPass;
    }

    public int getCountFail() {
        return countFail;
    }
}
