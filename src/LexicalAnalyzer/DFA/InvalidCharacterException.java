package LexicalAnalyzer.DFA;


public class InvalidCharacterException extends Exception {
    public InvalidCharacterException(Position pos, String token) {
        super("Invalid Character in token " + token + ". " + pos);
    }

}
