package SemanticAnalyzer;

import CodeGeneration.CompilerException;
import LexicalAnalyzer.DFA.Token;
import SemanticEvaluation.*;
import SyntacticAnalyzer.Grammar;
import SyntacticAnalyzer.Tuple;

import java.util.ArrayList;

/**
 * Class holding an assignment of a variable
 */
public class VariableAssig implements Variable{


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
    public VariableAssig(Node variable, SymbolTable symbolTable) throws CompilerException, FatalCompilerException {
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
     *
     * @param V1 Node
     * @param va VariableAssignment to be updated with any information
     * @throws Exception
     */
    private void V1(Node V1, VariableAssig va) throws CompilerException, FatalCompilerException {
        Node indiceR = V1.getFirstChild();
        ArrayList<Tuple> tokens = indiceR.getTokens();
        // Determine array dimension
        for(Tuple token: tokens) {
            if(token.getX().equals(Token.DOT.toString())) break;
            if(token.getX().equals(Token.ASSIGNMENT.toString())) break;
            if(token.getX().equals(Token.SEMICOLON.toString())) break;
            if(token.getX().equals(Token.CSB.toString())) {
                va.arraySize += 1;
            }
        }

        // Get access to indiceR function
        VariableReference vr = new VariableReference(symbolTable);
        vr.indiceR(indiceR);

        // Continue evaluation down the tree
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
    public VariableAssig variable(Node variable, VariableAssig va) throws CompilerException, FatalCompilerException {
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
        for(int i=0;i<arraySize; i++){
            rtn += "[]";
        }
        if(attributeName != null) rtn += " . " + attributeName.toString() + " ";

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
