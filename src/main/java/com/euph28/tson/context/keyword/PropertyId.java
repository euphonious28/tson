package com.euph28.tson.context.keyword;

import com.euph28.tson.context.TSONContext;
import com.euph28.tson.context.VariableType;
import com.euph28.tson.core.keyword.Keyword;
import com.euph28.tson.reporter.TSONReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility keyword: ID Property
 * <p>
 * Set the Test ID property
 */
public class PropertyId extends Keyword {
    /* ----- VARIABLES ------------------------------ */
    Logger logger = LoggerFactory.getLogger(PropertyId.class);

    /* ----- OVERRIDE: KEYWORD ------------------------------ */
    @Override
    public String getCode() {
        return "ID";
    }

    @Override
    public String getDescriptionShort() {
        return "Set the ID of this test";
    }

    @Override
    public String getDescriptionLong() {
        return "Set the ID of this test. Subsequent calls will override the existing ID";
    }

    @Override
    public boolean isAction() {
        return false;
    }

    @Override
    public boolean handle(TSONContext tsonContext, TSONReporter tsonReporter, String value) {
        tsonContext.addVariable(VariableType.PROPERTY, "id", value);
        return true;
    }
}
