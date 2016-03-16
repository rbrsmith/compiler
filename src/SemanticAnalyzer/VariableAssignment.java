package SemanticAnalyzer;

import LexicalAnalyzer.DFA.Token;
import SyntacticAnalyzer.Rule;
import SyntacticAnalyzer.Tuple;

import java.util.ArrayList;
import java.util.HashMap;

public class VariableAssignment {

    private String name;
    private ArrayList<Integer> arraySize;
    private ArrayList<VariableAssignment> attributeName;



    private ArrayList<Rule> rules = new ArrayList<Rule>() {{
        add(new Rule("", "id V1 assignOp expr SEMICOLON"));

    }};

    public VariableAssignment() {
        name = null;
        arraySize = new ArrayList<>();
        attributeName = new ArrayList<>();
    }

    public VariableAssignment assign(Node parent, HashMap<Integer, Node> nodes) {
        Integer lowest = null;
        for(Integer i : nodes.keySet()){
            if(lowest == null || i < lowest) {
                lowest = i;
            }
        }
        Node first = nodes.get(lowest);



        // FB1
        // First there should be an id
        // Then FB4 -> FB2 -> V1
        // Evaluate V1 for the variable
        if(parent.getValue().equals("FB1")) {
            Node firstL = first.getLeaf();
            Tuple firstLeaf = firstL.getLeafValue();
            if(first.getFirstLeafType().equals(Token.ID.toString())) {
                this.name = first.getFirstLeafValue();
                Node FB4 = first.getLeftSibling();
                if(FB4.getValue().equals("FB4")) {
                    Node FB2 = FB4.getFirstChild();
                    if(FB2.getValue().equals("FB2")) {
                        Node V1 = FB2.getFirstChild();
                        if(V1.getValue().equals("V1")) {
                            V1(V1);
                            return this;
                        }
                    }
                }
            }
        }


        // AssignStat
        // First there should ve variable
        // Evaluate variable for variable
        else if(parent.getValue().equals("assignStat")) {
            if(first.getValue().equals("variable")) {
                return variable(first);
            } else {
                return null;
            }
        }
        // S3
        // First there should be for then ORB then type then id
        // Evalaute ID for variable
        else if(parent.getValue().equals("S3")) {
            // GO four over to ID
            Node ID = first.getLeftSibling().getLeftSibling().getLeftSibling();
            if(ID.getFirstLeafType().equals(Token.ID.toString())){
                name = ID.getFirstLeafValue();
                return this;
            }
        }

        return null;

    }

    private void V1(Node V1) {
        /*
        indiceR V2
        V2 -> DOT variable
        V2 -> EPSILON
         */
        ArrayList<Tuple> tokens = V1.getTokens();
        if(tokens.size() == 0) {
            return;
        }
        VariableAssignment current = this;
        for(Tuple t: tokens) {
            if(t.getX().equals(Token.DOT.toString())) {
                VariableAssignment next = new VariableAssignment();
                current.attributeName.add(next);
                current = next;
            } else if(t.getX().equals(Token.ID.toString())) {
                current.name = t.getY().toString();
            } else if(t.getX().equals(Token.INTEGER.toString())) {
                current.arraySize.add(Integer.parseInt(t.getY().toString()));
            }
        }

    }


    public VariableAssignment variable(Node variable) {
        Node firstLeaf = variable.getFirstChild().getLeaf();
        Tuple tkn = firstLeaf.getLeafValue();
        if(tkn.getX().equals(Token.ID.toString())) {
            name = (String) tkn.getY();
            Node V1 = firstLeaf.getLeftSibling();
            if(V1.getValue().equals("V1")){
                V1(V1);
                return this;
            }
        }
        return null;
    }

    @Override
    public String toString(){
        String rtn = "";
        rtn = name;
        for(Integer i : arraySize) {
            rtn += "["+i+"]";
        }
        for(VariableAssignment v: attributeName){
            rtn += "." + v.toString();
        }

        return rtn.replaceAll(" = ...", "") + " = ...";
    }
}
