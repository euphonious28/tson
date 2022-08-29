package com.euph28.tson.core;

import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class that performs actions that have no specific category
 */
public class Utility {

    /**
     * Version of TSON
     */
    static String version = "";

    public static String getVersion() {
        // Load version from manifest if its empty
        if (version.isEmpty()) {
            // Retrieve version from manifest (code from: https://stackoverflow.com/a/49456168)
            version = Utility.class.getPackage().getImplementationVersion();

            // Check if no version
            if(version == null) {
                version = "no-version";
                LoggerFactory.getLogger(Utility.class).error("Failed to retrieve TSON version. Defaulting to " + version + " instead");
            }
        }

        // Return version
        return version;
    }

    /**
     * Split a text by delimiter character, respecting quotes
     *
     * @param text         Text to be split
     * @param delimiter    Delimiter to use when splitting
     * @param removeQuotes Boolean on whether quotes should be removed after splitting
     * @return Array of String split from the {@code text}
     */
    public static String[] split(String text, char delimiter, boolean removeQuotes) {
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
}
