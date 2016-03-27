package SemanticAnalyzer;

import LexicalAnalyzer.DFA.Position;
import LexicalAnalyzer.DFA.Token;
import SyntacticAnalyzer.Tuple;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ClassDeclTest {


    @Test
    public void test() throws Exception {
        String test = "Test";
        Position pos = new Position();
        pos.newLine();
        assertTrue(pos.getLine() == 2);
        Node ID = new Node(Token.ID.toString(), false, false, null, pos);
        Node childID = new Node(new Tuple(Token.ID, test), true, false, ID, pos);
        ClassDecl c = new ClassDecl(childID);
        assertTrue(c.getName().equals(test));
        assertTrue(childID.getPosition().getLine() == 2);
    }
}