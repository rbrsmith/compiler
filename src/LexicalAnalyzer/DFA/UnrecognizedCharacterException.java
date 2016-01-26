package LexicalAnalyzer.DFA;


public class UnrecognizedCharacterException extends Exception {
    public UnrecognizedCharacterException(Position pos) {
        super("Unrecognized Character. " + pos);
    }

}
