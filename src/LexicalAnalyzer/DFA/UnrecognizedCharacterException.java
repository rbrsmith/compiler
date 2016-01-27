package LexicalAnalyzer.DFA;


public class UnrecognizedCharacterException extends Exception {
    public UnrecognizedCharacterException(Position pos, String token) {
        super("Unrecognized Character in token " + token + ". " + pos);
    }

}
