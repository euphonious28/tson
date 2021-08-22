package com.euph28.tson.restclientinterface.keyword;

import com.euph28.tson.interpreter.data.RequestData;
import com.euph28.tson.interpreter.data.ResponseData;
import com.euph28.tson.restclientinterface.TSONRestClient;

public class KeywordSend extends KeywordBase {

    /* ----- CONSTRUCTOR ------------------------------ */
    public KeywordSend(TSONRestClient tsonRestClient) {
        super(tsonRestClient);
    }

    /* ----- OVERRIDE: KeywordBase ------------------------------ */
    @Override
    public String getCode() {
        return "SEND";
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
        tsonRestClient.setRequestBody(value, true);
        return true;
    }
}
