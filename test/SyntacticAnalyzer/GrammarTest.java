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
        g.parse(new File("/home/ross/Dropbox/IdeaProjects/CompilerDesign/src/Grammar.txt"));
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

    @Test
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
                "   int test3 " +
                "} ; " +
                "long test4 ( int test5 , int test6 )" +
                " { } ;" +  
                "$";
        run(test);
    }

}