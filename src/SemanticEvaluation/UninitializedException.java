package SemanticEvaluation;

import LexicalAnalyzer.DFA.Position;
import SemanticAnalyzer.VariableDecl;

public class UninitializedException extends Exception {

    public UninitializedException(Position pos, VariableDecl vd) {
        super("Uninitialized variable at: " + pos + ".  " + vd.getName() + " is being referenced without initialization.");
    }
}
