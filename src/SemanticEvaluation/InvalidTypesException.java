package SemanticEvaluation;

import CodeGeneration.CompilerException;
import LexicalAnalyzer.DFA.Position;

public class InvalidTypesException extends CompilerException {

    public InvalidTypesException(Position pos, String type1, String type2, String var) {
        super("Types mismatch at: " + pos + ".  Types inconsistent between " + type1 + " and " + type2 + " for variable " + var);
    }

    public InvalidTypesException(Position pos, String type1, String type2) {
        super("Types mismatch at: " + pos + ".  Types inconsistent between " + type1 + " and " + type2);
    }
}
