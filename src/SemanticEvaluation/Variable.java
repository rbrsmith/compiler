package SemanticEvaluation;

/**
 * Interface for Right Hand Side variables
 */
public interface Variable {

    String getName();
    int getSize();
    Variable getAttribute();

}
