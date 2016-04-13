package SemanticEvaluation;

import CodeGeneration.CompilerException;
import LexicalAnalyzer.DFA.Position;

/**
 * Thrown when a function parameters do not match
 * Ex:  int a(int a) vs a(3.0) or int a() vs a(3)
 */
public class InvalidFunctionParamsException extends CompilerException {

    public InvalidFunctionParamsException(Position pos) {
        super("Invalid function parameter match at: " + pos);

    }
}
