package SyntacticAnalyzer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RuleTest {

    private Rule rule;
    private Integer ruleid;

    @Before
    public void setUp() throws Exception {
        rule = new Rule("E", "b c");
        ruleid = rule.getID();
    }

    @After
    public void tearDown() throws Exception {
        rule = null;
    }

    @Test
    public void testGetLHS() throws Exception {
        assertTrue(rule.getLHS().equals("E"));
    }

    @Test
    public void testGetID() throws Exception {
        // Make sure adding a new rule does not change rule id
        Rule rule2 = new Rule("F", "g h");

        assertTrue(rule.getID().compareTo(ruleid) == 0);

    }

    @Test
    public void testGetRHS() throws Exception {
        assertTrue(rule.getRHS().size() == 2);
        assertTrue(rule.getRHS().contains("b"));
        assertTrue(rule.getRHS().contains("c"));
    }
}