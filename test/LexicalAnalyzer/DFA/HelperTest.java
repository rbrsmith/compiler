package LexicalAnalyzer.DFA;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

/**
 * Class used to help create source code used in testing
 */
public class HelperTest {

    String path = "test" + File.separator + "LexicalAnalyzer" + File.separator + "DFA" + File.separator + "test";

    /**
     * Create a source code file
     *
     * @param msg String of files contents
     * @return File object representing source code
     * @throws Exception
     */
    public File makeFile(String msg) throws Exception {
        int n = new Random().nextInt(50) + 1;
        String tmpPath = path + n + ".txt";
        PrintWriter writer = new PrintWriter(tmpPath, "UTF-8");
        writer.print(msg);
        writer.close();
        return  new File(tmpPath);
    }

    /**
     * Delete a file
     *
     * @param file File to be deleted
     * @throws Exception
     */
    public void removeFile(File file) throws Exception {
        Files.delete(Paths.get(file.getPath()));
    }



}
