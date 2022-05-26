package problems.kqbf.solvers;

import problems.Evaluator;
import problems.kqbf.KQBF;
import problems.qbf.solvers.TS_QBF;
import solutions.KSolution;

import java.io.IOException;

public class TS_KQBF extends TS_QBF {

    public static final Integer iterations = 5000;

    private final KQBF objFunction;

    /**
     * Constructor for the TS_KQBF class. An inverse QBF objective function is
     * passed as argument for the superclass constructor.
     *
     * @param tenure      A double hyperparameter used by the constructive heuristics.
     * @param filename    Name of the file for which the objective function parameters
     *                    should be read.
     * @param stImproving If should use the first-improving local search, or the best-improving.
     * @throws IOException necessary for I/O operations.
     */
    public TS_KQBF(int tenure, String filename, boolean stImproving) throws IOException {
        super(tenure, iterations, filename, stImproving);
        objFunction = (KQBF) ObjFunction;
    }

    public double weight() {
        return ((KSolution<Integer>) sol).weigth;
    }

    @Override
    protected Evaluator<Integer> initEvaluator(String filename) throws IOException {
        return new KQBF(filename);
    }

    @Override
    public void updateCL() {
        Double currWeight = ((KSolution<Integer>) sol).weigth;
        CL.removeIf(c -> // if adding this item will overpass the capacity
                objFunction.W[c] > objFunction.W_max - currWeight
        );
    }

    @Override
    public KSolution<Integer> createEmptySol() {
        KSolution<Integer> sol = new KSolution<>(super.createEmptySol());
        sol.weigth = 0.0;
        return sol;
    }
}
