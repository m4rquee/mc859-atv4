package problems.kqbf.solvers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class TS_KQBF_Probabilistic extends TS_KQBF {
    public TS_KQBF_Probabilistic(int tenure, String filename)
            throws IOException {
        super(tenure, filename, false);
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

        int sampled = Math.round((float) (CL.size() * 0.5));
        ArrayList<Integer> sampledCL = new ArrayList<Integer>(sampled);
        Collections.shuffle(CL);
        for (int i = 0; i < sampled; ++i) {
            sampledCL.add(CL.get(i));
        }

        // Evaluate removals
        for (int candOut : sol) {
            double deltaCost = ObjFunction.evaluateRemovalCost(candOut, sol);
            if (!TL.contains(candOut) || sol.cost + deltaCost < bestSol.cost)
                if (deltaCost < minDeltaCost) {
                    minDeltaCost = deltaCost;
                    bestCandOut = candOut;
                }
        }
        // Evaluate exchanges
        for (int candIn : sampledCL) {
            for (int candOut : sol) {
                double deltaCost = ObjFunction.evaluateExchangeCost(candIn, candOut, sol);
                if (!(TL.contains(candIn) || TL.contains(candOut)) || sol.cost + deltaCost < bestSol.cost)
                    if (deltaCost < minDeltaCost) {
                        minDeltaCost = deltaCost;
                        bestCandIn = candIn;
                        bestCandOut = candOut;
                    }
            }
        }
        // Evaluate insertions
        for (int candIn : sampledCL) {
            double deltaCost = ObjFunction.evaluateInsertionCost(candIn, sol);
            if (!TL.contains(candIn) || sol.cost + deltaCost < bestSol.cost)
                if (deltaCost < minDeltaCost) {
                    minDeltaCost = deltaCost;
                    bestCandIn = candIn;
                    bestCandOut = null;
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
}
