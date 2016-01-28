package LexicalAnalyzer.DFA;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

public class HelperTest {

    String path = "test" + File.separator + "LexicalAnalyzer" + File.separator + "DFA" + File.separator + "test";

    public File makeFile(String msg) throws Exception {
        int n = new Random().nextInt(50) + 1;
        String tmpPath = path + n + ".txt";
        PrintWriter writer = new PrintWriter(tmpPath, "UTF-8");
        writer.print(msg);
        writer.close();
        return  new File(tmpPath);
    }

    public void removeFile(File file) throws Exception {
        Files.delete(Paths.get(file.getPath()));
    }



}
