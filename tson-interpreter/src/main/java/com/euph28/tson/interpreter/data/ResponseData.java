package com.euph28.tson.interpreter.data;

/**
 * Data related to response received from server
 */
public class ResponseData {
    /* ----- VARIABLES ------------------------------ */
    String responseBody;
    int responseStatus;

    /**
     * Response duration in nanoseconds
     */
    int responseDuration;

    /* ----- CONSTRUCTOR ------------------------------ */
    public ResponseData(int responseStatus, String responseBody, int responseDuration) {
        this.responseStatus = responseStatus;
        this.responseBody = responseBody;
        this.responseDuration = responseDuration;
    }

    /* ----- SETTERS & GETTERS ------------------------------ */

    public int getResponseStatus() {
        return responseStatus;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public int getResponseDuration() {
        return responseDuration;
    }
}
