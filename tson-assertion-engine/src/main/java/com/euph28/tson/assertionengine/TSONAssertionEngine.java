package com.euph28.tson.assertionengine;

import com.euph28.tson.assertionengine.keyword.Assert;
import com.euph28.tson.assertionengine.keyword.assertion.AssertEqual;
import com.euph28.tson.assertionengine.listener.TSONAssertionEngineListener;
import com.euph28.tson.assertionengine.result.AssertionReport;
import com.euph28.tson.assertionengine.result.AssertionResult;
import com.euph28.tson.interpreter.keyword.Keyword;
import com.euph28.tson.interpreter.keyword.KeywordProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Entry access point for the TSON Assertion Engine
 */
public class TSONAssertionEngine implements KeywordProvider {
    /* ----- VARIABLES ------------------------------ */
    /**
     * List of assertion reports that are pending read
     */
    List<AssertionReport> assertionReportList = new ArrayList<>();

    /**
     * Current assertion report that is being written to
     */
    AssertionReport currentAssertionReport = new AssertionReport();

    /**
     * List of event listeners
     */
    List<TSONAssertionEngineListener> listenerList = new ArrayList<>();

    /* ----- METHODS: Assertion result handling ------------------------------ */

    /**
     * Add {@link AssertionResult} to be reported
     *
     * @param assertionResultList List of {@link AssertionResult} to be reported
     */
    public void addAssertionResult(List<AssertionResult> assertionResultList) {
        currentAssertionReport.addResults(assertionResultList);
    }

    /**
     * Mark the current {@link AssertionReport} as complete status
     */
    public void publishCurrentAssertionResult() {
        if (currentAssertionReport.getCountPass() + currentAssertionReport.getCountFail() != 0) {
            assertionReportList.add(currentAssertionReport);
            currentAssertionReport = new AssertionReport();
            listenerList.forEach(listener -> listener.onAvailableReport(this));
        }
    }

    /**
     * Set the title of the current report
     *
     * @param title Title of the current report
     */
    public void setCurrentAssertionReportTitle(String title) {
        currentAssertionReport.setReportTitle(title);
    }

    /**
     * Get the current list of available {@link AssertionReport}
     *
     * @return List of available {@link AssertionReport}
     */
    public List<AssertionReport> getAssertionReportList() {
        return Collections.unmodifiableList(assertionReportList);
    }

    /**
     * Clear the current list of available {@link AssertionReport}
     */
    public void clearAssertionReports() {
        assertionReportList.clear();
    }

    /* ----- OVERRIDE: KeywordProvider ------------------------------ */
    @Override
    public List<Keyword> getKeywordList() {
        // TODO: Load all classes in package & external jar
        List<Keyword> keywordList = new ArrayList<>();
        keywordList.add(new AssertEqual(this));
        keywordList.add(new Assert(this));
        return keywordList;
    }

    /* ----- SETTERS & GETTERS: LISTENERS ------------------------------ */

    /**
     * Add a listener to listen for events from this assertion engine
     *
     * @param listener Listener to be added
     */
    public void addListener(TSONAssertionEngineListener listener) {
        listenerList.add(listener);
    }
}
