import SyntacticAnalyzer.AmbiguousGrammarException;
import SyntacticAnalyzer.Grammar;
import SyntacticAnalyzer.Tuple;

import java.io.*;
import java.util.ArrayList;

/**
 * Lexical Analyzer Driver
 */
public class Main {

    /**
     * @param args String Array, args[0] must be full path to the sourcec code
     */
    public static void main(String[] args) {
        if(args.length < 2) {
            System.out.println("Error.  Unable to find source code or grammar.  " +
                    "Make sure the program is called: java Main /path/to/source/code /path/to/grammar");
        } else {
            // Prepare inputs / outputs
            String sourceCodePath = args[0];
            String grammarPath = args[1];
            String base = args[0].substring(0, args[0].lastIndexOf(File.separator));
            String derivationOut = base + File.separator + "derivation.txt";


            try {
                // Build grammar
                Grammar grammar = new Grammar(grammarPath);

                // Parse the source code
                Tuple<ArrayList<Exception>, ArrayList<ArrayList<String>>> result =
                        grammar.LL(new File(sourceCodePath));

                // Save derivation to out file
                PrintWriter derivationWriter = new PrintWriter(derivationOut, "UTF-8");
                for(ArrayList<String> derivationString :  result.getY()) {
                    String derivation = "";
                    for(String s: derivationString) {
                        derivation += s + " ";
                    }
                    derivation += "\n";
                    derivationWriter.write(derivation);
                }
                derivationWriter.close();

                // Print out all the errors
                for(Exception e : result.getX()) {
                    System.out.println(e);
                }

                if(result.getX().size() == 0) {
                    System.out.println("OK.");
                }


            } catch(FileNotFoundException e) {
                System.out.println("Unable to find grammar or source code");
            } catch(IOException ie){
                System.out.println("Error reading grammar or source code");
            } catch(AmbiguousGrammarException ame) {
                System.out.println("Error grammar contains ambiguities!");
            }
        }
    }

}
