package SemanticAnalyzer;


import SyntacticAnalyzer.Tuple;

import java.util.ArrayList;

public class Analyzer {

    private Node root;
    private Node current;

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


    public Analyzer() {
        root = null;
        current = null;
    }

    // Rule poppage
    public void evaluate(Tuple<String, String> tknTup) {
        current.addChild(new Node(tknTup, true, false, current));

        if(current.getLeftSibling() == null){
            if(!current.getParent().isRoot()) {
                System.err.println("ERRRRRRRRRRRRRRRRRRRRRROOOOOOOOOOOOORRRRRRRRRRRRRRRRR");
            }
            return;
        }
        current = current.getLeftSibling();



    }

    public void evaluate(String tknTup, ArrayList<String> children) {
        if(root == null) {
            root = new Node(tknTup, false, true, null);
            current = root;
        } else {
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


    public void analyze() {
        System.out.println("Analyze");
        analyze(root);
    }

    public void analyze(Node current) {
        for(Node child : current.children.values()) {


            if(variableDeclaration.contains(child.getValue())) {
                VariableDeclaration v = new VariableDeclaration();
                v = v.declare(child.children);
                if(v != null) System.out.println("DECL:  " + v);
            }
            if(variableAssignment.contains(child.getValue())) {
                VariableAssignment v = new VariableAssignment();
                v = v.assign(child, child.children);
                if(v!=null) System.out.println("ASSIG:  " + v);

            }
            if(functionDefinition.contains(child.getValue())) {
                FunctionDefinition f = new FunctionDefinition();
                f = f.define(child, child.children);
                if(f!=null) System.out.println("FUNC: " + f);
            }
            analyze(child);

        }
    }
}
