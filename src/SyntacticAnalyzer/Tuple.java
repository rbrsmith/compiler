package SyntacticAnalyzer;


/**
 * Class used to hold two related values at the same time
 * @param <X>
 * @param <Y>
 */
public class Tuple<X, Y> {

    public X x;
    public Y y;

    public Tuple() {}

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
}
