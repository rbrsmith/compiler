package SyntacticAnalyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;


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

    // ASsignment 3
    public ArrayList<String> getSemanticRHS() {
        return RHS;
    }

    public ArrayList<String> getRHS() {
        // Assignment 3 - Remove SEMANTIC rules
        ArrayList<String> filteredRHS = new ArrayList<>(RHS);
        for (Iterator<String> iterator = filteredRHS.iterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (string.contains("SEMANTIC")) {
                // Remove the current element from the iterator and the list.
                iterator.remove();
            }
        }
        return filteredRHS;
    }



}
