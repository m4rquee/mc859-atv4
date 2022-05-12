package solutions;

public class KSolution<E> extends Solution<E> {

    public Double weigth;

    public KSolution(Solution<E> sol) {
        super(sol);
    }

    public KSolution(KSolution<E> sol) {
        super(sol);
        weigth = sol.weigth;
    }

    @Override
    public String toString() {
        return "Solution: cost=[" + cost + "], weight=[" + this.weigth +
                "], size=[" + this.size() + "], elements=" + elementsString();
    }

    @Override
    public KSolution<E> clone() {
        return new KSolution<>(this);
    }
}
