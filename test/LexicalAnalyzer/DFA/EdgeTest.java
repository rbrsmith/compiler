package LexicalAnalyzer.DFA;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Class to test graphs edges
 */
public class EdgeTest {

    Edge edge;

    @Before
    public void setUp() throws Exception {
        edge = new Edge(new Node(1), new Node(2), Lexicon.LETTER);
    }

    @After
    public void tearDown() throws Exception {
        edge = null;
    }

    @Test
    public void testGetDest() throws Exception {
        assertEquals(edge.getDest(), new Node(2));
        assertNotEquals(edge.getDest(), new Node(1));
    }

    @Test
    public void testGetSource() throws Exception {
        assertNotEquals(edge.getDest(), edge.getSource());
        assertEquals(edge.getSource(), new Node(1));
    }

    @Test
    public void testGetToken() throws Exception {
        assertEquals(Lexicon.LETTER, edge.getToken());

    }
}