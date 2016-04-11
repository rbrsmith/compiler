package SemanticEvaluation;

import CodeGeneration.CodeGenerator;
import LexicalAnalyzer.DFA.Token;
import SemanticAnalyzer.*;

import java.util.ArrayList;

public class SemanticEvaluation {


    // Rule in Grammar that is a trigger for semantic action
    public final static String VAR_REF = "SEMANTIC-12";
    public final static String EXPR_REF = "SEMANTIC-13";
    public final static String RETURN_REF = "SEMANTIC-14";

    public final static String ELSE = "SEMANTIC-15";
    public final static String ENDIF = "SEMANTIC-16";


    public final static String FOR_REL_EXPR = "SEMANTIC-17";
    public final static String FOR_END = "SEMANTIC-18";
    public final static String FOR_BLOCK = "SEMANTIC-19";

    public final static String FOR_ASSIG = "SEMANTIC-20";
    public final static String END_ASSIG = "SEMANTIC-21";

    public final static String ASSIG_ACTION_1 = "SEMANTIC-10";
    public final static String ASSIG_ACTION_2 = "SEMANTIC-11";


    private CodeGenerator code;




    public void evaluate(Node current, SymbolTable symbolTable, ArrayList<Exception> errors) {
        this.code = CodeGenerator.getInstance();
        variableGetReference(current, symbolTable, errors);
        variableAssignment(current, symbolTable, errors);
        expressionUse(current, symbolTable, errors);
        returnUse(current, symbolTable, errors);
        conditional(current, symbolTable, errors);
        startProgram(current, symbolTable, errors);
    }

    private void startProgram(Node current, SymbolTable symbolTable, ArrayList<Exception> errors) {
        if (!current.isLeaf() && current.getValue().equals(Analyzer.PROG_ACTION)) {
            code.writeStartProgram();
        }
    }


    private void conditional(Node current, SymbolTable symbolTable, ArrayList<Exception> errors) {

        if (!current.isLeaf() && current.getValue().equals(ELSE)) {
            code.writeElse();
        }
        if (!current.isLeaf() && current.getValue().equals(ENDIF)) {
            code.writeEndIf();
        }


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
                if(!(symbol.getDecl() instanceof VariableDecl)) throw new AlreadyDeclaredException(current.getPosition(), symbol.getDecl().getName());
                VariableDecl LHSVar = (VariableDecl) symbol.getDecl();

                if(va.getAttribute() != null) {
                    LHSVar = LHSVar.getAttribute(va.getAttribute());
                }

               LHSVar.initialize();

                // Now make sure return types match otherwise throw an exception
                Node expr = current.getRightSibling();
                String exprType = new Expression(symbolTable).evaluate(expr);

                Integer attributeOffset = symbol.getOffset(LHSVar);



                code.write(symbol.getAddress(), va, attributeOffset);
                if (!LHSVar.getType().equals(exprType)) {
                    throw new InvalidTypesException(expr.getPosition(), exprType, LHSVar.getType(), symbol.getDecl().getName());
                }


            }

        } catch(Exception e){
            errors.add(e);
        }

    }

    private void expressionUse(Node current, SymbolTable symbolTable, ArrayList<Exception> errors) {
        if(!current.isLeaf() && current.getValue().equals(EXPR_REF)) {
            try {
                new Expression(symbolTable).evaluate(current.getLeftSibling());
                Node action = current.getLeftSibling().getLeftSibling().getLeftSibling();
                if(action.getValue().equals("put")) {
                    code.writePut();
                } else if(action.getValue().equals("if")){
                    code.writeIf();
                }


            } catch(Exception e) {
                errors.add(e);
            }
        }
        if(!current.isLeaf() && current.getValue().equals(FOR_REL_EXPR)) {
            try {

                code.writeStartFor();


                Node relExpr = current.getLeftSibling();
                Node arithExprLHS = relExpr.getFirstChild();
                Node relOp = arithExprLHS.getRightSibling();
                Node arithExprRHS = relOp.getRightSibling();

                String LHSType = new Expression(symbolTable).arithExpr(arithExprLHS);
                String RHSType = new Expression(symbolTable).arithExpr(arithExprRHS);

                if (!LHSType.equals(RHSType)) {
                    throw new InvalidTypesException(arithExprLHS.getPosition(), LHSType, RHSType);
                }

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

                code.writeForBranch();

            } catch(Exception e) {
                errors.add(e);
            }


        }
        if(!current.isLeaf() && current.getValue().equals(FOR_END)) {
            code.writeEndFor();

        }
        if(!current.isLeaf() && current.getValue().equals(FOR_BLOCK)) {
            code.writeStartForBlock();
        }

        if(!current.isLeaf() && current.getValue().equals(FOR_ASSIG)) {
            code.writeForAssig();
        }

        if(!current.isLeaf() && current.getValue().equals(END_ASSIG)) {
            code.writeEndForAssig();
        }
    }

    private void returnUse(Node current, SymbolTable symbolTable, ArrayList<Exception> errors) {
        if(!current.isLeaf() && current.getValue().equals(RETURN_REF)) {
            try {
                String returnType = new Expression(symbolTable).evaluate(current.getLeftSibling());
                Declaration decl = symbolTable.getDecl();
                if(decl instanceof FunctionDecl) {
                    FunctionDecl tmp = (FunctionDecl) decl;
                    if(!tmp.getType().equals(returnType)) {
                        throw new InvalidTypesException(current.getPosition(), returnType,
                                tmp.getType(), tmp.getName());
                    }
                    String address = "";
                    if(symbolTable.getParent().getDecl() instanceof ClassDecl) {
                        address += code.className + symbolTable.getParent().getName();
                        address += code.functionName + tmp.getName();
                    } else {
                        address += code.functionName + tmp.getName();
                    }

                    code.writeReturn(address);
                }
            } catch(Exception e) {
                errors.add(e);
            }
        }
    }



}
