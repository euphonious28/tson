package com.euph28.tson.interpreter.keyword;

import java.util.List;

/**
 * Interface for classes that can provide {@link Keyword} to process in TSON content
 */
public interface KeywordProvider {

    /**
     * Retrieve a list of {@link Keyword} that should be processed by the interpreter
     *
     * @return List of {@link Keyword}
     */
    List<Keyword> getKeywordList();
}