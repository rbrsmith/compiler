package LexicalAnalyzer.DFA;
public class Node {

    private int id;
    private boolean leaf = false;
    private Token type = null;

    public Node(int id){
        this.id = id;
    }

    public Node(int id, boolean leaf, Token type) {
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

    public Token getType() {
        return this.type;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()){
            return false;
        }
        Node n = (Node) obj;
        if(n.getID() == getID() &&
                n.getType() == getType() &&
                n.isLeaf() == isLeaf()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return getID() / 11;
    }
}