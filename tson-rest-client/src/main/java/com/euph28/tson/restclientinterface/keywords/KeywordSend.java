package com.euph28.tson.restclientinterface.keywords;

import com.euph28.tson.interpreter.data.RequestData;
import com.euph28.tson.interpreter.data.ResponseData;
import com.euph28.tson.restclientinterface.adapter.TSONRestClientAdapter;

public class KeywordSend extends KeywordBase {

    /* ----- CONSTRUCTOR ------------------------------ */
    public KeywordSend(TSONRestClientAdapter tsonRestClientAdapter) {
        super(tsonRestClientAdapter);
    }

    /* ----- OVERRIDE: KeywordBase ------------------------------ */
    @Override
    public String getCode() {
        return "JSON";
    }

    @Override
    public String getDescriptionShort() {
        return "JSON request";
    }

    @Override
    public String getDescriptionLong() {
        return "Path to JSON body to be sent";
    }

    @Override
    public boolean handle(RequestData requestData, ResponseData responseData, String value) {
        tsonRestClientAdapter.setRequestBody(value, true);
        return true;
    }
}
