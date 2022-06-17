package problems.kqbf.solvers;

import java.io.IOException;

public class GA_KQBF_Adaptative_Mutation extends GA_KQBF {

    public static final double MULTIPLIER = 3.0; // how much the mutation will increase for below mean individuals

    public GA_KQBF_Adaptative_Mutation(Integer popSize, Double mutationRate, String filename) throws IOException {
        super(popSize, mutationRate, filename);
    }

    @Override
    protected Population mutate(Population offsprings) {
        Double sumFitness = 0.0, meanFitness;
        for (Chromosome c : offsprings)
            sumFitness += c.fitness;
        meanFitness = sumFitness / popSize;

        for (Chromosome c : offsprings) {
            var currMutationRate = c.fitness >= meanFitness ? mutationRate : mutationRate * MULTIPLIER;
            if (rng.nextDouble() < currMutationRate) {
                for (int locus = 0; locus < chromosomeSize; locus++)
                    if (rng.nextDouble() < currMutationRate / 10)
                        mutateGene(c, locus);
                fixChromosome(c); // make the invalid chromosomes viable
                c.fitness = fitness(c);
            }
        }

        return offsprings;
    }
}
