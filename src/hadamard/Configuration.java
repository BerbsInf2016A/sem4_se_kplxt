package hadamard;


public enum Configuration {
    instance;
    public final long maxTimeOutInSeconds = Integer.MAX_VALUE;
    final int maximumNumberOfThreads = Runtime.getRuntime().availableProcessors();
    public int dimension = 2;

    public boolean printDebugMessages = false;

    public boolean simulateSteps = true;

    public boolean abortAfterFirstResult = true;

    public int simulationStepDelayInMS = 100;
}
