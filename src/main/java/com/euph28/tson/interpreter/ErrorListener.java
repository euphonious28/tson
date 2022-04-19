package com.euph28.tson.interpreter;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic error listener for tracking errors from parsing
 */
public class ErrorListener extends BaseErrorListener {

    /* ----- VARIABLES ------------------------------ */
    Logger logger = LoggerFactory.getLogger(ErrorListener.class);

    /**
     * Indicates if an error has been found, defaults as {@code false} and switched to {@code true} if an error is found
     */
    boolean hasError = false;

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        hasError = true;
        logger.error(msg);
        super.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e);
    }

    public boolean isHasError() {
        return hasError;
    }
}
