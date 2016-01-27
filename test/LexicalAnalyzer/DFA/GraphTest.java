package LexicalAnalyzer.DFA;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.PrintWriter;
import java.io.RandomAccessFile;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertTrue;

public class GraphTest {

    Graph g;
    POS pos;
    final String path = "test" + File.separator + "LexicalAnalyzer" + File.separator + "DFA" + File.separator + "test.txt";
    File file;

    @Before
    public void setUp() throws Exception {
        g = new Graph(new Node(0));

        PrintWriter writer = new PrintWriter(path, "UTF-8");
        writer.println("The first line");
        writer.close();
        file = new File(path);
    }

    @After
    public void tearDown() throws Exception {
        g = null;
    }


    @Test
    public void testSimpleCharacters() throws Exception {
        g.addNode(new Node(1, true, Token.ID));
        assertTrue(file.canRead());

        RandomAccessFile buffer = new RandomAccessFile(file, "r");
        try {
            g.getNextToken(buffer, new Position());
            assertTrue(false);
        } catch(InvalidCharacterException e) {
            assertTrue(true);
        }

        g.addEdge(0,1,Lexicon.LETTER);
        g.buildTST();
        POS pos = g.getNextToken(buffer, new Position());
        assertTrue(pos.getToken().equals("T"));

        g.addEdge(1,1, Lexicon.LETTER);
        g.buildTST();
        buffer.seek(0);
        pos = g.getNextToken(buffer, new Position());
        assertTrue(pos.getToken().equals("The"));

        try {
            g.getNextToken(buffer, new Position());
            fail();
        } catch(InvalidCharacterException e){
            assertTrue(true);
        }

        buffer.seek(0);
        g.addNode(new Node(2, true, Token.SPACE));
        g.addEdge(0,2,Lexicon.SPACE);
        g.buildTST();
        Position position = new Position();
        g.getNextToken(buffer, position); // the
        g.getNextToken(buffer, position); // space
        g.getNextToken(buffer, position); // first
        g.getNextToken(buffer, position); // space
        assertTrue(g.getNextToken(buffer, position).getToken().equals("line"));
    }

}
