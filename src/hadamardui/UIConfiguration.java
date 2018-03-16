package hadamardui;

import javafx.beans.property.ReadOnlyDoubleProperty;

/**
 * Contains configuration values for the UI.
 */
public enum UIConfiguration {
    /**
     * Instance of this configuration.
     */
    instance;

    /**
     * The maximum supported dimension. Higher dimensions are not easy to visualize on standard screens.
     */
    public final static int maxDimension = 668;

    /**
     * The WidthProperty of the tab pane.
     */
    public static ReadOnlyDoubleProperty tabPaneWidthProperty;

    /**
     * The HeightProperty of the tab pane.
     */
    public static ReadOnlyDoubleProperty tabPaneHeightProperty;

    /**
     * The style which should be set for a tab, which contains a result.
     */
    public String resultTabStyle = "-fx-border-color:green; -fx-background-color: green;  -fx-font-weight: bold;";
}
