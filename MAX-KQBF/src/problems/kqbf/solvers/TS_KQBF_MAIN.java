package problems.kqbf.solvers;

import solutions.Solution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TS_KQBF_MAIN {

    private static final String INSTANCES_DIR = "instances/kqbf/";
    private static final List<String> INSTANCE_LIST = Arrays.asList("kqbf060", "kqbf100", "kqbf200");

    private static final Map<String, Double> HALT_COSTS =
            Map.of("kqbf060", 400.0, "kqbf100", 1000.0, "kqbf200", 3200.0);

    /**
     * A main method used for testing the Tabu Search metaheuristic.
     */
    public static void main(String[] args) throws IOException {
        var ttt = new ArrayList<ArrayList<Double>>();

        // Probabilistic TS strategy:
        ttt.add(new ArrayList<>());
        System.out.println("----------------------------------- Probabilistic TS strategy");
        for (var instance : INSTANCE_LIST) {
            var fileName = INSTANCES_DIR + instance;
            for (int i = 0; i < 50; i++)
                ttt.get(0).add(run_algorithm(i, new TS_KQBF_Probabilistic(
                        HALT_COSTS.get(instance), 7, fileName, false), fileName));
        }

        // TS with Diversification by Restart:
        ttt.add(new ArrayList<>());
        System.out.println("----------------------------------- TS with Diversification by Restart");
        for (var instance : INSTANCE_LIST) {
            var fileName = INSTANCES_DIR + instance;
            for (int i = 0; i < 50; i++)
                ttt.get(1).add(run_algorithm(i, new TS_KQBF_Restart_Diversification(
                        HALT_COSTS.get(instance), 7, fileName, false), fileName));
        }

        for (ArrayList<Double> line : ttt) {
            System.out.print("[");
            for (Double val : line)
                System.out.printf("%4f, ", val);
            System.out.println("]");
        }
    }

    private static double run_algorithm(int iter, TS_KQBF tabuSearch, String fileName) {
        long startTime = System.currentTimeMillis();
        System.out.println("\n\n============================= " + iter);
        Solution<Integer> bestSolution = tabuSearch.solve();
        double knapsackWeight = tabuSearch.weight();
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("-----------------");
        System.out.println("instance: " + fileName);
        System.out.println("tenure: " + tabuSearch.tenure);
        System.out.println("firstImproving: " + tabuSearch.stImproving);
        System.out.println("iterations: " + (tabuSearch.iter + 1));
        System.out.println("Best Solution Found: " + bestSolution);
        System.out.println("Knapsack Weight of Best Solution: " + knapsackWeight);
        System.out.println("Time = " + (double) totalTime / (double) 1000 + " seg");
        return (double) totalTime / 1000;
    }
}
