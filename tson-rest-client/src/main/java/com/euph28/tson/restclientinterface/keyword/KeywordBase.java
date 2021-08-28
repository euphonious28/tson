package com.euph28.tson.restclientinterface.keyword;

import com.euph28.tson.interpreter.keyword.Keyword;
import com.euph28.tson.restclientinterface.TSONRestClient;

/**
 * Base {@link Keyword} for use with {@link TSONRestClient}
 */
public abstract class KeywordBase extends Keyword {
    /**
     * Rest client that this {@link Keyword} will interact with
     */
    TSONRestClient tsonRestClient;

    /**
     * Base {@link Keyword} for use with {@link TSONRestClient}
     *
     * @param tsonRestClient Rest client that this {@link Keyword} will interact with
     */
    public KeywordBase(TSONRestClient tsonRestClient) {
        this.tsonRestClient = tsonRestClient;
    }
}
