package bet.astral.flunkie.tuple;

public class Pair<A, B> {
    private final A valueA;
    private final B valueB;

    public Pair(A valueA, B valueB) {
        this.valueA = valueA;
        this.valueB = valueB;
    }

    public A getValueA() {
        return valueA;
    }

    public B getValueB() {
        return valueB;
    }
}
