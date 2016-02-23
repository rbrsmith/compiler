package SyntacticAnalyzer;

import LexicalAnalyzer.DFA.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.Exchanger;

public class Grammar {

    ArrayList<Rule> rules;

    private Table table;

    private FirstSet firstSet;
    private FollowSet followSet;

    final static String EPSILON = "EPSILON";
    final String START = "prog";
    final String END = "$";

    final private DFA tokenizer = new DFA();



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

//    public void buildParseTable() throws Exception {
//        table = new Table();
//        for(Rule rule: rules) {
//            String firstSmbl = rule.getFirst();
//            ArrayList<String> first = firstSet.get(firstSmbl);
//            if(!firstSmbl.equals(EPSILON) && first != null) {
//                for(String f : first) {
//                       table.add(rule.getLHS(), f, rule.getID());
//                }
//            } else {
//                ArrayList<String> follow = followSet.get(rule.getLHS());
//                for(String f : follow) {
//                        table.add(rule.getLHS(), f, rule.getID());
//                }
//            }
//        }
//    }

//    public void buildParseTable() throws Exception{
//        table = new Table();
//        for(Rule rule: rules) {
//            for(String smbl : rule.getRHS()) {
//                ArrayList<String> first = firstSet.get(smbl);
//                if(!smbl.equals(EPSILON) && first != null) {
//                    for(String f : first) {
//                        table.add(rule.getLHS(), f, rule.getID());
//                    }
//                } else {
//                    ArrayList<String> follow = followSet.get(rule.getLHS());
//                    for(String f : follow) {
//                        table.add(rule.getLHS(), f, rule.getID());
//                    }
//                }
//
//            }
//
//        }
//    }

    public void buildParseTable() throws AmbiguousGrammarException {
        table = new Table();
        for (Rule rule : rules) {
            ArrayList<String> first = MultiFirst(rule.getRHS());
            if (first != null && !first.isEmpty()) {
                for (String f : first) {
                    table.add(rule.getLHS(), f, rule.getID());
                }
            }
            if (first != null && first.contains(EPSILON)) {
                ArrayList<String> follow = followSet.get(rule.getLHS());
                for (String f : follow) {
                    table.add(rule.getLHS(), f, rule.getID());
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
                      ArrayList<String> restOfFirst = MultiFirst(rule.getRHS());
                     if(firstSet.addFirst(X, restOfFirst)) j += 1;
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
            ArrayList<String> Y2 = new ArrayList<>(MultiFirst(tmp));
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
        followSet = new FollowSet(rules, START, END);
        boolean change = true;
        while(true) {

            int l = 0;
            for(Rule rule: rules) {
                // work our way through the terminals
                ArrayList<String> RHS = rule.getRHS();
                for(int j=0;j<RHS.size();++j){
                    String nonTerminal = RHS.get(j);

//                    if(nonTerminal.equals(EPSILON)) continue;

                    if(followSet.isTerminal(nonTerminal)) continue;
                    if(j + 1 != RHS.size()) {
                        if(applyRule(RHS, j, nonTerminal, rule)) {
                            l+=1;
                        }
                    } else {
                       if(followSet.addFollow(nonTerminal, followSet.get(rule.getLHS()))){
                           l+=1;
                       }
                    }
                }
            }
            if(l == 0){
                break;
            }
        }
    }

//    public boolean applyRule(ArrayList<String> RHS, int j, String nonTerminal, Rule rule) {
//        boolean change = true;
//        String next = RHS.get(j+1);
//        // follow of nonTerminal is fist of j + 1
//        ArrayList<String> firstNext = firstSet.get(next);
//        for(String s: firstNext) {
//            if(!s.equals(EPSILON)) {
//                change = followSet.addFollow(nonTerminal, s);
//            }
//        }
//        if(firstNext.contains(EPSILON)) {
//            // follow of non temrinal equals follow of LHS
//            change = followSet.addFollow(nonTerminal, followSet.get(rule.getLHS()));
//        }
//        return change;
//    }

    public boolean applyRule(ArrayList<String> RHS, int j, String nonTerminal, Rule rule) {
        boolean change = true;
        int l = 0;
      //  String next = RHS.get(j+1);
        // follow of nonTerminal is fist of j + 1
        ArrayList<String> remRHS = new ArrayList<>();
        for(int k=j+1;k<RHS.size();k++){
            remRHS.add(RHS.get(k));
        }

        ArrayList<String> firstNext = MultiFirst(remRHS);
        for(String s: firstNext) {
            if(!s.equals(EPSILON)) {
                if(followSet.addFollow(nonTerminal, s)) {
                 l += 1;
                }
            }
        }
        if(firstNext.contains(EPSILON)) {
            // follow of non temrinal equals follow of LHS
            if(followSet.addFollow(nonTerminal, followSet.get(rule.getLHS()))) {
                l += 1;
            }
        }
        return (l != 0);
    }

    public void parse(File file) throws FileNotFoundException, AmbiguousGrammarException {
        if(rules != null ){
            return ;
        }
        Scanner input = new Scanner(file);
        String line;
        String[] tokens;
        rules = new ArrayList<>();
        while(input.hasNext()) {
            line = input.nextLine();
            if(line.equals("")) continue;
            tokens = line.split(" ",3);
            rules.add(new Rule(tokens[0], tokens[2]));
        }

        getFirsts();
        getFollows();
        buildParseTable();

    }

    public Tuple LL(File file) throws IOException {
        // TODO not factor
        DFA dfa = new DFA();
        ArrayList<POS> tags = dfa.getTags(file);
        dfa.cleanTags(tags);



        RandomAccessFile buffer = new RandomAccessFile(file, "r");




        Integer i = 0;
        Stack<String> stack = new Stack<>();
        stack.push(END);
        stack.push(START);

        ArrayList<String> derivation = new ArrayList<>();
        int dIndex = 0;
        derivation.add(dIndex, START);
        ArrayList<ArrayList<String>> allDerivations = new ArrayList<>();


        Position pos = new Position();
        ArrayList<Exception> errors = new ArrayList<>();
//        String tkn = getNextToken(buffer);
        Tuple<String, String> tknTup = getNextToken(buffer, pos, errors);


        i++;





        while(true) {
            String X = stack.get(stack.size()-1);

            // TODO token is null from bad file?
            // TODO error if no end symbol?
            if(X.equals(END) && tknTup.getX().equals(END)) {
                break;
            }

            if(followSet.isTerminal(X)) {
                if(X.equals(tknTup.getX().toLowerCase()) || X.equals(tknTup.getX().toUpperCase())) {
                    stack.pop();
                    derivation.set(dIndex, tknTup.getY());
                    allDerivations.add(new ArrayList<>(derivation));
                    dIndex++;

                    tknTup = getNextToken(buffer, pos, errors);
                    if(tknTup == null){
                        // TODO throw new error
                        errors.add(new SyntacticError(pos));
                        buffer.close();
                        break;
                    }
                    i++;
                } else {

//                    errors.add(new SyntacticError(pos));
                    tknTup = getNextToken(buffer, pos, errors);
                    if(tknTup == null){
                        // TODO throw new error
                        errors.add(new SyntacticError(pos));
                        buffer.close();
                        break;
                    }
                }
            } else {
                Integer rule = table.get(X, tknTup.getX().toLowerCase());
                if(rule == null) rule = table.get(X, tknTup.getX().toUpperCase());
                if(rule == null || rule == -1) {

    //                buffer.close();
    //                throw new SyntacticError();
                    errors.add(new SyntacticError(pos, table.get(X), tknTup.getY()));
                    tknTup = getNextToken(buffer, pos, errors);
                    if(tknTup == null) {
                        // TODO throw new error
                        errors.add(new SyntacticError(pos));
                        break;
                    }
                    continue;
                }
                for(Rule r: rules) {
                    if(rule.compareTo(r.getID()) == 0) {
                        stack.pop();

                        derivation.remove(dIndex);
                        ArrayList<String> tmp = new ArrayList<>();

                        ArrayList<String> RHS = r.getRHS();
                        for(int k = RHS.size() - 1; k > -1; --k){
                            if(!RHS.get(k).equals(EPSILON)) {
                                stack.push(RHS.get(k));
                            }
                        }



                        for(int k=0; k<dIndex; k++){
                            tmp.add(derivation.get(k));
                        }

                        for(int k=0;k<RHS.size();k++){
                            if(!RHS.get(k).equals(EPSILON)) {
                                tmp.add(RHS.get(k));
                            }
                        }
                        for(int k=dIndex; k < derivation.size(); k++){
                            tmp.add(derivation.get(k));
                        }

                        derivation = tmp;

                        allDerivations.add(new ArrayList<>(derivation));

                        break;
                    }
                }
            }
        }

        buffer.close();

        Tuple rtnTuple = new Tuple();
        rtnTuple.setX(errors);
        rtnTuple.setY(allDerivations);

        return rtnTuple;


    }


    public Tuple getNextToken(RandomAccessFile buffer, Position pos,
                              ArrayList<Exception> errors)  {
        POS token = null;
        String tkn = "";
        Tuple<String, String> tuple = new Tuple<>();
            try {
            // Use RandomAccessFile as we will commonly go backtrack one in the file
                if (buffer.getFilePointer() == buffer.length()) {
                    // EOF
                    return null;
                }
                try {
                    token = tokenizer.getNextToken(buffer, pos);
                    if (token.getType() == Token.RESERVED) {
                        tkn = token.getWord().toString();
                        tuple.setX(token.getWord().toString());
                        tuple.setY(Reserved.valueOf(token.getWord().toString()).getWord());
                    } else if (token.getType() == Token.EOF) {
                        tkn = END;
                        tuple.setX(END);
                        tuple.setY(token.getToken());
                    } else {
                        tkn = token.getType().toString();
                        tuple.setX(token.getType().toString());
                        tuple.setY(token.getToken());
                    }
                } catch (Exception e) {
                    // Any error that occurs during token reading
                    errors.add(e);
                    return getNextToken(buffer, pos, errors);
                }
            }catch(IOException e){
                // File reading errors
                System.out.println(e.getMessage());
                System.out.println("Error reading file.");
                return null;
            }
        return tuple;

    }

}
