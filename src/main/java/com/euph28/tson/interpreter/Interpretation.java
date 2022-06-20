package com.euph28.tson.interpreter;

import com.euph28.tson.antlr.TsonLexer;
import com.euph28.tson.antlr.TsonParser;
import com.euph28.tson.core.keyword.Keyword;
import com.euph28.tson.core.keyword.KeywordType;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Interprets and stores the interpreted content of a TSON file
 */
public class Interpretation {
    /* ----- VARIABLES ------------------------------ */
    Logger logger = LoggerFactory.getLogger(Statement.class);

    /**
     * List of interpreted {@link Statement}
     */
    List<Statement> statementList;

    /**
     * Iteration index of the {@link #statementList}. The index points to the next {@link Statement} that should be read.
     * eg: Starts at index=0 as the next item is item0. Retrieving item3 would make index=4 as next item is item4
     */
    int iteratorNextIndex = 0;

    /**
     * Parser error listener/tracker
     */
    ErrorListener errorListener = new ErrorListener();

    /**
     * Tree result of interpretation
     */
    ParseTree interpretedTree;

    /* ----- CONSTRUCTOR ------------------------------ */

    /**
     * Interpret and store the interpreted content from a TSON content
     *
     * @param content TSON content found within a file
     */
    public Interpretation(List<Keyword> keywordList, String content) {
        // Parse
        parse(keywordList, content);

        // Generate basic Statement list
        StatementListener statementListener = new StatementListener(keywordList);
        walkListener(statementListener);
        statementList = statementListener.getStatementList();
    }

    /* ----- METHODS: PARSER ------------------------------ */

    /**
     * Parse TSON content
     *
     * @param keywordList List of {@link Keyword} to look for when parsing
     * @param content     TSON content to be parsed
     */
    void parse(List<Keyword> keywordList, String content) {
        // ANTLR4 Lexer, input the keywordList for dynamic keyword parsing
        TsonLexer lexer = new TsonLexer(
                CharStreams.fromString(content),
                new HashSet<>(keywordList
                        .stream()
                        .map(Keyword::getCode)
                        .collect(Collectors.toList())
                )
        );

        // ANTLR4 Parser, using the lexer results
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        TsonParser parser = new TsonParser(tokenStream);
        parser.addErrorListener(errorListener);

        // Generate tree and store tree for walking (using .file() as that is the root entry)
        this.interpretedTree = parser.file();
    }

    /**
     * Walk listener through the parse tree result from {@link #parse(List, String)}
     *
     * @param listener Listener to be used for the walk
     */
    public void walkListener(ParseTreeListener listener) {
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(listener, interpretedTree);
    }

    /**
     * Check if there has been an error found when parsing the content
     *
     * @return Returns {@code true} if an error was found parsing, returns {@code false} if otherwise
     */
    public boolean hasError() {
        return interpretedTree == null
                || interpretedTree.getText().isEmpty()
                || errorListener.isHasError();
    }

    /* ----- METHODS: ITERATOR ------------------------------ */

    /**
     * Retrieve the next {@link Statement} in the iterator. Does not move the iterator forward
     *
     * @return Returns the next {@link Statement}. Returns {@code null} if it has reached the end (see {@link #isEof()}
     */
    public Statement peek() {
        return !isEof()
                ? statementList.get(iteratorNextIndex)
                : null;
    }

    /**
     * Retrieve the next {@link Statement} in the iterator that has a matching type. Does not move the iterator forward
     *
     * @param targetTypes Valid types to filter for
     * @return Returns the next {@link Statement} that has a type matching with {@code targetTypes}
     * Returns {@code null} if it has reached the end without finding a suitable match
     */
    public Statement peekType(List<KeywordType> targetTypes) {
        return !isEof() ?
                statementList.subList(iteratorNextIndex, statementList.size())
                        .stream()
                        .filter(statement -> targetTypes.contains(statement.getKeyword().getKeywordType()))
                        .findFirst()
                        .orElse(null)
                : null;
    }

    /**
     * Retrieve the next {@link Statement} in the iterator and moves the iterator forward
     *
     * @return Returns the next {@link Statement}. Returns {@code null} if it has reached the end (see {@link #isEof()}
     */
    public Statement getNext() {
        return !isEof()
                ? statementList.get(iteratorNextIndex++)
                : null;
    }

    /**
     * Checks if the iterator has reached the end of the {@link #statementList}
     *
     * @return Returns true if the iterator has iterated through all items
     */
    public boolean isEof() {
        return iteratorNextIndex >= statementList.size();
    }

    /**
     * Reset the iterator to the start
     */
    public void resetIterator() {
        iteratorNextIndex = 0;
    }
}
