package problems.kqbf.solvers;

import java.io.IOException;

public class GA_KQBF_Uniform_Crossover extends GA_KQBF {

    public GA_KQBF_Uniform_Crossover(Integer popSize, Double mutationRate, String filename) throws IOException {
        super(popSize, mutationRate, filename);
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

            Chromosome offspring1 = new Chromosome(), offspring2 = new Chromosome();

            for (int j = 0; j < chromosomeSize; j++)
                if (Math.random() < 0.5) {
                    offspring1.add(parent1.get(j));
                    offspring2.add(parent2.get(j));
                } else {
                    offspring1.add(parent2.get(j));
                    offspring2.add(parent1.get(j));
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
}
