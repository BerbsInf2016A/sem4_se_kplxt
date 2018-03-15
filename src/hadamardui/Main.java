package hadamardui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("ui.fxml").openStream());
        primaryStage.setTitle("Hadamard");
        primaryStage.setScene(new Scene(root));
        HadamardController controller = fxmlLoader.getController();
        primaryStage.setOnCloseRequest(controller.windowIsClosedEventHandler);
        primaryStage.setMaximized(true);
        primaryStage.setResizable(false);

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX(primaryScreenBounds.getMinX());
        primaryStage.setY(primaryScreenBounds.getMinY());

        primaryStage.setMaxWidth(primaryScreenBounds.getWidth());
        primaryStage.setMinWidth(primaryScreenBounds.getWidth());

        primaryStage.setMaxHeight(primaryScreenBounds.getHeight());
        primaryStage.setMinHeight(primaryScreenBounds.getHeight());
        primaryStage.show();
    }
}
