package operation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import settings.Settings;

import java.util.Objects;

public class Main extends Application implements Settings {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("../source/fxml/window.fxml")));
        primaryStage.setTitle("FlashCards 3.0");
        primaryStage.setScene(new Scene(root, WIDTH, HEIGHT));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
