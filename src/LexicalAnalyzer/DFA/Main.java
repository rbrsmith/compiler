package LexicalAnalyzer.DFA;

import java.io.File;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.println("Error.  No Source Code Found");
        } else {
            DFA dfa = new DFA();
            ArrayList<POS> tags = dfa.getTags(new File(args[0]));
            dfa.cleanTags(tags);
            for(POS t : tags) {
                System.out.println(t);
            }
        }
    }

}
