package SemanticAnalyzer;

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
     * Validate that variables used in a variable assignment are defiend
     * // TODO - does not work for expr in RHS ie indiceR
     * @param v VariableAssig representing a variable assignment
     * @return True if the vairables in v are defiend, False otherwise
     */
    public Symbol validate(VariableAssig v) {
        return validate(v, true);
    }

    /**
     * Validate that variables used in a variable assignment are defiends
     * @param v VariableAssig representing a variable assignment
     * @param global boolean if we should look globally or not
     * @return True if the vairables in v are defiend, False otherwise
     */
    public Symbol validate(VariableAssig v, boolean global) {
        // Get symbol with this name
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
                VariableAssig va = v.getAttribute();
                // We can only do this because nothing is allowed to be declared before
                // classes according to the grammar
                if(classTable.validate(va, false) == null) return null;
            } else {
                return null;
            }
        }
        return s;
    }


    public Symbol validate(VariableReference vr) {
        return validate(vr, true);
    }

    public Symbol validate(VariableReference vr, boolean global) {
        Symbol s = findID(vr.getName(), global);
        if (s == null) {
            return null;
        } else {
            // Match array size
            if (s.getDecl() instanceof VariableDecl) {
                VariableDecl tmp = (VariableDecl) s.getDecl();
                if(tmp.getSize().size() < vr.getSize()) return null;
                if(tmp.getSize().size() > 0 && vr.getSize() == 0) return null;

            }
        }

        // So ID is a match and array size is a match, now check attributes
        if(vr.getAttribute() != null) {
            // We think we have a class
            if(s.getDecl() instanceof VariableDecl) {
                // Find that class name...
                VariableDecl tmp = (VariableDecl) s.getDecl();
                SymbolTable classTable = getClassSymbolTable(tmp.getType());
                if(classTable == null) {
                    return null;
                }
                // OK GOOD, we have a class
                VariableReference vrAttr =  vr.getAttribute();
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
