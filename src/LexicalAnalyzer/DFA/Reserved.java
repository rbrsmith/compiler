package LexicalAnalyzer.DFA;

public enum Reserved {
    IF("if"),THEN("then"),ELSE("else"),FOR("for"),CLASS("class"),INT("int"),FLOAT("float"),GET("get"),PUT("put"),RETURN("return");

    private String word;

    private Reserved(String word) {
        this.word = word;
    }

    public String getWord() {
        return this.word;
    }


    public static Reserved get(String word) {
        for(Reserved v : values()) {
            if(v.getWord().equalsIgnoreCase(word)) {
                return v;
            }
        }
        return null;
    }



}
