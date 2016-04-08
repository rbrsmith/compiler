package CodeGeneration;

import SemanticAnalyzer.Declaration;
import SemanticAnalyzer.VariableDecl;

public class CodeGenerator {

    public void write(String message) {
        System.out.println(message);
    }

    public void write(String memroyAddress, Declaration decl) {
        if(decl instanceof VariableDecl) write(memroyAddress, (VariableDecl) decl);

    }
    public void write(String memoryAddress, VariableDecl decl) {
        if(decl.isPrimitive()) {
            if(decl.getSize().size() == 0) {
                // Symbol variable int x
                write(memoryAddress + "\tdw\t0\t\t% Var Decl " + decl.getType() + " " + decl.getName());
            }
        }
    }
}
