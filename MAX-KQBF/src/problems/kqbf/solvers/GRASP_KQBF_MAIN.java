package problems.kqbf.solvers;

import metaheuristics.grasp.ConstructiveHeuristic;
import solutions.Solution;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GRASP_KQBF_MAIN {

    private static final String INSTANCES_DIR = "instances/kqbf/";
    private static final List<String> INSTANCE_LIST = Arrays.asList(
            "kqbf020",
            "kqbf040",
            "kqbf060",
            "kqbf080",
            "kqbf100",
            "kqbf200",
            "kqbf400");
    private static final List<Double> ALPHAS = Arrays.asList(0.05, 0.5);

    /**
     * A main method used for testing the Genetic Algorithm metaheuristic.
     */
    public static void main(String[] args) throws IOException {
        // Basic constructive heuristic:
        System.out.println("----------------------------------- Basic constructive heuristic");
        var heu = ConstructiveHeuristic.ConstructiveHeuristicType.Basic;
        for (var instance : INSTANCE_LIST)
            for (var alpha : ALPHAS)
                for (boolean stImproving : new boolean[]{true, false}) {
                    var fileName = INSTANCES_DIR + instance;
                    run_algorithm(new GRASP_KQBF(alpha, fileName, heu, stImproving), fileName);
                }

        // Sampled Greedy constructive heuristic:
        System.out.println("----------------------------------- Sampled Greedy constructive heuristic");
        heu = ConstructiveHeuristic.ConstructiveHeuristicType.SampledGreedy;
        for (var instance : INSTANCE_LIST)
            for (boolean stImproving : new boolean[]{true, false}) {
                var fileName = INSTANCES_DIR + instance;
                run_algorithm(new GRASP_KQBF(20.0, fileName, heu, stImproving), fileName);
            }

        // Reactive constructive heuristic:
        System.out.println("----------------------------------- Reactive constructive heuristic");
        heu = ConstructiveHeuristic.ConstructiveHeuristicType.Reactive;
        for (var instance : INSTANCE_LIST)
            for (boolean stImproving : new boolean[]{true, false}) {
                var fileName = INSTANCES_DIR + instance;
                run_algorithm(new GRASP_KQBF(100.0, fileName, heu, stImproving), fileName);
            }
    }

    private static void run_algorithm(GRASP_KQBF graspAlgo, String fileName) {
        long startTime = System.currentTimeMillis();
        System.out.println("\n\n=============================");
        Solution<Integer> bestSolution = graspAlgo.solve();
        double knapsackWeight = graspAlgo.weight();
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("-----------------");
        System.out.println("instance: " + fileName);
        System.out.println("param: " + graspAlgo.Heuristic.PARAM);
        System.out.println("firstImproving: " + graspAlgo.stImproving);
        System.out.println("iterations: " + GRASP_KQBF.iterations);
        System.out.println("Best Solution Found: " + bestSolution);
        System.out.println("Knapsack Weight of Best Solution: " + knapsackWeight);
        System.out.println("Time = " + (double) totalTime / (double) 1000 + " seg");
    }
}
