package LexicalAnalyzer.DFA;

public class POS {

    private String token;
    private Token type;

    public POS(String token, Token type) {
        this.token = token;
        this.type = type;
    }

    public String getToken() {
        return this.token;
    }

    public Token getType() {
        return this.type;
    }

    public String toString() {
        return "Token: " + this.getType() + ", " + this.getToken();
    }


}
