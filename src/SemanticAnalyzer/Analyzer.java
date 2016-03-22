package SemanticAnalyzer;


import LexicalAnalyzer.DFA.Token;
import SyntacticAnalyzer.Tuple;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Analyzer {

    private Node root;
    private Node current;

    private int bracketCount;

    private final ArrayList<String> variableDeclaration = new ArrayList<String>() {{
        add("FB1");
        add("CD1");
        add("S3");
    }};

    private final ArrayList<String> variableAssignment = new ArrayList<String>() {{
        add("FB1");
        add("assignStat");
        add("S3");
    }};

    private final ArrayList<String> functionDefinition = new ArrayList<String>() {{
        add("funcHead");
        add("CD1");
    }};

    private final ArrayList<String> classDefinition = new ArrayList<String>() {{
        add("classDecl");
    }};

    private final ArrayList<String> programDefinition = new ArrayList<String>() {{
        add("progBody");

    }};

    private SymbolTable symboleTable;
    private SymbolTable globalTable;

    public Analyzer() {
        root = null;
        current = null;
        symboleTable = new SymbolTable(null);
        globalTable = new SymbolTable(null);
        bracketCount = 0;
    }

    // Rule poppage
    public void evaluate(Tuple<String, String> tknTup) {
        if(!current.isLeaf() && current.getValue().contains("SEMANTIC")) {
            current = current.getRightSibling();
            evaluate(tknTup);
            return;
        }
        current.addChild(new Node(tknTup, true, false, current));

        if(current.getRightSibling() == null){
            if(!current.getParent().isRoot()) {
                // TODO
     //           System.err.println("ERRRRRRRRRRRRRRRRRRRRRROOOOOOOOOOOOORRRRRRRRRRRRRRRRR");
            }
            return;
        }

//        current = current.getRightSibling();
            current = current.getRightSibling();

    }

    public void evaluate(String tknTup, ArrayList<String> children) {
        if(root == null) {
            root = new Node(tknTup, false, true, null);
            current = root;

            if(children.size() == 1 && children.get(0).equals("EPSILON")) {
                Tuple<String, String> tmp = new Tuple();
                tmp.setX("EPSILON");
                tmp.setY("EPSILON");
                this.evaluate(tmp);
                return;
            }

            for(String child: children) {
                Node tmp = new Node(child, false, false, current);
                current.addChild(tmp);
            }
            current = current.getFirstChild();


        } else {
            if(!current.isLeaf() && current.getValue().contains("SEMANTIC")) {
                current = current.getRightSibling();
                evaluate(tknTup, children);
                return;
            }
            // TODO const for EPSILON
            if(children.size() == 1 && children.get(0).equals("EPSILON")) {
                Tuple<String, String> tmp = new Tuple();
                tmp.setX("EPSILON");
                tmp.setY("EPSILON");
                this.evaluate(tmp);
                return;
            }

            for(String child: children) {
                Node tmp = new Node(child, false, false, current);
                current.addChild(tmp);
            }
            current = current.getFirstChild();
        }
    }

    public void print() {
        root.print(0);
    }


    public void analyze() throws SemanticException {
     //   System.out.println("Analyze");
        analyze(root);
    }

    public void analyze(Node current) throws SemanticException {
        if(!current.isLeaf() && current.getValue().equals("SEMANTIC-1")) {
            Node id = current.getLeftSibling().getLeaf();
            ClassDecl c = new ClassDecl(id);
            SymbolTable sub = symboleTable.add(new Symbol(c, symboleTable));
            symboleTable = sub;
        }

        if(!current.isLeaf() && current.getValue().equals("SEMANTIC-2")) {
            Node id = current.getLeftSibling().getLeftSibling().getLeftSibling().getLeftSibling().getLeaf();
            Node type = id.getLeftSibling().getLeaf();
            Node fParams = current.getLeftSibling().getLeftSibling();
            FunctionDecl f = new FunctionDecl(id, type, fParams);
            SymbolTable sub = symboleTable.add(new Symbol(f, symboleTable));
            symboleTable = sub;
            for(VariableDecl v: f.getParams()) {
                symboleTable.add(new Symbol(v, symboleTable));
            }
        }

        if(!current.isLeaf() && current.getValue().equals("SEMANTIC-3")) {
            Node id = current.getLeftSibling().getLeftSibling().getLeaf();
            Node type = id.getLeftSibling().getLeaf();
            Node array = current.getLeftSibling();
            VariableDecl v = new VariableDecl(id, type, array);
            symboleTable.add(new Symbol(v, symboleTable));
        }

        if(!current.isLeaf() && current.getValue().equals("SEMANTIC-8")) {
            Node id = current.getLeftSibling().getLeaf();
            Node type = id.getLeftSibling().getLeaf();
            Node array = null;
            VariableDecl v = new VariableDecl(id, type, array);
            symboleTable.add(new Symbol(v, symboleTable));
        }



        if(!current.isLeaf() && current.getValue().equals("SEMANTIC-7")) {
            Node program = current.getLeftSibling().getLeaf();
            ProgramDecl p = new ProgramDecl(program);
            SymbolTable sub = symboleTable.add(new Symbol(p, symboleTable));
            symboleTable = sub;
        }

        if(!current.isLeaf() && current.getValue().equals("SEMANTIC-0")) {
            if(symboleTable.getParent() == null) throw new SemanticException("ERROR IN TABLE HIERARCHY");
            symboleTable = symboleTable.getParent();
        }


        for(Node child : current.getChildrenValues()) {
            analyze(child);
        }
    }

    public String toString() {
        return symboleTable.toString();
    }
}
