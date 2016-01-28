package LexicalAnalyzer.DFA;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

import static junit.framework.TestCase.assertTrue;

public class IDTest {


    HelperTest ht;
    File file;
    DFA dfa;


    @Before
    public void setUp() throws Exception{
        dfa = new DFA();
        ht = new HelperTest();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void simpleID() throws Exception {
        String test = "TeSt";
        file = ht.makeFile(test);
        ArrayList<POS> tags = dfa.getTags(file);
        assertTrue(tags.size() == 1);
        assertTrue(tags.get(0).getToken().equals(test));
        assertTrue(tags.get(0).getType() == Token.ID);
        ht.removeFile(file);

        test = "supercalifragilisticexpialidocious";
        file = ht.makeFile(test);
        tags = dfa.getTags(file);
        assertTrue(tags.size() == 1);
        assertTrue(tags.get(0).getToken().equals(test));
        assertTrue(tags.get(0).getType() == Token.ID);
        ht.removeFile(file);

        test = "cat7";
        file = ht.makeFile(test);
        tags = dfa.getTags(file);
        assertTrue(tags.size() == 1);
        assertTrue(tags.get(0).getToken().equals(test));
        assertTrue(tags.get(0).getType() == Token.ID);
        ht.removeFile(file);

        String str1, str2;
        str1 = "dog";
        str2 = "}";
        test = str1 + str2;
        file = ht.makeFile(test);
        tags = dfa.getTags(file);
        assertTrue(tags.size() == 1);
        assertTrue(tags.get(0).getToken().equals(str1));
        assertTrue(tags.get(0).getType() == Token.ID);
        ht.removeFile(file);
    }



}
