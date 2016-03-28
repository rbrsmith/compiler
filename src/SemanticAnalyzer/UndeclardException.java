package SemanticAnalyzer;

import LexicalAnalyzer.DFA.Position;

/**
 * Thrown when we are assigning a variable that has not been declared
 */
public class UndeclardException extends Exception {

    public UndeclardException(Position pos, VariableAssig va) {
        super("Semantic Error at: " + pos + ".  Undeclared element in " + va);

    }

    public UndeclardException(Position pos, Declaration v) {
        super("Semantic Error at: " + pos + ". Undeclared class for " + v.getName());
    }

}
