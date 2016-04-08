package SemanticAnalyzer;

import LexicalAnalyzer.DFA.Position;
import LexicalAnalyzer.DFA.Token;
import SyntacticAnalyzer.Tuple;
import org.junit.Test;

import static org.junit.Assert.*;

public class SymbolTableTest {

    @Test
    public void testAdd() throws Exception {
        Node ID = new Node(Token.ID.toString(), false, false, null, new Position());
        Node childID = new Node(new Tuple(Token.ID, "TestClass"), true, false, ID, new Position());
        ClassDecl c = new ClassDecl(childID);

        SymbolTable symbolTable = new SymbolTable(null);
        SymbolTable sub = symbolTable.add(new Symbol(c, symbolTable, new Position()));
        assertTrue(sub.getParent() == symbolTable);
        assertTrue(symbolTable.alreadyExists(c));
        assertTrue(symbolTable.classExists("TestClass"));
        assertFalse(symbolTable.classExists("CLassTest"));
    }
}