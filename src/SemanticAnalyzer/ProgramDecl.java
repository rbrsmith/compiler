package SemanticAnalyzer;

public class ProgramDecl implements Declaration {

    private String name;

    public ProgramDecl(Node program) {
        name = (String) program.getLeafValue().getX();

    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }


}
