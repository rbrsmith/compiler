package LexicalAnalyzer.DFA;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

import static junit.framework.TestCase.assertTrue;

/**
 * Class to test comments
 */
public class CommentTest {

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
    public void commentTest() throws Exception {
        String test = "/*";
        file = ht.makeFile(test);
        ArrayList<POS> tags = dfa.getTags(file);
        assertTrue(tags.size() == 1);
        assertTrue(tags.get(0).getToken().equals(test));
        assertTrue(tags.get(0).getType() == Token.OPEN_COMMENT);
        ht.removeFile(file);

        test = "*/";
        file = ht.makeFile(test);
        tags = dfa.getTags(file);
        assertTrue(tags.size() == 1);
        assertTrue(tags.get(0).getToken().equals(test));
        assertTrue(tags.get(0).getType() == Token.CLOSE_COMMENT);
        ht.removeFile(file);


        test = "//";
        file = ht.makeFile(test);
        tags = dfa.getTags(file);
        assertTrue(tags.size() == 1);
        assertTrue(tags.get(0).getToken().equals(test));
        assertTrue(tags.get(0).getType() == Token.INLINE_COMMENT);
        ht.removeFile(file);
    }





}

