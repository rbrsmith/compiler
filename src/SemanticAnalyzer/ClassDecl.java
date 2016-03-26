package SemanticAnalyzer;

import LexicalAnalyzer.DFA.Token;

public class ClassDecl implements Declaration {

    private String name;

    public ClassDecl(Node id) throws SemanticException {
        if(!id.getLeafValue().getX().equals(Token.ID.toString())) {
            throw new SemanticException("Bad Class");
        }
        name = (String) id.getLeafValue().getY();
    }

    public String getName() {
        return name;
    }


    public String toString() {
        return "CLASS\t\tName: "+name;
    }

}
