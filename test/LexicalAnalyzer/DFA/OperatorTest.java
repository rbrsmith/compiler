package LexicalAnalyzer.DFA;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

public class OperatorTest {

    File file;
    DFA dfa;
    HelperTest ht;


    @Before
    public void setUp() throws Exception{
        dfa = new DFA();
        ht = new HelperTest();
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void equalsTest() throws Exception {
        String test = "==";
        file = ht.makeFile(test);
        ArrayList<POS> tags = dfa.getTags(file);
        assertTrue(tags.size() == 1);
        assertTrue(tags.get(0).getToken().equals(test));
        assertTrue(tags.get(0).getType() == Token.EQUALS);
        ht.removeFile(file);


    }

    @Test
    public void notEqualsTest() throws Exception {
        String test = "<>";
        file = ht.makeFile(test);
        ArrayList<POS> tags = dfa.getTags(file);
        assertTrue(tags.size() == 1);
        assertTrue(tags.get(0).getToken().equals(test));
        assertTrue(tags.get(0).getType() == Token.NOT_EQUALS);
        ht.removeFile(file);

        // TODO
//        test = " >< ";
//        file = ht.makeFile(test);
//        tags = dfa.getTags(file);
//        System.out.println(tags);
//        assertTrue(tags.size() == 0);
    }

    @Test
    public void lessThanTest() throws Exception {
        String test = " < ";
        file = ht.makeFile(test);
        ArrayList<POS> tags = dfa.getTags(file);
        assertTrue(tags.size() == 1);
        assertTrue(tags.get(0).getToken().equals("<"));
        assertTrue(tags.get(0).getType() == Token.LESS_THAN);
        ht.removeFile(file);
    }

    @Test
    public void greaterThanTest() throws Exception {
        String test = " > ";
        file = ht.makeFile(test);
        ArrayList<POS> tags = dfa.getTags(file);
        assertTrue(tags.size() == 1);
        assertTrue(tags.get(0).getToken().equals(">"));
        // TODO
//        assertTrue(tags.get(0).getType() == Token.NOT_EQUALS);
        ht.removeFile(file);
    }



}

