package SyntacticAnalyzer;

import java.util.ArrayList;
import java.util.FormatFlagsConversionMismatchException;
import java.util.HashMap;
import java.util.Map;

public class Grammar {

    final ArrayList<Rule> rules = new ArrayList<Rule>() {{
        add(new Rule("E", "T E' $"));
        add(new Rule("E'", "+ T E'"));
        add(new Rule("E'", "- T E'"));
        add(new Rule("E'", "EPSILON"));
        add(new Rule("T", "F T'"));
        add(new Rule("T'", "* F T'"));
        add(new Rule("T'", "/ F T'"));
        add(new Rule("T'", "EPSILON"));
        add(new Rule("F", "num"));
        add(new Rule("F", "id"));

//            add(new Rule("E", "T E'"));
//            add(new Rule("E'", "+ T E'"));
//            add(new Rule("E'", "EPSILON"));
//            add(new Rule("T", "F T'"));
//            add(new Rule("T'", "* F T'"));
//            add(new Rule("T'", "EPSILON"));
//            add(new Rule("F", "( E )"));
//            add(new Rule("F", "id"));

    }};

//    final HashMap<String, FF> firsts = new HashMap<String, FF>() {{
//        put("E", new FF("E", "num id"));
//        put("E'", new FF("E'", "+ - EPSILON"));
//        put("T", new FF("T", "num id"));
//        put("T'", new FF("T'", "* / EPSILON"));
//        put("F", new FF("F", "num id"));
//        put("+", new FF("+", "+"));
//        put("-", new FF("-", "-"));
//        put("*", new FF("*", "*"));
//        put("/", new FF("/", "/"));
//        put("num", new FF("num", "num"));
//        put("id", new FF("id", "id"));
//    }};

    HashMap<String, FF> firsts;
    HashMap<String, FF> follows;
//    final HashMap<String, FF> follows = new HashMap<String, FF>() {{
//        put("E", new FF("E", "$"));
//        put("E'", new FF("E'", "$"));
//        put("T", new FF("T", "+ - $"));
//        put("T'", new FF("T'", "+ - $"));
//        put("F", new FF("F", "+ - * / $"));
//    }};

    public String toString() {
        String rtn = "";
        rtn += "Rules:\n";
        for(Rule r: rules) {
            rtn += r + "\n";
        }
        rtn += "\n\n";
        rtn += "Firsts\n";
        for(FF f: firsts.values()) {
            rtn += f + "\n";
        }
        rtn += "\n\n";
        rtn += "Follows\n";
        for(FF f: follows.values()) {
            rtn += f + "\n";
        }
        return rtn;
    }

    public void buildParseTable() {
        Table table = new Table();
        for(Rule rule: rules) {
            String firstSmbl = rule.getFirst();
            FF first = firsts.get(firstSmbl);
            if(!firstSmbl.equals("EPSILON") && first != null) {
                ArrayList<String> ff = first.getList();
                for(String f : ff) {
                   table.add(rule.getLHS(), f, rule.getID());
                }
            } else {
                FF follow = follows.get(rule.getLHS());
                ArrayList<String> ff = follow.getList();
                for(String f : ff) {
                    table.add(rule.getLHS(), f, rule.getID());
                }
            }



        }
        System.out.println(table);

    }



    public void getFirsts() {
        HashMap<String, ArrayList<String>> First = new HashMap<>();
        int i = 0;

            for (Rule rule : rules) {
                String nonTerminal = rule.getLHS();
                if (First.get(nonTerminal) == null) {
                    First.put(nonTerminal, new ArrayList<>());
                }
            }

            for (Rule rule : rules) {
                ArrayList<String> RHS = rule.getRHS();
                for (String r : RHS) {
                    if (isTerminal(First, r) && !r.equals("EPSILON")) {
                        if (First.get(r) == null) {
                            First.put(r, new ArrayList<String>() {{
                                add(r);
                            }});
                        }
                    }
                }
            }

        boolean change = true;
        while(true) {
            for (Rule rule : rules) {
                String X = rule.getLHS();
                if (isTerminal(First, X)) {
                    change = addFirst(First, X, X);
                } else if (rule.getRHS().contains("EPSILON")) {
                    change = addFirst(First, X, "EPSILON");
                    continue;
                } else {
                    change = addFirst(First, X, MultiFirst(First, rule.getRHS()));
                }
            }
            if(!change) {
                break;
            }
            i += 1;

        }

//        System.out.println(First);
        firsts = new HashMap<String, FF>();
        for(Map.Entry<String, ArrayList<String>> f : First.entrySet()) {
            String X = f.getKey();
            ArrayList<String> vals = f.getValue();
            String v = "";
            for(String val : vals){
                v += val + " ";
            }
            firsts.put(X, new FF(X, v));
        }



    }

    public ArrayList<String> MultiFirst(HashMap<String, ArrayList<String>> First, ArrayList<String> Ys) {
        String Y1 = Ys.get(0);
        ArrayList<String> firstY1 = First.get(Y1);
        if(!firstY1.contains("EPSILON")) {
            return firstY1;
        } else {
            ArrayList<String> tmp = new ArrayList<>();
            for (int i = 1; i < Ys.size(); i++) {
                tmp.add(Ys.get(i));
            }
            ArrayList<String> Y2 = MultiFirst(First, tmp);
            for (String s : firstY1) {
                if (!s.equals("EPSILON") && !Y2.contains(s)) {
                    Y2.add(s);
                }
            }
            boolean found = true;
            for (String s : Ys) {
                ArrayList<String> firstS = First.get(s);
                if (!firstS.contains("EPSILON")) {
                    found = false;
                }
            }
            if (found && !Y2.contains("EPSILON")) {
                Y2.add("EPSILON");
            }
            return Y2;
        }
    }

    public String getRHSBeforeEpsilon(ArrayList<String> RHS, HashMap<String, ArrayList<String>> First) {
        ArrayList<String> newRHS = new ArrayList<String>();
        for(String r: RHS) {
            ArrayList<String> f = First.get(r);

            if(f.contains("EPSILON")) {
                newRHS.add(r);
            } else {
                return r;
            }
        }
        return null;
    }

    public boolean isEpsilonInRHS(ArrayList<String> RHS, HashMap<String, ArrayList<String>> First) {
       for(String r: RHS) {
           ArrayList<String> f = First.get(r);

           if (!f.contains("EPSILON")) {
               return false;
           }
       }
        return true;

    }

    public boolean isTerminal(HashMap<String,ArrayList<String>> nonTerminals, String X) {

        if(nonTerminals.get(X) == null) {
            return true;
        } else {
            return false;
        }

    }

    public boolean addFirst(HashMap<String,ArrayList<String>> nonTerminals, String X, String val) {
        if(!nonTerminals.get(X).contains(val)) {
            nonTerminals.get(X).add(val);
            return true;
        }
        return false;
    }

    public boolean addFirst(HashMap<String,ArrayList<String>> nonTerminals, String X, ArrayList<String> val) {
        int c = 0;
        for(String s: val) {
            if(addFirst(nonTerminals, X, s)) {
                c += 1;
            }
        }
        if(c == 0) return false;
        else return true;

    }

    public boolean addFollows(HashMap<String,ArrayList<String>> nonTerminals, String X, ArrayList<String> val, int id) {
        int c = 0;
        for(String s: val) {
            if(addFollows(nonTerminals, X, s, id)) {
                c += 1;
            }
        }
        if(c == 0) return false;
        else return true;

    }

    public boolean addFollows(HashMap<String,ArrayList<String>> nonTerminals, String X, String val, int id) {
        if(nonTerminals.get(X) == null) {
            return false;
        }
        if(!nonTerminals.get(X).contains(val)) {
            nonTerminals.get(X).add(val);
//            System.out.println("Rule: " + id + "  Adding " + val + " to " + X);
//            System.out.println(nonTerminals);
//            System.out.println();
            return true;
        }
        return false;
    }


    public void getFollows() {
        HashMap<String, ArrayList<String>> Follows = new HashMap<String, ArrayList<String>>();
        for (Rule rule : rules) {
            String nonTerminal = rule.getLHS();
            if (Follows.get(nonTerminal) == null) {
                Follows.put(nonTerminal, new ArrayList<>());
            }
        }

        Follows.put("E", new ArrayList<String>() {{
            add("$");
        }});

        boolean change = true;
        while(true) {
            for(Rule rule: rules) {
                // work our way through the terminals
                ArrayList<String> RHS = rule.getRHS();
                for(int j=0;j<RHS.size();++j){
                    String nonTerminal = RHS.get(j);
                    if(isTerminal(Follows, nonTerminal)) {
                        continue;
                    }
                    if(j + 1 != RHS.size()) {
                        String next = RHS.get(j+1);
                        // follow of nonTerminal is fist of j + 1
                        FF firstNext = firsts.get(next);
                        for(String s: firstNext.getList()) {
                            if(!s.equals("EPSILON")) {
                                change = addFollows(Follows, nonTerminal, s, 2);
                            }
                        }
                        if(firstNext.getList().contains("EPSILON")) {
                            // follow of non temrinal equals follow of LHS
                           change = addFollows(Follows, nonTerminal, Follows.get(rule.getLHS()), 4);
                        }

                    } else {
                        change = addFollows(Follows, nonTerminal, Follows.get(rule.getLHS()), 3);
                    }

                }


            }
            if(!change){
                break;
            }



        }
    //    System.out.println(Follows);
        follows = new HashMap<String, FF>();
        for(Map.Entry<String, ArrayList<String>> mp : Follows.entrySet()) {
            String nt = mp.getKey();
            ArrayList<String> vals = mp.getValue();
            String t = "";
            for(String v: vals){
                t += v + " ";
            }
            follows.put(nt, new FF(nt, t));
        }


    }

}
