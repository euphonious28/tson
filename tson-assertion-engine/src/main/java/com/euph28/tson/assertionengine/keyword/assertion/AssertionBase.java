package com.euph28.tson.assertionengine.keyword.assertion;

import com.euph28.tson.assertionengine.TSONAssertionEngine;
import com.euph28.tson.assertionengine.result.AssertionResult;
import com.euph28.tson.interpreter.TSONInterpreter;
import com.euph28.tson.interpreter.data.RequestData;
import com.euph28.tson.interpreter.data.ResponseData;
import com.euph28.tson.interpreter.interpreter.Statement;
import com.euph28.tson.interpreter.keyword.Keyword;

import java.util.ArrayList;
import java.util.List;

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

    /* ----- METHODS ------------------------------ */

    /**
     * Handle the assertion based on the provided data. Use {@link #resultPass(String)} and {@link #resultFail(String)}
     * to report assertion results
     *
     * @param requestData  Request data of the last sent request
     * @param responseData Response data of the last received response
     * @param value        Additional value for this {@link Keyword} provided in the TSON file
     * @return Returns true if handle was successful
     */
    protected abstract boolean handleAssertion(RequestData requestData, ResponseData responseData, String value);

    /* ----- OVERRIDE: KEYWORD BASE ------------------------------ */

    /**
     * Calls {@link #handleAssertion(RequestData, ResponseData, String)} and returns results to {@link #tsonAssertionEngine}. <br/>
     * Do not override unless you plan to alter how data is returned to {@link #tsonAssertionEngine}
     */
    @Override
    public boolean handle(TSONInterpreter tsonInterpreter, RequestData requestData, ResponseData responseData, String value) {
        // Reset result list
        assertionResultList.clear();

        // Perform assertion (and populate assertionResultList)
        boolean status = handleAssertion(requestData, responseData, value);

        // Report result to AssertionEngine
        tsonAssertionEngine.addAssertionResult(assertionResultList);

        // Check if AssertionEngine should publish result (publish if next action is not an assertion)
        Statement nextStatementAction = tsonInterpreter.peekAction();
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
