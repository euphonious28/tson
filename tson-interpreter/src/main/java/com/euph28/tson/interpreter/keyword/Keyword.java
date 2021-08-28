package com.euph28.tson.interpreter.keyword;

import com.euph28.tson.interpreter.data.RequestData;
import com.euph28.tson.interpreter.data.ResponseData;

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

    /* ----- HANDLERS ------------------------------ */

    /**
     * Handle the processing of this {@link Keyword}
     *
     * @param requestData  Request data of the last sent request
     * @param responseData Response data of the last received response
     * @param value        Additional value for this {@link Keyword} provided in the TSON file
     * @return Returns true if handle was successful
     */
    public abstract boolean handle(RequestData requestData, ResponseData responseData, String value);

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
