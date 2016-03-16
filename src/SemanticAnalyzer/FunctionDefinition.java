package SemanticAnalyzer;

import LexicalAnalyzer.DFA.Reserved;
import LexicalAnalyzer.DFA.Token;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rbrsmith on 16/03/16.
 */
public class FunctionDefinition {


    private String name;
    private String type;
    private ArrayList<VariableDeclaration> params;

    public FunctionDefinition(){
        name = null;
        params = new ArrayList<>();
    }

    public FunctionDefinition define(Node parent, HashMap<Integer, Node> nodes){

        Integer lowest = null;
        for(Integer i : nodes.keySet()){
            if(lowest == null || i < lowest) {
                lowest = i;
            }
        }
        Node first = nodes.get(lowest);


        // read type id then CD2
        // if CD2 goes to ORB then you are in fparams country

        if(first.getFirstLeafType().equals(Reserved.INT.toString()) ||
                first.getFirstLeafType().equals(Reserved.FLOAT.toString()) ||
                first.getFirstLeafType().equals(Token.ID.toString())) {
                    type = first.getFirstLeafValue();

                Node ID = first.getLeftSibling();
                if(ID.getFirstLeafType().equals(Token.ID.toString())) {
                    name = ID.getFirstLeafValue();

                    Node CD2 = ID.getLeftSibling();
                    Node ORB = null;
                    if(CD2.getValue().equals("CD2")) {
                        ORB = CD2.getFirstChild();
                    } else if(CD2.getValue().equals("ORB")) {
                        ORB = CD2;
                    } else {
                        return null;
                    }
                    if(ORB.getValue().equals("ORB")) {
                        Node FPARAMS = ORB.getLeftSibling();
                        if(FPARAMS.getValue().equals("fParams")) {
                            VariableDeclaration v = new VariableDeclaration();
                            v = v.fParams(FPARAMS.children);
                            if(v!=null) params.add(v);

                            // Recursively look for more params
                            Node FPARAMSTAILR = FPARAMS.getFirstChild().getLeftSibling().
                                    getLeftSibling().getLeftSibling();
                            if(FPARAMSTAILR.getValue().equals("fParamsTailR")) {
                                fParamsTailR(FPARAMSTAILR);
                            }


                            return this;
                        }
                    }


                }
        }

        return null;
    }

    private void fParamsTailR(Node fparamstailr) {
        Node first = fparamstailr.getFirstChild();
        if(first.getFirstLeafType().equals("EPSILON")) {

        } else {
            if(first.getValue().equals("fParamsTail")) {
                VariableDeclaration vd = new VariableDeclaration();
                vd = vd.fParams(first.children);
            }
            Node second = first.getLeftSibling();
            if(second.getValue().equals("fParamsTailR")) {
                fParamsTailR(second);
            }
        }

    }

    @Override
    public String toString() {
        String rtn = "";
        rtn += this.type + " " + this.name + "( ";
        for(VariableDeclaration vd: params) {
            rtn += vd.toString() + ", ";
        }
        if(rtn.indexOf(",") != -1) {
            rtn = rtn.substring(0, rtn.length() - 2);
        }
        rtn += ")";
        return rtn;

    }
}
