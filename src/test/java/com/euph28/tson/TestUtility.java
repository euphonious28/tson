package com.euph28.tson;

import com.euph28.tson.runner.TSONRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Utility class for running tests
 */
public class TestUtility {
    /**
     * Retrieve TSON Runner for use in JUnit tests
     * @return TSON Runner
     */
    public static TSONRunner getTsonRunner() {
        return new TSONRunner(Paths.get("").toAbsolutePath().toFile());
    }

    /**
     * Retrieve TSON file content from resources
     * @param filename Name of TSON file to be retrieved
     * @param defaultContent Default content if content could not be retrieved
     * @return Returns content of the file located in resources
     */
    public static String getTsonFile(String filename, String defaultContent) {
        String content;

        try {
            content = new String(
                    Files.readAllBytes(
                            Paths.get(Objects.requireNonNull(TestUtility.class.getClassLoader().getResource(filename)).toURI())
                    )
            );
        } catch (IOException | URISyntaxException | NullPointerException e) {
            return defaultContent;
        }

        return content;
    }
}
