package problems.kqbf.solvers;

import solutions.Solution;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GA_KQBF_MAIN {

    private static final String INSTANCES_DIR = "instances/kqbf/";
    private static final List<String> INSTANCE_LIST = Arrays.asList(
            "kqbf020",
            "kqbf040",
            "kqbf060",
            "kqbf080",
            "kqbf100",
            "kqbf200",
            "kqbf400");
    private static final List<Integer> POPULATION_LIST = Arrays.asList(10000, 100000);
    private static final List<Double> MUTATION_LIST = Arrays.asList(1.0 / 100.0, 0.5 / 100.0);

    /**
     * A main method used for testing the Genetic Algorithm metaheuristic.
     */
    public static void main(String[] args) throws IOException {
        // Default method:
        System.out.println("----------------------------------- Default method");
        for (var instance : INSTANCE_LIST)
            for (var population : POPULATION_LIST)
                for (var mutation : MUTATION_LIST) {
                    var fileName = INSTANCES_DIR + instance;
                    run_algorithm(new GA_KQBF(population, mutation, fileName), fileName);
                }

        // 1st GA strategy:
        System.out.println("----------------------------------- 1st GA strategy");
        for (var instance : INSTANCE_LIST)
            for (var population : POPULATION_LIST)
                for (var mutation : MUTATION_LIST) {
                    var fileName = INSTANCES_DIR + instance;
                    run_algorithm(new GA_KQBF_Uniform_Crossover(population, mutation, fileName), fileName);
                }

        // 2nd GA strategy:
        System.out.println("----------------------------------- 2nd GA strategy");
        for (var instance : INSTANCE_LIST)
            for (var population : POPULATION_LIST)
                for (var mutation : MUTATION_LIST) {
                    var fileName = INSTANCES_DIR + instance;
                    run_algorithm(new GA_KQBF_Adaptative_Mutation(population, mutation, fileName), fileName);
                }
    }

    private static void run_algorithm(GA_KQBF geneticAlgo, String fileName) {
        long startTime = System.currentTimeMillis();
        System.out.println("\n\n=============================");
        Solution<Integer> bestSolution = geneticAlgo.solve();
        double knapsackWeight = geneticAlgo.weight();
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("-----------------");
        System.out.println("instance: " + fileName);
        System.out.println("population: " + geneticAlgo.popSize);
        System.out.println("mutation: " + geneticAlgo.mutationRate);
        System.out.println("generations: " + GA_KQBF.generations);
        System.out.println("Best Solution Found: " + bestSolution);
        System.out.println("Knapsack Weight of Best Solution: " + knapsackWeight);
        System.out.println("Time = " + (double) totalTime / (double) 1000 + " seg");
    }
}
