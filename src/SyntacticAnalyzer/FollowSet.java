package SyntacticAnalyzer;

import java.util.ArrayList;

public class FollowSet extends FF {

    public FollowSet(ArrayList<Rule> rules, String startSmbl, String endSmbl) {
        super(rules);
        map.put(startSmbl, new ArrayList<String>(){{add(endSmbl);}});
    }


    public boolean addFollow(String key, ArrayList<String> vals) {
        return add(key, vals);
    }

    public boolean addFollow(String key, String val) {
        return add(key, val);
    }

    public boolean isTerminal(String k) {
        if(map.containsKey(k)) {
            return false;
        } else {
            return true;
        }
    }
}
