package SemanticAnalyzer;


import CodeGeneration.CodeGenerator;

/**
 * Wrapper for a class declaration
 */
public class ClassDecl implements Declaration {

    // Name of class
    private String name;

    public ClassDecl(Node id) {
        name = (String) id.getLeafValue().getY();
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return "CLASS\t\tName: "+name;
    }



}
