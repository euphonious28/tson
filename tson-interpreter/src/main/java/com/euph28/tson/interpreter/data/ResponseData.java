package com.euph28.tson.interpreter.data;

/**
 * Data related to response received from server
 */
public class ResponseData {
    String body;

    public ResponseData(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }
}
