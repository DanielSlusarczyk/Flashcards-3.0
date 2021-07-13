package management;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class WindowController implements Initializable {
    //Static:
    public static ImageView synchroIcon, synchroOffIcon;
    private static double xOffset = 0;
    private static double yOffset = 0;
    //FXML:
    @FXML
    AnchorPane rootPane, windowBar;
    @FXML
    ImageView synchro, synchroOff;
    //Graphics:
    private Stage primaryStage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AnchorPane pane;
        try {
            pane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("../source/fxml/menu.fxml")));
            rootPane.getChildren().setAll(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
        synchro.setVisible(false);
        synchroOff.setVisible(false);
        synchroIcon = synchro;
        synchroOffIcon = synchroOff;
        enableDragWindow();
    }

    private void enableDragWindow() {
        windowBar.setOnMousePressed(event -> {
            primaryStage = (Stage) rootPane.getScene().getWindow();
            xOffset = primaryStage.getX() - event.getScreenX();
            yOffset = primaryStage.getY() - event.getScreenY();
        });
        windowBar.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() + xOffset);
            primaryStage.setY(event.getScreenY() + yOffset);
        });
    }

    @FXML
    private void exitButtonAction() {
        System.out.println("[INFO]Window close");
        SettingsController.autoSave = false;
        if (CardsController.getThread() != null)
            CardsController.getThread().interrupt();
        System.exit(0);
    }

    @FXML
    private void minimizeButtonAction() {
        primaryStage = (Stage) rootPane.getScene().getWindow();
        primaryStage.setIconified(true);
    }

}
