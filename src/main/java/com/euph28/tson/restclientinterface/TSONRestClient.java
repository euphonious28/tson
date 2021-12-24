package com.euph28.tson.restclientinterface;

import com.euph28.tson.context.TSONContext;
import com.euph28.tson.context.VariableType;
import com.euph28.tson.context.restdata.RequestData;
import com.euph28.tson.context.restdata.ResponseData;
import com.euph28.tson.core.keyword.Keyword;
import com.euph28.tson.core.keyword.KeywordProvider;
import com.euph28.tson.core.provider.ContentProvider;
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
import java.util.function.Function;

/**
 * Main access point for the TSON Rest Client
 */
public class TSONRestClient implements KeywordProvider {

    final static String PROPERTY_REQUEST_URL = "restclient.url";
    final static String PROPERTY_REQUEST_PORT = "restclient.port";
    final static String PROPERTY_REQUEST_ROUTE = "restclient.route";
    final static String PROPERTY_REQUEST_VERB = "restclient.verb";
    final static String PROPERTY_REQUEST_BODY_PREFIX = "restclient.bodyprefix";

    /* ----- VARIABLES ------------------------------ */
    final Logger logger = LoggerFactory.getLogger(TSONRestClient.class);

    /**
     * Content provider that is able to resolve content for {@link #requestBody}
     */
    ContentProvider contentProvider;

    /**
     * TSONContext for storing/retrieving properties
     */
    TSONContext tsonContext;

    /* ----- VARIABLES: REST REQUEST ------------------------------ */
    /**
     * Request variable: Content body to send in request
     */
    String requestBody = "";

    /* ----- VARIABLES: REST RESPONSE ------------------------------ */
    /**
     * Request data of the last sent request
     */
    RequestData requestData = new RequestData("", "");

    /**
     * Response data of the last received response
     */
    ResponseData responseData = new ResponseData(-1, "", 0);

    /* ----- VARIABLES: LISTENERS ------------------------------ */
    /**
     * List of event listeners
     */
    List<TSONRestClientListener> listenerList = new ArrayList<>();

    /* ----- CONSTRUCTOR ------------------------------ */

    /**
     * Create a REST client adapter to provide and retrieve REST client info
     *
     * @param tsonContext     TSONContext for the run. Used for retrieving/storing properties
     * @param contentProvider Content provider that is able to load content from file. The content provider should
     *                        point to the workspace
     */
    public TSONRestClient(TSONContext tsonContext, ContentProvider contentProvider) {
        this.contentProvider = contentProvider;
        this.tsonContext = tsonContext;

        // Link to context
        tsonContext.setTsonRestClient(this);

        // Generate default properties
        tsonContext.addVariableIfNotExists(VariableType.PROPERTY, PROPERTY_REQUEST_URL, "localhost");
        tsonContext.addVariableIfNotExists(VariableType.PROPERTY, PROPERTY_REQUEST_PORT, "8080");
        tsonContext.addVariableIfNotExists(VariableType.PROPERTY, PROPERTY_REQUEST_ROUTE, "/");
        tsonContext.addVariableIfNotExists(VariableType.PROPERTY, PROPERTY_REQUEST_VERB, "GET");
        tsonContext.addVariableIfNotExists(VariableType.PROPERTY, PROPERTY_REQUEST_BODY_PREFIX, "");
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
        // Retrieve values from property
        String requestUrl = tsonContext.getContent(VariableType.PROPERTY.getPrefix() + "." + PROPERTY_REQUEST_URL);
        String requestPort = tsonContext.getContent(VariableType.PROPERTY.getPrefix() + "." + PROPERTY_REQUEST_PORT);
        String requestRoute = tsonContext.getContent(VariableType.PROPERTY.getPrefix() + "." + PROPERTY_REQUEST_ROUTE);
        String requestVerb = tsonContext.getContent(VariableType.PROPERTY.getPrefix() + "." + PROPERTY_REQUEST_VERB);

        // Connection
        HttpURLConnection connection;
        String urlString = "http://"
                + requestUrl
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
        Duration responseDuration;
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

    /**
     * Add a listener to listen for events from this Rest Client
     *
     * @param listener Listener to be added
     */
    public void addListener(TSONRestClientListener listener) {
        listenerList.add(listener);
    }

    /* ----- SETTERS & GETTERS: REQUEST/RESPONSE DATA ------------------------------ */

    /**
     * Retrieve the last sent request data
     *
     * @return Data of last sent request
     */
    public RequestData getRequestData() {
        return requestData;
    }

    /**
     * Retrieve the last received response data
     *
     * @return Data of last received response
     */
    public ResponseData getResponseData() {
        return responseData;
    }

    /* ----- SETTERS & GETTERS: REQUEST VARIABLES ------------------------------ */

    /**
     * Set the hostname to send the request to
     *
     * @param requestUrl Hostname to send the request to
     */
    public void setRequestUrl(String requestUrl) {
        tsonContext.addVariable(VariableType.PROPERTY, PROPERTY_REQUEST_URL, requestUrl);
    }

    /**
     * Set the port to send the request to
     *
     * @param requestPort Port to send the request to
     */
    public void setRequestPort(String requestPort) {
        tsonContext.addVariable(VariableType.PROPERTY, PROPERTY_REQUEST_PORT, requestPort);
    }

    /**
     * Set the route to send the request to
     *
     * @param requestRoute Route to send the request to
     */
    public void setRequestRoute(String requestRoute) {
        tsonContext.addVariable(VariableType.PROPERTY, PROPERTY_REQUEST_ROUTE, requestRoute);
    }

    /**
     * Set the verb to send the request with
     *
     * @param requestVerb Verb to be used when sending request
     */
    public void setRequestVerb(String requestVerb) {
        tsonContext.addVariable(VariableType.PROPERTY, PROPERTY_REQUEST_VERB, requestVerb);
    }

    /**
     * Set the body content to send in the request. To use the {@link ContentProvider}, use {@link #setRequestBody(String, boolean)}
     *
     * @param requestBody Body content to be used when sending request
     */
    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    /**
     * Set the body content to send in the request.
     * If specified to use the {@link ContentProvider}, the value will be used as a source name for resolving
     *
     * @param requestBody        Body content to be used when sending request/Source name to be resolved for body content
     * @param useContentProvider Specifies if the {@code requestBody} should be sent to the {@link ContentProvider} to be resolved
     */
    public void setRequestBody(String requestBody, boolean useContentProvider) {
        String requestBodyPrefix = tsonContext.getContent(VariableType.PROPERTY.getPrefix() + "." + PROPERTY_REQUEST_BODY_PREFIX);

        if (useContentProvider) {
            setRequestBody(contentProvider.getContent(requestBodyPrefix + requestBody));
        } else {
            setRequestBody(requestBody);
        }
    }

    /**
     * Transform the body content that will be sent in the request
     *
     * @param fn Function to use to transform the body
     */
    public void transformRequestBody(Function<String, String> fn) {
        setRequestBody(fn.apply(requestBody));
    }
}
