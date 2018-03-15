package hadamard;

/**
 * Interface for the Hadamard Strategy.
 */
public interface IHadamardStrategy {
    /**
     * The run Method for starting the Algorithm.
     *
     * @param threadDataAggregator The Thread Data Aggregator.
     */
    void run(ThreadDataAggregator threadDataAggregator);
    /**
     * Checks if the Algorithm can run for a given dimension.
     *
     * @param dimension The dimension.
     * @return Boolean indicating if the Algorithm can be executed for the given dimension.
     */
    boolean canExecuteForDimension(int dimension);
}
