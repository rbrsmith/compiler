package LexicalAnalyzer.DFA;


public class InvalidCharacterException extends Exception {
    public InvalidCharacterException(Position pos) {
        super("Invalid Character. " + pos);
    }

}
