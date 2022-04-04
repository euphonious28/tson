package com.euph28.tson.interpreter;

import com.euph28.tson.antlr.TsonParser;
import com.euph28.tson.antlr.TsonParserListener;
import com.euph28.tson.core.keyword.Keyword;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;

/**
 * Listener for processing statements from ANTLR4
 */
public class StatementListener implements TsonParserListener {

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
    public void enterFile(TsonParser.FileContext ctx) {

    }

    @Override
    public void exitFile(TsonParser.FileContext ctx) {

    }

    @Override
    public void enterEntry(TsonParser.EntryContext ctx) {

    }

    @Override
    public void exitEntry(TsonParser.EntryContext ctx) {

    }

    @Override
    public void enterStatement(TsonParser.StatementContext ctx) {
        currentKeyword = null;
        currentProperties = new HashMap<>();
        currentValue = "";
    }

    @Override
    public void exitStatement(TsonParser.StatementContext ctx) {
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
    public void exitProperties(TsonParser.PropertiesContext ctx) {

    }

    @Override
    public void enterPropertiesMap(TsonParser.PropertiesMapContext ctx) {

    }

    @Override
    public void exitPropertiesMap(TsonParser.PropertiesMapContext ctx) {

    }

    @Override
    public void enterPropertiesKey(TsonParser.PropertiesKeyContext ctx) {
        currentPropertyKey = ctx.getText();
    }

    @Override
    public void exitPropertiesKey(TsonParser.PropertiesKeyContext ctx) {

    }

    @Override
    public void enterPropertiesValue(TsonParser.PropertiesValueContext ctx) {

    }

    @Override
    public void exitPropertiesValue(TsonParser.PropertiesValueContext ctx) {
        currentProperties.put(
                currentPropertyKey == null ? "title" : currentPropertyKey,
                ctx.getText()
        );
    }

    @Override
    public void enterComment(TsonParser.CommentContext ctx) {
    }

    @Override
    public void exitComment(TsonParser.CommentContext ctx) {

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
    public void exitKeyword(TsonParser.KeywordContext ctx) {

    }

    @Override
    public void enterValue(TsonParser.ValueContext ctx) {
        currentValue = ctx.getText();
    }

    @Override
    public void exitValue(TsonParser.ValueContext ctx) {

    }

    @Override
    public void visitTerminal(TerminalNode terminalNode) {

    }

    @Override
    public void visitErrorNode(ErrorNode errorNode) {

    }

    @Override
    public void enterEveryRule(ParserRuleContext parserRuleContext) {

    }

    @Override
    public void exitEveryRule(ParserRuleContext parserRuleContext) {

    }
}
