package SyntacticAnalyzer;

import CodeGeneration.CodeGenerator;
import LexicalAnalyzer.DFA.HelperTest;
import SemanticAnalyzer.UndeclardException;
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
        CodeGenerator.getInstance().deactivate();
        ht = new HelperTest();
    }

    @After
    public void tearDown() throws Exception {
        g = null;
        ht = null;
        tuple = null;
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
                "    int rman[5];\n" +
                "};\n" +
                "class amb{};\n" +
                "PROGRAM{};\n" +
                "$";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 0);
    }
    @Test
    public void testGrammarOneA() throws Exception {
        String test = "class ross{\n" +
                "    int rman[5];\n" +
                "};\n" +
                "class amb{};\n" +
                "PROGRAM{};\n" +
                "$";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 0);
    }
    @Test
    public void testGrammarOneB() throws Exception {
        String test = "class ross{\n" +
                "    float rman[5];\n" +
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
                "float test4 ( int test5 , int test6 )" +
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
                "int test2 ( ) { " +
                "   int test4[56];" +
                "   test4 [ 55 ] = 65 ; " +
                "} ;" +
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
        String test = "" +
                "   class a {" +
                "       float test2;" +
                "   };" +
                "   PROGRAM { " +
                "       a test1; " +
                "       test1 . test2 = + 6.5 ;" +
                "        return (" +
                "            test1.test2  ) ;" +
                "       put ( 6.5 <> 6.5 + 6.5 ) ;" +
                "       }" +
                " ; $";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 0);
    }

    @Test
    public void testGrammarSix() throws Exception {
        String test = "PROGRAM { " +
                "           float test1 [ 4 ] [ 5 ]; " +
                "           int test2;" +
                "           test2 = 4;" +
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
                "               int id2 ;" +
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
                "           int minValue;" +
                "           int maxValue;\n" +
                "           int idx2;" +
                "           maxValue = 4;\n" +
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
        String test = "class Utility{" +
                "           int findMax(int a) {};" +
                "           int findMIn(int b) {};" +
                "}; program {" +
                "       int sample[100];" +
                "       int idx;" +
                "       int minValue;" +
                "       int maxValue;" +
                "       Utility utility;" +
                "       Utility arrayUtility[2][3][6][7];" +
                "       for(int t = 0; t<=100; t = t+1) {" +
                "           sample[t] = 1;" +
                "           get(sample[t]);" +
                "           sample[t] = (sample[t] * randomize());" +
                "       };"+
                   "    minValue =  utility.findMax(sample[0]);"+
                   "    maxValue = utility.findMIn(sample[0]);"+
                "       put(maxValue);" +
                "       put(minValue);"+
                "}; int randomize() {}; $ ";

        tuple = run(test);
        assertTrue(tuple.getX().size() == 0);
    }

    @Test
    public void testGrammar11() throws Exception {
        String test = "program{};" +
                "       float randomize() {" +
                "           float value;" +
                "           value = 100.0 * (2.0+3.0/7.006);" +
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
                "   int var1[4][5][7][8][9][1][0];\n" +
                "   float var2;\n" +
                "   int findMax(int array[100])\n" +
                "   {\n" +
                "       int maxValue;\n" +
                "       int idx;" +
                "       idx = 2;" +
                "       maxValue = 3;\n" +
                "       maxValue = array[100];\n" +
                "       for( int idx2 = 99; idx2 > 0; idx2 = idx2 - 1 )\n" +
                "       {\n" +
                "           if(array[idx] > maxValue) then {\n" +
                "               maxValue = array[idx];\n" +
                "           }else{};\n" +
                "       };\n" +
                "       return (maxValue);\n" +
                "   };\n" +
                "   int findMin(int array[100])\n" +
                "   {\n" +
                "       int minValue;\n" +
                "       int maxValue;\n" +
                "       int idx3;" +
                "       maxValue = 43;\n" +
                "       minValue = array[100];\n" +
                "       for( int idx = 1; idx <= 99; idx = ( idx ) + 1)\n" +
                "       {\n" +
                "           if(array[idx] < maxValue) then {\n" +
                "               maxValue = array[idx];\n" +
                "           }else{};\n" +
                "       };\n" +
                "       return (minValue);\n" +
                "   };\n" +
                "};\n" +
                "program {\n" +
                "   int sample[100];\n" +
                "   int idx;\n" +
                "   int maxValue;\n" +
                "   int minValue;\n" +
                "   Utility utility;\n" +
                "   Utility arrayUtility[2][3][6][7];\n" +
                "   for(int t = 0; t<=100 ; t = t + 1)\n" +
                "   {\n" +
                "       get(sample[t]);\n" +
                "       sample[t] = (sample[t] * randomize());\n" +
                "   };\n" +
                "   maxValue = utility.findMax(sample[0]);\n" +
                "   minValue = utility.findMin(sample[1]);\n" +
                "   utility. var1[4][1][0][0][0][0][0] = 10;\n" +
                "   arrayUtility[1][1][1][1].var1[4][1][0][0][0][0][0] = 2;\n" +
                "   put(maxValue);\n" +
                "   put(minValue);\n" +
                "};\n" +
                "int randomize()\n" +
                "{\n" +
                "   float value;" +
                "   int m;" +
                "   m = 4;\n" +
                "   value = 100.0 * (2.0 + 3.0 / 7.0006);\n" +
                "   value = 1.05 + ((2.04 * 2.47) - 3.0) + 7.0006 ;\n" +
                "   return (m);\n" +
                "};"+
                  "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 0);
    }

    @Test
    public void grammarTest13() throws Exception{
        String test = "class foo { int id(){}; };" +
                "" +
                "       class id {" +
                "           int id2; " +
                "           foo id3[9];" +
                "           int id( ) { " +
                "               if(  6 - id3 [8]. id ()  ==  id2 ) " +
                "               then { put(not 5); } " +
                "               else ; " +
                "           } ;" +
                "       };" +
                "program{};" +
                "$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 0);
    }


    @Test
    public void grammarTest14() throws Exception{
        String test = "class id {\n" +
                "           float id1 [ 5 ] ; \n" +
                "           int id2 [6];\n" +
                "           id id3 (long id36 [5][6][6] ,int id15 [5],long id16  ) {\n" +
                "               int id4 [1][2];\n" +
                "               for(long id5 = (5) <> (not 5);\n" +
                "                   8 == 8;\n" +
                "                   id2[0] = 5\n" +
                "               ){\n" +
                "                   return (7);\n" +
                "               }\n;" +
                "               return( id47 [5][4].  id85 [not 5] . id69() or + 5 );\n" +
                "           }\n;" +
                "           int id6() {}\n;" +
                "}\n;" +
                "class id12 {};\n" +
                "program{};\n" +
                "$ \n";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 3);
    }

    @Test
    public void grammarTest15() throws Exception{
        String test = "class id {" +
                "           int id[5];" +
                "}; program{}\n;" +
                "       int id1( )\n" +
                        "{" +
                "           id id2[5][4][3];" +
                "           int id3[2][3][4];" +
                "           id id7[5][5];" +
                "           id4[ +5 + not 5 + 5 ].id5[5][4][3].id[4] = id6[5][3].id( )\n== 5;" +
                "" +
                "" +
                "           if(5) then  else ;\n" +
                "           for(int id9 = 6;id9 < 7; id9 = i10d + 1);\n" +
                "            get(id7[5][5].id[4]);\n" +
                "           put(5);" +
                "           return(5 + 5 + 5 <> 5);" +
                "       }\n;" +
                "int i11d(){}; $ ";

        tuple = run(test);
        assertTrue(tuple.getX().size() == 2);
    }

    @Test
    public void failedGrammarTest1() throws Exception {
        String test = "program class {} ; $";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 1);
    }


    @Test
    public void failedGrammarTest2() throws Exception {
        String test = "program class { class } ; $";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 2);
    }

    @Test
    public void failedGrammarTest3() throws Exception {
        String test = "class id {};";
        tuple = run(test);
        // Impossible to parse as we are missing file end
        assertTrue(tuple.getX().size() == 1);
        assertTrue(tuple.getX().get(0).getMessage().contains("Unrecoverable"));
    }

    @Test
    public void failedGrammarTest4() throws Exception {
        String test = "class id {" +
                "       int idx" +
                "       int y;    " +
                "       };program{};$ ";
        tuple = run(test);
        // We reover on the third token - so 2 errors
        assertTrue(tuple.getX().size() == 2);
    }

    @Test
    public void failedGrammarTest5() throws Exception {
        String test = "class id {" +
                "           int idx[0][3][2][4][5][5" +
                "           int y;" +
                "           int x;" +
                "           int z;" +
                "           ];" +
                "}; program {};$ ";
        tuple = run(test);
        assertTrue(tuple.getX().size() == 9);

    }


}