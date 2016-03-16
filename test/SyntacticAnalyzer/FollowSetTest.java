package SyntacticAnalyzer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.IncludeCategories;

import java.util.HashMap;

import static org.junit.Assert.*;

public class FollowSetTest {

    private FollowSet fs;

    @Before
    public void setUp() throws Exception {
        HashMap<Integer, Rule> rules = new HashMap<Integer, Rule>() {{
            put(1, new Rule("E", "a b"));
            put(2, new Rule("F", "c d"));
        }};
        fs = new FollowSet(rules, "START", "EOF");
    }

    @After
    public void tearDown() throws Exception {
        fs = null;
    }

    @Test
    public void testFS() throws Exception {
        assertTrue(fs.get("E").size() == 0);
        assertNull(fs.get("c"));

    }

    @Test
    public void testIsTerminal() throws Exception {
        assertTrue(fs.isTerminal("c"));
        assertFalse(fs.isTerminal("E"));

    }
}