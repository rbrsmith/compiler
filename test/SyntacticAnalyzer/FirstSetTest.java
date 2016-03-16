package SyntacticAnalyzer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class FirstSetTest {

    private FirstSet fs;

    @Before
    public void setUp() throws Exception {
        HashMap<Integer, Rule> rules = new HashMap<Integer, Rule>() {{
            put(1, new Rule("E", "a B"));
            put(2, new Rule("B", "c d"));
        }};

        fs = new FirstSet(rules);
    }

    @After
    public void tearDown() throws Exception {
        fs = null;
    }

    @Test
    public void testFirst() throws Exception {
        // Test terminals have a first set of themselves
        assertTrue(fs.get("a").size() == 1);
        assertTrue(fs.get("a").get(0).equals("a"));

        // Test non terminals have an empty array list
        assertTrue(fs.get("E").size() == 0);


    }
}