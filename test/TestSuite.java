import CodeGeneration.CodeGenerator;
import CodeGeneration.CodeGeneratorTest;
import LexicalAnalyzer.DFA.*;
import LexicalAnalyzer.DFA.NodeTest;
import SemanticAnalyzer.*;
import SemanticEvaluation.*;
import SyntacticAnalyzer.*;
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
        GrammarTest.class,
        RuleTest.class,
        FFTest.class,
        FirstSetTest.class,
        FollowSetTest.class,
        TableTest.class,
        AnalyzerTest.class,
        ClassDeclTest.class,
        FunctionDeclTest.class,
        NodeTest.class,
        ProgramDeclTest.class,
        SymbolTableTest.class,
        VariableDeclTest.class,
        SemanticEvaluationTest.class,
//        CodeGeneratorTest.class
})

public class TestSuite {


}
