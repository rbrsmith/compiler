import CodeGeneration.CodeGenerator;
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
        if(args.length < 1) {
            System.out.println("Error.  Unable to find source code.  " +
                    "Make sure the program is called: java Main /path/to/source/code");
        } else {
            // Prepare inputs / outputs
            String sourceCodePath = args[0];
            File currDir = new File(".");
            String base = args[0].substring(0, args[0].lastIndexOf(File.separator));
            String grammarPath = currDir.getAbsolutePath().substring(0, currDir.getAbsolutePath().length()-1);
            grammarPath +=  File.separator + "src" + File.separator + "Grammar.txt";
            String parseTableOut = base + File.separator + "parse.txt";
            String derivationOut = base + File.separator + "derivation.txt";
            String errorOut = base + File.separator + "errors.txt";
            String symbolsOut = base + File.separator + "symbols.txt";
            String moonOut = base + File.separator + "moon.m";


            try {
                // Build grammar
                Grammar grammar = new Grammar(grammarPath);

                // Parse the source code
                Tuple<ArrayList<Exception>, ArrayList<ArrayList<String>>> result =
                        grammar.LL(new File(sourceCodePath));


                // Save parse table and other items
                PrintWriter parseWriter = new PrintWriter(parseTableOut, "UTF-8");
                parseWriter.write(grammar.toString());
                parseWriter.close();


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

                // Print out all the errors & Save to file
                PrintWriter errorWriter = new PrintWriter(errorOut, "UTF-8");
                for(Exception e : result.getX()) {
                    System.out.println(e);
                    errorWriter.write(e + "\n");
                }
                errorWriter.close();


                PrintWriter symbolWriter = new PrintWriter(symbolsOut, "UTF-8");
                symbolWriter.write(grammar.getSemanticAnalyzer().toString());
                symbolWriter.close();

                CodeGenerator code = CodeGenerator.getInstance();
                code.save(moonOut);


                if(result.getX().size() == 0) {
                    System.out.println("OK.");
                }


            } catch(FileNotFoundException e) {
                System.out.println("Unable to find source code");
            } catch(IOException ie){
                System.out.println("Error reading source code");
            } catch(AmbiguousGrammarException ame) {
                System.out.println("Error grammar contains ambiguities!");
            }
        }
    }

}
