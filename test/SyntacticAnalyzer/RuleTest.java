package SyntacticAnalyzer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RuleTest {

    public Rule rule;
    public Rule rule2;

    @Before
    public void setUp() throws Exception {
        rule = new Rule("E", "T F");
        rule2 = new Rule("F", "+ num");

    }

    @After
    public void tearDown() throws Exception {
        rule = null;
    }

    @Test
    public void testRule() throws Exception {
        System.out.println(rule);
        System.out.println(rule2);
    }

}