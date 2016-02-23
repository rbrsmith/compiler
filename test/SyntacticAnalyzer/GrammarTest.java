package SyntacticAnalyzer;

import LexicalAnalyzer.DFA.HelperTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class GrammarTest {

    Grammar g;
    File file;
    HelperTest ht;


    @Before
    public void setUp() throws Exception {
        g = new Grammar();
    //    g.parse(new File("/home/ross/Dropbox/IdeaProjects/CompilerDesign/src/Grammar.txt"));
        g.parse(new File("C:\\Users\\b0467851\\IdeaProjects\\compilers\\src\\Grammar.txt"));
        ht = new HelperTest();
    }

    @After
    public void tearDown() throws Exception {
        g = null;
        ht = null;
    }

    public void run(String test) throws Exception {
        file = ht.makeFile(test);
        g.LL(file);
        ht.removeFile(file);
    }

//    @Test
    public void testGrammarOne() throws Exception {
        String test = "class ross{\n" +
                "    bob rman[5];\n" +
                "};\n" +
                "class amb{};\n" +
                "PROGRAM{};\n" +
                "$";
        run(test);
        test = "class ross{\n" +
                "    int rman[5];\n" +
                "};\n" +
                "class amb{};\n" +
                "PROGRAM{};\n" +
                "$";
        run(test);
        test = "class ross{\n" +
                "    long rman[5];\n" +
                "};\n" +
                "class amb{};\n" +
                "PROGRAM{};\n" +
                "$";
        run(test);
    }

    @Test
    public void testGrammarTwo() throws Exception {
        String test = "" +
                "class test { " +
                "   type test2 [ 95 ] ; " +
                "} ; \n" +
                "program { " +
                "   int test3 ; " +
                "} ; " +
                "long test4 ( int test5 , int test6 )" +
                " { } ;" +
                "$";
        run(test);
    }


    @Test
    public void testGrammarThree() throws Exception {
        String test = "" +
                "program " +
                "{ } " +
                "; " +
                "int test2 ( ) { test4 [ 55 ] = 65 ; } ;" +
                "$";
        run(test);
    }

    @Test
    public void testGrammarFour() throws Exception {
        String test = "program { } ; $";
        run(test);
    }

    @Test
    public void testGrammarFive() throws Exception {
        String test = "PROGRAM { " +
                "        test1 . test2 = + 6.5 ;" +
                "        return (" +
                "            id  ) ;" +
                "       put ( 6.5 <> 6.5 + 6.5 ) ;" +
                "       }" +
                " ; $";
        run(test);
    }

    @Test
    public void testGrammarSix() throws Exception {
        String test = "PROGRAM { " +
                "           float test1 [ 4 ] [ 5 ]; test2 = 4;" +
                "       } ; $";
        run(test);
    }

    @Test
    public void testGrammarSeven() throws  Exception {
        String test = "class Utility {};" +
                "       program {};" +
                "       float randomize() {};" +
                "$";

        run(test);
    }


    @Test
    public void testGrammarEight() throws  Exception {
        String test = "class Utility {" +
                "           int var1[4][5][6][7][8][9][1][0]; " +
                "           float var2;" +
                "           int findMax(int array[100]) {" +
                "               int maxValue ;" +
                "               int idx ;" +
                "               maxValue = array[100] ;" +
                "               for(int idx = 99; idx > 0; idx = idx - 1) {" +
                "                   if ( array [idx] > maxValue) then {" +
                "                       maxValue = array[idx] ;" +
                "                    } else { };" +
                "               };" +
                "               return ( maxValue );" +
                "           };  " +
                "" +
                "       };" +
                "       program {};" +
                "       float randomize() {};" +
                "$";

        run(test);
    }


}