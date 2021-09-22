package com.euph28.tson.assertionengine.keyword.assertion;

import com.euph28.tson.assertionengine.TSONAssertionEngine;
import com.euph28.tson.assertionengine.result.AssertionResult;
import com.euph28.tson.context.TSONContext;
import com.euph28.tson.context.provider.JsonValueProvider;
import com.euph28.tson.interpreter.interpreter.Statement;
import com.euph28.tson.interpreter.keyword.Keyword;

import java.util.*;

/**
 * Base class of an Assertion Keyword
 */
public abstract class AssertionBase extends Keyword {

    /* ----- VARIABLES ------------------------------ */
    /**
     * Assertion engine for this {@link Keyword}. The engine is used for receiving shared values and returning results
     */
    TSONAssertionEngine tsonAssertionEngine;

    /**
     * List of assertion results
     */
    private final List<AssertionResult> assertionResultList = new ArrayList<>();

    /* ----- CONSTRUCTOR ------------------------------ */

    /**
     * Base of an Assertion Keyword
     *
     * @param tsonAssertionEngine TSON Assertion Engine that should handle results of this assertion
     */
    AssertionBase(TSONAssertionEngine tsonAssertionEngine) {
        this.tsonAssertionEngine = tsonAssertionEngine;
    }

    /* ----- METHODS: UTILITY ------------------------------ */

    /**
     * Record a pass assertion
     *
     * @param description Description on the pass
     */
    protected void resultPass(String description) {
        assertionResultList.add(new AssertionResult(this, true, description));
    }

    /**
     * Record a fail assertion
     *
     * @param description Description on the fail
     */
    protected void resultFail(String description) {
        assertionResultList.add(new AssertionResult(this, false, description));
    }

    /**
     * Split a text by delimiter character, respecting quotes
     *
     * @param text         Text to be split
     * @param delimiter    Delimiter to use when splitting
     * @param removeQuotes Boolean on whether quotes should be removed after splitting
     * @return Array of String split from the {@code text}
     */
    protected String[] split(String text, char delimiter, boolean removeQuotes) {
        List<String> result = new ArrayList<>();

        // Initialize variables
        boolean isQuotes = false;                           // Boolean checking if iterator is currently in a quote
        char currentQuote = 0;                              // Current quote character (if in a quote)
        int indexStart = 0;                                 // Index of the start of the current string
        StringBuilder currentString = new StringBuilder();  // Current generated string

        // Loop through each character
        for (int i = 0; i < text.length(); i++) {
            char currentChar = text.charAt(i);

            // Split handling between in and out of quotes
            if (!isQuotes) {    // State: currently NOT in quotes, check if we should enter quotes or split lines
                if (currentChar == '\'' || currentChar == '"') {        // Enter quotes mode if its a quote
                    isQuotes = true;
                    currentQuote = currentChar;

                    // Update current string with values before the quote (and quote if needed)
                    currentString.append(text, indexStart, removeQuotes ? i : i + 1);
                    indexStart = i + 1;
                } else if (currentChar == delimiter) {                  // Split text if it's a delimiter instead
                    currentString.append(text, indexStart, i);
                    result.add(currentString.toString());
                    currentString.setLength(0);
                    indexStart = i + 1;
                }
            } else {            // State: current IN quotes, check if exiting, otherwise skip everything else
                if (currentChar == currentQuote) {
                    isQuotes = false;

                    // Update current string with values from the quote (and quote if needed)
                    currentString.append(text, indexStart, removeQuotes ? i : i + 1);
                    indexStart = i + 1;
                }
            }
        }
        // Add the last line
        currentString.append(text.substring(indexStart));
        result.add(currentString.toString());

        return result.toArray(new String[0]);
    }

    /**
     * Retrieve value from a jsonContent and a jsonPath
     *
     * @param tsonContext  Context class that stores the variables related to the current running state
     * @param jsonPath     Path to the value. Path is separated by colons (eg: body.item.0.value).
     *                     Wildcards can be used to retrieve all values in an array
     * @return Map of path-to-value of resolved values. Returns {@code null} if path was invalid
     */
    protected Map<String, String> getValueFromJson(TSONContext tsonContext, String jsonPath) {
        JsonValueProvider jsonValueProvider = new JsonValueProvider();
        return jsonValueProvider.getValuesFromJson(tsonContext, jsonPath);
    }

    /* ----- METHODS ------------------------------ */

    /**
     * Handle the assertion based on the provided data. Use {@link #resultPass(String)} and {@link #resultFail(String)}
     * to report assertion results
     *
     * @param tsonContext  Context class that stores the variables related to the current running state
     * @param value        Additional value for this {@link Keyword} provided in the TSON file
     * @return Returns true if handle was successful
     */
    protected abstract boolean handleAssertion(TSONContext tsonContext, String value);

    /* ----- OVERRIDE: KEYWORD BASE ------------------------------ */

    /**
     * Calls {@link #handleAssertion(TSONContext, String)} and returns results to {@link #tsonAssertionEngine}. <br/>
     * Do not override unless you plan to alter how data is returned to {@link #tsonAssertionEngine}
     */
    @Override
    public boolean handle(TSONContext tsonContext, String value) {
        // Reset result list
        assertionResultList.clear();

        // Perform assertion (and populate assertionResultList)
        boolean status = handleAssertion(tsonContext, value);

        // Report result to AssertionEngine
        tsonAssertionEngine.addAssertionResult(assertionResultList);

        // Check if AssertionEngine should publish result (publish if next action is not an assertion)
        Statement nextStatementAction = tsonContext.getTsonInterpreter().peekAction();
        if (nextStatementAction == null || !(nextStatementAction.getKeyword() instanceof AssertionBase)) {
            tsonAssertionEngine.publishCurrentAssertionResult();
        }

        return status;
    }

    @Override
    public boolean isAction() {
        return true;
    }
}
