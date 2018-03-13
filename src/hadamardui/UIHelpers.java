package hadamardui;

import hadamard.Configuration;
import hadamard.Matrix;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import java.util.BitSet;

import static hadamardui.UIConfiguration.*;


public class UIHelpers {

    private static void drawRectangle(GraphicsContext gc,Rectangle rect, Color fillColor){
        gc.setFill(Color.GRAY);
        gc.fillRect(rect.getX(),
                rect.getY(),
                rect.getWidth(),
                rect.getHeight());
        gc.setFill(fillColor);
        gc.fillRect(rect.getX(),
                rect.getY(),
                rect.getWidth() - 2,
                rect.getHeight() - 2);
    }

    public static Canvas generateMatrixMatrixCanvas(Matrix changedMatrix) {
        Canvas canvas = new Canvas();
        canvas.widthProperty().bind(UIConfiguration.tabPaneWidthProperty);
        canvas.heightProperty().bind(UIConfiguration.tabPaneHeightProperty.multiply(0.95));

        GraphicsContext graphicContext = canvas.getGraphicsContext2D();

        int dimension = changedMatrix.getDimension();

        for (int row = 0; row < dimension; row++){
            for (int column = 0; column < dimension; column++){

                Rectangle rec = new Rectangle();



                rec.widthProperty().bind(canvas.widthProperty().divide(dimension ));
                rec.heightProperty().bind(canvas.heightProperty().divide(dimension ));
                rec.xProperty().bind(rec.widthProperty().multiply(column ));
                rec.yProperty().bind(rec.heightProperty().multiply(row ));


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


    private static CellValue getCellValue(Integer rowIndex, BitSet column) {
        boolean value = column.get(rowIndex);
        if (value) return CellValue.Positive;
        return CellValue.Negative;
    }

    public static Canvas updateTabCanvasContent(Node content, int columnIndex, BitSet column) {
        Canvas canvas = (Canvas) content;
        GraphicsContext graphicContext = canvas.getGraphicsContext2D();
        int dimension = column.length();
        for (int row = 0; row < dimension; row++){
            Rectangle rec = new Rectangle();

            rec.widthProperty().bind(canvas.widthProperty().divide(dimension ));
            rec.heightProperty().bind(canvas.heightProperty().divide(dimension ));
            rec.xProperty().bind(rec.widthProperty().multiply(columnIndex ));
            rec.yProperty().bind(rec.heightProperty().multiply(row ));


            CellValue value = getCellValue(row, column);
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
        return canvas;
    }
}
