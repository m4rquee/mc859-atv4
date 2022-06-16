package metaheuristics.grasp;

public class SampledGreedyHeuristic<E> extends ConstructiveHeuristic<E> {

    /**
     * the size of the sampled RCL
     */
    private final int p;

    SampledGreedyHeuristic(double p, AbstractGRASP<E> solver) {
        super(p, solver);
        this.p = (int) p;
    }

    @Override
    public void newSolution() {
        SOLVER.CL = SOLVER.makeCL();
        SOLVER.RCL = SOLVER.makeRCL();
        SOLVER.sol = SOLVER.createEmptySol();
        SOLVER.cost = Double.POSITIVE_INFINITY;

        /* Main loop, which repeats until the stopping criteria is reached. */
        while (!SOLVER.constructiveStopCriteria()) {
            SOLVER.cost = SOLVER.ObjFunction.evaluate(SOLVER.sol);
            SOLVER.updateCL();

            // Explore min(p, |CL|) candidate elements to enter the solution.
            E minE = null;
            int sampleSize = Math.min(p, SOLVER.CL.size());
            double minCost = Double.POSITIVE_INFINITY;
            for (int i = 0; i < sampleSize; i++) {
                E e = SOLVER.CL.remove(AbstractGRASP.rng.nextInt(SOLVER.CL.size()));
                SOLVER.RCL.add(e);
                double delta = SOLVER.ObjFunction.evaluateInsertionCost(e, SOLVER.sol);
                if (delta < minCost) {
                    minE = e;
                    minCost = delta;
                }
            }

            // Among all candidates, chose the smallest one to insert.
            if (SOLVER.RCL.size() == 0) break;
            SOLVER.sol.add(minE);
            SOLVER.RCL.remove(minE);
            SOLVER.CL.addAll(SOLVER.RCL); // put the not chosen back
            SOLVER.RCL.clear();
            SOLVER.ObjFunction.evaluate(SOLVER.sol);
        }
    }
}

