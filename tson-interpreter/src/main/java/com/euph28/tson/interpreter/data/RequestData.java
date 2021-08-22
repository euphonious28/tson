package com.euph28.tson.interpreter.data;

/**
 * Data related to request sent to server
 */
public class RequestData {
    /* ----- VARIABLES ------------------------------ */
    String requestUrl;
    String requestBody;

    /* ----- CONSTRUCTOR ------------------------------ */
    public RequestData(String requestUrl, String requestBody) {
        this.requestUrl = requestUrl;
        this.requestBody = requestBody;
    }

    /* ----- SETTERS & GETTERS ------------------------------ */

    public String getRequestUrl() {
        return requestUrl;
    }

    public String getRequestBody() {
        return requestBody;
    }
}
