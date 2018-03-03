package hadamard;

public class TempMain {
    public static void main(String args[]) {
        long startTime = System.currentTimeMillis();
        Algorithm algo = new Algorithm();
        algo.run(16);
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println(Configuration.instance.debugCounter.get());
        System.out.println(elapsedTime);
    }
}
