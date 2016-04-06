package SemanticEvaluation;

import LexicalAnalyzer.DFA.Position;


public class BadOpException extends Exception {

    public BadOpException(Position pos, String type1, String type2, String op) {
        super("Bad Operation at: " + pos + ".  Trying to " + op + " between " + type1 + " and " + type2);
    }
}
