package LexicalAnalyzer.DFA;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.RandomAccessFile;
import java.util.*;


/**
 * Di-Graph object that represents a series of states
 * and the character edges between them
 */
public class Graph {

    // Nodes indexed by their ID
    HashMap<Integer, Node> nodes;
    // Edges indexed by source node ID
    HashMap<Integer, ArrayList<Edge>> edges;

    // Transition State Table
    HashMap<Integer, HashMap<Lexicon, Integer>> tst;

    // Sample row of Transition State Table - each Leixcon points to a new state (Integer)
    HashMap<Lexicon, Integer> header;


    /**
     *
     * @param n Node which will be used as root
     */
    public Graph(Node n){
        nodes = new HashMap<>();
        edges = new HashMap<>();
        this.addNode(n);

        header = new HashMap<>();

        // Set default row of TST to point to -1 for every possible character edge
        for(Lexicon lex : Lexicon.values()) {
            header.put(lex, -1);
        }
    }

    /**
     *
     * @param n Node to be added to graph
     */
    public void addNode(Node n) {
        // Do not add node if it already exists
        Node root = nodes.get(n.getID());
        if(root != null){
            return;
        }
        nodes.put(n.getID(), n);
        edges.put(n.getID(), new ArrayList<>());
    }

    /**
     *
     * @param s Node that is source of the edge path
     * @param d Node that is destination of the source path
     * @param t The Lexicon character that is traversed on this edge
     */
    public void addEdge(Node s, Node d, Lexicon t){
        ArrayList<Edge> e = edges.get(s.getID());
        if(e == null) {
            edges.put(s.getID(), new ArrayList<>());
        }

        edges.get(s.getID()).add(new Edge(s, d, t));
    }

    /**
     *
     * @param s Node ID that is source of the edge path
     * @param d Node ID that is destination of the source path
     * @param t The Lexicon character that is traversed on this edge
     */
    public void addEdge(int s, int d, Lexicon t) {
        Node source = nodes.get(s);
        Node dest = nodes.get(d);
        this.addEdge(source, dest, t);
    }


    /**
     * Constructs a Transition State Table from the existing graph
     */
    public void buildTST() {
        ArrayList<Integer> visited = new ArrayList<>();
        Stack<Node> s = new Stack<>();
        s.push(nodes.get(0));
        tst = new HashMap<>();

        while(!s.isEmpty()){
            Node u = s.pop();
            if(visited.contains(u.getID())) {
                continue;
            }
            visited.add(u.getID());

            // Get or build this states row
            HashMap<Lexicon, Integer> uRow = tst.get(u.getID());
            if(uRow == null) {
                uRow = new HashMap<>(header);
            }

            // Set all the possible state destinations based on character edge
            ArrayList<Edge> es = edges.get(u.getID());
            for(Edge e: es) {
                Node v = e.getDest();
                uRow.put(e.getToken(), v.getID());
                tst.put(u.getID(), uRow);

                if(tst.get(v.getID()) == null) {
                    tst.put(v.getID(), new HashMap<>(header));
                }

                s.push(v);
            }
        }
    }


    /**
     *
     * @return String representation of the TST
     */
    public String toString() {
        if(tst == null) {
            buildTST();
        }

        String output = "";
        if(tst.get(0) == null) {
            return output;
        }

        output += String.format("%s\t\t", "IS");
        output += String.format("%s\t\t", "Fin");
        for(Lexicon s : tst.get(0).keySet()) {
            output += String.format("%s\t\t", s.getAbbr());
        }

        output += String.format("%s", "\n");

        for(Map.Entry<Integer, HashMap<Lexicon,Integer>> h : tst.entrySet()) {
            Integer row = h.getKey();
            HashMap<Lexicon, Integer> values = h.getValue();

            output += String.format("%d\t\t", row);
            output += String.format("%s\t\t", (nodes.get(row).isLeaf() ? "Y" : "N"));
            for(Lexicon s : tst.get(0).keySet()) {
                output += String.format("%s\t\t", values.get(s));
            }

            output += String.format("%s", "\n");
        }
        return output;
    }

    /**
     *
     * @param s String to be turned into it's Lexicon value
     * @return Lexicon representing s
     */
    public Lexicon getLexicon(String s) {
        // To simplify the regex we use some base regex for the character
        String l = "[a-zA-Z]";
        String z = "[0]";
        String nz = "[1-9]";

        if(s.matches(l)) {
            return Lexicon.LETTER;
        }
        if(s.matches(z)) {
            return Lexicon.ZERO;
        }
        if(s.matches(nz)) {
            return Lexicon.NZERO;
        }
        for(Lexicon val :Lexicon.values()) {
            if(val.toString().equals(s)) {
                return val;
            }
        }
        return null;
    }


    /**
     * Get the next full token in the file
     *
     * @param filePointer RandomAccessFile pointing to the next character to be read
     * @param pos Position object representing place in file
     * @return POS object containing the unclean tag and token
     * @throws InvalidCharacterException when a character is read that leads to an invalid state, and the current state isn't a terminal
     * @throws UnrecognizedCharacterException when a character that is not part of the TST is encountered
     * @throws IOException when the file cannot be read
     */
    public POS getNextToken(RandomAccessFile filePointer, Position pos)
            throws InvalidCharacterException, UnrecognizedCharacterException, IOException {

        if(tst == null) {
            buildTST();
        }

        // Start at root
        Integer state = 0;

        // Input
        String str;

        // Aim to build token
        String token = "";

        if(tst.get(0) == null) {
            throw new InvalidCharacterException(pos, token, "");
        }

        while(true) {
            // Get next character
            char c = (char) filePointer.read();
            pos.incChar();
            str = "" + c;
            Lexicon reStr = this.getLexicon(str);

            // Is the token valid
            if(reStr == null && ((int) c != 65535)) {
                if(nodes.get(state).getType() == Token.LINE_FEED) {
                    pos.newLine();
                }
                throw new UnrecognizedCharacterException(pos, token, str);
            }

            // Is there another state we can go to?
            Integer nextState = tst.get(state).get(reStr);
            if(nextState == null || nextState == -1) {

                // No next state.
                if(!nodes.get(state).isLeaf()) {
                    throw new InvalidCharacterException(pos, token, str);
                } else {
                    // If we are at a terminal we have a valid token

                    POS partOfSpeech = new POS(token, nodes.get(state).getType());
                    // Update our position in the file
                    if(nodes.get(state).getType() == Token.LINE_FEED) {
                        pos.newLine();
                    }
                    // Back track one
                    if((int) c != 65535) {
                        filePointer.seek(filePointer.getFilePointer() - 1);
                    }
                    pos.decChar();

                    return partOfSpeech;
                }
            } else {
                // Append to token and move to the next state
                token += str;
                state = nextState;
            }
        }
    }
}