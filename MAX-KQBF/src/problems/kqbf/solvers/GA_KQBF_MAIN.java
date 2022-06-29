package problems.kqbf.solvers;

import solutions.Solution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GA_KQBF_MAIN {

    private static final String INSTANCES_DIR = "instances/kqbf/";
    private static final List<String> INSTANCE_LIST = Arrays.asList(
            "kqbf060",
            "kqbf100",
            "kqbf200");

    private static final Map<String, Double> HALT_COSTS =
            Map.of("kqbf060", 400.0, "kqbf100", 1000.0, "kqbf200", 3200.0);

    private static final List<Integer> POPULATION_LIST = Arrays.asList(100, 200);
    private static final List<Double> MUTATION_LIST = Arrays.asList(10.0 / 100.0, 20.0 / 100.0);

    /**
     * A main method used for testing the Genetic Algorithm metaheuristic.
     */
    public static void main(String[] args) throws IOException {
        var ttt = new ArrayList<ArrayList<Double>>();

        // Default method:
        ttt.add(new ArrayList<>());
        System.out.println("----------------------------------- Default method");
        for (var instance : INSTANCE_LIST) {
            var fileName = INSTANCES_DIR + instance;
            for (int i = 0; i < 50; i++)
                ttt.get(0).add(run_algorithm(i, new GA_KQBF(
                        HALT_COSTS.get(instance), 100, 0.1, fileName), fileName));
        }

        // GA with Adaptative Mutation:
        ttt.add(new ArrayList<>());
        System.out.println("----------------------------------- GA with Adaptative Mutation");
        for (var instance : INSTANCE_LIST) {
            var fileName = INSTANCES_DIR + instance;
            for (int i = 0; i < 50; i++)
                ttt.get(1).add(run_algorithm(i, new GA_KQBF_Adaptative_Mutation(
                        HALT_COSTS.get(instance), 100, 0.1, fileName), fileName));
        }

        for (ArrayList<Double> line : ttt) {
            System.out.print("[");
            for (Double val : line)
                System.out.printf("%4f, ", val);
            System.out.println("]");
        }
    }

    private static double run_algorithm(int iter, GA_KQBF geneticAlgo, String fileName) {
        long startTime = System.currentTimeMillis();
        System.out.println("\n\n============================= " + iter);
        var bestSolution = geneticAlgo.solve();
        double knapsackWeight = geneticAlgo.weight();
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("-----------------");
        System.out.println("instance: " + fileName);
        System.out.println("population: " + geneticAlgo.popSize);
        System.out.println("mutation: " + geneticAlgo.mutationRate);
        System.out.println("generations: " + geneticAlgo.getGenerations());
        System.out.println("Best Solution Found: " + bestSolution);
        System.out.println("Knapsack Weight of Best Solution: " + knapsackWeight);
        System.out.println("Time = " + (double) totalTime / 1000 + " seg");
        return (double) totalTime / 1000;
    }
}
