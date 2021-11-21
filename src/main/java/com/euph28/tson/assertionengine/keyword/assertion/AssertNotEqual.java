package com.euph28.tson.assertionengine.keyword.assertion;

import com.euph28.tson.assertionengine.TSONAssertionEngine;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Assertion keyword: Not Equal
 * <p>
 * Compares a path to a value, reporting {@code PASS} if the actual value in the path is not equal to the provided value
 */
public class AssertNotEqual extends PathValueAssertion {

    /* ----- CONSTRUCTOR ------------------------------ */
    public AssertNotEqual(TSONAssertionEngine tsonAssertionEngine) {
        super(tsonAssertionEngine);
    }

    /* ----- OVERRIDE: PathValueAssertion ------------------------------ */
    @Override
    protected String getStepDescription(String[] expressionValues) throws ArrayIndexOutOfBoundsException {
        return String.format("Assert that value at \"%s\" is not equal to \"%s\"", expressionValues[0], expressionValues[1]);
    }

    @Override
    protected String getResultDescription(ResultMessageType resultMessageType, String[] expressionValues, String actualValue, String path) throws ArrayIndexOutOfBoundsException {
        switch (resultMessageType) {
            case RESULT_DEFAULT_PASS:
                return String.format("Actual value \"%s\" at path \"%s\" (based on \"%s\") is not equal to invalid value", actualValue, path, getPathFromExpression(expressionValues));
            case RESULT_DEFAULT_FAIL:
                return String.format("Actual value \"%s\" at path \"%s\" (based on \"%s\") is equal to invalid value \"%s\"", actualValue, path, getPathFromExpression(expressionValues), expressionValues[1]);
            case RESULT_COUNT_PASS:
                return String.format("Count of value not being equal \"%s\" at path \"%s\" is equal to expected", actualValue, getPathFromExpression(expressionValues));
            case RESULT_COUNT_FAIL:
                return String.format("Count of value not being equal \"%s\" at path \"%s\" is %s and is not equal to expected range \"%s\"", actualValue, getPathFromExpression(expressionValues), actualValue, expressionValues[2]);
            default:
                LoggerFactory.getLogger(this.getClass()).error("Unknown result message requested for: " + Arrays.toString(expressionValues));
                return "Unknown result message retrieved";
        }
    }

    @Override
    protected char getEntryDelimiter() {
        return ' ';
    }

    @Override
    protected char getExpressionDelimiter() {
        return '=';
    }

    @Override
    protected String getPathFromExpression(String[] expressionValues) throws ArrayIndexOutOfBoundsException {
        return expressionValues[0];
    }

    @Override
    protected boolean checkAssertion(String[] expressionValues, String actualValue, String path) throws ArrayIndexOutOfBoundsException {
        return !expressionValues[1].equals("*") && !expressionValues[1].equals(actualValue);
    }

    /* ----- OVERRIDE: AssertionBase ------------------------------ */
    @Override
    public String getCode() {
        return "NOT_EQUAL";
    }

    @Override
    public String getDescriptionShort() {
        return "Assert that value is not equal";
    }

    @Override
    public String getDescriptionLong() {
        return "Asserts that value located at a JSON path is not equal to the provided value.\n"
                + "Usage\t: <jsonPath>=<invalidValue>\n"
                + "Example\t: body.item.0.subItem=text\n"
                + "Note\t: jsonPath supports wildcards (*) for arrays";
    }
}
