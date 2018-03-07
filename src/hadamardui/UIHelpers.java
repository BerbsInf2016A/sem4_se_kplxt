package hadamardui;

import hadamard.Configuration;
import hadamard.Matrix;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import java.util.BitSet;




public class UIHelpers {


    public static ReadOnlyDoubleProperty tabPaneWidthProperty;
    public static ReadOnlyDoubleProperty tabPaneHeightProperty;

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

    private static void drawRectangle(GraphicsContext gc,Rectangle rect, Color fillColor){
        gc.setFill(Color.GRAY);
        gc.fillRect(rect.getX(),
                rect.getY(),
                rect.getWidth(),
                rect.getHeight());
        gc.setFill(fillColor);
        //gc.setStroke(Color.GRAY);
        gc.fillRect(rect.getX(),
                rect.getY(),
                rect.getWidth() - 2,
                rect.getHeight() - 2);
    }

    public static Canvas generateMatrixMatrixCanvas(Matrix changedMatrix) {
        Canvas canvas = new Canvas();
        canvas.widthProperty().bind(tabPaneWidthProperty);
        canvas.heightProperty().bind(tabPaneHeightProperty);

        GraphicsContext graphicContext = canvas.getGraphicsContext2D();

        int dimension = changedMatrix.getDimension();

        for (int row = 0; row < dimension; row++){
            for (int column = 0; column < dimension; column++){

                Rectangle rec = new Rectangle();
                rec.widthProperty().bind(canvas.widthProperty().divide(dimension + 0.8));
                rec.heightProperty().bind(canvas.heightProperty().divide(dimension + 1.1));
                rec.xProperty().bind(rec.widthProperty().multiply(column + 0.2));
                rec.yProperty().bind(rec.heightProperty().multiply(row + 0.2));

                CellValue value = getCellValue(row, column, changedMatrix);
                switch (value) {
                    case Positive:
                        drawRectangle(graphicContext, rec, Color.BLACK);
                        break;
                    case Negative:
                        drawRectangle(graphicContext, rec, Color.WHITE);
                        break;
                    case Unset:
                        drawRectangle(graphicContext, rec, Color.PINK);
                        break;
                }
            }
        }

        return canvas;
    }

    private static CellValue getCellValue(int row, int column, Matrix matrix) {
        int nextColumIndex = matrix.getNextUnsetColumnIndex();
        if ( column >= nextColumIndex && nextColumIndex != -1 )
            return CellValue.Unset;

        boolean value = matrix.getColumns()[column].get(row);
        return value ? CellValue.Positive : CellValue.Negative;
    }

    public static void updateTabContent(GridPane content, int columnIndex, BitSet column) {
        GridPane root = content;
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
