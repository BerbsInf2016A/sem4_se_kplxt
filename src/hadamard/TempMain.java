package hadamard;

public class TempMain {
    public static void main(String args[]) {
        long startTime = System.currentTimeMillis();

        Configuration.instance.dimension = 4;
        BruteForceAlgorithm algo = new BruteForceAlgorithm();

        ThreadDataAggregator threadDataAggregator = new ThreadDataAggregator();
        algo.run(threadDataAggregator);

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println(Configuration.instance.debugCounter.get());
        System.out.println(elapsedTime);
    }
}
