package LexicalAnalyzer.DFA;


import CodeGeneration.CompilerException;

/**
 * Thrown when a known character appears in an unexpected place
 */
public class InvalidCharacterException extends CompilerException {
    public InvalidCharacterException(Position pos, String token, String str) {
        super("Invalid Character in token " + token.replace("\n", "\\n").replace("\r","\\r") +
                " at character " + str.replace("\n", "\\n").replace("\r","\\r") + " " + pos);
    }

}
