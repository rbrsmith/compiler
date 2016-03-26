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
                "           findMin = findMin();" +

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
                "   int id; int b;" +
                "   b = id(); " +
                "};" +
                "int id(){};" +
                "int id(){};" +

                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 1);
        assertTrue(tuple.getX().get(0) instanceof AlreadyDeclaredException);
    }


    @Test
    public void testSemanticThree() throws Exception {
        String test = "" +
                "program{" +
                "   int a; " +
                "   b = 5;" +
                "};" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 1);
        assertTrue(tuple.getX().get(0) instanceof UndeclardException);

        test = "" +
                "class a {" +
                "   int b;" +
                "};" +
                "program {" +
                "   a foo;" +
                "   foo.b = 4;" +
                "}; $ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 0);

        test = "" +
                "class a {" +
                "   int b;" +
                "};" +
                "program {" +
                "   a foo;" +
                "   foo.c = 4;" +
                "}; $ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 1);
        assertTrue(tuple.getX().get(0) instanceof UndeclardException);

    }

        @Test
        public void testSemanticFour() throws Exception {
            String test = "" +
                    "class a {" +
                    "   int b;" +
                    "};" +
                    "program {" +
                    "   bar foo;" +
                    "}; $ ";
            tuple = run(test);
            assertTrue(tuple.getX().size() == 1);
            assertTrue(tuple.getX().get(0) instanceof UndeclardException);


            test = "" +
                    "class a {" +
                    "   int b[9];" +
                    "};" +
                    "program {" +
                    "   a foo;" +
                    "   foo.b[8] = 5;" +
                    "}; $ ";
            tuple = run(test);
            assertTrue(tuple.getX().size() == 0);


            test = "" +
                    "class a {" +
                    "   int b[9];" +
                    "};" +
                    "program {" +
                    "   a foo;" +
                    "   foo.b[10] = 5;" +
                    "}; $ ";
            tuple = run(test);
            assertTrue(tuple.getX().size() == 1);
            assertTrue(tuple.getX().get(0) instanceof UndeclardException);
        }
    @Test
    public void testSemanticFive() throws Exception {

        String test = "" +
                "class a {" +
                "   int b[9];" +
                "};" +
                "program {" +
                "}; " +
                "float a() {" +
                "   a b;" +
                "   b.b = 4;" +
                "   b.c = 5;" +
                "};$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 1);
        assertTrue(tuple.getX().get(0) instanceof UndeclardException);



        test = "" +
                "class a {" +
                "};" +
                "program {" +
                "}; " +
                "float a() {" +
                "};" +
                "float a() {" +
                "}; $ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 1);
        assertTrue(tuple.getX().get(0) instanceof AlreadyDeclaredException);

        test = "class a{}; " +
                "program{};" +
                "int a(int b, int c) {};" +
                "int d(int e, int f, int g, int h, int i) {" +
                "   int f;" +
                "   int g;" +
                "   int h;" +
                "   int i;" +
                "}; $ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 4);
        assertTrue(tuple.getX().get(0) instanceof AlreadyDeclaredException);
        assertTrue(tuple.getX().get(1) instanceof AlreadyDeclaredException);
        assertTrue(tuple.getX().get(2) instanceof AlreadyDeclaredException);
        assertTrue(tuple.getX().get(3) instanceof AlreadyDeclaredException);

    }
}