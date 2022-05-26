package problems.qbf.solvers;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;

import metaheuristics.tabusearch.AbstractTS;
import problems.Evaluator;
import problems.kqbf.KQBF;
import solutions.Solution;


/**
 * Metaheuristic TS (Tabu Search) for obtaining an optimal solution to a QBF
 * (Quadractive Binary Function -- {@link #QuadracticBinaryFunction}).
 * Since by default this TS considers minimization problems, an inverse QBF
 * function is adopted.
 *
 * @author ccavellucci, fusberti
 */
public class TS_QBF extends AbstractTS<Integer> {

    public final boolean stImproving;

    private final int fake = -1;

    /**
     * Constructor for the TS_QBF class. An inverse QBF objective function is
     * passed as argument for the superclass constructor.
     *
     * @param tenure      The Tabu tenure parameter.
     * @param iterations  The number of iterations which the TS will be executed.
     * @param filename    Name of the file for which the objective function parameters
     *                    should be read.
     * @param stImproving If should use the first-improving local search, or the best-improving.
     * @throws IOException necessary for I/O operations.
     */
    public TS_QBF(int tenure, int iterations, String filename, boolean stImproving) throws IOException {
        super(filename, tenure, iterations);
        this.stImproving = stImproving;
    }

    @Override
    protected Evaluator<Integer> initEvaluator(String filename) throws IOException {
        return new KQBF(filename);
    }

    /* (non-Javadoc)
     * @see metaheuristics.tabusearch.AbstractTS#makeCL()
     */
    @Override
    public ArrayList<Integer> makeCL() {
        ArrayList<Integer> _CL = new ArrayList<>();
        for (int i = 0; i < ObjFunction.getDomainSize(); i++)
            _CL.add(i);
        return _CL;
    }

    /* (non-Javadoc)
     * @see metaheuristics.tabusearch.AbstractTS#makeRCL()
     */
    @Override
    public ArrayList<Integer> makeRCL() {
        return new ArrayList<>();
    }

    /* (non-Javadoc)
     * @see metaheuristics.tabusearch.AbstractTS#makeTL()
     */
    @Override
    public ArrayDeque<Integer> makeTL() {
        ArrayDeque<Integer> _TS = new ArrayDeque<>(2 * tenure);
        for (int i = 0; i < 2 * tenure; i++)
            _TS.add(fake);
        return _TS;
    }

    /* (non-Javadoc)
     * @see metaheuristics.tabusearch.AbstractTS#updateCL()
     */
    @Override
    public void updateCL() {
        // do nothing
    }

    /**
     * {@inheritDoc}
     * <p>
     * This createEmptySol instantiates an empty solution and it attributes a
     * zero cost, since it is known that a QBF solution with all variables set
     * to zero has also zero cost.
     */
    @Override
    public Solution<Integer> createEmptySol() {
        Solution<Integer> sol = new Solution<>();
        sol.cost = 0.0;
        return sol;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The local search operator developed for the QBF objective function is
     * composed by the neighborhood moves Insertion, Removal and 2-Exchange.
     */
    @Override
    public void neighborhoodMove() {
        double minDeltaCost = Double.POSITIVE_INFINITY;
        Integer bestCandIn = null, bestCandOut = null;
        updateCL();

        // Evaluate removals
        for (int candOut : sol) {
            double deltaCost = ObjFunction.evaluateRemovalCost(candOut, sol);
            if (!TL.contains(candOut) || sol.cost + deltaCost < bestSol.cost)
                if (deltaCost < minDeltaCost) {
                    minDeltaCost = deltaCost;
                    bestCandOut = candOut;
                    if (stImproving) break;
                }
        }
        // Evaluate exchanges
        outerLoop:
        for (int candIn : CL) {
            for (int candOut : sol) {
                double deltaCost = ObjFunction.evaluateExchangeCost(candIn, candOut, sol);
                if (!(TL.contains(candIn) || TL.contains(candOut)) || sol.cost + deltaCost < bestSol.cost)
                    if (deltaCost < minDeltaCost) {
                        minDeltaCost = deltaCost;
                        bestCandIn = candIn;
                        bestCandOut = candOut;
                        if (stImproving) break outerLoop;
                    }
            }
        }
        // Evaluate insertions
        for (int candIn : CL) {
            double deltaCost = ObjFunction.evaluateInsertionCost(candIn, sol);
            if (!TL.contains(candIn) || sol.cost + deltaCost < bestSol.cost)
                if (deltaCost < minDeltaCost) {
                    minDeltaCost = deltaCost;
                    bestCandIn = candIn;
                    bestCandOut = null;
                    if (stImproving) break;
                }
        }

        // Implement the best non-tabu move or an aspired one:
        TL.poll();
        if (bestCandOut != null) {
            sol.remove(bestCandOut);
            CL.add(bestCandOut);
            TL.add(bestCandOut);
        } else
            TL.add(fake);
        TL.poll();
        if (bestCandIn != null) {
            sol.add(bestCandIn);
            CL.remove(bestCandIn);
            TL.add(bestCandIn);
        } else
            TL.add(fake);
        ObjFunction.evaluate(sol);
    }

    /**
     * A main method used for testing the TS metaheuristic.
     */
    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        TS_QBF tabusearch = new TS_QBF(20, 1000, "instances/qbf/qbf100", false);
        Solution<Integer> bestSol = tabusearch.solve();
        System.out.println("maxVal = " + bestSol);
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Time = " + (double) totalTime / (double) 1000 + " seg");
    }
}
