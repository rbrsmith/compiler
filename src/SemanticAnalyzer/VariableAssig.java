package SemanticAnalyzer;

import LexicalAnalyzer.DFA.InvalidCharacterException;
import LexicalAnalyzer.DFA.Token;
import SyntacticAnalyzer.Tuple;

import java.util.ArrayList;
import java.util.Collection;

public class VariableAssig {


    private String name;
    private ArrayList<Integer> arraySize;
    private ArrayList<VariableAssig> attributeName;

    public VariableAssig(Node variable) {
        name = null;
        arraySize = new ArrayList<>();
        attributeName = new ArrayList<>();
        if(variable.getValue().equals("variable")) {
            variable(variable);
        }else if(variable.getFirstLeafType().equals(Token.ID.toString() )) {
            name = variable.getFirstLeafValue();
        } else if(variable.getValue().equals("V1")) {
            Node FB2 = variable.getParent();
            Node FB4 = FB2.getParent();
            Node ID = FB4.getLeftSibling();
            if(ID.getFirstLeafType().equals(Token.ID.toString())) {
                name = ID.getFirstLeafValue();
                V1(variable);
            }
        }
    }

    public VariableAssig() {
        name = null;
        arraySize = new ArrayList<>();
        attributeName = new ArrayList<>();
    }


    private void V1(Node V1) {
        ArrayList<Tuple> tokens = V1.getTokens();
        if(tokens.size() == 0) {
            return;
        }
        VariableAssig current = this;
        for(Tuple t: tokens) {
            if(t.getX().equals(Token.DOT.toString())) {
                VariableAssig next = new VariableAssig();
                current.attributeName.add(next);
                current = next;
            } else if(t.getX().equals(Token.ID.toString())) {
                current.name = t.getY().toString();
            } else if(t.getX().equals(Token.INTEGER.toString())) {
                // This only works for simple arrays, any array that has an expression
                // As the index wont work
                current.arraySize.add(Integer.parseInt(t.getY().toString()));
            }
        }

    }


    public VariableAssig variable(Node variable) {
        Node firstLeaf = variable.getFirstChild().getLeaf();
        Tuple tkn = firstLeaf.getLeafValue();
        if(tkn.getX().equals(Token.ID.toString())) {
            name = (String) tkn.getY();
            Node V1 = firstLeaf.getRightSibling();
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
        for(VariableAssig v: attributeName){
            rtn += "." + v.toString();
        }

        return rtn.replaceAll(" = ...", "") + " = ...";
    }
    public String getName() {
        return name;
    }

    public ArrayList<Integer> getSize() {
        return arraySize;
    }

    public ArrayList<VariableAssig> getAttributes() {
        return attributeName;
    }
}
