package com.euph28.tson.context.keyword;

import com.euph28.tson.context.TSONContext;
import com.euph28.tson.context.VariableType;
import com.euph28.tson.core.keyword.Keyword;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility keyword: Description Property
 * <p>
 * Set the Test description property
 */
public class PropertyDescription extends Keyword {
    /* ----- VARIABLES ------------------------------ */
    Logger logger = LoggerFactory.getLogger(PropertyDescription.class);

    /* ----- OVERRIDE: KEYWORD ------------------------------ */
    @Override
    public String getCode() {
        return "DESC";
    }

    @Override
    public String getDescriptionShort() {
        return "Set the description of this test";
    }

    @Override
    public String getDescriptionLong() {
        return "Set the description of this test. Subsequent calls will override the existing description";
    }

    @Override
    public boolean isAction() {
        return false;
    }

    @Override
    public boolean handle(TSONContext tsonContext, String value) {
        tsonContext.addVariable(VariableType.PROPERTY, "desc", value);
        return true;
    }
}
