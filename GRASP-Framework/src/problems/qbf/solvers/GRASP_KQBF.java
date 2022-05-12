package problems.qbf.solvers;

import problems.Evaluator;
import problems.qbf.KQBF;
import solutions.Solution;

import java.io.IOException;
import java.util.ArrayList;

public class GRASP_KQBF extends GRASP_QBF {

    /**
     * Constructor for the GRASP_KQBF class. An inverse QBF objective function is
     * passed as argument for the superclass constructor.
     *
     * @param alpha      The GRASP greediness-randomness parameter (within the range
     *                   [0,1])
     * @param iterations The number of iterations which the GRASP will be executed.
     * @param filename   Name of the file for which the objective function parameters
     *                   should be read.
     * @throws IOException necessary for I/O operations.
     */
    public GRASP_KQBF(Double alpha, Integer iterations, String filename) throws IOException {
        super(alpha, iterations, filename);
    }

    @Override
    protected Evaluator<Integer> initEvaluator(String filename) throws IOException {
        return new KQBF(filename);
    }

    @Override
    public ArrayList<Integer> makeCL() {
        return super.makeCL();
    }

    @Override
    public ArrayList<Integer> makeRCL() {
        return super.makeRCL();
    }

    @Override
    public void updateCL() {
        super.updateCL();
    }

    @Override
    public Solution<Integer> createEmptySol() {
        return super.createEmptySol();
    }

    @Override
    public Solution<Integer> localSearch() {
        return super.localSearch();
    }

    /**
     * A main method used for testing the GRASP metaheuristic.
     */
    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        GRASP_QBF grasp = new GRASP_KQBF(0.05, 1000, "instances/kqbf/kqbf020");
        Solution<Integer> bestSol = grasp.solve();
        System.out.println("maxVal = " + bestSol);
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Time = " + (double) totalTime / (double) 1000 + " seg");
    }
}
