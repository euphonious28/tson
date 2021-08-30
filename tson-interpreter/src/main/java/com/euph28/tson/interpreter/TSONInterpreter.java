package com.euph28.tson.interpreter;

import com.euph28.tson.interpreter.interpreter.Interpretation;
import com.euph28.tson.interpreter.interpreter.Statement;
import com.euph28.tson.interpreter.keyword.Keyword;
import com.euph28.tson.interpreter.keyword.KeywordProvider;
import com.euph28.tson.interpreter.provider.ContentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main access point for the TSON Interpreter
 */
public class TSONInterpreter {

    /* ----- VARIABLES ------------------------------ */
    Logger logger = LoggerFactory.getLogger(TSONInterpreter.class);

    /**
     * List of {@link KeywordProvider} for generating list of {@link Keyword}
     */
    List<KeywordProvider> keywordProviderList = new ArrayList<>();

    /**
     * List of {@link ContentProvider} that is capable of converting from source name to content
     */
    List<ContentProvider> contentProviderList = new ArrayList<>();

    /**
     * List of keywords that should be used
     */
    List<Keyword> keywordList = new ArrayList<>();

    /**
     * Interpretation of a TSON content. Can be converted to a list in the future for parallel run
     */
    Interpretation interpretation;

    /* ----- CONSTRUCTOR ------------------------------ */

    public TSONInterpreter() {

    }

    /* ----- METHODS: INTERPRETER ------------------------------ */

    /**
     * Load a TSON file from filename into the interpreter
     *
     * @param sourceName File/source name of the content to be loaded
     * @return Returns {@code true} if content was successfully read
     */
    public boolean interpret(String sourceName) {
        // Retrieve content
        String content = getContentFromProvider(sourceName);
        if (content.isEmpty()) {                // Early check if content retrieval failed
            return false;
        }

        // Perform interpretation and store result
        Interpretation interpretation = new Interpretation(getKeywords(), content);
        if (!interpretation.isValid()) {         // Early check if interpretation failed
            return false;
        }
        this.interpretation = interpretation;

        return true;
    }

    /* ----- METHODS: ITERATOR ------------------------------ */

    /**
     * @see Interpretation#peek()
     */
    public Statement peek() {
        return interpretation.peek();
    }

    /**
     * @see Interpretation#peekAction()
     */
    public Statement peekAction() {
        return interpretation.peekAction();
    }

    /**
     * @see Interpretation#getNext()
     */
    public Statement getNext() {
        return interpretation.getNext();
    }

    /**
     * @see Interpretation#isEof()
     */
    public boolean isEof() {
        return interpretation.isEof();
    }

    /**
     * @see Interpretation#resetIterator()
     */
    public void resetIterator() {
        interpretation.resetIterator();
    }

    /* ----- METHODS: PROVIDERS ------------------------------ */

    /**
     * Add a {@link KeywordProvider} for resolving the list of {@link Keyword}
     *
     * @param keywordProvider Provider to be added
     */
    public void addKeywordProvider(KeywordProvider keywordProvider) {
        if (!keywordProviderList.contains(keywordProvider)) {
            // Add to list
            logger.trace("New keyword provider added");
            keywordProviderList.add(keywordProvider);
            // Regenerate keyword list
            keywordList.clear();
            keywordProviderList.forEach(provider -> keywordList.addAll(provider.getKeywordList()));
            logger.debug(String.format("Keyword list (re)generated. %d keywords were found", keywordList.size()));
        }
    }

    /**
     * Retrieve the list of {@link Keyword} provided by the {@link KeywordProvider}
     *
     * @return List of {@link Keyword} provided
     */
    List<Keyword> getKeywords() {
        return keywordProviderList
                .stream()
                .flatMap(provider -> provider.getKeywordList().stream())
                .collect(Collectors.toList());
    }

    /**
     * Add a {@link ContentProvider} that is capable of resolving from source name to content
     *
     * @param contentProvider Provider to be added
     */
    public void addContentProvider(ContentProvider contentProvider) {
        if (!contentProviderList.contains(contentProvider)) {
            contentProviderList.add(contentProvider);
        }
    }

    /**
     * Retrieve content from providers based on source name (using {@link ContentProvider#getContent(String)}
     *
     * @param sourceName Source name to be used for the providers
     * @return Returns the first resolved content. Returns an empty {@code String} if none of the providers were able to resolve
     */
    String getContentFromProvider(String sourceName) {
        logger.trace("Retrieving content for: " + sourceName);
        for (ContentProvider provider : contentProviderList) {
            String result = provider.getContent(sourceName);
            if (!result.isEmpty()) {
                logger.trace("Content found for source: " + sourceName);
                return result;
            }
        }
        logger.info("Failed to retrieve content for source: " + sourceName);
        return "";
    }
}
