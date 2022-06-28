package problems.kqbf.solvers;

import metaheuristics.grasp.ConstructiveHeuristic;
import problems.Evaluator;
import problems.kqbf.KQBF;
import problems.qbf.solvers.GRASP_QBF;
import solutions.KSolution;
import solutions.Solution;

import java.io.IOException;
import java.util.Collections;

public class GRASP_KQBF extends GRASP_QBF {

    public static final int iterations = 10000;

    private final KQBF objFunction;

    /**
     * Constructor for the GRASP_KQBF class. An inverse QBF objective function is
     * passed as argument for the superclass constructor.
     *
     * @param param       A double hyperparameter used by the constructive heuristics.
     * @param filename    Name of the file for which the objective function parameters
     *                    should be read.
     * @param hType       The constructive heuristic type to be used in generating new solutions.
     * @param stImproving If should use the first-improving local search, or the best-improving.
     * @throws IOException necessary for I/O operations.
     */
    public GRASP_KQBF(Double param, String filename,
                      ConstructiveHeuristic.ConstructiveHeuristicType hType, boolean stImproving) throws IOException {
        super(param, iterations, filename, hType, stImproving);
        objFunction = (KQBF) ObjFunction;
    }

    /**
     * Constructor for the GRASP_KQBF class. An inverse QBF objective function is
     * passed as argument for the superclass constructor.
     *
     * @param param       A double hyperparameter used by the constructive heuristics.
     * @param haltCost    The solver will halt only after reaching this  value.
     * @param filename    Name of the file for which the objective function parameters
     *                    should be read.
     * @param hType       The constructive heuristic type to be used in generating new solutions.
     * @param stImproving If should use the first-improving local search, or the best-improving.
     * @throws IOException necessary for I/O operations.
     */
    public GRASP_KQBF(Double param, Double haltCost, String filename,
                      ConstructiveHeuristic.ConstructiveHeuristicType hType, boolean stImproving) throws IOException {
        super(param, haltCost, filename, hType, stImproving);
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

    public double weight() {
        return ((KSolution<Integer>) bestSol).weigth;
    }

    /**
     * A main method used for testing the GRASP metaheuristic.
     */
    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        GRASP_QBF grasp = new GRASP_KQBF(0.05, "instances/kqbf/kqbf020",
                ConstructiveHeuristic.ConstructiveHeuristicType.Basic, true);
        KSolution<Integer> bestSol = (KSolution<Integer>) grasp.solve();
        System.out.println("maxVal = " + bestSol);
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Time = " + (double) totalTime / (double) 1000 + " seg");
    }
}
