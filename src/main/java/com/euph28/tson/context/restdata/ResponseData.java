package com.euph28.tson.context.restdata;

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
     * Time that connection started
     */
    long timeStart;

    /**
     * Time that connection was successful
     */
    long timeConnect;

    /**
     * Time that the response started to be received
     */
    long timeResponse;

    /**
     * Time that connection ended
     */
    long timeEnd;

    /* ----- CONSTRUCTOR ------------------------------ */

    /**
     * Empty response data
     */
    public ResponseData() {
        this(0, "", 0, 0, 0, 0);
    }

    /**
     * Data related to response received from server
     *
     * @param responseStatus Status code of the response
     * @param responseBody   Content body of the response
     * @param timeStart      Time connection started in nanoseconds
     * @param timeConnect    Time connection was successful in nanoseconds
     * @param timeResponse   Time initial response received in nanoseconds
     * @param timeEnd        Time connection ended in nanoseconds
     */
    public ResponseData(int responseStatus, String responseBody, long timeStart, long timeConnect, long timeResponse, long timeEnd) {
        this.responseStatus = responseStatus;
        this.responseBody = responseBody;
        this.timeStart = timeStart;
        this.timeConnect = timeConnect;
        this.timeResponse = timeResponse;
        this.timeEnd = timeEnd;
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
     * Retrieve the time connection started
     *
     * @return Time connection started in nanoseconds
     */
    public long getTimeStart() {
        return timeStart;
    }

    /**
     * Retrieve the time connection was successful
     *
     * @return Time connection was successful in nanoseconds
     */
    public long getTimeConnect() {
        return timeConnect;
    }

    /**
     * Retrieve the time initial response was received
     *
     * @return Time initial response received in nanoseconds
     */
    public long getTimeResponse() {
        return timeResponse;
    }

    /**
     * Retrieve the time connection ended
     *
     * @return Time connection ended in nanoseconds
     */
    public long getTimeEnd() {
        return timeEnd;
    }
}
