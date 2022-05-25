/**
 *
 */
package metaheuristics.grasp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import problems.Evaluator;
import solutions.Solution;

/**
 * Abstract class for metaheuristic GRASP (Greedy Randomized Adaptive Search
 * Procedure). It consider a minimization problem.
 *
 * @param <E> Generic type of the element which composes the solution.
 * @author ccavellucci, fusberti
 */
public abstract class AbstractGRASP<E> {

    protected final long MAXIMUM_RUNNING_TIME_SECONDS = 30 * 60; // 30 minutes

    /**
     * flag that indicates whether the code should print more information on
     * screen
     */
    public static boolean verbose = true;

    /**
     * a random number generator
     */
    static Random rng = new Random(42);

    /**
     * the objective function being optimized
     */
    protected Evaluator<E> ObjFunction;

    /**
     * the best (incumbent) solution cost
     */
    protected Double bestCost;

    /**
     * the current solution cost
     */
    protected Double cost;

    /**
     * the best solution
     */
    protected Solution<E> bestSol;

    /**
     * the current solution
     */
    protected Solution<E> sol;

    /**
     * the number of iterations the GRASP main loop executes.
     */
    protected Integer iterations;

    /**
     * the Candidate List of elements to enter the solution.
     */
    protected ArrayList<E> CL;

    /**
     * the Restricted Candidate List of elements to enter the solution.
     */
    protected ArrayList<E> RCL;

    /**
     * The GRASP constructive heuristic, which is responsible for building a
     * feasible solution by selecting in a greedy-random fashion, candidate
     * elements to enter the solution.
     */
    private final ConstructiveHeuristic<E> Heuristic;

    /**
     * Creates the Candidate List, which is an ArrayList of candidate elements
     * that can enter a solution.
     *
     * @return The Candidate List.
     */
    public abstract ArrayList<E> makeCL();

    /**
     * Creates the Restricted Candidate List, which is an ArrayList of the
     * candidate elements that can enter a solution. This list depends on
     * the constructive heuristic being used.
     *
     * @return The Restricted Candidate List.
     */
    public abstract ArrayList<E> makeRCL();

    /**
     * Updates the Candidate List according to the current solution
     * {@link #sol}. In other words, this method is responsible for
     * updating which elements are still viable to take part into the solution.
     */
    public abstract void updateCL();

    /**
     * Creates a new solution which is empty, i.e., does not contain any
     * element.
     *
     * @return An empty solution.
     */
    public abstract Solution<E> createEmptySol();

    /**
     * The GRASP local search phase is responsible for repeatedly applying a
     * neighborhood operation while the solution is getting improved, i.e.,
     * until a local optimum is attained.
     *
     * @return An local optimum solution.
     */
    public abstract Solution<E> localSearch();

    /**
     * Creates an Evaluator based on the parameters in the input file.
     *
     * @return The created evaluator.
     */
    protected abstract Evaluator<E> initEvaluator(String filename) throws IOException;

    /**
     * Constructor for the AbstractGRASP class.
     *
     * @param filename   The file containing the objective function parameters.
     * @param param      A double hyperparameter used by the constructive heuristics.
     * @param iterations The number of iterations which the GRASP will be executed.
     * @param hType      The constructive heuristic type to be used in generating new solutions.
     */
    public AbstractGRASP(String filename, Double param, Integer iterations,
                         ConstructiveHeuristic.ConstructiveHeuristicType hType) throws IOException {
        this.ObjFunction = initEvaluator(filename);
        this.iterations = iterations;

        if (hType == ConstructiveHeuristic.ConstructiveHeuristicType.Basic)
            this.Heuristic = new BasicHeuristic<E>(param, this);
        else if (hType == ConstructiveHeuristic.ConstructiveHeuristicType.SampledGreedy)
            this.Heuristic = new SampledGreedyHeuristic<E>(param, this);
        else if (hType == ConstructiveHeuristic.ConstructiveHeuristicType.Reactive)
            this.Heuristic = new ReactiveHeuristic<E>(param, this);
        else
            this.Heuristic = null; // will never occur
    }

    /**
     * The GRASP mainframe. It consists of a loop, in which each iteration goes
     * through the constructive heuristic and local search. The best solution is
     * returned as result.
     *
     * @return The best feasible solution obtained throughout all iterations.
     */
    public Solution<E> solve() {
        long startTime = System.currentTimeMillis();
        bestSol = createEmptySol();
        int interval = iterations / 10;
        for (int i = 0; i < iterations; i++) {
            double totalTime = (System.currentTimeMillis() - startTime) / 1000.0;
            if (verbose && totalTime % 60 == 0)
                System.out.println("CurrTime = " + totalTime + " s");
            if (totalTime > MAXIMUM_RUNNING_TIME_SECONDS) break;
            Heuristic.newSolution();
            localSearch();
            if (verbose && i % interval == 0)
                System.out.println("(Iter. " + i + ") CurrSol = " + sol);
            if (bestSol.cost > sol.cost) {
                bestSol = sol.clone();
                if (verbose)
                    System.out.println("(Iter. " + i + ") BestSol = " + bestSol);
            }
        }

        return bestSol;
    }

    /**
     * A standard stopping criteria for the constructive heuristic is to repeat
     * until the current solution improves by inserting a new candidate
     * element.
     *
     * @return true if the criteria is met.
     */
    public Boolean constructiveStopCriteria() {
        return cost <= sol.cost;
    }
}
