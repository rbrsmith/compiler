package SemanticEvaluation;

import CodeGeneration.CodeGenerator;
import CodeGeneration.CompilerException;
import LexicalAnalyzer.DFA.Token;
import SemanticAnalyzer.*;

import java.util.ArrayList;

public class SemanticEvaluation {


    // Triggers used in this class
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

    /**
     * Write assembly for program start
     *
     * @param current Node
     * @param symbolTable SymbolTable
     * @param errors List of running errors
     */
    private void startProgram(Node current, SymbolTable symbolTable, ArrayList<Exception> errors) {
        if (!current.isLeaf() && current.getValue().equals(Analyzer.PROG_ACTION)) {
            // Write the start of the program lable in the assembly
            code.writeStartProgram();
        }
    }


    /**
     * Write assembly for conditional statements
     *
     * @param current Node
     * @param symbolTable SymbolTable
     * @param errors List of running errors
     */
    private void conditional(Node current, SymbolTable symbolTable, ArrayList<Exception> errors) {

        if (!current.isLeaf() && current.getValue().equals(ELSE)) {
            code.writeElse();
        }
        if (!current.isLeaf() && current.getValue().equals(ENDIF)) {
            code.writeEndIf();
        }


    }



    /**
     * Semantic Evaluation on Gets
     * // TODO Assembly code for get
     *
     * @param current Node
     * @param symbolTable SymbolTable
     * @param errors List of running errors
     */
    private void variableGetReference(Node current, SymbolTable symbolTable, ArrayList<Exception> errors) {
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
            } catch(CompilerException e) {
                errors.add(e);
            } catch (FatalCompilerException e) {
                e.printStackTrace();
                System.exit(-1);
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

            // We are not at an assignment...move on
            if(va == null) return;

            // Make sure variable assignment exists
            Symbol symbol = symbolTable.validate(va);
            if(symbol == null) throw new UndeclardException(current.getPosition(), va);
            else {
                // We can only have variables here
                if(!(symbol.getDecl() instanceof VariableDecl)) throw new AlreadyDeclaredException(current.getPosition(), symbol.getDecl().getName());

                // Get Left Hand Side
                VariableDecl LHSVar = (VariableDecl) symbol.getDecl();

                // Reference the appropriate variable foo vs foo.bar
                if(va.getAttribute() != null) {
                    LHSVar = LHSVar.getAttribute(va.getAttribute());
                }

                // Initialize variable
                LHSVar.initialize();

                // Now make sure return types match otherwise throw an exception
                Node expr = current.getRightSibling();
                // Get the assignment type: a = {expr}
                String exprType = new Expression(symbolTable).evaluate(expr);

                // Get the offset for assembly (this is the number the value is in the class object)
                // ex class foo {int a; int b} - b is offset of 2
                // If null, we are not in a class attribute
                Integer attributeOffset = symbol.getOffset(LHSVar);

                // Write assembly
                code.write(symbol.getAddress(), va, attributeOffset);

                // Make sure the types match
                if (!LHSVar.getType().toLowerCase().equals(exprType.toLowerCase())) {
                    throw new InvalidTypesException(expr.getPosition(), exprType, LHSVar.getType(), symbol.getDecl().getName());
                }
            }
        } catch(CompilerException e){
            errors.add(e);
        } catch (FatalCompilerException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     *
     * @param current Node
     * @param symbolTable SymbolTable
     * @param errors List of exception
     */
    private void expressionUse(Node current, SymbolTable symbolTable, ArrayList<Exception> errors) {
        if(!current.isLeaf() && current.getValue().equals(EXPR_REF)) {
            try {
                // Evaluate the expression - this pushes all variables to the code stack
                new Expression(symbolTable).evaluate(current.getLeftSibling());

                // Get action used before expression
                Node action = current.getLeftSibling().getLeftSibling().getLeftSibling();

                // Special actions
                if(action.getValue().equals("put")) {
                    code.writePut();
                } else if(action.getValue().equals("if")){
                    code.writeIf();
                }
            } catch(CompilerException e) {
                errors.add(e);
            } catch (FatalCompilerException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }

        // For loop
        if(!current.isLeaf() && current.getValue().equals(FOR_REL_EXPR)) {
            try {
                // Write for label
                code.writeStartFor();

                // Evaluate expressions in for loop
                Node relExpr = current.getLeftSibling();
                Node arithExprLHS = relExpr.getFirstChild();
                Node relOp = arithExprLHS.getRightSibling();
                Node arithExprRHS = relOp.getRightSibling();

                // Make sure types match
                String LHSType = new Expression(symbolTable).arithExpr(arithExprLHS);
                String RHSType = new Expression(symbolTable).arithExpr(arithExprRHS);

                if (!LHSType.toLowerCase().equals(RHSType.toLowerCase())) {
                    throw new InvalidTypesException(arithExprLHS.getPosition(), LHSType, RHSType);
                }

                // Write the relative operation
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

                // Write the branching statement
                code.writeForBranch();

            } catch(CompilerException e) {
                errors.add(e);
            } catch (FatalCompilerException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }

        // Write label for FOR end of for loop
        if(!current.isLeaf() && current.getValue().equals(FOR_END)) {
            code.writeEndFor();
        }
        // Write label for FOR start block
        if(!current.isLeaf() && current.getValue().equals(FOR_BLOCK)) {
            code.writeStartForBlock();
        }

        // Write label for FOR assignment start
        if(!current.isLeaf() && current.getValue().equals(FOR_ASSIG)) {
            code.writeForAssig();
        }

        // Write label for FOR end assignment
        if(!current.isLeaf() && current.getValue().equals(END_ASSIG)) {
            code.writeEndForAssig();
        }
    }

    /**
     *
     * @param current Node
     * @param symbolTable SymbolTable
     * @param errors List of Exceptions
     */
    private void returnUse(Node current, SymbolTable symbolTable, ArrayList<Exception> errors) {
        if(!current.isLeaf() && current.getValue().equals(RETURN_REF)) {
            try {
                // Get return type by evaluating return expression
                String returnType = new Expression(symbolTable).evaluate(current.getLeftSibling());
                Declaration decl = symbolTable.getDecl();

                // We have to be in a function to have a return
                if(decl instanceof FunctionDecl) {
                    // Get function
                    FunctionDecl f = (FunctionDecl) decl;
                    // Match types
                    if(!f.getType().toLowerCase().equals(returnType.toLowerCase())) {
                        throw new InvalidTypesException(current.getPosition(), returnType,
                                f.getType(), f.getName());
                    }

                    // Write end of function code
                    String address = "";
                    if(symbolTable.getParent().getDecl() instanceof ClassDecl) {
                        address += code.className + symbolTable.getParent().getName();
                        address += code.functionName + f.getName();
                    } else {
                        address += code.functionName + f.getName();
                    }

                    code.writeReturn(address);
                }
            } catch(CompilerException e) {
                errors.add(e);
            } catch (FatalCompilerException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }
}
