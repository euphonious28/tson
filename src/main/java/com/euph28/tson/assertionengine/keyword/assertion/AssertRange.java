package com.euph28.tson.assertionengine.keyword.assertion;

import com.euph28.tson.assertionengine.TSONAssertionEngine;
import com.euph28.tson.assertionengine.keyword.utility.AssertionUtilities;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class AssertRange extends PathValueAssertion {

    /* ----- CONSTRUCTOR ------------------------------ */
    public AssertRange(TSONAssertionEngine tsonAssertionEngine) {
        super(tsonAssertionEngine);
    }

    /* ----- OVERRIDE: PathValueAssertion ------------------------------ */
    @Override
    protected String getStepDescription(String[] expressionValues) throws ArrayIndexOutOfBoundsException {
        return String.format("Assert that value at \"%s\" fits within range \"%s\"", expressionValues[0], expressionValues[1]);
    }

    @Override
    protected String getResultDescription(ResultMessageType resultMessageType, String[] expressionValues, String actualValue, String path) throws ArrayIndexOutOfBoundsException {
        switch (resultMessageType) {
            case RESULT_DEFAULT_PASS:
                return String.format("Actual value \"%s\" at path \"%s\" (based on \"%s\") is equal to expected range", actualValue, path, getPathFromExpression(expressionValues));
            case RESULT_DEFAULT_FAIL:
                return String.format("Actual value \"%s\" at path \"%s\" (based on \"%s\") is not equal to expected range \"%s\"", actualValue, path, getPathFromExpression(expressionValues), expressionValues[1]);
            case RESULT_COUNT_PASS:
                return String.format("Count of value \"%s\" at path \"%s\" is equal to expected", expressionValues[1], getPathFromExpression(expressionValues));
            case RESULT_COUNT_FAIL:
                return String.format("Count of value \"%s\" at path \"%s\" is %s and is not equal to expected range \"%s\"", expressionValues[1], getPathFromExpression(expressionValues), actualValue, expressionValues[2]);
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
        double value;

        // Convert actual value from String to double
        try {
            value = Double.parseDouble(actualValue);
        } catch (NumberFormatException e) {
            LoggerFactory.getLogger(this.getClass()).error("Failed to convert the following value to double: " + actualValue, e);
            return false;
        }

        return AssertionUtilities.checkValueRange(expressionValues[1], value);
    }

    /* ----- OVERRIDE: AssertionBase ------------------------------ */
    @Override
    public String getCode() {
        return "RANGE";
    }

    @Override
    public String getLspDescriptionShort() {
        return "Assert that value is within provided range";
    }

    @Override
    public String getLspDescriptionLong() {
        return "Asserts that value located at a JSON path is within the provided range.\n"
                + "Usage\t: <jsonPath>=<expectedRange>\n"
                + "Example\t: body.item.0.subValue=1-5\n"
                + "Note\t: jsonPath supports wildcard (*) for arrays. expectedValue accepts values in the form of a range";
    }
}
