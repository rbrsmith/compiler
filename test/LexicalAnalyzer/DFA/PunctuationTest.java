package LexicalAnalyzer.DFA;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

import static junit.framework.TestCase.assertTrue;


/**
 * Class to test punctuation
 */
public class PunctuationTest {

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
    public void semiColonTest() throws Exception {
        String test = " ; ";
        file = ht.makeFile(test);
        ArrayList<POS> tags = dfa.getTags(file);
        assertTrue(tags.size() == 3);
        assertTrue(tags.get(1).getToken().equals(";"));
        assertTrue(tags.get(1).getType() == Token.SEMICOLON);
        ht.removeFile(file);


    }


    @Test
    public void commaTest() throws Exception {
        String test = " , ";
        file = ht.makeFile(test);
        ArrayList<POS> tags = dfa.getTags(file);
        assertTrue(tags.size() == 3);
        assertTrue(tags.get(1).getToken().equals(","));
        assertTrue(tags.get(1).getType() == Token.COMMA);
        ht.removeFile(file);

    }


    @Test
    public void spaceTest() throws Exception {
        String test = "   ";
        file = ht.makeFile(test);
        ArrayList<POS> tags = dfa.getTags(file);
        assertTrue(tags.size() == 3);
        assertTrue(tags.get(0).getToken().equals(" "));
        assertTrue(tags.get(0).getType() == Token.SPACE);
        ht.removeFile(file);

    }

    @Test
    public void newLineTest() throws Exception {
        String test = "\n";
        file = ht.makeFile(test);
        ArrayList<POS> tags = dfa.getTags(file);
        assertTrue(tags.size() == 1);
        assertTrue(tags.get(0).getToken().equals(test));
        assertTrue(tags.get(0).getType() == Token.LINE_FEED);
        ht.removeFile(file);

        test = "\r";
        file = ht.makeFile(test);
        tags = dfa.getTags(file);
        assertTrue(tags.size() == 1);
        assertTrue(tags.get(0).getToken().equals(test));
        assertTrue(tags.get(0).getType() == Token.CARRIAGE_RETURN);
        ht.removeFile(file);

    }

    @Test
    public void tabTest() throws Exception {
        String test = "\t";
        file = ht.makeFile(test);
        ArrayList<POS> tags = dfa.getTags(file);
        assertTrue(tags.size() == 1);
        assertTrue(tags.get(0).getToken().equals(test));
        assertTrue(tags.get(0).getType() == Token.TAB);
        ht.removeFile(file);
    }

}

