package LexicalAnalyzer.DFA;

public class Edge {

    private Node source;
    private Node dest;
    private Lexicon token;


    public Edge(Node s, Node d, Lexicon t){
        this.source = s;
        this.dest = d;
        this.token = t;
    }

    public Node getDest(){
        return this.dest;
    }

    public Node getSource() {
        return this.source;
    }

    public Lexicon getToken() {
        return this.token;
    }


}