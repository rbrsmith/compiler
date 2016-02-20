package SyntacticAnalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FF {

    protected HashMap<String, ArrayList<String>> map;

    public FF() {

    }

    public FF(ArrayList<Rule> rules) {
        map = new HashMap<>();
        String LHS;
        ArrayList<String> RHS;
        for(Rule rule : rules) {
            LHS = rule.getLHS();
            map.put(LHS, new ArrayList<>());
        }
    }

    public ArrayList<String> get(String str) {
        return map.get(str);
    }

    public boolean add(String key, ArrayList<String> vals) {
        int added = 0;
        for(String val: vals) {
            if(add(key, val)) added += 1;
        }
        return (added != 0);
    }

    public boolean add(String key, String val) {
        ArrayList<String> currentVals = map.get(key);
        if(currentVals == null) {
            // TODO
            System.out.println("ERR");
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