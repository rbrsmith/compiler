package SemanticAnalyzer;


import LexicalAnalyzer.DFA.Position;
import SemanticEvaluation.SemanticEvaluation;
import SyntacticAnalyzer.Grammar;
import SyntacticAnalyzer.Tuple;

import java.util.ArrayList;

/**
 * Main Driver of the semantic analyzer
 * Class is responsible for building a parse tree from syntactic analyzer
 * Class then traverses this tree building symbol tables and looking for semantic exceptions
 */
public class Analyzer {


    // Rule in Grammar that is a trigger for semantic action
    public final static String SEMANTIC = "SEMANTIC";
    // Rules for semantic action
    public final static String CLASS_ACTION = "SEMANTIC-1";
    public final static String FUNC_ACTION_1 = "SEMANTIC-2";
    public final static String FUNC_ACTION_2 = "SEMANTIC-5";
    public final static String VAR_ACTION_1 = "SEMANTIC-3";
    public final static String VAR_ACTION_2 = "SEMANTIC-4";
    public final static String ARR_ACTION = "SEMANTIC-6";
    public final static String PROG_ACTION = "SEMANTIC-7";

    public final static String SCOPE_CHANGE_ACTION = "SEMANTIC-0";




    private Node root;
    // Pointer to current node in the parse tree
    private Node current;

    // Root symbol table, all sub tables are pointers in this main table
    private SymbolTable symbolTable;

    private SemanticEvaluation evaluation;

    private boolean eval;

    /**
     * Default constructor
     */
    public Analyzer() {
        root = null;
        current = null;
        symbolTable = new SymbolTable(null);
        evaluation = new SemanticEvaluation();
        this.eval = false;
    }


    /**
     * Called during syntactic phase
     * Called when a terminal is encountered
     * Function adds this terminal to the parse tree
     * @param tknTup Tuple of Type, Name
     * @param pos Position object in the source file
     */
    public void evaluate(Tuple<String, String> tknTup, Position pos) {
        // We ignore any parent with SEMANTIC - SEMANTIC is a leaf
        if(!current.isLeaf() && current.getValue().contains(SEMANTIC)) {
            current = current.getRightSibling();
            evaluate(tknTup, pos);
            return;
        }
        // Add node & move right one
        current.addChild(new Node(tknTup, true, false, current, pos));
        current = current.getRightSibling();
    }

    /**
     * Called during syntactic phrase
     * Called when a non-terminal is encountered
     * Function adds all items to the tree
     * @param parent String name of parent Node (item replaced on the stack)
     * @param children ArrayList String of children (RHS of rule added to the stack)
     * @param pos Position object holding our place in the source file
     */
    public void evaluate(String parent, ArrayList<String> children, Position pos) {
        if(root == null) {
            // Create root
            root = new Node(parent, false, true, null, pos);
            current = root;
        } else if (!current.isLeaf() && current.getValue().contains(SEMANTIC)) {
            // Skip SEMANTIC rule
            current = current.getRightSibling();
            evaluate(parent, children, pos);
            return;
        }

        // If we are at an EPSILON, added it and move on
        if(children.size() == 1 && children.get(0).equals(Grammar.EPSILON)) {
            Tuple<String, String> tmp = new Tuple();
            tmp.setX(Grammar.EPSILON);
            tmp.setY(Grammar.EPSILON);
            this.evaluate(tmp, pos);
            return;
        }

        // Add each child to the current node
        for(String child: children) {
            Node tmp = new Node(child, false, false, current, pos);
            current.addChild(tmp);
        }
        // Move on to next child
        current = current.getFirstChild();
    }


    /**
     * Main function used to traverse the parse tree
     * @param errors ArrayList of Exceptions from Syntactic phase, this will be added to as we parse
     */
    public void analyze(ArrayList<Exception> errors, boolean eval) {
        this.eval = eval;
        analyze(root, errors);

    }


    /**
     * Recursively traverse parse tree, building symbol tables and picking up semantic
     * exceptions along the way
     * @param current Node that we are currently on
     * @param errors ArrayList of Exceptions from Syntactic phase, this will be added to as we parse
     */
    private void analyze(Node current, ArrayList<Exception> errors) {
        // Perform any necessary actions
        classDeclaration(current, errors);
        functionDeclaration(current, errors);
        variableDeclaration(current, errors);
        programDeclaration(current, errors);
        endScope(current, errors);

        if(eval) evaluation.evaluate(current, symbolTable, errors);


        // Apply actions to children
        for(Node child : current.getChildrenValues()) {
            analyze(child, errors);
        }
    }

    /**
     * Create a class declaration if required
     * @param current Node
     * @param errors ArrayList of Exceptions to add to
     */
    private void classDeclaration(Node current, ArrayList<Exception> errors) {
        if (!current.isLeaf() && current.getValue().equals(CLASS_ACTION)) {
            Node id = current.getLeftSibling().getLeaf();
            ClassDecl c = new ClassDecl(id);
            if(!eval) {
                if (symbolTable.alreadyExists(c)) errors.add(
                        new AlreadyDeclaredException(current.getPosition(), c.getName()));
                Symbol sym = new Symbol(c, symbolTable, id.getPosition());
                SymbolTable sub2 = symbolTable.add(sym);
            }
            SymbolTable sub = symbolTable.get(c);
            symbolTable = sub;
        }
    }

    /**
     * Create a function declaration if required
     * @param current Node
     * @param errors ArrayList of Exception to add to
     */
    private void functionDeclaration(Node current, ArrayList<Exception> errors) {
        if (!current.isLeaf() && current.getValue().equals(FUNC_ACTION_1)) {
            Node id = current.getLeftSibling().getLeftSibling().getLeftSibling().getLeftSibling().getLeaf();
            Node type = id.getLeftSibling().getLeaf();
            Node fParams = current.getLeftSibling().getLeftSibling();
            FunctionDecl f = new FunctionDecl(id, type, fParams);
            Symbol sym;
            if(!eval) {
                if (symbolTable.alreadyExists(f)) errors.add(
                        new AlreadyDeclaredException(current.getPosition(), f.getName()));

                if (!f.isPrimitive() && !symbolTable.classExists(f.getType())) {
                    errors.add(new UndeclardException(current.getPosition(), f));
                }
                sym = new Symbol(f, symbolTable, type.getPosition());
                SymbolTable sub2 = symbolTable.add(sym);
            }
            SymbolTable sub = symbolTable.get(f);
            symbolTable = sub;
            if(!eval) {
                for (VariableDecl v : f.getParams()) {
                    sym = new Symbol(v, symbolTable, type.getPosition());
                    symbolTable.add(sym);
                }
            }
        }
    }

    /**
     * Create variable declaration if required
     * @param current Node
     * @param errors ArrayList of Exceptions to add to
     */
    private void variableDeclaration(Node current, ArrayList<Exception> errors) {
        if(!eval) {
            if (!current.isLeaf() && current.getValue().equals(VAR_ACTION_1)) {
                Node id = current.getLeftSibling().getLeftSibling().getLeaf();
                Node type = id.getLeftSibling().getLeaf();
                Node array = current.getLeftSibling();
                VariableDecl v = new VariableDecl(id, type, array);
                if (symbolTable.alreadyExists(v)) errors.add(
                        new AlreadyDeclaredException(current.getPosition(), v.getName()));
                if (!v.isPrimitive() && !symbolTable.classExists(v.getType())) {
                    errors.add(new UndeclardException(current.getPosition(), v));
                }

                if(!v.isPrimitive()) {
                    // ADD attribute possibilities
                    SymbolTable classTable = symbolTable.getClassSymbolTable(v.getType());
                    if(classTable != null) {
                        ArrayList<VariableDecl> attributes = classTable.getVariables();
                        v.setAttributes(attributes);
                    }
                }


                // Classes attributes are initialized
                if(symbolTable.getDecl() instanceof ClassDecl) {
                    v.initialize();
                }

                Symbol sym = new Symbol(v, symbolTable, type.getPosition());
                symbolTable.add(sym);
            }

            if (!current.isLeaf() && current.getValue().equals(VAR_ACTION_2)) {
                Node id = current.getLeftSibling().getLeaf();
                Node type = id.getLeftSibling().getLeaf();
                Node array = null;
                VariableDecl v = new VariableDecl(id, type, array);
                if (symbolTable.alreadyExists(v)) errors.add(
                        new AlreadyDeclaredException(current.getPosition(), v.getName()));

                if(!v.isPrimitive()) {
                    // ADD attribute possibilities
                    SymbolTable classTable = symbolTable.getClassSymbolTable(v.getType());
                    if(classTable != null) {
                        ArrayList<VariableDecl> attributes = classTable.getVariables();
                        v.setAttributes(attributes);
                    }
                }


                // Classes attributes are initialized
                if(symbolTable.getDecl() instanceof ClassDecl) {
                    v.initialize();
                }

                Symbol sym = new Symbol(v, symbolTable, type.getPosition());
                symbolTable.add(sym);
            }
        }
    }


    /**
     * Create program declaration if required
     * @param current Node
     * @param errors ArrayList of Exception to be added to
     */
    private void programDeclaration(Node current, ArrayList<Exception> errors) {
        if (!current.isLeaf() && current.getValue().equals(PROG_ACTION)) {
            Node program = current.getLeftSibling().getLeaf();
            ProgramDecl p = new ProgramDecl(program);
            if(!eval) {
                if (symbolTable.alreadyExists(p)) errors.add(
                        new AlreadyDeclaredException(current.getPosition(), p.getName()));
                Symbol sym = new Symbol(p, symbolTable, program.getPosition());
                SymbolTable sub2 = symbolTable.add(sym);
            }
            SymbolTable sub = symbolTable.get(p);
            symbolTable = sub;
        }

    }

    /**
     * End a scope statement, forcing symbol table to return a level up
     * @param current Node
     * @param errors ArrayList of Exception to add to
     */
    private void endScope(Node current, ArrayList<Exception> errors) {
        if (!current.isLeaf() && current.getValue().equals(SCOPE_CHANGE_ACTION)) {
            symbolTable = symbolTable.getParent();
        }
    }


    @Override
    public String toString() {
        return symbolTable.toString();
    }

}
