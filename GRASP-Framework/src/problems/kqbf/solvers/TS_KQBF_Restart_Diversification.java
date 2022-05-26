package problems.kqbf.solvers;

import java.util.*;
import java.io.IOException;
import java.util.stream.Collectors;

public class TS_KQBF_Restart_Diversification extends TS_KQBF {

    private class Tuple implements Map.Entry<Integer, Integer> {

        protected int Key, Value;

        public Tuple(int key, int value) {
            Key = key;
            Value = value;
        }

        @Override
        public Integer getKey() {
            return Key;
        }

        @Override
        public Integer getValue() {
            return Value;
        }

        @Override
        public Integer setValue(Integer value) {
            int oldValue = Value;
            Value = value;
            return oldValue;
        }
    }

    private final int RESTART_FREQUENCY = 1000;

    // How many times a variable appeared as true in a solution (stored as a <Freq, Var> pair):
    private final ArrayList<Tuple> FREQUENCY_MEMORY;

    public TS_KQBF_Restart_Diversification(int tenure, String filename, boolean stImproving) throws IOException {
        super(tenure, filename, stImproving);
        FREQUENCY_MEMORY = new ArrayList<>(this.ObjFunction.getDomainSize());
        for (int i = 0; i < this.ObjFunction.getDomainSize(); i++)
            FREQUENCY_MEMORY.add(new Tuple(0, i));
    }

    @Override
    public void neighborhoodMove() {
        super.neighborhoodMove();
        for (var v : sol)
            FREQUENCY_MEMORY.get(v).Key++;
        if ((iter + 1) % RESTART_FREQUENCY == 0)
            restart_sol();
    }

    private void restart_sol() {
        // Sort by increasing frequency:
        FREQUENCY_MEMORY.sort(Map.Entry.comparingByKey());

        // Restart the search but now using the least used variables:
        sol = createEmptySol();
        TL.clear();
        RCL.clear();
        cost = Double.POSITIVE_INFINITY;

        CL = (ArrayList<Integer>) FREQUENCY_MEMORY.stream().map(t -> t.Value).collect(Collectors.toList());
        while (CL.size() > 0) {
            sol.add(CL.remove(0));
            ObjFunction.evaluate(sol);
            updateCL();
        }
        cost = sol.cost;

        // Reset the frequencies and reorder by variable name:
        for (var t : FREQUENCY_MEMORY)
            t.Key = 0;
        FREQUENCY_MEMORY.sort(Map.Entry.comparingByValue());
    }
}
