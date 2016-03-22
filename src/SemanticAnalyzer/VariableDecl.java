package SemanticAnalyzer;

import java.util.ArrayList;
import java.util.Collection;


public class VariableDecl implements Declaration {

    private String type;
    private String name;
    private ArrayList<Integer> sizes;


    public VariableDecl(Node id, Node type, Node array) {
        this.name = (String) id.getLeafValue().getY();
        this.type = (String) type.getLeafValue().getY();

        sizes = new ArrayList<>();
        if(array != null) analyzeArray(array);
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

    private void analyzeArray(Node array) {
        for (Node child : array.getChildrenValues()) {
            if (!child.isLeaf() && child.getValue().equals("SEMANTIC-6")) {
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
