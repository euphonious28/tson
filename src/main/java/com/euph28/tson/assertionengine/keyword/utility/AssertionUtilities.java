package com.euph28.tson.assertionengine.keyword.utility;

import com.euph28.tson.core.Utility;
import org.slf4j.LoggerFactory;

public class AssertionUtilities {

    /**
     * Check if the value is within the expected range.
     * The expected value is a list of comma separated ranges which accepts the following format: <br/>
     * <br/>
     * {@code value+}: Accept all values from value to double limit, inclusive <br/>
     * {@code value-}: Accept all values from value to lower double limit, inclusive <br/>
     * {@code value1-value2}: Accept all values from value1 to value2, inclusive of both <br/>
     *
     * @param expectedValueRange Expected range of values that the actual value should be within
     * @param actualValue        Actual value to be checked
     * @return Returns {@code true} if {@code actualValue} is within {@code expectedValueRange}.
     * Returns {@code false} if value is not in range or an error was encountered
     */
    public static boolean checkValueRange(String expectedValueRange, double actualValue) {
        // Split expected value into individual items
        String[] splitValues = Utility.split(expectedValueRange, ',', true);

        // 2D array: Range (min,max) array
        double[][] expectedValueAsRange = new double[splitValues.length][2];

        // Convert from String range to double range
        try {
            for (int i = 0; i < splitValues.length; i++) {
                String value = splitValues[i];
                if (value.endsWith("+")) {
                    // Case: value+
                    expectedValueAsRange[i][0] = Double.parseDouble(value.substring(0, value.length() - 1));
                    expectedValueAsRange[i][1] = Double.MAX_VALUE;
                } else if (value.endsWith("-")) {
                    // Case: value-
                    expectedValueAsRange[i][0] = Double.MIN_VALUE;
                    expectedValueAsRange[i][1] = Double.parseDouble(value.substring(0, value.length() - 1));
                } else if (value.contains("-")) {
                    // Case value1-value2
                    String[] splitRange = Utility.split(value, '-', true);
                    expectedValueAsRange[i][0] = Double.parseDouble(splitRange[0]);
                    expectedValueAsRange[i][1] = Double.parseDouble(splitRange[1]);
                } else {
                    // Case single value
                    expectedValueAsRange[i][0] = Double.parseDouble(value);
                    expectedValueAsRange[i][1] = Double.parseDouble(value);
                }
            }

            // Assert the range
            for (double[] range : expectedValueAsRange) {
                if (actualValue >= range[0] && actualValue <= range[1]) {
                    return true;
                }
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            LoggerFactory.getLogger(AssertionUtilities.class).error("Failed to check double value for range: " + expectedValueRange, e);
            return false;
        }
        return false;
    }
}
