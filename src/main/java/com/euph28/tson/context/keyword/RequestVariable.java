package com.euph28.tson.context.keyword;

import com.euph28.tson.context.TSONContext;
import com.euph28.tson.context.VariableType;
import com.euph28.tson.interpreter.keyword.Keyword;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility keyword: Request Variable
 * <p>
 * Add a variable from the JSON request body
 */
public class RequestVariable extends Keyword {

    /* ----- VARIABLES ------------------------------ */
    Logger logger = LoggerFactory.getLogger(RequestVariable.class);

    /* ----- OVERRIDE: KEYWORD ------------------------------ */
    @Override
    public String getCode() {
        return "REQUEST_VARIABLE";
    }

    @Override
    public String getDescriptionShort() {
        return "Create variable from JSON request body";
    }

    @Override
    public String getDescriptionLong() {
        return "Create a variable from the JSON request body based on JSON path.\n"
                + "Usage\t: <variableName>=<jsonPath>\n"
                + "Example\t: textValue=body.item.0.value"
                + "Note\t: Multiple entries can be created by separating them with space";
    }

    @Override
    public boolean isAction() {
        return true;
    }

    @Override
    public boolean handle(TSONContext tsonContext, String value) {
        // Split into entries
        String[] values = split(tsonContext.resolveContent(value), ' ', true);

        /* ===== HANDLING OF INDIVIDUAL ENTRY ===== */
        for (String entry : values) {
            // Split into before and after operand
            String[] splitValues = split(entry, '=', true);
            // Error check: entry is invalid (not enough/too many parts)
            if (splitValues.length != 2) {
                logger.error("Invalid number of parameters (should be 2) for request variable: " + value);
                continue;
            }

            // Retrieve JSON path
            String pathValue = tsonContext.getContent("json.req." + splitValues[1]);

            // Error check: invalid value
            if (pathValue.isEmpty()) {
                logger.error("No JSON value found for request variable: " + value);
                continue;
            }

            // Store value
            tsonContext.addVariable(VariableType.VARIABLE, values[0], pathValue);
            logger.trace(String.format("Stored request variable with key \"%s\" and value \"%s\"", values[0], pathValue));
        }
        return true;
    }
}