package hadamardui;

import hadamard.Matrix;
import javafx.collections.ObservableList;
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
            Tab tab = optionalExistingTab.get();
            tab.setContent(UIHelpers.generateMatrix(this.changedMatrix));
            if (this.matrixIsResult)
                tab.setStyle("-fx-border-color:green;");


        } else {
            Tab newTab = new Tab();
            newTab.setText(threadName);
            this.tabs.add(newTab);
        }
    }
}
