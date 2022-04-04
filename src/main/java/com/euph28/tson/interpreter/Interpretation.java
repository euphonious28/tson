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

import java.util.*;
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

    /* ----- CONSTRUCTOR ------------------------------ */

    /**
     * Interpret and store the interpreted content from a TSON content
     *
     * @param content TSON content found within a file
     */
    public Interpretation(List<Keyword> keywordList, String content) {
        statementList = parse(keywordList, content);
    }

    /* ----- METHODS: PARSER ------------------------------ */

    /**
     * Converts a TSON content into a {@link List} of {@link Statement}
     *
     * @param keywordList List of {@link Keyword} to look for when parsing
     * @param content     TSON content to be parsed
     * @return List of {@link Statement} parsed from the {@code content}
     */
    List<Statement> parse(List<Keyword> keywordList, String content) {

        StatementListener listener = new StatementListener(keywordList);
        parse(keywordList, listener, content);

        // Get statement list from walker
        return listener.getStatementList();
    }

    /**
     * Parse TSON content and handle via provided listener
     *
     * @param keywordList List of {@link Keyword} to look for when parsing
     * @param listener    Listener that will handle the results of the parser
     * @param content     TSON content to be parsed
     */
    void parse(List<Keyword> keywordList, ParseTreeListener listener, String content) {
        // ANTLR4 Parser & Lexer
        TsonLexer lexer = new TsonLexer(
                CharStreams.fromString(content),
                new HashSet<>(keywordList
                        .stream()
                        .map(Keyword::getCode)
                        .collect(Collectors.toList())
                )
        );
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        TsonParser parser = new TsonParser(tokenStream);
        parser.addErrorListener(new ErrorListener());

        // Generate tree and wa;l
        ParseTree tree = parser.file();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(listener, tree);
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

    /* ----- METHODS ------------------------------ */

    /**
     * Returns if the interpretation is valid based on the content of {@link #statementList}
     *
     * @return Returns true if there are {@link Statement} interpreted
     */
    public boolean isValid() {
        return !statementList.isEmpty();
    }
}
