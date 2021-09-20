package com.euph28.tson.interpreter.data;

import com.euph28.tson.interpreter.TSONInterpreter;

/**
 * Data class that stores all variables related to the current state
 */
public class Data {
    /* ----- VARIABLES ------------------------------ */
    /**
     * Data of the last sent request
     */
    RequestData requestData;

    /**
     * Data of the last received response
     */
    ResponseData responseData;

    /**
     * Current TSON Interpreter
     */
    TSONInterpreter tsonInterpreter;

    /* ----- CONSTRUCTOR ------------------------------ */
    public Data() {
    }

    /* ----- GETTERS & SETTERS ------------------------------ */

    public RequestData getRequestData() {
        return requestData;
    }

    public void setRequestData(RequestData requestData) {
        this.requestData = requestData;
    }

    public ResponseData getResponseData() {
        return responseData;
    }

    public void setResponseData(ResponseData responseData) {
        this.responseData = responseData;
    }

    public TSONInterpreter getTsonInterpreter() {
        return tsonInterpreter;
    }

    public void setTsonInterpreter(TSONInterpreter tsonInterpreter) {
        this.tsonInterpreter = tsonInterpreter;
    }
}
