package SemanticAnalyzer;

import CodeGeneration.CompilerException;
import LexicalAnalyzer.DFA.Position;
import SemanticEvaluation.VariableReference;

/**
 * Thrown when we are assigning a variable that has not been declared
 */
public class UndeclardException extends CompilerException {

    public UndeclardException(Position pos, VariableAssig va) {
        super("Semantic Error at: " + pos + ".  Undeclared element in '" + va + "'.");
    }

    public UndeclardException(Position pos, Declaration v) {
        super("Semantic Error at: " + pos + ". Undeclared class for '" + v.getName() + "'.");
    }

    public UndeclardException(Position pos, VariableReference vr) {
        super("Semantic Error at: " + pos + ". Undeclared element in '" + vr.getName() + "'.");
    }
}
