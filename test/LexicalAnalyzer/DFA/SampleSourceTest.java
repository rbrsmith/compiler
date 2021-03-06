package LexicalAnalyzer.DFA;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.*;

import static junit.framework.TestCase.assertTrue;

/**
 * Class to test sample source code
 */
public class SampleSourceTest {

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
    public void lineTest() throws Exception {
        String test = "\tmaxValue = utility.findMax(sample);";
        file = ht.makeFile(test);
        ArrayList<POS> tags = dfa.getTags(file);
        assertTrue(tags.size() == 9);
        ht.removeFile(file);


        test = "\n\tminValue = utility.findMin(sample);";
        file = ht.makeFile(test);
        tags = dfa.getTags(file);
        assertTrue(tags.size() == 9);
        ht.removeFile(file);


        test = "value = 100 * (2 + 3.0 / 7.0006);";
        file = ht.makeFile(test);
        tags = dfa.getTags(file);
        assertTrue(tags.size() == 12);
        ht.removeFile(file);

    }

    @Test
    public void randomTest() throws Exception {
        String all = "cat}{&&|| 6\t==6.0001<>+<->*./=()[/*]*/.//\r\n";
        int size = 21;


        String test = all;
        file = ht.makeFile(test);
        ArrayList<POS> tags = dfa.getTags(file);
        assertTrue(tags.size() == size);
        ht.removeFile(file);
    }



}

