package SemanticAnalyzer;

import LexicalAnalyzer.DFA.Position;

import java.util.ArrayList;

/**
 * CLass wrapping a declaration
 * Represents an entry in the symbol table
 */
public class Symbol {

    private Declaration decl;
    private SymbolTable table;
    private ArrayList<VariableAssig> assignments;


    /**
     *
     * @param decl Declaration object, if not a variable we create a new symbole table
     * @param parent SymbolTable that this symbol is being inserted into
     * @param pos Position in the source code we encounter this declaration
     */
    public Symbol(Declaration decl, SymbolTable parent, Position pos) {
       if(!(decl instanceof VariableDecl)) this.table = new SymbolTable(parent, decl.getName());
       this.decl = decl;
       this.assignments = new ArrayList<>();
    }

    public Declaration getDecl() {
        return decl;
    }

    public SymbolTable getSubTable(){
        return table;
    }

    public String toString() {
        String rtn = decl.toString();
        if(!assignments.isEmpty()) rtn += "\tValue:\t"+ assignments.toString();
        return rtn;
    }

    public void add(VariableAssig va) {
        assignments.add(va);
    }
}
