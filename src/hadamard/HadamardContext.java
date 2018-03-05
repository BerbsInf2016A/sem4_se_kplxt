package hadamard;

public class HadamardContext {
    private IHadamardStrategy strategy;

    public HadamardContext(IHadamardStrategy strategy) {
        this.strategy = strategy;
    }
    public void executeStrategy(ThreadDataAggregator threadDataAggregator) {
        strategy.run(threadDataAggregator);
    }

    public boolean canExceuteForDimension(int dimension) {
        return strategy.canExecutorForDimension(dimension);
    }
}
