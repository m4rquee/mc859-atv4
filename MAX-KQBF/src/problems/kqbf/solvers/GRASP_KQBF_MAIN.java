package problems.kqbf.solvers;

import metaheuristics.grasp.ConstructiveHeuristic;

import java.io.IOException;
import java.util.*;

public class GRASP_KQBF_MAIN {

    private static final String INSTANCES_DIR = "instances/kqbf/";
    private static final List<String> INSTANCE_LIST = Arrays.asList("kqbf100", "kqbf200", "kqbf400");

    private static final Map<String, Double> HALT_COSTS = Map.of("kqbf100", 1200.0, "kqbf200", 3800.0, "kqbf400", 9800.0);

    /**
     * A main method used for testing the Genetic Algorithm metaheuristic.
     */
    public static void main(String[] args) throws IOException {
        var ttt = new ArrayList<ArrayList<Double>>();

        // Sampled Greedy constructive heuristic:
        ttt.add(new ArrayList<>());
        System.out.println("----------------------------------- Sampled Greedy constructive heuristic");
        var heu = ConstructiveHeuristic.ConstructiveHeuristicType.SampledGreedy;
        for (var instance : INSTANCE_LIST) {
            var fileName = INSTANCES_DIR + instance;
            for (int i = 0; i < 50; i++)
                ttt.get(0).add(run_algorithm(i, new GRASP_KQBF(
                        50.0, HALT_COSTS.get(instance), fileName, heu, true), fileName));
        }

        // Reactive constructive heuristic:
        ttt.add(new ArrayList<>());
        System.out.println("----------------------------------- Reactive constructive heuristic");
        heu = ConstructiveHeuristic.ConstructiveHeuristicType.Reactive;
        for (var instance : INSTANCE_LIST) {
            var fileName = INSTANCES_DIR + instance;
            for (int i = 0; i < 50; i++)
                ttt.get(1).add(run_algorithm(i, new GRASP_KQBF(
                        1000.0, HALT_COSTS.get(instance), fileName, heu, true), fileName));
        }

        for (ArrayList<Double> line : ttt) {
            System.out.print("[");
            for (Double val : line)
                System.out.printf("%4f, ", val);
            System.out.println("]");
        }
    }

    private static double run_algorithm(int iter, GRASP_KQBF graspAlgo, String fileName) {
        long startTime = System.currentTimeMillis();
        System.out.println("\n\n============================= " + iter);
        var bestSolution = graspAlgo.solve();
        double knapsackWeight = graspAlgo.weight();
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("-----------------");
        System.out.println("instance: " + fileName);
        System.out.println("param: " + graspAlgo.Heuristic.PARAM);
        System.out.println("firstImproving: " + graspAlgo.stImproving);
        System.out.println("iterations: " + (graspAlgo.iter + 1));
        System.out.println("Best Solution Found: " + bestSolution);
        System.out.println("Knapsack Weight of Best Solution: " + knapsackWeight);
        System.out.println("Time = " + (double) totalTime / 1000 + " seg");
        return (double) totalTime / 1000;
    }
}
