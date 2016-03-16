package SyntacticAnalyzer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TableTest {

    private Table table;

    @Before
    public void setUp() throws Exception {
        table = new Table();
    }

    @After
    public void tearDown() throws Exception {
        table = null;
    }

    @Test
    public void testAdd() throws Exception {
        // Test basic table add and table get
        table.add("A", "B", 1);
        assertTrue(table.get("A", "B") == 1);
        assertTrue(table.get("A").keySet().contains("B"));

        // Make sure no exception is thrown when adding a new column
        table.add("A", "C", 2);

        // Test that an ambiguous grammar is thrown when trying to overwrite a new rule
        try {
            table.add("A", "B", 2);
            fail();
        } catch(AmbiguousGrammarException e) {};

        // No issue with adding the sae rule again
        table.add("A", "B", 1);

    }

}