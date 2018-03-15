package hadamard;

/**
 * Context for the Hadamard algorithm.
 */
public class HadamardContext {
    /**
     * The strategy to use.
     */
    private IHadamardStrategy strategy;

    /**
     * Constructor for the HadamardContext.
     *
     * @param strategy The strategy to use.
     */
    public HadamardContext(IHadamardStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Execute the strategy.
     *
     * @param threadDataAggregator The ThreadDataAggregator to use.
     */
    public void executeStrategy(ThreadDataAggregator threadDataAggregator) {
        strategy.run(threadDataAggregator);
    }

    /**
     * Check if the strategy can execute for the given dimension.
     *
     * @param dimension The dimension.
     * @return True if the strategy can execute, false if not.
     */
    public boolean canExecuteForDimension(int dimension) {
        return strategy.canExecuteForDimension(dimension);
    }
}
