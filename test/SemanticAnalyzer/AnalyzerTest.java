package SemanticAnalyzer;

import LexicalAnalyzer.DFA.HelperTest;
import SyntacticAnalyzer.Grammar;
import SyntacticAnalyzer.Tuple;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class AnalyzerTest {
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
                "class test {" +
                "   int id;" +
                "   int id;" +
                "};" +
                "program{};" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 1);
        assertTrue(tuple.getX().get(0) instanceof AlreadyDeclaredException);

        test = "" +
                "class test {" +
                "};" +
                "class test {" +
                "};" +
                "program{};" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 1);
        assertTrue(tuple.getX().get(0) instanceof AlreadyDeclaredException);

        test = "" +
                "class test {"+
                "   int test;"+
                "};"+
                "program{" +
                "   int test;" +
                "   test = 5;" +
                "};"+
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 0);
    }


    @Test
    public void testSemanticTwo() throws Exception {
        String test = "" +
                        "program{" +
                "           int findMin;" +
                "           finMin = findMin();" +

                "       };" +
                "       int findMin() {" +
                "           float findMin2;" +
                "       };" +
                        "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 0);

        test = "" +
                "class id { " +
                "   id id;" +
                "};" +
                "program{};" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 0);


        test = "" +
                "class id { " +
                "   id id;" +
                "};" +
                "program {" +
                "   int id;" +
                "   b = id(); " +
                "};" +
                "int id(){};" +
                "int id(){};" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 1);
        assertTrue(tuple.getX().get(0) instanceof AlreadyDeclaredException);

    }
}