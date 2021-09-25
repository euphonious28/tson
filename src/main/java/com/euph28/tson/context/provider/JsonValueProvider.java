package com.euph28.tson.context.provider;

import com.euph28.tson.context.TSONContext;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Load content value from JSON data. Accepts JSON path in the form of period delimited paths (eg: {@code body.text.item})
 * and from JSON Pointer (eg: {@code /body/text/item})
 */
public class JsonValueProvider implements ContentProvider {

    /* ----- VARIABLES ------------------------------ */
    Logger logger = LoggerFactory.getLogger(JsonValueProvider.class);

    /* ----- METHODS: JSON ------------------------------ */

    /**
     * Retrieve JSON content to be used based on jsonPath
     *
     * @param tsonContext Context for retrieving available JSON content
     * @param jsonPath    JSON path that will determine what JSON content to use
     * @return JSON content as requested by {@code jsonPath}. Returns request JSON if path starts with {@code request.}
     * and response JSON if otherwise.
     */
    String getJsonContent(TSONContext tsonContext, String jsonPath) {
        return jsonPath.startsWith("request.")
                ? tsonContext.getRequestData().getRequestBody()
                : tsonContext.getResponseData().getResponseBody();
    }

    /**
     * Format path to be compatible with {@link JsonPointer#valueOf(String)} requirements
     *
     * @param jsonPath Path to be cleaned up
     * @return Returns path that follows format of {@link JsonPointer#valueOf(String)}
     */
    String updatePath(String jsonPath) {
        // Trim jsonPath if it starts with request/response
        jsonPath = jsonPath.startsWith("request.") ? jsonPath.substring("request.".length()) : jsonPath;
        jsonPath = jsonPath.startsWith("response.") ? jsonPath.substring("response.".length()) : jsonPath;

        // Update jsonPath to JsonPointer formatting (only convert if there is no '/' symbol, if it has, it means JsonPointer was already intended)
        jsonPath = jsonPath.contains("/") ? jsonPath : jsonPath.replace('.', '/');
        jsonPath = jsonPath.startsWith("/") ? jsonPath : "/" + jsonPath;

        return jsonPath;
    }

    /**
     * Retrieve a map of path-value from JSON content and path
     *
     * @param tsonContext Context class that stores the variables related to the current running state
     * @param jsonPath    Path to the value. Path can be separated by periods (eg: body.item.0.value) or in the
     *                    form of a JSON Pointer  (eg: {@code /body/item/0/value}).<br/>
     *                    Wildcards ({@code *}) can be used to retrieve all values in an array. <br/>
     *                    Starting the jsonPath with "{@code request.}" will result in request JSON being used.
     *                    Otherwise, response data will be used instead
     * @return Map of path-to-value of resolved values. Returns {@code null} if path was invalid
     */
    public Map<String, String> getValuesFromJson(TSONContext tsonContext, String jsonPath) {
        // Store original path for logging
        String originalPath = jsonPath;

        // Retrieve jsonContent and update path
        String jsonContent = getJsonContent(tsonContext, jsonPath);
        jsonPath = updatePath(jsonPath);

        // Retrieve value from jsonPath
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, JsonNode> nodeMap = new LinkedHashMap<>();
        try {
            // Initial node
            nodeMap.put("", objectMapper.readTree(jsonContent));

            // Generate list of path to traverse
            String[] jsonPathSplit = jsonPath.split("/");

            // Traverse each path and update node
            for (String path : jsonPathSplit) {
                // Updated node map, will replace the original at end of each iteration
                Map<String, JsonNode> updatedNodeMap = new LinkedHashMap<>();

                // Traverse each node to traverse each path (handles splitting when wildcard is present)
                for (String nodeMapKey : nodeMap.keySet()) {
                    JsonNode node = nodeMap.get(nodeMapKey);

                    if (path.equals("*") && node.isArray()) {   // Scenario: Split into individual elements in array if wildcard is used
                        for (int i = 0; i < node.size(); i++) {
                            updatedNodeMap.put(nodeMapKey + "/" + i, node.get(i));
                        }
                    } else {                                    // Scenario: Default scenario to just take direct value
                        updatedNodeMap.put(
                                nodeMapKey + "/" + path,
                                path.matches("-?\\d+") ? node.get(Integer.parseInt(path)) : node.get(path)
                        );
                    }
                }

                nodeMap = updatedNodeMap;
            }
        } catch (JsonProcessingException e) {
            logger.error("Failed to process provided JSON for path: " + originalPath, e);
            return null;
        } catch (NullPointerException e) {
            logger.error(String.format("Failed to resolve pointer path \"%s\", provided by JSON path \"%s\"", jsonPath, originalPath), e);
            return null;
        }

        // Post-processing: Convert from node to path-value map
        Map<String, String> pathValueResultMap = new LinkedHashMap<>();
        for (String key : nodeMap.keySet()) {
            try {
                pathValueResultMap.put(
                        key.startsWith("/") ? key : "/" + key,
                        nodeMap.get(key).asText()
                );
            } catch (NullPointerException e) {
                logger.error(String.format("Failed to resolve pointer path \"%s\", provided by JSON path \"%s\"", jsonPath, originalPath), e);
                return null;
            }
        }

        return pathValueResultMap;
    }

    /**
     * Retrieve a value from JSON content and path
     *
     * @param tsonContext Context class that stores the variables related to the current running state
     * @param jsonPath    Path to the value. Path can be separated by periods (eg: body.item.0.value) or in the
     *                    form of a JSON Pointer  (eg: {@code /body/item/0/value}).<br/>
     *                    Path does not accept wildcards <br/>
     *                    Starting the jsonPath with "{@code request.}" will result in request JSON being used.
     *                    Otherwise, response data will be used instead
     * @return Value located at path. Returns an empty String if path is invalid
     */
    public String getValueFromJson(TSONContext tsonContext, String jsonPath) {
        // Store original path for logging
        String originalPath = jsonPath;

        // Retrieve jsonContent and update path
        String jsonContent = getJsonContent(tsonContext, jsonPath);
        jsonPath = updatePath(jsonPath);

        // Retrieve value from jsonPath
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonContent).at(JsonPointer.valueOf(jsonPath));
            if (!jsonNode.isMissingNode()) {
                return jsonNode.toString();
            } else {
                return "";
            }
        } catch (JsonProcessingException e) {
            logger.error(String.format("Failed to resolve JsonPointer \"%s\", provided by path \"%s\"", jsonPath, originalPath), e);
            return "";
        }
    }

    /* ----- OVERRIDE: CONTENT PROVIDER ------------------------------ */
    @Override
    public String getPrefix() {
        return "json";
    }

    @Override
    public String getContent(TSONContext tsonContext, String key) {
        return null;
    }
}
