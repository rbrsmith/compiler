package LexicalAnalyzer.DFA;

/**
 * Object which holds the current place in a file
 */
public class Position {

    int lineNumber;
    int character;

    /**
     * Start at line 1, character 0
     */
    public Position() {
        this.lineNumber = 1;
        this.character = 0;
    }

    public Position(Position pos2) {
        this.lineNumber = pos2.getLine();
        this.character = pos2.getChar();
    }

    /**
     * Called once a character has been read
     */
    public void incChar() {
        this.character += 1;
    }

    /**
     * Called once we move back one in a file
     */
    public void decChar() {
        if(getChar() > 0) {
            this.character -= 1;
        }
    }

    /**
     * Called when we read a new line feed
     */
    public void newLine() {
        this.character = 0;
        this.lineNumber +=1;
    }

    public int getLine() {
        return this.lineNumber;
    }

    public int getChar() {
        return this.character;
    }

    public String toString() {
        return "Line: " + getLine() + ", Character: " + getChar();
    }

}
