package SyntacticAnalyzer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class holds all the Follows of the grammar
 */
public class FollowSet extends FF {

    /**
     *
     * @param rules ArrayList of rules in the grammar
     * @param startSmbl String starting symbol of grammar
     * @param endSmbl Ending symbol of the grammar
     */
    public FollowSet(HashMap<Integer, Rule> rules, String startSmbl, String endSmbl) {
        super(rules);

        // Follow(Start) -> End
        map.put(startSmbl, new ArrayList<String>(){{add(endSmbl);}});
    }


    public boolean addFollow(String key, ArrayList<String> vals) {
        return add(key, vals);
    }

    public boolean addFollow(String key, String val) {
        return add(key, val);
    }

    /**
     * Since Followset only hold nonterminals on LHS, we can use
     * the followset to determine if a token is a terminal
     *
     * @param k String from grammar
     * @return True if k is a terminal | False otherwise
     */
    public boolean isTerminal(String k) {
        if(map.containsKey(k)) {
            return false;
        } else {
            return true;
        }
    }
}
