package SemanticAnalyzer;

import LexicalAnalyzer.DFA.Token;
import SemanticEvaluation.VariableReference;
import SyntacticAnalyzer.Grammar;
import SyntacticAnalyzer.SyntacticException;
import SyntacticAnalyzer.Tuple;

import java.lang.reflect.AnnotatedTypeVariable;
import java.util.ArrayList;

/**
 * Class holding an assignment of a variable
 */
public class VariableAssig {


    // ex a = 5;
    private String name;
    // ex a[2] = 5;
    private int arraySize;
    // ex a.b = 5;
    private VariableAssig attributeName;

    private SymbolTable symbolTable;



    /**
     * Constructor
     * @param variable Node representing an ID, V1 or a Variable
     */
    public VariableAssig(Node variable, SymbolTable symbolTable) throws Exception {
        name = null;
        arraySize = 0;
        attributeName = null;
        this.symbolTable = symbolTable;
        if(variable.getValue().equals("variable")) {
            variable(variable, this);
        }else if(variable.getFirstLeafType().equals(Token.ID.toString() )) {
            name = variable.getFirstLeafValue();
        } else if(variable.getValue().equals("V1")) {
            // The ID of V1 is two parents up and one over
            Node FB2 = variable.getParent();
            Node FB4 = FB2.getParent();
            Node ID = FB4.getLeftSibling();
            if(ID.getFirstLeafType().equals(Token.ID.toString())) {
                name = ID.getFirstLeafValue();
                V1(variable, this);
            }
        }
    }


    /**
     * Default constructor
     */
    public VariableAssig() {
        name = null;
        arraySize = 0;
        attributeName = null;
        symbolTable = null;
    }



    /**
     * Analyze a V1
     * @param V1 Node representing a V1 tree
     */
//    private void V1(Node V1) {
//        ArrayList<Tuple> tokens = V1.getTokens();
//        if(tokens.size() == 0) {
//            return;
//        }
//        VariableAssig current = this;
//        for(Tuple t: tokens) {
//            if(t.getX().equals(Token.DOT.toString())) {
//                VariableAssig next = new VariableAssig();
//                current.attributeName = next;
//                current = next;
//            } else if(t.getX().equals(Token.ID.toString())) {
//                current.name = t.getY().toString();
//            } else if(t.getX().equals(Token.INTEGER.toString())) {
//                // This only works for simple arrays, any array that has an expression
//                // As the index wont work
//                // TODO - anaylze expr ex: indiceR
//                current.arraySize += 1;
//            }
//        }
//
//    }

    private void V1(Node V1, VariableAssig va) throws Exception {
//        V1 -> indiceR V2
//        V2 -> DOT variable
//        V2 -> EPSILON
        Node indiceR = V1.getFirstChild();
        // Kind of a hack here
        ArrayList<Tuple> tokens = indiceR.getTokens();
        for(Tuple token: tokens) {
            if(token.getX().equals(Token.DOT.toString())) break;
            if(token.getX().equals(Token.ASSIGNMENT.toString())) break;
            if(token.getX().equals(Token.SEMICOLON.toString())) break;
            if(token.getX().equals(Token.CSB.toString())) {
                va.arraySize += 1;
            }
        }

        VariableReference vr = new VariableReference(symbolTable);
        vr.indiceR(indiceR);

        // TODO VR in indiceR
        Node V2 = indiceR.getRightSibling();
        if(V2.getFirstLeafType().equals(Grammar.EPSILON)) return;
        else {
            Node dot = V2.getFirstChild();
            Node variable = dot.getRightSibling();
            va.attributeName = new VariableAssig();
            variable(variable, va.attributeName);

        }
    }

    /**
     *
     * @param variable Node representing a Variable tree
     * @return VariableAssig if we find a variable assignment | Null otherwise
     */
    public VariableAssig variable(Node variable, VariableAssig va) throws Exception {
        Node firstLeaf = variable.getFirstChild().getLeaf();
        Tuple tkn = firstLeaf.getLeafValue();
        if(tkn.getX().equals(Token.ID.toString())) {
            va.name = (String) tkn.getY();
            Node V1 = firstLeaf.getRightSibling();
            if(V1.getValue().equals("V1")){
                V1(V1, va);
                return this;
            }
        }
        return null;
    }

    @Override
    public String toString(){
        String rtn = "";
        rtn = name;
        if(arraySize != 0) rtn += " size: " + arraySize;
        if(attributeName != null) rtn += " [ " + attributeName.toString() + " ]";

        return rtn;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return arraySize;
    }

    public VariableAssig getAttribute() {
        return attributeName;
    }


}
