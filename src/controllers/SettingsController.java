package controllers;

import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import settings.Settings;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class SettingsController implements Initializable, Settings {
    //Static:
    public static boolean autoSave = true;
    public static boolean newFirst = false;
    public static boolean tips = true;
    public static int frequency = Settings.AMOUNT_OF_CARDS_BETWEEN_RANDOM;
    //FXML:
    @FXML
    AnchorPane rootPane;
    @FXML
    Slider slider;
    //Graphics:
    ToggleSwitch newFirstTSwitch, tipsTSwitch, autoSaveTSwitch;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setTButtons();
        slider.setValue(frequency);
    }

    private void setTButtons() {
        autoSaveTSwitch = new ToggleSwitch(50, 25);
        autoSaveTSwitch.setTranslateX(600);
        autoSaveTSwitch.setTranslateY(90);
        autoSaveTSwitch.setCursor(Cursor.HAND);

        tipsTSwitch = new ToggleSwitch(50, 25);
        tipsTSwitch.setTranslateX(600);
        tipsTSwitch.setTranslateY(170);
        tipsTSwitch.setCursor(Cursor.HAND);

        newFirstTSwitch = new ToggleSwitch(50, 25);
        newFirstTSwitch.setTranslateX(600);
        newFirstTSwitch.setTranslateY(250);
        newFirstTSwitch.setCursor(Cursor.HAND);

        autoSaveTSwitch.switchedOn.set(autoSave);
        newFirstTSwitch.switchedOn.set(newFirst);
        tipsTSwitch.switchedOn.set(tips);

        rootPane.getChildren().addAll(autoSaveTSwitch, tipsTSwitch, newFirstTSwitch);
    }

    @FXML
    private void defaultButtonAction() {
        autoSave = true;
        newFirst = false;
        tips = true;
        autoSaveTSwitch.switchedOn.set(true);
        newFirstTSwitch.switchedOn.set(false);
        tipsTSwitch.switchedOn.set(true);
        frequency = AMOUNT_OF_CARDS_BETWEEN_RANDOM;
        slider.setValue(AMOUNT_OF_CARDS_BETWEEN_RANDOM);
    }

    @FXML
    private void applyButtonAction() {
        autoSave = autoSaveTSwitch.switchedOn.get();
        if (!autoSave && WindowController.synchroOffIcon != null) {
            WindowController.synchroIcon.setVisible(false);
            WindowController.synchroOffIcon.setVisible(true);
        }
        if (autoSave) {
            CardsController.startThread();
            if (WindowController.synchroOffIcon != null) {
                WindowController.synchroOffIcon.setVisible(false);
            }
        }
        newFirst = newFirstTSwitch.switchedOn.get();
        tips = tipsTSwitch.switchedOn.get();
        frequency = (int) slider.getValue();
        returnButtonAction();
        WindowController.inform("Saved successfully");
    }

    @FXML
    private void returnButtonAction() {
        try {
            AnchorPane cardsPane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/source/fxml/cards.fxml")));
            rootPane.getChildren().setAll(cardsPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ToggleSwitch extends Parent {
        //Switch ON-OFF
        private final BooleanProperty switchedOn = new SimpleBooleanProperty(false);
        private final TranslateTransition translateTransition = new TranslateTransition(Duration.millis(250));
        private final FillTransition fillTransition = new FillTransition(Duration.millis(250));
        private final ParallelTransition animation = new ParallelTransition(translateTransition, fillTransition);

        public ToggleSwitch(double width, double height) {
            Rectangle background = new Rectangle(width, height);
            background.setArcWidth(height);
            background.setArcHeight(height);
            background.setFill(Color.WHITE);
            background.setStroke(Color.rgb(68, 67, 67));

            Circle trigger = new Circle(height / 2);
            trigger.setCenterX(height / 2);
            trigger.setCenterY(height / 2);
            trigger.setFill(Color.WHITE);
            trigger.setStroke(Color.rgb(68, 67, 67));

            translateTransition.setNode(trigger);
            fillTransition.setShape(background);
            getChildren().addAll(background, trigger);

            switchedOn.addListener((obs, oldState, newState) -> {
                boolean isOn = newState;
                translateTransition.setToX(isOn ? width - width / 2 : 0);
                fillTransition.setFromValue(isOn ? Color.rgb(68, 67, 67) : Color.rgb(255, 132, 116));
                fillTransition.setToValue(isOn ? Color.rgb(255, 132, 116) : Color.rgb(68, 67, 67));
                animation.play();
            });
            setOnMouseClicked(mouseEvent -> switchedOn.set(!switchedOn.get()));
        }
    }
}
