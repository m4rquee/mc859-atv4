package problems.kqbf.solvers;

import java.io.IOException;

public class GA_KQBF_Adaptative_Mutation extends GA_KQBF {
  public GA_KQBF_Adaptative_Mutation(Integer popSize, Double mutationRate, String filename) throws IOException {
    super(popSize, mutationRate, filename);
  }

  @Override
  protected Population mutate(Population offsprings) {
    Double sumFitness = 0.0;
    Double meanFitness;
    for (Chromosome c : offsprings) {
      sumFitness += fitness(c);
    }
    meanFitness = sumFitness / popSize;

    for (Chromosome c : offsprings) {
      mutationRate = fitness(c) > meanFitness ? mutationRate : mutationRate * 3;
      for (int locus = 0; locus < chromosomeSize; locus++)
        if (rng.nextDouble() < mutationRate)
          mutateGene(c, locus);
    }

    return offsprings;
  }
}
