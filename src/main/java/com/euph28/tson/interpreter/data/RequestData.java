package com.euph28.tson.interpreter.data;

/**
 * Data related to request sent to server
 */
public class RequestData {
    /* ----- VARIABLES ------------------------------ */
    /**
     * URL that was sent for the request. The URL follows format for creating a {@link java.net.URL} object
     */
    String requestUrl;

    /**
     * Content body sent for the request
     */
    String requestBody;

    /* ----- CONSTRUCTOR ------------------------------ */

    /**
     * Data related to request sent to server
     *
     * @param requestUrl  URL that was sent for the request. The URL should follow format for creating a {@link java.net.URL} object
     * @param requestBody Content body sent for the request
     */
    public RequestData(String requestUrl, String requestBody) {
        this.requestUrl = requestUrl;
        this.requestBody = requestBody;
    }

    /* ----- SETTERS & GETTERS ------------------------------ */

    /**
     * Retrieve the URL that was sent for the request
     *
     * @return URL used for the request
     */
    public String getRequestUrl() {
        return requestUrl;
    }

    /**
     * Retrieve the content body sent for the request
     *
     * @return Body of the request
     */
    public String getRequestBody() {
        return requestBody;
    }
}
