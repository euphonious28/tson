package com.euph28.tson.assertionengine.keyword.assertion;

import com.euph28.tson.assertionengine.TSONAssertionEngine;
import com.euph28.tson.assertionengine.result.AssertionResult;
import com.euph28.tson.interpreter.TSONInterpreter;
import com.euph28.tson.interpreter.data.RequestData;
import com.euph28.tson.interpreter.data.ResponseData;
import com.euph28.tson.interpreter.interpreter.Statement;
import com.euph28.tson.interpreter.keyword.Keyword;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Base class of an Assertion Keyword
 */
public abstract class AssertionBase extends Keyword {

    /* ----- VARIABLES ------------------------------ */
    /**
     * Assertion engine for this {@link Keyword}. The engine is used for receiving shared values and returning results
     */
    TSONAssertionEngine tsonAssertionEngine;

    /**
     * List of assertion results
     */
    private final List<AssertionResult> assertionResultList = new ArrayList<>();

    /* ----- CONSTRUCTOR ------------------------------ */

    /**
     * Base of an Assertion Keyword
     *
     * @param tsonAssertionEngine TSON Assertion Engine that should handle results of this assertion
     */
    AssertionBase(TSONAssertionEngine tsonAssertionEngine) {
        this.tsonAssertionEngine = tsonAssertionEngine;
    }

    /* ----- METHODS: UTILITY ------------------------------ */

    /**
     * Record a pass assertion
     *
     * @param description Description on the pass
     */
    protected void resultPass(String description) {
        assertionResultList.add(new AssertionResult(this, true, description));
    }

    /**
     * Record a fail assertion
     *
     * @param description Description on the fail
     */
    protected void resultFail(String description) {
        assertionResultList.add(new AssertionResult(this, false, description));
    }

    /**
     * Split a text by delimiter character, respecting quotes
     *
     * @param text         Text to be split
     * @param delimiter    Delimiter to use when splitting
     * @param removeQuotes Boolean on whether quotes should be removed after splitting
     * @return Array of String split from the {@code text}
     */
    protected String[] split(String text, char delimiter, boolean removeQuotes) {
        List<String> result = new ArrayList<>();

        // Initialize variables
        boolean isQuotes = false;                           // Boolean checking if iterator is currently in a quote
        char currentQuote = 0;                              // Current quote character (if in a quote)
        int indexStart = 0;                                 // Index of the start of the current string
        StringBuilder currentString = new StringBuilder();  // Current generated string

        // Loop through each character
        for (int i = 0; i < text.length(); i++) {
            char currentChar = text.charAt(i);

            // Split handling between in and out of quotes
            if (!isQuotes) {    // State: currently NOT in quotes, check if we should enter quotes or split lines
                if (currentChar == '\'' || currentChar == '"') {        // Enter quotes mode if its a quote
                    isQuotes = true;
                    currentQuote = currentChar;

                    // Update current string with values before the quote (and quote if needed)
                    currentString.append(text, indexStart, removeQuotes ? i : i + 1);
                    indexStart = i + 1;
                } else if (currentChar == delimiter) {                  // Split text if it's a delimiter instead
                    currentString.append(text, indexStart, i);
                    result.add(currentString.toString());
                    currentString.setLength(0);
                    indexStart = i + 1;
                }
            } else {            // State: current IN quotes, check if exiting, otherwise skip everything else
                if (currentChar == currentQuote) {
                    isQuotes = false;

                    // Update current string with values from the quote (and quote if needed)
                    currentString.append(text, indexStart, removeQuotes ? i : i + 1);
                    indexStart = i + 1;
                }
            }
        }
        // Add the last line
        currentString.append(text.substring(indexStart));
        result.add(currentString.toString());

        return result.toArray(new String[0]);
    }

    /**
     * Retrieve value from a jsonContent and a jsonPath
     *
     * @param requestData  Request data containing request JSON
     * @param responseData Response data containing response JSON
     * @param jsonPath     Path to the value. Path is separated by colons (eg: body.item.0.value).
     *                     Wildcards can be used to retrieve all values in an array
     * @return Map of path-to-value of resolved values. Returns {@code null} if path was invalid
     */
    protected Map<String, String> getValueFromJson(RequestData requestData, ResponseData responseData, String jsonPath) {

        // Retrieve jsonContent (if jsonPath starts with request.xxx, it'll be from request. Otherwise, it's from response)
        String jsonContent = jsonPath.startsWith("request.") ? requestData.getRequestBody() : responseData.getResponseBody();

        // Trim jsonPath if it starts with request/response
        jsonPath = jsonPath.startsWith("request.") ? jsonPath.substring("request.".length()) : jsonPath;
        jsonPath = jsonPath.startsWith("response.") ? jsonPath.substring("response.".length()) : jsonPath;

        // Retrieve value from jsonPath
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, JsonNode> nodeMap = new LinkedHashMap<>();
        try {
            // Convert to map of jsonPath-jsonNode in order to handle splitting from wildcards
            nodeMap.put("", objectMapper.readTree(jsonContent));

            // Generate list of path to traverse
            String[] jsonPathSplit = split(jsonPath, '.', true);

            // Traverse each path
            for (String path : jsonPathSplit) {
                // Updated node map, will replace the original once done updating
                Map<String, JsonNode> updatedNodeMap = new LinkedHashMap<>();

                // Traverse each node to traverse each path (handles splitting when wildcard is present)
                for (String nodeMapKey : nodeMap.keySet()) {
                    JsonNode jsonNode = nodeMap.get(nodeMapKey);

                    if (path.equals("*") && jsonNode.isArray()) {   // Scenario: Split into individual elements in array if wildcard is used
                        for (int i = 0; i < jsonNode.size(); i++) {
                            updatedNodeMap.put(nodeMapKey + "." + i, jsonNode.get(i));
                        }
                    } else {                                        // Scenario: Default scenario to just take direct value
                        updatedNodeMap.put(
                                nodeMapKey + "." + path,
                                path.matches("-?\\d+") ? jsonNode.get(Integer.parseInt(path)) : jsonNode.get(path)
                        );
                    }
                }
                nodeMap = updatedNodeMap;
            }
        } catch (JsonProcessingException e) {
            Logger logger = LoggerFactory.getLogger(this.getClass());
            logger.error("Failed to process provided JSON", e);
            return null;
        } catch (NullPointerException e) {
            Logger logger = LoggerFactory.getLogger(this.getClass());
            logger.error("Failed to resolve the following JSON path: " + jsonPath, e);
            return null;
        }

        // Post-processing: Convert to path-value map from path-node map
        Map<String, String> pathValueResultMap = new LinkedHashMap<>();
        for (String key : nodeMap.keySet()) {
            try {
                pathValueResultMap.put(
                        key.startsWith(".") ? key.substring(1) : key,
                        nodeMap.get(key).asText()
                );
            } catch (NullPointerException e) {
                Logger logger = LoggerFactory.getLogger(this.getClass());
                logger.error("Failed to resolve the following JSON path: " + jsonPath, e);
                return null;
            }
        }

        return pathValueResultMap;
    }

    /**
     * Split a String into individual expressions and handle the expressions. Expressions should be made up of a JSON path
     * and one or more values, each separated by the same delimiter
     *
     * @param requestData             Request data object
     * @param responseData            Response data object
     * @param entryDelimiter          Delimiter to be used for splitting the entries
     * @param expressionDelimiter     Delimiter to be used for splitting individual values within an entry
     * @param value                   Value/input String to be parsed
     * @param simpleAssertionProvider Assertion provider that describes how the assertion should be handled
     */
    protected void simpleAssertion(RequestData requestData, ResponseData responseData,
                                   char entryDelimiter, char expressionDelimiter,
                                   String value, SimpleAssertionProvider simpleAssertionProvider) {
        // Split into entries
        String[] values = split(value, entryDelimiter, false);

        for (String s : values) {
            // Split into before and after operands
            String[] splitValues = split(s, expressionDelimiter, true);
            // Retrieve JSON path
            String path;
            try {
                path = simpleAssertionProvider.getPathFromExpression(splitValues);
            } catch (ArrayIndexOutOfBoundsException e) {
                Logger logger = LoggerFactory.getLogger(this.getClass());
                logger.error("Failed to retrieve path from expression array: " + Arrays.toString(splitValues), e);
                resultFail("Failed to retrieve path for expression: " + s);
                continue;
            }
            // Map of path-to-actualValue
            Map<String, String> actualValue = getValueFromJson(requestData, responseData, path);

            // Error handling: Checking if values failed to be resolved
            if (actualValue == null) {
                resultFail("Failed to retrieve values for JSON path: " + path);
                continue;
            }

            // Verify for each values
            for (String key : actualValue.keySet()) {
                // Wrap in try/catch in case expression is accessed without handling array index
                try {
                    if (simpleAssertionProvider.getAssertionResult(splitValues, actualValue.get(key), key)) {
                        resultPass(simpleAssertionProvider.getPassMessage(splitValues, actualValue.get(key), key));
                    } else {
                        resultFail(simpleAssertionProvider.getFailMessage(splitValues, actualValue.get(key), key));
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    Logger logger = LoggerFactory.getLogger(this.getClass());
                    logger.error("Failed to handle assertion of expression array: " + Arrays.toString(splitValues), e);
                    resultFail("Failed to assert expression: " + s);
                }
            }
        }
    }

    /* ----- METHODS ------------------------------ */

    /**
     * Handle the assertion based on the provided data. Use {@link #resultPass(String)} and {@link #resultFail(String)}
     * to report assertion results
     *
     * @param requestData  Request data of the last sent request
     * @param responseData Response data of the last received response
     * @param value        Additional value for this {@link Keyword} provided in the TSON file
     * @return Returns true if handle was successful
     */
    protected abstract boolean handleAssertion(RequestData requestData, ResponseData responseData, String value);

    /* ----- OVERRIDE: KEYWORD BASE ------------------------------ */

    /**
     * Calls {@link #handleAssertion(RequestData, ResponseData, String)} and returns results to {@link #tsonAssertionEngine}. <br/>
     * Do not override unless you plan to alter how data is returned to {@link #tsonAssertionEngine}
     */
    @Override
    public boolean handle(TSONInterpreter tsonInterpreter, RequestData requestData, ResponseData responseData, String value) {
        // Reset result list
        assertionResultList.clear();

        // Perform assertion (and populate assertionResultList)
        boolean status = handleAssertion(requestData, responseData, value);

        // Report result to AssertionEngine
        tsonAssertionEngine.addAssertionResult(assertionResultList);

        // Check if AssertionEngine should publish result (publish if next action is not an assertion)
        Statement nextStatementAction = tsonInterpreter.peekAction();
        if (nextStatementAction == null || !(nextStatementAction.getKeyword() instanceof AssertionBase)) {
            tsonAssertionEngine.publishCurrentAssertionResult();
        }

        return status;
    }

    @Override
    public boolean isAction() {
        return true;
    }

    /* ----- INTERFACE ------------------------------ */

    /**
     * Basic assertion provider which describes how a simple assertion should be handled
     */
    interface SimpleAssertionProvider {

        /**
         * Retrieve the JSON path from an array of expression values
         *
         * @param expressionValues Array of values that has been retrieved from an expression
         * @return The JSON path from the {@code expressionValues} that should be used by the assertion
         * @throws ArrayIndexOutOfBoundsException Throws exception if the index of the expression chosen is invalid
         */
        String getPathFromExpression(String[] expressionValues) throws ArrayIndexOutOfBoundsException;

        /**
         * Handle the assertion based on the provided values
         *
         * @param expressionValues Array of values that has been retrieved from an expression
         * @param actualValue      The actual value that was found at the JSON path within the {@code expressionValues}.
         *                         The JSON path is retrieved using {@link #getPathFromExpression(String[])}
         * @param path             Actual path of the {@code actualValue}. This value will be different from the JSON path if wildcards were used
         * @return Returns {@code true} if the assertion was successful and {@code false} if assertion failed/faced an error
         * @throws ArrayIndexOutOfBoundsException Throws exception if the index of the expression chosen is invalid
         */
        boolean getAssertionResult(String[] expressionValues, String actualValue, String path) throws ArrayIndexOutOfBoundsException;

        /**
         * Message to be used when the assertion passes
         *
         * @param expressionValues Array of values that has been retrieved from an expression
         * @param actualValue      The actual value that was found at the JSON path within the {@code expressionValues}.
         *                         The JSON path is retrieved using {@link #getPathFromExpression(String[])}
         * @param path             Actual path of the {@code actualValue}. This value will be different from the JSON path if wildcards were used
         * @return Pass message to be used when logging a {@link #resultPass(String)}
         * @throws ArrayIndexOutOfBoundsException Throws exception if the index of the expression chosen is invalid
         */
        String getPassMessage(String[] expressionValues, String actualValue, String path) throws ArrayIndexOutOfBoundsException;

        /**
         * Message to be used when the assertion fails
         *
         * @param expressionValues Array of values that has been retrieved from an expression
         * @param actualValue      The actual value that was found at the JSON path within the {@code expressionValues}.
         *                         The JSON path is retrieved using {@link #getPathFromExpression(String[])}
         * @param path             Actual path of the {@code actualValue}. This value will be different from the JSON path if wildcards were used
         * @return Failure message to be used when logging a {@link #resultFail(String)}
         * @throws ArrayIndexOutOfBoundsException Throws exception if the index of the expression chosen is invalid
         */
        String getFailMessage(String[] expressionValues, String actualValue, String path) throws ArrayIndexOutOfBoundsException;
    }
}
