package com.euph28.tson.interpreter.provider;

/**
 * Interface for classes that can provide TSON content to the interpreter
 */
public interface ContentProvider {
    String getContent(String sourceName);
}
