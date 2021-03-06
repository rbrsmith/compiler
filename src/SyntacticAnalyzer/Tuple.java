package SyntacticAnalyzer;


/**
 * Class used to hold two related values at the same time
 * @param <X>
 * @param <Y>
 */
public class Tuple<X, Y> {

    private X x;
    private Y y;

    public Tuple() {}

    public Tuple(X X, Y Y) {
        this.x = X;
        this.y = Y;
    }

    public void setX(X x) {
        this.x = x;
    }

    public void setY(Y y) {
        this.y = y;
    }

    public X getX() {
        return x;
    }

    public Y getY() {
        return y;
    }

    @Override
    public String toString() {
        return "("+getX()+","+getY()+")";
    }
}
