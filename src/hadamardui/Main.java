package hadamardui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("ui.fxml").openStream());
        primaryStage.setTitle("Hadamard");
        primaryStage.setScene(new Scene(root));
        HadamardController controller = fxmlLoader.getController();
        primaryStage.setOnCloseRequest(controller.windowIsClosedEventHandler);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
