package LexicalAnalyzer.DFA;

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

/**
 * Class to test integers and floats
 */
public class NumberTest {

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
    public void intTest() throws Exception {
        String test = "123";
        file = ht.makeFile(test);
        ArrayList<POS> tags = dfa.getTags(file);
        assertTrue(tags.size() == 1);
        assertTrue(tags.get(0).getToken().equals(test));
        assertTrue(tags.get(0).getType() == Token.INTEGER);
        ht.removeFile(file);

        test = "123.00";
        file = ht.makeFile(test);
        tags = dfa.getTags(file);
        assertTrue(tags.size() == 0);
        ht.removeFile(file);

        // Too Small
        test = "0";
        file = ht.makeFile(test);
        tags = dfa.getTags(file);
        assertTrue(tags.size() == 1);
        assertTrue(tags.get(0).getToken().equals(test));
        assertTrue(tags.get(0).getType() == Token.INTEGER);
        ht.removeFile(file);

    }

    @Test
    public void floatTest() throws Exception {

        String test = "123.000001";
        file = ht.makeFile(test);
        ArrayList<POS> tags;
        tags = dfa.getTags(file);
        assertTrue(tags.size() == 1);
        assertTrue(tags.get(0).getToken().equals(test));
        assertTrue(tags.get(0).getType() == Token.FLOAT);
        ht.removeFile(file);

        test = "0.0000000";
        file = ht.makeFile(test);
        tags = dfa.getTags(file);
        assertTrue(tags.size() == 0);
        ht.removeFile(file);

        test = "0.0000001";
        file = ht.makeFile(test);
        tags = dfa.getTags(file);
        assertTrue(tags.size() == 1);
        assertTrue(tags.get(0).getToken().equals(test));
        assertTrue(tags.get(0).getType() == Token.FLOAT);
        ht.removeFile(file);

        test = "12345678987654321.1234567898764321";
        file = ht.makeFile(test);
        tags = dfa.getTags(file);
        assertTrue(tags.size() == 1);
        assertTrue(tags.get(0).getToken().equals(test));
        assertTrue(tags.get(0).getType() == Token.FLOAT);
        ht.removeFile(file);

        test = "123.0";
        file = ht.makeFile(test);
        tags = dfa.getTags(file);
        assertTrue(tags.size() == 1);
        assertTrue(tags.get(0).getToken().equals(test));
        assertTrue(tags.get(0).getType() == Token.FLOAT);
        ht.removeFile(file);

    }
}

