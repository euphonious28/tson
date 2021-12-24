package com.euph28.tson.runner;

import com.euph28.tson.assertionengine.TSONAssertionEngine;
import com.euph28.tson.context.TSONContext;
import com.euph28.tson.context.VariableType;
import com.euph28.tson.core.keyword.KeywordType;
import com.euph28.tson.core.provider.ContentProvider;
import com.euph28.tson.filereader.FileReader;
import com.euph28.tson.interpreter.Statement;
import com.euph28.tson.interpreter.TSONInterpreter;
import com.euph28.tson.reporter.TSONReporter;
import com.euph28.tson.reporter.report.Report;
import com.euph28.tson.reporter.report.ReportSource;
import com.euph28.tson.reporter.report.ReportType;
import com.euph28.tson.restclientinterface.TSONRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Properties;

/**
 * Runner that calls all the other components to execute a run
 */
public class TSONRunner {
    Logger logger = LoggerFactory.getLogger(TSONRunner.class);

    /* ----- VARIABLES ------------------------------ */
    /**
     * TSON Context, will be reused between runs
     */
    TSONContext tsonContext;

    /**
     * TSON Interpreter, will be reused between runs
     */
    TSONInterpreter tsonInterpreter;

    /**
     * Workspace folder of the test files. This should point to the root folder from where path resolutions
     * should occur
     */
    File workspace;

    /**
     * Content provider for providing content from workspace to components that require it
     */
    ContentProvider contentProvider;

    /* ----- CONSTRUCTOR ------------------------------ */

    /**
     * Create a runner for running TSON
     *
     * @param workspace  Workspace folder that should be used for resolving paths
     * @param properties Custom properties to be inserted into {@link TSONContext}
     */
    public TSONRunner(File workspace, Properties properties) {
        // Store workspace
        this.workspace = workspace;

        // Initialize shared components
        tsonContext = new TSONContext();
        contentProvider = new FileReader(workspace.getAbsolutePath());

        // Initialize interpreter & load keywords
        tsonInterpreter = new TSONInterpreter();
        tsonContext.setTsonInterpreter(tsonInterpreter);
        tsonInterpreter.addKeywordProvider(tsonContext);
        tsonInterpreter.addKeywordProvider(new TSONAssertionEngine());
        tsonInterpreter.addKeywordProvider(new TSONRestClient(tsonContext, contentProvider));
        tsonInterpreter.addContentProvider(contentProvider);

        // Load properties into context
        for(String key : properties.stringPropertyNames()) {
            tsonContext.addVariable(VariableType.PROPERTY, key, properties.getProperty(key));
        }
    }

    /**
     * Create a runner for running TSON
     *
     * @param workspace Workspace folder that should be used for resolving paths
     */
    public TSONRunner(File workspace) {
        this(workspace, new Properties());
    }

    /* ----- METHODS ------------------------------ */

    /**
     * Run a specific TSON test file
     *
     * @param filename Target TSON test to run
     */
    public TSONReporter run(String filename) {
        logger.info("Running TSON for: " + filename);

        // Variables
        TSONReporter tsonReporter = new TSONReporter();
        tsonReporter.getReport().setReportFallbackTitle(filename);
        TSONReporter lastActionReporter = tsonReporter;

        // Parse content
        if (!tsonInterpreter.interpret(filename)) {
            logger.error("Failed to read TSON file: " + filename);
            return tsonReporter;
        }

        // Run statements
        // TODO: Wrap in general try to avoid full failures
        while (!tsonInterpreter.isEof()) {
            // Get statement
            Statement statement = tsonInterpreter.getNext();

            // Select root reporter based on keyword type (for nesting items under SEND request)
            TSONReporter currentReporter;
            if (statement.getKeyword().getKeywordType() == KeywordType.ACTION | statement.getKeyword().getKeywordType() == KeywordType.NO_IMPACT) {
                currentReporter = tsonReporter;
            } else {
                currentReporter = lastActionReporter;
            }

            // Create sub-reporter for this statement
            TSONReporter subReporter = currentReporter.createSubReport(new Report(
                    ReportType.INFO,
                    statement.getProperty("title", ""),
                    "",
                    String.format("[%s] %s", statement.getKeyword().getCode(), statement.getValue()),
                    new ReportSource(statement.getKeyword(), statement.getValue())
            ));

            // Store sub-reporter if its a SEND request
            if (statement.getKeyword().getKeywordType() == KeywordType.ACTION) {
                lastActionReporter = subReporter;
            }

            // Handle statement
            statement.getKeyword().handle(
                    tsonContext,
                    subReporter,
                    statement
            );
        }

        // Output result
        return tsonReporter;
    }
}