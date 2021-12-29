package com.euph28.tson.context.provider;

import com.euph28.tson.context.TSONContext;

import java.util.Map;

/**
 * Base class for providing content based on a String
 * Provides a way to inject values into value Strings used in other classes
 */
public interface ContentProvider {

    /**
     * Prefix indicating that the content should be substituted by this class
     *
     * @return Unique prefix String
     */
    String getPrefix();

    /**
     * Retrieve the content for the provided key
     *
     * @param tsonContext TSON context that called for this provider
     * @param key         Key for the content to be retrieved
     * @return Map of path to content. This should return an empty map if nothing was found
     */
    Map<String, String> getContent(TSONContext tsonContext, String key);
}