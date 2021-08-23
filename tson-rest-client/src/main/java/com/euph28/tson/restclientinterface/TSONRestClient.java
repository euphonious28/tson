package com.euph28.tson.restclientinterface;

import com.euph28.tson.interpreter.data.RequestData;
import com.euph28.tson.interpreter.data.ResponseData;
import com.euph28.tson.interpreter.keyword.Keyword;
import com.euph28.tson.interpreter.keyword.KeywordProvider;
import com.euph28.tson.interpreter.provider.ContentProvider;
import com.euph28.tson.restclientinterface.keyword.KeywordSend;
import com.euph28.tson.restclientinterface.listener.TSONRestClientListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TSONRestClient implements KeywordProvider {
    /* ----- VARIABLES ------------------------------ */
    final Logger logger = LoggerFactory.getLogger(TSONRestClient.class);

    ContentProvider contentProvider;

    /* ----- VARIABLES: REST REQUEST ------------------------------ */
    String requestUrl = "localhost";

    int requestPort = 8080;

    String requestRoute = "/";

    String requestVerb = "POST";

    String requestBody = "";

    /* ----- VARIABLES: REST RESPONSE ------------------------------ */
    RequestData requestData;
    ResponseData responseData;

    /* ----- VARIABLES: LISTENERS ------------------------------ */
    List<TSONRestClientListener> listenerList = new ArrayList<>();

    /* ----- CONSTRUCTOR ------------------------------ */

    /**
     * Create a REST client adapter to provide and retrieve REST client info
     *
     * @param contentProvider Content provider that is able to load content from file
     */
    public TSONRestClient(ContentProvider contentProvider) {
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

    /* ----- REST CLIENT ------------------------------ */

    /**
     * Send the REST request
     */
    public void send() {

        // Connection
        HttpURLConnection connection;
        String urlString = requestUrl
                + ":"
                + requestPort
                + (requestRoute.startsWith("/") ? "" : "/")
                + requestRoute;

        // Setup request
        try {
            // Create connection objects
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();

            // Customize connection
            connection.setRequestMethod(requestVerb);
            connection.addRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);

            // Connection body
            connection.setDoOutput(true);
            OutputStream outputStream = connection.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            outputStreamWriter.write(requestBody);
            outputStreamWriter.flush();
            outputStreamWriter.close();
            outputStream.close();

        } catch (MalformedURLException e) {
            logger.error("Failed to send request. URL is invalid: " + urlString, e);
            return;
        } catch (IOException e) {
            logger.error("Failed to send request.", e);
            return;
        }

        // Send request
        Duration responseDuration = null;
        try {
            // Trigger listeners (before send)
            listenerList.forEach(listener -> listener.onBeforeSend(this));

            // Response duration: start
            Instant instantStart = Instant.now();

            // Connect
            connection.connect();

            // Response duration: end
            responseDuration = Duration.between(instantStart, Instant.now());

            // Trigger listeners (after send)
            listenerList.forEach(listener -> listener.onAfterSend(this, true));
        } catch (IOException e) {
            logger.error("Failed to connect to remote host at: " + urlString, e);
            return;
        }

        // Read results
        try {
            // Read response body
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder inputBuffer = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                inputBuffer.append(inputLine);
            }
            in.close();

            // Generate result objects
            requestData = new RequestData(urlString, requestBody);
            responseData = new ResponseData(
                    connection.getResponseCode(),
                    inputBuffer.toString(),
                    responseDuration.getNano()
            );

        } catch (IOException e) {
            logger.error("Failed to read response results", e);
        }
    }
    /* ----- SETTERS & GETTERS: LISTENERS ------------------------------ */

    public void addListener(TSONRestClientListener listener) {
        listenerList.add(listener);
    }

    /* ----- SETTERS & GETTERS: REQUEST/RESPONSE DATA ------------------------------ */

    public RequestData getRequestData() {
        return requestData;
    }

    public ResponseData getResponseData() {
        return responseData;
    }

    /* ----- SETTERS & GETTERS: REQUEST VARIABLES ------------------------------ */

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
