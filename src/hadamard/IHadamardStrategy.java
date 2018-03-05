package hadamard;

public interface IHadamardStrategy {
    void run(ThreadDataAggregator threadDataAggregator);
    boolean canExecutorForDimension(int dimension);
}
