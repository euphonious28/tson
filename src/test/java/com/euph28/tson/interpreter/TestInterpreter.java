package com.euph28.tson.interpreter;

import com.euph28.tson.TestUtility;
import com.euph28.tson.runner.TSONRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestInterpreter {

    TSONRunner tsonRunner = TestUtility.getTsonRunner();

    @Test
    public void testSimple() {
        Interpretation interpretation = new Interpretation(
                tsonRunner.getTsonInterpreter().getKeywords(),
                TestUtility.getTsonFile("simple01.tson", "")
        );

        Assertions.assertFalse(interpretation.hasError());
        Assertions.assertEquals(7, interpretation.statementList.size());
    }
}
