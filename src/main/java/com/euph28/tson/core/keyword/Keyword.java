package com.euph28.tson.core.keyword;

import com.euph28.tson.context.TSONContext;
import com.euph28.tson.interpreter.Statement;
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
    public abstract String getLspDescriptionShort();

    /**
     * Retrieve a long description of what this {@link Keyword} performs. Used for LSP and documentation
     *
     * @return Long description of this {@link Keyword}
     */
    public abstract String getLspDescriptionLong();

    /**
     * Retrieve list of tags that this {@link Keyword} is related to. When the user enters text related to the tag,
     * this {@link Keyword} will be recommended. Used for LSP and documentation
     *
     * @return List of words that should recommend {@link Keyword}
     */
    public List<String> getLspTags() {
        return new ArrayList<>();
    }

    /* ----- BEHAVIOUR ------------------------------ */

    /**
     * Retrieve the type of the {@link Keyword}
     *
     * @return Type of this {@link Keyword}
     */
    public abstract KeywordType getKeywordType();

    /* ----- HANDLERS ------------------------------ */

    /**
     * Handle the processing of this {@link Keyword}
     *
     * @param tsonContext  Context class that stores the variables related to the current running state
     * @param tsonReporter Reporter class to report execution result to
     * @param statement        Additional value for this {@link Keyword} provided in the TSON file
     * @return Returns true if handle was successful
     */
    public abstract boolean handle(TSONContext tsonContext, TSONReporter tsonReporter, Statement statement);

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
