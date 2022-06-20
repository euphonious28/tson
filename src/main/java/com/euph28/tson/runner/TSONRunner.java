package com.euph28.tson.runner;

import com.euph28.tson.assertionengine.TSONAssertionEngine;
import com.euph28.tson.context.TSONContext;
import com.euph28.tson.context.VariableType;
import com.euph28.tson.core.keyword.KeywordType;
import com.euph28.tson.core.provider.ContentProvider;
import com.euph28.tson.filereader.FileReader;
import com.euph28.tson.interpreter.Interpretation;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
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
        loadProperties(properties);
    }

    /**
     * Create a runner for running TSON
     *
     * @param workspace Workspace folder that should be used for resolving paths
     */
    public TSONRunner(File workspace) {
        this(workspace, new Properties());
    }

    /* ----- UTILITY ------------------------------ */

    /**
     * Load properties from files into {@link #tsonContext}. Properties are loaded with the following order, with the later properties
     * overriding the earlier: <br/>
     * 1. Global properties, located at {@code <tsonFolder>/global.properties}<br/>
     * 2. Local properties, located at {@code <tsonFolder>/local.properties}<br/>
     * 3. Workspace properties, located at {@code <workspaceFolder>/local.properties}<br/>
     * 4. Custom properties, provided in argument
     *
     * @param customProperties Custom properties to be loaded. This is loaded last and overrides loaded properties
     */
    void loadProperties(Properties customProperties) {
        Properties properties = new Properties();

        // 1. Global properties
        try {
            File globalPropertiesFile = new File(TSONRunner.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                    .getParentFile()
                    .toPath()
                    .resolve("global.properties")
                    .toFile();
            if (globalPropertiesFile.exists() && globalPropertiesFile.isFile()) {
                properties.load(new FileInputStream(globalPropertiesFile));
                logger.trace(String.format("Loaded global properties file (%s). Total of %d properties to be added", globalPropertiesFile, properties.stringPropertyNames().size()));
            } else {
                logger.trace("Global properties file not found: " + globalPropertiesFile);
            }
        } catch (URISyntaxException | IOException e) {
            logger.error("Failed to load global properties", e);
        }

        // 2. Local properties
        try {
            File localPropertiesFile = new File(TSONRunner.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                    .getParentFile()
                    .toPath()
                    .resolve("local.properties")
                    .toFile();
            if (localPropertiesFile.exists() && localPropertiesFile.isFile()) {
                properties.load(new FileInputStream(localPropertiesFile));
                logger.trace(String.format("Loaded local properties file (%s). Total of %d properties to be added", localPropertiesFile, properties.stringPropertyNames().size()));
            } else {
                logger.trace("Local properties file not found: " + localPropertiesFile);
            }
        } catch (URISyntaxException | IOException e) {
            logger.error("Failed to load local properties", e);
        }

        // 3. Workspace properties
        try {
            File workspacePropertiesFile = workspace.toPath().resolve("local.properties").toFile();
            if (workspacePropertiesFile.exists() && workspacePropertiesFile.isFile()) {
                properties.load(new FileInputStream(workspacePropertiesFile));
                logger.trace(String.format("Loaded workspace properties file (%s). Total of %d properties to be added", workspacePropertiesFile, properties.stringPropertyNames().size()));
            } else {
                logger.trace("Workspace properties file not found: " + workspacePropertiesFile);
            }
        } catch (IOException e) {
            logger.error("Failed to load workspace properties", e);
        }

        // 4. Custom properties
        properties.putAll(customProperties);

        // Load properties
        for (String key : properties.stringPropertyNames()) {
            tsonContext.addVariable(VariableType.PROPERTY, key, properties.getProperty(key));
        }
        logger.debug(String.format("Loaded %d properties to TSONContext", properties.stringPropertyNames().size()));
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
        Interpretation interpretation = tsonInterpreter.interpret(filename);
        if (interpretation == null || interpretation.hasError()) {
            logger.error("Failed to read TSON file: " + filename);
            return tsonReporter;
        }

        // Run statements
        while (!interpretation.isEof()) {
            // Get statement
            Statement statement = interpretation.getNext();

            // Validity check of next statement
            if (statement == null || statement.getKeyword() == null) {
                logger.error("Next available statement is invalid. Skipping statement execution");
                continue;
            }

            // Log execution
            logger.info(String.format("Executing Statement: [%s] %s", statement.getKeyword().getCode(), statement.getValue()));

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

            // Handle statement, wrap in try-catch to handle any unexpected errors when handling statement
            try {
                statement.getKeyword().handle(
                        tsonContext,
                        subReporter,
                        statement
                );
            } catch (Exception e) {
                // Log error
                logger.error(String.format(
                        "Error encountered when handling statement: [%s] %s",
                        statement.getKeyword().getCode(),
                        statement.getValue()
                ), e);
                // Set report to error
                subReporter.getReport().setReportType(ReportType.ERROR);
                subReporter.getReport().setReportDetail("Error encountered when handling statement. Check logs for details");
            }
        }

        // Output result
        return tsonReporter;
    }

    /* ----- GETTERS ------------------------------ */
    public TSONInterpreter getTsonInterpreter() {
        return tsonInterpreter;
    }
}