package SyntacticAnalyzer;

public class AmbiguousGrammarException extends Exception{
    public AmbiguousGrammarException(int ruleid) {
        super("Inproper grammar at rule: "+ruleid);
    }

}
