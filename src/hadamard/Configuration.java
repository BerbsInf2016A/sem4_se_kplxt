package hadamard;

/**
 * Configuration for the Hadamard Matrix generation tool.
 */
public enum Configuration {
    instance;

    /**
     * The Maximum Number of threads available.
     */
    final int maximumNumberOfThreads = Runtime.getRuntime().availableProcessors();

    /**
     * The Timeout value.
     */
    public final long maxTimeOutInSeconds = Integer.MAX_VALUE;

    /**
     * The dimension of the Matrix to generate.
     */
    public int dimension = 2;

    /**
     * Boolean flag indicating if the Debug Messages should be printed out.
     */
    public boolean printDebugMessages = true;

    /**
     * Boolean flag indicating if the calculation steps should be simulated.
     */
    public boolean simulateSteps = true;

    /**
     * Boolean flag indicating if the algorithm should abort after finding one result.
     */
    public boolean abortAfterFirstResult = true;

    /**
     * The delay for the simulation step.
     */
    public int simulationStepDelayInMS = 100;
}
