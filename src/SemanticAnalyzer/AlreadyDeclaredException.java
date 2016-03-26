package SemanticAnalyzer;

import LexicalAnalyzer.DFA.Position;

/**
 * Exception throw if we are trying to define an ID that has already been used
 */
public class AlreadyDeclaredException extends Exception {

    public AlreadyDeclaredException(Position pos, String ID) {
        super("Semantic Error at: " + pos + ".  The ID `" + ID + "` is already defined.");

    }
}
