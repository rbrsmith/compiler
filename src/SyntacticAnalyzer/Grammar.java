package SyntacticAnalyzer;

import LexicalAnalyzer.DFA.*;
import SemanticAnalyzer.AlreadyDeclaredException;
import SemanticAnalyzer.Analyzer;
import SemanticAnalyzer.SemanticException;

import java.io.*;
import java.util.*;

/**
 * Class responsble for building and LL parsing a source code
 */
public class Grammar {

    // Holds all production rules in the grammar
    HashMap<Integer ,Rule> rules;

    // Holds the parse table
    private Table table;

    // Holds the first and follow sets
    private FirstSet firstSet;
    private FollowSet followSet;

    // Token names
    public final static String EPSILON = "EPSILON";
    final String START = "prog";
    final String END = "$";

    // Reference to the graph and State Transition Table to get our tokens from
    final private DFA tokenizer = new DFA();


    public Grammar(String grammarPath) throws AmbiguousGrammarException, FileNotFoundException {
        File f = new File(grammarPath);
        buildGrammar(f);

    }


    public String toString() {
        String rtn = "";
        rtn += "Rules:\n";
        for(Rule r: rules.values()) {
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


    /**
     * Constructs a parser table where each terminal and nonterminal matched with a token
     * will say which rule to follow
     * @throws AmbiguousGrammarException if the grammar is malformed
     */
    private void buildParseTable() throws AmbiguousGrammarException {
        table = new Table();
        for (Rule rule : rules.values()) {
            // Get the first of the Right Hand Side
            ArrayList<String> first = multiFirst(rule.getRHS());
            // Add T[LHS, f] = ruleID - for each first
            if (first != null && !first.isEmpty()) {
                for (String f : first) {
                    table.add(rule.getLHS(), f, rule.getID());
                }
            }
            // Add T[LHS, f] = ruleID - for each follow - only if first contains EPSILON
            if (first != null && first.contains(EPSILON)) {
                ArrayList<String> follow = followSet.get(rule.getLHS());
                for (String f : follow) {
                    table.add(rule.getLHS(), f, rule.getID());
                }
            }
        }
    }


    /**
     * Builds the first sets from the grammar
     * Uses a heuristic - we keep updating until nothing has changed
     */
    private void getFirsts() {
        // Build initial rules for nonterminals
        firstSet = new FirstSet(rules);
        while(true) {
            int j = 0;
            for (Rule rule : rules.values()) {
              String LHS = rule.getLHS();
              // Add EPSILON if needed
              if (rule.getRHS().contains(EPSILON)) {
                     if(firstSet.addFirst(LHS, EPSILON)) j += 1;
                } else {
                     // Get first of Right Hand Side add add to the Left Hand Side
                     ArrayList<String> restOfFirst = multiFirst(rule.getRHS());
                     if(firstSet.addFirst(LHS, restOfFirst)) j += 1;
              }
            }
            if(j == 0) {
                // When no more changes are done we exit
                break;
            }
        }
    }

    /**
     * Gets the first set of the Right Hand Side of the production rules
     * Ex:  First(a B c) in L -> a B c
     * @param Ys
     * @return ArrayList of first in the production rule
     */
    private ArrayList<String> multiFirst(ArrayList<String> Ys) {
        if(Ys.size() == 0) return new ArrayList<>();
        String Y1 = Ys.get(0);
        ArrayList<String> firstY1 = firstSet.get(Y1);
        // If the first of the first symbol does not have EPSILON
        // then we have found our First
        if(!firstY1.contains(EPSILON)) {
            return firstY1;
        } else {

            // If all the rule had was Epsilon, then we return just EPSILON
            // Ex: A -> EPSILON; First(A) -> EPSILON
            if(firstY1.size() == 1 && firstY1.get(0).equals(EPSILON)) {
                return new ArrayList<String>(){{add(EPSILON);}};
            }

            // Otherwise we call multiFirst on everything but the first symbol
            // Ex First(L) -> a B c
            //      First(L) -> First(A) + First(B c) ...

            ArrayList<String> tmp = new ArrayList<>();
            for (int i = 1; i < Ys.size(); i++) {
                tmp.add(Ys.get(i));
            }
            ArrayList<String> Y2 = new ArrayList<>(multiFirst(tmp));
            // Add first of first symbol back to the array
            for (String s : firstY1) {
                if (!s.equals(EPSILON) && !Y2.contains(s)) {
                    Y2.add(s);
                }
            }

            // Add EPSILON if all Right Hand Side symbols has EPSILON
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


    /**
     * Build the Follow Set for the grammar
     */
    private void getFollows() {
        // Set up follow set
        followSet = new FollowSet(rules, START, END);

        // Use heuristic - only exit when no more changes are being made
        while(true) {

            int l = 0;
            for(Rule rule: rules.values()) {
                // Work our way through the terminals in the Right Hand Side
                ArrayList<String> RHS = rule.getRHS();
                for(int j=0;j<RHS.size();++j){
                    String nonTerminal = RHS.get(j);

                    if(followSet.isTerminal(nonTerminal)) continue;
                    if(j + 1 != RHS.size()) {
                        // If there are values to the left we apply a rule
                        if(applyRule(RHS, j, nonTerminal, rule)) {
                            l+=1;
                        }
                    } else {
                       // Otherwise follow of nonTerminal is follow of LHS
                       if(followSet.addFollow(nonTerminal, followSet.get(rule.getLHS()))){
                           l+=1;
                       }
                    }
                }
            }

            // Check for changes in this iteration
            if(l == 0){
                break;
            }
        }
    }

    /**
     * Apply Rule if we have a nonTerminal B in a rule L -> a B c
     * @param RHS ArrayList full RHS of the rule (a B c)
     * @param j int location of our terminal in the RHS (Ex: B -> j = 1)
     * @param nonTerminal String nonTerminal we are looking at (Ex: B)
     * @param rule Rule that we are current examining (L -> a B c)
     * @return True if we updated the follow set | False otherwise
     */
    private boolean applyRule(ArrayList<String> RHS, int j, String nonTerminal, Rule rule) {
        int l = 0;

        // Get the remainder of the Right Hand Side after our terminal
        ArrayList<String> remRHS = new ArrayList<>();
        for(int k=j+1;k<RHS.size();k++){
            remRHS.add(RHS.get(k));
        }

        // Look at the first of the remainder add add them to the follow
        ArrayList<String> firstNext = multiFirst(remRHS);
        for(String s: firstNext) {
            if(!s.equals(EPSILON)) {
                if(followSet.addFollow(nonTerminal, s)) {
                 l += 1;
                }
            }
        }
        // If first is EPSILON, then we must add the follow of the LHS
        if(firstNext.contains(EPSILON)) {
            if(followSet.addFollow(nonTerminal, followSet.get(rule.getLHS()))) {
                l += 1;
            }
        }
        // Check for changes in return
        return (l != 0);
    }

    /**
     * Parse the grammar file and build the grammar
     *
     * @param file File holding the grammar
     * @throws FileNotFoundException thrown if we cannot find the grammar
     * @throws AmbiguousGrammarException thrown if the grammar has ambiguities
     */
    private void buildGrammar(File file) throws FileNotFoundException, AmbiguousGrammarException {
        // Stop if we already have some rules
        if(rules != null ){
            return ;
        }
        Scanner input = new Scanner(file);

        // Split the Right Hand Side of A -> b C d
        String line;
        String[] tokens;
        rules = new HashMap<>();
        while(input.hasNext()) {
            line = input.nextLine();
            if(line.equals("")) continue;
            tokens = line.split(" ",3);
            Rule r = new Rule(tokens[0], tokens[2]);
            rules.put(r.getID(), r);
        }

        // Construct the parse table
        getFirsts();
        getFollows();
        buildParseTable();
    }

    /**
     * Parse the source file
     * @param file File containing source code
     * @return Tuuple containing derivations and Exceptions
     * @throws IOException thrown if there was an issue readying the source code
     */
    public Tuple LL(File file) throws IOException {
        // Access source code
        RandomAccessFile buffer = new RandomAccessFile(file, "r");

        // Stack used to parse
        Stack<String> stack = new Stack<>();
        stack.push(END);
        stack.push(START);

        // Array containing the derivation at each step
        ArrayList<ArrayList<String>> allDerivations = new ArrayList<>();
        // Current derivation
        ArrayList<String> derivation = new ArrayList<>();
        int dIndex = 0;
        derivation.add(dIndex, START);
        allDerivations.add(new ArrayList<>(derivation));


        // Keep track of where we are in the file for error reporting
        Position pos = new Position();
        ArrayList<Exception> errors = new ArrayList<>();

        // Get first token
        Tuple<String, String> tknTup = getNextToken(buffer, pos, errors);


        // Assignment 3
        Analyzer semanticAnalyzer = new Analyzer();

        while(true) {
            String X = stack.get(stack.size()-1);

            // We are at the end of the file
            if(X.equals(END) && tknTup.getX().equals(END)) {
                // All good
                break;
            }


            if(followSet.isTerminal(X)) {
                // If we have found a token pop it and move on
                if(X.equals(tknTup.getX().toLowerCase()) || X.equals(tknTup.getX().toUpperCase())) {
                    stack.pop();
                    semanticAnalyzer.evaluate(tknTup, pos);


                    // Add derivation rule
                    derivation = createDerivation(dIndex, derivation, tknTup.getY());
                    allDerivations.add(new ArrayList<>(derivation));
                    dIndex++;

                    // Get next token
                    tknTup = getNextToken(buffer, pos, errors);
                    if(tknTup == null){
                        // Unable to recover
                        // Could be an unexpected end of file
                        // Impossible to parse
                        errors.add(new SyntacticException(pos));
                        buffer.close();
                        break;
                    }
                } else {
                    // We have an error - we encountered a terminal
                    errors.add(new SyntacticException(pos,tknTup.getY()));
                    // but not the one we want - get next token and keep moving
                    tknTup = getNextToken(buffer, pos, errors);
                    if(tknTup == null){
                        // Unable to recover
                        // Could be an unexpected end of file
                        // Impossible to parse
                        errors.add(new SyntacticException(pos));
                        buffer.close();
                        break;
                    }
                }
            } else {
                // We have a terminal
                // We must replace it on the stack with its Right Hand Side
                Integer rule = table.get(X, tknTup.getX().toLowerCase());
                if(rule == null) rule = table.get(X, tknTup.getX().toUpperCase());
                if(rule == null || rules.get(rule) == null) {
                    // We have an error
                    // Capture the error and move on in the hopes we find the correct
                    // symbol to provide a rule soon
                    errors.add(new SyntacticException(pos, table.get(X), tknTup.getY()));
                    tknTup = getNextToken(buffer, pos, errors);
                    if(tknTup == null) {
                        // Unable to recover
                        // Could be an unexpected end of file
                        // Impossible to parse
                        errors.add(new SyntacticException(pos));
                        break;
                    }
                    continue;
                }

                // We have a rule to replace with on the stack

                Rule r = rules.get(rule);
                String poppedToken = stack.pop();

                ArrayList<String> RHS = r.getRHS();

                semanticAnalyzer.evaluate(poppedToken, r.getSemanticRHS(), pos);

                for (int k = RHS.size() - 1; k > -1; --k) {
                    // Assignment 3 - avoid semantic rules
                    if (!RHS.get(k).equals(EPSILON) &&
                            !RHS.get(k).contains("SEMANTIC")) {
                        stack.push(RHS.get(k));
                    }
                }


                // new line to derivations
                derivation = createDerivation(dIndex, derivation, RHS);
                allDerivations.add(new ArrayList<>(derivation));


                // We found our rule so we can move on
                //break;

            }
        }

        buffer.close();


        if(errors.size() == 0) {
            semanticAnalyzer.analyze(errors);
        }

        Tuple rtnTuple = new Tuple();
        rtnTuple.setX(errors);
        rtnTuple.setY(allDerivations);


        return rtnTuple;
    }

    /**
     *
     * @param dIndex int current location in derivation
     * @param derivation ArrayList of currents derivation
     * @param token String token we are replacing in derivation
     * @return
     */
    private ArrayList<String> createDerivation(int dIndex, ArrayList<String> derivation, String token) {

        derivation.set(dIndex, token);
        return derivation;
    }

    /**
     *
     * @param dIndex int current location in derivation
     * @param derivation ArrayList of currents derivation
     * @param RHS String of tokens to be added to derivation
     * @return
     */
    private ArrayList<String> createDerivation(int dIndex, ArrayList<String> derivation,
                                  ArrayList<String> RHS) {

        ArrayList<String> tmp = new ArrayList<>();

        derivation.remove(dIndex);
        // Create a derivation
        // Keep all derived so far
        for(int k=0; k<dIndex; k++){
            tmp.add(derivation.get(k));
        }
        // Then add the new rule
        for(int k=0;k<RHS.size();k++){
            if(!RHS.get(k).equals(EPSILON)) {
                tmp.add(RHS.get(k));
            }
        }
        // Now keep the remaining derivation to be done
        for(int k=dIndex; k < derivation.size(); k++){
            tmp.add(derivation.get(k));
        }

        return tmp;
    }


    /**
     *
     * @param buffer File we are reading from
     * @param pos Position we are at in the file
     * @param errors List of current errors found
     * @return Tuple contains token type and token value
     */
    private Tuple getNextToken(RandomAccessFile buffer, Position pos,
                              ArrayList<Exception> errors) throws IOException   {
        POS token;
        Tuple<String, String> tuple = new Tuple<>();
        try {
            // Use RandomAccessFile as we will commonly go backtrack one in the file
            if (buffer.getFilePointer() == buffer.length() - 1) {
                // EOF
                return null;
            }
            try {

                // Get the raw token
                token = tokenizer.getNextToken(buffer, pos);
                if(token == null) return null;
                // Update it if it is a reserved word
                if (token.getType() == Token.RESERVED) {
                    tuple.setX(token.getWord().toString());
                    tuple.setY(Reserved.valueOf(token.getWord().toString()).getWord());
                } else if (token.getType() == Token.EOF) {
                    // Update token if it is end of file
                    tuple.setX(END);
                    tuple.setY(token.getToken());
                } else {
                    // Prepare (type, token) return value
                    tuple.setX(token.getType().toString());
                    tuple.setY(token.getToken());
                }
            } catch (UnrecognizedCharacterException e) {
                // Unrecognized Character
                errors.add(e);
                return getNextToken(buffer, pos, errors);
            } catch (InvalidCharacterException fe) {
                // Invalid Character
                errors.add(fe);
                return getNextToken(buffer, pos, errors);
            }
        }catch(IOException e){
            // File reading errors
            throw e;
        }
        return tuple;

    }

}
