package com.euph28.tson.core.keyword;

/**
 * Type of {@link Keyword}
 */
public enum KeywordType {

    /**
     * No impact type where the execution of the {@link Keyword} has no impact on the AUT (Application-under-test)
     * nor the assertion process
     */
    NO_IMPACT,

    /**
     * An action done towards the AUT (Application-under-test) that should impact the behaviour of the AUT
     */
    ACTION,

    /**
     * Utility keyword that assist an {@link #ACTION}. Utility keywords have no/minimal impact on the
     * AUT (Application-under-test) and does not generate assertion results
     */
    UTILITY,

    /**
     * Assertion done based on the output of an {@link #ACTION}
     */
    ASSERTION,

    /**
     * General purpose type. Usage of this type should be kept at minimum as it is not handled
     */
    OTHER
}
