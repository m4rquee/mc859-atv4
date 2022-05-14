package problems.qbf.solvers;

import problems.Evaluator;
import problems.qbf.KQBF;
import solutions.KSolution;
import solutions.Solution;

import java.io.IOException;
import java.util.Collections;

public class GRASP_KQBF extends GRASP_QBF {

    private final KQBF objFunction;

    /**
     * Constructor for the GRASP_KQBF class. An inverse QBF objective function is
     * passed as argument for the superclass constructor.
     *
     * @param param       A double hyperparameter used by the constructive heuristics.
     * @param iterations  The number of iterations which the GRASP will be executed.
     * @param filename    Name of the file for which the objective function parameters
     *                    should be read.
     * @param hType       The constructive heuristic type to be used in generating new solutions.
     * @param stImproving If should use the first-improving local search, or the best-improving.
     * @throws IOException necessary for I/O operations.
     */
    public GRASP_KQBF(Double param, Integer iterations, String filename,
                      ConstructiveHeuristicType hType, boolean stImproving) throws IOException {
        super(param, iterations, filename, hType, stImproving);
        objFunction = (KQBF) ObjFunction;
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
        Collections.shuffle(CL);
    }

    @Override
    public KSolution<Integer> createEmptySol() {
        KSolution<Integer> sol = new KSolution<>(super.createEmptySol());
        sol.weigth = 0.0;
        return sol;
    }

    @Override
    public Solution<Integer> localSearch() {
        return super.localSearch(); // will only look for exchanges for items that already fit the knapsack
    }

    /**
     * A main method used for testing the GRASP metaheuristic.
     */
    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        GRASP_QBF grasp = new GRASP_KQBF(0.05, 1000000000, "instances/kqbf/kqbf020",
                ConstructiveHeuristicType.Basic, true);
        KSolution<Integer> bestSol = (KSolution<Integer>) grasp.solve();
        System.out.println("maxVal = " + bestSol);
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Time = " + (double) totalTime / (double) 1000 + " seg");
    }
}
