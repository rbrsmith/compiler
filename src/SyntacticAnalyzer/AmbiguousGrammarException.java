package SyntacticAnalyzer;

import CodeGeneration.CompilerException;

/**
 * Throw if the grammar has any ambiguities
 */
public class AmbiguousGrammarException extends CompilerException {
    public AmbiguousGrammarException(int ruleid) {
        super("Ambiguous grammar at rule: "+ruleid);
    }

}
