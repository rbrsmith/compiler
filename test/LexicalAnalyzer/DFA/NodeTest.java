package LexicalAnalyzer.DFA;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NodeTest  {

    private Node node;
    private Node leaf;

    @Before
    public void setUp() {
        node = new Node(0);
        leaf = new Node(-1, true, Token.ID);
    }

    @Test
    public void testGetID() throws Exception {
        assertTrue(node.getID() == 0);
        assertTrue(leaf.getID() == -1);
    }

    @Test
    public void testIsLeaf() throws Exception {
        assertFalse(node.isLeaf());
        assertTrue(leaf.isLeaf());
    }

    @Test
    public void testGetType() throws Exception {
        assertNull(node.getType());
        assertEquals(leaf.getType(), Token.ID);
        assertNotEquals(leaf.getType(), Token.AND);
    }

    @Test
    public void testEquals() throws Exception {
        assertNotEquals(node, leaf);
        assertEquals(new Node(5), new Node(5));
        assertEquals(new Node(6, true, Token.OR), new Node(6, true, Token.OR));
        assertNotEquals(new Node(7, true, Token.AND), new Node(7, true, Token.SPACE));

    }

    @After
    public void tearDown() {
        node = null;
        leaf = null;
    }
}