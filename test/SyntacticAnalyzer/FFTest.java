package SyntacticAnalyzer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class FFTest {

    FF ff;

    @Before
    public void setUp() throws Exception {
        HashMap<Integer, Rule> rules = new HashMap<Integer, Rule>() {{
            put(1, new Rule("E", "b c"));
            put(2, new Rule("D", "e f"));
        }};
        ff = new FF(rules);
    }

    @After
    public void tearDown() throws Exception {
        ff = null;

    }

    @Test
    public void testGet() throws Exception {
        assertTrue(ff.add("E", "D"));
        assertTrue(ff.get("E").size() == 1);
        assertTrue(ff.get("E").get(0).equals("D"));


        assertTrue(ff.add("D", new ArrayList<String>() {{
                add("b");
                add("c");
        }}));

        assertTrue(ff.get("D").size() == 2);
        assertTrue(ff.get("D").get(0).equals("b"));

        assertFalse(ff.add("F", "b"));

    }
}