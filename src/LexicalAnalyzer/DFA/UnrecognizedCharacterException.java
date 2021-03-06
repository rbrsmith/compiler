package LexicalAnalyzer.DFA;

import CodeGeneration.CompilerException;

/**
 *  Throw when an unknown character is encountered
 */
public class UnrecognizedCharacterException extends CompilerException {
    public UnrecognizedCharacterException(Position pos, String token, String str) {
        super("Unrecognized Character in token " + token.replace("\n", "\\n").replace("\r","\\r")  +
                " at character " + str.replace("\n", "\\n").replace("\r","\\r") + " " + pos);
    }

}
