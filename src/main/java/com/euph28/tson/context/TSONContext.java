package com.euph28.tson.context;

import com.euph28.tson.context.provider.ContentProvider;
import com.euph28.tson.context.restdata.RequestData;
import com.euph28.tson.context.restdata.ResponseData;
import com.euph28.tson.interpreter.TSONInterpreter;

import java.util.List;

/**
 * Context class that stores all variables related to the current state
 */
public class TSONContext {
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

    /**
     * List of content providers
     */
    List<ContentProvider> contentProviderList;

    /* ----- CONSTRUCTOR ------------------------------ */
    public TSONContext() {
    }

    /* ----- METHODS: CONTENT PROVIDER ------------------------------ */

    public void addContentProvider(ContentProvider contentProvider) {
        if (!contentProviderList.contains(contentProvider)) {
            contentProviderList.add(contentProvider);
        }
    }

    /**
     * Resolve content tags (items marked by symbols {@code ${}}) within the provided String and return the String with tags resolved into actual value
     * @param text Text to be resolved
     * @return Returns {@code text} with items marked with content tags resolved
     */
    public String resolveContent(String text) {
        return "";
    }

    /* ----- GETTERS & SETTERS: REQUEST, RESPONSE, INTERPRETER ------------------------------ */

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
