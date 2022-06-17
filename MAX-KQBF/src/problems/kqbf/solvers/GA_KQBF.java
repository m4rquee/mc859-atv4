package problems.kqbf.solvers;

import problems.Evaluator;
import problems.kqbf.KQBF;
import problems.qbf.solvers.GA_QBF;
import solutions.KSolution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

class Contribution {
    public Double contribution;
    public Integer variable;

    public Contribution(Double contribution, Integer variable) {
        this.contribution = contribution;
        this.variable = variable;
    }

    public Double getContribution() {
        return contribution;
    }
};

public class GA_KQBF extends GA_QBF {

    protected static final Integer generations = 10000;

    /**
     * Constructor for the GA_QBF class. The QBF objective function is passed as
     * argument for the superclass constructor.
     *
     * @param popSize      Size of the population.
     * @param mutationRate The mutation rate.
     * @param filename     Name of the file for which the objective function
     *                     parameters
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
    protected Chromosome generateRandomChromosome() {

        Chromosome chromosome = new Chromosome();
        for (int i = 0; i < chromosomeSize; i++) {
            chromosome.add(rng.nextInt(2));
        }

        // Torna o cromossomo viável caso não seja
        chromosome = fixChromosome(chromosome);

        return chromosome;
    }

    protected Double chromosomeWeight(Chromosome chromosome) {
        Double chromosomeWeight = 0.0;
        for (int index = 0; index < chromosomeSize; index++) {
            chromosomeWeight += chromosome.get(index) * ((KQBF) ObjFunction).W[index];
        }
        return chromosomeWeight;
    }

    protected Chromosome fixChromosome(Chromosome chromosome) {
        Double chromosomeWeight = chromosomeWeight(chromosome);

        // Se o peso do cromossomo ultrapassa o peso da mochila, remove alguns items em
        // ordem ordem crescente de benefício/peso
        if (chromosomeWeight > ((KQBF) ObjFunction).W_max) {
            // Armazena os items que estão na mochila e seu benefício/peso
            ArrayList<Contribution> contributions = new ArrayList<Contribution>();
            for (int index = 0; index < chromosomeSize; index++) {
                if (chromosome.get(index) > 0.0) {
                    contributions.add(
                            new Contribution(((KQBF) ObjFunction).evaluateContributionQBF(index, decode(chromosome))
                                    / ((KQBF) ObjFunction).W[index], index));
                }
            }

            // Ordena em ordem crescente
            Collections.sort(contributions, Comparator.comparing(Contribution::getContribution));
            // Remove os que menos contribuem até que o cromossomo seja viável
            while (chromosomeWeight > ((KQBF) ObjFunction).W_max) {
                chromosome.set(contributions.remove(0).variable, 0);
                chromosomeWeight = chromosomeWeight(chromosome);
            }
        }

        return chromosome;
    }

    @Override
    protected Population crossover(Population parents) {

        Population offsprings = new Population();

        for (int i = 0; i < popSize; i = i + 2) {

            Chromosome parent1 = parents.get(i);
            Chromosome parent2 = parents.get(i + 1);

            int crosspoint1 = rng.nextInt(chromosomeSize + 1);
            int crosspoint2 = crosspoint1 + rng.nextInt((chromosomeSize + 1) - crosspoint1);

            Chromosome offspring1 = new Chromosome();
            Chromosome offspring2 = new Chromosome();

            for (int j = 0; j < chromosomeSize; j++) {
                if (j >= crosspoint1 && j < crosspoint2) {
                    offspring1.add(parent2.get(j));
                    offspring2.add(parent1.get(j));
                } else {
                    offspring1.add(parent1.get(j));
                    offspring2.add(parent2.get(j));
                }
            }

            offsprings.add(offspring1);
            offsprings.add(offspring2);

        }

        // Torna os cromossomos viáveis caso não sejam
        for (Chromosome offspring : offsprings) {
            offspring = fixChromosome(offspring);
        }

        return offsprings;

    }
}
