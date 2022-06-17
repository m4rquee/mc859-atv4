package problems.kqbf.solvers;

import problems.Evaluator;
import problems.kqbf.KQBF;
import problems.qbf.solvers.GA_QBF;
import solutions.KSolution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

class Contribution {
    protected Double contribution;
    protected Integer variable;

    public Contribution(Double contribution, Integer variable) {
        this.contribution = contribution;
        this.variable = variable;
    }

    public Double getContribution() {
        return contribution;
    }
}

public class GA_KQBF extends GA_QBF {

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
        super(0, popSize, mutationRate, filename);
        generations = chromosomeSize * 10;
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
        for (int i = 0; i < chromosomeSize; i++)
            chromosome.add(rng.nextInt(2));
        fixChromosome(chromosome); // make an invalid chromosome viable
        return chromosome;
    }

    protected void fixChromosome(Chromosome chromosome) {
        var decodedChromosome = (KSolution<Integer>) decode(chromosome);
        double currWeight = decodedChromosome.weigth;

        // Se o peso do cromossomo ultrapassar o peso da mochila, remove alguns items em
        // ordem ordem crescente de benefício/peso:
        KQBF auxRef = ((KQBF) ObjFunction);
        if (currWeight > auxRef.W_max) {
            // Armazena os items que estão na mochila e seu benefício/peso:
            ArrayList<Contribution> contributions = new ArrayList<>();
            for (int i = 0; i < chromosomeSize; i++)
                if (chromosome.get(i) == 1.0) {
                    var contribution = auxRef.evaluateContributionQBF(i, decodedChromosome) / auxRef.W[i];
                    contributions.add(new Contribution(contribution, i));
                }

            contributions.sort(Comparator.comparing(Contribution::getContribution)); // ordena em ordem crescente
            // Remove os que menos contribuem até que o cromossomo seja viável:
            while (currWeight > auxRef.W_max) {
                var worstVar = contributions.remove(0);
                chromosome.set(worstVar.variable, 0);
                currWeight -= auxRef.W[worstVar.variable];
            }
        }
    }

    @Override
    protected Population crossover(Population parents) {
        Population offsprings = new Population();

        for (int i = 0; i < popSize; i = i + 2) {
            Chromosome parent1 = parents.get(i), parent2 = parents.get(i + 1);

            if (parent1 == parent2) { // Save time as the offspring will be the same as the parents:
                offsprings.add(parent1);
                offsprings.add(parent2);
                continue;
            }

            int crosspoint1 = rng.nextInt(chromosomeSize + 1);
            int crosspoint2 = crosspoint1 + rng.nextInt((chromosomeSize + 1) - crosspoint1);

            Chromosome offspring1 = new Chromosome(), offspring2 = new Chromosome();

            for (int j = 0; j < chromosomeSize; j++)
                if (j >= crosspoint1 && j < crosspoint2) {
                    offspring1.add(parent2.get(j));
                    offspring2.add(parent1.get(j));
                } else {
                    offspring1.add(parent1.get(j));
                    offspring2.add(parent2.get(j));
                }

            // Make the invalid chromosomes viable:
            fixChromosome(offspring1);
            fixChromosome(offspring2);

            offspring1.fitness = fitness(offspring1);
            offspring2.fitness = fitness(offspring2);
            offsprings.add(offspring1);
            offsprings.add(offspring2);
        }

        return offsprings;
    }

    protected Population mutate(Population offsprings) {
        for (Chromosome c : offsprings) {
            if (rng.nextDouble() < mutationRate) {
                for (int locus = 0; locus < chromosomeSize; locus++)
                    if (rng.nextDouble() < mutationRate / 10)
                        mutateGene(c, locus);
                fixChromosome(c); // make the invalid chromosomes viable
                c.fitness = fitness(c);
            }
        }
        return offsprings;
    }
}
