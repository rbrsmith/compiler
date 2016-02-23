import LexicalAnalyzer.DFA.*;
import SyntacticAnalyzer.Grammar;
import SyntacticAnalyzer.GrammarTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        NodeTest.class,
        EdgeTest.class,
        POSTest.class,
        PositionTest.class,
        GraphTest.class,
        IDTest.class,
        NumberTest.class,
        OperatorTest.class,
        PunctuationTest.class,
        BracketTest.class,
        CommentTest.class,
        SampleSourceTest.class,
        GrammarTest.class

})

public class TestSuite {


}
