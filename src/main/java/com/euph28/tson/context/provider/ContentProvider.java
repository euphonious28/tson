package com.euph28.tson.context.provider;

import com.euph28.tson.context.TSONContext;

/**
 * Base class for providing content based on a String
 * Provides a way to inject values into value Strings used in other classes
 */
public abstract class ContentProvider {

    /**
     * Prefix indicating that the content should be substituted by this class
     *
     * @return Unique prefix String
     */
    public abstract String getPrefix();

    /**
     * Retrieve the substitute content for the provided key
     *
     * @param tsonContext TSON context that called for this provider
     * @param key         Key for the content to be retrieved
     * @return Content that should replace the key. This should return an empty String if key is invalid
     */
    public abstract String getContent(TSONContext tsonContext, String key);
}