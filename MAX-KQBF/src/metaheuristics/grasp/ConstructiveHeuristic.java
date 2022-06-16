package metaheuristics.grasp;

public abstract class ConstructiveHeuristic<E> {

    public enum ConstructiveHeuristicType {
        Basic, SampledGreedy, Reactive
    }

    final AbstractGRASP<E> SOLVER;

    public ConstructiveHeuristic(double param, AbstractGRASP<E> solver) {
        SOLVER = solver;
    }

    public abstract void newSolution();
}
