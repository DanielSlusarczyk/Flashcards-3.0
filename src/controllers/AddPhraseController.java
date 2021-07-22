package controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import management.CardsManager;
import operation.FileReader;
import settings.Settings;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class AddPhraseController implements Initializable, Settings {
    //FXML
    @FXML
    AnchorPane rootPane;
    @FXML
    TextField englishTextField, polishTextField;
    @FXML
    HBox translationHBox;
    @FXML
    Button addTranslationButton, VButton, PVButton, AButton, AdButton, OButton, NButton;
    @FXML
    Label translationLabel, englishLabel, groupLabel;
    @FXML
    ImageView cancelImage;
    private List<Button> buttonsList;
    private String group = "";
    private CardsManager cardsManager;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cardsManager = Controller.getCardsManager();
        buttonsList = new ArrayList<>();
        buttonsList.add(VButton);
        buttonsList.add(PVButton);
        buttonsList.add(AButton);
        buttonsList.add(OButton);
        buttonsList.add(NButton);
        buttonsList.add(AdButton);

        for (Button button : buttonsList) {
            button.setOnAction(actionEvent -> {
                for (Button b : buttonsList) {
                    b.setId("categoriesAddButton");
                }
                button.setId("categoriesAddedButton");
                switch (button.getText()) {
                    case "Phrasal Verb" -> group = "PV";
                    case "Verb" -> group = "V";
                    case "Noun" -> group = "N";
                    case "Adjective" -> group = "AD";
                    case "Adverb" -> group = "A";
                    case "Others" -> group = "O";
                }
            });
        }
    }

    @FXML
    private void addTranslationButtonAction() {
        //Add new translation for flashcard
        if (polishTextField.getText().equals("")) return;
        //Graphics:
        ImageView cancelImageView = new ImageView(cancelImage.getImage());
        String translation = polishTextField.getText();
        polishTextField.clear();
        Button translationButton = new Button(translation, cancelImageView);
        translationButton.setId("translationButton");
        translationButton.getStylesheets().add("source/css/buttons.css");
        translationButton.setPrefHeight(30);
        translationButton.setCursor(Cursor.HAND);
        translationButton.setOnAction(actionEvent -> translationHBox.getChildren().remove(translationButton));
        translationHBox.getChildren().add(translationButton);
        HBox.setMargin(translationButton, new Insets(0, 2, 0, 2));
    }

    @FXML
    private void addButtonAction() {
        //Basic:
        List<String> translation = new ArrayList<>();
        ObservableList<Node> translationButtons = translationHBox.getChildren();
        for (Node node : translationButtons) {
            Button button = (Button) node;
            translation.add(button.getText());
        }
        String engWord = englishTextField.getText();
        translationLabel.setId("addPhraseLabel");
        englishLabel.setId("addPhraseLabel");
        groupLabel.setId("addPhraseLabel");
        if (!group.equals("") && !engWord.equals("") && translation.size() != 0) {
            FileReader.addToDictionary(group, engWord, translation, 0, 0);
            FileReader.writeCards(cardsManager.autoSaveIterator());
            translationHBox.getChildren().clear();
            polishTextField.clear();
            englishTextField.clear();
            buttonsList.forEach(button -> button.setId("categoriesAddButton"));
            WindowController.inform("Added successfully");
        } else {
            if (group.equals("")) {
                groupLabel.setId("addPhraseLabelIncorrect");
            }
            if (engWord.equals("")) {
                englishLabel.setId("addPhraseLabelIncorrect");
            }
            if (translation.size() == 0) {
                translationLabel.setId("addPhraseLabelIncorrect");
            }
        }
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
}
