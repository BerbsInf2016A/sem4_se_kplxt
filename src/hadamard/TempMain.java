package hadamard;

public class TempMain {
    public static void main(String args[]) throws InterruptedException {
        long startTime = System.currentTimeMillis();

        //Thread.sleep(5000);
        Configuration.instance.dimension = 32768;
        //BacktrackingAlgorithm algo = new BacktrackingAlgorithm();
        SylvesterAlgorithm algo = new SylvesterAlgorithm();

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
        //System.out.println(ThreadDataAggregator.resultMatrix.getUIDebugStringRepresentation());

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println(elapsedTime);
    }
}
