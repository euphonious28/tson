package com.euph28.tson.restclientinterface.keywords;

import com.euph28.tson.interpreter.keyword.Keyword;
import com.euph28.tson.restclientinterface.adapter.TSONRestClientAdapter;

public abstract class KeywordBase extends Keyword {
    TSONRestClientAdapter tsonRestClientAdapter;

    public KeywordBase(TSONRestClientAdapter tsonRestClientAdapter) {
        this.tsonRestClientAdapter = tsonRestClientAdapter;
    }
}
