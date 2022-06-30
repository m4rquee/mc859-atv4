package problems.kqbf.solvers;

import java.io.IOException;
import java.util.*;

public class TS_KQBF_Probabilistic extends TS_KQBF {
    public TS_KQBF_Probabilistic(int tenure, String filename, boolean firstImproving)
            throws IOException {
        super(tenure, filename, firstImproving);
    }

    public TS_KQBF_Probabilistic(double haltCost, int tenure, String filename, boolean firstImproving)
            throws IOException {
        super(haltCost, tenure, filename, firstImproving);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The local search operator developed for the QBF objective function is
     * composed by the neighborhood moves Insertion, Removal and 2-Exchange.
     */
    @Override
    public void neighborhoodMove() {
        updateCL();
        int sampled = Math.round((float) (CL.size() * 0.5));
        Collections.shuffle(CL);
        _neighborhoodMove(CL.subList(0, sampled));
    }
}
