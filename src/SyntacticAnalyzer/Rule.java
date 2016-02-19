package SyntacticAnalyzer;

import java.util.ArrayList;
import java.util.Arrays;

public class Rule {

    String LHS;
    ArrayList<String> RHS;
    static int id = 0;
    int ruleId;

    public Rule(String LHS, String RHS) {
        this.LHS = LHS;
        this.RHS = new ArrayList<String>(Arrays.asList(RHS.split(" ")));
        this.id += 1;
        this.ruleId = id;
    }

    public String toString() {
        String rtn = ruleId + ":\t" + LHS + "\t->\t";
        for(String smbl : RHS) {
            rtn += " " + smbl;
        }
        return rtn;
    }

    public String getFirst() {
        return RHS.get(0);
    }

    public String getLHS() {
        return LHS;
    }

    public Integer getID() {
        return ruleId;
    }

    public ArrayList<String> getRHS() {
        return RHS;
    }

    public String getLast() {
        return RHS.get(RHS.size() - 1);
    }
}
