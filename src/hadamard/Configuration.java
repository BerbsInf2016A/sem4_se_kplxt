package hadamard;


public enum Configuration {
    instance;
    final int maximumNumberOfThreads = Runtime.getRuntime().availableProcessors();
    //final int maximumNumberOfThreads = 1;

    public final long maxTimeOutInSeconds = Integer.MAX_VALUE;
    public int dimension = 2;

    public boolean printDebugMessages = false;

    public boolean simulateSteps = true;

    public boolean abortAfterFirstResult = true;

    public int simulationStepDelayInMS = 100;
}
