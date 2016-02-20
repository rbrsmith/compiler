package LexicalAnalyzer.DFA;

/**
 * Part of Speech class
 * Call wraps a series of characters as a token or reserved word
 */
public class POS {

    private String token;
    private Token type;
    private Reserved word;

    /**
     *
     * @param token String representing the delimited token
     * @param type Token enum that the string represents
     */
    public POS(String token, Token type) {
        this.token = token;
        this.type = type;
        word = null;
    }

    /**
     *
     * @return String of delimited token
     */
    public String getToken() {
        return this.token;
    }

    /**
     *
     * @return Token of type
     */
    public Token getType() {
        return this.type;
    }

    public String toString() {
        return "Token: " + this.getType() + ", " +
                (this.word != null ? word : this.getToken());
    }

    /**
     *
     * @param word Update the Token Type to be one of a reserved word
     */
    public void setWord(Reserved word) {
        if(type == Token.ID || type == Token.RESERVED) {
            this.word = word;
            type = Token.RESERVED;
        }
    }

    public Reserved getWord() {
        return this.word;
    }


}
