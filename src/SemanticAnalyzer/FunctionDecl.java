package SemanticAnalyzer;

import LexicalAnalyzer.DFA.Reserved;

import java.util.ArrayList;

/**
 * CLass holding a function declaration
 */
public class FunctionDecl implements Declaration {

    private String name;
    private String type;
    // List of parameters passed to the function
    private ArrayList<VariableDecl> params;

    public FunctionDecl(Node id, Node type, Node fParams) {
        this.name = (String) id.getLeafValue().getY();
        this.type = (String) type.getLeafValue().getY();
        params = new ArrayList<>();
        analyzeFParams(fParams);

    }

    /**
     * Recursively examine every node in a tree starting at fParams
     * Create variable declarations where required
     * @param fParams Node representing root of fParams tree
     */
    private void analyzeFParams(Node fParams) {
        for(Node child: fParams.getChildrenValues()) {
            if(!child.isLeaf() && child.getValue().equals(Analyzer.FUNC_ACTION_2)){
                Node type = child.getLeftSibling().getLeftSibling().getLeftSibling().getLeaf();
                Node id = type.getRightSibling().getLeaf();
                Node array = id.getRightSibling();
                VariableDecl v = new VariableDecl(id, type, array);
                v.initialize();
                params.add(v);
                // Add v to table
            }
            analyzeFParams(child);

        }
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

    @Override
    public String toString() {
        String rtn = "FUNCTION\t\tType: "+type+",\tName: "+name;
        if(params.size() != 0) {
            rtn += "\n\tParams:\n";
        }
        for(VariableDecl param: params) {
            rtn += "\t" + param.toString();
        }
        return rtn;
    }

    public String getName() {
        return name;
    }

    public ArrayList<VariableDecl> getParams() {
        return params;
    }

    public String getType() {
        return type;
    }



}
