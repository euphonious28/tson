package com.euph28.tson.context.provider;

import com.euph28.tson.context.TSONContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Content provider that stores and returns values similar to a key-value map
 */
public class StringMapProvider extends ContentProvider {
    /* ----- VARIABLES ------------------------------ */
    Logger logger = LoggerFactory.getLogger(StringMapProvider.class);

    /**
     * Map storing content data
     */
    Map<String, String> dataMap = new HashMap<>();

    /**
     * Unique prefix for differentiating this provider from other similar ones
     */
    final String contentProviderPrefix;

    /* ----- CONSTRUCTOR ------------------------------ */
    public StringMapProvider(String contentProviderPrefix) {
        this.contentProviderPrefix = contentProviderPrefix;
    }

    /* ----- METHODS ------------------------------ */

    /**
     * Clear existing content data
     */
    public void clear() {
        dataMap.clear();
    }

    /**
     * Store value with the provided key. Existing value with the same key will be overridden
     *
     * @param key   Key to be used when retrieving the value later
     * @param value Value to be stored
     */
    public void add(String key, String value) {
        dataMap.put(key, value);
    }

    /* ----- OVERRIDE: CONTENT PROVIDER ------------------------------ */
    @Override
    public String getPrefix() {
        return contentProviderPrefix;
    }

    @Override
    public String getContent(TSONContext tsonContext, String key) {
        return dataMap.getOrDefault(key, "");
    }
}
