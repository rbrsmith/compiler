package SyntacticAnalyzer;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Class that encapulates a single grammar rule
 */
public class Rule {

    String LHS; // Left Hand Side
    ArrayList<String> RHS; // Right Hand Side
    static int id = 0;
    int ruleId;

    /**
     *
     * @param LHS String LHS of a rule
     * @param RHS String RHS of a rule in space delimited form a B c
     */
    public Rule(String LHS, String RHS) {
        this.LHS = LHS;
        this.RHS = new ArrayList<>(Arrays.asList(RHS.split(" ")));
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


    public String getLHS() {
        return LHS;
    }

    public Integer getID() {
        return ruleId;
    }

    public ArrayList<String> getRHS() {
        return RHS;
    }



}
