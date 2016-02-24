package SyntacticAnalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class used to hold First and Follow sets
 */
public class FF {

    // First and Follow data will be stored here
    protected HashMap<String, ArrayList<String>> map;

    /**
     *
     * @param rules ArrayList of Rules in the grammar
     */
    public FF(HashMap<Integer, Rule> rules) {
        // Add all left hand sides to the set
        map = new HashMap<>();
        String LHS;
        ArrayList<String> RHS;
        for(Rule rule : rules.values()) {
            LHS = rule.getLHS();
            map.put(LHS, new ArrayList<>());
        }
    }


    public ArrayList<String> get(String str) {
        return map.get(str);
    }

    /**
     *
     * @param key String
     * @param vals ArrayList values
     * @return True if value is added | False otherwise
     */
    public boolean add(String key, ArrayList<String> vals) {
        int added = 0;
        for(String val: vals) {
            if(add(key, val)) added += 1;
        }
        return (added != 0);
    }

    /**
     *
     * @param key String
     * @param val String
     * @return True if value is added | False otherwise
     */
    public boolean add(String key, String val) {
        ArrayList<String> currentVals = map.get(key);
        if(currentVals == null) {
            return false;
        }
        if(!currentVals.contains(val)) {
            currentVals.add(val);
            return true;
        }
        return false;
    }

    public String toString() {
        String rtn = "";
        for(Map.Entry<String, ArrayList<String>> mp : map.entrySet()) {
            String nonTerminal = mp.getKey();
            ArrayList<String> firsts = mp.getValue();
            rtn += nonTerminal + ":\t\t{";
            for(int i=0;i<firsts.size();++i){
                rtn += firsts.get(i);
                if(i!=firsts.size()-1){
                    rtn+=", ";
                }
            }
            rtn += "}\n";
        }
        return rtn;
    }





}