package com.euph28.tson.assertionengine.listener;

import com.euph28.tson.assertionengine.TSONAssertionEngine;

/**
 * Listener to events within the {@link TSONAssertionEngine}.
 * Listeners should be added using {@link TSONAssertionEngine#addListener(TSONAssertionEngineListener)}
 */
public interface TSONAssertionEngineListener {
    /**
     * Event triggered when there is a new report available in the {@link TSONAssertionEngine}
     *
     * @param tsonAssertionEngine Assertion engine which contains the new report
     */
    void onAvailableReport(TSONAssertionEngine tsonAssertionEngine);
}
