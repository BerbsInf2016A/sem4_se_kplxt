package hadamardui;

import hadamard.Matrix;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Tab;

import java.util.Optional;

/**
 * A task to update a whole matrix in the ui.
 */
public class UpdateUIMatrixTask implements Runnable {
    /**
     * Name of the thread. Used to identify the tab.
     */
    private final String threadName;
    /**
     * The changed matrix to set.
     */
    private final Matrix changedMatrix;
    /**
     * The tabs to select the tab to update from.
     */
    private ObservableList<Tab> tabs;
    /**
     * True, if the matrix is a result, false if not.
     */
    private boolean matrixIsResult;

    /**
     * Constructor for the UpdateUIMatrixTask.
     *
     * @param threadName     Name of the thread. Used to identify the tab.
     * @param changedMatrix  The changed matrix to set.
     * @param tabs           The tabs to select the tab to update from.
     * @param matrixIsResult True, if the matrix is a result, false if not.
     */
    public UpdateUIMatrixTask(String threadName, Matrix changedMatrix, ObservableList<Tab> tabs, boolean matrixIsResult) {
        this.threadName = threadName;
        this.changedMatrix = changedMatrix;
        this.tabs = tabs;
        this.matrixIsResult = matrixIsResult;
    }

    /**
     * Execute the operation.
     */
    @Override
    public void run() {
        Optional<Tab> optionalExistingTab = this.tabs.stream().filter(t -> t.getText().equalsIgnoreCase(threadName))
                .findFirst();

        if (optionalExistingTab.isPresent()) { // Tab exists --> update

            Tab tab = optionalExistingTab.get();
            Canvas canvas = UIHelpers.generateMatrixMatrixCanvas(changedMatrix);
            tab.setContent(canvas);
            if (this.matrixIsResult)
                UIHelpers.setResultTabStyle(tab);
        } else { // Create new tab.
            Tab newTab = new Tab();
            newTab.setText(threadName);
            Canvas canvas = UIHelpers.generateMatrixMatrixCanvas(changedMatrix);
            newTab.setContent(canvas);
            if (this.matrixIsResult)
                UIHelpers.setResultTabStyle(newTab);
            this.tabs.add(newTab);
        }
    }
}
