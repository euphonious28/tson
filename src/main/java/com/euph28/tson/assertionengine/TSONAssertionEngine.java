package com.euph28.tson.assertionengine;

import com.euph28.tson.assertionengine.keyword.Assert;
import com.euph28.tson.assertionengine.keyword.assertion.AssertEqual;
import com.euph28.tson.assertionengine.keyword.assertion.AssertNotEqual;
import com.euph28.tson.assertionengine.keyword.assertion.AssertRange;
import com.euph28.tson.assertionengine.keyword.assertion.AssertRegex;
import com.euph28.tson.assertionengine.listener.TSONAssertionEngineListener;
import com.euph28.tson.core.keyword.Keyword;
import com.euph28.tson.core.keyword.KeywordProvider;
import com.euph28.tson.reporter.TSONReporter;

import java.util.ArrayList;
import java.util.List;

/**
 * Entry access point for the TSON Assertion Engine
 */
public class TSONAssertionEngine implements KeywordProvider {
    /* ----- VARIABLES ------------------------------ */
    /**
     * TSONReporter that should be used for reporting
     */
    TSONReporter currentReporter = null;

    /**
     * List of event listeners
     */
    List<TSONAssertionEngineListener> listenerList = new ArrayList<>();

    /* ----- METHODS: Assertion result handling ------------------------------ */

    /**
     * Retrieve the reporter to be used. Returns a stored reporter if {@link #setReporter(TSONReporter)}  was used beforehand.
     * Otherwise, returns the same reporter
     *
     * @param tsonReporter Reporter that was provided to the {@link Keyword}
     * @return Reporter that should be used to reporting. This reporter will have the correct report tree position
     */
    public TSONReporter getReporter(TSONReporter tsonReporter) {
        if (currentReporter == null) {  // Return the provided reporter if there is no current reporter (ASSERT not called)
            return tsonReporter;
        } else {                        // Otherwise, return the sub-report of provided reporter
            TSONReporter reporter = currentReporter.createSubReport(tsonReporter.getReport());
            tsonReporter.delete();
            return reporter;
        }
    }

    /**
     * Set the current reporter to a different reporter
     *
     * @param tsonReporter New reporter to be used for subsequent reports
     */
    public void setReporter(TSONReporter tsonReporter) {
        currentReporter = tsonReporter;
    }

    /**
     * Remove tracking of current reporter so the next entry will use a new reporter (and generate a new report)
     */
    public void doCompleteReport() {
        // Remove record of current reporter to get a new reporter
        currentReporter = null;
    }

    /* ----- OVERRIDE: KeywordProvider ------------------------------ */
    @Override
    public List<Keyword> getKeywordList() {
        // TODO: Load all classes in package & external jar
        List<Keyword> keywordList = new ArrayList<>();
        keywordList.add(new AssertEqual(this));
        keywordList.add(new AssertNotEqual(this));
        keywordList.add(new AssertRegex(this));
        keywordList.add(new AssertRange(this));
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
