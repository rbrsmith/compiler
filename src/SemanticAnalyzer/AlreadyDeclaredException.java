package SemanticAnalyzer;

import LexicalAnalyzer.DFA.Position;

public class AlreadyDeclaredException extends Exception {

    public AlreadyDeclaredException(Position pos, String ID) {
        super("Semantic Error at: " + pos + ".  The ID `" + ID + "` is already defined.");

    }
}
