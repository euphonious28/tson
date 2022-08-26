package com.euph28.tson.interpreter;

import com.euph28.tson.antlr.TsonParser;
import com.euph28.tson.antlr.TsonParserBaseListener;
import com.euph28.tson.core.keyword.Keyword;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Listener for processing statements from ANTLR4
 */
public class StatementListener extends TsonParserBaseListener {

    /* ----- VARIABLES ------------------------------ */
    /**
     * List of {@link Statement} that are found
     */
    List<Statement> statementList = new ArrayList<>();

    /**
     * List of supported {@link Keyword}
     */
    List<Keyword> keywordList;

    /**
     * The keyword of the currently parsed Statement
     */
    Keyword currentKeyword;

    /**
     * The properties of the currently parsed Statement
     */
    Map<String, String> currentProperties;

    /**
     * The key of the current property that will be added to {@link #currentProperties}
     */
    String currentPropertyKey;

    /**
     * The value of the currently parsed Statement
     */
    String currentValue;

    /* ----- CONSTRUCTOR ------------------------------ */
    StatementListener(List<Keyword> keywordList) {
        this.keywordList = keywordList;
    }

    /* ----- METHODS ------------------------------ */
    public List<Statement> getStatementList() {
        return statementList;
    }

    /* ----- OVERRIDE: TSON LISTENER ------------------------------ */

    @Override
    public void enterStatement(TsonParser.StatementContext ctx) {
        currentKeyword = null;
        currentProperties = new HashMap<>();
        currentValue = "";
    }

    @Override
    public void exitStatement(TsonParser.StatementContext ctx) {
        // Preprocessing: Cleanup value
        currentValue = currentValue
                .trim()                                               // Remove trailing spaces
                .replaceAll("[\\t\\n\\r]+", " ")    // Convert all newline to space
                .replaceAll("  +", " ");            // Remove repetitive spaces

        statementList.add(new Statement(
                currentKeyword,
                currentProperties,
                currentValue
        ));
    }

    @Override
    public void enterProperties(TsonParser.PropertiesContext ctx) {
        currentPropertyKey = null;
    }

    @Override
    public void enterPropertiesKey(TsonParser.PropertiesKeyContext ctx) {
        currentPropertyKey = ctx.getText();
    }

    @Override
    public void enterPropertiesValue(TsonParser.PropertiesValueContext ctx) {
        currentProperties.put(
                currentPropertyKey == null ? "title" : currentPropertyKey,
                ctx.getText()
        );
    }

    @Override
    public void enterKeyword(TsonParser.KeywordContext ctx) {
        currentKeyword = keywordList
                .stream()
                .filter(k -> k.getCode().equals(ctx.getText()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void enterValue(TsonParser.ValueContext ctx) {
        currentValue = ctx.getText();
    }
}
