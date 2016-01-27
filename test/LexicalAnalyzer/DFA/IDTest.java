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


    String path = "test" + File.separator + "LexicalAnalyzer" + File.separator + "DFA" + File.separator + "test";
    File file;
    DFA dfa;

    private File makeFile(String msg) throws Exception {
        int n = new Random().nextInt(50) + 1;
        String tmpPath = path + n + ".txt";
        PrintWriter writer = new PrintWriter(tmpPath, "UTF-8");
        writer.print(msg);
        writer.close();
        file = new File(tmpPath);
        return file;
    }

    private void removeFile(File file) throws Exception {
        Files.delete(Paths.get(file.getPath()));
    }

    @Before
    public void setUp() throws Exception{
        dfa = new DFA();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void simpleID() throws Exception {
        String test = "TeSt";
        file = makeFile(test);
        ArrayList<POS> tags = dfa.getTags(file);
        assertTrue(tags.size() == 1);
        assertTrue(tags.get(0).getToken().equals(test));
        assertTrue(tags.get(0).getType() == Token.ID);
        removeFile(file);

        test = "supercalifragilisticexpialidocious";
        makeFile(test);
        tags = dfa.getTags(file);
        assertTrue(tags.size() == 1);
        assertTrue(tags.get(0).getToken().equals(test));
        assertTrue(tags.get(0).getType() == Token.ID);
        removeFile(file);

        test = "cat7";
        makeFile(test);
        tags = dfa.getTags(file);
        assertTrue(tags.size() == 1);
        assertTrue(tags.get(0).getToken().equals(test));
        assertTrue(tags.get(0).getType() == Token.ID);
        removeFile(file);

        String str1, str2;
        str1 = "dog";
        str2 = "}";
        test = str1 + str2;
        makeFile(test);
        tags = dfa.getTags(file);
        assertTrue(tags.size() == 1);
        assertTrue(tags.get(0).getToken().equals(str1));
        assertTrue(tags.get(0).getType() == Token.ID);
        removeFile(file);
    }



}
