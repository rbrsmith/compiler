package SemanticAnalyzer;

import SemanticEvaluation.Variable;
import SemanticEvaluation.VariableReference;

import java.util.ArrayList;

/**
 * Class that holds a bunch of symbols
 */
public class SymbolTable {

    ArrayList<Symbol> symbols;
    private SymbolTable parent;
    private Declaration decl;
    private String name;

    /**
     *
     * @param parent SymbolTable this is a child of
     */
    public SymbolTable(SymbolTable parent) {
        symbols = new ArrayList<>();
        this.parent = parent;
        // Default to global table
        decl = null;
        this.name = "Global";
    }

    /**
     *
     * @param parent SymbolTable this is a child of
     */
    public SymbolTable(SymbolTable parent, Declaration decl) {
        this.decl = decl;
        this.name = decl.getName();
        symbols = new ArrayList<>();
        this.parent = parent;
    }

    /**
     *
     * @param sym Symbol to be added to this table
     * @return SymbolTable that this symbol may have created
     */
    public SymbolTable add(Symbol sym) {
        symbols.add(sym);
        return sym.getSubTable();
    }

    /**
     *
     * @return SymbolTable one level higher than this table
     */
    public SymbolTable getParent() {
        return parent;
    }

    public String toString() {
        ArrayList<SymbolTable> tables = new ArrayList<>();
        String rtn = "Table Name: " + this.name + "\n";
        rtn += "-------\n";
        for (Symbol s : symbols) {
            rtn += s.toString() + "\n";
            if (s.getSubTable() != null) {
                tables.add(s.getSubTable());
            }
        }
        rtn += "-------\n";
        rtn += "\n";
        for (SymbolTable t : tables) {
            rtn += t.toString();
        }

        return rtn;
    }


    /**
     * Recursively determine if d already exists in current scope
     * @param d Declaration to determine if is already defined
     * @return True if defined | False otherwise
     */
    public boolean alreadyExists(Declaration d) {
        String name = d.getName();
        for (Symbol s : symbols) {

            // Class name can be re-used, just not for creating another class
            if (s.getDecl().getName().equals(name)) {
                if(s.getDecl() instanceof ClassDecl && d instanceof ClassDecl) return true;
                if(s.getDecl() instanceof ClassDecl) continue;
                else return true;
            }
        }
        if(parent == null) return false;
        // Look in parent scope
        else return parent.alreadyExists(d);
    }

    /**
     *
     * @param v Variable to validate
     * @return Symbol if v is found | null otherwise
     */
    public Symbol validate(Variable v) {
        return validate(v, true);
    }

    /**
     *
     * @param v Variable to validate
     * @param global boolean indicating if we should search in parent tables for v
     * @return Symbol if v is found | null otherwise
     */
    public Symbol validate(Variable v, boolean global) {
        Symbol s = findID(v.getName(), global);
        if (s == null) {
            return null;
        } else {
            // Match array size
            if (s.getDecl() instanceof VariableDecl) {
                VariableDecl tmp = (VariableDecl) s.getDecl();
                if(tmp.getSize().size() < v.getSize()) return null;
                if(tmp.getSize().size() > 0 && v.getSize() == 0) return null;

            }
        }

        // So ID is a match and array size is a match, now check attributes
        if(v.getAttribute() != null) {
            // We think we have a class
            if(s.getDecl() instanceof VariableDecl) {
                // Find that class name...
                VariableDecl tmp = (VariableDecl) s.getDecl();
                SymbolTable classTable = getClassSymbolTable(tmp.getType());
                if(classTable == null) {
                    return null;
                }
                // OK GOOD, we have a class
                Variable vrAttr =  v.getAttribute();
                // We can only do this because nothing is allowed to be declared before
                // classes according to the grammar
                if(classTable.validate(vrAttr, false) == null) return null;

            } else {
                return null;
            }
        }
        return s;
    }


    /**
     *
     * @param type String representing a class name
     * @return SymbolTable representing all elements defined in @type class
     */
    public SymbolTable getClassSymbolTable(String type) {
        // Get the class symbols in this table
        for(Symbol s: symbols) {
            if(s.getDecl() instanceof ClassDecl) {
                if(s.getDecl().getName().equals(type)) {
                    return s.getSubTable();
                }
            }
        }
        // Have not found, check with parent
        if(parent != null) {
            return parent.getClassSymbolTable(type);
        } else {
            return null;
        }
    }

    /**
     *
     * @param name String of symbol we are looking for
     * @return Symbol matching name if found
     */
    private Symbol findID(String name) {
        return findID(name, true);
    }

    /**
     *
     * @param name String of symbol we are looking for
     * @param global Boolean if we should look in parent scope or not
     * @return Symbol matching name if found
     */
    private Symbol findID(String name, boolean global) {
        for (Symbol s : symbols) {
            if (s.getDecl().getName().equals(name)) {
                return s;
            }
        }
        // No luck, check with parent
        if (parent != null && global) {
            return parent.findID(name, global);
        } else {
            return null;
        }
    }

    /**
     *
     * @param type String name of class
     * @return True if this class is defined | False otherwise
     */
    public boolean classExists(String type) {
        // Classes are always defined in the global table
        // So get the global table
        SymbolTable current = this;
        while(current.getParent() != null) {
            current = current.getParent();
        }
        // Now look for this class matching this type
        for(Symbol s: current.symbols) {
            if(s.getDecl() instanceof ClassDecl) {
                if(s.getDecl().getName().equals(type)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *
     * @param decl Declation to be looked up
     * @return SymbolTable at that decl | null if not found
     */
    public SymbolTable get(Declaration decl) {
        String name = decl.getName();
        for(Symbol s: symbols) {
            Declaration symbolDecl = s.getDecl();
            if(symbolDecl.getName().equals(name)) {
                if(decl instanceof ClassDecl && symbolDecl instanceof ClassDecl) return s.getSubTable();
                if(decl instanceof VariableDecl && symbolDecl instanceof VariableDecl) return s.getSubTable();
                if(decl instanceof FunctionDecl && symbolDecl instanceof FunctionDecl) return s.getSubTable();
                if(decl instanceof ProgramDecl && symbolDecl instanceof ProgramDecl) return s.getSubTable();
            }
        }
        return null;
    }


    public Declaration getDecl() {
        return decl;
    }

    /**
     *
     * @return List of Variables in this table
     */
    public ArrayList<VariableDecl> getVariables() {
        ArrayList<VariableDecl> res = new ArrayList<>();
        for(Symbol s: symbols) {
            if(s.getDecl() instanceof VariableDecl) {
                res.add((VariableDecl) s.getDecl());
            }
        }
        return res;
    }

    public String getName() {
        return name;
    }
}
