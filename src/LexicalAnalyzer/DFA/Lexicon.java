package LexicalAnalyzer.DFA;


public enum Lexicon {
    LETTER("[a-zA-Z]", "l"), NZERO("[1-9]", "nz"), ZERO("[0]", "z"), UNDERSCORE("_", "u"), OCB("{", "ocb"),
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

