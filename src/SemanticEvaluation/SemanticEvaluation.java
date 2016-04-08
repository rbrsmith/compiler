package SemanticEvaluation;

import SemanticAnalyzer.*;
import com.sun.corba.se.impl.io.TypeMismatchException;

import java.util.ArrayList;

public class SemanticEvaluation {

    private Expression expression;


    // Rule in Grammar that is a trigger for semantic action
    public final static String VAR_REF = "SEMANTIC-12";


    public final static String ASSIG_ACTION_1 = "SEMANTIC-10";
    public final static String ASSIG_ACTION_2 = "SEMANTIC-11";




    public void evaluate(Node current, SymbolTable symbolTable, ArrayList<Exception> errors) {

        variableGetReference(current, symbolTable, errors);
        variableAssignment(current, symbolTable, errors);


    }



    // GET
    private void variableGetReference(Node current, SymbolTable symbolTable, ArrayList<Exception> errors) {
        // TODO a return type of get?
        if (!current.isLeaf() && current.getValue().equals(VAR_REF)) {
            Node variable = current.getLeftSibling();
            try {
                VariableAssig va = new VariableAssig(variable, symbolTable);
                Symbol symbol = symbolTable.validate(va);
                if (symbol == null) {
                    throw new UndeclardException(variable.getPosition(), va);
                } else {
                    symbol.initialize();
                }
            } catch(Exception e) {
                errors.add(e);
            }
        }
    }


    /**
     * Create a variable assignment if required
     * @param current Node
     * @param errors ArrayList of exceptions to be added to
     */
    private void variableAssignment(Node current, SymbolTable symbolTable, ArrayList<Exception> errors) {
        // Get the assignment object if one exists
        VariableAssig va = null;
        try {
            if(!current.isLeaf() && current.getValue().equals(ASSIG_ACTION_2)) {
                va = new VariableAssig(current.getLeftSibling().getLeftSibling().getLeftSibling(), symbolTable);
            }
            if(!current.isLeaf() && current.getValue().equals(ASSIG_ACTION_1)) {
                 va = new VariableAssig(current.getLeftSibling().getLeftSibling(), symbolTable);
            }
            if(va == null) return;

            Symbol symbol = symbolTable.validate(va);
             // Check if this variable as been declared
            if(symbol == null) throw new UndeclardException(current.getPosition(), va);
            else {
                symbol.initialize();

                // Now make sure return types match otherwise throw an exception
                Node expr = current.getRightSibling();
                String exprType = new Expression(symbolTable).evaluate(expr);

                if(!(symbol.getDecl() instanceof VariableDecl)) throw new AlreadyDeclaredException(expr.getPosition(), symbol.getDecl().getName());
                VariableDecl LHSVar = (VariableDecl) symbol.getDecl();
                if (!LHSVar.getType().equals(exprType)) {
                    throw new InvalidTypesException(expr.getPosition(), exprType, LHSVar.getType(), symbol.getDecl().getName());
                }
            }

        } catch(Exception e){
            errors.add(e);
        }

    }




}
