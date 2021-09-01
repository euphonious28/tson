package com.euph28.tson.assertionengine.keyword.assertion;

import com.euph28.tson.assertionengine.TSONAssertionEngine;
import com.euph28.tson.interpreter.data.RequestData;
import com.euph28.tson.interpreter.data.ResponseData;

import java.util.Map;

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
            // Map of path-to-actualValue
            Map<String, String> actualValue = getValueFromJson(requestData, responseData, path);

            // Error handling: Checking if values failed to be resolved
            if (actualValue == null) {
                resultFail("Failed to retrieve values for JSON path: " + path);
                continue;
            }

            // Verify for each values
            for (String key : actualValue.keySet()) {
                if (actualValue.get(key).equals(expectedValue)) {
                    resultPass(String.format("Actual value \"%s\" at path \"%s\" is equal to expected", actualValue.get(key), key));
                } else {
                    resultFail(String.format("Actual value \"%s\" at path \"%s\" is not equal to expected value \"%s\"", actualValue.get(key), key, expectedValue));
                }
            }
        }
        return true;
    }
}
