package com.euph28.tson.assertionengine.keyword.assertion;

import com.euph28.tson.assertionengine.TSONAssertionEngine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestAssertEqual {

    TSONAssertionEngine assertionEngine = new TSONAssertionEngine();

    @Test
    public void testCheckBasicAssertion() {
        // Variables
        AssertEqual assertion = new AssertEqual(assertionEngine);

        // Input data
        String[] expressionValues = {"testPath", "testExpected"};

        // Assertion
        Assertions.assertTrue(assertion.checkAssertion(expressionValues, "testExpected", "testPath"));
        Assertions.assertFalse(assertion.checkAssertion(expressionValues, "testActual", "testPath"));
    }
}
