package SemanticAnalyzer;

import LexicalAnalyzer.DFA.Position;
import SyntacticAnalyzer.Grammar;
import SyntacticAnalyzer.Tuple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Class representing a node in the parse tree
 */
public class Node {

    // Value holds non terminal value
    // Leaf Value hold a leaf value
    private String value;
    private Tuple<String, String> leafValue;
    private static int id;
    private int nodeid;
    private boolean isLeaf;
    private boolean isRoot;
    public HashMap<Integer, Node> children;

    Position pos;

    private Node parent;

    /**
     *  Non Terminal constructor
     */
    public Node(String value, boolean isLeaf, boolean isRoot, Node parent, Position pos) {
        this.value = value;
        this.leafValue = null;
        this.isLeaf = isLeaf;
        this.isRoot = isRoot;
        this.parent = parent;
        this.children = new HashMap<>();
        this.nodeid = id;
        this.pos = new Position(pos);
        id += 1;
    }

    /**
     *  Terminal constructor
     */
    public Node(Tuple value, boolean isLeaf, boolean isRoot, Node parent, Position pos) {
        this.value = null;
        this.leafValue = value;
        this.isLeaf = isLeaf;
        this.isRoot = isRoot;
        this.parent = parent;
        this.children = new HashMap<>();
        this.nodeid = id;
        this.pos = new Position(pos);
        id += 1;
    }

    public boolean isLeaf() {
        return isLeaf;
    }
    public boolean isRoot() {
        return isRoot;
    }
    public String getValue() {
        return value;
    }
    public Tuple getLeafValue() {
        return leafValue;
    }
    public Node getParent() {
        return parent;
    }
    public int getNodeID() {
        return nodeid;
    }
    public void addChild(Node child) {
        children.put(child.getNodeID(), child);
    }
    public Node getChild(int id) {
        return children.get(id);
    }
    public Position getPosition() {
        return pos;
    }

    /**
     *
     * @return Node to the left of this
     */
    public Node getLeftSibling() {
        Node current = this;
        while(!current.isRoot()) {
            if(current.getParent().getChild(current.getNodeID() - 1) != null) {
                // Return left sibling on same level
                return current.getParent().getChild(current.getNodeID() - 1);
            } else {
                // Get parents sibling
                current = current.getParent();
            }
        }
        return null;
    }

    /**
     *
     * @return Node representing first child of this
     */
    public Node getFirstChild() {
        Integer lowest = null;
        for(Integer child : children.keySet()) {
            if(lowest == null || child < lowest) {
                lowest = child;
            }
        }
        if(lowest == null ) return null;
        else return children.get(lowest);
    }

    @Override
    public String toString() {
        if(isLeaf()) return "{"+getLeafValue()+"}";
        else return "{"+getValue()+"}";
    }

    /**
     *
     * @return Node first leaf of tree at current node
     */
    public Node getLeaf() {
        Node current = this;
        while(!current.isLeaf()){
            current = current.getFirstChild();
            if(current == null) return null;
        }
        return current;
    }

    /**
     *
     * @return String Type of first leaf of tree at current node
     */
    public String getFirstLeafType() {
        return (String) getLeaf().getLeafValue().getX();
    }

    /**
     *
     * @return String Value of first leaf of tree at current node
     */
    public String getFirstLeafValue() {
        return (String) getLeaf().getLeafValue().getY();
    }

    /**
     *
     * @return ArrayList of Tuples representing all tokens at leaf of the tree at current node
     */
    public ArrayList<Tuple> getTokens() {
        ArrayList<Tuple> res = new ArrayList<>();
        Node current = this;
        Node next = current.getRightSibling();
        while(current != null) {
            Node currentLeaf = current.getLeaf();
            if(current.getValue().contains(Analyzer.SEMANTIC)) {
                current = current.getRightSibling();
                continue;
            }
            if(currentLeaf == null) {
                break;
            }
            Tuple tkn = currentLeaf.getLeafValue();

            if(!tkn.getX().equals(Grammar.EPSILON)) res.add(tkn);

            current = currentLeaf.getRightSibling();
        }
        return res;
    }

    /**
     *
     * @return ArrayList of Node representing the children to this node
     */
    public ArrayList<Node> getChildrenValues() {
        List<Integer> sortedKeys=new ArrayList(children.keySet());
        Collections.sort(sortedKeys);

        ArrayList<Node> vals = new ArrayList<>();
        for(Integer i: sortedKeys) {
            vals.add(children.get(i));
        }
        return vals;

    }

    /**
     *
     * @return Node to the right of this
     */
    public Node getRightSibling() {
        Node current = this;
        while (!current.isRoot()) {
            if (current.getParent().getChild(current.getNodeID() + 1) != null) {
                // Return node to the right of this on the same level
                return current.getParent().getChild(current.getNodeID() + 1);
            } else {
                // Return node to the right of the parent
                current = current.getParent();
            }
        }
        return null;
    }
}
