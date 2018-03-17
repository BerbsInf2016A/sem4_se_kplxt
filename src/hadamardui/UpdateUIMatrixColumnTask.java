package hadamardui;

import javafx.collections.ObservableList;
import javafx.scene.control.Tab;

import java.util.BitSet;
import java.util.Optional;

/**
 * A task to update a column of a matrix in the ui.
 */
public class UpdateUIMatrixColumnTask implements Runnable {
    /**
     * Name of the thread. Used to identify the tab.
     */
    private final String threadName;
    /**
     * The index of the column to update.
     */
    private final int columnIndex;
    /**
     * The column value to set,
     */
    private final BitSet column;
    /**
     * The tabs to select the tab to update from.
     */
    private final ObservableList<Tab> tabs;

    /**
     * Constructor for the UpdateUIMatrixColumnTask.
     *
     * @param threadName  Name of the thread. Used to identify the tab.
     * @param columnIndex The index of the column to update.
     * @param column      The column value to set,
     * @param tabs        The tabs to select the tab to update from.
     */
    public UpdateUIMatrixColumnTask(String threadName, int columnIndex, BitSet column, ObservableList<Tab> tabs) {
        this.threadName = threadName;
        this.columnIndex = columnIndex;
        this.column = column;
        this.tabs = tabs;
    }

    /**
     * Execute the operation.
     */
    @Override
    public void run() {
        Optional<Tab> optionalExistingTab = this.tabs.stream().filter(t -> t.getText().equalsIgnoreCase(threadName))
                .findFirst();
        // Tab exists -> update. Tabs should already be created by another task.
        if (optionalExistingTab.isPresent()) {
            Tab tab = optionalExistingTab.get();
            if (tab.getContent() != null) {
                tab.setContent(UIHelpers.updateTabCanvasContent(tab.getContent(), columnIndex, column));
            }
        }
    }
}
