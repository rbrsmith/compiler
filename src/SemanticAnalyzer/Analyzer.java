package SemanticAnalyzer;


import LexicalAnalyzer.DFA.Position;
import SyntacticAnalyzer.Tuple;

import java.util.ArrayList;

public class Analyzer {

    private Node root;
    private Node current;


    private SymbolTable symbolTable;

    public Analyzer() {
        root = null;
        current = null;
        symbolTable = new SymbolTable(null);
    }


    public void evaluate(Tuple<String, String> tknTup, Position pos) {
        if(!current.isLeaf() && current.getValue().contains("SEMANTIC")) {
            current = current.getRightSibling();
            evaluate(tknTup, pos);
            return;
        }
        current.addChild(new Node(tknTup, true, false, current, pos));

        if(current.getRightSibling() == null){
            if(!current.getParent().isRoot()) {
                // TODO
            }
            return;
        }

            current = current.getRightSibling();

    }

    public void evaluate(String tknTup, ArrayList<String> children, Position pos) {
        if(root == null) {
            root = new Node(tknTup, false, true, null, pos);
            current = root;

            if(children.size() == 1 && children.get(0).equals("EPSILON")) {
                Tuple<String, String> tmp = new Tuple();
                tmp.setX("EPSILON");
                tmp.setY("EPSILON");
                this.evaluate(tmp, pos);
                return;
            }

            for(String child: children) {
                Node tmp = new Node(child, false, false, current, pos);
                current.addChild(tmp);
            }
            current = current.getFirstChild();


        } else {
            if(!current.isLeaf() && current.getValue().contains("SEMANTIC")) {
                current = current.getRightSibling();
                evaluate(tknTup, children, pos);
                return;
            }
            // TODO const for EPSILON
            if(children.size() == 1 && children.get(0).equals("EPSILON")) {
                Tuple<String, String> tmp = new Tuple();
                tmp.setX("EPSILON");
                tmp.setY("EPSILON");
                this.evaluate(tmp, pos);
                return;
            }

            for(String child: children) {
                Node tmp = new Node(child, false, false, current, pos);
                current.addChild(tmp);
            }
            current = current.getFirstChild();
        }
    }

    public void print() {
        root.print(0);
    }


    public void analyze() throws AlreadyDeclaredException {
        analyze(root);
    }

    public void analyze(Node current) throws AlreadyDeclaredException {
        if(!current.isLeaf() && current.getValue().equals("SEMANTIC-1")) {
            Node id = current.getLeftSibling().getLeaf();
            ClassDecl c = new ClassDecl(id);
            SymbolTable sub = symbolTable.add(new Symbol(c, symbolTable, id.getPosition()));
            symbolTable = sub;
        }

        if(!current.isLeaf() && current.getValue().equals("SEMANTIC-2")) {
            Node id = current.getLeftSibling().getLeftSibling().getLeftSibling().getLeftSibling().getLeaf();
            Node type = id.getLeftSibling().getLeaf();
            Node fParams = current.getLeftSibling().getLeftSibling();
            FunctionDecl f = new FunctionDecl(id, type, fParams);
            SymbolTable sub = symbolTable.add(new Symbol(f, symbolTable, type.getPosition()));
            symbolTable = sub;
            for(VariableDecl v: f.getParams()) {
                symbolTable.add(new Symbol(v, symbolTable, type.getPosition()));
            }
        }

        if(!current.isLeaf() && current.getValue().equals("SEMANTIC-3")) {
            Node id = current.getLeftSibling().getLeftSibling().getLeaf();
            Node type = id.getLeftSibling().getLeaf();
            Node array = current.getLeftSibling();
            VariableDecl v = new VariableDecl(id, type, array);
            symbolTable.add(new Symbol(v, symbolTable, type.getPosition()));
        }

        if(!current.isLeaf() && current.getValue().equals("SEMANTIC-8")) {
            Node id = current.getLeftSibling().getLeaf();
            Node type = id.getLeftSibling().getLeaf();
            Node array = null;
            VariableDecl v = new VariableDecl(id, type, array);
            symbolTable.add(new Symbol(v, symbolTable, type.getPosition()));
        }


        if(!current.isLeaf() && current.getValue().equals("SEMANTIC-7")) {
            Node program = current.getLeftSibling().getLeaf();
            ProgramDecl p = new ProgramDecl(program);
            SymbolTable sub = symbolTable.add(new Symbol(p, symbolTable, program.getPosition()));
            symbolTable = sub;
        }

        if(!current.isLeaf() && current.getValue().equals("SEMANTIC-0")) {
        //    if(symbolTable.getParent() == null) throw new SemanticException("ERROR IN TABLE HIERARCHY");
            symbolTable = symbolTable.getParent();
        }


        for(Node child : current.getChildrenValues()) {
            analyze(child);
        }
    }

    public String toString() {
        return symbolTable.toString();
    }
}
