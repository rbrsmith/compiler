package SemanticEvaluation;

import LexicalAnalyzer.DFA.Position;

public class InvalidTypesException extends Exception {

    public InvalidTypesException(Position pos, String type1, String type2, String var) {
        super("Types mismatch at: " + pos + ".  Types inconsistent between " + type1 + " and " + type2 + " for variable " + var);
    }
}
