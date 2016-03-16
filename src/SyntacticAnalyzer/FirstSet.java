package SyntacticAnalyzer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class holds all the Firsts in the grammar
 */
public class FirstSet extends FF {


    /**
     *
     * @param rules ArrayList of rules in the grammar
     */
    public FirstSet(HashMap<Integer, Rule> rules) {
        super(rules);

        // In addition to super, we also add all the RHS' as First(terminal) -> terminal
        ArrayList<String> RHS;
        for(Rule rule : rules.values()) {
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
        // First(EPSILON) -> EPSILON
        map.put(Grammar.EPSILON, new ArrayList<String>(){{ add(Grammar.EPSILON);}});
    }

    public boolean addFirst(String key, ArrayList<String> vals) {
        return add(key, vals);
    }

    public boolean addFirst(String key, String val) {
        return add(key, val);
    }





}
