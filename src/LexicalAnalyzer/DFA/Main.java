package LexicalAnalyzer.DFA;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Lexical Analyzer Driver
 */
public class Main {

    /**
     *
     * @param args String Array, args[0] must be full path to the sourcec code
     */
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        if(args.length == 0) {
            System.out.println("Error.  No Source Code Found");
        } else {
            // Prepare outputs
            String base = args[0].substring(0, args[0].lastIndexOf(File.separator));
            String tagsOut = base + File.separator + "output.txt";
            String errorsOUt = base + File.separator + "Errors.txt";

            PrintWriter tagWriter = new PrintWriter(tagsOut, "UTF-8");
            PrintWriter errorWriter = new PrintWriter(errorsOUt, "UTF-8");

            // Get tags and errors
            DFA dfa = new DFA();
            ArrayList<POS> tags;
            ArrayList<Exception> errors = new ArrayList<>();
            tags = dfa.getTags(new File(args[0]), errors);
            dfa.cleanTags(tags);
            for(POS t : tags) {
                tagWriter.write(t.toString() + "\n");
            }
            tagWriter.close();
            for(Exception e: errors) {
                errorWriter.write(e.toString() + "\n");
            }

            errorWriter.close();
        }

    }

}
