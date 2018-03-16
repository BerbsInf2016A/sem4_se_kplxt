package hadamardui;

import hadamard.ThreadDataAggregator;

/**
 * StaticThreadExecutorHelper can be used to start the search algorithm.
 */
public class StaticThreadExecutorHelper {
    /**
     * The model, containing all necessary data to execute the algorithm.
     */
    private static HadamardModel model;
    /**
     * The ThreadDataAggregator which should be used to aggregate the data.
     */
    private static ThreadDataAggregator aggregator;

    /**
     * Set the model.
     *
     * @param model The model to set.
     */
    public static void setModel(HadamardModel model) {
        StaticThreadExecutorHelper.model = model;
    }

    /**
     * Set the aggregator.
     *
     * @param aggregator The aggregator to set.
     */
    public static void setAggregator(ThreadDataAggregator aggregator) {
        StaticThreadExecutorHelper.aggregator = aggregator;
    }

    /**
     * Execute the algorithm.
     */
    public static void execute() {
        model.execute(aggregator);
    }


}
