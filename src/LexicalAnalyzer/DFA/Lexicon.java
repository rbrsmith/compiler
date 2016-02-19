package LexicalAnalyzer.DFA;

/**
 * Enum representing the possible characer values along a graph edge
 */
public enum Lexicon {
    LETTER("[a-zA-Z]", "l"), NZERO("[1-9]", "nz"), ZERO("[0]", "z"), UNDERSCORE("_", "u"), OCB("{", "ocb"),
    CCB("}", "ccb"), DOT(".", "dot"), AMP("&", "amp"), PIPE("|", "pip"), SPACE(" ", "sp"),TAB("\t", "tab"), EQUALS("=", "eq"), LESS_THAN("<","lt"), GREATER_THAN(">","gt"),
    SEMICOLON(";","sc"), COMMA(",", "c"),ADDITION("+", "add"),SUBTRACTION("-", "sub"),MULTIPLICATION("*","tms"),DIVISION("/", "div"),
    ORB("(", "orb"),CRB(")", "crb"),OSB("[","osb"),CSB("]","csb"), LINE_FEED("\n", "lf"), CARRIAGE_RETURN("\r", "cr"), EOF("$", "eof");
    private String value;
    private String abbr;

    /**
     *
     * @param value String of the character
     * @param abbr String shortened name used in display
     */
    Lexicon(String value, String abbr) {
        this.value = value;
        this.abbr = abbr;
    }

    /**
     *
     * @return String abbreviation of the Lexicon name
     */
    public String getAbbr() {
        return this.abbr;
    }

    /**
     *
     * @return String value of the lexicon
     */
    public String toString() {
        return this.value;
    }





}

