package SyntacticAnalyzer;

/**
 * Throw if the grammar has any ambiguities
 */
public class AmbiguousGrammarException extends Exception{
    public AmbiguousGrammarException(int ruleid) {
        super("Ambiguous grammar at rule: "+ruleid);
    }

}
