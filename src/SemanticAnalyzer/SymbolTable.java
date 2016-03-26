package SemanticAnalyzer;

import java.util.ArrayList;

public class SymbolTable {

    ArrayList<Symbol> symbols;
    private int countBrackets;
    private SymbolTable parent;
    private String name;
    private boolean locked;

    public SymbolTable(SymbolTable parent) {
        symbols = new ArrayList<>();
        this.parent = parent;
        this.name = "Global";
        locked = false;
    }

    public SymbolTable(SymbolTable parent, String name) {
        this.name = name;
        symbols = new ArrayList<>();
        this.parent = parent;
    }

    public SymbolTable add(Symbol sym) {
        symbols.add(sym);
        return sym.getSubTable();
    }

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


    public boolean alreadyExists(Declaration d) {
        String name = d.getName();
        for (Symbol s : symbols) {

            // Allow using same names as class - unless its another class
            if (s.getDecl().getName().equals(name)) {
                if(s.getDecl() instanceof ClassDecl && d instanceof ClassDecl) return true;
                if(s.getDecl() instanceof ClassDecl) continue;
                else return true;
            }
        }
        if(parent == null) return false;
        else return parent.alreadyExists(d);
    }


    public boolean validate(VariableAssig v) {
        Symbol s = findID(v.getName());
        if (s == null) {
            return false;
        } else {
            // Match array size
            if (s.getDecl() instanceof VariableDecl) {
                VariableDecl tmp = (VariableDecl) s.getDecl();
                if (tmp.getSize().size() >= v.getSize().size()) {
                    for (int i = 0; i < v.getSize().size(); i++) {
                        if (tmp.getSize().get(i) < v.getSize().get(i)) {
                            return false;
                        }
                    }
                } else {
                    return false;
                }
            }
        }

        // SO we have an id...now for params
        if(v.getAttributes().size() > 0) {
            // We think we have a class
            if(s.getDecl() instanceof VariableDecl) {
                // Find that class name...
                VariableDecl tmp = (VariableDecl) s.getDecl();
                SymbolTable classTable = getClassSymbolTable(tmp.getType());
                if(classTable == null) {
                    return false;
                }
                // OK GOOD, we have a class
                boolean allGood = true;
                for(VariableAssig va: v.getAttributes()) {
                    // We can only do this because nothing is allowed to be declared befre
                    // classes according to the grammar
                    if(!classTable.validate(va)) {
                        allGood = false;
                    }
                }
                if(!allGood) return false;
            } else {
                return false;
            }
        }
        return true;
    }



    private SymbolTable getClassSymbolTable(String type) {
        for(Symbol s: symbols) {
            if(s.getDecl() instanceof ClassDecl) {
                if(s.getDecl().getName().equals(type)) {
                    return s.getSubTable();
                }
            }
        }
        if(parent != null) {
            return parent.getClassSymbolTable(type);
        } else {
            return null;
        }
    }

    private Symbol findID(String name) {
        boolean found = false;
        Symbol foundSym = null;
        for (Symbol s : symbols) {
            if (s.getDecl().getName().equals(name)) {
                return s;
            }
        }
        if (parent != null) {
            return parent.findID(name);
        } else {
            return null;
        }
    }

    public String getName() {
        return name;
    }

    public boolean classExists(String type) {
        // Classes are always defined in the global table
        SymbolTable current = this;
        while(current.getParent() != null) {
            current = current.getParent();
        }
        for(Symbol s: current.symbols) {
            if(s.getDecl() instanceof ClassDecl) {
                if(s.getDecl().getName().equals(type)) {
                    return true;
                }
            }
        }
        return false;
    }
}
