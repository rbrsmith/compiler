package SemanticEvaluation;

import CodeGeneration.CodeGenerator;
import LexicalAnalyzer.DFA.Reserved;
import LexicalAnalyzer.DFA.Token;
import SemanticAnalyzer.*;
import SyntacticAnalyzer.Grammar;

import java.util.ArrayList;

public class Expression {

    private SymbolTable symbolTable;
    private ArrayList<VariableReference> referencedVariables;
    private CodeGenerator code;

    public Expression(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        this.referencedVariables = new ArrayList<>();
        this.code = CodeGenerator.getInstance();
    }


    public String evaluate(Node expr) throws Exception {
        String ar = arithExpr(expr.getFirstChild());
        String e = E1(expr.getFirstChild().getRightSibling());
        if(e.equals(Grammar.EPSILON)) return ar;
        if(!e.equals(ar)) throw new BadOpException(expr.getPosition(), ar, e, "add");
        else return e;
    }

    public String arithExpr(Node arithExpr) throws Exception {
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

                Node possibleSign = id.getLeftSibling();
                VariableReference vr = new VariableReference(id, symbolTable);
                if(possibleSign.getValue().equals("addOp") || possibleSign.getValue().equals("relOp")) {

                } else {
                    referencedVariables.add(vr);
                }
                Symbol s = symbolTable.validate(vr);
                if(s == null) throw new UndeclardException(id.getPosition(), vr);
                if(s.getDecl() instanceof VariableDecl) {
                    VariableDecl tmp = (VariableDecl) s.getDecl();

                    if(vr.getAttribute() != null) {
                        VariableDecl tmp2 = tmp.getAttribute(vr.getAttribute());
                        if(tmp2 == null) {
                            // Method
                            SymbolTable classTable = symbolTable.getClassSymbolTable(tmp.getType());
                            FunctionDecl method = (FunctionDecl) classTable.validate(vr.getAttribute(), false).getDecl();




                            VariableReference caller = vr;
                            if(vr.getAttribute() != null){
                                caller = vr.getAttribute();
                            }
                            if(caller.getParams() == null) throw new UndeclardException(factor.getPosition(), caller);
                            if(method.getParams().size() != caller.getParams().size()) throw new InvalidFunctionParamsException(factor.getPosition());

                            for(int i=0;i<method.getParams().size(); i++) {
                                VariableReference callerParam = caller.getParams().get(i);

                                if(callerParam.getName() == null) {
                                    // Not an id, but a number
                                    continue;
                                }
                                Symbol callerSymbol = symbolTable.validate(callerParam);
                                if(callerSymbol == null) {
                                    throw new UndeclardException(factor.getPosition(), callerParam);
                                }

                                String callerType = "";
                                String callerName = "";
                                if(callerSymbol.getDecl() instanceof VariableDecl) {
                                    VariableDecl callerParamDecl = (VariableDecl) callerSymbol.getDecl();

                                    if (callerParam.getAttribute() != null) {
                                        callerParamDecl = callerParamDecl.getAttribute(callerParam.getAttribute());
                                    }
                                    callerType = callerParamDecl.getType();
                                    callerName = callerParamDecl.getName();
                                } else if(callerSymbol.getDecl() instanceof FunctionDecl) {

                                    FunctionDecl callerParamDecl = (FunctionDecl) callerSymbol.getDecl();
                                    callerType = callerParamDecl.getType();
                                    callerName = callerParamDecl.getName();
                                }



                                if(callerType != method.getParams().get(i).getType()) {
                                    throw new InvalidTypesException(factor.getPosition(), callerType,
                                            method.getParams().get(i).getType(), callerName);
                                }
                            }



















                            String address = code.className + classTable.getName() + code.functionName + method.getName();
                            code.writeFunctionCall(method, address,  true);


                            code.writeGetReturn(address);


                            return method.getType();
                        } else {
                            // Attribute
                            if (!tmp2.isInitialized()) throw new UninitializedException(id.getPosition(), tmp);
                            int offset = s.getOffset(tmp2);
                            code.loadVar(s.getAddress(), offset);
                            if(possibleSign.getValue().equals("addOp")) {
                                code.writeSign(possibleSign.getFirstLeafType());
                            }
                            return tmp2.getType();
                        }
                    } else {
                       if (!tmp.isInitialized()) throw new UninitializedException(id.getPosition(), tmp);
                        code.loadVar(s.getAddress());
                        if(possibleSign.getValue().equals("addOp")) {
                            code.writeSign(possibleSign.getFirstLeafType());
                        }
                       return tmp.getType();
                    }
                } else {
                    if(s.getDecl() instanceof ClassDecl) {
                        throw new AlreadyDeclaredException(factor.getPosition(), s.getDecl().getName());
                    }
                    // else Function Declaration
                    FunctionDecl method = (FunctionDecl) s.getDecl();
                    VariableReference caller = vr;
                    if(vr.getAttribute() != null){
                        caller = vr.getAttribute();
                    }
                    if(caller.getParams() == null) throw new UndeclardException(factor.getPosition(), caller);
                    if(method.getParams().size() != caller.getParams().size()) {
                        throw new InvalidFunctionParamsException(factor.getPosition());
                    }

                    for(int i=0;i<method.getParams().size(); i++) {
                        VariableReference callerParam = caller.getParams().get(i);


                        if(callerParam.getName() == null) {
                            // Not an id, but a number
                            // but is the other one the same number type?
                            String methodType = method.getParams().get(i).getType();
                            String callerType = callerParam.getType();
                            if(!callerType.equals(methodType)) {
                                throw new InvalidFunctionParamsException(id.getPosition());
                            }
                            continue;
                        }
                        Symbol callerSymbol = symbolTable.validate(callerParam);
                        if(callerSymbol == null) throw new UndeclardException(factor.getPosition(), callerParam);

                        String callerType = "";
                        String callerName = "";
                        if(callerSymbol.getDecl() instanceof VariableDecl) {
                            VariableDecl callerParamDecl = (VariableDecl) callerSymbol.getDecl();

                            if (callerParam.getAttribute() != null) {
                                callerParamDecl = callerParamDecl.getAttribute(callerParam.getAttribute());
                            }
                            callerType = callerParamDecl.getType();
                            callerName = callerParamDecl.getName();

                        } else if(callerSymbol.getDecl() instanceof FunctionDecl) {
                            FunctionDecl callerParamDecl = (FunctionDecl) callerSymbol.getDecl();
                            callerType = callerParamDecl.getType();
                            callerName = callerParamDecl.getName();
                        }

                        if(callerType!= method.getParams().get(i).getType()) {
                            throw new InvalidTypesException(factor.getPosition(), callerType,
                                    method.getParams().get(i).getType(), callerName);
                        }
                    }

                    String address = "";
                    if(symbolTable.getParent().getDecl() instanceof ClassDecl) {
                        SymbolTable classTable = symbolTable.getParent();
                        address += code.className + classTable.getName() + code.functionName + method.getName();
                        code.writeFunctionCall(method, address, true);
                    } else {
                        address = code.functionName + method.getName();
                        code.writeFunctionCall(method, address, false);
                    }

                    code.writeGetReturn(address);

                    return method.getType();
                }
            case "num":
                Node num = first;
                String val  =num.getFirstLeafType();

                Node possibleSign2 = num.getLeftSibling();

                VariableReference vr2 = new VariableReference(symbolTable);
                if(possibleSign2.getValue().equals("addOp") || possibleSign2.getValue().equals("relOp")) {

                } else {
                    referencedVariables.add(vr2);
                }
                if(val.equals("FLOAT")) {
                    vr2.setType("float");
                    return "float";
                }
                if(val.equals("INTEGER")) {
                    vr2.setType("int");
                    Node sign = num.getLeftSibling();

                    code.writeNum(num.getFirstLeafValue());
                    if(sign.getValue().equals("addOp")) {
                        code.writeSign(sign.getFirstLeafType());
                    }
                    return "int";
                }
            case "ORB":
                Node orb = first;
                return arithExpr(first.getRightSibling());
            case "not":
                Node not = first;
                return factor(not.getRightSibling());
            case "sign":
                Node sign = first;
                String factorType = factor(sign.getRightSibling());

                code.writeSign(sign.getFirstLeafType());

                return factorType;
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

            if(multOp.getFirstLeafType().equals(Token.MULTIPLICATION.toString())) {
                code.writeMultiply();
            } else if(multOp.getFirstLeafType().equals(Token.DIVISION.toString())) {
                code.writeDivide();
            }
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


            if(addOp.getFirstLeafType().equals(Token.ADDITION.toString())) {
                code.writeAdd();
            } else if(addOp.getFirstLeafType().equals(Token.SUBTRACTION.toString())) {
                code.writeSub();
            }

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
        Node relOp = E1.getFirstChild();
        relOp(relOp);
        String ae = arithExpr(E1.getFirstChild().getRightSibling());

        if(relOp.getFirstLeafType().equals(Token.EQUALS.toString())) {
            code.writeEquals();
        } else if(relOp.getFirstLeafType().equals(Token.GREATER_THAN.toString())) {
            code.writeGreaterThan();
        } else if(relOp.getFirstLeafType().equals(Token.GREATER_THAN_EQUALS.toString())) {
            code.writeGreaterThanEquals();
        } else if(relOp.getFirstLeafType().equals(Token.LESS_THAN_EQUALS.toString())) {
            code.writeLessThanEquals();
        } else if(relOp.getFirstLeafType().equals(Token.LESS_THAN.toString())) {
            code.writeLessThan();
        } else if(relOp.getFirstLeafType().equals(Token.NOT_EQUALS.toString())) {
            code.writeNotEquals();
        }


        return ae;

    }

    private String relOp(Node relOp) {


        return "";
    }

    public ArrayList<VariableReference> getReferencedVariables() {
        return referencedVariables;
    }


}
