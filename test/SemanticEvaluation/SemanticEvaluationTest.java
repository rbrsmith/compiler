package SemanticEvaluation;


import CodeGeneration.CodeGenerator;
import LexicalAnalyzer.DFA.HelperTest;
import SemanticAnalyzer.AlreadyDeclaredException;
import SemanticAnalyzer.UndeclardException;
import SyntacticAnalyzer.Grammar;
import SyntacticAnalyzer.Tuple;
import com.sun.org.apache.bcel.internal.classfile.Code;
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
        CodeGenerator.getInstance().deactivate();
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
    @Test
    public void testSemanticFive() throws Exception {
        String test = "" +
                "  class foo {\n" +
                "        int var1;\n" +
                "        int var2;\n" +
                "    };\n" +
                "    class bar {\n" +
                "        int var1;\n" +
                "        int var2;\n" +
                "    };\n" +
                "\n" +
                "    program {\n" +
                "        foo bar;\n" +
                "        bar foo;\n" +
                "        int c;\n" +
                "        bar.var1 = 5;\n" +
                "        foo.var1 = bar.var1;\n" +
                "        bar.var2 = foo.var1;\n" +
                "        foo.var2 = bar.var2;\n" +
                "        c = (foo.var2) + 4;\n" +
                "    };\n" +
                "    $ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 0);
    }

    @Test
    public void testSemanticSix() throws Exception {
        String test = "" +
                "      program {" +
                "           int a;" +
                "           a = 5 + 5.0;" +
                "       };" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 1);
        assertTrue(tuple.getX().get(0) instanceof BadOpException);
    }

    @Test
    public void testSemanticSeven() throws Exception {
        String test = "" +
                "      program {" +
                "           int a;" +
                "           a = 5 + random();" +
                "       };" +
                "       float random() {" +
                "       };" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 1);
        assertTrue(tuple.getX().get(0) instanceof BadOpException);
    }
    @Test
    public void testSemanticEight() throws Exception {
        String test = "" +
                "     class foo {" +
                "           float rand() {};" +
                "       };" +
                "      program {" +
                "           int a;" +
                "           foo b;" +
                "           a = 5 + b.rand();" +
                "       };" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 1);
        assertTrue(tuple.getX().get(0) instanceof BadOpException);
    }

    @Test
    public void testSemanticNine() throws Exception {
        String test = "" +
                "   class foo {" +
                "       float ran() {};  " +
                "   };" +
                "      program {" +
                "           int a;" +
                "           foo cls;" +
                "           a = cls.ran();" +
                "       };" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 1);
        assertTrue(tuple.getX().get(0) instanceof InvalidTypesException);
    }

    @Test
    public void testSemanticTen() throws Exception {
        String test = "" +
                "      program {" +
                "           int a;" +
                "           int b;" +
                "           a = b;" +
                "       };" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 1);
        assertTrue(tuple.getX().get(0) instanceof UninitializedException);
    }

    @Test
    public void testSemanticEleven() throws Exception {
        String test = "class foo {" +
                "           int var1;" +
                "       };" +
                "      program {" +
                "           int a;" +
                "           foo cls;" +
                "           a = 5 + cls.var1;" +
                "       };" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 1);
        assertTrue(tuple.getX().get(0) instanceof UninitializedException);
    }

    @Test
    public void testSemanticTwelve() throws Exception {
        String test = "class foo {" +
                "           int var1;" +
                "       };" +
                "      program {" +
                "           int a;" +
                "           foo cls;" +
                "           cls.var1 = 2.0;" +
                "           a = 5 + cls.var1;" +
                "       };" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 1);
        assertTrue(tuple.getX().get(0) instanceof InvalidTypesException);
    }


    @Test
    public void testSemanticThirteen() throws Exception {
        String test = "class foo {" +
                "           int var1;" +
                "       };" +
                "      program {" +
                "           int a;" +
                "           foo cls;" +
                "           cls.var1 = 2;" +
                "           a = 5 + cls.var1;" +
                "       };" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 0);
    }

    @Test
    public void testSemanticFourteen() throws Exception {
        String test = ""+
                "      program {" +
                "           int a;" +
                "           a = getVar() + getVar();"+
                "       };" +
                "       int getVar(){};" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 0);
    }


    @Test
    public void testSemanticFifteen() throws Exception {
        String test = ""+
                "      program {" +
                "           int a;" +
                "           a = getVar() + getVar();"+
                "       };" +
                "       int getVar(int b){};" +
                "       int getVal(){};" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 1);
        assertTrue(tuple.getX().get(0) instanceof InvalidFunctionParamsException);
    }

    @Test
    public void testSemanticSixteen() throws Exception {
        String test = ""+
                "      program {" +
                "           int a;" +
                "           a = getVar(getVal()) / getVar(getVal());"+
                "       };" +
                "       int getVar(int b){};" +
                "       int getVal(){};" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 0);
    }

    @Test
    public void testSemanticSeventeen() throws Exception {
        String test = ""+
                "      program {" +
                "           int a;" +
                "           a = getVar(1,2,3,4,5,6,7,8,9);"+
                "       };" +
                "       int getVar(int a, int b, int c, int d, int e, int f, int g, int h, int i){};" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 0);
    }

    @Test
    public void testSemanticEighteen() throws Exception {
        String test = ""+
                "       class foo {" +
                "           int method(int a, int b, int c){};" +
                "       };" +
                "      program {" +
                "           int a;" +
                "           foo cls;\n" +
                "           a = foo.method(1,2,3);"+
                "       };" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 1);
        assertTrue(tuple.getX().get(0) instanceof UndeclardException);
    }

    @Test
    public void testSemanticNinteen() throws Exception {
        String test = ""+
                "       class foo {" +
                "           int method(int a, int b, int c){};" +
                "       };" +
                "      program {" +
                "           int a;" +
                "           foo cls;\n" +
                "           a = cls.method(1,2,3);"+
                "       };" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 0);
    }

    @Test
    public void testSemanticTwenty() throws Exception {
        String test = ""+
                "       class foo {" +
                "           int method(float a, int b, int c){};" +
                "       };" +
                "      program {" +
                "           int a;" +
                "           int e;" +
                "           foo cls;" +
                "           e = 3;\n" +
                "           a = cls.method(e,2,3);"+
                "       };" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 1);
        assertTrue(tuple.getX().get(0) instanceof InvalidTypesException);
    }
    @Test
    public void testSemanticTwentyOne() throws Exception {
        String test = ""+
                "      program {" +
                "           int a;" +
                "           int b;" +
                "           b = 2;" +
                "           a = method(2,2,3);"+
                "       };" +
                "\n" +
                "int method(float a, int b, int c){};" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 1);

    }

    @Test
    public void testSemanticTwentyTwo() throws Exception {
        String test = ""+
                "      program {" +
                "           float a;" +
                "           a = method(2,2,3);"+
                "       };" +
                "\n" +
                "int method(int a, int b, int c){};" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 1);
        assertTrue(tuple.getX().get(0) instanceof InvalidTypesException);

    }

    @Test
    public void testSemanticTwentyThree() throws Exception {
        String test = "class foo {" +
                "           int random(int a, int b, int c) {" +
                "           };" +
                "       };"+
                "      program {" +
                "           float a;" +
                "           foo cls;" +
                "           a = cls.random(2,2,3);"+
                "       };" +
                "\n" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 1);
        assertTrue(tuple.getX().get(0) instanceof InvalidTypesException);

    }

    @Test
    public void testSemanticTwentyFour() throws Exception {
        String test = "class foo {" +
                "           int random(int a, int b, int c) {" +
                "           };" +
                "       };"+
                "      program {" +
                "           float a;" +
                "           int b;" +
                "           foo cls;" +
                "           a = 2.0;" +
                "           b = cls.random(2,2,3) + a;"+
                "       };" +
                "\n" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 1);
        assertTrue(tuple.getX().get(0) instanceof BadOpException);
    }


    @Test
    public void testSemanticTwentyFive() throws Exception {
        String test = "class foo {" +
                "          int var1;" +
                "       };"+
                "      program {" +
                "           float a;" +
                "           int b;" +
                "           foo cls;" +
                "           cls.var1  = 2;" +
                "           a = 2.0;" +
                "           b = cls.var1 + a;"+
                "       };" +
                "\n" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 1);
        assertTrue(tuple.getX().get(0) instanceof BadOpException);
    }

}