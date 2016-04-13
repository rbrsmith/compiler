package SemanticEvaluation;

import CodeGeneration.CompilerException;
import LexicalAnalyzer.DFA.Position;
import SemanticAnalyzer.VariableDecl;

/**
 * Thrown when a variable is used that is not initialized
 */
public class UninitializedException extends CompilerException {

    public UninitializedException(Position pos, VariableDecl vd) {
        super("Uninitialized variable at: " + pos + ".  " + vd.getName() + " is being referenced without initialization.");
    }
}
