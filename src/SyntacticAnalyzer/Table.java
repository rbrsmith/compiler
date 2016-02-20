package SyntacticAnalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Table {

    HashMap<String, HashMap<String, Integer>> data;


    public Table() {
        data = new HashMap<String, HashMap<String, Integer>>();
    }

    public void add(String row, String col, Integer data) throws Exception{
        HashMap<String, Integer> cols = this.data.get(row);
        if(cols == null) {
            cols = new HashMap<String, Integer>();
            cols.put(col, data);
            this.data.put(row, cols);
        } else {
            if(cols.get(col) != null && cols.get(col) != data) {

                throw new Exception("Inproper grammar at rule: "+data+".  Trying to set {" + row+", "+col+", "+ data+"} but found {"+row+", "+col+", "+cols.get(col) + "}");
            }
            cols.put(col, data);
        }
    }

    public Integer get(String row, String col) {
        return this.data.get(row).get(col);
    }

    public String toString() {
        String rtn = "\t";

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

//            for(Map.Entry<String,Integer> col : row.getValue().entrySet()) {
//
//                String colId = col.getKey();
//                Integer val = col.getValue();
//
//                rtn += colId + " = " + val + "\t";
//
//            }
//            rtn += "\n";


        }

        return rtn;

    }


}
