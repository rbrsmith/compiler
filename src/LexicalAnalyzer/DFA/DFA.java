package LexicalAnalyzer.DFA;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Builds a DFA and Transition State Table
 * Majority of lexical analyzer happens in this class
 */
public class DFA {

    private Graph g;
    private ArrayList<Token> remove;

    /**
     * Build the DFA from custom rules
     */
    public DFA() {
        remove = new ArrayList<Token>(){{
             add(Token.SPACE);
             add(Token.TAB);
             add(Token.CARRIAGE_RETURN);
             add(Token.LINE_FEED);
            add(Token.INLINE_COMMENT);
            add(Token.OPEN_COMMENT);
            add(Token.CLOSE_COMMENT);
        }};
        Node root = new Node(0);

    //    String regex = "(l(l|d|_)*)|(([nz]d*)|0)|((([nz]d*)|0)(.d*[nz])|.0)";


        g = new Graph(root);
        g.addNode(new Node(1, true, Token.ID));
        g.addNode(new Node(2, true, Token.INTEGER));
        g.addNode(new Node(3, true, Token.INTEGER));
        g.addNode(new Node(4, true, Token.DOT));
        g.addNode(new Node(5, true, Token.ID));
        g.addNode(new Node(6, true, Token.ID));
        g.addNode(new Node(7, true, Token.ID));
        g.addNode(new Node(8, true, Token.INTEGER));
        g.addNode(new Node(9));
        g.addNode(new Node(10, true, Token.FLOAT));
        g.addNode(new Node(11, true, Token.FLOAT));
        g.addNode(new Node(12));


        // ID
        g.addEdge(0,1,Lexicon.LETTER);
        g.addEdge(0,2,Lexicon.NZERO);
        g.addEdge(0,3,Lexicon.ZERO);
        g.addEdge(0,4,Lexicon.DOT);

        g.addEdge(1,5,Lexicon.LETTER);
        g.addEdge(1,6, Lexicon.NZERO);
        g.addEdge(1,6, Lexicon.ZERO);
        g.addEdge(1,7,Lexicon.UNDERSCORE);

        g.addEdge(2,8,Lexicon.NZERO);
        g.addEdge(2,8,Lexicon.ZERO);
        g.addEdge(2,9,Lexicon.DOT);

        g.addEdge(3,9, Lexicon.DOT);

        g.addEdge(4,10,Lexicon.ZERO);

        g.addEdge(5,5,Lexicon.LETTER);
        g.addEdge(5,6,Lexicon.NZERO);
        g.addEdge(5,6,Lexicon.ZERO);
        g.addEdge(5,7,Lexicon.UNDERSCORE);

        g.addEdge(6,6,Lexicon.NZERO);
        g.addEdge(6, 6, Lexicon.ZERO);
        g.addEdge(6,5,Lexicon.LETTER);
        g.addEdge(6,7,Lexicon.UNDERSCORE);

        g.addEdge(7,7,Lexicon.UNDERSCORE);
        g.addEdge(7,5,Lexicon.LETTER);

        g.addEdge(8,8,Lexicon.NZERO);
        g.addEdge(8,8,Lexicon.ZERO);
        g.addEdge(8,9,Lexicon.DOT);

        g.addEdge(9,11,Lexicon.NZERO);
        g.addEdge(9,11,Lexicon.ZERO);

        g.addEdge(11,11,Lexicon.NZERO);
        g.addEdge(11,12,Lexicon.ZERO);

        g.addEdge(12, 12, Lexicon.ZERO);
        g.addEdge(12, 11, Lexicon.NZERO);


        // Comparison
        g.addNode(new Node(13, true, Token.ASSIGNMENT));
        g.addNode(new Node(14, true, Token.EQUALS));

        g.addEdge(0,13, Lexicon.EQUALS);
        g.addEdge(13,14,Lexicon.EQUALS);

        g.addNode(new Node(15, true, Token.LESS_THAN));
        g.addNode(new Node(16, true, Token.NOT_EQUALS));


        g.addEdge(0,15,Lexicon.LESS_THAN);
        g.addEdge(15,16,Lexicon.GREATER_THAN);

        g.addNode(new Node(17, true, Token.GREATER_THAN));
        g.addEdge(0,17,Lexicon.GREATER_THAN);

        g.addNode(new Node(18, true, Token.LESS_THAN_EQUALS));
        g.addEdge(15,18, Lexicon.EQUALS);

        g.addNode(new Node(19, true, Token.GREATER_THAN_EQUALS));
        g.addEdge(17, 19, Lexicon.EQUALS);


        // Punctuation
        g.addNode(new Node(20, true, Token.SEMICOLON));
        g.addEdge(0,20,Lexicon.SEMICOLON);

        g.addNode(new Node(21, true, Token.COMMA));
        g.addEdge(0,21,Lexicon.COMMA);

        // Math
        g.addNode(new Node(22, true, Token.ADDITION));
        g.addEdge(0,22,Lexicon.ADDITION);


        g.addNode(new Node(23, true, Token.SUBTRACTION));
        g.addEdge(0,23,Lexicon.SUBTRACTION);

        g.addNode(new Node(24, true, Token.MULTIPLICATION));
        g.addEdge(0,24,Lexicon.MULTIPLICATION);

        g.addNode(new Node(25, true, Token.DIVISION));
        g.addEdge(0,25,Lexicon.DIVISION);

        // Space
        g.addNode(new Node(26, true, Token.SPACE));
        g.addEdge(0,26,Lexicon.SPACE);

        // Brackets
        g.addNode(new Node(27, true, Token.OCB));
        g.addNode(new Node(28, true, Token.CCB));
        g.addNode(new Node(29, true, Token.ORB));
        g.addNode(new Node(30, true, Token.CRB));
        g.addNode(new Node(31, true, Token.OSB));
        g.addNode(new Node(32, true, Token.CSB));

        g.addEdge(0,27,Lexicon.OCB);
        g.addEdge(0,28,Lexicon.CCB);
        g.addEdge(0,29,Lexicon.ORB);
        g.addEdge(0,30,Lexicon.CRB);
        g.addEdge(0,31,Lexicon.OSB);
        g.addEdge(0,32,Lexicon.CSB);

        // New Line
        g.addNode(new Node(33, true, Token.CARRIAGE_RETURN));
        g.addNode(new Node(34, true, Token.LINE_FEED));

        g.addEdge(0,33,Lexicon.CARRIAGE_RETURN);
        g.addEdge(0,34,Lexicon.LINE_FEED);

        // Comments
        g.addNode(new Node(35, true, Token.OPEN_COMMENT));
        g.addNode(new Node(36, true, Token.CLOSE_COMMENT));
        g.addNode(new Node(37, true, Token.INLINE_COMMENT));

        g.addEdge(25, 35, Lexicon.MULTIPLICATION);
        g.addEdge(24, 36, Lexicon.DIVISION);
        g.addEdge(25, 37, Lexicon.DIVISION);

        // Tab
        g.addNode(new Node(38, true, Token.TAB));
        g.addEdge(0,38,Lexicon.TAB);

        // AND OR
        g.addNode(new Node(39));
        g.addNode(new Node(40, true, Token.OR));

        g.addEdge(0,39, Lexicon.PIPE);
        g.addEdge(39,40, Lexicon.PIPE);

        g.addNode(new Node(41));
        g.addNode(new Node(42, true, Token.AND));

        g.addEdge(0,41,Lexicon.AMP);
        g.addEdge(41,42,Lexicon.AMP);

        g.addNode(new Node(43, true, Token.EOF));
        g.addEdge(0,43, Lexicon.EOF);
    }


    /**
     *
     * @param file File of source code to be read and tagged
     * @param errors ArrayList used to capture any exceptions
     * @return ArrayList of POS tags
     */
    public ArrayList<POS> getTags(File file, ArrayList<Exception> errors) {
        ArrayList<POS> tags = new ArrayList<>();
        try {
            // Use RandomAccessFile as we will commonly go backtrack one in the file
            RandomAccessFile buffer = new RandomAccessFile(file, "r");
            POS partOfSpeech;
            Position pos = new Position();
            while (buffer.getFilePointer() < buffer.length()) {
                try {
                    partOfSpeech = getNextToken(buffer, pos);
                    if(partOfSpeech == null) {
                        continue;
                    }
                    tags.add(partOfSpeech);
                } catch (Exception e) {
                    // Any error that occurs during token reading
                    errors.add(e);
                }
            }
            buffer.close();
        } catch(IOException e) {
            // File reading errors
            System.out.println(e.getMessage());
            System.out.println("Error reading file.");
        }
        return tags;
    }


    /**
     * Get the next sanitized token, will not return spaces, linefeeds etc.
     * Comments and everything in between them will be removed
     *
     * @param buffer RandomAccessFile pointing to source code
     * @param pos Position object representing where we are in the file
     * @return  POS partOfSpeech representing the next token
     * @throws InvalidCharacterException thrown if we encounter an unexpected character in the token
     * @throws UnrecognizedCharacterException thrown if we encounter an unrecognized character in the token
     * @throws IOException thrown if we have trouble reading the file
     */
    public POS getNextToken(RandomAccessFile buffer, Position pos) throws InvalidCharacterException,
                UnrecognizedCharacterException, IOException{
        POS token;
        // Cleaned version of the token
        POS cleaned = null;

        // Track if we are current pursuing a comment
        boolean inlineComments = false;
        boolean multiLineComments = false;

        while(cleaned == null) {

            token = g.getNextToken(buffer, pos);
            if(token == null) return null;

            // Start & finish of comment
            if(token.getType() == Token.INLINE_COMMENT) {
                inlineComments = true;
            } else if(token.getType() == Token.OPEN_COMMENT) {
                multiLineComments = true;
            } else if(token.getType() == Token.LINE_FEED && inlineComments) {
                inlineComments = false;
            } else if(token.getType() == Token.CLOSE_COMMENT && multiLineComments) {
                multiLineComments = false;
            }

            if(inlineComments || multiLineComments) {
                continue;
            }


            cleaned = cleanTag(token);
        }
        return cleaned;
    }

    /**
     *
     * @param file File of source code to be read and tagged
     * @return ArrayList of POS tags
     */
    public ArrayList<POS> getTags(File file) {
        return getTags(file, new ArrayList<>());
    }

    @Override
    public String toString() {
        return g.toString();
    }

    /**
     * Remove unwanted tags from tag list
     * As well as lookup reserved words
     *
     * @param tags ArrayList of POS tags cleaned out
     */
    public void cleanTags(ArrayList<POS> tags) {
        for (Iterator<POS> iterator = tags.iterator(); iterator.hasNext();) {
            POS tag = iterator.next();
            tag = cleanTag(tag);
            if(tag == null) {
                iterator.remove();
            }
        }

    }

    /**
     * @param tag POS PartOfSpeech tag to be cleaned
     * @return POS of cleaned tag, word if token is reserved or null if it is not used in the parsing
     */
    public POS cleanTag(POS tag) {
        // Change if tag is a reserved word
        if(tag.getType() == Token.ID) {
            Reserved reserved = Reserved.get(tag.getToken());
            if(reserved != null) {
                tag.setWord(reserved);
                return tag;
            }

        } else if(remove.contains(tag.getType())) {
            // Certain tags should not be returned
            return null;
        }
        return tag;

    }

}