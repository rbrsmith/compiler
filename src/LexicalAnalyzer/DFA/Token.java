package LexicalAnalyzer.DFA;

/**
 * Enum of all possible delimited tokens in the lexicon
 */
public enum Token {
    ID("id"), CCB("}"), OCB("{"), AND("&"), OR("||"), SPACE(" "), TAB("\t"),
    INTEGER("INTEGER"), FLOAT("FLOAT"), EQUALS("=="), NOT_EQUALS("!="), LESS_THAN("<"), GREATER_THAN(">"),
    LESS_THAN_EQUALS("<="), GREATER_THAN_EQUALS(">="),ADDITION("+"),
    SEMICOLON(";"),COMMA(","),MULTIPLICATION("*"), SUBTRACTION("-"), DIVISION("/"),
    ASSIGNMENT("="), ORB("("), CRB(")"), OSB("["), CSB("]"), LINE_FEED("\r"), CARRIAGE_RETURN("\n"),
    OPEN_COMMENT("/*"), CLOSE_COMMENT("*/"), INLINE_COMMENT("//"), DOT("."), RESERVED("RESERVED"),
    EOF("$");


    private String val;
    Token(String s) {
        val = s;
    }

    public String getValue() {
        return val;
    }
}
