package SemanticAnalyzer;

import LexicalAnalyzer.DFA.Token;

public class ClassDecl implements Declaration {

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
