package LexicalAnalyzer.DFA;

/**
 * Class represents a node in the grade
 * Each node represents a state
 */
public class Node {

    private int id;
    // is Terminal
    private boolean leaf = false;
    private Token type = null;

    /**
     *
     * @param id int node id
     */
    public Node(int id){
        this.id = id;
    }

    /**
     *
     * @param id int node id
     * @param leaf boolean is an end point
     * @param type Token type if traversal ends here
     */
    public Node(int id, boolean leaf, Token type) {
        this.id = id;
        this.leaf = leaf;
        this.type = type;
    }

    /**
     *
     * @return int node id
     */
    public int getID(){
        return this.id;
    }

    /**
     *
     * @return true if node is a terminal | false otherwise
     */
    public boolean isLeaf() {
        return leaf;
    }

    /**
     *
     * @return Token of nodes type
     */
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