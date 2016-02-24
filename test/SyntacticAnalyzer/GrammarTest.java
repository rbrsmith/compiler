package SyntacticAnalyzer;

import LexicalAnalyzer.DFA.HelperTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class GrammarTest {

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
    public void testGrammarOne() throws Exception {
        String test = "class ross {\n" +
                "    bob rman[5];\n" +
                "};\n" +
                "class amb{};\n" +
                "PROGRAM{};\n" +
                "$";
        run(test);
        tuple = run(test);
        assertTrue(tuple.getX().size() == 0);
        test = "class ross{\n" +
                "    int rman[5];\n" +
                "};\n" +
                "class amb{};\n" +
                "PROGRAM{};\n" +
                "$";
        run(test);
        tuple = run(test);
        assertTrue(tuple.getX().size() == 0);
        test = "class ross{\n" +
                "    long rman[5];\n" +
                "};\n" +
                "class amb{};\n" +
                "PROGRAM{};\n" +
                "$";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 0);
    }

    @Test
    public void testGrammarTwo() throws Exception {
        String test = "" +
                "class test { " +
                "   int test2 [ 95 ] ; " +
                "} ; \n" +
                "program { " +
                "   int test3 ; " +
                "} ; " +
                "long test4 ( int test5 , int test6 )" +
                " { } ;" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 0);
    }


    @Test
    public void testGrammarThree() throws Exception {
        String test = "" +
                "program " +
                "{ } " +
                "; " +
                "int test2 ( ) { test4 [ 55 ] = 65 ; } ;" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 0);
    }

    @Test
    public void testGrammarFour() throws Exception {
        String test = "program { } ; $";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 0);
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
        tuple = run(test);
        assertTrue(tuple.getX().size() == 0);
    }

    @Test
    public void testGrammarSix() throws Exception {
        String test = "PROGRAM { " +
                "           float test1 [ 4 ] [ 5 ]; test2 = 4;" +
                "       } ; $";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 0);
    }

    @Test
    public void testGrammarSeven() throws  Exception {
        String test = "class Utility {};" +
                "       program {};" +
                "       float randomize() {};" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 0);
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
                "$ ";

        tuple = run(test);
        assertTrue(tuple.getX().size() == 0);
    }

    @Test
    public void testGrammarNine() throws Exception {
        String test = "class Utility {" +
                        "int findMin(int array[100]) {" +
                "           int minValue;\n" +
                "           int idx;\n" +
                "           minValue = array[100];\n" +
                "           for( int idx = 1; idx <= 99; idx = ( idx ) + 1)\n" +
                "           {\n" +
                "               if(array[idx] < maxValue) then {\n" +
                                    "maxValue = array[idx];\n" +
                    "               } else{};\n" +
                "           };\n" +
                "           return (minValue);\n" +
                "           };" +
                    "}; program {};" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 0);
    }

    @Test
    public void testGrammar10() throws Exception {
        String test = "program {" +
                "       int sample[100];" +
                "       int idx;" +
                "       int minValue;" +
                "       int maxValue;" +
                "       Utility utility;" +
                "       Utility arrayUtility[2][3][6][7];" +
                "       for(int t = 0; t<=100; t = t+1) {" +
                "           get(sample[t]);" +
                "           sample[t] = (sample[t] * randomize());" +
                "       };"+
                   "    minValue =  utility.findMax(sample);"+
                   "    maxValue = utility.findMIn(sample);"+
                    "   utility.var1[4][0][0][0][0] = 10;" +
                "       put(maxValue);" +
                "       put(minValue);"+
                "}; $";

        tuple = run(test);
        assertTrue(tuple.getX().size() == 0);
    }

    @Test
    public void testGrammar11() throws Exception {
        String test = "program{};" +
                "       float randomize() {" +
                "           float value;" +
                "           vaule = 100 * (2+3.0/7.006);" +
                "           value = 1.05 + ((2.04*2.47) - 3.0) + 7.006;" +
                "       };" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 0);
    }

    @Test
    public void testGrammar12() throws Exception {

        String test = "" +
              "class Utility\n" +
                "{\n" +
                "int var1[4][5][7][8][9][1][0];\n" +
                "float var2;\n" +
                "int findMax(int array[100])\n" +
                "{\n" +
                "int maxValue;\n" +
                "int idx;\n" +
                "maxValue = array[100];\n" +
                "for( int idx = 99; idx > 0; idx = idx - 1 )\n" +
                "{\n" +
                "if(array[idx] > maxValue) then {\n" +
                "maxValue = array[idx];\n" +
                "}else{};\n" +
                "};\n" +
                "return (maxValue);\n" +
                "};\n" +
                "int findMin(int array[100])\n" +
                "{\n" +
                "int minValue;\n" +
                "int idx;\n" +
                "minValue = array[100];\n" +
                "for( int idx = 1; idx <= 99; idx = ( idx ) + 1)\n" +
                "{\n" +
                "if(array[idx] < maxValue) then {\n" +
                "maxValue = array[idx];\n" +
                "}else{};\n" +
                "};\n" +
                "return (minValue);\n" +
                "};\n" +
                "};\n" +
                "program {\n" +
                "int sample[100];\n" +
                "int idx;\n" +
                "int maxValue;\n" +
                "int minValue;\n" +
                "Utility utility;\n" +
                "Utility arrayUtility[2][3][6][7];\n" +
                "for(int t = 0; t<=100 ; t = t + 1)\n" +
                "{\n" +
                "get(sample[t]);\n" +
                "sample[t] = (sample[t] * randomize());\n" +
                "};\n" +
                "maxValue = utility.findMax(sample);\n" +
                "minValue = utility.findMin(sample);\n" +
                "utility. var1[4][1][0][0][0][0][0] = 10;\n" +
                "arrayUtility[1][1][1][1].var1[4][1][0][0][0][0][0] = 2;\n" +
                "put(maxValue);\n" +
                "put(minValue);\n" +
                "};\n" +
                "float randomize()\n" +
                "{\n" +
                "float value;\n" +
                "value = 100 * (2 + 3.0 / 7.0006);\n" +
                "value = 1.05 + ((2.04 * 2.47) - 3.0) + 7.0006 ;\n" +
                "return (value);\n" +
                "};"+
                  "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 0);
    }


}