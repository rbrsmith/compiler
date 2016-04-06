package SemanticEvaluation;


import LexicalAnalyzer.DFA.HelperTest;
import SemanticAnalyzer.AlreadyDeclaredException;
import SemanticAnalyzer.UndeclardException;
import SyntacticAnalyzer.Grammar;
import SyntacticAnalyzer.Tuple;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class SemanticEvaluationTest {
    Grammar g;
    File file;
    HelperTest ht;
    Tuple<ArrayList<Exception>, ArrayList<ArrayList<String>>> tuple;


    @Before
    public void setUp() throws Exception {
        g = new Grammar(System.getProperty("user.dir") + File.separator + "src" + File.separator + "Grammar.txt");
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
    public void testSemanticOne() throws Exception {
        String test = "" +
                "program {\n" +
                "\tint c;\n" +
                "\tint d;\n" +
                "//\tc = 4;\n" +
                "\tget(d);\n" +
                "};\n" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 0);
    }

    @Test
    public void testSemanticTwo() throws Exception {
        String test = "" +
                "program {\n" +
                "\tint c[2];\n" +
                "\tint d;\n" +
                "\tc = 4;\n" +
                "};\n" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 1);
        assertTrue(tuple.getX().get(0) instanceof UndeclardException);
    }

    @Test
    public void testSemanticThree() throws Exception {
        String test = "" +
                "program {" +
                "   get(c);" +
                "};\n" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 1);
        assertTrue(tuple.getX().get(0) instanceof UndeclardException);
    }
    @Test
    public void testSemanticFour() throws Exception {
        String test = "" +
                "program {" +
                "   int c;" +
                "   get(c);" +
                "};\n" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 0);
    }


}