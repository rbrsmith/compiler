package CodeGeneration;

import LexicalAnalyzer.DFA.Token;
import SemanticAnalyzer.Node;
import SemanticAnalyzer.VariableAssig;
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
    private String r0 = "r0";
    private String r13 = "r13";
    private String r14 = "r14";
    private String r15 = "r15";

    private ArrayList<String> execution;
    private ArrayList<String> dataRes;
    private ArrayList<String> dataDW;
    private ArrayList<String> varStack;
    private ArrayList<String> tmpVars;
    private ArrayList<String> arrays;
    private int randomVar;
    private final int intSize = 4;

    private CodeGenerator(){
        execution = new ArrayList<>();
        dataRes = new ArrayList<>();
        dataDW = new ArrayList<>();
        varStack = new ArrayList<>();
        tmpVars = new ArrayList<>();
        arrays = new ArrayList<>();
        randomVar = 0;
    }

    public static CodeGenerator getInstance() {
        return instance;
    }

    public void write(String address, VariableAssig va) {
        if(varStack.size() == 0) return;
        comment("Variable Assignment " + va.toString() + " = ...");

        String reg = loadWord(pop());
        String multOffsetReg;
        if(va.getSize() == 0) {
            multOffsetReg = r0;
        } else {
            multOffsetReg= getPositionReg();
        }
        storeWord(address, reg, multOffsetReg);
        if(!multOffsetReg.equals(r0)) freeRegister(multOffsetReg);

        freeRegister(reg);

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
        writeRes(memoryAddress, size);
    }

    public void writeRes(String memoryAddress, int size) {
        comment("Memory Definition");
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
        String var2 = pop();
        if(var2 == null) return;
        String reg2 = loadWord(var2);

        String var1 = pop();
        if(var1 == null) return;
        String reg1 = loadWord(var1);

        String reg3 = getRegister();
        String e = "add\t"+reg3 + ", " +reg1+", "+reg2;
        execution.add(e);
        writeTemprorary(reg3);
        freeRegister(reg1);freeRegister(reg2);freeRegister(reg3);

    }


    public void writeSub() {
        comment("Subtraction");
        String var2 = pop();
        if(var2 == null) return;
        String reg2 = loadWord(var2);

        String var1 = pop();
        if(var1 == null) return;
        String reg1 = loadWord(var1);

        String reg3 = getRegister();
        String e = "sub\t"+reg3+", "+reg1+", " +reg2;
        execution.add(e);
        writeTemprorary(reg3);
        freeRegister(reg1);freeRegister(reg2);freeRegister(reg3);
    }

    public void writeMultiply() {
        comment("Multiply");
        String var2 = pop();
        if(var2 == null) return;
        String reg2 = loadWord(var2);

        String var1 = pop();
        if(var1 == null) return;
        String reg1 = loadWord(var1);

        String reg3 = getRegister();
        String e = "mul\t"+reg3 +", " + reg1 + ", " + reg2;
        execution.add(e);
        writeTemprorary(reg3);
        freeRegister(reg1);freeRegister(reg2);freeRegister(reg3);
    }


    public void writeDivide() {
        comment("Divide");
        String var2 = pop();
        if(var2 == null) return;
        String reg2 = loadWord(var2);

        String var1 = pop();
        if(var1 == null) return;
        String reg1 = loadWord(var1);

        String reg3 = getRegister();
        String e = "div\t"+reg3 +", " + reg1 + ", " + reg2;
        execution.add(e);
        writeTemprorary(reg3);
        freeRegister(reg1);freeRegister(reg2);freeRegister(reg3);

    }

    public void writePut() {
        if(varStack.size() == 0) return;
        comment("Put");

        String tmpVar = pop();
        String offsetReg;
        if(varStack.size() == 0){
            offsetReg = r0;
        } else {
            offsetReg = getPositionReg();
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

        for(String line: execution){
            moonWriter.write(line + "\n");
        }

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
    private void freeRegister(String reg) {
        registers.put(reg, true);
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
}
