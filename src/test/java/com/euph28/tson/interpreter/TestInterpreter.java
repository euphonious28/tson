package com.euph28.tson.interpreter;

import com.euph28.tson.TestUtility;
import com.euph28.tson.runner.TSONRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

public class TestInterpreter {

    TSONRunner tsonRunner = new TSONRunner(Paths.get("").toAbsolutePath().toFile());

    @Test
    public void testSimple() {
        Interpretation interpretation = new Interpretation(
                tsonRunner.getTsonInterpreter().getKeywords(),
                TestUtility.getTsonFile("simple01.tson", "")
        );

        Assertions.assertEquals(7, interpretation.statementList.size());
    }
}
