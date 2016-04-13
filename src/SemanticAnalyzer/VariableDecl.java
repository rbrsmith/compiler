package SemanticAnalyzer;

import LexicalAnalyzer.DFA.Reserved;
import SemanticEvaluation.Variable;
import SemanticEvaluation.VariableReference;

import java.util.ArrayList;


/**
 * Class representing a variable declaration
 */
public class VariableDecl implements Declaration {

    private String type;
    private String name;
    private boolean initialize;


    // Size of variable
    // Ex:  int a[5][4];
    private ArrayList<Integer> sizes;
    private ArrayList<VariableDecl> attributes;


    public VariableDecl(Node id, Node type, Node array) {
        this.name = (String) id.getLeafValue().getY();
        this.type = (String) type.getLeafValue().getY();

        sizes = new ArrayList<>();
        if(array != null) analyzeArray(array);

        attributes = new ArrayList<>();
        initialize = false;



    }

    public VariableDecl(VariableDecl v, boolean initialize) {
        this.name = v.name;
        this.type = v.type;
        this.sizes = v.sizes;
        this.attributes = v.attributes;
        this.initialize = initialize;

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
        String rtn = "";
        if(initialize) {
            rtn += "INITIALIZED ";
        } else {
            rtn += "UN-INITIALIZED ";
        }
        rtn += "VAR\t\tType: "+type+",\tName: "+name;
        if(sizes.size() != 0) rtn += ",\tSize: ";
        for(Integer i : sizes) {
            rtn += "["+i+"]";
        }
        for(VariableDecl vd: attributes) {
            rtn += "\n\t"+vd.toString();
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

    public void initialize() {
        this.initialize = true;
    }

    public boolean isInitialized() {
        return initialize;
    }

    /**
     *
     * If this declared is complex (foo.bar)
     * Check to see if bar is registered with this foo object
     *
     * @param attribute Variable we want to look up
     * @return VariableDecl of attribute
     */
    public VariableDecl getAttribute(Variable attribute) {
        for(VariableDecl v : attributes) {
            if(v.getName().equals(attribute.getName())) {
                return v;
            }
        }
        return null;
    }


    /**
     *
     * @param attributes List of VariableDecl to be set as attributes
     */
    public void setAttributes(ArrayList<VariableDecl> attributes) {
        for(VariableDecl vd: attributes) {
            this.attributes.add(new VariableDecl(vd, false));
        }
    }

    public ArrayList<VariableDecl> getAttributes() {
        return attributes;
    }


}
