package SemanticAnalyzer;

import LexicalAnalyzer.DFA.Reserved;

import java.util.ArrayList;


/**
 * Class representing a variable declaration
 */
public class VariableDecl implements Declaration {

    private String type;
    private String name;
    // Size of variable
    // Ex:  int a[5][4];
    private ArrayList<Integer> sizes;

    public VariableDecl(Node id, Node type, Node array) {
        this.name = (String) id.getLeafValue().getY();
        this.type = (String) type.getLeafValue().getY();

        sizes = new ArrayList<>();
        if(array != null) analyzeArray(array);
    }

    /**
     *
     * @return True if this variable is primitive | False otherwise
     */
    public boolean isPrimitive() {
        if(type.equals(Reserved.INT.getWord()) ||
                type.equals(Reserved.FLOAT.getWord())) {
            return true;
        } else {
            return false;
        }
    }

    public String getName() {
        return name;
    }

    public String toString() {
        String rtn = "VARIABLE\t\tType: "+type+",\tName: "+name;
        if(sizes.size() != 0) rtn += ",\tSize: ";
        for(Integer i : sizes) {
            rtn += "["+i+"]";
        }
        return rtn;
    }

    /**
     * Add integers to sizes array if required
     *
     * @param array Node representing root of an arraySizeR tree
     */
    private void analyzeArray(Node array) {
        for (Node child : array.getChildrenValues()) {
            if (!child.isLeaf() && child.getValue().equals(Analyzer.ARR_ACTION)) {
                Integer integer = Integer.parseInt(child.getLeftSibling().getFirstLeafValue());
                sizes.add(integer);
            }
            analyzeArray(child);

        }
    }

    public ArrayList<Integer> getSize() {
        return sizes;
    }

    public String getType() {
        return type;
    }
}
