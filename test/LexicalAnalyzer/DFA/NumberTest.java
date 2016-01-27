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

    @FixMethodOrder(MethodSorters.NAME_ASCENDING)
    public class NumberTest {

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
        public void intTest() throws Exception {
            String test = "123";
            file = makeFile(test);
            ArrayList<POS> tags = dfa.getTags(file);
            assertTrue(tags.size() == 1);
            assertTrue(tags.get(0).getToken().equals(test));
            assertTrue(tags.get(0).getType() == Token.INTEGER);
            removeFile(file);

            test = "123.0";
            file = makeFile(test);
            tags = dfa.getTags(file);
            assertTrue(tags.size() == 0);
            removeFile(file);

            // Too Small
            test = "0";
            file = makeFile(test);
            tags = dfa.getTags(file);
            assertTrue(tags.size() == 0);
            removeFile(file);

        }

        @Test
        public void floatTest() throws Exception {

            String test = "123.000001";
            file = makeFile(test);
            ArrayList<POS> tags;
            tags = dfa.getTags(file);
            assertTrue(tags.size() == 1);
            assertTrue(tags.get(0).getToken().equals(test));
            assertTrue(tags.get(0).getType() == Token.FLOAT);
            removeFile(file);

            test = "0.0000000";
            file = makeFile(test);
            tags = dfa.getTags(file);
            assertTrue(tags.size() == 0);
            removeFile(file);


            test = "0.0000001";
            file = makeFile(test);
            tags = dfa.getTags(file);
            assertTrue(tags.size() == 1);
            assertTrue(tags.get(0).getToken().equals(test));
            assertTrue(tags.get(0).getType() == Token.FLOAT);
            removeFile(file);

            test = "12345678987654321.1234567898764321";
            file = makeFile(test);
            tags = dfa.getTags(file);
            assertTrue(tags.size() == 1);
            assertTrue(tags.get(0).getToken().equals(test));
            assertTrue(tags.get(0).getType() == Token.FLOAT);
            removeFile(file);

        }



    }

