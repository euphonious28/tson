parser grammar TsonParser;

options { tokenVocab=TsonLexer; }

 // Main entry
file                : (entry)+ EOF;
entry               : (statement | comment) (SPACE)* ;

// Comments
comment             : COMMENT_SINGLE | COMMENT_MULTI ;

// Statement
statement           : keyword SPACE+ (properties SPACE+)? value ;
properties          : PROPERTIES_OPEN ((propertiesMap (SPACE propertiesMap)*) | propertiesValue) PROPERTIES_CLOSE ;
propertiesMap       : propertiesKey EQUAL propertiesValue ;
propertiesKey       : (WORD | STRING)+ ;
propertiesValue     : (WORD | STRING)+ ;
keyword             : KEYWORD ;
value               : (WORD | SPACE | STRING | comment)+? ;