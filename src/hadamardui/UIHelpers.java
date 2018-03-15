package hadamardui;

import hadamard.Matrix;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tab;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.BitSet;

/**
 * Class containing helper methods for the ui.
 */
public class UIHelpers {

    /**
     * Draw a rectangle on a given GraphicsContext.
     *
     * @param gc The GraphicsContext to draw on.
     * @param rect The rectangle to draw.
     * @param fillColor The color which should be used to fill the rectangle.
     */
    private static void drawRectangle(GraphicsContext gc, Rectangle rect, Color fillColor) {
        // Draw border.
        gc.setFill(Color.GRAY);
        gc.fillRect(rect.getX(),
                rect.getY(),
                rect.getWidth(),
                rect.getHeight());
        // Draw rectangle.
        gc.setFill(fillColor);
        gc.fillRect(rect.getX(),
                rect.getY(),
                rect.getWidth() - 2,
                rect.getHeight() - 2);
    }

    /**
     * Generate a Canvas for a given matrix.
     *
     * @param matrix The matrix to generate the canvas for.
     * @return The created canvas.
     */
    public static Canvas generateMatrixMatrixCanvas(Matrix matrix) {
        // Create the canvas and bind it to the outer ui element.
        Canvas canvas = new Canvas();
        canvas.widthProperty().bind(UIConfiguration.tabPaneWidthProperty);
        // The height must be reduced, because the size of the tab pane includes the tab headers.
        canvas.heightProperty().bind(UIConfiguration.tabPaneHeightProperty.multiply(0.95));

        GraphicsContext graphicContext = canvas.getGraphicsContext2D();
        int dimension = matrix.getDimension();

        // Iterate the matrix and create the rectangles on the canvas.
        for (int row = 0; row < dimension; row++) {
            for (int column = 0; column < dimension; column++) {
                // Create, calculate the size and bind the rectangle.
                Rectangle rec = new Rectangle();
                rec.widthProperty().bind(canvas.widthProperty().divide(dimension));
                rec.heightProperty().bind(canvas.heightProperty().divide(dimension));
                rec.xProperty().bind(rec.widthProperty().multiply(column));
                rec.yProperty().bind(rec.heightProperty().multiply(row));

                // Draw the rectangle on the canvas.
                CellValue value = getCellValue(row, column, matrix);
                drawRectangleForColor(graphicContext, rec, value);
            }
        }

        return canvas;
    }

    /**
     * Draw a rectangle on a given GraphicsContext and fill it depending on the given cell value.
     *
     * @param graphicContext The GraphicsContext to draw on.
     * @param rec The rectangle to draw.
     * @param value The represented value.
     */
    private static void drawRectangleForColor(GraphicsContext graphicContext, Rectangle rec, CellValue value) {
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

    /**
     * Get the value of a cell from a matrix, described by its row and column position.
     *
     * @param row The row of the cell.
     * @param column The column of the cell.
     * @param matrix The matrix containing the cell.
     * @return The value of the cell.
     */
    private static CellValue getCellValue(int row, int column, Matrix matrix) {
        int nextColumnIndex = matrix.getNextUnsetColumnIndex();
        if (column >= nextColumnIndex && nextColumnIndex != -1)
            return CellValue.Unset;

        boolean value = matrix.getColumns()[column].get(row);
        return value ? CellValue.Positive : CellValue.Negative;
    }

    /**
     * Get the value of a cell from a column, described by its rowIndex.
     *
     * @param rowIndex The row index of the cell.
     * @param column The column to extract the value from.
     * @return The value of the cell.
     */
    private static CellValue getCellValue(Integer rowIndex, BitSet column) {
        boolean value = column.get(rowIndex);
        if (value) return CellValue.Positive;
        return CellValue.Negative;
    }

    /**
     * Update the content of a tab canvas.
     *
     * @param content The existing content to update.
     * @param columnIndex The index of the column to update.
     * @param column The column value to set.
     * @return The update canvas.
     */
    public static Canvas updateTabCanvasContent(Node content, int columnIndex, BitSet column) {
        Canvas canvas = (Canvas) content;
        GraphicsContext graphicContext = canvas.getGraphicsContext2D();
        int dimension = column.length();
        for (int row = 0; row < dimension; row++) {
            Rectangle rec = new Rectangle();

            rec.widthProperty().bind(canvas.widthProperty().divide(dimension));
            rec.heightProperty().bind(canvas.heightProperty().divide(dimension));
            rec.xProperty().bind(rec.widthProperty().multiply(columnIndex));
            rec.yProperty().bind(rec.heightProperty().multiply(row));


            CellValue value = getCellValue(row, column);
            drawRectangleForColor(graphicContext, rec, value);
        }
        return canvas;
    }

    /**
     * Set the styling of tab, which contains a result.
     *
     * @param tab The tab to set the style to.
     */
    public static void setResultTabStyle(Tab tab) {
        tab.setStyle(UIConfiguration.instance.ResultTabStyle);
    }
}
