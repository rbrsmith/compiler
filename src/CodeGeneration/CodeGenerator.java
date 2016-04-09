package CodeGeneration;

import LexicalAnalyzer.DFA.Reserved;
import LexicalAnalyzer.DFA.Token;
import SemanticAnalyzer.Declaration;
import SemanticAnalyzer.Node;
import SemanticAnalyzer.VariableAssig;
import SemanticAnalyzer.VariableDecl;
import SyntacticAnalyzer.Tuple;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CodeGenerator {

    private String currentVar;
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
    private int pointer;
    private ArrayList<String> execution;
    private ArrayList<String> dataRes;
    private ArrayList<String> dataDW;
    private ArrayList<String> varStack;
    private ArrayList<String> tmpVars;
    private int randomVar;
    private final int intSize = 4;

    private CodeGenerator(){
        this.pointer = 0;
        execution = new ArrayList<>();
        dataRes = new ArrayList<>();
        dataDW = new ArrayList<>();
        varStack = new ArrayList<>();
        tmpVars = new ArrayList<>();
        randomVar = 0;
    }

    public static CodeGenerator getInstance() {
        return instance;
    }

    public void write(String message) {
        System.out.println(message);
    }




    public void write(String address, VariableAssig va) {
        if(varStack.size() == 0) return;
        String e = "\n%\tVariable Definition";
        write(e);
        execution.add(e);
        if(va.getSize() == 0) {
            String reg = loadWord(pop());
            storeWord(address, reg, null);
        } else {
            e = "\n%\tGet array offset";
            write(e);
            execution.add(e);
            String tmp = pop();
            if(tmp == null) return;
            String reg = loadWord(tmp);
            if(reg == null) return;

            String offset = pop();
            if(offset == null) return;
            String offsetReg = loadWord(offset);
            if(offsetReg == null) return;


            e = "\n%\tMultiply Offset";
            write(e);
            execution.add(e);
            String multOffsetReg = getRegister();
            if(multOffsetReg == null) return;
            e = "muli\t"+multOffsetReg +", " +offsetReg+ ", "+intSize;
            write(e);
            execution.add(e);

            freeRegister(offsetReg);

            e = "\n%\tStore word";
            write(e);
            execution.add(e);
            storeWord(address, reg, multOffsetReg);


        }

    }


    public void writeDefine(String address, int val) {
        String d = address + "\tdw\t" + val;
        write(d);
        dataDW.add(d);
    }


    public void writeRes(String memoryAddress, Node array) {
        // Definition nis easy, no expression in array size
        ArrayList<Tuple> tokens = array.getTokens();
        int size = 1;
        for(int i=0;i<tokens.size();i++){
            if(tokens.get(i).getX().equals(Token.INTEGER.toString())) {
                size *= Integer.parseInt((String)tokens.get(i).getY());
            }
        }
        writeRes(memoryAddress, size);
    }

    public void writeRes(String memoryAddress, int size) {
        String d = memoryAddress + "\tres\t" + size;
        write(d);
        dataRes.add(d);
    }


    public void writeNum(String num) {
        execution.add("\n%\tUpdate Number");
        String reg = getRegister();
        String a2 = "addi\t" + reg +", " + "r0" + ", " + num;
        // Store to temporary variable
        write(a2);
        execution.add(a2);
        writeTemprorary(reg);
    }

    public void writeAdd() {
        execution.add("\n%\tAddition");
        String var2 = pop();
        String var1 = pop();
        if(var1 == null) return;
        String reg1 = loadWord(var1);
        String reg2 = loadWord(var2);
        String reg3 = getRegister();
        String e = "add\t"+reg3 + ", " +reg1+", "+reg2;
        write(e);
        execution.add(e);
        writeTemprorary(reg3);
    }


    public void writeSub() {
        execution.add("\n%\tSubtraction");
        String var2 = pop();
        String var1 = pop();
        if(var1 == null) return;
        String reg1 = loadWord(var1);
        String reg2 = loadWord(var2);
        String reg3 = getRegister();
        String e = "sub\t"+reg3+", "+reg1+", " +reg2;
        write(e);
        execution.add(e);
        writeTemprorary(reg3);
    }

    public void writeMultiply() {
        execution.add("\n%\tMultiply");
        String var2 = pop();
        String var1 = pop();
        if(var1 == null) return;
        String reg1 = loadWord(var1);
        String reg2 = loadWord(var2);
        String reg3 = getRegister();
        String e = "mul\t"+reg3 +", " + reg1 + ", " + reg2;
        write(e);
        execution.add(e);
        writeTemprorary(reg3);
    }


    public void writeDivide() {
        execution.add("\n%\tDivide");
        String var2 = pop();
        String var1 = pop();
        if(var1 == null) return;
        String reg1 = loadWord(var1);
        String reg2 = loadWord(var2);
        String reg3 = getRegister();
        String e = "div\t"+reg3 +", " + reg1 + ", " + reg2;
        write(e);
        execution.add(e);
        writeTemprorary(reg3);

    }

    public void writePut() {
        if(varStack.size() == 0) return;
        execution.add("\n%\tPut");

        String tmpVar = pop();
        if(varStack.size() == 0) {
            String reg = loadWord(tmpVar);
            String tmpReg = getRegister();
            String e = "sw\t-8(" + r14 + ")," + reg;
            e += "\naddi\t" + tmpReg + ",r0,buf";
            e += "\nsw\t-12(" + r14 + ")," + tmpReg + "";
            e += "\njl\t" + r15 + ",intstr";
            e += "\nsw\t-8(" + r14 + ")," + r13 + "";
            e += "\njl\t" + r15 + ",putstr";
            write(e);
            execution.add(e);
            freeRegister(tmpReg);
            freeRegister(reg);
        } else {
            // More on the stack - for now assume array of 1
            String offsetReg = loadWord(pop());
            // Put in multiply
            String e = "\n%\tMultiply Offset";
            write(e);
            execution.add(e);
            String multOffsetReg = getRegister();
            if(multOffsetReg == null) return;
            e = "muli\t"+multOffsetReg +", " +offsetReg+ ", "+intSize;
            write(e);
            execution.add(e);
            freeRegister(offsetReg);

            // Get value into a register
            String reg = getRegister();
            e = "lw\t" + reg + ", " + tmpVar +"("+multOffsetReg+")";
            write(e);
            execution.add(e);


            String tmpReg = getRegister();
            e = "sw\t-8(" + r14 + ")," + reg;
            e += "\naddi\t" + tmpReg + ",r0,buf";
            e += "\nsw\t-12(" + r14 + ")," + tmpReg + "";
            e += "\njl\t" + r15 + ",intstr";
            e += "\nsw\t-8(" + r14 + ")," + r13 + "";
            e += "\njl\t" + r15 + ",putstr";
            write(e);
            execution.add(e);
            freeRegister(tmpReg);
            freeRegister(reg);




        }
    }


    private void writeTemprorary(String register) {
        String tmpVar = getTmpVar();
        storeWord(tmpVar, register, null);
        varStack.add(tmpVar);

    }

    private String loadWord(String var) {
        String reg = getRegister();
        String e = "lw\t" + reg + ", " + var + "(r0)";
        write(e);
        execution.add(e);
        if(var.startsWith("tmp")) tmpVars.add(var);
        return reg;
    }


    private void storeWord(String var, String reg, String positionReg) {
        if(positionReg == null) {
            String e = "sw\t" + var + "(r0), " + reg;
            write(e);
            execution.add(e);
            freeRegister(reg);
        } else {
            // Set up offset
            String offsetReg = getRegister();

//            String e = "addi\t"+offsetReg +", r0, "+offset*intSize;
//            write(e);
//            execution.add(e);

            String e2 = "sw\t" + var + "(" +positionReg+"), "+ reg;
            write(e2);
            execution.add(e2);
            freeRegister(reg);
            freeRegister(offsetReg);
            freeRegister(positionReg);

        }
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
//        moonWriter.write("buf\tres 20\n");
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
        this.pointer = 0;
        execution = new ArrayList<>();
        dataRes = new ArrayList<>();
        dataDW = new ArrayList<>();
        varStack = new ArrayList<>();
        tmpVars = new ArrayList<>();
        randomVar = 0;

    }
}
