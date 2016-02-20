package SyntacticAnalyzer;

import java.util.ArrayList;
import java.util.HashMap;

public class FirstSet extends FF {

    private HashMap<String, ArrayList<String>> firstMap;


    public FirstSet(ArrayList<Rule> rules) {
        super(rules);
        ArrayList<String> RHS;
        for(Rule rule : rules) {
            RHS = rule.getRHS();
            for(String str: RHS) {
                if(str.equals(Grammar.EPSILON)) continue;
                if(map.containsKey(str)) continue;
                else {
                    // Terminal symbol
                    map.put(str, new ArrayList<String>(){{add(str);}});
                }
            }
        }
    }

    public boolean addFirst(String key, ArrayList<String> vals) {
        return add(key, vals);
    }

    public boolean addFirst(String key, String val) {
        return add(key, val);
    }





}
