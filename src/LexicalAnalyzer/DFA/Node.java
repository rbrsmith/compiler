package LexicalAnalyzer.DFA;
public class Node {

    private int id;
    private boolean leaf = false;
    private String type = null;

    public Node(int id){
        this.id = id;
    }

    public Node(int id, boolean leaf, String type) {
        this.id = id;
        this.leaf = leaf;
        this.type = type;
    }

    public int getID(){
        return this.id;
    }

    public boolean isLeaf() {
        return leaf;
    }


    public String getType() {
        return this.type;
    }
}