package LexicalAnalyzer.DFA;

/**
 *  Enum representing al reserved words in the lexicon
 */
public enum Reserved {
    IF("if"),THEN("then"),ELSE("else"),FOR("for"),CLASS("class"),
    INT("int"),FLOAT("float"),GET("get"),PUT("put"),RETURN("return"),
    PROGRAM("program");

    private String word;

    /**
     *
     * @param word
     */
    Reserved(String word) {
        this.word = word;
    }

    /**
     *
     * @return String of the enums value
     */
    public String getWord() {
        return this.word;
    }


    /**
     *
     * @param word String of enum to find
     * @return Reserved enum matching the word
     */
    public static Reserved get(String word) {
        for(Reserved v : values()) {
            if(v.getWord().equalsIgnoreCase(word)) {
                return v;
            }
        }
        return null;
    }



}
