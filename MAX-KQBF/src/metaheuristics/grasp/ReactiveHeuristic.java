package metaheuristics.grasp;

import java.util.Arrays;
import java.util.Random;

public class ReactiveHeuristic<E> extends BasicHeuristic<E> {

    final double[] alphas, pdf, solSum, solCount, prefixSum;
    final int n_alpha;
    int alphaIndex;

    protected final Random rng = new Random(System.currentTimeMillis());

    ReactiveHeuristic(double param, AbstractGRASP<E> solver) {
        super(param, solver);
        n_alpha = (int) param;
        alphas = new double[n_alpha];
        pdf = new double[n_alpha];
        solSum = new double[n_alpha];
        solCount = new double[n_alpha];
        prefixSum = new double[n_alpha];
        for (int i = 0; i < n_alpha; i++) {
            alphas[i] = (i + 1) * 1.0 / n_alpha;
            pdf[i] = 1.0 / n_alpha; // starts as a uniform distribution
            solSum[i] = 0.0;
            solCount[i] = 0.0;
        }
    }

    void chooseAlpha() {
        prefixSum[0] = pdf[0];
        for (int i = 1; i < n_alpha; i++)
            prefixSum[i] = prefixSum[i - 1] + pdf[i];
        double random = rng.nextDouble();
        alphaIndex = -(Arrays.binarySearch(prefixSum, random) + 1);
        alpha = alphas[alphaIndex];
    }

    void updateDistribution() {
        for (int i = 0; i < n_alpha; i++)
            if (solSum[i] == 0.0 || solCount[i] == 0.0) return; // keep the old pdf
        double denominator = 0.0;
        for (int i = 0; i < n_alpha; i++)
            denominator += -SOLVER.cost / solSum[i] * solCount[i];
        for (int i = 0; i < n_alpha; i++)
            pdf[i] = (-SOLVER.cost / solSum[i] * solCount[i]) / denominator;
    }

    @Override
    public void newSolution() {
        chooseAlpha();
        super.newSolution();
        solSum[alphaIndex] += -SOLVER.cost;
        solCount[alphaIndex]++;
        updateDistribution();
    }
}

