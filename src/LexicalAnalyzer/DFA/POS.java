package LexicalAnalyzer.DFA;

public class POS {

    private String token;
    private Token type;
    private Reserved word;

    public POS(String token, Token type) {
        this.token = token;
        this.type = type;
        word = null;
    }

    public String getToken() {
        return this.token;
    }

    public Token getType() {
        return this.type;
    }

    public String toString() {
        return "Token: " + this.getType() + ", " +
                (this.word != null ? word : this.getToken());
    }

    public void setWord(Reserved word) {
        if(type == Token.ID || type == Token.RESERVED) {
            this.word = word;
            type = Token.RESERVED;
        }
    }


}
