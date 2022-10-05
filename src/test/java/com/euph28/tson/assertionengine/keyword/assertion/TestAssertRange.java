package com.euph28.tson.assertionengine.keyword.assertion;

import com.euph28.tson.assertionengine.TSONAssertionEngine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestAssertRange {
    TSONAssertionEngine assertionEngine = new TSONAssertionEngine();

    @Test
    public void testCheckBasicAssertion() {
        // Variables
        AssertRange assertion = new AssertRange(assertionEngine);

        // Input data
        String[] expressionValues = {"testPath", "29+"};

        // Assertion
        Assertions.assertFalse(assertion.checkAssertion(expressionValues, "28", "testPath"));
        Assertions.assertTrue(assertion.checkAssertion(expressionValues, "29", "testPath"));
        Assertions.assertTrue(assertion.checkAssertion(expressionValues, "30", "testPath"));
    }
}
