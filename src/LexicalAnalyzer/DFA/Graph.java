package LexicalAnalyzer.DFA;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.RandomAccessFile;
import java.util.*;

public class Graph {

    private Node root;

    HashMap<Integer, Node> nodes;
    HashMap<Integer, ArrayList<Edge>> edges;

    HashMap<Integer, HashMap<Lexicon, Integer>> tst;

    HashMap<Lexicon, Integer> header;


    public Graph(Node n){
        nodes = new HashMap<>();
        edges = new HashMap<>();
        this.root = n;
        this.addNode(n);

        header = new HashMap<>();

        for(Lexicon lex : Lexicon.values()) {
            header.put(lex, -1);
        }
    }


    public void addNode(Node n) {
        nodes.put(n.getID(), n);
        edges.put(n.getID(), new ArrayList<>());
    }

    public void addEdge(Node s, Node d, Lexicon t){
        ArrayList<Edge> e = edges.get(s.getID());
        if(e == null) {
            edges.put(s.getID(), new ArrayList<>());
        }

        edges.get(s.getID()).add(new Edge(s, d, t));
    }

    public void addEdge(int s, int d, Lexicon t) {
        Node source = nodes.get(s);
        Node dest = nodes.get(d);
        this.addEdge(source, dest, t);
    }


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

            HashMap<Lexicon, Integer> uRow = tst.get(u.getID());
            if(uRow == null) {
                uRow = new HashMap<>(header);
            }

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


    public Lexicon getLexicon(String s) {

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


    public POS getNextToken(RandomAccessFile filePointer, Position pos) throws Exception {

        if(tst == null) {
            buildTST();
        }

        Integer state = 0;

        String str = "";
        String token = "";

        if(tst.get(0) == null) {
            throw new InvalidCharacterException(pos, token);
        }

        while(true) {


            char c = (char) filePointer.read();
            pos.incChar();
            str = "" + c;

            Lexicon reStr = this.getLexicon(str);
            if(reStr == null && ((int) c != 65535)) {
                throw new UnrecognizedCharacterException(pos, token);
            }
            Integer nextState = tst.get(state).get(reStr);
            if(nextState == null || nextState == -1) {
                // Are we done or at error
                if(!nodes.get(state).isLeaf()) {
                    throw new InvalidCharacterException(pos, token);
                } else {
                    POS partOfSpeech = new POS(token, nodes.get(state).getType());

                    if(nodes.get(state).getType() == Token.LINE_FEED) {
                        pos.newLine();
                    }

                    if((int) c != 65535) {
                        filePointer.seek(filePointer.getFilePointer() - 1);
                    }
                    pos.decChar();
                    return partOfSpeech;
                }
            } else {
                token += str;
                state = nextState;
                continue;
            }
        }
    }
}