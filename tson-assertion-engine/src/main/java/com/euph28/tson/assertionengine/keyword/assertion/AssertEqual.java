package com.euph28.tson.assertionengine.keyword.assertion;

import com.euph28.tson.assertionengine.TSONAssertionEngine;
import com.euph28.tson.interpreter.data.RequestData;
import com.euph28.tson.interpreter.data.ResponseData;

/**
 * Assertion keyword: Equal
 * <p>
 * Compares a path to a value, reporting {@code PASS} if the actual value in the path is equal to the provided value
 */
public class AssertEqual extends AssertionBase {

    /* ----- CONSTRUCTOR ------------------------------ */
    public AssertEqual(TSONAssertionEngine tsonAssertionEngine) {
        super(tsonAssertionEngine);
    }

    /* ----- OVERRIDE: AssertionBase ------------------------------ */
    @Override
    public String getCode() {
        return "EQUAL";
    }

    @Override
    public String getDescriptionShort() {
        return "Assert that value is equal";
    }

    @Override
    public String getDescriptionLong() {
        return "Asserts that value located at a JSON path is equal to the provided value.\n"
                + "Usage\t: <jsonPath>=<expectedValue>\n"
                + "Example\t: body.item.0.subItem=text\n"
                + "Note\t: jsonPath supports wildcards (*) for arrays";
    }

    @Override
    protected boolean handleAssertion(RequestData requestData, ResponseData responseData, String value) {
        simpleAssertion(
                requestData, responseData, ' ', '=', value,
                new SimpleAssertionProvider() {
                    @Override
                    public String getPathFromExpression(String[] expressionValues) {
                        return expressionValues[0];
                    }

                    @Override
                    public boolean getAssertionResult(String[] expressionValues, String actualValue, String path) {
                        return actualValue.equals(expressionValues[1]);
                    }

                    @Override
                    public String getPassMessage(String[] expressionValues, String actualValue, String path) {
                        return String.format("Actual value \"%s\" at path \"%s\" (based on \"%s\") is equal to expected", actualValue, path, getPathFromExpression(expressionValues));
                    }

                    @Override
                    public String getFailMessage(String[] expressionValues, String actualValue, String path) {
                        return String.format("Actual value \"%s\" at path \"%s\" (based on \"%s\") is not equal to expected value \"%s\"", actualValue, path, getPathFromExpression(expressionValues), expressionValues[1]);
                    }
                }
        );

        return true;
    }
}
