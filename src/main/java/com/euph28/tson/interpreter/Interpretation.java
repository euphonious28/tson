package com.euph28.tson.interpreter;

import com.euph28.tson.core.keyword.Keyword;
import com.euph28.tson.core.keyword.KeywordType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Interprets and stores the interpreted content of a TSON file
 */
public class Interpretation {
    /* ----- VARIABLES ------------------------------ */
    Logger logger = LoggerFactory.getLogger(Statement.class);

    /**
     * List of interpreted {@link Statement}
     */
    List<Statement> statementList;

    /**
     * Iteration index of the {@link #statementList}. The index points to the next {@link Statement} that should be read.
     * eg: Starts at index=0 as the next item is item0. Retrieving item3 would make index=4 as next item is item4
     */
    int iteratorNextIndex = 0;

    /* ----- CONSTRUCTOR ------------------------------ */

    /**
     * Interpret and store the interpreted content from a TSON content
     *
     * @param content TSON content found within a file
     */
    public Interpretation(List<Keyword> keywordList, String content) {
        statementList = parse(keywordList, content);
    }

    /* ----- METHODS: PARSER ------------------------------ */

    /**
     * Converts a TSON content into a {@link List} of {@link Statement}
     *
     * @param content Content found within the TSON file
     * @return List of {@link Statement} parsed from the {@code content}
     */
    List<Statement> parse(List<Keyword> keywordList, String content) {

        /*
         * PARSER FLOW
         *
         * 1. Setup             : Generate regex from Keywords
         * 2. Pre-processing    : Remove comments
         * 3. Parsing           : Split from one long String into a list of Strings,
         *                        each String starting with a Keyword (Statement String)
         * 4. Post-processing   : Convert from String to Statement
         */

        /* 1. Setup             : Generate regex from Keywords */
        String[] keywordCodeList = keywordList
                .stream()
                .map(Keyword::getCode)
                .toArray(String[]::new);
        String regex = "(?=\\b(" + String.join("|", keywordCodeList) + ")\\b)";

        /* 2. Pre-processing    : Remove comments */
        /* 2 Remove multi-line and single comments (using an OR of both scenarios) */
        content = content.replaceAll("(?://.*?(?:[\\n\\r]|\\z))|(?:/\\*.*?(?:\\*/|\\z))", "");

        /*
         * 3. Parsing           : Split from one long String into a list of Strings,
         *                        each String starting with a Keyword (Statement String)
         */
        List<String> statementStringList = Arrays.asList(content.split(regex));

        /* 4. Post-processing   : Convert from String to Statement (and return) */
        return statementStringList
                .stream()
                .map(s -> s                                             /* 3.1 Cleanup redundant spaces */
                        .trim()                                               // Front and end
                        .replaceAll("[\\t\\n\\r]+", " ")    // Newline
                        .replaceAll("  +", " ")             // Double spaces
                )
                .filter(s -> !s.isEmpty() && !s.equals(" "))            // Remove empty Strings
                .map(s -> {                                             // Convert from String->Statement
                    /* 3.2 Retrieve Keyword */
                    String value = s;
                    Keyword keyword = keywordList
                            .stream()
                            .filter(k -> value.startsWith(k.getCode()))
                            .findFirst()
                            .orElse(null);

                    // Error handling if it failed to resolve
                    if (keyword == null) {
                        logger.error("Failed to parse (Keyword not found) the following statement: " + s);
                        return null;
                    }

                    // Update string to no longer contain Keyword
                    s = s.substring(keyword.getCode().length() + 1);

                    /* 3.3 Retrieve properties */
                    Map<String, String> properties = getProperties(s);

                    return new Statement(keyword, properties, getValueWithoutProperties(s));
                })
                .filter(Objects::nonNull)                               // Remove null objects (keyword mapping failed)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve mapping of properties from content that starts with properties
     *
     * @param content Content to retrieve properties from
     * @return Mapping of properties. This only returns valid mapping if content starts with properties
     */
    Map<String, String> getProperties(String content) {
        Map<String, String> properties = new HashMap<>();
        String contentWithoutProperties = getValueWithoutProperties(content);   // Use a trimmed content to reverse the index of properties

        // Checking that there is properties
        if (contentWithoutProperties.length() == content.length()) {
            return properties;
        }

        // Retrieve properties String (trim brackets)
        String propertiesString = content.substring(0, content.length() - contentWithoutProperties.length()).trim();
        propertiesString = propertiesString.substring(1, propertiesString.length() - 1).trim();

        // Quick return if there is no '=' in title
        if (getIndexOfChar(propertiesString, 0, '=') == -1) {
            properties.put("title", propertiesString);
            return properties;
        }

        // Loop through properties String to retrieve properties
        // TODO: Not do this and make something better
        // Variables
        boolean isQuotes = false;                           // Boolean checking if iterator is currently in a quote
        char currentQuote = 0;                              // Current quote character (if in a quote)
        StringBuilder currentString = new StringBuilder();  // Current generated string
        String key = "";                                    // Current key (value is empty if currentString is building key, filled if building value)
        // Loop
        for (int i = 0; i < propertiesString.length(); i++) {
            char currentChar = propertiesString.charAt(i);

            // Split handling between in and out of quotes
            if (!isQuotes) {    // State: currently NOT in quotes, check if we should enter quotes or split lines
                switch (currentChar) {
                    case '\'':
                    case '"':        // Enter quotes mode if its a quote
                        isQuotes = true;
                        currentQuote = currentChar;
                        currentString.append(currentChar);
                        break;
                    case ' ':      // Enter key-value property
                        properties.put(key.isEmpty() ? "title" : key, currentString.toString());    // Default key is title
                        key = "";
                        currentString.setLength(0);
                        break;
                    case '=':       // Store key and start building value
                        key = currentString.toString();
                        currentString.setLength(0);
                        break;
                    default:
                        currentString.append(currentChar);
                }
            } else {            // State: current IN quotes, check if exiting, otherwise skip everything else
                if (currentChar == currentQuote) {
                    isQuotes = false;
                }
                currentString.append(currentChar);
            }
        }
        // Add the last line
        properties.put(key.isEmpty() ? "title" : key, currentString.toString());    // Default key is title

        return properties;
    }

    /**
     * Retrieve the next instance of the target that is outside of quotes
     *
     * @param content    Content to search in
     * @param startIndex Starting index of content to search in (inclusive)
     * @param target     Target character to look for
     * @return First index of target that is outside of quotes. Returns -1 if it is not found
     */
    int getIndexOfChar(String content, int startIndex, char target) {
        // Variables
        boolean isQuotes = false;       // Boolean checking if iterator is currently in a quote
        char currentQuote = 0;          // Current quote character (if in a quote)

        // Loop through each character
        for (int i = startIndex; i < content.length(); i++) {
            char currentChar = content.charAt(i);

            if (!isQuotes) {        // State: currently NOT in quotes, check if we should enter quotes or split lines
                if (currentChar == '\'' || currentChar == '"') {        // Enter quotes mode if its a quote
                    isQuotes = true;
                } else if (currentChar == target) {                     // Return index if target found
                    return i;
                }
            } else {                // State: current IN quotes, check if exiting, otherwise skip everything else
                if (currentChar == currentQuote) {
                    isQuotes = false;
                }
            }
        }

        return -1;
    }

    /**
     * Retrieve value without properties
     *
     * @param content Content to be parsed
     * @return Returns value String without the starting properties. This only occurs if value starts with properties
     */
    String getValueWithoutProperties(String content) {
        return content.replaceFirst("^\\[.*?(?:\".*?\".*?)*?]", "").trim();
    }

    /* ----- METHODS: ITERATOR ------------------------------ */

    /**
     * Retrieve the next {@link Statement} in the iterator. Does not move the iterator forward
     *
     * @return Returns the next {@link Statement}. Returns {@code null} if it has reached the end (see {@link #isEof()}
     */
    public Statement peek() {
        return !isEof()
                ? statementList.get(iteratorNextIndex)
                : null;
    }

    /**
     * Retrieve the next {@link Statement} in the iterator that has a matching type. Does not move the iterator forward
     *
     * @param targetTypes Valid types to filter for
     * @return Returns the next {@link Statement} that has a type matching with {@code targetTypes}
     * Returns {@code null} if it has reached the end without finding a suitable match
     */
    public Statement peekType(List<KeywordType> targetTypes) {
        return !isEof() ?
                statementList.subList(iteratorNextIndex, statementList.size())
                        .stream()
                        .filter(statement -> targetTypes.contains(statement.getKeyword().getKeywordType()))
                        .findFirst()
                        .orElse(null)
                : null;
    }

    /**
     * Retrieve the next {@link Statement} in the iterator and moves the iterator forward
     *
     * @return Returns the next {@link Statement}. Returns {@code null} if it has reached the end (see {@link #isEof()}
     */
    public Statement getNext() {
        return !isEof()
                ? statementList.get(iteratorNextIndex++)
                : null;
    }

    /**
     * Checks if the iterator has reached the end of the {@link #statementList}
     *
     * @return Returns true if the iterator has iterated through all items
     */
    public boolean isEof() {
        return iteratorNextIndex >= statementList.size();
    }

    /**
     * Reset the iterator to the start
     */
    public void resetIterator() {
        iteratorNextIndex = 0;
    }

    /* ----- METHODS ------------------------------ */

    /**
     * Returns if the interpretation is valid based on the content of {@link #statementList}
     *
     * @return Returns true if there are {@link Statement} interpreted
     */
    public boolean isValid() {
        return !statementList.isEmpty();
    }
}
