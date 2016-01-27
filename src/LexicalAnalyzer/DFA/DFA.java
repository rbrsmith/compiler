package LexicalAnalyzer.DFA;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DFA {

    private Graph g;

    public DFA() {
        Node root = new Node(0);

        String regex = "(l(l|d|_)*)|(([nz]d*)|0)|((([nz]d*)|0)(.d*[nz])|.0)";

        g = new Graph(root);
        g.addNode(new Node(1, true, Token.ID));
        g.addNode(new Node(2, true, Token.INTEGER));
        g.addNode(new Node(3, true, Token.INTEGER));
        g.addNode(new Node(4));
        g.addNode(new Node(5, true, Token.ID));
        g.addNode(new Node(6, true, Token.ID));
        g.addNode(new Node(7, true, Token.ID));
        g.addNode(new Node(8, true, Token.INTEGER));
        g.addNode(new Node(9));
        g.addNode(new Node(10, true, Token.FLOAT));
        g.addNode(new Node(11));
        g.addNode(new Node(12, true, Token.FLOAT));

        g.addEdge(0,1,Lexicon.LETTER);
        g.addEdge(0,2,Lexicon.NZERO);
        g.addEdge(0,3,Lexicon.ZERO);
        g.addEdge(0,4,Lexicon.DOT);

        g.addEdge(1,5,Lexicon.LETTER);
        g.addEdge(1,6, Lexicon.NZERO);
        g.addEdge(1,6, Lexicon.ZERO);
        g.addEdge(1,7,Lexicon.UNDERSCORE);

        g.addEdge(2,8,Lexicon.NZERO);
        g.addEdge(2,8,Lexicon.ZERO);
        g.addEdge(2,9,Lexicon.DOT);

        g.addEdge(3,9, Lexicon.DOT);

        g.addEdge(4,10,Lexicon.ZERO);

        g.addEdge(5,5,Lexicon.LETTER);
        g.addEdge(5,6,Lexicon.NZERO);
        g.addEdge(5,6,Lexicon.ZERO);
        g.addEdge(5,7,Lexicon.UNDERSCORE);

        g.addEdge(6,6,Lexicon.NZERO);
        g.addEdge(6, 6, Lexicon.ZERO);
        g.addEdge(6,5,Lexicon.LETTER);
        g.addEdge(6,7,Lexicon.UNDERSCORE);

        g.addEdge(7,7,Lexicon.UNDERSCORE);
        g.addEdge(7,5,Lexicon.LETTER);

        g.addEdge(8,8,Lexicon.NZERO);
        g.addEdge(8,8,Lexicon.ZERO);
        g.addEdge(8,9,Lexicon.DOT);

        g.addEdge(9,11,Lexicon.NZERO);
        g.addEdge(9,11,Lexicon.ZERO);
        g.addEdge(9,12,Lexicon.NZERO);

        g.addEdge(11,11,Lexicon.NZERO);
        g.addEdge(11,11,Lexicon.ZERO);
        g.addEdge(11,12,Lexicon.NZERO);

        g.addEdge(12,12,Lexicon.NZERO);





    }


    public ArrayList<POS> getTags(File file) {
        ArrayList<POS> tags = new ArrayList<>();
        try {
            RandomAccessFile buffer = new RandomAccessFile(file, "r");
            if(buffer.length() == 1) {
                buffer.close();
                throw new IOException();
            }
            POS partOfSpeech;
            Position pos = new Position();
            while (buffer.getFilePointer() < buffer.length() - 1) {
                try {
                    partOfSpeech = g.getNextToken(buffer, pos);
                    tags.add(partOfSpeech);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
            buffer.close();
        } catch(IOException e) {
            System.out.println("Error reading file.");
        }
        return tags;
    }

    public Graph getGraph() {
        return g;
    }

    @Override
    public String toString() {
        return g.toString();
    }




}