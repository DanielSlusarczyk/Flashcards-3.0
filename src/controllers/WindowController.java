package controllers;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class WindowController implements Initializable {
    //Static:
    public static ImageView synchroIcon, synchroOffIcon;
    private static Label label;
    private static TranslateTransition translateTransition;
    private static FadeTransition fadeTransition;
    private static double xOffset = 0;
    private static double yOffset = 0;
    //FXML:
    @FXML
    AnchorPane rootPane, windowBar;
    @FXML
    ImageView synchro, synchroOff;
    @FXML
    Label infoLabel;
    //Graphics:
    private Stage primaryStage;

    public static void inform(String info) {
        label.setText(info);
        if (!translateTransition.getStatus().equals(Animation.Status.RUNNING) && !fadeTransition.getStatus().equals(Animation.Status.RUNNING)) {
            translateTransition.play();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AnchorPane pane;
        try {
            pane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/source/fxml/menu.fxml")));
            rootPane.getChildren().setAll(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
        synchro.setVisible(false);
        synchroOff.setVisible(false);
        synchroIcon = synchro;
        synchroOffIcon = synchroOff;
        label = infoLabel;
        infoLabel.setTranslateY(-35);
        setTranslation();
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

    private void setTranslation() {
        fadeTransition = new FadeTransition(Duration.millis(2000), label);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        translateTransition = new TranslateTransition(Duration.millis(1000), label);
        translateTransition.setFromY(-35);
        translateTransition.setToY(0);
        translateTransition.setRate(1);
        translateTransition.setOnFinished(actionEvent -> {
            fadeTransition.play();
            fadeTransition.setOnFinished(actionEvent1 -> {
                label.setTranslateY(-35);
                label.setOpacity(1.0);
            });
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
