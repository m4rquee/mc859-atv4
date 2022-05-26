package metaheuristics.tabusearch;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Random;

import problems.Evaluator;
import solutions.Solution;

/**
 * Abstract class for metaheuristic Tabu Search. It consider a minimization problem.
 *
 * @param <E> Generic type of the candidate to enter the solution.
 * @author ccavellucci, fusberti
 */
public abstract class AbstractTS<E> {

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
     * the best solution cost
     */
    protected Double bestCost;

    /**
     * the incumbent solution cost
     */
    protected Double cost;

    /**
     * the best solution
     */
    protected Solution<E> bestSol;

    /**
     * the incumbent solution
     */
    protected Solution<E> sol;

    /**
     * the number of iterations the TS main loop executes.
     */
    protected Integer iterations;

    /**
     * the tabu tenure.
     */
    public final int tenure;

    /**
     * the Candidate List of elements to enter the solution.
     */
    protected ArrayList<E> CL;

    /**
     * the Restricted Candidate List of elements to enter the solution.
     */
    protected ArrayList<E> RCL;

    /**
     * the Tabu List of elements to enter the solution.
     */
    protected ArrayDeque<E> TL;

    /**
     * the current tabu search iteration.
     */
    protected int iter;

    /**
     * Creates the Candidate List, which is an ArrayList of candidate elements
     * that can enter a solution.
     *
     * @return The Candidate List.
     */
    public abstract ArrayList<E> makeCL();

    /**
     * Creates the Restricted Candidate List, which is an ArrayList of the best
     * candidate elements that can enter a solution.
     *
     * @return The Restricted Candidate List.
     */
    public abstract ArrayList<E> makeRCL();

    /**
     * Creates the Tabu List, which is an ArrayDeque of the Tabu
     * candidate elements. The number of iterations a candidate
     * is considered tabu is given by the Tabu Tenure {@link #tenure}
     *
     * @return The Tabu List.
     */
    public abstract ArrayDeque<E> makeTL();

    /**
     * Updates the Candidate List according to the incumbent solution
     * {@link #sol}. In other words, this method is responsible for
     * updating the costs of the candidate solution elements.
     */
    public abstract void updateCL();

    /**
     * Creates a new solution which is empty, i.e., does not contain any
     * candidate solution element.
     *
     * @return An empty solution.
     */
    public abstract Solution<E> createEmptySol();

    /**
     * The TS local search phase is responsible for repeatedly applying a
     * neighborhood operation while the solution is getting improved, i.e.,
     * until a local optimum is attained. When a local optimum is attained
     * the search continues by exploring moves which can make the current
     * solution worse. Cycling is prevented by not allowing forbidden
     * (tabu) moves that would otherwise backtrack to a previous solution.
     */
    public abstract void neighborhoodMove();

    /**
     * Creates an Evaluator based on the parameters in the input file.
     *
     * @return The created evaluator.
     */
    protected abstract Evaluator<E> initEvaluator(String filename) throws IOException;

    /**
     * Constructor for the AbstractTS class.
     *
     * @param filename   The file containing the objective function parameters.
     * @param tenure     The Tabu tenure parameter.
     * @param iterations The number of iterations which the TS will be executed.
     */
    public AbstractTS(String filename, Integer tenure, Integer iterations) throws IOException {
        this.ObjFunction = initEvaluator(filename);
        this.tenure = tenure;
        this.iterations = iterations;
    }

    /**
     * The TS constructive heuristic, which is responsible for building a
     * feasible solution by selecting in a greedy fashion, candidate
     * elements to enter the solution.
     */
    public void constructiveHeuristic() {
        CL = makeCL();
        RCL = makeRCL();
        sol = createEmptySol();
        cost = Double.POSITIVE_INFINITY;

        /* Main loop, which repeats until the stopping criteria is reached. */
        while (!constructiveStopCriteria()) {
            double maxCost = Double.NEGATIVE_INFINITY, minCost = Double.POSITIVE_INFINITY;
            cost = sol.cost;
            updateCL();

            /*
             * Explore all candidate elements to enter the solution, saving the
             * highest and lowest cost variation achieved by the candidates.
             */
            for (E c : CL) {
                Double deltaCost = ObjFunction.evaluateInsertionCost(c, sol);
                if (deltaCost < minCost)
                    minCost = deltaCost;
                if (deltaCost > maxCost)
                    maxCost = deltaCost;
            }

            /*
             * Among all candidates, insert into the RCL those with the highest
             * performance.
             */
            for (E c : CL) {
                Double deltaCost = ObjFunction.evaluateInsertionCost(c, sol);
                if (deltaCost <= minCost)
                    RCL.add(c);
            }

            /* Choose a candidate randomly from the RCL */
            if (RCL.size() == 0) break;
            int rndIndex = rng.nextInt(RCL.size());
            sol.add(CL.remove(rndIndex));
            ObjFunction.evaluate(sol);
            RCL.clear();
        }
    }

    /**
     * The TS mainframe. It consists of a constructive heuristic followed by
     * a loop, in which each iteration a neighborhood move is performed on
     * the current solution. The best solution is returned as result.
     *
     * @return The best feasible solution obtained throughout all iterations.
     */
    public Solution<E> solve() {
        long startTime = System.currentTimeMillis();
        bestSol = createEmptySol();
        constructiveHeuristic();
        TL = makeTL();
        int interval = iterations / 10;
        for (iter = 0; iter < iterations; iter++) {
            double totalTime = (System.currentTimeMillis() - startTime) / 1000.0;
            if (verbose && totalTime % 60 == 0)
                System.out.println("CurrTime = " + totalTime + " s");
            if (totalTime > MAXIMUM_RUNNING_TIME_SECONDS) break;
            neighborhoodMove();
            if (verbose && iter % interval == 0)
                System.out.println("(Iter. " + iter + ") CurrSol = " + sol);
            if (bestSol.cost > sol.cost) {
                bestSol = new Solution<>(sol);
                if (verbose)
                    System.out.println("(Iter. " + iter + ") BestSol = " + bestSol);
            }
        }

        return bestSol;
    }

    /**
     * A standard stopping criteria for the constructive heuristic is to repeat
     * until the incumbent solution improves by inserting a new candidate
     * element.
     *
     * @return true if the criteria is met.
     */
    public Boolean constructiveStopCriteria() {
        return cost <= sol.cost;
    }
}
