package LexicalAnalyzer.DFA;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Class to test the Part of Speech object
 */
public class POSTest {

    private POS pos;

    @Before
    public void setUp() throws Exception {
        pos = new POS("test", Token.ID);

    }

    @After
    public void tearDown() throws Exception {
        pos = null;

    }

    @Test
    public void testGetToken() throws Exception {
        assertEquals("test", pos.getToken());

    }

    @Test
    public void testGetType() throws Exception {
        assertEquals(Token.ID, pos.getType());
    }

}