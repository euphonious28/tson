package com.euph28.tson.context;

import com.euph28.tson.context.keyword.*;
import com.euph28.tson.context.provider.ContentProvider;
import com.euph28.tson.context.provider.JsonValueProvider;
import com.euph28.tson.context.provider.StringMapProvider;
import com.euph28.tson.context.restdata.RequestData;
import com.euph28.tson.context.restdata.ResponseData;
import com.euph28.tson.core.keyword.Keyword;
import com.euph28.tson.core.keyword.KeywordProvider;
import com.euph28.tson.interpreter.TSONInterpreter;
import com.euph28.tson.restclientinterface.TSONRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

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
        keywordList.add(new CustomVariable());
        keywordList.add(new PropertyId());
        keywordList.add(new PropertyDescription());
        keywordList.add(new Sleep());
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

    /**
     * Add a variable if it does not already exist
     *
     * @param variableType Type of variable to be stored. See the documentation in {@link VariableType} for details
     * @param key          Key to be used for retrieving the value later
     * @param value        Value to be stored
     */
    public void addVariableIfNotExists(VariableType variableType, String key, String value) {
        if (!hasContent(variableType.prefix + "." + key)) {
            addVariable(variableType, key, value);
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
            String resolvedText = getContent(contentText, false);

            text = text.substring(0, startIndex)
                    + (resolvedText.isEmpty() ? contentText : resolvedText)
                    + text.substring(endIndex + 1);

            startIndex = text.lastIndexOf(CONTENT_TAG_START);
        }

        return text;
    }

    /**
     * Get content from content text (text within content tags). This uses {@link #getContent(String)} but simplifies the result.
     * For resolving large text that has multiple content tags, use {@link #resolveContent(String)}
     *
     * @param text                 Text to retrieve content with. Text should have the format of {@code <content-provider-prefix>.<key>}
     * @param allowMultipleResults Specify behaviour if multiple results were found. Set to {@code true} to accept the first
     *                             result if multiple were found and {@code false} to receive an empty String instead
     * @return Retrieved text from content provider.
     * Returns empty String if retrieval failed (no matching content provider or no content for key)
     */
    public String getContent(String text, boolean allowMultipleResults) {
        // Retrieve all results from retriever
        Map<String, String> fullResult = getContent(text);

        // Return if there is only one item
        switch (fullResult.size()) {
            case 0:
                return "";
            case 1:
                return fullResult.get(fullResult.keySet().stream().findFirst().orElse(""));
            default:
                if (!allowMultipleResults) {
                    logger.warn("More than one result found when resolving content when it should only find one. Content text: " + text);
                    return "";
                } else {
                    return fullResult.get(fullResult.keySet().stream().findFirst().orElse(""));
                }
        }
    }

    /**
     * Get content from content text (text within content tags).
     * For resolving large text that has multiple content tags, use {@link #resolveContent(String)}
     *
     * @param text Text to retrieve content with. Text can start with a prefix followed by a period to specify which
     *             provider to use. Otherwise, all providers are searched instead
     * @return Map of path to retrieved text. Paths will include the provider as a prefix
     */
    public Map<String, String> getContent(String text) {
        logger.trace("Retrieving content from provider for content text: " + text);
        Map<String, String> result = new LinkedHashMap<>();   // Result that will be populated and returned

        /* 1. Retrieve possible prefix and key */
        int periodIndex = text.indexOf('.');            // Index of first period, used for separating prefix from key
        // Get prefix: text before first period
        String prefix = text.substring(0, periodIndex > -1 ? periodIndex : 0);
        // Key that is given to providers. This key is the same as the text arg but without prefix
        // This is reverted to the text arg if the prefix is found invalid on a later step
        String contentKey = text.substring(periodIndex + 1);

        /* 2. Create list of providers to resolve with (this sets up Step 3) */
        // Retrieve using prefix (multiple is allowed if for some reason we have that scenario)
        List<ContentProvider> targetContentProviderList = contentProviderList
                .stream()
                .filter(provider -> provider.getPrefix().equals(prefix))
                .collect(Collectors.toList());
        // Default to all providers if there is no targets (no matching prefix) found
        if (targetContentProviderList.isEmpty()) {
            targetContentProviderList.addAll(contentProviderList);
            contentKey = text;                                      // Revert content key to text if the prefix was invalid
        }

        /* 3. With the list of providers, retrieve contents from them */
        for (ContentProvider provider : targetContentProviderList) {
            Map<String, String> providerResult = provider.getContent(this, contentKey);
            result.putAll(
                    providerResult.entrySet()
                            .stream()
                            .collect(Collectors.toMap(
                                    map -> provider.getPrefix() + "." + map.getKey(),
                                    Map.Entry::getValue
                            ))
            );
        }

        // Log warning just in case
        if (result.isEmpty()) {
            logger.warn("No result found when resolving content: " + text);
        }
        return result;
    }

    /**
     * Checks if there is content from content text (text with content tag)
     *
     * @param text Text to check for content with. Text should have the format of {@code <content-provider-prefix>.<key>}
     * @return Returns {@code true} if there is content, returns {@code false} if otherwise
     */
    public boolean hasContent(String text) {
        return !getContent(text).isEmpty();
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
