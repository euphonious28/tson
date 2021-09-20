package com.euph28.tson.assertionengine.result;

import com.euph28.tson.assertionengine.keyword.assertion.AssertionBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class containing an individual assertion result
 */
public class AssertionResult {

    /* ----- VARIABLE ------------------------------ */
    Logger logger = LoggerFactory.getLogger(AssertionResult.class);

    /**
     * Indicates whether this assertion pass or fail. Value of {@code true} would mean the assertion pass
     */
    boolean pass;

    /**
     * Source {@link AssertionBase} that generated this result
     */
    AssertionBase assertion;

    /**
     * User-friendly description of this result
     */
    String description;

    /* ----- CONSTRUCTOR ------------------------------ */

    /**
     * Create an individual assertion result
     *
     * @param source      Source assertion that created this result
     * @param isPass      State whether the result is pass ({@code true}) or fail ({@code false})
     * @param description User-friendly description of this result
     */
    public AssertionResult(AssertionBase source, boolean isPass, String description) {
        logger.trace(String.format(
                "Created assertion result: [%s] [%s] %s",
                isPass ? "PASS" : "FAIL",
                source.getCode(),
                description
        ));

        this.assertion = source;
        this.pass = isPass;
        this.description = description;
    }

    /* ----- GETTERS ------------------------------ */

    /**
     * Retrieve the pass state of this result
     *
     * @return Returns {@code true} if this result pass
     */
    public boolean isPass() {
        return pass;
    }

    /**
     * Retrieve the {@link AssertionBase} that generated this result
     *
     * @return Assertion that generated this result
     */
    public AssertionBase getAssertion() {
        return assertion;
    }

    /**
     * Retrieve the description of this result
     *
     * @return Description of this result
     */
    public String getDescription() {
        return description;
    }
}
