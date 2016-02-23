package LexicalAnalyzer.DFA;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

import static junit.framework.TestCase.assertTrue;

/**
 * Class to test operators
 */
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


        test = " >< ";
        file = ht.makeFile(test);
        tags = dfa.getTags(file);
        assertTrue(tags.size() == 2);
        assertTrue(tags.get(0).getToken().equals(">"));
        assertTrue(tags.get(0).getType().equals(Token.GREATER_THAN));


        assertTrue(tags.get(1).getToken().equals("<"));
        assertTrue(tags.get(1).getType().equals(Token.LESS_THAN));

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
        assertTrue(tags.get(0).getType() == Token.GREATER_THAN);
        ht.removeFile(file);
    }

    @Test
    public void lessThanEqualTest() throws Exception {
        String test = "<=";
        file = ht.makeFile(test);
        ArrayList<POS> tags = dfa.getTags(file);
        assertTrue(tags.size() == 1);
        assertTrue(tags.get(0).getToken().equals("<="));
        assertTrue(tags.get(0).getType() == Token.LESS_THAN_EQUALS);
        ht.removeFile(file);
    }

    @Test
    public void greaterThanEqualTest() throws Exception {
        String test = ">=";
        file = ht.makeFile(test);
        ArrayList<POS> tags = dfa.getTags(file);
        assertTrue(tags.size() == 1);
        assertTrue(tags.get(0).getToken().equals(">="));
        assertTrue(tags.get(0).getType() == Token.GREATER_THAN_EQUALS);
        ht.removeFile(file);
    }



    @Test
    public void subtractionTest() throws Exception {
        String test = " - ";
        file = ht.makeFile(test);
        ArrayList<POS> tags = dfa.getTags(file);
        assertTrue(tags.size() == 1);
        assertTrue(tags.get(0).getToken().equals("-"));
        assertTrue(tags.get(0).getType() == Token.SUBTRACTION);
        ht.removeFile(file);
    }

    @Test
    public void multiplyTest() throws Exception {
        String test = " * ";
        file = ht.makeFile(test);
        ArrayList<POS> tags = dfa.getTags(file);
        assertTrue(tags.size() == 1);
        assertTrue(tags.get(0).getToken().equals("*"));
        assertTrue(tags.get(0).getType() == Token.MULTIPLICATION);
        ht.removeFile(file);
    }

    @Test
    public void divideTest() throws Exception {
        String test = " / ";
        file = ht.makeFile(test);
        ArrayList<POS> tags = dfa.getTags(file);
        assertTrue(tags.size() == 1);
        assertTrue(tags.get(0).getToken().equals("/"));
        assertTrue(tags.get(0).getType() == Token.DIVISION);
        ht.removeFile(file);
    }

    @Test
    public void assignTest() throws Exception {
        String test = "=";
        file = ht.makeFile(test);
        ArrayList<POS> tags = dfa.getTags(file);
        assertTrue(tags.size() == 1);
        assertTrue(tags.get(0).getToken().equals(test));
        assertTrue(tags.get(0).getType() == Token.ASSIGNMENT);
        ht.removeFile(file);

    }


    @Test
    public void andTest() throws Exception {
        String test = "&&";
        file = ht.makeFile(test);
        ArrayList<POS> tags = dfa.getTags(file);
        assertTrue(tags.size() == 1);
        assertTrue(tags.get(0).getToken().equals(test));
        assertTrue(tags.get(0).getType() == Token.AND);
        ht.removeFile(file);

    }


    @Test
    public void orTest() throws Exception {
        String test = "||";
        file = ht.makeFile(test);
        ArrayList<POS> tags = dfa.getTags(file);
        assertTrue(tags.size() == 1);
        assertTrue(tags.get(0).getToken().equals(test));
        assertTrue(tags.get(0).getType() == Token.OR);
        ht.removeFile(file);

    }

}

