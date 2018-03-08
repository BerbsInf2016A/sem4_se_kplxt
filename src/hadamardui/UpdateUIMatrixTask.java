package hadamardui;

import hadamard.Configuration;
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
        if (optionalExistingTab.isPresent()){
            // TODO Remove debug statments
            Tab tab = optionalExistingTab.get();
            long startTime = System.currentTimeMillis();
            //tab.setContent(UIHelpers.generateMatrix(this.changedMatrix));
            Canvas canvas = UIHelpers.generateMatrixMatrixCanvas(changedMatrix);
            tab.setContent(canvas);
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            System.out.println("old:" + elapsedTime);



            if (this.matrixIsResult)
                tab.setStyle("-fx-border-color:green;");


        } else {
            Tab newTab = new Tab();
            newTab.setText(threadName);
            //TODO Needed for canvas:
            Canvas canvas = UIHelpers.generateMatrixMatrixCanvas(changedMatrix);
            newTab.setContent(canvas);
            this.tabs.add(newTab);
        }
    }
}
