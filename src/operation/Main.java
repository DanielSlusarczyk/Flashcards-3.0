package operation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import management.CardsController;
import management.SettingsController;
import settings.Settings;

import java.util.Objects;

public class Main extends Application implements Settings {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("../source/fxml/menu.fxml")));
        primaryStage.setTitle("FlashCards 3.0");
        primaryStage.setScene(new Scene(root, WIDTH, HEIGHT));
        //primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();
        primaryStage.setOnCloseRequest(windowEvent -> {
            System.out.println("[INFO]Window close");
            SettingsController.autoSave = false;
            if (CardsController.getThread() != null)
                CardsController.getThread().interrupt();
        });
    }
}
