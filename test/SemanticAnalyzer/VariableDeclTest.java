package SemanticAnalyzer;

import LexicalAnalyzer.DFA.Position;
import LexicalAnalyzer.DFA.Reserved;
import LexicalAnalyzer.DFA.Token;
import SyntacticAnalyzer.Tuple;
import com.sun.org.apache.regexp.internal.RE;
import org.junit.Test;

import static org.junit.Assert.*;

public class VariableDeclTest {

    @Test
    public void test() throws Exception {
        Node id = new Node(new Tuple(Token.ID.toString(), "IDTEST"), true, false, null, new Position());
        Node type = new Node(new Tuple(Reserved.INT.getWord(), Reserved.INT.getWord()), true, false, null, new Position());
        Node array = new Node(new Tuple("EPSILON", "EPSILON"), true, false, null, new Position());


        VariableDecl v = new VariableDecl(id, type, array);
        assertTrue(v.getName().equals("IDTEST"));
        assertTrue(v.getType().equals(Reserved.INT.getWord()));
        assertTrue(v.getSize().size() == 0);
        assertTrue(v.isPrimitive());

    }

}