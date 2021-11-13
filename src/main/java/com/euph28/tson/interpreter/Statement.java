package com.euph28.tson.interpreter;

import com.euph28.tson.core.keyword.Keyword;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a line within a TSON file. A statement is formed starting with a {@link Keyword} followed by values
 */
public class Statement {
    /* ----- VARIABLES ------------------------------ */
    Logger logger = LoggerFactory.getLogger(Statement.class);

    /**
     * Keyword for this statement
     */
    Keyword keyword;

    /**
     * Values accompanying the {@link Keyword}
     */
    String value;

    /* ----- CONSTRUCTOR ------------------------------ */

    /**
     * Create a statement. Statements are lines within a TSON file that starts with a {@link Keyword} followed
     * by {@code value} that should be passed to the {@link Keyword} to be handled
     *
     * @param keyword The {@link Keyword} of this statement
     * @param value   Values to be passed to the {@link Keyword}
     */
    public Statement(Keyword keyword, String value) {
        this.keyword = keyword;
        this.value = value;
    }

    /* ----- GETTERS ------------------------------ */

    /**
     * Retrieve the keyword of this statement
     *
     * @return Keyword of this statement
     */
    public Keyword getKeyword() {
        return keyword;
    }

    /**
     * Retrieve the value of this statement to be sent to the {@link Keyword}
     *
     * @return Values of this statement
     */
    public String getValue() {
        return value;
    }
}
