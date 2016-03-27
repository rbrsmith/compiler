package SemanticAnalyzer;

import LexicalAnalyzer.DFA.Position;
import SyntacticAnalyzer.Tuple;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by ross on 26/03/16.
 */
public class NodeTest {

    @Test
    public void testIsLeaf() throws Exception {
        Node n = new Node("test", false, false, null, new Position());
        assertFalse(n.isLeaf());
        n = new Node(new Tuple("test", "test"), true, false, null, new Position());
        assertTrue(n.isLeaf());

    }

    @Test
    public void testIsRoot() throws Exception {

        Node n = new Node("test", false, false, null, new Position());
        assertFalse(n.isRoot());
        n = new Node(new Tuple("test", "test"), true, true, null, new Position());
        assertTrue(n.isRoot());
    }

    @Test
    public void testGetValue() throws Exception {
        Node n = new Node("test", false, false, null, new Position());
        assertTrue(n.getValue().equals("test"));
        n = new Node(new Tuple("test", "test"), true, true, null, new Position());
        assertTrue(n.getValue() == null);

    }

    @Test
    public void testGetLeafValue() throws Exception {
        Node n = new Node("test", false, false, null, new Position());
        assertTrue(n.getLeafValue() == null);
        n = new Node(new Tuple("testx", "testy"), true, true, null, new Position());
        assertTrue(n.getLeafValue().getX().equals("testx"));
        assertTrue(n.getLeafValue().getY().equals("testy"));
    }

    @Test
    public void testGetParent() throws Exception {
        Node n = new Node("test", false, false, null, new Position());
        assertNull(n.getParent());
        Node n2 = new Node("test", false, false, n, new Position());
        assertEquals(n.getNodeID(), n2.getParent().getNodeID());
    }

    @Test
    public void testGetNodeID() throws Exception {
        Node n = new Node("test", false, false, null, new Position());
        Node n2 = new Node("test", false, false, null, new Position());
        assertEquals(n.getNodeID() + 1, n2.getNodeID());
    }

    @Test
    public void testAddChild() throws Exception {
        Node n = new Node("test", false, false, null, new Position());
        Node n2 = new Node("test", false, false, n, new Position());
        n.addChild(n2);
        assertTrue(n.getFirstChild().getNodeID() == n2.getNodeID());
    }

    @Test
    public void testGetChild() throws Exception {

        Node n = new Node("test1", false, false, null, new Position());
        Node n2 = new Node("test2", false, false, n, new Position());
        n.addChild(n2);
        assertTrue(n.getChild(n2.getNodeID()).getValue().equals("test2"));
    }

    @Test
    public void testGetPosition() throws Exception {
        Position pos = new Position();
        pos.newLine();pos.newLine();pos.incChar();
        Node n = new Node("test", false, false, null, pos);
        assertTrue(n.getPosition().getLine() == 3);
        assertTrue(n.getPosition().getChar() == 1);
    }

    @Test
    public void testGetLeftSibling() throws Exception {
        Node n = new Node("parent", false, false, null, new Position());
        Node l = new Node("left", false, false, n, new Position());
        n.addChild(l);
        Node r = new Node("right", false, false, n, new Position());
        n.addChild(r);
        assertTrue(r.getLeftSibling().getNodeID() == l.getNodeID());


        n = new Node("parent", false, false, null, new Position());
        l = new Node("left", false, false, n, new Position());
        n.addChild(l);
        r = new Node("right", false, false, n, new Position());
        n.addChild(r);
        Node r2 = new Node("right2", false, false, r, new Position());
        r.addChild(r2);

        assertTrue(r2.getLeftSibling().getNodeID() == l.getNodeID());
    }

    @Test
    public void testGetFirstChild() throws Exception {
        Node n = new Node("parent", false, false, null, new Position());
        Node l = new Node("left", false, false, n, new Position());
        n.addChild(l);
        Node r = new Node("right", false, false, n, new Position());
        n.addChild(r);
        assertTrue(n.getFirstChild().getNodeID() == l.getNodeID());


    }

    @Test
    public void testGetLeaf() throws Exception {
        Node n = new Node("parent", false, false, null, new Position());
        Node l = new Node("left", true, false, n, new Position());
        n.addChild(l);
        Node r = new Node("right", false, false, n, new Position());
        n.addChild(r);
        assertTrue(n.getLeaf().getNodeID() == l.getNodeID());


        n = new Node("parent", false, false, null, new Position());
        l = new Node("left", false, false, n, new Position());
        n.addChild(l);
        r = new Node("right", false, false, n, new Position());
        n.addChild(r);
        Node r2 = new Node("right2", true, false, r, new Position());
        r.addChild(r2);

        assertTrue(r.getLeaf().getNodeID() == r2.getNodeID());

    }

    @Test
    public void testGetFirstLeafType() throws Exception {

        Node n = new Node("parent", false, false, null, new Position());
        Node l = new Node("left", false, false, n, new Position());
        n.addChild(l);
        Node r = new Node("right", false, false, n, new Position());
        n.addChild(r);
        Node r2 = new Node(new Tuple("RightX", "RightY"), true, false, r, new Position());
        r.addChild(r2);

        assertTrue(r.getFirstLeafValue().equals("RightY"));
        assertTrue(r.getFirstLeafType().equals("RightX"));

    }

    @Test
    public void testGetTokens() throws Exception {

        Node n = new Node("parent", false, true, null, new Position());
        Node l = new Node(new Tuple("L1X", "L1Y"), true, false, n, new Position());
        n.addChild(l);
        Node r = new Node("right", false, false, n, new Position());
        n.addChild(r);
        Node r2 = new Node(new Tuple("R2X", "R2Y"), true, false, r, new Position());
        r.addChild(r2);
        System.out.println(l.getTokens());


        assertTrue(l.getTokens().size() == 2);

    }

    @Test
    public void testGetChildrenValues() throws Exception {

        Node n = new Node("parent", false, true, null, new Position());
        Node l = new Node(new Tuple("L1X", "L1Y"), true, false, n, new Position());
        n.addChild(l);
        Node r = new Node("right", false, false, n, new Position());
        n.addChild(r);
        Node r2 = new Node(new Tuple("R2X", "R2Y"), true, false, r, new Position());
        r.addChild(r2);
        assertTrue(n.getChildrenValues().size() == 2);

    }

    @Test
    public void testGetRightSibling() throws Exception {
        Node n = new Node("parent", false, true, null, new Position());
        Node l = new Node(new Tuple("L1X", "L1Y"), true, false, n, new Position());
        n.addChild(l);
        Node r = new Node("right", false, false, n, new Position());
        n.addChild(r);
        Node r2 = new Node(new Tuple("R2X", "R2Y"), true, false, r, new Position());
        r.addChild(r2);

        assertTrue(l.getRightSibling().getNodeID() == r.getNodeID());

    }
}