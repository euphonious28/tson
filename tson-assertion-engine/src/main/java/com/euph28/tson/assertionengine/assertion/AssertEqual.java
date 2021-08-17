package com.euph28.tson.assertionengine.assertion;

import com.euph28.tson.assertionengine.TSONAssertionEngine;
import com.euph28.tson.interpreter.data.RequestData;
import com.euph28.tson.interpreter.data.ResponseData;

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
        return false;
    }
}
