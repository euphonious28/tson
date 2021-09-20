package com.euph28.tson.interpreter.data;

/**
 * Data related to response received from server
 */
public class ResponseData {
    /* ----- VARIABLES ------------------------------ */

    /**
     * Status code of the response
     */
    int responseStatus;

    /**
     * Content body of the response
     */
    String responseBody;

    /**
     * Response duration in nanoseconds
     */
    int responseDuration;

    /* ----- CONSTRUCTOR ------------------------------ */

    /**
     * Data related to response received from server
     *
     * @param responseStatus   Status code of the response
     * @param responseBody     Content body of the response
     * @param responseDuration Response duration in nanoseconds
     */
    public ResponseData(int responseStatus, String responseBody, int responseDuration) {
        this.responseStatus = responseStatus;
        this.responseBody = responseBody;
        this.responseDuration = responseDuration;
    }

    /* ----- SETTERS & GETTERS ------------------------------ */

    /**
     * Retrieve the status code of the response
     *
     * @return Status code of the response
     */
    public int getResponseStatus() {
        return responseStatus;
    }

    /**
     * Retrieve the content body of the response
     *
     * @return Content body of the response
     */
    public String getResponseBody() {
        return responseBody;
    }

    /**
     * Retrieve the duration for the response
     *
     * @return Response duration in nanoseconds
     */
    public int getResponseDuration() {
        return responseDuration;
    }
}
