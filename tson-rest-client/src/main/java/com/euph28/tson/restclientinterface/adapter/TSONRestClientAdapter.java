package com.euph28.tson.restclientinterface.adapter;

import com.euph28.tson.interpreter.keyword.Keyword;
import com.euph28.tson.interpreter.keyword.KeywordProvider;
import com.euph28.tson.interpreter.provider.ContentProvider;
import com.euph28.tson.restclientinterface.keywords.KeywordSend;

import java.util.ArrayList;
import java.util.List;

public class TSONRestClientAdapter implements KeywordProvider {
    /* ----- VARIABLES ------------------------------ */
    ContentProvider contentProvider;

    /* ----- VARIABLES: REST ------------------------------ */
    String requestUrl;

    int requestPort;

    String requestRoute;

    String requestVerb;

    String requestBody;

    /* ----- CONSTRUCTOR ------------------------------ */

    /**
     * Create a REST client adapter to provide and retrieve REST client info
     *
     * @param contentProvider Content provider that is able to load content from file
     */
    public TSONRestClientAdapter(ContentProvider contentProvider) {
        this.contentProvider = contentProvider;
    }

    /* ----- OVERRIDE: KeywordProvider ------------------------------ */
    @Override
    public List<Keyword> getKeywordList() {
        // TODO: Load all classes in package
        List<Keyword> keywordList = new ArrayList<>();
        keywordList.add(new KeywordSend(this));
        return keywordList;
    }

    /* ----- SETTERS & GETTERS ------------------------------ */

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public int getRequestPort() {
        return requestPort;
    }

    public void setRequestPort(int requestPort) {
        this.requestPort = requestPort;
    }

    public String getRequestRoute() {
        return requestRoute;
    }

    public void setRequestRoute(String requestRoute) {
        this.requestRoute = requestRoute;
    }

    public String getRequestVerb() {
        return requestVerb;
    }

    public void setRequestVerb(String requestVerb) {
        this.requestVerb = requestVerb;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public void setRequestBody(String requestBody, boolean useContentProvider) {
        if (useContentProvider) {
            setRequestBody(contentProvider.getContent(requestBody));
        } else {
            setRequestBody(requestBody);
        }
    }
}
