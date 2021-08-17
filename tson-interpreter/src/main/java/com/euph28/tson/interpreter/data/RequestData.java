package com.euph28.tson.interpreter.data;

/**
 * Data related to request sent to server
 */
public class RequestData {
    String body;

    public RequestData(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }
}
