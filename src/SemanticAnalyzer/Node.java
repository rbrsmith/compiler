package SemanticAnalyzer;

import LexicalAnalyzer.DFA.Position;
import SyntacticAnalyzer.Tuple;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Node {

    private String value;
    private Tuple<String, String> leafValue;
    private static int id;
    private int nodeid;
    private boolean isLeaf;
    private boolean isRoot;
    public HashMap<Integer, Node> children;

    Position pos;

    private Node parent;

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

    // to undo put to +1
    public Node getLeftSibling() {
        Node current = this;
        while(!current.isRoot()) {
            if(current.getParent().getChild(current.getNodeID() - 1) != null) {
                return current.getParent().getChild(current.getNodeID() - 1);
            } else {
                current = current.getParent();
            }
        }
        return null;
    }

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

    public void print(int level) {
        for(int i=0; i<level;i++){
            System.out.print("\t");
        }
        if(isLeaf()) System.out.println(leafValue);
        else System.out.println(value);
        for(Node child : children.values()) {
            child.print(level + 1);
        }

    }

    public Node getLeaf() {
        Node current = this;
        while(!current.isLeaf()){
            current = current.getFirstChild();
        }
        return current;
    }

    public String getFirstLeafType() {
        return (String) getLeaf().getLeafValue().getX();
    }

    public String getFirstLeafValue() {
        return (String) getLeaf().getLeafValue().getY();
    }

    public ArrayList<Tuple> getTokens() {
        ArrayList<Tuple> res = new ArrayList<>();
        Node current = this;
        Node next = current.getLeftSibling();
        while(current.getNodeID() != next.getNodeID()) {
            Node currentLeaf = current.getLeaf();
            Tuple tkn = currentLeaf.getLeafValue();
            if(!tkn.getX().equals("EPSILON")) res.add(tkn);
            current = currentLeaf.getLeftSibling();
        }
        return res;
    }

    public ArrayList<Node> getChildrenValues() {
        List<Integer> sortedKeys=new ArrayList(children.keySet());
        Collections.sort(sortedKeys);

        ArrayList<Node> vals = new ArrayList<>();
        for(Integer i: sortedKeys) {
            vals.add(children.get(i));
        }
        return vals;

    }

    public Node getRightSibling() {
        Node current = this;
        while(!current.isRoot()) {

            if(current.getParent().getChild(current.getNodeID() + 1) != null) {
                return current.getParent().getChild(current.getNodeID() + 1);
            } else {
                current = current.getParent();
            }
        }
        return null;
    }

    public Position getPosition() {
        return pos;
    }
}
