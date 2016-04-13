package CodeGeneration;

import LexicalAnalyzer.DFA.Token;
import SemanticAnalyzer.*;
import SyntacticAnalyzer.Tuple;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that does all the assembly code generation
 */
public class CodeGenerator {


    // Used to determine if code generation is on or off
    private boolean active;

    // Singleton
    private static CodeGenerator instance = new CodeGenerator();

    // Map of free / reserved registers
    private final HashMap<String, Boolean> registers = new HashMap<String,Boolean>() {{
        put("r1",true);put("r2",true);put("r3",true);put("r4",true);put("r5",true);
        put("r6",true);put("r7",true);put("r8",true);put("r9",true);put("r10",true);
        put("r11",true);put("r12",true);
    }};
    // Registers not in use by this class
    private final String r0 = "r0";
    private final String r13 = "r13";
    private final String r14 = "r14";
    private final String r15 = "r15";

    // Names used in address mapping
    public final String functionName = "FUNCTION__";
    public final String className = "CLASS__";
    public final String variableName = "VARIABLE__";
    public final String globalName = "GLOBAL__";

    // Name used in return address mapping
    private final String rtn = "__RTN";

    // List of execution code
    private ArrayList<String> execution;
    // List of data code
    private ArrayList<String> dataRes;
    private ArrayList<String> dataDW;

    // List of variables in use
    private ArrayList<String> varStack;

    // List of available temporary variables
    private ArrayList<String> tmpVars;

    // List of variables used in arrays
    private ArrayList<String> arrays;

    // List of numbers for if and for loop labels
    private ArrayList<Integer> ifNums;

    // Map of functionName -> function return variables
    private HashMap<String, String> functionMap;

    // Coutner of if / for's
    private int ifNumCount;

    // Random int
    private int randomVar;

    // Integer size
    private final int intSize = 4;

    // End program label
    private final String endProgram = "endProgram";

    // List of return varaibles
    private final ArrayList<String> returnVar;

    // Tmp var pointer, increases with every new var and function call
    int tmpVarPointer;

    private CodeGenerator(){
        execution = new ArrayList<>();
        dataRes = new ArrayList<>();
        dataDW = new ArrayList<>();
        varStack = new ArrayList<>();
        tmpVars = new ArrayList<>();
        arrays = new ArrayList<>();
        ifNums = new ArrayList<Integer>() {{ add(1); }};
        ifNumCount = 1;
        functionMap = new HashMap<>();
        randomVar = 0;
        returnVar = new ArrayList<>();
        tmpVarPointer = 0;
        active = true;
    }


    public static CodeGenerator getInstance() {
        return instance;
    }

    /**
     *
     * @param address String
     * @param va VariableAssignment
     * @param attributeOffset Integer to use as offset (o for regular variables)
     */
    public void write(String address, VariableAssig va, Integer attributeOffset) throws CodeException {
        if(varStack.size() == 0 || !active) return;

        comment("Variable Assignment " + va.toString() + " = ...");

        String reg = loadWord(pop());

        // Get array offset
        String multOffsetReg;
        if(va.getSize() == 0) {
            multOffsetReg = r0;
        } else {
            multOffsetReg = getPositionReg();
        }

        // Class and array ids
        if(attributeOffset != null){
            // Get attribute offset
            String attributeOffsetReg = getAttributeOffset(attributeOffset);

            // If we have an offset from array, we must add on the attribute array
            if(!multOffsetReg.equals(r0)) {
                // We need to multiply the offset by integer value
                String tmpReg = getRegister();
                String e = "mul\t" + tmpReg +", " + attributeOffsetReg + ", " + multOffsetReg;
                execution.add(e);
                freeRegister(multOffsetReg);
                multOffsetReg = tmpReg;
            } else {
                multOffsetReg = attributeOffsetReg;
            }
        }

        storeWord(address, reg, multOffsetReg);
        if(!multOffsetReg.equals(r0)) freeRegister(multOffsetReg);

        freeRegister(reg);
    }


    /**
     *
     * @param attributeOffset Integer amount the attribute is offset by class{var1, var2} var2 is 2
     * @return String containing register of the integer amount in bytes
     */
    private String getAttributeOffset(Integer attributeOffset) throws CodeException {
        String reg = getRegister();
        String e = "addi\t"+reg+", " +r0+","+attributeOffset.toString();
        execution.add(e);
        String reg2 = getRegister();
        e = "muli\t"+reg2+", "+reg+", 4";
        execution.add(e);
        freeRegister(reg);
        return reg2;
    }

    /**
     *
     * @param address String
     * @param f FunctionDecl
     * @param method boolean True if this is a method | False if this is a free function
     */
    public void write(String address, FunctionDecl f, boolean method) throws CodeException {
        if(!active) return;
        comment("Function");

        // We cannot use a temporary variable in a function call, incase it is already in use
        tmpVarPointer += 1;

        // Write the label
        String e = address;
        execution.add(e);

        // Write the return code
        String returnReg;
        if(!method) {
            // Free function, just get return register
            returnReg = getFunctionReg(f.getName());
        } else {
            // Method comes first, set up return register
            returnReg = getFunctionReg(f.getName());
            if(returnReg == null) {
                returnReg = getRegister();
            }
            storeFunctionReg(returnReg, f.getName());
        }

        if (returnReg != null) {
            // Save return variables
            String rv = "returnVar" + randomVar;
            randomVar += 1;
            returnVar.add(rv);
            writeDefine(rv, 0);
            storeWord(rv, returnReg);
        }

        // Write the parameters in the function
        for(VariableDecl vd : f.getParams()) {
            writeDefine(address + variableName+ vd.getName(), 0);
        }

        // Write the return data
        writeDefine(address + rtn, 0);
    }

    /**
     *
     * @param addrss String
     * @param v VariableDecl
     */
    public void writeDefine(String addrss, VariableDecl v) {
        if(!active) return;
        if(!v.isPrimitive()) {
            writeRes(addrss, v.getAttributes().size()*4);
        }

    }

    /**
     *
     * @param address String
     * @param val int
     */
    public void writeDefine(String address, int val) {
        if(!active) return;
        String d = address + "\tdw\t" + val;
        dataDW.add(d);
    }


    /**
     *
     * @param memoryAddress String
     * @param array Node representing root of an array tree
     */
    public void writeRes(String memoryAddress, Node array) {
        if(!active) return;

        // Definition nis easy, no expression in array size
        ArrayList<Tuple> tokens = array.getTokens();
        int size = 1;
        for(int i=0;i<tokens.size();i++){
            if(tokens.get(i).getX().equals(Token.SEMICOLON.toString())) break;
            if(tokens.get(i).getX().equals(Token.INTEGER.toString())) {
                // Cheating a bit - should really push value to variable stack and let assembly determine it
                size *= Integer.parseInt((String)tokens.get(i).getY());
            }
        }
        writeRes(memoryAddress, size * intSize);
    }

    /**
     *
     * @param memoryAddress String
     * @param size int
     */
    public void writeRes(String memoryAddress, int size) {
        if(!active) return;
        String d = memoryAddress + "\tres\t" + size;
        dataRes.add(d);
        arrays.add(memoryAddress);
    }


    /**
     *
     * @param num String number we wish to store in assembly variable
     */
    public void writeNum(String num) throws CodeException {
        if(!active) return;
        comment("Save number");
        String reg = getRegister();
        String a2 = "addi\t" + reg +", " + "r0" + ", " + num;
        execution.add(a2);
        writeTemprorary(reg);
        freeRegister(reg);
    }

    /**
     * Addition
     */
    public void writeAdd() throws CodeException {
        if(!active) return;
        comment("Addition");
        binaryOp("add");
    }


    /**
     * Subtraction
     */
    public void writeSub() throws CodeException {
        if(!active) return;
        comment("Subtraction");

        // Subtraction is treated as addition of a negative number
        binaryOp("add");

    }

    /**
     * Multiply
     */
    public void writeMultiply() throws CodeException {
        if(!active) return;
        comment("Multiply");
        binaryOp("mul");
    }

    /**
     * Divide
     */
    public void writeDivide() throws CodeException {
        if(!active) return;
        comment("Divide");
        binaryOp("div");
    }


    /**
     * Equals
     */
    public void writeEquals() throws CodeException {
        if(!active) return;
        comment("Equals");
        binaryOp("ceq");
    }

    /**
     * Greater Than
     */
    public void writeGreaterThan() throws CodeException {
        if(!active) return;
        comment("Greater Than");
        binaryOp("cgt");

    }

    /**
     *
     * @param op String operation to be performed
     */
    private void binaryOp(String op) throws CodeException {
        // Get variables
        String var2 = pop();
        String reg2 = loadWord(var2);

        String var1 = pop();
        String reg1 = loadWord(var1);

        String reg3 = getRegister();

        if(reg3 == null) return;
        execution.add(op +"\t"+reg3+", " + reg1 +", " + reg2);
        writeTemprorary(reg3);
        freeRegister(reg1);
        freeRegister(reg2);
        freeRegister(reg3);
    }


    /**
     * Perform put operation
     */
    public void writePut() throws CodeException {
        if(!active) return;
        if(varStack.size() == 0) return;
        comment("Put");

        String tmpVar = pop();
        String offsetReg;
        if(arrays.contains(tmpVar)) {
            offsetReg = getPositionReg();
        } else {
            offsetReg = r0;
        }
        String reg = loadWord(tmpVar, offsetReg);
        String tmpReg = getRegister();
        String e = "sw\t-8(" + r14 + ")," + reg;
        e += "\naddi\t" + tmpReg + ",r0,buf";
        e += "\nsw\t-12(" + r14 + ")," + tmpReg + "";
        e += "\njl\t" + r15 + ",intstr";
        e += "\nsw\t-8(" + r14 + ")," + r13 + "";
        e += "\njl\t" + r15 + ",putstr";
        execution.add(e);
        freeRegister(tmpReg);
        freeRegister(reg);
        if(offsetReg != null && !offsetReg.equals(r0)) freeRegister(offsetReg);
    }

    /**
     * @param register String register we want to write to to temporary variable
     */
    private void writeTemprorary(String register) {
        if(!active) return;
        String tmpVar = getTmpVar();
        storeWord(tmpVar, register);
        varStack.add(tmpVar);
    }

    /**
     *
     * @param var String variable we want to load
     * @return String of register var is in
     */
    private String loadWord(String var) throws CodeException {
        if (arrays.contains(var)) {
            String arrayOffset = getPositionReg();
            return loadWord(var, arrayOffset);
        } else {
            return loadWord(var, r0);
        }
    }

    /**
     *
     * @param var String variable we want to load
     * @param offsetReg String register with variable offset
     * @return String register var is now in
     */
    private String loadWord(String var, String offsetReg) throws CodeException {
        String reg = getRegister();
        String e = "lw\t" + reg + ", " + var + "("+offsetReg+")";
        execution.add(e);
        if(var.startsWith("tmp")) tmpVars.add(var);
        freeRegister(offsetReg);
        return reg;


    }

    /**
     *
     * @param var String var we want to store
     * @param reg String reg we want to store var in
     */
    private void storeWord(String var, String reg) {
        storeWord(var, reg, r0);
    }

    /**
     *
     * @param var String var we want to store
     * @param reg String reg we want to store var in
     */
    private void storeWord(String var, String reg, String positionReg) {
        String e = "sw\t" + var + "(" + positionReg + "), " + reg;
        execution.add(e);
    }

    /**
     *
     * @return String register with position offset in
     */
    private String getPositionReg() throws CodeException {
        // We assume position offset is in varstack
        String offsetReg = loadWord(pop());
        String multOffsetReg = getRegister();
        String e = "muli\t"+multOffsetReg +", " +offsetReg+ ", "+intSize;
        execution.add(e);
        freeRegister(offsetReg);
        return multOffsetReg;

    }

    /**
     *
     * @return String free register
     */
    private String getRegister() throws CodeException {
        for(Map.Entry<String, Boolean> map: registers.entrySet()) {
            if(map.getValue()) {
                registers.put(map.getKey(), false);
                return map.getKey();
            }
        }
        // No registers are free
        throw new CodeException(true, false);
    }

    /**
     *
     * @return String tmporary variable name
     */
    private String getTmpVar()
    {
        if(tmpVars.size() == 0) {
            String tmp = "tmp" + tmpVarPointer + "_"+randomVar;
            randomVar += 1;
            writeDefine(tmp, 0);
            return tmp;
        } else {
            String tmp = tmpVars.get(tmpVars.size() - 1);
            if(!tmp.contains("tmp" + tmpVarPointer)) {
                tmp = "tmp" + tmpVarPointer + "_"+randomVar;
                randomVar += 1;
                writeDefine(tmp, 0);
                return tmp;
            }
            tmpVars.remove(tmpVars.size() - 1);
            return tmp;
        }
    }


    /**
     *
     * @param path String to save file to
     */
    public void save(String path) throws FileNotFoundException, UnsupportedEncodingException {
        if(!active) return;
        PrintWriter moonWriter = new PrintWriter(path, "UTF-8");

        // Write some headers
        moonWriter.write("align\nentry\n\n");
        moonWriter.write("addi\t" + r14 +",r0,topaddr\n");
        moonWriter.write("j\tstartProgram\n");

        // Write execution
        for(String line: execution){
            moonWriter.write(line + "\n");
        }

        // Write end program labels
        moonWriter.write("\n" + endProgram);
        moonWriter.write("\nhlt");
        moonWriter.write("\n");

        // Write data
        writeRes("buf", 20);
        for(String line : dataDW) {
            moonWriter.write(line + "\n");
        }
        for(String line : dataRes) {
            moonWriter.write(line + "\n");
        }
        moonWriter.close();
    }

    /**
     *
     * @return String top item on variable stack
     */
    private String pop() throws CodeException {
        if(varStack.size() == 0) throw new CodeException(false, true);
        String var = varStack.get(varStack.size()-1);
        varStack.remove(varStack.size() - 1);
        return var;
    }

    /**
     *
     * @param pop boolean if we should remove number from the stack
     * @return integer from the ifStack
     */
    private Integer getIfNum(boolean pop) {
        Integer num = ifNums.get(ifNums.size() - 1);
        if(pop) ifNums.remove(ifNums.size() - 1);
        return num;
    }

    /**
     *
     * @param reg String register we want to remove from occupied registers
     */
    private void freeRegister(String reg) {
        if(reg.equals(r0) || reg.equals(r13) || reg.equals(r14) || reg.equals(r15)) return;
        registers.put(reg, true);
    }

    /**
     * Place variable on the stack
     *
     * @param address String
     * @param offset Integer
     */
    public void loadVar(String address, Integer offset) throws CodeException {
        if(!active) return;
        comment("Get Variable " + address);
        // Load offset - this could always be r0
        String offsetReg = getRegister();
        if(offsetReg == null) return;
        String e = "addi\t" + offsetReg +", "+r0+", " + offset.toString();
        execution.add(e);
        // Multiply offset by bytes
        String offsetProperReg = getRegister();
        if(offsetProperReg == null) return;
        e = "muli\t"+offsetProperReg+","+offsetReg+", "+ intSize;
        execution.add(e);
        // Now we can load our variable
        String varReg = loadWord(address, offsetProperReg);
        writeTemprorary(varReg);

        freeRegister(offsetReg);
        freeRegister(offsetProperReg);
        freeRegister(varReg);
    }

    /**
     * Load variable to variable stack
     * @param address String
     */
    public void loadVar(String address) {
        if(!active) return;
        varStack.add(address);
    }


    /**
     * @param s String we want to add as comment
     */
    private void comment(String s) {
        execution.add("\n%\t" + s);
    }

    /**
     * Increase value used in if and for statements - used for unique labelling
     */
    private void updateIfNum() {
        ifNumCount += 1;
        ifNums.add(ifNumCount);
    }

    /**
     *  Write if label
     */
    public void writeIf() throws CodeException {
        if(!active) return;
        comment("If Statement");

        // New if, increase number
        updateIfNum();

        String tmpVar = pop();
        String relOpReg = loadWord(tmpVar);

        String e = "bz\t" + relOpReg + ", else" + getIfNum(false);
        execution.add(e);
        freeRegister(relOpReg);
    }

    /**
     * Else jump
     */
    public void writeElse() {
        if(!active) return;
        comment("Else Statement");
        execution.add("j\tendif"+getIfNum(false));
        execution.add("else" + getIfNum(false));
    }

    /**
     * End if jump
     */
    public void writeEndIf() {
        if(!active) return;
        comment("End If");
        execution.add("endif"+getIfNum(true));

    }

    /**
     * Greater Than Equals
     */
    public void writeGreaterThanEquals() throws CodeException {
        if(!active) return;
        comment("Greater Than Equals");
        binaryOp("cge");
    }

    /**
     * Less Than Equals
     */
    public void writeLessThanEquals() throws CodeException {
        if(!active) return;
        comment("Less Than Equals");
        binaryOp("cle");
    }

    /**
     * Less Than
     */
    public void writeLessThan() throws CodeException {
        if(!active) return;
        comment("Less Than");
        binaryOp("clt");
    }

    /**
     * Not Equals
     */
    public void writeNotEquals() throws CodeException {
        if(!active) return;
        comment("Not Equals");
        binaryOp("cne");
    }

    /**
     * Write begging of FOR loop
     */
    public void writeStartFor() {
        if(!active) return;
        comment("For");
        updateIfNum();
        String e = "for"+getIfNum(false);
        execution.add(e);
    }


    /**
     * Write branch used in for loop if condition is met
     */
    public void writeForBranch() throws CodeException {
        if(!active) return;
        String tmpVar = pop();
        String relOpReg = loadWord(tmpVar);
        String e = "bz\t"+relOpReg+", endfor"+getIfNum(false);
        execution.add(e);
        e = "j\tstartFor" + getIfNum(false);
        execution.add(e);
        freeRegister(relOpReg);
    }

    public void writeEndFor() {
        if(!active) return;
        String e = "j\tassigFor" + getIfNum(false);
        execution.add(e);

        e = "endfor"+getIfNum(true);
        execution.add(e);
    }

    /**
     * Write beginning of statement label in for loop
     */
    public void writeStartForBlock() {
        if(!active) return;
        String e = "startFor"+ getIfNum(false);
        execution.add(e);
    }

    /**
     * Write assignment label
     */
    public void writeForAssig() {
        if(!active) return;
        String e = "assigFor" + getIfNum(false);
        execution.add(e);
    }

    /**
     * Write end For loop
     */
    public void writeEndForAssig() {
        if(!active) return;
        String e = "j\tfor"+getIfNum(false);
        execution.add(e);
    }

    /**
     * @param decl Decclartion that has ended
     * @throws CodeException
     */
    public void writeEnd(Declaration decl) throws CodeException {
        if(!active) return;
        if(decl instanceof ProgramDecl) {
            // End of program
            String e = "\nj\t"+endProgram;
            execution.add(e);
        } else if(decl instanceof FunctionDecl) {
            // End of function
            comment("Leaving function");

            // No return variables - no return statement
            if(returnVar.size() == 0) return;
            // Get return variable
            String rv = returnVar.get(returnVar.size() - 1);
            returnVar.remove(returnVar.size() - 1);

            // Get return register
            String returnReg = loadWord(rv);
            // Write the jump
            String e = "jr\t"+returnReg;
            execution.add(e);
            freeRegister(returnReg);
        }
    }

    /**
     *
     * @param f FuncctionDecl
     * @param address String
     * @param method boolean if method or free function
     * @throws CodeException
     */
    public void writeFunctionCall(FunctionDecl f, String address, boolean method) throws CodeException {
        if(!active) return;

        // Write function parameters
        for(int i =f.getParams().size() - 1; i > -1;i--){
            String popped = pop();
            String reg = loadWord(popped);
            String param = address + variableName + f.getParams().get(i).getName();
            storeWord(param, reg);

        }

        // Write functions register to be used in the return link
        // If method this register already exists (methods occur before calls)
        // If free function then we set it here
        String reg;
        if(!method) {
            reg = getFunctionReg(f.getName());
            if(reg == null) {
                reg = getRegister();
            }
            storeFunctionReg(reg, f.getName());
        } else {
            reg = getFunctionReg(f.getName());
        }
        String e = "jl\t"+reg+", " + address;
        execution.add(e);
        freeRegister(reg);
    }

    /**
     *
     * @param reg String register to store
     * @param name String function name using this register
     */
    private void storeFunctionReg(String reg, String name) {
        functionMap.put(name, reg);
    }

    /**
     *
     * @param name String function name
     * @return String register used by function
     */
    private String getFunctionReg(String name) {
        String reg = functionMap.get(name);
        return reg;
    }

    /**
     *
     * Place return value in variable
     * @param address String
     */
    public void writeReturn(String address) throws CodeException {
        if(!active) return;
        comment("Set up return");
        String varName = address + rtn;
        String tmpVar = pop();
        String tmpReg = loadWord(tmpVar);
        storeWord(varName, tmpReg);
    }

    /**
     * Load return variable into temporary variable
     *
     * @param address String
     */
    public void writeGetReturn(String address) throws CodeException {
        if(!active) return;
        comment("Get return");
        String varName = address + rtn;
        String tmpReg = loadWord(varName);
        writeTemprorary(tmpReg);
    }

    /**
     * Write start program label
     */
    public void writeStartProgram() {
        if(!active) return;
        comment("Start of Program Block");
        execution.add("startProgram");
    }

    /**
     *
     * @param sign String (+/-)
     */
    public void writeSign(String sign) throws CodeException {
        if(!active) return;
        // If subtraction - we negate the nnumber
        if(sign.equals(Token.SUBTRACTION.toString())) {
            if(varStack.size() == 0) return;
            comment("Negative Number");
            String reg = getRegister();
            String tmpVar = pop();
            String varReg = loadWord(tmpVar);
            String e = "muli\t"+reg+ ", " + varReg + ", -1";
            execution.add(e);
            writeTemprorary(reg);
            freeRegister(reg);
            freeRegister(varReg);
        }
    }

    public void deactivate() {
        this.active = false;
    }
}
