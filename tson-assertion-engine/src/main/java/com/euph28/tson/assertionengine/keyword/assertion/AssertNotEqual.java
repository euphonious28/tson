package com.euph28.tson.assertionengine.keyword.assertion;

import com.euph28.tson.assertionengine.TSONAssertionEngine;
import com.euph28.tson.interpreter.data.RequestData;
import com.euph28.tson.interpreter.data.ResponseData;

/**
 * Assertion keyword: Not Equal
 * <p>
 * Compares a path to a value, reporting {@code PASS} if the actual value in the path is not equal to the provided value
 */
public class AssertNotEqual extends AssertionBase {

    /* ----- CONSTRUCTOR ------------------------------ */
    public AssertNotEqual(TSONAssertionEngine tsonAssertionEngine) {
        super(tsonAssertionEngine);
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

    @Override
    protected boolean handleAssertion(RequestData requestData, ResponseData responseData, String value) {
        simpleAssertion(
                requestData, responseData, ' ', '=', value,
                new SimpleAssertionProvider() {
                    @Override
                    public String getPathFromExpression(String[] expressionValues) throws ArrayIndexOutOfBoundsException {
                        return expressionValues[0];
                    }

                    @Override
                    public boolean getAssertionResult(String[] expressionValues, String actualValue, String path) throws ArrayIndexOutOfBoundsException {
                        return !actualValue.equals(expressionValues[1]);
                    }

                    @Override
                    public String getPassMessage(String[] expressionValues, String actualValue, String path) throws ArrayIndexOutOfBoundsException {
                        return String.format("Actual value \"%s\" at path \"%s\" (based on \"%s\") is not equal to invalid value", actualValue, path, getPathFromExpression(expressionValues));
                    }

                    @Override
                    public String getFailMessage(String[] expressionValues, String actualValue, String path) throws ArrayIndexOutOfBoundsException {
                        return String.format("Actual value \"%s\" at path \"%s\" (based on \"%s\") is equal to invalid value \"%s\"", actualValue, path, getPathFromExpression(expressionValues), expressionValues[1]);
                    }
                }
        );
        return true;
    }
}
