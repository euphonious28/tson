package com.euph28.tson.assertionengine;

import com.euph28.tson.assertionengine.assertion.AssertEqual;
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
    List<AssertionReport> assertionReportList = new ArrayList<>();

    List<TSONAssertionEngineListener> listenerList = new ArrayList<>();

    /* ----- METHODS: Assertion result handling ------------------------------ */
    public void addAssertionResult(List<AssertionResult> assertionResultList) {
        assertionReportList.add(new AssertionReport(assertionResultList));
        listenerList.forEach(listener -> listener.onAvailableReport(this));
    }

    public List<AssertionReport> getAssertionReportList() {
        return Collections.unmodifiableList(assertionReportList);
    }

    public void clearAssertionReports() {
        assertionReportList.clear();
    }

    /* ----- OVERRIDE: KeywordProvider ------------------------------ */
    @Override
    public List<Keyword> getKeywordList() {
        // TODO: Load all classes in package & external jar
        List<Keyword> keywordList = new ArrayList<>();
        keywordList.add(new AssertEqual(this));
        return keywordList;
    }

    /* ----- SETTERS & GETTERS: LISTENERS ------------------------------ */
    public void addListener(TSONAssertionEngineListener listener) {
        listenerList.add(listener);
    }
}
