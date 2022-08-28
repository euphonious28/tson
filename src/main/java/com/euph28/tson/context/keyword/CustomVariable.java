package com.euph28.tson.context.keyword;

import com.euph28.tson.context.TSONContext;
import com.euph28.tson.context.VariableType;
import com.euph28.tson.core.Utility;
import com.euph28.tson.core.keyword.Keyword;
import com.euph28.tson.core.keyword.KeywordType;
import com.euph28.tson.interpreter.Statement;
import com.euph28.tson.reporter.TSONReporter;
import com.euph28.tson.reporter.report.Report;
import com.euph28.tson.reporter.report.ReportType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomVariable extends Keyword {

    /* ----- VARIABLES ------------------------------ */
    Logger logger = LoggerFactory.getLogger(CustomVariable.class);

    /* ----- OVERRIDE: KEYWORD ------------------------------ */
    @Override
    public String getCode() {
        return "CUSTOM_VARIABLE";
    }

    @Override
    public String getLspDescriptionShort() {
        return "Create custom variable";
    }

    @Override
    public String getLspDescriptionLong() {
        return "Create custom variable\n"
                + "Usage\t: <variableName>=<value>\n"
                + "Example\t: textValue=customValue"
                + "Note\t: Multiple entries can be created by separating them with space";
    }

    @Override
    public KeywordType getKeywordType() {
        return KeywordType.UTILITY;
    }

    @Override
    public boolean handle(TSONContext tsonContext, TSONReporter tsonReporter, Statement statement) {
        // Update report
        Report report = tsonReporter.getReport();
        report.setReportType(ReportType.INFO);
        report.setReportFallbackTitle("Create custom variable");

        // Split into entries
        String[] values = Utility.split(tsonContext.resolveContent(statement.getValue()), ' ', true);

        /* ===== HANDLING OF INDIVIDUAL ENTRY ===== */
        for (String entry : values) {
            // Split into before and after operand
            String[] splitValues = Utility.split(entry, '=', true);
            // Error check: entry is invalid (not enough/too many parts)
            if (splitValues.length != 2) {
                logger.error("Invalid number of parameters (should be 2) for custom variable: " + statement.getValue());
                continue;
            }

            // Store value
            tsonContext.addVariable(VariableType.VARIABLE, splitValues[0], splitValues[1]);
            logger.trace(String.format("Stored custom variable with key \"%s\" and value \"%s\"", values[0], splitValues[1]));
            tsonReporter.createSubReport(new Report(
                            ReportType.INFO,
                            entry,
                            String.format(
                                    "Store variable \"%s\" with custom value \"%s\"",
                                    splitValues[0],
                                    splitValues[1]
                            ),
                            String.format("Create custom variable \"%s\" with value \"%s\"", splitValues[0], splitValues[1]),
                            tsonReporter.getReport().getSource()
                    )
            );
        }
        return true;
    }
}
