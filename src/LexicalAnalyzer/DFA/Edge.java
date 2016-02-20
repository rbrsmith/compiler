package LexicalAnalyzer.DFA;

/**
 * Edge class to hold a graph's edge
 * Edges represent an input character
 */
public class Edge {

    private Node source;
    private Node dest;
    // Lexicon that represents that edges path
    private Lexicon character;

    public Edge(Node s, Node d, Lexicon t){
        this.source = s;
        this.dest = d;
        this.character = t;
    }

    public Node getDest(){
        return this.dest;
    }

    public Node getSource() {
        return this.source;
    }

    public Lexicon getToken() {
        return this.character;
    }


}