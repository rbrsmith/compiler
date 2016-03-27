package SemanticAnalyzer;

import LexicalAnalyzer.DFA.Position;
import LexicalAnalyzer.DFA.Reserved;
import LexicalAnalyzer.DFA.Token;
import SyntacticAnalyzer.Tuple;
import org.junit.Test;

import static org.junit.Assert.*;

public class FunctionDeclTest {

    @Test
    public void test() {
        Position pos = new Position();
        Node root = new Node("root", false, true, null, pos);

        Node TYPE = new Node(Reserved.INT.getWord(), false, false, root, pos);
        Node type = new Node(new Tuple(Reserved.INT.getWord(), Reserved.INT.getWord()), true, false, TYPE, pos);

        Node ID = new Node(Token.ID.toString(), false, false, root, pos);
        Node id = new Node(new Tuple(Token.ID.toString(), "IDTEST"), true, false, ID, pos);

        Node FPAMARS = new Node("fParams", false, false, root, pos);
        Node EPSILON = new Node(new Tuple("EPSILON", "EPSILON"), true, false, FPAMARS, pos);

        FunctionDecl fd = new FunctionDecl(id, type, FPAMARS);

        assertTrue(fd.getName().equals("IDTEST"));
        assertTrue(fd.getType().equals("int"));
        assertTrue(fd.getParams().size() == 0);

        FPAMARS = new Node("fParams", false, false, root, pos);

        Node TYPE2 = new Node(Reserved.FLOAT.getWord(), false, false, FPAMARS, pos);
        Node ID2 = new Node(Token.ID.toString(), false, false, FPAMARS, pos);
        EPSILON = new Node(new Tuple("EPSILON", "EPSILON"), true, false, FPAMARS, pos);

        FPAMARS.addChild(TYPE2);FPAMARS.addChild(ID2);FPAMARS.addChild(EPSILON);
        FPAMARS.addChild(new Node("SEMANTIC-5", false, false, FPAMARS, pos));


        Node type2 = new Node(new Tuple(Reserved.FLOAT.getWord(), Reserved.FLOAT.getWord()), true, false, TYPE2, pos);
        TYPE2.addChild(type2);

        Node id2 = new Node(new Tuple(Token.ID.toString(), "IDTEST2"), true, false, ID2, pos);
        ID2.addChild(id2);


        fd = new FunctionDecl(id, type, FPAMARS);
        assertTrue(fd.getName().equals("IDTEST"));
        assertTrue(fd.getType().equals("int"));
        assertTrue(fd.getParams().size() == 1);

        assertTrue(fd.getParams().get(0).getType().equals("float"));
        assertTrue(fd.getParams().get(0).getName().equals("IDTEST2"));
    }

}