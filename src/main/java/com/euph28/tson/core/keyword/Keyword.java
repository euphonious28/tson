package com.euph28.tson.core.keyword;

import com.euph28.tson.context.TSONContext;
import com.euph28.tson.reporter.TSONReporter;

import java.util.ArrayList;
import java.util.List;

/**
 * Keyword located within a TSOn file. Keywords mark actions that needs to be processed
 */
public abstract class Keyword {

    /* ----- GETTERS: SYNTAX ------------------------------ */

    /**
     * Retrieve the code version of this {@link Keyword}. This is the text used within a TSON file
     *
     * @return Code form of this {@link Keyword}
     */
    public abstract String getCode();

    /**
     * Retrieve a short description of what this {@link Keyword} performs. Used for LSP and documentation
     *
     * @return Short description of this {@link Keyword}
     */
    public abstract String getDescriptionShort();

    /**
     * Retrieve a long description of what this {@link Keyword} performs. Used for LSP and documentation
     *
     * @return Long description of this {@link Keyword}
     */
    public abstract String getDescriptionLong();

    /* ----- GETTERS: BEHAVIOUR ------------------------------ */

    /**
     * Behaviour attribute on whether this {@link Keyword} performs an action (eg: Assertion, Send)
     *
     * @return Returns {@code true} if this {@link Keyword} performs an action
     */
    public abstract boolean isAction();

    /* ----- METHODS: UTILITY ------------------------------ */
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

    /* ----- HANDLERS ------------------------------ */

    /**
     * Handle the processing of this {@link Keyword}
     *
     * @param tsonContext  Context class that stores the variables related to the current running state
     * @param tsonReporter Reporter class to report execution result to
     * @param value Additional value for this {@link Keyword} provided in the TSON file
     * @return Returns true if handle was successful
     */
    public abstract boolean handle(TSONContext tsonContext, TSONReporter tsonReporter, String value);

    /* ----- OVERRIDE: EQUALS ------------------------------ */

    @Override
    public boolean equals(Object obj) {
        // Return true if it's the same object
        if (obj == this) {
            return true;
        }

        // Type checking (early return if different type)
        if (!(obj instanceof Keyword)) {
            return false;
        }

        // Check that it's the same code
        Keyword keyword = (Keyword) obj;
        return this.getCode().equals(keyword.getCode());
    }
}
