package SemanticEvaluation;

import CodeGeneration.CodeGenerator;
import CodeGeneration.CompilerException;
import LexicalAnalyzer.DFA.Token;
import SemanticAnalyzer.*;
import SyntacticAnalyzer.Grammar;

import java.util.ArrayList;

/**
 * Class used whenever we encoutner a variable in the Right Hand Side (most often in an expression)
 */
public class VariableReference implements Variable {

    private String name;
    private int size;
    private VariableReference attribute;

    private String type;

    // Variables used within this variable refernce
    // Ex a = b(c(d)) - c and d are sub variables
    private ArrayList<VariableReference> subVariables;

    // Parameters if this is a function
    private ArrayList<VariableReference> params;

    private SymbolTable symbolTable;

    private CodeGenerator code;


    public VariableReference(Node id, SymbolTable symbolTable) throws CompilerException, FatalCompilerException {
        this.name = id.getFirstLeafValue();
        this.size = 0;
        this.attribute = null;
        this.subVariables = new ArrayList<>();
        this.params = new ArrayList<>();
        this.symbolTable = symbolTable;
        this.code = CodeGenerator.getInstance();
        this.type = null;

        Node F1 = id.getRightSibling();
        Node F1First = F1.getFirstChild();

        // Start evaluation
        if(F1First.getValue().equals("indiceR")) {
            indiceR(F1First);
            F2(F1First.getRightSibling());
        } else if(F1First.getValue().equals("F3")) {
            F3(F1First);
        }
    }


    /**
     * Empty constructor - mostly used for parameter building on numbers
     * Ex: a.b(3) - 3 would use this
     * @param symbolTable
     */
    public VariableReference(SymbolTable symbolTable) {
        this.name = null;
        this.size = 0;
        this.attribute = null;
        this.subVariables = new ArrayList<>();
        this.params = new ArrayList<>();
        this.symbolTable = symbolTable;
        this.code = CodeGenerator.getInstance();
        this.type = null;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    /**
     *
     * @param indiceR Node
     */
    public void indiceR(Node indiceR) throws CompilerException, FatalCompilerException {
        if(indiceR.getFirstLeafType().equals(Grammar.EPSILON)) return;
        else {
            indice(indiceR.getFirstChild());
            indiceR(indiceR.getFirstChild().getRightSibling());
        }
    }

    /**
     *
     * @param indice Node
     */
    private void indice(Node indice) throws CompilerException, FatalCompilerException {
        this.size += 1;
        Node arithExpr = indice.getFirstChild().getRightSibling();
        arithExpr(arithExpr);
    }

    /**
     *
     * @param arithExpr Node
     */
    private void arithExpr(Node arithExpr) throws CompilerException, FatalCompilerException {
        term(arithExpr.getFirstChild());
        arithExprRight(arithExpr.getFirstChild().getRightSibling());
    }

    /**
     *
     * @param term Node
     */
    private void term(Node term) throws CompilerException, FatalCompilerException {
        factor(term.getFirstChild());
        termRight(term.getFirstChild().getRightSibling());
    }


    /**
     *
     * @param factor Node
     */
    private void factor(Node factor) throws CompilerException, FatalCompilerException {
        Node first = factor.getFirstChild();
        switch (first.getValue()) {
            case "id":
                // We've got another variable inside this variable
                subVariables.add(new VariableReference(first, symbolTable));

                Symbol s;
                VariableDecl tmp;
                // Make sure all subvariables are initialized and valide
                for(VariableReference vrS: subVariables) {
                    s = symbolTable.validate(vrS);
                    if(s == null) throw new UndeclardException(first.getPosition(), vrS);
                    tmp = (VariableDecl) s.getDecl();

                    if(vrS.getAttribute() != null) {
                        tmp = tmp.getAttribute(vrS.getAttribute());
                    }
                    if(tmp == null) {
                        // method, no initializing needed
                    } else {
                        if (!tmp.isInitialized()) throw new UninitializedException(first.getPosition(), tmp);
                    }
                }
                break;
            case "num":
                Node num = first;
                String val = first.getFirstLeafType();
                if(val.equals(Token.INTEGER.toString())) {
                    Node sign = num.getLeftSibling();
                    code.writeNum(num.getFirstLeafValue());
                    if(sign.getValue().equals("addOp")) {
                        // Make sure negative numbers are negative
                        code.writeSign(sign.getFirstLeafType());
                    }
                }
                break;
            case "ORB":
                arithExpr(first.getRightSibling());
                break;
            case "not":
                factor(first.getRightSibling());
                break;
            case "sign":
                factor(first.getRightSibling());
                break;
        }

    }

    /**
     *
     * @param F2 Node
     */
    private void F2(Node F2) throws CompilerException, FatalCompilerException {
        if(F2.getFirstLeafType().equals(Grammar.EPSILON)) return;
        else {
            Node factor = F2.getFirstChild().getRightSibling();
            Node first = factor.getFirstChild();
            if(first.getValue().equals("id")) {
                // We have an attribute
                attribute = new VariableReference(first, symbolTable);
            } else {
                factor(factor);
            }
        }

    }


    /**
     *
     * @param arithExprRight Node
     */
    private void arithExprRight(Node arithExprRight) throws CompilerException, FatalCompilerException {
        if(arithExprRight.getFirstLeafType().equals(Grammar.EPSILON)) return;
        else {
            Node addOp = arithExprRight.getFirstChild();
            Node term = arithExprRight.getFirstChild().getRightSibling();
            Node arithExprRight2 = term.getRightSibling();
            term(term);
            arithExprRight(arithExprRight2);

            // Write assembly
            if(addOp.getFirstLeafType().equals(Token.SUBTRACTION.toString())) {
                code.writeSub();
            } else if(addOp.getFirstLeafType().equals(Token.ADDITION.toString())) {
                code.writeAdd();
            }
        }

    }


    /**
     *
     * @param termRight Node
     */
    private void termRight(Node termRight) throws CompilerException, FatalCompilerException {
        if(termRight.getFirstLeafType().equals(Grammar.EPSILON)) return;

        Node factor = termRight.getFirstChild().getRightSibling();
        Node termRight2 = factor.getRightSibling();
        factor(factor);
        termRight(termRight2);

        Node multOp = termRight.getFirstChild();

        // Write assembly
        if(multOp.getFirstLeafType().equals(Token.MULTIPLICATION.toString())) {
            code.writeMultiply();
        } else if(multOp.getFirstLeafType().equals(Token.DIVISION.toString())) {
            code.writeDivide();
        }
    }

    /**
     *
     * @param F3 Node
     */
    private void F3(Node F3) throws CompilerException, FatalCompilerException {
        Node aParams = F3.getFirstChild().getRightSibling();
        aParams(aParams);
    }

    /**
     *
     * @param aParams Node
     */
    private void aParams(Node aParams) throws CompilerException, FatalCompilerException {
        if(aParams.getFirstLeafType().equals(Grammar.EPSILON)) return;
        Node expr = aParams.getFirstChild();
        Node aParamsTailR = expr.getRightSibling();
        expr(expr);
        aParamsTailR(aParamsTailR);
    }

    /**
     *
     * @param expr Node
     */
    private void expr(Node expr) throws CompilerException, FatalCompilerException {
        Expression expression = new Expression(symbolTable);
        expression.evaluate(expr);
        // Any variables encountered in the expression are treated as parameters
        // Expression handles if a variable should or should not be a parameter
        params.addAll(expression.getReferencedVariables());
    }

    /**
     *
     * @param aParamsTailR Node
     */
    private void aParamsTailR(Node aParamsTailR) throws CompilerException, FatalCompilerException {
        if(aParamsTailR.getFirstLeafType().equals(Grammar.EPSILON)) return;
        Node aParamsTail = aParamsTailR.getFirstChild();
        Node aParamsTailR2 = aParamsTail.getRightSibling();
        aParamsTail(aParamsTail);
        aParamsTailR(aParamsTailR2);
    }

    /**
     *
     * @param aParamsTail Node
     */
    private void aParamsTail(Node aParamsTail) throws CompilerException, FatalCompilerException {
        Node expr = aParamsTail.getFirstChild().getRightSibling();
        Expression expression = new Expression(symbolTable);
        expression.evaluate(expr);
        params.addAll(expression.getReferencedVariables());
    }


    public String toString() {
        String rtn = "";
        rtn += name;
        rtn += "\tSize: "+ size;
        if(attribute != null) rtn += "\tAttributes: " + attribute;
        rtn += "\tSub Variables: " + subVariables.toString();
        return rtn;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public VariableReference getAttribute() {
        return attribute;
    }

    public ArrayList<VariableReference> getParams() {
        return params;
    }
}
