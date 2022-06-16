package metaheuristics.grasp;

import java.util.ArrayList;

public class BasicHeuristic<E> extends ConstructiveHeuristic<E> {

    /**
     * the GRASP greediness-randomness parameter
     */
    protected Double alpha;

    /**
     * @param alpha The GRASP greediness-randomness parameter (within the range
     *              [0,1])
     */
    BasicHeuristic(double alpha, AbstractGRASP<E> solver) {
        super(alpha, solver);
        this.alpha = alpha;
    }

    @Override
    public void newSolution() {
        SOLVER.CL = SOLVER.makeCL();
        SOLVER.RCL = SOLVER.makeRCL();
        SOLVER.sol = SOLVER.createEmptySol();
        SOLVER.cost = Double.POSITIVE_INFINITY;
        ArrayList<Double> deltas = new ArrayList<>();

        /* Main loop, which repeats until the stopping criteria is reached. */
        while (!SOLVER.constructiveStopCriteria()) {
            double maxCost = Double.NEGATIVE_INFINITY, minCost = Double.POSITIVE_INFINITY;
            SOLVER.cost = SOLVER.ObjFunction.evaluate(SOLVER.sol);
            SOLVER.updateCL();

            /*
             * Explore all candidate elements to enter the solution, saving the
             * highest and lowest cost variation achieved by the candidates.
             */
            for (E e : SOLVER.CL) {
                Double deltaCost = SOLVER.ObjFunction.evaluateInsertionCost(e, SOLVER.sol);
                deltas.add(deltaCost);
                if (deltaCost < minCost)
                    minCost = deltaCost;
                if (deltaCost > maxCost)
                    maxCost = deltaCost;
            }

            /*
             * Among all candidates, insert into the RCL those with the highest
             * performance using parameter alpha as threshold.
             */
            for (int i = 0; i < SOLVER.CL.size(); i++) {
                Double deltaCost = deltas.get(i);
                if (deltaCost <= minCost + alpha * (maxCost - minCost))
                    SOLVER.RCL.add(SOLVER.CL.get(i));
            }

            /* Choose a candidate randomly from the RCL */
            if (SOLVER.RCL.size() == 0) break;
            int rndIndex = AbstractGRASP.rng.nextInt(SOLVER.RCL.size());
            SOLVER.sol.add(SOLVER.CL.remove(rndIndex));
            SOLVER.ObjFunction.evaluate(SOLVER.sol);
            SOLVER.RCL.clear();
            deltas.clear();
        }
    }
}
