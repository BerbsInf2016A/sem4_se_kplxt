package hadamardui;

import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;

import java.util.BitSet;
import java.util.Optional;

public class UpdateUIMatrixColumnTask implements Runnable {
    private final String threadName;
    private final int columnIndex;
    private final BitSet column;
    private final ObservableList<Tab> tabs;

    public UpdateUIMatrixColumnTask(String threadName, int columnIndex, BitSet column, ObservableList<Tab> tabs) {
        this.threadName = threadName;
        this.columnIndex = columnIndex;
        this.column = column;
        this.tabs = tabs;
    }

    @Override
    public void run() {
        Optional<Tab> optionalExistingTab = this.tabs.stream().filter(t -> t.getText().equalsIgnoreCase(threadName))
                .findFirst();
        // tab exists -> update
        if (optionalExistingTab.isPresent()){
            Tab tab = optionalExistingTab.get();
            if (tab.getContent() != null){
                GridPane content = (GridPane) tab.getContent();
                UIHelpers.updateTabContent(content, columnIndex, column);
            } else {
                int g = 0;
            }



        } else {
            Tab newTab = new Tab();
            newTab.setText(threadName);
            this.tabs.add(newTab);
        }
    }
}
