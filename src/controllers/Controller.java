package controllers;

import exceptions.UnhandledSituationException;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import management.CardsManager;
import settings.Settings;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class Controller implements Initializable, Settings {
    //Static:
    private static CardsManager cardsManager;
    //FXML:
    @FXML
    Button buttonAll, buttonPV, buttonV, buttonN, buttonAd, buttonA, buttonO;
    @FXML
    AnchorPane rootPane;
    @FXML
    Pane p1, p2, p3, p4, p5, p6;
    //Basic:
    boolean categoriesMenuIsActive = false;
    boolean categoryIsChosen = false;

    public static CardsManager getCardsManager() {
        return cardsManager;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            cardsManager = new CardsManager();
        } catch (FileNotFoundException e) {
            System.out.println("There is no default file!");
            System.exit(1);
        }
        System.out.println("[INFO]Phrases are read");
        setActionForButtons();
    }

    private void setActionForButtons() {
        ArrayList<Button> buttons = new ArrayList<>();
        buttons.add(buttonAll);
        buttons.add(buttonA);
        buttons.add(buttonAd);
        buttons.add(buttonN);
        buttons.add(buttonO);
        buttons.add(buttonPV);
        buttons.add(buttonV);
        for (javafx.scene.control.Button button : buttons) {
            button.setOnAction(e -> {
                try {
                    initializeByChosenSet(button.getText());
                    categoryIsChosen = true;
                    categoriesButtonAction();
                } catch (UnhandledSituationException exception) {
                    exception.printStackTrace();
                }
            });
        }
    }

    private void initializeByChosenSet(String set) throws UnhandledSituationException {
        System.out.println("[INFO]Initialize with: " + set);
        switch (set) {
            case "Phrasal Verb" -> cardsManager.setMode(Mode.PHRASAL_VERB);
            case "Noun" -> cardsManager.setMode(Mode.NOUN);
            case "Verb" -> cardsManager.setMode(Mode.VERB);
            case "Adjective" -> cardsManager.setMode(Mode.ADJECTIVE);
            case "Adverb" -> cardsManager.setMode(Mode.ADVERB);
            case "Play" -> cardsManager.setMode(Mode.ALL);
            case "Other" -> cardsManager.setMode(Mode.OTHERS);
            default -> throw new UnhandledSituationException("Switch - Buttons");
        }
    }

    private void changeScene() {
        FadeTransition paneFadeInRootPane = new FadeTransition(Duration.millis(1500), rootPane);
        paneFadeInRootPane.setFromValue(1);
        paneFadeInRootPane.setToValue(0);
        paneFadeInRootPane.setCycleCount(1);
        paneFadeInRootPane.play();
        paneFadeInRootPane.setOnFinished(actionEvent -> {
            AnchorPane transitionPane;
            try {
                transitionPane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/source/fxml/transitionScene.fxml")));
                rootPane.getChildren().setAll(transitionPane);
                FadeTransition paneFadeInAmountLabel = new FadeTransition(Duration.millis(1500), rootPane);
                paneFadeInAmountLabel.setFromValue(0);
                paneFadeInAmountLabel.setToValue(1);
                paneFadeInAmountLabel.setCycleCount(1);
                paneFadeInAmountLabel.play();
                FadeTransition paneFadeOutAmountLabel = new FadeTransition(Duration.millis(1500), rootPane);
                paneFadeOutAmountLabel.setFromValue(1);
                paneFadeOutAmountLabel.setToValue(0);
                paneFadeOutAmountLabel.setCycleCount(1);
                paneFadeOutAmountLabel.play();
                paneFadeOutAmountLabel.setOnFinished(event -> {
                    AnchorPane cardsPane;
                    try {
                        cardsPane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/source/fxml/cards.fxml")));
                        rootPane.getChildren().setAll(cardsPane);
                        FadeTransition paneFadeInCards = new FadeTransition(Duration.millis(1500), rootPane);
                        paneFadeInCards.setFromValue(0);
                        paneFadeInCards.setToValue(1);
                        paneFadeInCards.setCycleCount(1);
                        paneFadeInCards.play();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void categoriesButtonAction() {
        FadeTransition[] fadeInArray = new FadeTransition[6];
        Pane[] paneArray = new Pane[]{p1, p2, p3, p4, p5, p6};
        if (!categoriesMenuIsActive && !categoryIsChosen) {
            for (int i = 0; i < 6; i++) {
                fadeInArray[i] = new FadeTransition(Duration.millis(40), paneArray[i]);
                fadeInArray[i].setFromValue(0);
                fadeInArray[i].setToValue(1);
                fadeInArray[i].setCycleCount(1);
            }
            for (int i = 0; i < 5; i++) {
                int finalI = i;
                fadeInArray[i].setOnFinished(actionEvent -> {
                    paneArray[finalI + 1].setVisible(true);
                    fadeInArray[finalI + 1].play();
                });
            }
            fadeInArray[0].play();
            paneArray[0].setVisible(true);
            categoriesMenuIsActive = true;
        } else {
            for (int i = 0; i < 6; i++) {
                fadeInArray[i] = new FadeTransition(Duration.millis(20), paneArray[i]);
                fadeInArray[i].setFromValue(1);
                fadeInArray[i].setToValue(0);
                fadeInArray[i].setCycleCount(1);
            }
            for (int i = 5; i > 0; i--) {
                int finalI = i;
                fadeInArray[i].setOnFinished(actionEvent -> {
                    fadeInArray[finalI - 1].play();
                    paneArray[finalI].setVisible(false);
                });
            }
            fadeInArray[0].setOnFinished(actionEvent -> {
                if (categoryIsChosen) {
                    changeScene();
                }
            });
            fadeInArray[5].play();
            categoriesMenuIsActive = false;
        }
    }
}
