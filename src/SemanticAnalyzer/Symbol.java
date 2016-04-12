package SemanticAnalyzer;

import CodeGeneration.CodeGenerator;
import LexicalAnalyzer.DFA.Position;

import java.util.ArrayList;

/**
 * CLass wrapping a declaration
 * Represents an entry in the symbol table
 */
public class Symbol {

    private Declaration decl;
    private SymbolTable table;
    private String memroyAddress;


    /**
     *
     * @param decl Declaration object, if not a variable we create a new symbole table
     * @param parent SymbolTable that this symbol is being inserted into
     * @param pos Position in the source code we encounter this declaration
     */
    public Symbol(Declaration decl, SymbolTable parent, Position pos) {
        if (!(decl instanceof VariableDecl)) this.table = new SymbolTable(parent, decl);
        this.decl = decl;

        this.memroyAddress = "";
        if (parent.getDecl() == null) {
            this.memroyAddress = CodeGenerator.getInstance().globalName  + decl.getName();;
        } else if (parent.getDecl() instanceof ClassDecl) {
            this.memroyAddress = CodeGenerator.getInstance().className + parent.getName() + decl.getName();
        } else if (parent.getDecl() instanceof VariableDecl) {
            this.memroyAddress = CodeGenerator.getInstance().variableName + decl.getName();
        } else if (parent.getDecl() instanceof FunctionDecl) {

            String address = "";
            CodeGenerator code = CodeGenerator.getInstance();
            FunctionDecl f = (FunctionDecl) parent.getDecl();
            if (parent.getParent().getDecl() instanceof ClassDecl) {
                address += code.className + parent.getParent().getName();
                address += code.functionName + f.getName() + code.variableName + decl.getName();
            } else {
                address += code.functionName + f.getName() + code.variableName + decl.getName();
                ;
            }

            this.memroyAddress = address;
        } else {

            this.memroyAddress += parent.getName() + "__"+ decl.getName();
        }
    }

    public Declaration getDecl() {
        return decl;
    }

    public SymbolTable getSubTable(){
        return table;
    }

    public String toString() {
        String rtn = decl.toString();
        return rtn;
    }

    public void initialize() {
        if(decl instanceof VariableDecl) {
            VariableDecl tmp = (VariableDecl) decl;
            tmp.initialize();
        }
    }


    public String getAddress() {
        return memroyAddress;
    }

    public Integer getOffset(VariableDecl lhs) {
        if(decl instanceof VariableDecl){
            VariableDecl tmp = (VariableDecl) decl;
            for(int i=0;i<tmp.getAttributes().size();i++){
                if(tmp.getAttributes().get(i).getName().equals(lhs.getName())) {
                    return i;
                }
            }

        }
        return null;
    }
}
