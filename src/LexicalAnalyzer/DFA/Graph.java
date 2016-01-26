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


    public Graph(Node n){
        nodes = new HashMap<>();
        edges = new HashMap<>();
        this.root = n;
        this.addNode(n);
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

    public void addEdge(int s, int d, Lexicon t) throws Exception {
        Node source = nodes.get(s);
        Node dest = nodes.get(d);

        if(source == null || dest == null) {
            throw new Exception();
        } else {
            this.addEdge(source, dest, t);


        }
    }


    public void buildTST(HashMap<Lexicon, Integer> row) {
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
                uRow = new HashMap<>(row);
            }

            ArrayList<Edge> es = edges.get(u.getID());
            for(Edge e: es) {
                Node v = e.getDest();
                uRow.put(e.getToken(), v.getID());
                tst.put(u.getID(), uRow);

                if(tst.get(v.getID()) == null) {
                    tst.put(v.getID(), new HashMap<>(row));
                }

                s.push(v);
            }
        }
    }


    public String toString() {
        String output = "";

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
        String d = "[0-9]";

        if(s.matches(l)) {
            return Lexicon.LETTER;
        }
        if(s.matches(d)) {
            return Lexicon.DIGIT;
        }
        for(Lexicon val :Lexicon.values()) {
            if(val.toString().equals(s)) {
                return val;
            }
        }
        return null;

    }

    public String changeRange(Lexicon s) {

        String l = "[a-zA-Z]";
        String d = "[0-9]";
        String cr = "\r";
        String lf = "\n";
        String tab = "\t";
        String sp = " ";

        if(s == Lexicon.LETTER) {
            return "l";
        }
        if(s == Lexicon.DIGIT) {
            return "d";
        }
        if(s == Lexicon.CR) {
            return "cr";
        }
        if(s == Lexicon.LF) {
            return "lf";
        }
        if(s == Lexicon.TAB) {
            return "tb";
        }
        if(s == Lexicon.SPACE) {
            return "sp";
        }
        return s.toString();

    }

    public POS getNextToken(RandomAccessFile filePointer, Position pos) throws Exception {
        Integer state = 0;

        String str = "";
        String token = "";
        while(true) {

            char c = (char) filePointer.read();
            pos.incChar();
            str = "" + c;

            Lexicon reStr = this.getLexicon(str);
            if(reStr == null && ((int) c != 65535)) {
                throw new UnrecognizedCharacterException(pos);
            }
            Integer nextState = tst.get(state).get(reStr);
            if(nextState == null || nextState == -1) {
                // Are we done or at error
                if(!nodes.get(state).isLeaf()) {
                    throw new InvalidCharacterException(pos);
                } else {
                    POS partOfSpeech = new POS(token, nodes.get(state).getType());

                    if(nodes.get(state).getType().equals("lf")) {
                        pos.newLine();
                    }

                    filePointer.seek(filePointer.getFilePointer() - 1);
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