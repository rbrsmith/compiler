package LexicalAnalyzer.DFA;

/**
 * Thrown when a known character appears in an unexpected place
 */
public class InvalidCharacterException extends Exception {
    public InvalidCharacterException(Position pos, String token, String str) {
        super("Invalid Character in token " + token.replace("\n", "\\n").replace("\r","\\r") +
                " at character " + str.replace("\n", "\\n").replace("\r","\\r") + " " + pos);
    }

}
