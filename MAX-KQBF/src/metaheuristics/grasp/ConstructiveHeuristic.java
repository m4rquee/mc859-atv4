package metaheuristics.grasp;

public abstract class ConstructiveHeuristic<E> {

    public enum ConstructiveHeuristicType {
        Basic, SampledGreedy, Reactive
    }

    final AbstractGRASP<E> SOLVER;

    public final double PARAM;

    public ConstructiveHeuristic(double param, AbstractGRASP<E> solver) {
        PARAM = param;
        SOLVER = solver;
    }

    public abstract void newSolution();
}
