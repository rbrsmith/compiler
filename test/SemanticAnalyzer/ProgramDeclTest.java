package SemanticAnalyzer;

import LexicalAnalyzer.DFA.Position;
import LexicalAnalyzer.DFA.Reserved;
import SyntacticAnalyzer.Tuple;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProgramDeclTest {

    @Test
    public void test() throws Exception {
        Node root = new Node("root", false, true, null, new Position());

        Node program = new Node(new Tuple(Reserved.PROGRAM.getWord(), Reserved.PROGRAM.getWord()), true, false, root, new Position());
        root.addChild(program);
        ProgramDecl p = new ProgramDecl(program);
        assertTrue(p.getName().equals(Reserved.PROGRAM.getWord()));


    }

}