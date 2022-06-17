package problems.kqbf.solvers;

import problems.Evaluator;
import problems.kqbf.KQBF;
import problems.qbf.solvers.GA_QBF;
import solutions.KSolution;

import java.io.IOException;

public class GA_KQBF extends GA_QBF {

    protected static final Integer generations = 10000;

    /**
     * Constructor for the GA_QBF class. The QBF objective function is passed as
     * argument for the superclass constructor.
     *
     * @param popSize      Size of the population.
     * @param mutationRate The mutation rate.
     * @param filename     Name of the file for which the objective function parameters
     *                     should be read.
     * @throws IOException Necessary for I/O operations.
     */
    public GA_KQBF(Integer popSize, Double mutationRate, String filename) throws IOException {
        super(generations, popSize, mutationRate, filename);
    }

    public double weight() {
        return ((KSolution<Integer>) bestSol).weigth;
    }

    @Override
    protected Evaluator<Integer> initEvaluator(String filename) throws IOException {
        return new KQBF(filename);
    }

    @Override
    public KSolution<Integer> createEmptySol() {
        KSolution<Integer> sol = new KSolution<>(super.createEmptySol());
        sol.weigth = 0.0;
        return sol;
    }

    @Override
    protected GA_QBF.Chromosome generateRandomChromosome() {
        double weight = 0.0;
        KQBF auxRef = (KQBF) ObjFunction;
        Chromosome chromosome = new Chromosome();
        for (int i = 0; i < chromosomeSize; i++) {
            var locus = rng.nextInt(2);
            if (locus == 1)  // Must check the capacity constraint:
                if (weight + auxRef.W[i] > auxRef.W_max)
                    locus = 0; // cannot add this item
            chromosome.add(locus);
            weight += locus * auxRef.W[i];
        }

        return chromosome;
    }
}
