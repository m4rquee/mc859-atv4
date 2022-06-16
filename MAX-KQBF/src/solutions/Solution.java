package solutions;

import java.util.ArrayList;

public class Solution<E> extends ArrayList<E> implements Cloneable {

    public Double cost = Double.POSITIVE_INFINITY;

    public Solution() {
        super();
    }

    public Solution(Solution<E> sol) {
        super(sol);
        cost = sol.cost;
    }

    protected String elementsString() {
        return super.toString();
    }

    @Override
    public String toString() {
        return "Solution: cost=[" + -cost + "], size=[" + this.size() + "], elements=" + elementsString();
    }

    @Override
    public Solution<E> clone() {
        return new Solution<>(this);
    }
}

