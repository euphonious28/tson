package com.euph28.tson.context.keyword;

import com.euph28.tson.context.TSONContext;
import com.euph28.tson.context.VariableType;
import com.euph28.tson.core.keyword.Keyword;
import com.euph28.tson.core.keyword.KeywordType;
import com.euph28.tson.reporter.report.Report;
import com.euph28.tson.reporter.report.ReportType;
import com.euph28.tson.reporter.TSONReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility keyword: Response Variable
 * <p>
 * Add a variable from the JSON response body
 */
public class ResponseVariable extends Keyword {

    /* ----- VARIABLES ------------------------------ */
    Logger logger = LoggerFactory.getLogger(ResponseVariable.class);

    /* ----- OVERRIDE: KEYWORD ------------------------------ */
    @Override
    public String getCode() {
        return "RESPONSE_VARIABLE";
    }

    @Override
    public String getDescriptionShort() {
        return "Create variable from JSON response body";
    }

    @Override
    public String getDescriptionLong() {
        return "Create a variable from the JSON response body based on JSON path.\n"
                + "Usage\t: <variableName>=<jsonPath>\n"
                + "Example\t: textValue=body.item.0.value"
                + "Note\t: Multiple entries can be created by separating them with space";
    }

    @Override
    public KeywordType getKeywordType() {
        return KeywordType.UTILITY;
    }

    @Override
    public boolean handle(TSONContext tsonContext, TSONReporter tsonReporter, String value) {
        // Update report
        Report report = tsonReporter.getReport();
        report.setReportType(ReportType.INFO);
        report.setReportFallbackTitle("Create variable from response");

        // Split into entries
        String[] values = split(tsonContext.resolveContent(value), ' ', true);

        /* ===== HANDLING OF INDIVIDUAL ENTRY ===== */
        for (String entry : values) {
            // Split into before and after operand
            String[] splitValues = split(entry, '=', true);
            // Error check: entry is invalid (not enough/too many parts)
            if (splitValues.length != 2) {
                logger.error("Invalid number of parameters (should be 2) for response variable: " + value);
                continue;
            }

            // Retrieve JSON path
            String pathValue = tsonContext.getContent("json." + splitValues[1]);

            // Error check: invalid value
            if (pathValue.isEmpty()) {
                logger.error("No JSON value found for response variable: " + value);
                continue;
            }

            // Store value
            tsonContext.addVariable(VariableType.VARIABLE, splitValues[0], pathValue);
            logger.trace(String.format("Stored response variable with key \"%s\" and value \"%s\"", values[0], pathValue));
            tsonReporter.createSubReport(new Report(
                            ReportType.INFO,
                            entry,
                            String.format(
                                    "Store variable \"%s\" with response value at path \"%s\". Value at path is: %s",
                                    splitValues[0],
                                    splitValues[1],
                                    pathValue
                            ),
                            String.format("Create variable \"%s\" with response value from path \"%s\"", splitValues[0], splitValues[1]),
                            tsonReporter.getReport().getSource()
                    )
            );
        }
        return true;
    }
}
