package SemanticAnalyzer;

import LexicalAnalyzer.DFA.Reserved;
import LexicalAnalyzer.DFA.Token;
import SyntacticAnalyzer.Rule;
import SyntacticAnalyzer.Tuple;

import java.util.ArrayList;
import java.util.HashMap;


public class VariableDeclaration {

    private String type;
    private String name;
    private ArrayList<Integer> sizes;

    private final ArrayList<Rule> rules = new ArrayList<Rule>() {{
        add(new Rule("", "id id arraySizeR SEMICOLON"));
        add(new Rule("", "int id arraySizeR SEMICOLON"));
        add(new Rule("", "float id arraySizeR SEMICOLON"));
        add(new Rule("", "type id arraySizeR SEMICOLON"));
        add(new Rule("", "for ( type id assignOp"));

        type = null;
        name = null;
        sizes = new ArrayList<>();
    }};

    public void setType(String type){
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArrayList(ArrayList<Integer> sizes) {
        this.sizes = sizes;
    }


    public VariableDeclaration declare(HashMap<Integer, Node> nodes) {
        // Get first leaf
        Integer lowest = null;
        for(Integer i : nodes.keySet()){
            if(lowest == null || i < lowest) {
                lowest = i;
            }
        }

        Node first = nodes.get(lowest);
        Tuple firstLeaf = first.getLeaf().getLeafValue();
        Node second = null;
        Tuple secondLeaf = null;
    //    System.out.println(firstLeaf);
        if(firstLeaf.getX().equals(Reserved.INT.toString()) ||
                firstLeaf.getX().equals(Reserved.FLOAT.toString()) ||
                firstLeaf.getX().equals(Token.ID.toString())) {
                    type = firstLeaf.getY().toString();
    //                System.out.println("Have variable");

                    second = first.getLeftSibling().getLeaf();
                      secondLeaf = second.getLeafValue();
        } else if(firstLeaf.getX().equals(Reserved.FOR.toString())){
            second = first.getLeftSibling().getLeaf();
            secondLeaf = second.getLeafValue();
            if(secondLeaf.getX().equals(Token.ORB.toString())) {
                Node third = second.getLeftSibling().getLeaf();
                Tuple thirdLeaf = third.getLeafValue();
                if(thirdLeaf.getX() == Reserved.INT.toString() ||
                thirdLeaf.getX().equals(Reserved.FLOAT.toString()) ||
                        thirdLeaf.getX().equals(Token.ID.toString())) {
                            type = thirdLeaf.getY().toString();
                            second = third.getLeftSibling().getLeaf();
                            secondLeaf = second.getLeafValue();
                } else {
                    return null;
                }

            } else {
                return null;
            }
        } else {
            return null;
        }


    //    System.out.println("\t"+secondLeaf);
        if(secondLeaf.getX().equals(Token.ID.toString())) {
            name = secondLeaf.getY().toString();
    //        System.out.println("We still have a variable");
        } else {
            return null;
        }

        Node third = second.getLeftSibling().getLeaf();
        Tuple thirdLeaf = third.getLeafValue();
    //    System.out.println("\t\t"+thirdLeaf);
        if(thirdLeaf.getX().equals("EPSILON")) {
            third = third.getLeftSibling().getLeaf();
            thirdLeaf = third.getLeafValue();
        }

    //    System.out.println("\t\t"+thirdLeaf);

        if(thirdLeaf.getX().equals(Token.SEMICOLON.toString()) ||
                thirdLeaf.getX().equals(Token.ASSIGNMENT.toString())) {
            // we are done
   //         System.out.println("We are done");
            return this;
        }

        while(thirdLeaf.getX().equals(Token.OSB.toString())) {
            Node fourth = third.getLeftSibling().getLeaf();
            Tuple<String, String> fourthLeaf = fourth.getLeafValue();
    //        System.out.println("\t\t\t1: " + fourthLeaf);

            Node fifth = fourth.getLeftSibling().getLeaf();
            Tuple fifthLeaf = fifth.getLeafValue();
   //         System.out.println("\t\t\t\t2: " + fifthLeaf);

            if(fourthLeaf.getX().equals(Token.INTEGER.toString()) &&
                    fifthLeaf.getX().equals(Token.CSB.toString())) {

                        sizes.add(Integer.parseInt(fourthLeaf.getY()));
    //                    System.out.println("Still have a variable");

                        third = fifth.getLeftSibling().getLeaf();
                        thirdLeaf = third.getLeafValue();
    //                    System.out.println("\t\t"+thirdLeaf);
            } else {
                return null;
            }


        }

        if(thirdLeaf.getX().equals("EPSILON")) {
            third = third.getLeftSibling().getLeaf();
            thirdLeaf = third.getLeafValue();
        }

    //    System.out.println("\t\t"+thirdLeaf);

        if(thirdLeaf.getX().equals(Token.SEMICOLON.toString())) {
            // we are done
    //        System.out.println("We are done");
            return this;
        }



        return null;
    }

    public VariableDeclaration fParams(HashMap<Integer, Node> nodes) {
        // Get first leaf
        Integer lowest = null;
        for(Integer i : nodes.keySet()){
            if(lowest == null || i < lowest) {
                lowest = i;
            }
        }

        Node first = nodes.get(lowest);
        if(first.getFirstLeafType().equals(Token.COMMA.toString())) {
            first = first.getLeftSibling();
        }

        if(first.getFirstLeafType().equals(Reserved.INT.toString()) ||
                first.getFirstLeafType().equals(Reserved.FLOAT.toString()) ||
                first.getFirstLeafType().equals(Token.ID.toString())) {
                    type = first.getFirstLeafValue();

                Node ID = first.getLeftSibling();
                if(ID.getFirstLeafType().equals(Token.ID.toString())) {
                    name = ID.getFirstLeafValue();
                }
                Node ARRAY = ID.getLeftSibling();
                if(ARRAY.getValue().equals("arraySizeR")) {
                    evaluateArray(ARRAY);
                    return this;
                }
        }
        return null;

    }

    private void evaluateArray(Node array) {
        ArrayList<Tuple> tokens = array.getTokens();
        for(Tuple tkn : tokens) {
            if (tkn.getX().equals(Token.INTEGER.toString())) {
                sizes.add(Integer.parseInt(tkn.getY().toString()));
            }
        }
    }

    @Override
    public String toString() {
        String rtn = "";
        rtn += type + " " + name;
        for(Integer s: sizes) {
            rtn += "["+s+"]";
        }
        return rtn;
    }
}
