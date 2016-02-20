package SyntacticAnalyzer;

import LexicalAnalyzer.DFA.DFA;
import LexicalAnalyzer.DFA.POS;
import LexicalAnalyzer.DFA.Token;

import java.io.File;
import java.util.*;

public class Grammar {

    ArrayList<Rule> rules;

    private Table table;

    private FirstSet firstSet;
    private FollowSet followSet;

    final static String EPSILON = "EPSILON";


    public String toString() {
        String rtn = "";
        rtn += "Rules:\n";
        for(Rule r: rules) {
            rtn += r + "\n";
        }
        rtn += "\n\n";
        rtn += "Firsts\n";
        rtn += firstSet;
        rtn += "\n\n";
        rtn += "Follows\n";
        rtn += followSet;

        rtn += "Parse table\n";
        rtn += table;

        return rtn;
    }

//    public void buildParseTable() {
//        table = new Table();
//        for(Rule rule: rules) {
//            String firstSmbl = rule.getFirst();
//            ArrayList<String> first = firstSet.get(firstSmbl);
//            if(!firstSmbl.equals(EPSILON) && first != null) {
//                for(String f : first) {
//                   table.add(rule.getLHS(), f, rule.getID());
//                }
//            } else {
//                ArrayList<String> follow = followSet.get(rule.getLHS());
//                for(String f : follow) {
//                    table.add(rule.getLHS(), f, rule.getID());
//                }
//            }
//        }
//    }

    public void buildParseTable() {
        table = new Table();
        for(Rule rule: rules) {
            for(String smbl : rule.getRHS()) {
                ArrayList<String> first = firstSet.get(smbl);
                if(!smbl.equals(EPSILON) && first != null) {
                    for(String f : first) {
                        table.add(rule.getLHS(), f, rule.getID());
                    }
                } else {
                    ArrayList<String> follow = followSet.get(rule.getLHS());
                    for(String f : follow) {
                        table.add(rule.getLHS(), f, rule.getID());
                    }
                }

            }

        }
    }




    public void getFirsts() {
        firstSet = new FirstSet(rules);
        while(true) {
            int j = 0;
            for (Rule rule : rules) {
                String X = rule.getLHS();
              if (rule.getRHS().contains(EPSILON)) {
                     if(firstSet.addFirst(X, EPSILON)) j += 1;
                } else {
                     if(firstSet.addFirst(X, MultiFirst(rule.getRHS()))) j += 1;
              }
            }
            if(j == 0) {
                break;
            }
        }
    }

    public ArrayList<String> MultiFirst(ArrayList<String> Ys) {
        if(Ys.size() == 0) return new ArrayList<>();
        String Y1 = Ys.get(0);
        ArrayList<String> firstY1 = firstSet.get(Y1);
        if(!firstY1.contains(EPSILON)) {
            return firstY1;
        } else {

            if(firstY1.size() == 1 && firstY1.get(0).equals(EPSILON)) {
                return new ArrayList<String>(){{add(EPSILON);}};
            }

            ArrayList<String> tmp = new ArrayList<>();
            for (int i = 1; i < Ys.size(); i++) {
                tmp.add(Ys.get(i));
            }
            ArrayList<String> Y2 = MultiFirst(tmp);
            for (String s : firstY1) {
                if (!s.equals(EPSILON) && !Y2.contains(s)) {
                    Y2.add(s);
                }
            }
            boolean found = true;
            for (String s : Ys) {
                ArrayList<String> firstS = firstSet.get(s);
                if (!firstS.contains(EPSILON)) {
                    found = false;
                }
            }
            if (found && !Y2.contains(EPSILON)) {
                Y2.add(EPSILON);
            }
            return Y2;
        }
    }



    public void getFollows() {
        followSet = new FollowSet(rules, "prog", "$");
        boolean change = true;
        while(true) {
            for(Rule rule: rules) {
                // work our way through the terminals
                ArrayList<String> RHS = rule.getRHS();
                for(int j=0;j<RHS.size();++j){
                    String nonTerminal = RHS.get(j);
                    if(followSet.isTerminal(nonTerminal)) continue;
                    if(j + 1 != RHS.size()) {
                        change = applyRule(RHS, j, nonTerminal, rule);
                    } else {
                       change = followSet.addFollow(nonTerminal, followSet.get(rule.getLHS()));
                    }
                }
            }
            if(!change){
                break;
            }
        }
    }

    public boolean applyRule(ArrayList<String> RHS, int j, String nonTerminal, Rule rule) {
        boolean change = true;
        String next = RHS.get(j+1);
        // follow of nonTerminal is fist of j + 1
        ArrayList<String> firstNext = firstSet.get(next);
        for(String s: firstNext) {
            if(!s.equals(EPSILON)) {
                change = followSet.addFollow(nonTerminal, s);
            }
        }
        if(firstNext.contains(EPSILON)) {
            // follow of non temrinal equals follow of LHS
            change = followSet.addFollow(nonTerminal, followSet.get(rule.getLHS()));
        }
        return change;
    }

    public void parse(String filePath) throws Exception {
        Scanner input = new Scanner(new File(filePath));
        String line;
        String[] tokens;
        rules = new ArrayList<>();
        while(input.hasNext()) {
            line = input.nextLine();
            if(line.equals("")) continue;
            System.out.println("line: " + line);
            tokens = line.split(" ",3);
            rules.add(new Rule(tokens[0], tokens[2]));
        }

    }

    public void LL() {
        DFA dfa = new DFA();
        ArrayList<POS> tags = dfa.getTags(new File("/home/ross/Dropbox/IdeaProjects/CompilerDesign/src/test.txt"));
        dfa.cleanTags(tags);
        for(POS t : tags) {
            System.out.println(t);
        }

        int i = 0;



        Stack<String> stack = new Stack<>();
        stack.push("$");
     //   stack.push("E");
        stack.push("prog");
        POS token = tags.get(i);
        String tkn;
        if(token.getType() == Token.RESERVED) {
            tkn = token.getWord().toString();
        } else if(token.getType() == Token.ID) {
            tkn = token.getType().toString();
        } else {
            tkn = token.getToken().toString();
        }

        i++;

        while(true) {
            String X = stack.get(stack.size()-1);


            if(X.equals("$") && tkn.equals("$")) {
                break;
            }

            if(followSet.isTerminal(X)) {
                if(X.equals(tkn)) {
                    stack.pop();
                    if(tags.size() == i) {
                        token = null;
                    } else {
                        token = tags.get(i);
                        if(token.getType() == Token.RESERVED) {
                            tkn = token.getWord().toString();
                        } else if(token.getType() == Token.ID) {
                            tkn = token.getType().toString();
                        } else {
                            tkn = token.getToken().toString();
                        }
                    }
                    i++;
                } else {
                    System.out.println("ERROR");
                    break;
                }
            } else {
                Integer rule = table.get(X, tkn);
                if(rule == -1) {
                    System.out.println("ERROR");
                    break;
                }
                for(Rule r: rules) {
                    if(r.getID() == rule) {
                        stack.pop();
                        ArrayList<String> RHS = r.getRHS();
                        for(int k = RHS.size() - 1; k > -1; --k){
                            if(!RHS.get(k).equals(EPSILON)) {
                                stack.push(RHS.get(k));
                            }
                        }
                        break;
                    }
                }
            }
        }



    }

}
