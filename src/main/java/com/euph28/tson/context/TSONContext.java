package com.euph28.tson.context;

import com.euph28.tson.context.keyword.PropertyDescription;
import com.euph28.tson.context.keyword.PropertyId;
import com.euph28.tson.context.keyword.RequestVariable;
import com.euph28.tson.context.keyword.ResponseVariable;
import com.euph28.tson.context.provider.ContentProvider;
import com.euph28.tson.context.provider.JsonValueProvider;
import com.euph28.tson.context.provider.StringMapProvider;
import com.euph28.tson.context.restdata.RequestData;
import com.euph28.tson.context.restdata.ResponseData;
import com.euph28.tson.interpreter.TSONInterpreter;
import com.euph28.tson.core.keyword.Keyword;
import com.euph28.tson.core.keyword.KeywordProvider;
import com.euph28.tson.restclientinterface.TSONRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Context class that stores all variables related to the current state
 */
public class TSONContext implements KeywordProvider {
    /* ----- CONSTANTS ------------------------------ */
    final static String CONTENT_TAG_START = "${";
    final static String CONTENT_TAG_END = "}";

    /* ----- VARIABLES ------------------------------ */
    Logger logger = LoggerFactory.getLogger(TSONContext.class);

    /**
     * Current TSON Interpreter
     */
    TSONInterpreter tsonInterpreter;

    /**
     * TSON Rest client that handles send and receive data
     */
    TSONRestClient tsonRestClient;

    /**
     * Map of {@link StringMapProvider}
     */
    Map<VariableType, StringMapProvider> variablesMap = new HashMap<>();

    /**
     * List of content providers
     */
    List<ContentProvider> contentProviderList = new ArrayList<>();

    /**
     * List of provided keywords
     */
    List<Keyword> keywordList = new ArrayList<>();

    /* ----- CONSTRUCTOR ------------------------------ */
    public TSONContext() {
        // Initialize default providers
        addContentProvider(new JsonValueProvider());

        StringMapProvider providerVariable = new StringMapProvider(VariableType.VARIABLE.prefix);
        addContentProvider(providerVariable);
        variablesMap.put(VariableType.VARIABLE, providerVariable);

        StringMapProvider providerProperty = new StringMapProvider(VariableType.PROPERTY.prefix);
        addContentProvider(providerProperty);
        variablesMap.put(VariableType.PROPERTY, providerProperty);

        // Initialize keywords
        keywordList.add(new RequestVariable());
        keywordList.add(new ResponseVariable());
        keywordList.add(new PropertyId());
        keywordList.add(new PropertyDescription());
    }

    public TSONContext(TSONInterpreter tsonInterpreter, TSONRestClient tsonRestClient) {
        this();
        this.tsonInterpreter = tsonInterpreter;
        this.tsonRestClient = tsonRestClient;
    }

    /* ----- METHODS: VARIABLES ------------------------------ */

    /**
     * Store variable to be retrieved later. Retrieve variable using {@link #getContent(String)} with the text
     * format ${{@link VariableType#prefix}.{@code <text>}}
     *
     * @param variableType Type of variable to be stored. See the documentation in {@link VariableType} for details
     * @param key          Key to be used for retrieving the value later
     * @param value        Value to be stored
     */
    public void addVariable(VariableType variableType, String key, String value) {
        StringMapProvider provider = variablesMap.get(variableType);
        if (provider != null) {
            provider.add(key, value);
        }
    }

    /* ----- METHODS: CONTENT PROVIDER ------------------------------ */

    public void addContentProvider(ContentProvider contentProvider) {
        if (!contentProviderList.contains(contentProvider)) {
            contentProviderList.add(contentProvider);
        }
    }

    /**
     * Resolve content tags (items marked by symbols {@code ${}}) within the provided String and return the String with tags resolved into actual value
     *
     * @param text Text to be resolved
     * @return Returns {@code text} with items marked with content tags resolved.
     * Items that fail to resolve will be returned without tag symbols
     */
    public String resolveContent(String text) {
        // Keep resolving until there is no more content tag
        int startIndex = text.lastIndexOf(CONTENT_TAG_START);
        while (startIndex >= 0) {
            int endIndex = text.indexOf(CONTENT_TAG_END, startIndex + 2);

            String contentText = text.substring(startIndex + 2, endIndex);
            String resolvedText = getContent(contentText);

            text = text.substring(0, startIndex)
                    + (resolvedText.isEmpty() ? contentText : resolvedText)
                    + text.substring(endIndex + 1);

            startIndex = text.lastIndexOf(CONTENT_TAG_START);
        }

        return text;
    }

    /**
     * Get content from content text (text within content tags).
     * For resolving large text that has multiple content tags, use {@link #resolveContent(String)}
     *
     * @param text Text to retrieve content with. Text should have the format of {@code <content-provider-prefix>.<key>}
     * @return Retrieved text from content provider.
     * Returns empty String if retrieval failed (no matching content provider or no content for key)
     */
    public String getContent(String text) {
        logger.trace("Retrieving content from provider for content text: " + text);
        String[] splitText = text.split("\\.", 2);

        // Early check: If there is not 2 parts in the text, there isn't enough to continue
        if (splitText.length != 2) {
            logger.debug(String.format("Retrieval of content from content provider for text \"%s\" failed due to invalid text structure", text));
            return "";
        }

        // Look for content provider
        ContentProvider contentProvider = contentProviderList
                .stream()
                .filter(provider -> provider.getPrefix().equals(splitText[0]))
                .findFirst()
                .orElse(null);

        // Error check: No content provider found
        if (contentProvider == null) {
            logger.debug(String.format("Retrieval of content from content provider for text \"%s\" failed due to invalid provider prefix", text));
            return "";
        }

        // Retrieve from provider and return (remove the prefix + .)
        return contentProvider.getContent(this, text.substring(contentProvider.getPrefix().length() + 1));
    }

    /* ----- GETTERS & SETTERS: REQUEST, RESPONSE, INTERPRETER ------------------------------ */

    public RequestData getRequestData() {
        return tsonRestClient.getRequestData();
    }

    public ResponseData getResponseData() {
        return tsonRestClient.getResponseData();
    }

    public TSONInterpreter getTsonInterpreter() {
        return tsonInterpreter;
    }

    public void setTsonInterpreter(TSONInterpreter tsonInterpreter) {
        this.tsonInterpreter = tsonInterpreter;
    }

    public void setTsonRestClient(TSONRestClient tsonRestClient) {
        this.tsonRestClient = tsonRestClient;
    }

    /* ----- OVERRIDE: KEYWORD PROVIDER ------------------------------ */
    @Override
    public List<Keyword> getKeywordList() {
        return keywordList;
    }
}
