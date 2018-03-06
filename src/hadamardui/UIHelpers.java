package hadamardui;

import hadamard.Configuration;
import hadamard.Matrix;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import java.util.BitSet;


public class UIHelpers {
    public static GridPane generateMatrix(Matrix changedMatrix) {
        GridPane root = new GridPane();
        int dimension = changedMatrix.getDimension();

        for (int row = 0; row < dimension; row++){
            for (int column = 0; column < dimension; column++){
                Rectangle rec = new Rectangle();
                rec.widthProperty().bind(root.widthProperty().divide(dimension));
                rec.heightProperty().bind(root.heightProperty().divide(dimension));
                rec.setStroke(Color.GRAY);
                rec.setStrokeType(StrokeType.INSIDE);

                CellValue value = getCellValue(row, column, changedMatrix);
                switch (value) {
                    case Positive:
                        rec.setFill(Color.BLACK);
                        break;
                    case Negative:
                        rec.setFill(Color.WHITE);
                        break;
                    case Unset:
                        rec.setFill(Color.PINK);
                        break;
                }
                root.add(rec, column, row);
            }
        }

        return root;
    }

    private static CellValue getCellValue(int row, int column, Matrix matrix) {
        int nextColumIndex = matrix.getNextUnsetColumnIndex();
        if ( column >= nextColumIndex && nextColumIndex != -1 )
            return CellValue.Unset;

        boolean value = matrix.getColumns()[column].get(row);
        return value ? CellValue.Positive : CellValue.Negative;
    }

    public static void updateTabContent(Tab tab, int columnIndex, BitSet column) {
        GridPane root = (GridPane) tab.getContent();
        for (int row = 0; row < Configuration.instance.dimension; row++){
            Rectangle rec =  (Rectangle)(root.getChildren().get(row* Configuration.instance.dimension+ columnIndex));
            updateValue(rec, row, column);
        }
/*
        for (Node node : root.getChildren()) {
            if (GridPane.getColumnIndex(node) == columnIndex){
                Rectangle rec = (Rectangle) node;
                updateValue(rec, GridPane.getRowIndex(node), column);
            }
        }
        */
    }

    private static void updateValue(Rectangle rec, Integer rowIndex, BitSet column) {
        CellValue value = getCellValue(rowIndex, column);
        switch (value) {
            case Positive:
                rec.setFill(Color.BLACK);
                break;
            case Negative:
                rec.setFill(Color.WHITE);
                break;
            case Unset:
                rec.setFill(Color.PINK);
                break;
        }

    }

    private static CellValue getCellValue(Integer rowIndex, BitSet column) {
        boolean value = column.get(rowIndex);
        if (value) return CellValue.Positive;
        return CellValue.Negative;
    }
}
