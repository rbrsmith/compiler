package LexicalAnalyzer.DFA;

public class Position {

    int lineNumber;
    int character;

    public Position() {
        this.lineNumber = 1;
        this.character = 0;
    }

    public void incChar() {
        this.character += 1;
    }

    public void decChar() {
        if(getChar() > 0) {
            this.character -= 1;
        }
    }

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
