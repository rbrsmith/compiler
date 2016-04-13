package SemanticAnalyzer;

import CodeGeneration.CompilerException;
import LexicalAnalyzer.DFA.Position;

/**
 * Exception throw if we are trying to define an ID that has already been used
 */
public class AlreadyDeclaredException extends CompilerException {

    public AlreadyDeclaredException(Position pos, String ID) {
        super("Semantic Error at: " + pos + ".  The ID `" + ID + "` is already defined.");

    }
}
