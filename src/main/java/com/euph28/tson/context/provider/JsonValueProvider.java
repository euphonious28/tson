package com.euph28.tson.context.provider;

import com.euph28.tson.context.TSONContext;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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
    Map<String, String> getValuesFromJson(TSONContext tsonContext, String jsonPath) {
        // Store original path for logging
        String originalPath = jsonPath;

        // Retrieve jsonContent and update path
        String jsonContent = getJsonContent(tsonContext, jsonPath);
        jsonPath = updatePath(jsonPath);

        // Setup required variables
        ObjectMapper objectMapper = new ObjectMapper();             // JSON library mapper
        Map<String, JsonNode> nodeMap = new LinkedHashMap<>();      // Map of path-to-nodes that will be returned. This is traversed until path is resolved
        String[] jsonPathSplit = jsonPath.split("/");         // List of paths to traverse

        // Retrieve the first node
        try {
            // Initial node
            nodeMap.put("", objectMapper.readTree(jsonContent));
        } catch (JsonProcessingException e) {
            logger.error("Failed to process provided JSON for path: " + originalPath, e);
            return new LinkedHashMap<>();
        }

        // Traverse each path and update node
        for (String path : jsonPathSplit) {
            // Skip if empty (needed for first index due to path starting with "/", keeping it in case of human error)
            if (path.isEmpty()) {
                continue;
            }

            // Temporary updated node map for this iteration, will replace the original at end of each iteration
            Map<String, JsonNode> iterationNodeMap = new LinkedHashMap<>();

            // Traverse each node to traverse each path (handles splitting when wildcard is present)
            for (String nodeMapKey : nodeMap.keySet()) {
                JsonNode currentNode = nodeMap.get(nodeMapKey);

                // Part 1: Generate new list of paths for this node (if it splits)
                List<String> currentNodePathList = new ArrayList<>();
                if (path.equals("*") && currentNode.isArray()) {    // Generate new node paths if this is an array and wildcard is used
                    for (int i = 0; i < currentNode.size(); i++) {
                        currentNodePathList.add(String.valueOf(i));
                    }
                } else {
                    currentNodePathList.add(path);
                }

                // Part 2: Retrieve the new nodes based on the new list of paths
                for (String currentNodePath : currentNodePathList) {
                    JsonNode value = currentNodePath.matches("-?\\d+") ? currentNode.get(Integer.parseInt(currentNodePath)) : currentNode.get(currentNodePath);
                    if (value != null) {
                        // Only add if value is not null
                        iterationNodeMap.put(nodeMapKey + "/" + currentNodePath, value);
                    } else {
                        // Otherwise, log a warning
                        logger.error(String.format("Null value found when resolving pointer path \"%s\", provided by JSON path \"%s\"", nodeMapKey + "/" + currentNodePath, originalPath));
                    }
                }
            }

            nodeMap = iterationNodeMap;
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
                return new LinkedHashMap<>();
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
     * @deprecated Use {@link #getValuesFromJson(TSONContext, String)} instead
     */
    String getValueFromJson(TSONContext tsonContext, String jsonPath) {
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
    public Map<String, String> getContent(TSONContext tsonContext, String key) {
        return getValuesFromJson(tsonContext, key);
    }
}
