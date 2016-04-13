package SemanticEvaluation;

import CodeGeneration.CodeGenerator;
import CodeGeneration.CompilerException;
import LexicalAnalyzer.DFA.Position;
import LexicalAnalyzer.DFA.Reserved;
import LexicalAnalyzer.DFA.Token;
import SemanticAnalyzer.*;
import SyntacticAnalyzer.Grammar;

import java.util.ArrayList;

/**
 * Class representing expr rule in Grammar
 */
public class Expression {

    private SymbolTable symbolTable;
    private ArrayList<VariableReference> referencedVariables;
    private CodeGenerator code;

    public Expression(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        this.referencedVariables = new ArrayList<>();
        this.code = CodeGenerator.getInstance();
    }


    /**
     * Driver
     * @param expr Node representing root of expression trees
     * @return String representing the type of the expression
     */
    public String evaluate(Node expr) throws CompilerException, FatalCompilerException {
        String ar = arithExpr(expr.getFirstChild());
        String e = E1(expr.getFirstChild().getRightSibling());
        if(e.equals(Grammar.EPSILON)) return ar;
        if(!e.toLowerCase().equals(ar.toLowerCase())) throw new BadOpException(expr.getPosition(), ar, e, "add");
        else return e;
    }

    /**
     *
     * @param arithExpr Node
     * @return String type
     */
    public String arithExpr(Node arithExpr) throws CompilerException, FatalCompilerException {
        String t = term(arithExpr.getFirstChild());
        String ar = arithExprRight(arithExpr.getFirstChild().getRightSibling());
        if(ar.equals(Grammar.EPSILON)) return t;
        if(!t.toLowerCase().equals(ar.toLowerCase())) throw new BadOpException(arithExpr.getPosition(), t, ar, "add");
        else return t;
    }

    /**
     *
     * @param term Node
     * @return String term type
     */
    private String term(Node term) throws CompilerException, FatalCompilerException {
        String f = factor(term.getFirstChild());
        String tr = termRight(term.getFirstChild().getRightSibling());

        if(tr.equals(Grammar.EPSILON)) return f;
        if(!tr.toLowerCase().equals(f.toLowerCase())) throw new BadOpException(term.getPosition(), f, tr, "multiply");
        return f;

    }

    /**
     *
     * Bulk of semantic issues lies here
     *
     * @param factor Node
     * @return String type of factor
     */
    private String factor(Node factor) throws CompilerException, FatalCompilerException {
        Node first = factor.getFirstChild();
        switch (first.getValue()) {
            case "id":
                Node id = first;
                return ID(id);
            case "num":
                Node num = first;
                return num(num);
            case "ORB":
                // Do nothing here
                Node orb = first;
                return arithExpr(first.getRightSibling());
            case "not":
                // Do nothing here
                // TODO missing assembly code for 'not'
                Node not = first;
                return factor(not.getRightSibling());
            case "sign":
                Node sign = first;
                String factorType = factor(sign.getRightSibling());
                code.writeSign(sign.getFirstLeafType());
                return factorType;
            default:
                throw new FatalCompilerException("A factor was found during the parse which recursively calls itself!");
        }
    }

    /**
     * Evalute a number node
     * @param num Node
     * @return String type of number (int | float)
     */
    private String num(Node num) throws FatalCompilerException, CompilerException {

        String val = num.getFirstLeafType();
        Node sign = num.getLeftSibling();

        // Add this to referenced variables aka potential calling functions params
        VariableReference vr2 = new VariableReference(symbolTable);

        if(!sign.getValue().equals("addOp") && !sign.getValue().equals("relOp")) {
            referencedVariables.add(vr2);
        }

        if(val.equals(Token.FLOAT.toString())) {
            vr2.setType(Reserved.FLOAT.toString());
            // TODO float assembly code
            return Reserved.FLOAT.toString();
        }
        if(val.equals(Token.INTEGER.toString())) {
            vr2.setType(Reserved.INT.toString());

            // Do the assembly code
            // Subtraction is really addition with a negative

            code.writeNum(num.getFirstLeafValue());
            if (sign.getValue().equals("addOp")) {
                code.writeSign(sign.getFirstLeafType());
            }
            return Reserved.INT.toString();
        }
        throw new FatalCompilerException("Found anumber that was not an int or float...");
    }

    /**
     *
     * @param termRight Node
     * @return String termRight type
     */
    private String termRight(Node termRight) throws CompilerException, FatalCompilerException {
        if(termRight.getFirstLeafType().equals(Grammar.EPSILON)) return Grammar.EPSILON;
        else {
            Node multOp = termRight.getFirstChild();
            Node factor = multOp.getRightSibling();
            Node termRight2 = factor.getRightSibling();
            multOp(multOp);
            String f = factor(factor);
            String tr = termRight(termRight2);

            // Perform assembly code operation
            if(multOp.getFirstLeafType().equals(Token.MULTIPLICATION.toString())) {
                code.writeMultiply();
            } else if(multOp.getFirstLeafType().equals(Token.DIVISION.toString())) {
                code.writeDivide();
            }

            if(tr.equals(Grammar.EPSILON)) return f;
            if(!tr.toLowerCase().equals(f.toLowerCase())) throw new BadOpException(termRight.getPosition(), f, tr, "multiply");
            else return f;


        }
    }

    /**
     *
     * @param multOp Node
     * @return String empty as multOp has no type
     */
    private String multOp(Node multOp) {
        return "";
    }

    /**
     *
     * @param arithExprRight Node
     * @return String of arithExprRight
     */
    private String arithExprRight(Node arithExprRight) throws CompilerException, FatalCompilerException {
        if(arithExprRight.getFirstLeafType().equals(Grammar.EPSILON)) return Grammar.EPSILON;
        else {
            Node addOp = arithExprRight.getFirstChild();
            Node term = addOp.getRightSibling();
            Node artihExprRight2 = term.getRightSibling();
            addOp(addOp);
            String t = term(term);
            String arith = arithExprRight(artihExprRight2);

            // Perform assemnbly code
            if(addOp.getFirstLeafType().equals(Token.ADDITION.toString())) {
                code.writeAdd();
            } else if(addOp.getFirstLeafType().equals(Token.SUBTRACTION.toString())) {
                code.writeSub();
            }

            if(arith.equals(Grammar.EPSILON)) return t;
            if(!t.toLowerCase().equals(arith.toLowerCase())) throw new BadOpException(arithExprRight.getPosition(), t, arith, "add");
            else return t;

        }
    }

    /**
     *
     * @param addOp Node
     * @return Empty String as addOp has no type
     */
    private String addOp(Node addOp) {
        // Add op is just add
        return "";
    }


    /**
     *
     * @param E1 Node
     * @return String E1 type
     */
    private String E1(Node E1) throws CompilerException, FatalCompilerException {
        if(E1.getFirstLeafType().equals(Grammar.EPSILON)) return Grammar.EPSILON;
        Node relOp = E1.getFirstChild();
        relOp(relOp);
        String ae = arithExpr(E1.getFirstChild().getRightSibling());

        // Write assembly code
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

    /**
     *
     * @param relOp Node
     * @return Empty String as relOp has no type
     */
    private String relOp(Node relOp) {
        return "";
    }


    /**
     *
     * @param id Node
     * @return String id type
     */
    private String ID(Node id) throws CompilerException, FatalCompilerException {
        // See if there is a +/- sign before the id
        Node sign = id.getLeftSibling();

        // We are evaluating an ID in an expression - we add this id to referenced variables
        VariableReference vr = new VariableReference(id, symbolTable);

        if (!sign.getValue().equals("addOp") && !sign.getValue().equals("relOp")) {
            referencedVariables.add(vr);
        }

        // Determine if this id exists
        Symbol s = symbolTable.validate(vr);
        if(s == null) throw new UndeclardException(id.getPosition(), vr);

        // We either have a variable or a function now

        if(s.getDecl() instanceof VariableDecl) {
            // VARIABLE
            return IDVar(sign, s, vr);

        } else if (s.getDecl() instanceof ClassDecl) {
            // A class? we shouldn't have a class - this must have already been declared
                throw new AlreadyDeclaredException(id.getPosition(), s.getDecl().getName());
         } else {
            // FUNCTION
            FunctionDecl method = (FunctionDecl) s.getDecl();
            // Get function type
            String type = IDFunc(method, vr, id.getPosition());

            // Write assembly code
            String address = "";
            if (symbolTable.getParent().getDecl() instanceof ClassDecl) {
                // We have a method call
                SymbolTable classTable = symbolTable.getParent();
                address += code.className + classTable.getName() + code.functionName + method.getName();
                code.writeFunctionCall(method, address, true);
            } else {
                // We have a function call
                address = code.functionName + method.getName();
                code.writeFunctionCall(method, address, false);
            }

            // Write the return code
            code.writeGetReturn(address);

            // Finally return the type
            return type;
        }
    }


    /**
     *
     * @param sign Node indicating +/- sign of id we are parising
     * @param symbol Symbol holding variable and address
     * @param vr Variable Reference we are referencing
     * @return String type of id we are parsing
     * @throws InvalidTypesException
     * @throws InvalidFunctionParamsException
     * @throws UndeclardException
     * @throws UninitializedException
     */
    private String IDVar(Node sign, Symbol symbol, VariableReference vr) throws CompilerException {
        VariableDecl id = (VariableDecl) symbol.getDecl();

        // See if we have foo.bar or just bar
        if(vr.getAttribute() != null) {
            // We have foo.bar
            VariableDecl attribute = id.getAttribute(vr.getAttribute());
            if(attribute == null) {
                // variable Referenced has the attribute but the id doesn't, this means we have a method
                // We do a function call
                // foo.bar();
                SymbolTable classTable = symbolTable.getClassSymbolTable(id.getType());
                FunctionDecl method = (FunctionDecl) classTable.validate(vr.getAttribute(), false).getDecl();

                String type = IDFunc(method, vr, sign.getPosition());
                // Write assembly
                String address = code.className + classTable.getName() + code.functionName + method.getName();
                code.writeFunctionCall(method, address,  true);
                code.writeGetReturn(address);
                // Return the function type
                return type;
            } else {
                // Attribute
                // foo.bar
                if (!attribute.isInitialized()) throw new UninitializedException(sign.getPosition(), id);
                // We need to find if this is the first, second, third...etc attribute in the class

                int offset = symbol.getOffset(attribute);

                // Write assembly
                code.loadVar(symbol.getAddress(), offset);
                if(sign.getValue().equals("addOp")) {
                    code.writeSign(sign.getFirstLeafType());
                }

                // Return attribute type
                return attribute.getType();
            }
        } else {
            // Simple Var
            // bar
            if (!id.isInitialized()) throw new UninitializedException(sign.getPosition(), id);

            // Write assembly
            code.loadVar(symbol.getAddress());
            if(sign.getValue().equals("addOp")) {
                code.writeSign(sign.getFirstLeafType());
            }
            return id.getType();
        }
    }


    /**
     *
     * @param method FunctionDecl method being called
     * @param caller VariableReferece making the call
     * @param pos Position object
     * @return String type of method
     */
    private String IDFunc(FunctionDecl method, VariableReference caller, Position pos) throws UndeclardException, InvalidFunctionParamsException, InvalidTypesException {
        // Make sure we have the correct caller
        // foo.bar not just foo
        if (caller.getAttribute() != null) {
            caller = caller.getAttribute();
        }
        // Validate
        if (caller.getParams() == null) throw new UndeclardException(pos, caller);
        if (method.getParams().size() != caller.getParams().size()) {
            throw new InvalidFunctionParamsException(pos);
        }

        // Validate parameters
        for (int i = 0; i < method.getParams().size(); i++) {

            VariableReference callerParam = caller.getParams().get(i);

            // The calling function parameter does not have a name
            // It is therefor a number not an id
            if (callerParam.getName() == null) {
                // Get types
                String methodType = method.getParams().get(i).getType();
                String callerType = callerParam.getType();
                // complete number validation
                if (!callerType.toLowerCase().equals(methodType.toLowerCase())) {
                    throw new InvalidTypesException(pos, callerType, methodType);
                }
                // NEXT
                continue;
            }
            // We have an id ex foo.bar(a);
            // Get the symbol
            Symbol callerSymbol = symbolTable.validate(callerParam);

            if (callerSymbol == null) throw new UndeclardException(pos, callerParam);

            // Compare types
            String callerType = "";
            String callerName = "";
            // Parameter can be an id or a function
            // foo.bar(a) vs foo.bar(a())
            if (callerSymbol.getDecl() instanceof VariableDecl) {
                // Variable
                VariableDecl callerParamDecl = (VariableDecl) callerSymbol.getDecl();

                // Make sure we are at the lowest form
                // foo.bar(a.b) -> we need b not a
                if (callerParam.getAttribute() != null) {
                    callerParamDecl = callerParamDecl.getAttribute(callerParam.getAttribute());
                }
                // Set Types
                callerType = callerParamDecl.getType();
                callerName = callerParamDecl.getName();

            } else if (callerSymbol.getDecl() instanceof FunctionDecl) {
                // Set Types
                FunctionDecl callerParamDecl = (FunctionDecl) callerSymbol.getDecl();
                callerType = callerParamDecl.getType();
                callerName = callerParamDecl.getName();
            }

            // Finally validate
            if (callerType.toLowerCase() != method.getParams().get(i).getType().toLowerCase()) {
                throw new InvalidTypesException(pos, callerType,
                        method.getParams().get(i).getType(), callerName);
            }
        }

        // Return type
        return method.getType();
    }


    /**
     *
     * @return List of VariableReferences made while traversing the expression tree
     * Ex:  ... = (5 + a + b.c()) -> [a, b.c()]
     */
    public ArrayList<VariableReference> getReferencedVariables() {
        return referencedVariables;
    }
}
