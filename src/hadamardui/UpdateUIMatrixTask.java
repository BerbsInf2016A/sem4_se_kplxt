package hadamardui;

import hadamard.Matrix;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Tab;

import java.util.Optional;

public class UpdateUIMatrixTask implements Runnable {


    private final String threadName;
    private final Matrix changedMatrix;
    private ObservableList<Tab> tabs;
    private boolean matrixIsResult;

    public UpdateUIMatrixTask(String threadName, Matrix changedMatrix, ObservableList<Tab> tabs, boolean matrixIsResult) {
        this.threadName = threadName;
        this.changedMatrix = changedMatrix;
        this.tabs = tabs;
        this.matrixIsResult = matrixIsResult;
    }

    @Override
    public void run() {
        Optional<Tab> optionalExistingTab = this.tabs.stream().filter(t -> t.getText().equalsIgnoreCase(threadName))
                .findFirst();
        // tab exists -> update
        if (optionalExistingTab.isPresent()) {

            Tab tab = optionalExistingTab.get();
            Canvas canvas = UIHelpers.generateMatrixMatrixCanvas(changedMatrix);
            tab.setContent(canvas);
            // TODO Move style out
            if (this.matrixIsResult)
                tab.setStyle("-fx-border-color:green; -fx-background-color: green;  -fx-font-weight: bold;");


        } else {
            Tab newTab = new Tab();
            newTab.setText(threadName);
            //TODO Needed for canvas:
            Canvas canvas = UIHelpers.generateMatrixMatrixCanvas(changedMatrix);
            newTab.setContent(canvas);
            if (this.matrixIsResult)
                newTab.setStyle("-fx-border-color:green; -fx-background-color: green;  -fx-font-weight: bold;");
            this.tabs.add(newTab);
        }
    }
}
