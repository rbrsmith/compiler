package SemanticEvaluation;

import CodeGeneration.CompilerException;
import LexicalAnalyzer.DFA.Position;

/**
 * Thrown when we are trying to add to incompatible types
 * Ex: 3.0 + 2
 */
public class BadOpException extends CompilerException {

    public BadOpException(Position pos, String type1, String type2, String op) {
        super("Bad Operation at: " + pos + ".  Trying to " + op + " between " + type1 + " and " + type2);
    }
}
