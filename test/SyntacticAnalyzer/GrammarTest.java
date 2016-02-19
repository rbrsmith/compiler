package SyntacticAnalyzer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GrammarTest {

    Grammar g;

    @Before
    public void setUp() throws Exception {
        g = new Grammar();
    }

    @After
    public void tearDown() throws Exception {
        g = null;
    }

    @Test
    public void testGrammar() throws Exception {
        g.parse("C:\\Users\\b0467851\\IdeaProjects\\compilers\\src\\Grammar2.txt");

        g.getFirsts();
        g.getFollows();
        g.buildParseTable();



        System.out.println(g);

        g.LL();

    }

}