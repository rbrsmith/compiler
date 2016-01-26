package LexicalAnalyzer.DFA;

public class POS {

    private String token;
    private String type;

    public POS(String token, String type) {
        this.token = token;
        this.type = type;
    }

    public String getToken() {
        return this.token;
    }

    public String getType() {
        return this.type;
    }

    public String toString() {
        return "Token: " + this.getType() + ", " + this.getToken();
    }


}
