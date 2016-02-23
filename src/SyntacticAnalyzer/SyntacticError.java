package SyntacticAnalyzer;

import LexicalAnalyzer.DFA.Position;
import LexicalAnalyzer.DFA.Token;

import java.util.HashMap;

public class SyntacticError extends Exception {
    private String msg;

    public SyntacticError(Position pos) {
        this.msg = "Unrecoverable Syntactic Error at " + pos;
    }

    public SyntacticError(Position pos, HashMap<String, Integer> options, String token) {
        String m = "Syntactic Error at " + pos + "\nFound \"" + token + "\" expected \" ";
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



