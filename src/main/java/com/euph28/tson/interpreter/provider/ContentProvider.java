package com.euph28.tson.interpreter.provider;

/**
 * Interface for classes that can provide TSON content to the interpreter
 */
public interface ContentProvider {
    /**
     * Retrieve content from a source name
     *
     * @param sourceName Source name that should be used to determine what content to retrieve
     * @return The content associated with the {@code sourceName}
     */
    String getContent(String sourceName);
}
