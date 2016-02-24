package SyntacticAnalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class abstracting a table
 * Used for parse table
 */
public class Table {

    HashMap<String, HashMap<String, Integer>> data;


    public Table() {
        data = new HashMap<>();
    }

    /**
     *
     * @param row String
     * @param col String
     * @param data Integer of rule ID
     * @throws AmbiguousGrammarException
     */
    public void add(String row, String col, Integer data) throws AmbiguousGrammarException {
        HashMap<String, Integer> cols = this.data.get(row);
        // First time seeing this row, create it
        if(cols == null) {
            cols = new HashMap<>();
            cols.put(col, data);
            this.data.put(row, cols);
        } else {
            // Add this column to the row
            // If there is already a value - then grammar is invalid
            if(cols.get(col) != null && cols.get(col) != data) {
                throw new AmbiguousGrammarException(data);
            }
            cols.put(col, data);
        }
    }

    public Integer get(String row, String col) {
        return this.data.get(row).get(col);
    }

    public HashMap<String, Integer> get(String row) {
        return this.data.get(row);
    }

    public String toString() {
        String rtn = "\t";

        // Print header
        ArrayList<String> header = new ArrayList<String>();
        for(Map.Entry<String, HashMap<String, Integer>> row : data.entrySet()) {
            for (Map.Entry<String, Integer> col : row.getValue().entrySet()) {
                if(!header.contains(col.getKey())) {
                    rtn += "\t" + col.getKey();
                    header.add(col.getKey());
                }
            }
        }
        rtn += "\n";


        // Print each row and value
        // Value of -1 printed if no option exists
        for(Map.Entry<String, HashMap<String, Integer>> row : data.entrySet()) {
            String rowId = row.getKey();
            rtn += rowId + "\t";

            HashMap<String, Integer> col = row.getValue();
            for(String head : header) {
                Integer v = col.get(head);
                if(v == null) {
                    v = -1;
                }
                rtn += "\t" + v;
            }
            rtn += "\n";
        }
        return rtn;
    }


}
