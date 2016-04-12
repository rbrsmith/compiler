package SemanticEvaluation;

import CodeGeneration.CodeGenerator;
import LexicalAnalyzer.DFA.Token;
import SemanticAnalyzer.*;
import SyntacticAnalyzer.Grammar;

import java.util.ArrayList;

public class VariableReference {



    // ex a = 5;
    private String name;
    // ex a[2] = 5;
    // We dont know if a[3] is out of bounds... but we can tell if a[2][3] is wrong dimension
    private int size;
    // ex a.b = 5;
    private VariableReference attribute;
    private ArrayList<VariableReference> subVariables;

    private ArrayList<VariableReference> params;

    private SymbolTable symbolTable;

    private boolean inParam;

    private CodeGenerator code;

    public VariableReference(Node id, SymbolTable symbolTable) throws Exception {
        this.name = id.getFirstLeafValue();
        this.size = 0;
        this.attribute = null;
        this.subVariables = new ArrayList<>();
        this.params = new ArrayList<>();
        this.symbolTable = symbolTable;
        this.inParam = false;
        this.code = CodeGenerator.getInstance();
        Node F1 = id.getRightSibling();
        Node F1First = F1.getFirstChild();
        if(F1First.getValue().equals("indiceR")) {
            indiceR(F1First);
            F2(F1First.getRightSibling());
        } else if(F1First.getValue().equals("F3")) {
            F3(F1First);
        }
//        factor -> id F1
//        F1 -> indiceR F2
//        F1 -> F3
//        F2 -> DOT factor
//        F2 -> EPSILON
//        F3 -> ORB aParams CRB

    }



    public VariableReference(SymbolTable symbolTable) {
        this.name = null;
        this.size = 0;
        this.attribute = null;
        this.subVariables = new ArrayList<>();
        this.params = new ArrayList<>();
        this.symbolTable = symbolTable;
        this.code = CodeGenerator.getInstance();
    }



    public void indiceR(Node indiceR) throws Exception {
//
//        indiceR -> indice indiceR
//        indiceR -> EPSILON
        if(indiceR.getFirstLeafType().equals(Grammar.EPSILON)) return;
        else {
            indice(indiceR.getFirstChild());
            indiceR(indiceR.getFirstChild().getRightSibling());
        }

    }

    private void indice(Node indice) throws Exception {
        //  OSB arithExpr CSB
        this.size += 1;
        Node arithExpr = indice.getFirstChild().getRightSibling();
        arithExpr(arithExpr);
    }

    private void arithExpr(Node arithExpr) throws Exception {
        //term arithExprRight
        term(arithExpr.getFirstChild());
        arithExprRight(arithExpr.getFirstChild().getRightSibling());
    }

    private void term(Node term) throws Exception {
        //factor termRight
        factor(term.getFirstChild());
        termRight(term.getFirstChild().getRightSibling());
    }


    private void factor(Node factor) throws Exception {
//        factor -> id F1
//        factor -> num
//        factor -> ORB arithExpr CRB
//        factor -> not factor
//        factor -> sign factor

        Node first = factor.getFirstChild();
        switch (first.getValue()) {
            case "id":
                subVariables.add(new VariableReference(first, symbolTable));

                Symbol s;
                VariableDecl tmp;
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
                if(val.equals("INTEGER")) {
                    Node sign = num.getLeftSibling();
                    code.writeNum(num.getFirstLeafValue());

                    if(sign.getValue().equals("addOp")) {
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

    private void F2(Node F2) throws Exception {
//        F2 -> DOT factor
//        F2 -> EPSILON
        if(F2.getFirstLeafType().equals(Grammar.EPSILON)) return;
        else {
            // Dot - we have an attribute
            Node factor = F2.getFirstChild().getRightSibling();
            Node first = factor.getFirstChild();
            if(first.getValue().equals("id")) {
                // Attribute
                attribute = new VariableReference(first, symbolTable);
            } else {
                factor(factor);
            }
        }

    }


    private void arithExprRight(Node arithExprRight) throws Exception {
//
//        arithExprRight -> EPSILON
//        arithExprRight -> addOp term arithExprRight
        if(arithExprRight.getFirstLeafType().equals(Grammar.EPSILON)) return;
        else {
            Node addOp = arithExprRight.getFirstChild();
            Node term = arithExprRight.getFirstChild().getRightSibling();
            Node arithExprRight2 = term.getRightSibling();
            term(term);
            arithExprRight(arithExprRight2);


            if(addOp.getFirstLeafType().equals(Token.SUBTRACTION.toString())) {
                code.writeSub();
            } else if(addOp.getFirstLeafType().equals(Token.ADDITION.toString())) {
                code.writeAdd();
            }
        }

    }


    private void termRight(Node termRight) throws Exception {
//
//        termRight -> EPSILON
//        termRight -> multOp factor termRight
        if(termRight.getFirstLeafType().equals(Grammar.EPSILON)) return;

        Node factor = termRight.getFirstChild().getRightSibling();
        Node termRight2 = factor.getRightSibling();
        factor(factor);
        termRight(termRight2);

        Node multOp = termRight.getFirstChild();

        if(multOp.getFirstLeafType().equals(Token.MULTIPLICATION.toString())) {
            code.writeMultiply();
        } else if(multOp.getFirstLeafType().equals(Token.DIVISION.toString())) {
            code.writeDivide();
        }

    }

    private void F3(Node F3) throws Exception {
        //ORB aParams CRB
        Node aParams = F3.getFirstChild().getRightSibling();
        aParams(aParams);

    }

    private void aParams(Node aParams) throws Exception {
//        aParams -> expr aParamsTailR
//        aParams -> EPSILON
        if(aParams.getFirstLeafType().equals(Grammar.EPSILON)) return;
        Node expr = aParams.getFirstChild();
        Node aParamsTailR = expr.getRightSibling();
        expr(expr);
        aParamsTailR(aParamsTailR);
    }

    private void expr(Node expr) throws Exception {
        Expression expression = new Expression(symbolTable);
        // Makes sure verything initialized fine and dandy
        expression.evaluate(expr);
        params.addAll(expression.getReferencedVariables());
    }

    private void aParamsTailR(Node aParamsTailR) throws Exception {
//        aParamsTailR -> aParamsTail aParamsTailR
//        aParamsTailR -> EPSILON
        if(aParamsTailR.getFirstLeafType().equals(Grammar.EPSILON)) return;
        Node aParamsTail = aParamsTailR.getFirstChild();
        Node aParamsTailR2 = aParamsTail.getRightSibling();
        aParamsTail(aParamsTail);
        aParamsTailR(aParamsTailR2);
    }

    private void aParamsTail(Node aParamsTail) throws Exception {
        //COMMA expr
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

    public ArrayList<VariableReference> getSubVariables() {
        return subVariables;
    }

    public ArrayList<VariableReference> getParams() {
        return params;
    }
}
