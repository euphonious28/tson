lexer grammar TsonLexer;

@lexer::members {
    private java.util.Set<String> keywords = new java.util.HashSet<>();

    public TsonLexer(CharStream input, java.util.Set<String> keywords) {
        this(input);
        this.keywords = keywords;
    }

    private boolean isKeyword() {
        return keywords.stream().anyMatch(term -> ahead(term, _input));
    }

    private boolean ahead(final String word, final CharStream input) {
          for (int i = 0; i < word.length(); i++) {
              char wordChar = word.charAt(i);
              int inputChar = input.LA(i + 1);

              if (inputChar != wordChar) {
                  return false;
              }
          }

        input.seek(input.index() + word.length() - 1);

        return true;
    }
}

// Strings
STRING              : ('"' ~('"')* '"') | ('\'' ~('\'')* '\'');

// Comments
COMMENT_SINGLE      : '//' .+? NEWLINE ;
COMMENT_MULTI       : '/*' .+? '*/' ;

// Keywords
KEYWORD             : {isKeyword()}? [A-Z]+ ;

// Whitespaces
SPACE               : NEWLINE | WHITESPACE ;
NEWLINE             : ('\r'? '\n' | '\r')+ -> channel(HIDDEN) ;
WHITESPACE          : (' ' | '\t') ;

// Properties
PROPERTIES_OPEN     : '[' -> pushMode(M_PROPERTY);

// Text
TEXT                : ~('[')+? ;

// ----- Mode: Property -----
mode M_PROPERTY;

PROPERTIES_CLOSE    : ']' -> popMode ;
EQUAL               : '=' ;
PROPERTY_STRING     : (('"' ~('"')* '"') | ('\'' ~('\'')* '\'')) -> type(STRING);
PROPERTY_TEXT       : ~('[' | '=')+? -> type(TEXT);