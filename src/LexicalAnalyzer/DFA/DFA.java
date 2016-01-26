package LexicalAnalyzer.DFA;

import java.io.*;
import java.util.HashMap;

public class DFA {


    public static void main(String[] args) throws Exception{

        Node root = new Node(0);

        String l = "[a-zA-Z]";
        String d = "[0-9]";
        String u = "_";
        String ocb = "{";
        String ccb = "}";
        String dot = ".";
        String amp = "&";
        String pipe = "|";
        String sp = " ";
        String cr = "\r";
        String lf = "\n";
        String tab = "\t";


        Graph g = new Graph(root);
        g.addNode(new Node(1, true, "id"));
        g.addNode(new Node(2));
        g.addNode(new Node(3, true, "ocb"));
        g.addNode(new Node(4, true, "ccb"));
        g.addNode(new Node(5));
        g.addNode(new Node(6, true, "id"));
        g.addNode(new Node(7, true, "id"));
        g.addNode(new Node(8));
        g.addNode(new Node(9, true, "and"));
        g.addNode(new Node(10, true, "num"));
        g.addNode(new Node(11));
        g.addNode(new Node(12, true, "or"));
        g.addNode(new Node(13, true, "sp"));

        g.addNode(new Node(14, true, "tab"));
        g.addNode(new Node(15, true, "lf"));
        g.addNode(new Node(16, true, "cr"));

        g.addEdge(0,1,Lexicon.LETTER);
        g.addEdge(0,2,Lexicon.DIGIT);
        g.addEdge(0,3,Lexicon.OCB);
        g.addEdge(0,4,Lexicon.CCB);
        g.addEdge(0,5,Lexicon.AMP);
        g.addEdge(1,6,Lexicon.LETTER);
        g.addEdge(1,7,Lexicon.DIGIT);
        g.addEdge(6,6,Lexicon.LETTER);
        g.addEdge(6,7,Lexicon.DIGIT);
        g.addEdge(7,6,Lexicon.LETTER);
        g.addEdge(7,7,Lexicon.DIGIT);
        g.addEdge(2,8,Lexicon.DOT);
        g.addEdge(10,10,Lexicon.DIGIT);
        g.addEdge(5,9,Lexicon.AMP);
        g.addEdge(8,10,Lexicon.DIGIT);
        g.addEdge(0,11, Lexicon.PIPE);
        g.addEdge(11,12, Lexicon.PIPE);
        g.addEdge(2,2, Lexicon.DIGIT);
        g.addEdge(0,13, Lexicon.SPACE);


        g.addEdge(0,14,Lexicon.TAB);
        g.addEdge(0,15,Lexicon.LF);
        g.addEdge(0,16,Lexicon.CR);


        HashMap<Lexicon, Integer> row = new HashMap<>();

        row.put(Lexicon.LETTER, -1);
        row.put(Lexicon.DIGIT, -1);
        row.put(Lexicon.UNDERSCORE, -1);
        row.put(Lexicon.OCB, -1);
        row.put(Lexicon.CCB, -1);
        row.put(Lexicon.DOT, -1);
        row.put(Lexicon.AMP, -1);
        row.put(Lexicon.PIPE, -1);
        row.put(Lexicon.SPACE, -1);

        row.put(Lexicon.CR, -1);
        row.put(Lexicon.LF, -1);
        row.put(Lexicon.TAB, -1);


        g.buildTST(row);

        System.out.println(g);


        File file = new File(args[0]);
        RandomAccessFile buffer = new RandomAccessFile(file, "r");


        POS partOfSpeech;
        Position pos = new Position();
        while(buffer.getFilePointer()  < buffer.length() - 1) {
            try {
                partOfSpeech = g.getNextToken(buffer, pos);
                System.out.println(partOfSpeech);
            } catch(Exception e){
                System.out.println(e);
            }
        }


        System.out.println("End of file");


    }





}