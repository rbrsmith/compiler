package SemanticEvaluation;

import SemanticAnalyzer.*;
import com.sun.org.apache.xpath.internal.operations.Variable;

import java.util.ArrayList;

public class SemanticEvaluation {


    // Rule in Grammar that is a trigger for semantic action
    public final static String SEMANTIC = "SEMANTIC";
    public final static String VAR_REF = "SEMANTIC-12";
    public final static String VAR_REF_EXPR = "SEMANTIC-13";


    public final static String ASSIG_ACTION_1 = "SEMANTIC-10";
    public final static String ASSIG_ACTION_2 = "SEMANTIC-11";

    public void evaluate(Node current, SymbolTable symbolTable, ArrayList<Exception> errors) {
//        variableReference(current, symbolTable, errors);
//        variableExpressionReference(current, symbolTable, errors);
        variableAssignment(current, symbolTable, errors);

    }


    // GET
    private void variableReference(Node current, SymbolTable symbolTable, ArrayList<Exception> errors) {
//        if (!current.isLeaf() && current.getValue().equals(VAR_REF)) {
//            Node variable = current.getLeftSibling();
//            VariableAssig va = new VariableAssig(variable);
//            if(symbolTable.validate(va) == null) {
//                errors.add(new UndeclardException(variable.getPosition(), va));
//            }
//        }
    }


    // reference as rexpression
    private void variableExpressionReference(Node current, SymbolTable symbolTable, ArrayList<Exception> errors) {
//        if (!current.isLeaf() && current.getValue().equals(VAR_REF_EXPR)) {
//            Node expr = current.getLeftSibling();
//        //    evaluateExpr(expr);
//
//        }

    }

    /**
     * Create a variable assignment if required
     * @param current Node
     * @param errors ArrayList of exceptions to be added to
     */
    private void variableAssignment(Node current, SymbolTable symbolTable, ArrayList<Exception> errors) {
        // Get the assignment object if one exists
        VariableAssig va = null;

        if(!current.isLeaf() && current.getValue().equals(ASSIG_ACTION_2)) {
            String expr = evaluateExpr(current.getRightSibling(), symbolTable);
            va = new VariableAssig(current.getLeftSibling().getLeftSibling().getLeftSibling(), expr);
        }
        if(!current.isLeaf() && current.getValue().equals(ASSIG_ACTION_1)) {
            String expr = evaluateExpr(current.getRightSibling(), symbolTable);
            va = new VariableAssig(current.getLeftSibling().getLeftSibling(), expr);
        }
        if(va == null) return;

        Symbol symbol = symbolTable.validate(va);
        // Check if this variable as been declared
        if(symbol == null) errors.add(
                new UndeclardException(current.getPosition(), va));
        else {
            // It has, so add the assignment to the symbol
            symbol.add(va);
        }
    }


    private String evaluateExpr(Node expr, SymbolTable symbolTable) {
        Node arithExpr = expr.getFirstChild();
        Node E1 = arithExpr.getRightSibling();
        return evaluateArithExpr(arithExpr, symbolTable) + " " + evaluateE1(E1, symbolTable);
    }


    private String evaluateArithExpr(Node arithExpr, SymbolTable symbolTable) {
        Node term = arithExpr.getFirstChild();
        Node arithExprRight = term.getRightSibling();
        return evaluteTerm(term, symbolTable) + " " + evaluateArithExprRight(arithExprRight, symbolTable);
    }

    private String evaluateE1(Node e1, SymbolTable symbolTable) {
        return "TODO - E1";
    }

    private String evaluteTerm(Node term, SymbolTable symbolTable) {
        Node factor = term.getFirstChild();
        Node termRight = factor.getRightSibling();
        return evaluateFactor(factor, symbolTable) + " " +evaluateTermRight(termRight, symbolTable);
    }

    private String evaluateArithExprRight(Node arithExprRight, SymbolTable symbolTable) {
        return "TODO - arithExprRight";
    }

    private String evaluateFactor(Node factor, SymbolTable symbolTable) {
        Node first = factor.getFirstChild();
        switch(first.getValue()) {
            case "id":
                // Id is a variable, we need to make sure it is declared
                // and initialized
                return first.getFirstLeafValue();
            case "num":
                return first.getFirstLeafValue();
            case "ORB":
                Node arithExpr = first.getRightSibling();
                return evaluateArithExpr(arithExpr, symbolTable);
            case "not":
                Node factor2 = first.getRightSibling();
                return "not " + evaluateFactor(factor2, symbolTable);
            case "sign":
                Node factor3 = first.getRightSibling();
                return "+ " + evaluateFactor(factor3, symbolTable);
        }
        return "";
    }

    private String evaluateTermRight(Node termRight, SymbolTable symbolTable) {
        return "TODO - termRight";
    }





}
