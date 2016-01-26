package LexicalAnalyzer.DFA;

public class Position {

    int lineNumber;
    int character;

    public Position() {
        this.lineNumber = 1;
        this.character = 0;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getCharacter() {
        return character;
    }

    public void incLine() {
        this.lineNumber +=1;
    }

    public void incChar() {
        this.character += 1;
    }

    public void decChar() {
        this.character -= 1;
    }

    public void newLine() {
        this.character = 0;
        this.lineNumber +=1;
    }

    public String toString() {
        return "Line: " + this.lineNumber + ", Character: " + this.character;
    }

}
