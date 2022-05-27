package problems.kqbf.solvers;

import problems.Evaluator;
import problems.kqbf.KQBF;
import problems.qbf.solvers.TS_QBF;
import solutions.KSolution;
import solutions.Solution;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TS_KQBF extends TS_QBF {

    protected static final String INSTANCES_DIR = "instances/kqbf/";
    protected static final List<String> INSTANCE_LIST = Arrays.asList(
            "kqbf020",
            "kqbf040",
            "kqbf060",
            "kqbf080",
            "kqbf100",
            "kqbf200",
            "kqbf400");
    protected static final List<Integer> TENURE_LIST = Arrays.asList(7, 14);

    protected static final Integer iterations = 10000;

    protected final KQBF objFunction;

    /**
     * Constructor for the TS_KQBF class. An inverse QBF objective function is
     * passed as argument for the superclass constructor.
     *
     * @param tenure      A double hyperparameter used by the constructive
     *                    heuristics.
     * @param filename    Name of the file for which the objective function
     *                    parameters
     *                    should be read.
     * @param stImproving If should use the first-improving local search, or the
     *                    best-improving.
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
        objFunction.W[c] > objFunction.W_max - currWeight);
    }

    @Override
    public KSolution<Integer> createEmptySol() {
        KSolution<Integer> sol = new KSolution<>(super.createEmptySol());
        sol.weigth = 0.0;
        return sol;
    }

    /**
     * A main method used for testing the Tabu Search metaheuristic.
     */
    public static void main(String[] args) throws IOException {
        for (var instance : INSTANCE_LIST)
            for (var tenure : TENURE_LIST)
                for (boolean firstImproving : new boolean[] { true, false })
                    run_algorithm(tenure, firstImproving, INSTANCES_DIR + instance);
    }

    private static void run_algorithm(int tenure, boolean firstImproving, String fileName) throws IOException {
        long startTime = System.currentTimeMillis();
        System.out.println("\n\n=============================");
        TS_KQBF tabuSearch = new TS_KQBF(tenure, fileName, firstImproving);
        Solution<Integer> bestSolution = tabuSearch.solve();
        double knapsackWeight = ((KSolution<Integer>) tabuSearch.sol).weigth;
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("-----------------");
        System.out.println("instance: " + fileName);
        System.out.println("tenure: " + tenure);
        System.out.println("firstImproving: " + firstImproving);
        System.out.println("iterations: " + iterations);
        System.out.println("Best Solution Found: " + bestSolution);
        System.out.println("Knapsack Weight of Best Solution: " + knapsackWeight);
        System.out.println("Time = " + (double) totalTime / (double) 1000 + " seg");
    }
}
