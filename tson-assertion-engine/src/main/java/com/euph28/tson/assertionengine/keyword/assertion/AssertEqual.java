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
        return "Asserts that value located at a JSON path is equal to the provided value";
    }

    @Override
    protected boolean handleAssertion(RequestData requestData, ResponseData responseData, String value) {
        // Split into entries
        String[] values = split(value, ' ', false);

        for (String s : values) {
            // Split into before and after the =
            String path = split(s, '=', true)[0];
            String expectedValue = split(s, '=', true)[1];
            String actualValue = getValueFromJson(requestData, responseData, path)[0];

            if (actualValue.equals(expectedValue)) {
                resultPass("Value is equal to path");
            } else {
                resultFail(String.format("Expected value \"%s\" is not equal to actual value \"%s\"", expectedValue, actualValue));
            }
        }
        return true;
    }
}
