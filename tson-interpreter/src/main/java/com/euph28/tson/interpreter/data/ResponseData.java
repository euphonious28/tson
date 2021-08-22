package com.euph28.tson.interpreter.data;

/**
 * Data related to response received from server
 */
public class ResponseData {
    /* ----- VARIABLES ------------------------------ */
    String responseBody;
    int responseStatus;

    /* ----- CONSTRUCTOR ------------------------------ */
    public ResponseData(int responseStatus, String responseBody) {
        this.responseStatus = responseStatus;
        this.responseBody = responseBody;
    }

    /* ----- SETTERS & GETTERS ------------------------------ */

    public int getResponseStatus() {
        return responseStatus;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
