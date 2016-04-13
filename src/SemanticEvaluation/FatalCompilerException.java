package SemanticEvaluation;

/**
 * A General exception which should hopefully never be seen
 */
public class FatalCompilerException extends Exception {
    public FatalCompilerException(String s) {
        super("Major Issue: " + s);
    }

}
