package com.euph28.tson.interpreter;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorListener extends BaseErrorListener {
    Logger logger = LoggerFactory.getLogger(ErrorListener.class);

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        logger.error(msg);
        super.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e);
    }
}
