package com.euph28.tson.assertionengine.keyword.assertion;

import com.euph28.tson.assertionengine.TSONAssertionEngine;
import com.euph28.tson.context.TSONContext;
import com.euph28.tson.interpreter.Statement;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;

public abstract class PathValueAssertion extends AssertionBase {

    /* ----- CONSTRUCTOR ------------------------------ */

    /**
     * Base of an Assertion Keyword
     *
     * @param tsonAssertionEngine TSON Assertion Engine that should handle results of this assertion
     */
    PathValueAssertion(TSONAssertionEngine tsonAssertionEngine) {
        super(tsonAssertionEngine);
    }

    /* ----- METHODS: UTILITY ------------------------------ */

    /**
     * Check if the value is within the expected range.
     * The expected value is a list of comma separated ranges which accepts the following format: <br/>
     * <br/>
     * {@code value+}: Accept all values from value to integer limit, inclusive <br/>
     * {@code value-}: Accept all values from value to lower integer limit, inclusive <br/>
     * {@code value1-value2}: Accept all values from value1 to value2, inclusive of both <br/>
     *
     * @param expectedValueRange Expected range of values that the actual value should be within
     * @param actualValue        Actual value to be checked
     * @return Returns {@code true} if {@code actualValue} is within {@code expectedValueRange}.
     * Returns {@code false} if value is not in range or an error was encountered
     */
    private boolean checkIntegerValue(String expectedValueRange, int actualValue) {
        // Split expected value into individual items
        String[] splitValues = split(expectedValueRange, ',', true);

        // 2D array: Range (min,max) array
        int[][] expectedValueAsRange = new int[splitValues.length][2];

        // TODO: Enable single value checking
        // Convert from String range to int range
        try {
            for (int i = 0; i < splitValues.length; i++) {
                String value = splitValues[i];
                if (value.endsWith("+")) {
                    // Case: value+
                    expectedValueAsRange[i][0] = Integer.parseInt(value.substring(0, value.length() - 1));
                    expectedValueAsRange[i][1] = Integer.MAX_VALUE;
                } else if (value.endsWith("-")) {
                    // Case: value-
                    expectedValueAsRange[i][0] = Integer.MIN_VALUE;
                    expectedValueAsRange[i][1] = Integer.parseInt(value.substring(0, value.length() - 1));
                } else if (value.contains("-")) {
                    // Case value1-value2
                    String[] splitRange = split(value, '-', true);
                    expectedValueAsRange[i][0] = Integer.parseInt(splitRange[0]);
                    expectedValueAsRange[i][1] = Integer.parseInt(splitRange[1]);
                } else {
                    // Case single value
                    expectedValueAsRange[i][0] = Integer.parseInt(value);
                    expectedValueAsRange[i][1] = Integer.parseInt(value);
                }
            }

            // Assert the range
            for (int[] range : expectedValueAsRange) {
                if (actualValue >= range[0] && actualValue <= range[1]) {
                    return true;
                }
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            LoggerFactory.getLogger(this.getClass()).error("Failed to check integer value for range: " + expectedValueRange, e);
            return false;
        }
        return false;
    }


    /* ----- ABSTRACT METHODS ------------------------------ */

    /**
     * Description of the assertion step that was taken. The step is independent of the result and is used for
     * manual reproduction of the step
     *
     * @param expressionValues Array of values that has been retrieved from an expression
     * @return Description of the assertion step that was taken
     */
    protected abstract String getStepDescription(String[] expressionValues) throws ArrayIndexOutOfBoundsException;

    /**
     * Retrieve the message to be used when reporting assertion result. This should explain the actual value and
     * its accuracy to the expected value
     *
     * @param resultMessageType Type of result that this message is intended for
     * @param expressionValues  Array of values that has been retrieved from an expression
     * @param actualValue       The actual value that was found at the JSON path within the {@code expressionValues}.
     *                          The JSON path is retrieved using {@link #getPathFromExpression(String[])}
     * @param path              Actual path of the {@code actualValue}. This value will be different from the JSON path
     *                          if wildcards were used
     * @return Message to be used when reporting assertion result
     * @throws ArrayIndexOutOfBoundsException Throws exception if the index of the expression chosen is invalid
     */
    protected abstract String getResultDescription(ResultMessageType resultMessageType, String[] expressionValues, String actualValue, String path)
            throws ArrayIndexOutOfBoundsException;

    /**
     * Retrieve the delimiter used to split entries from each other
     *
     * @return Delimiter to be used for splitting the entries
     */
    protected abstract char getEntryDelimiter();

    /**
     * Retrieves the delimiter used to split parts within the expression of an entry
     *
     * @return Delimiter to be used for splitting individual values within an entry
     */
    protected abstract char getExpressionDelimiter();

    /**
     * Retrieve the JSON path from an array of expression values
     *
     * @param expressionValues Array of values that has been retrieved from an expression
     * @return The JSON path from the {@code expressionValues} that should be used by the assertion
     * @throws ArrayIndexOutOfBoundsException Throws exception if the index of the expression chosen is invalid
     */
    protected abstract String getPathFromExpression(String[] expressionValues) throws ArrayIndexOutOfBoundsException;

    /**
     * Calculate the assertion of an entry
     *
     * @param expressionValues Array of values that has been retrieved from an expression
     * @param actualValue      The actual value that was found at the JSON path within the {@code expressionValues}.
     *                         The JSON path is retrieved using {@link #getPathFromExpression(String[])}
     * @param path             Actual path of the {@code actualValue}. This value will be different from the JSON path
     *                         if wildcards were used
     * @return Returns {@code true} if the assertion was successful and {@code false} if assertion failed/faced an error
     * @throws ArrayIndexOutOfBoundsException Throws exception if the index of the expression chosen is invalid
     */
    protected abstract boolean checkAssertion(String[] expressionValues, String actualValue, String path)
            throws ArrayIndexOutOfBoundsException;

    /* ----- OVERRIDE: ASSERTION BASE ------------------------------ */
    @Override
    protected boolean handleAssertion(TSONContext tsonContext, Statement statement) {
        // Split into entries
        String[] values = split(tsonContext.resolveContent(statement.getValue()), getEntryDelimiter(), false);

        /* ===== HANDLING OF INDIVIDUAL ENTRY ===== */
        for (String entry : values) {
            /* ===== EXPRESSION SPLITTING AND DATA SETUP ===== */
            // Split into before and after operands
            String[] splitValues = split(entry, getExpressionDelimiter(), true);

            // Retrieve JSON path
            String path;
            // Wrap in try-catch in case of ArrayOutOfBounds when accessing splitValues
            try {
                path = getPathFromExpression(splitValues);
            } catch (ArrayIndexOutOfBoundsException e) {
                LoggerFactory.getLogger(this.getClass()).error("Failed to retrieve path from expression array: " + Arrays.toString(splitValues), e);
                resultFail("Failed to retrieve path for expression: " + entry, getStepDescription(splitValues), entry);
                continue;
            }

            // Map of path-to-actualValue
            Map<String, String> actualValue = getValueFromJson(tsonContext, path);
            // Error handling: Checking if values failed to be resolved
            if (actualValue == null) {
                // TODO: Wrap this in another(?) try-catch
                resultFail("Failed to retrieve values for JSON path: " + path, getStepDescription(splitValues), entry);
                continue;
            }

            /* ===== VERIFY: NORMAL vs COUNT ===== */
            switch (splitValues.length) {
                case 2: // Default setup, eg: path=value. Perform assertion individually for each value
                    // Error checking: Report failure if there were no actual values (doesn't make sense for asserting nothing)
                    if (actualValue.isEmpty()) {
                        resultFail("Failed to retrieve any value for JSON path: " + path,
                                getStepDescription(splitValues),
                                entry
                        );
                    }
                    for (String key : actualValue.keySet()) {
                        // Wrap in try/catch in case expression is accessed without handling array index
                        try {
                            if (checkAssertion(splitValues, actualValue.get(key), key)) {
                                resultPass(
                                        getResultDescription(ResultMessageType.RESULT_DEFAULT_PASS, splitValues, actualValue.get(key), key),
                                        getStepDescription(splitValues),
                                        entry
                                );
                            } else {
                                resultFail(
                                        getResultDescription(ResultMessageType.RESULT_DEFAULT_FAIL, splitValues, actualValue.get(key), key),
                                        getStepDescription(splitValues),
                                        entry
                                );
                            }
                        } catch (ArrayIndexOutOfBoundsException e) {
                            LoggerFactory.getLogger(this.getClass()).error(String.format("Failed to handle assertion of expression \"%s\" for path \"%s\"", entry, key), e);
                            resultFail(String.format("Failed to assert expression \"%s\" for path \"%s\"", entry, key), "Failed to assert expression", entry);
                        }
                    }
                    break;
                case 3: // Count setup, eg: path=value=count. Count assertion results and report based on count instead
                    int count = 0;

                    // Retrieve the count of passes
                    for (String key : actualValue.keySet()) {
                        // Wrap in try/catch in case expression is accessed without handling array index
                        try {
                            if (checkAssertion(splitValues, actualValue.get(key), key)) {
                                count++;
                            }
                        } catch (ArrayIndexOutOfBoundsException e) {
                            LoggerFactory.getLogger(this.getClass()).error(String.format("Failed to handle assertion of expression \"%s\" for path \"%s\"", entry, key), e);
                            resultFail(String.format("Failed to assert expression \"%s\" for path \"%s\"", entry, key), "Failed to assert expression", entry);
                        }
                    }

                    // Assert result based on count
                    if (checkIntegerValue(splitValues[2], count)) {
                        resultPass(
                                getResultDescription(ResultMessageType.RESULT_COUNT_PASS, splitValues, String.valueOf(count), path),
                                getStepDescription(splitValues),
                                entry
                        );
                    } else {
                        resultFail(
                                getResultDescription(ResultMessageType.RESULT_COUNT_FAIL, splitValues, String.valueOf(count), path),
                                getStepDescription(splitValues),
                                entry
                        );
                    }
                    break;
                default:
                    resultFail("Failed to assert expression due to invalid format", "Failed to assert expression", entry);
            }
        }
        return true;
    }

    /* ----- ENUM: RESULT MESSAGE TYPES ------------------------------ */

    /**
     * Enum of Result Message type, used with {@link #getResultDescription(ResultMessageType, String[], String, String)}
     */
    protected enum ResultMessageType {
        RESULT_DEFAULT_PASS, RESULT_DEFAULT_FAIL,
        RESULT_COUNT_PASS, RESULT_COUNT_FAIL
    }
}