package LexicalAnalyzer.DFA;

/**
 * Created by ross on 26/01/16.
 */
public enum Lexicon {
    LETTER("[a-zA-Z]", "l"), DIGIT("[0-9]", "d"), UNDERSCORE("_", "u"), OCB("{", "ocb"),
    CCB("}", "ccb"), DOT(".", "dot"), AMP("&", "amp"), PIPE("|", "pip"), SPACE(" ", "sp"),
    CR("\n", "cr"), LF("\r", "lf"), TAB("   ", "tab");
    private String value;
    private String abbr;

    private Lexicon(String value, String abbr) {
        this.value = value;
        this.abbr = abbr;
    }

    public String getAbbr() {
        return this.abbr;
    }

    public String toString() {
        return this.value;
    }





}

