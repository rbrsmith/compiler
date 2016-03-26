package SemanticAnalyzer;

import LexicalAnalyzer.DFA.Position;

public class UndeclardException extends Exception {

    public UndeclardException(Position pos, VariableAssig va) {
        super("Semantic Error at: " + pos + ".  Undeclared element in " + va);

    }

    public UndeclardException(Position pos, VariableDecl v) {
        super("Semantic Error at: " + pos + ". Undeclared class for " + v.getName());
    }
}
