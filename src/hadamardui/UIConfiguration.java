package hadamardui;

import javafx.beans.property.ReadOnlyDoubleProperty;

public enum UIConfiguration {
    instance;

    public static ReadOnlyDoubleProperty tabPaneWidthProperty;
    public static ReadOnlyDoubleProperty tabPaneHeightProperty;

    public static double rectangleWidthPropertyAdditionalDimensionDivider = 0.8;
    public static double rectangleHeightPropertyAdditionalDimensionDivider = 2;
    public static double rectangleXPropertyAdditionalColumnMultiplier = 0.2;
    public static double rectangleYPropertyAdditionalRowMultiplier = 0.2;
}
