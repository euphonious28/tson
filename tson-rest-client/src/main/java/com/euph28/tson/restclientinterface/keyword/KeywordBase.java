package com.euph28.tson.restclientinterface.keyword;

import com.euph28.tson.interpreter.keyword.Keyword;
import com.euph28.tson.restclientinterface.TSONRestClient;

public abstract class KeywordBase extends Keyword {
    TSONRestClient tsonRestClient;

    public KeywordBase(TSONRestClient tsonRestClient) {
        this.tsonRestClient = tsonRestClient;
    }
}
