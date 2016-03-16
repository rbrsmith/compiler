package SyntacticAnalyzer;

import LexicalAnalyzer.DFA.Position;
import LexicalAnalyzer.DFA.Token;

import java.util.HashMap;

/**
 * Thrown when an error occurs during parsing
 */
public class SyntacticException extends Exception {
    private String msg;

    /**
     * Called when there is no chance for the grammar to recover
     * Ex: Missing END symbol ($)
     * @param pos Position object
     */
    public SyntacticException(Position pos) {
        this.msg = "Unrecoverable Syntactic Error at " + pos;
    }

    /**
     * Called when there tokens mismatch
     * @param pos
     * @param token
     */
    public SyntacticException(Position pos, String token) {
        this.msg = "Syntactic Error at " + pos + "\nFound \""+token+"\"\n";
    }

    /**
     * Called when an error is encountered but it is beleived to be recoverable
     * @param pos Position object
     * @param options ArrayList of expected values that would have allowed grammar to parse
     * @param token String of symbol at which the error was encountered
     */
    public SyntacticException(Position pos, HashMap<String, Integer> options, String token) {
        String m = "Syntactic Error at " + pos + "\nFound \"" + token + "\" expected \" ";

        // Display alternatives
        for(String s: options.keySet()){
            if(s.equals(Grammar.EPSILON)) continue;
            try {
                Token tkn = Token.valueOf(s);
                m += tkn.getValue() + ", ";
            } catch(Exception e) {
                m += s + ", ";
            }
        }
        this.msg = m.substring(0, m.length() -2) + " \"\n";
    }

    @Override
    public String getMessage() {
        return msg;
    }




}



