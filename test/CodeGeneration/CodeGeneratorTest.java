package CodeGeneration;

import LexicalAnalyzer.DFA.HelperTest;
import SyntacticAnalyzer.Grammar;
import SyntacticAnalyzer.Tuple;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class CodeGeneratorTest {

    Grammar g;
    File file;
    HelperTest ht;
    Tuple<ArrayList<Exception>, ArrayList<ArrayList<String>>> tuple;


    @Before
    public void setUp() throws Exception {
        g = new Grammar(System.getProperty("user.dir") + File.separator + "src" + File.separator + "Grammar.txt");
        g.getSemanticAnalyzer().getCode().reset();
        ht = new HelperTest();
    }

    @After
    public void tearDown() throws Exception {
        g = null;
        ht = null;
    }

    public Tuple run(String test) throws Exception {
        file = ht.makeFile(test);
        Tuple rtn = g.LL(file);
        ht.removeFile(file);
        return rtn;
    }

    @Test
    public void testCodeOne() throws Exception {
        String test = "" +
                "program {" +
                "   int a;" +
                "   int b;" +
                "   int c;" +
                "   a = 1;" +
                "   b = 2;" +
                "   c = a + b;" +
                "};" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 0);
        assertTrue(g.getSemanticAnalyzer().getCode().getExecutionSize() == 17);
    }

    @Test
    public void testCodeTwo() throws Exception {
        String test = "" +
                "program {" +
                "   int a;" +
                "   int b;" +
                "   int c;" +
                "   a = 1;" +
                "   b = 2;" +
                "   c = a - b;" +
                "};" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 0);
        assertTrue(g.getSemanticAnalyzer().getCode().getExecutionSize() == 17);
    }

    @Test
    public void testCodeThree() throws Exception {
        String test = "" +
                "program {" +
                "   int a;" +
                "   int b;" +
                "   int c;" +
                "   a = 1;" +
                "   b = 2;" +
                "   c = a / b;" +
                "};" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 0);
        assertTrue(g.getSemanticAnalyzer().getCode().getExecutionSize() == 17);


    }
    @Test
    public void testCodeFour() throws Exception {
        String test = "" +
                "program {" +
                "   int a;" +
                "   int b;" +
                "   int c;" +
                "   a = 1;" +
                "   b = 2;" +
                "   c = a * b;" +
                "};" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 0);
        assertTrue(g.getSemanticAnalyzer().getCode().getExecutionSize() == 17);
    }

}