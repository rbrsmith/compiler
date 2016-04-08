package SemanticEvaluation;

import LexicalAnalyzer.DFA.Position;

public class InvalidFunctionParamsException extends Exception {

    public InvalidFunctionParamsException(Position pos) {
        super("Invalid function parameter match at: " + pos);

    }
}
