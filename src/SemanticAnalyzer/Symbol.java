package SemanticAnalyzer;

import LexicalAnalyzer.DFA.Position;

public class Symbol {

    private Declaration decl;
    private SymbolTable table;


    public Symbol(Declaration decl, SymbolTable parent, Position pos) throws AlreadyDeclaredException {
       if(parent.alreadyExists(decl)) throw new AlreadyDeclaredException(pos, decl.getName());
        if(!(decl instanceof VariableDecl)) this.table = new SymbolTable(parent, decl.getName());
        this.decl = decl;
    }

    public Declaration getDecl() {
        return decl;
    }

    public SymbolTable getSubTable(){
        return table;
    }

    public String toString() {
        return decl.toString();
    }

}
