package com.euph28.tson.interpreter.interpreter;

import com.euph28.tson.interpreter.keyword.Keyword;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
    public List<Statement> parse(List<Keyword> keywordList, String content) {

        /*
         * PARSER FLOW
         *
         * 1. Setup for splitting (step 2): Generate regex from Keywords
         * 2. Split from one long String into a list of Strings,
         *    each String starting with a Keyword (Statement String)
         * 3. Convert from String to Statement
         */

        /* 1. Setup for splitting (step 2): Generate regex from Keywords */
        String[] keywordCodeList = keywordList
                .stream()
                .map(Keyword::getCode)
                .toArray(String[]::new);
        String regex = "(?=\\b(" + String.join("|", keywordCodeList) + ")\\b)";

        /*
         * 2. Split from one long String into a list of Strings,
         *    each String starting with a Keyword (Statement String)
         */
        List<String> statementStringList = Arrays.asList(content.split(regex));

        /* 3. Convert from String to Statement (and return) */
        return statementStringList
                .stream()
                .map(s -> s                                             // Clear redundant whitespaces
                        .trim()                                               // Front and end
                        .replaceAll("[\\t\\n\\r]+", " ")    // Newline
                        .replaceAll("  +", " ")             // Double spaces
                )
                .map(s -> {                                             // Convert from String->Statement
                    Keyword keyword = keywordList
                            .stream()
                            .filter(k -> s.startsWith(k.getCode()))
                            .findFirst()
                            .orElse(null);

                    // Error handling if it failed to resolve
                    if(keyword == null) {
                        logger.error("Failed to parse (Keyword not found) the following statement: " + s);
                        return null;
                    }

                    return new Statement(keyword, s.substring(keyword.getCode().length()));
                })
                .filter(Objects::nonNull)                               // Remove null objects (keyword mapping failed)
                .collect(Collectors.toList());
    }

    /* ----- METHODS ------------------------------ */

    /**
     * Returns if the interpretation is valid based on the content of {@link #statementList}
     * @return Returns true if there are {@link Statement} interpreted
     */
    public boolean isValid() {
        return !statementList.isEmpty();
    }
}
