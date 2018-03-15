package hadamardui;

import javafx.beans.property.ReadOnlyDoubleProperty;

public enum UIConfiguration {
    instance;

    public static ReadOnlyDoubleProperty tabPaneWidthProperty;
    public static ReadOnlyDoubleProperty tabPaneHeightProperty;
    public final static int MaxDimension = 668;
}
