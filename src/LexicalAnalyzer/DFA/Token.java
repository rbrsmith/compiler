package LexicalAnalyzer.DFA;

/**
 * Enum of all possible delimited tokens in the lexicon
 */
public enum Token {
    ID, CCB, OCB, AND, OR, SPACE, TAB,
    INTEGER, FLOAT, EQUALS, NOT_EQUALS, LESS_THAN, GREATER_THAN,
    LESS_THAN_EQUALS, GREATER_THAN_EQUALS,ADDITION,
    SEMICOLON,COMMA,MULTIPLICATION, SUBTRACTION, DIVISION,
    ASSIGNMENT, ORB, CRB, OSB, CSB, LINE_FEED, CARRIAGE_RETURN,
    OPEN_COMMENT, CLOSE_COMMENT, INLINE_COMMENT, DOT, RESERVED, EOF;
}
