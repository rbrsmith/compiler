package SemanticAnalyzer;

public class Symbol {

    private Declaration decl;
    private SymbolTable table;


    public Symbol(Declaration decl, SymbolTable parent) throws SemanticException {
       if(parent.alreadyExists(decl)) throw new SemanticException("Already Exists");
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
