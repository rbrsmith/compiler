package SemanticEvaluation;

import LexicalAnalyzer.DFA.Reserved;
import SemanticAnalyzer.*;
import SyntacticAnalyzer.Grammar;

import java.util.ArrayList;

public class Expression {

    private SymbolTable symbolTable;

    public Expression(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public String evaluate(Node expr) throws Exception {
        String ar = arithExpr(expr.getFirstChild());
        String e = E1(expr.getFirstChild().getRightSibling());
        if(e.equals(Grammar.EPSILON)) return ar;
        if(!e.equals(ar)) throw new BadOpException(expr.getPosition(), ar, e, "add");
        else return e;
    }

    private String arithExpr(Node arithExpr) throws Exception {
        String t = term(arithExpr.getFirstChild());
        String ar = arithExprRight(arithExpr.getFirstChild().getRightSibling());
        if(ar.equals(Grammar.EPSILON)) return t;
        if(!t.equals(ar)) throw new BadOpException(arithExpr.getPosition(), t, ar, "add");
        else return t;
    }

    private String term(Node term) throws Exception {
        String f = factor(term.getFirstChild());
        String tr = termRight(term.getFirstChild().getRightSibling());

        if(tr.equals(Grammar.EPSILON)) return f;
        if(!tr.equals(f)) throw new BadOpException(term.getPosition(), f, tr, "multiply");
        return f;

    }

    // I KNOW, this is public, it should be lambdad or something
    private String factor(Node factor) throws Exception {
        Node first = factor.getFirstChild();
        switch (first.getValue()) {
            case "id":
                Node id = first;
                VariableReference vr = new VariableReference(id, symbolTable);
                Symbol s = symbolTable.validate(vr);
                if(s == null) throw new UndeclardException(id.getPosition(), vr);
                VariableDecl tmp = (VariableDecl) s.getDecl();
                if(!tmp.isInitialized()) throw new UninitializedException(id.getPosition(), tmp);

                return tmp.getType();
            case "num":
                Node num = first;
                String val  =num.getFirstLeafType();
                if(val.equals("FLOAT")) return "float";
                if(val.equals("INTEGER")) return "int";
            case "ORB":
                Node orb = first;
                return arithExpr(first.getRightSibling());
            case "not":
                Node not = first;
                return factor(not.getRightSibling());
            case "sign":
                Node sign = first;
                return factor(sign.getRightSibling());
            default:
                // Overflow loop
                return factor(factor);
        }
    }


    private String termRight(Node termRight) throws Exception {
        if(termRight.getFirstLeafType().equals(Grammar.EPSILON)) return Grammar.EPSILON;
        else {
            Node multOp = termRight.getFirstChild();
            Node factor = multOp.getRightSibling();
            Node termRight2 = factor.getRightSibling();
            multOp(multOp);
            String f = factor(factor);
            String tr = termRight(termRight2);

            if(tr.equals(Grammar.EPSILON)) return f;
            if(!tr.equals(f)) throw new BadOpException(termRight.getPosition(), f, tr, "multiply");
            else return f;


        }
    }

    private String multOp(Node multOp) {
        // Multi op is just number manipulation
        return "";
    }


    private String arithExprRight(Node arithExprRight) throws Exception {
        if(arithExprRight.getFirstLeafType().equals(Grammar.EPSILON)) return Grammar.EPSILON;
        else {
            Node addOp = arithExprRight.getFirstChild();
            Node term = addOp.getRightSibling();
            Node artihExprRight2 = term.getRightSibling();
            addOp(addOp);
            String t = term(term);
            String arith = arithExprRight(artihExprRight2);

            if(arith.equals(Grammar.EPSILON)) return t;
            if(!t.equals(arith)) throw new BadOpException(arithExprRight.getPosition(), t, arith, "add");
            else return t;

        }
    }

    private String addOp(Node addOp) {
        // Add op is just add
        return "";
    }


    private String E1(Node E1) throws Exception {
        if(E1.getFirstLeafType().equals(Grammar.EPSILON)) return Grammar.EPSILON;
        relOp(E1.getFirstChild());
        return arithExpr(E1.getFirstChild().getRightSibling());

    }

    private String relOp(Node relOp) {
        // relOp is an operator
        return "";

    }


}
