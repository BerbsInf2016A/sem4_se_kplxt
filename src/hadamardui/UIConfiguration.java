package hadamardui;

import javafx.beans.property.ReadOnlyDoubleProperty;

public enum UIConfiguration {
    instance;

    public final static int MaxDimension = 668;
    public static ReadOnlyDoubleProperty tabPaneWidthProperty;
    public static ReadOnlyDoubleProperty tabPaneHeightProperty;
    public String ResultTabStyle = "-fx-border-color:green; -fx-background-color: green;  -fx-font-weight: bold;";
}
