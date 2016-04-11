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

public class CodeGenerator {

    private static CodeGenerator instance = new CodeGenerator();
    private final HashMap<String, Boolean> registers = new HashMap<String,Boolean>() {{
        put("r1",true);put("r2",true);put("r3",true);put("r4",true);put("r5",true);
        put("r6",true);put("r7",true);put("r8",true);put("r9",true);put("r10",true);
        put("r11",true);put("r12",true);
    }};
    private final String r0 = "r0";
    private final String r13 = "r13";
    private final String r14 = "r14";
    private final String r15 = "r15";

    public final String functionName = "FUNCTION__";
    public final String className = "CLASS__";
    public final String variableName = "VARIABLE__";
    public final String globalName = "GLOBAL__";


    private final String rtn = "__RTN";

    private ArrayList<String> execution;
    private ArrayList<String> dataRes;
    private ArrayList<String> dataDW;
    private ArrayList<String> varStack;
    private ArrayList<String> tmpVars;
    private ArrayList<String> arrays;
    private ArrayList<Integer> ifNums;
    private HashMap<String, String> functionMap;
    private int ifNumCount;

    private int randomVar;
    private final int intSize = 4;
    private final String endProgram = "endProgram";
    private final ArrayList<String> returnVar;

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

    }

    public static CodeGenerator getInstance() {
        return instance;
    }

    public void write(String address, VariableAssig va, Integer attributeOffset) {
        if(varStack.size() == 0) return;
        comment("Variable Assignment " + va.toString() + " = ...");

        String reg = loadWord(pop());
        String multOffsetReg;
        if(va.getSize() == 0) {
            multOffsetReg = r0;
        } else {
            multOffsetReg = getPositionReg();
        }

        if(attributeOffset != null){
            String attributeOffsetReg = getAttributeOffset(attributeOffset);

            if(!multOffsetReg.equals(r0)) {
                String tmpReg = getRegister();
                String e = "mul\t" + tmpReg +", " + attributeOffsetReg + ", " + multOffsetReg;
                execution.add(e);
                freeRegister(multOffsetReg);
                multOffsetReg = tmpReg;
            } else {
                freeRegister(multOffsetReg);
                multOffsetReg = attributeOffsetReg;
            }
        }

        storeWord(address, reg, multOffsetReg);
        if(!multOffsetReg.equals(r0)) freeRegister(multOffsetReg);

        freeRegister(reg);

    }

    private String getAttributeOffset(Integer attributeOffset) {
        String reg = getRegister();
        String e = "addi\t"+reg+", " +r0+","+attributeOffset.toString();
        execution.add(e);
        String reg2 = getRegister();
        e = "muli\t"+reg2+", "+reg+", 4";
        execution.add(e);
        freeRegister(reg);
        return reg2;
    }

    public void write(String address, FunctionDecl f, boolean method) {
        comment("Function");
        String e = address;
        execution.add(e);

        String returnReg;
        if(!method) {
            returnReg = getFunctionReg(f.getName());
        } else {
            returnReg = getRegister();
            storeFunctionReg(returnReg, f.getName());
        }
            if (returnReg != null) {
                String rv = "returnVar" + randomVar;
                randomVar += 1;
                returnVar.add(rv);
                writeDefine(rv, 0);
                storeWord(rv, returnReg);
            }

        for(VariableDecl vd : f.getParams()) {
            writeDefine(address + variableName+ vd.getName(), 0);
        }

        writeDefine(address + rtn, 0);

    }

    public void writeDefine(String addrss, VariableDecl v) {
        if(!v.isPrimitive()) {
            writeRes(addrss, v.getAttributes().size()*4);
        }

    }



    public void writeDefine(String address, int val) {
        String d = address + "\tdw\t" + val;
        dataDW.add(d);
    }


    public void writeRes(String memoryAddress, Node array) {
        // Definition nis easy, no expression in array size
        ArrayList<Tuple> tokens = array.getTokens();
        int size = 1;
        for(int i=0;i<tokens.size();i++){
            if(tokens.get(i).getX().equals(Token.SEMICOLON.toString())) break;
            if(tokens.get(i).getX().equals(Token.INTEGER.toString())) {
                size *= Integer.parseInt((String)tokens.get(i).getY());
            }
        }
        writeRes(memoryAddress, size*4);
    }

    public void writeRes(String memoryAddress, int size) {
        String d = memoryAddress + "\tres\t" + size;
        dataRes.add(d);
        arrays.add(memoryAddress);
    }


    public void writeNum(String num) {
        comment("Save number");
        String reg = getRegister();
        String a2 = "addi\t" + reg +", " + "r0" + ", " + num;
        execution.add(a2);
        writeTemprorary(reg);
        freeRegister(reg);
    }

    public void writeAdd() {
        comment("Addition");
        binaryOp("add");
    }


    public void writeSub() {
        comment("Subtraction");
        binaryOp("sub");
    }

    public void writeMultiply() {
        comment("Multiply");
        binaryOp("mul");
    }


    public void writeDivide() {
        comment("Divide");
        binaryOp("div");
    }


    public void writeEquals() {
        comment("Equals");
        binaryOp("ceq");
    }


    public void writeGreaterThan() {
        comment("Greater Than");
        binaryOp("cgt");

    }

    private void binaryOp(String op) {
        String var2 = pop();
        if(var2 == null) return;
        String reg2 = loadWord(var2);

        String var1 = pop();
        if(var1 == null) return;
        String reg1 = loadWord(var1);

        String reg3 = getRegister();
        execution.add(op +"\t"+reg3+", " + reg1 +", " + reg2);
        writeTemprorary(reg3);
        freeRegister(reg1);
        freeRegister(reg2);
        freeRegister(reg3);

    }


    public void writePut() {
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


    private void writeTemprorary(String register) {
        String tmpVar = getTmpVar();
        storeWord(tmpVar, register);
        varStack.add(tmpVar);

    }

    private String loadWord(String var) {
        if (var == null) return null;
        if (arrays.contains(var)) {
            String arrayOffset = getPositionReg();
            return loadWord(var, arrayOffset);
        } else {
            return loadWord(var, r0);
        }
    }

    private String loadWord(String var, String offsetReg) {
        String reg = getRegister();
        String e = "lw\t" + reg + ", " + var + "("+offsetReg+")";
        execution.add(e);
        if(var.startsWith("tmp")) tmpVars.add(var);
        freeRegister(offsetReg);
        return reg;


    }

    private void storeWord(String var, String reg) {
        storeWord(var, reg, r0);
    }


    private void storeWord(String var, String reg, String positionReg) {
        String e = "sw\t" + var + "(" + positionReg + "), " + reg;
        execution.add(e);
    }

    private String getPositionReg() {
        String offsetReg = loadWord(pop());
        String multOffsetReg = getRegister();
        if(multOffsetReg == null) return null;
        String e = "muli\t"+multOffsetReg +", " +offsetReg+ ", "+intSize;
        execution.add(e);
        freeRegister(offsetReg);
        return multOffsetReg;

    }

    public String getRegister() {
        for(Map.Entry<String, Boolean> map: registers.entrySet()) {
            if(map.getValue()) {
                registers.put(map.getKey(), false);
                return map.getKey();
            }
        }
        // TODO expcetion
        return null;
    }

    public String getTmpVar()
    {
        if(tmpVars.size() == 0) {
            String tmp = "tmp" + randomVar;
            randomVar += 1;
            writeDefine(tmp, 0);
            return tmp;
        } else {
            String tmp = tmpVars.get(tmpVars.size() - 1);
            tmpVars.remove(tmpVars.size() - 1);
            return tmp;
        }
    }


    public void save(String path) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter moonWriter = new PrintWriter(path, "UTF-8");
        moonWriter.write("align\nentry\n\n");
        moonWriter.write("addi\t" + r14 +",r0,topaddr\n");
        moonWriter.write("j\tstartProgram\n");

        for(String line: execution){
            moonWriter.write(line + "\n");
        }


        moonWriter.write("\n" + endProgram);
        moonWriter.write("\nhlt");
        moonWriter.write("\n");
        writeRes("buf", 20);
        for(String line : dataDW) {
            moonWriter.write(line + "\n");
        }
        for(String line : dataRes) {
            moonWriter.write(line + "\n");
        }

        moonWriter.close();

    }

    private String pop() {
        if(varStack.size() == 0) return null;
        String var = varStack.get(varStack.size()-1);
        varStack.remove(varStack.size() - 1);
        return var;
    }

    private Integer getIfNum(boolean pop) {
        Integer num = ifNums.get(ifNums.size() - 1);
        if(pop) ifNums.remove(ifNums.size() - 1);
        return num;
    }

    private void freeRegister(String reg) {
        registers.put(reg, true);
    }

    public void loadVar(String address, Integer offset){
        comment("Get Variable " + address);
        String offsetReg = getRegister();
        if(offsetReg == null) return;
        String e = "addi\t" + offsetReg +", "+r0+", " + offset.toString();
        execution.add(e);
        String offsetProperReg = getRegister();
        if(offsetProperReg == null) return;
        e = "muli\t"+offsetProperReg+","+offsetReg+", "+ intSize;
        execution.add(e);
        String varReg = loadWord(address, offsetProperReg);
        writeTemprorary(varReg);

        freeRegister(offsetReg);
        freeRegister(offsetProperReg);
        freeRegister(varReg);
    }

    public void loadVar(String address) {
        varStack.add(address);
    }

    public int getExecutionSize() {
        return execution.size();
    }

    public int getDataSize() {
        return dataRes.size() + dataDW.size();
    }

    public void reset() {
        execution = new ArrayList<>();
        dataRes = new ArrayList<>();
        dataDW = new ArrayList<>();
        varStack = new ArrayList<>();
        tmpVars = new ArrayList<>();
        randomVar = 0;

    }


    private void comment(String s) {
        execution.add("\n%\t" + s);
    }
    private void commentDW(String s) {
        dataDW.add("\n%\t" + s);
    }
    private void commentRES(String s) {
        dataRes.add("\n%\t" + s);
    }

    private void updateIfNum() {
        ifNumCount += 1;
        ifNums.add(ifNumCount);
    }

    public void writeIf() {
        comment("If Statement");

        updateIfNum();


        String tmpVar = pop();
        if(tmpVar == null) return;
        String relOpReg = loadWord(tmpVar);
        String e = "bz\t" + relOpReg + ", else" + getIfNum(false);
        execution.add(e);
        freeRegister(relOpReg);
    }

    public void writeElse() {
        comment("Else Statement");
        execution.add("j\tendif"+getIfNum(false));
        execution.add("else"+getIfNum(false));
    }

    public void writeEndIf() {
        comment("End If");
        execution.add("endif"+getIfNum(true));

    }

    public void writeGreaterThanEquals() {
        comment("Greater Than Equals");
        binaryOp("cge");
    }

    public void writeLessThanEquals() {
        comment("Less Than Equals");
        binaryOp("cle");
    }

    public void writeLessThan() {
        comment("Less Than");
        binaryOp("clt");
    }

    public void writeNotEquals() {
        comment("Not Equals");
        binaryOp("cne");
    }

    public void writeStartFor() {
        comment("For");
        updateIfNum();
        String e = "for"+getIfNum(false);
        execution.add(e);
    }


    public void writeForBranch() {
        String tmpVar = pop();
        if(tmpVar == null) return;
        String relOpReg = loadWord(tmpVar);
        String e = "bz\t"+relOpReg+", endfor"+getIfNum(false);
        execution.add(e);
        e = "j\tstartFor" + getIfNum(false);
        execution.add(e);
        freeRegister(relOpReg);
    }

    public void writeEndFor() {
        String e = "j\tassigFor" + getIfNum(false);
        execution.add(e);

        e = "endfor"+getIfNum(true);
        execution.add(e);
    }

    public void writeStartForBlock() {
        String e = "startFor"+ getIfNum(false);
        execution.add(e);
    }

    public void writeForAssig() {
        String e = "assigFor" + getIfNum(false);
        execution.add(e);
    }

    public void writeEndForAssig() {
        String e = "j\tfor"+getIfNum(false);
        execution.add(e);
    }

    public void writeEnd(Declaration decl) {
        if(decl instanceof ProgramDecl) {
            String e = "\nj\t"+endProgram;
            execution.add(e);
        } else if(decl instanceof FunctionDecl) {
            comment("Leaving function");

            if(returnVar.size() == 0) return;
            String rv = returnVar.get(returnVar.size() - 1);
            returnVar.remove(returnVar.size() - 1);

            String returnReg = loadWord(rv);
            if(returnReg == null) return;
            String e = "jr\t"+returnReg;
            execution.add(e);
            freeRegister(returnReg);
        }
    }

    public void writeFunctionCall(FunctionDecl f, String address, boolean method) {


        for(int i =f.getParams().size() - 1; i > -1;i--){
            String popped = pop();
            String reg = loadWord(popped);
            if(reg == null) return;
            String param = address + variableName + f.getParams().get(i).getName();
            storeWord(param, reg);

        }

        String reg;
        if(!method) {
            reg = getRegister();
            storeFunctionReg(reg, f.getName());
        } else {
            reg = getFunctionReg(f.getName());
        }
        String e = "jl\t"+reg+", " + address;
        execution.add(e);
    }

    private void storeFunctionReg(String reg, String name) {
        functionMap.put(name, reg);
    }

    private String getFunctionReg(String name) {
        String reg = functionMap.get(name);
        return reg;
    }

    private void writePutStr() {
        comment("Library PUTSTR");
        String reg1 = getRegister();
        String reg2 = getRegister();
        String reg3 = getRegister();

        String e = "putstr    lw    "+reg1+",-8(r14)    % i := r1";
        execution.add(e);
        e = "addi  "+reg2+",r0,0";
        execution.add(e);
        e = "putstr1   lb    "+reg2+",0("+reg1+")      % ch := B[i]";
        execution.add(e);
        e = "ceqi  "+reg3+","+reg2+",0";
        execution.add(e);
        e = "bnz   "+reg3+",putstr2    % branch if ch = 0";
        execution.add(e);
        e = "putc  "+reg2+"";
        execution.add(e);
        e = "addi  "+reg1+","+reg1+",1       % i++";
        execution.add(e);
        e = "j     putstr1";
        execution.add(e);
        e = "putstr2   jr    " + r15;
        execution.add(e);

        freeRegister(reg1);
        freeRegister(reg2);


    }

    public void writeReturn(String address) {
        comment("Set up return");
        String varName = address + rtn;
        String tmpVar = pop();
        if(tmpVar == null) return;
        String tmpReg = loadWord(tmpVar);
        if(tmpReg == null) return;
        storeWord(varName, tmpReg);
    }

    public void writeGetReturn(String address) {
        comment("Get return");
        String varName = address + rtn;
        String tmpReg = loadWord(varName);
        if(tmpReg == null) return;
        writeTemprorary(tmpReg);
    }

    public void writeStartProgram() {
        comment("Start of Program Block");
        execution.add("startProgram");
    }
}
