package SyntacticAnalyzer;

import java.util.ArrayList;
import java.util.Arrays;

public class FF {

    String smbl;
    ArrayList<String> ff;

    public FF(String smbl, String firsts) {
        this.smbl = smbl;
        this.ff = new ArrayList<String>(Arrays.asList(firsts.split(" ")));
    }


    public String toString() {
        String rtn = smbl + ":\t\t{";
        for(String f : ff) {
            rtn += " " + f;
        }
        rtn += " }";
        return rtn;
    }

    public ArrayList<String> getList() {
        return ff;
    }

}
