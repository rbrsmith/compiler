package SemanticAnalyzer;

import java.util.ArrayList;

public class FunctionDecl implements Declaration {

    private String name;
    private String type;
    private ArrayList<VariableDecl> params;

    public FunctionDecl(Node id, Node type, Node fParams) {
        this.name = (String) id.getLeafValue().getY();
        this.type = (String) type.getLeafValue().getY();

        params = new ArrayList<>();
        analyzeFParams(fParams);

    }

    public void analyzeFParams(Node fParams) {
        for(Node child: fParams.getChildrenValues()) {
            if(!child.isLeaf() && child.getValue().equals(Analyzer.FUNC_ACTION_2)){
                Node type = child.getLeftSibling().getLeftSibling().getLeftSibling().getLeaf();
                Node id = type.getRightSibling().getLeaf();
                Node array = id.getRightSibling();
                VariableDecl v = new VariableDecl(id, type, array);
                params.add(v);
                // Add v to table
            }
            analyzeFParams(child);

        }



    }

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

}
