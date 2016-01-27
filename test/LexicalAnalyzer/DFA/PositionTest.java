package LexicalAnalyzer.DFA;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PositionTest {

    Position pos;

    @Before
    public void setUp() throws Exception {
        pos = new Position();
    }

    @After
    public void tearDown() throws Exception {
        pos = null;

    }

    @Test
    public void testInit() throws Exception {
        assertEquals(pos.getChar(), 0);
        assertEquals(pos.getLine(), 1);
    }

    @Test
    public void testIncChar() throws Exception {
        pos.incChar();
        assertEquals(pos.getChar(), 1);

    }

    @Test
    public void testDecChar() throws Exception {
        pos.decChar();
        assertEquals(pos.getChar(), 0);

    }

    @Test
    public void testNewLine() throws Exception {
        pos.newLine();
        assertEquals(pos.getLine(), 2);
        assertEquals(pos.getChar(),0);
        pos.incChar();
        pos.newLine();
        assertEquals(pos.getLine(), 3);
        assertEquals(pos.getChar(), 0);

    }

}