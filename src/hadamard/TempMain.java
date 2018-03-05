package hadamard;

public class TempMain {
    public static void main(String args[]) throws InterruptedException {
        long startTime = System.currentTimeMillis();

        Configuration.instance.dimension = 28;
        BruteForceAlgorithm algo = new BruteForceAlgorithm();

        try {
            ThreadDataAggregator threadDataAggregator = new ThreadDataAggregator();
            algo.run(threadDataAggregator);
        } catch (RuntimeException ex){
            // Probably cancelled by us.
            if (ThreadDataAggregator.resultFound.get()) {

                Thread.sleep(300);
            } else {
                ex.printStackTrace();
            }
        }


        System.out.println(ThreadDataAggregator.resultThreadName + " found:");
        System.out.println(ThreadDataAggregator.resultMatrix.getUIDebugStringRepresentation());

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println(Configuration.instance.debugCounter.get());
        System.out.println(elapsedTime);
    }
}
