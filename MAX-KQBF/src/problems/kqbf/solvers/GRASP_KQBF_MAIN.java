package problems.kqbf.solvers;

import metaheuristics.grasp.ConstructiveHeuristic;
import solutions.Solution;

import java.io.IOException;
import java.util.*;

public class GRASP_KQBF_MAIN {

    private static final String INSTANCES_DIR = "instances/kqbf/";
    private static final List<String> INSTANCE_LIST = Arrays.asList(
            "kqbf060",
            "kqbf100",
            "kqbf200");

    private static final Map<String, Double> HALT_COSTS =
            Map.of("kqbf060", 450.0, "kqbf100", 1100.0, "kqbf200", 3500.0);

    /**
     * A main method used for testing the Genetic Algorithm metaheuristic.
     */
    public static void main(String[] args) throws IOException {
        var ttt = new ArrayList<ArrayList<Double>>();

        // Basic constructive heuristic:
        ttt.add(new ArrayList<>());
        System.out.println("----------------------------------- Basic constructive heuristic");
        var heu = ConstructiveHeuristic.ConstructiveHeuristicType.Basic;
        for (var instance : INSTANCE_LIST) {
            var fileName = INSTANCES_DIR + instance;
            var solver = new GRASP_KQBF(0.05, HALT_COSTS.get(instance), fileName, heu, false);
            for (int i = 0; i < 50; i++)
                ttt.get(0).add(run_algorithm(i, solver, fileName));
        }

        // Sampled Greedy constructive heuristic:
        ttt.add(new ArrayList<>());
        System.out.println("----------------------------------- Sampled Greedy constructive heuristic");
        heu = ConstructiveHeuristic.ConstructiveHeuristicType.SampledGreedy;
        for (var instance : INSTANCE_LIST) {
            var fileName = INSTANCES_DIR + instance;
            var solver = new GRASP_KQBF(20.0, HALT_COSTS.get(instance), fileName, heu, false);
            for (int i = 0; i < 50; i++)
                ttt.get(1).add(run_algorithm(i, solver, fileName));
        }

        for (ArrayList<Double> line : ttt) {
            System.out.print("[");
            for (Double val : line) {
                System.out.printf("%4f, ", val);
            }
            System.out.println("]");
        }
    }

    private static double run_algorithm(int iter, GRASP_KQBF graspAlgo, String fileName) {
        long startTime = System.currentTimeMillis();
        System.out.println("\n\n============================= " + iter);
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
        return (double) totalTime / 1000;
    }
}
