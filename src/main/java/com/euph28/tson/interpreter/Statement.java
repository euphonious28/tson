package com.euph28.tson.interpreter;

import com.euph28.tson.core.keyword.Keyword;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

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
     * Mapping of additional properties in value
     */
    Map<String, String> properties;

    /**
     * Values accompanying the {@link Keyword}
     */
    String value;

    /* ----- CONSTRUCTOR ------------------------------ */

    /**
     * Create a statement. Statements are lines within a TSON file that starts with a {@link Keyword} followed
     * by {@code value} that should be passed to the {@link Keyword} to be handled
     *
     * @param keyword    The {@link Keyword} of this statement
     * @param properties Map of properties for this statement
     * @param value      Values to be passed to the {@link Keyword}
     */
    public Statement(Keyword keyword, Map<String, String> properties, String value) {
        this.keyword = keyword;
        this.properties = properties != null ? properties : new HashMap<>();
        this.value = value != null ? value : "";
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
     * Retrieve property value of the provided key
     *
     * @param key          Key for the value
     * @param defaultValue Default value to retrieve if there is no entry
     * @return Value of the provided key
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getOrDefault(key, defaultValue);
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
