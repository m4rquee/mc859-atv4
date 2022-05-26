package problems.kqbf.solvers;

import solutions.Solution;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TS_KQBF_MAIN {

    private static final String INSTANCES_DIR = "instances/kqbf/";
    private static final List<String> INSTANCE_LIST = Arrays.asList(
            "kqbf020",
            "kqbf040",
            "kqbf060",
            "kqbf080",
            "kqbf100",
            "kqbf200",
            "kqbf400");
    private static final List<Integer> TENURE_LIST = Arrays.asList(7, 14);

    /**
     * A main method used for testing the Tabu Search metaheuristic.
     */
    public static void main(String[] args) throws IOException {
        // Default method:
        System.out.println("----------------------------------- Default method");
        for (var instance : INSTANCE_LIST)
            for (var tenure : TENURE_LIST)
                for (boolean firstImproving : new boolean[]{true, false}) {
                    var fileName = INSTANCES_DIR + instance;
                    run_algorithm(new TS_KQBF(tenure, fileName, firstImproving), fileName);
                }

        // Probabilistic TS strategy:
        System.out.println("----------------------------------- Probabilistic TS strategy");
        for (var instance : INSTANCE_LIST)
            for (var tenure : TENURE_LIST)
                for (boolean firstImproving : new boolean[]{true, false}) {
                    var fileName = INSTANCES_DIR + instance;
                    run_algorithm(new TS_KQBF_Probabilistic(tenure, fileName, firstImproving), fileName);
                }

        // TS with Diversification by Restart:
        System.out.println("----------------------------------- TS with Diversification by Restart");
        for (var instance : INSTANCE_LIST)
            for (var tenure : TENURE_LIST)
                for (boolean firstImproving : new boolean[]{true, false}) {
                    var fileName = INSTANCES_DIR + instance;
                    run_algorithm(new TS_KQBF_Restart_Diversification(tenure, fileName, firstImproving), fileName);
                }
    }

    private static void run_algorithm(TS_KQBF tabuSearch, String fileName) throws IOException {
        long startTime = System.currentTimeMillis();
        System.out.println("\n\n=============================");
        Solution<Integer> bestSolution = tabuSearch.solve();
        double knapsackWeight = tabuSearch.weight();
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("-----------------");
        System.out.println("instance: " + fileName);
        System.out.println("tenure: " + tabuSearch.tenure);
        System.out.println("firstImproving: " + tabuSearch.stImproving);
        System.out.println("iterations: " + TS_KQBF.iterations);
        System.out.println("Best Solution Found: " + bestSolution);
        System.out.println("Knapsack Weight of Best Solution: " + knapsackWeight);
        System.out.println("Time = " + (double) totalTime / (double) 1000 + " seg");
    }
}
