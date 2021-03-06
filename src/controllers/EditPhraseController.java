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
import phrases.Phrase;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class EditPhraseController implements Initializable {
    //Static:
    private static CardsManager cardsManager;
    //FXML:
    @FXML
    TextField polishTextField, englishTextField;
    @FXML
    HBox translationHBox;
    @FXML
    Button addTranslationButton, VButton, PVButton, AButton, AdButton, OButton, NButton;
    @FXML
    AnchorPane rootPane;
    @FXML
    Label englishLabel, translationLabel, groupLabel;
    @FXML
    ImageView cancelImage;
    //Basic:
    private String group;
    private Phrase phrase;
    private List<Button> buttonsList;
    private List<String> translation;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        phrase = StatsController.editPhrase;
        cardsManager = Controller.getCardsManager();
        buttonsList = new ArrayList<>();
        buttonsList.add(VButton);
        buttonsList.add(PVButton);
        buttonsList.add(AButton);
        buttonsList.add(OButton);
        buttonsList.add(NButton);
        buttonsList.add(AdButton);
        translation = new ArrayList<>();
        setPhraseInfo();
    }

    private void setPhraseInfo() {
        englishTextField.setText(phrase.getEngWord());
        for (String string : phrase.getTranslation()) {
            System.out.println(string);
            addTranslationAction(string);
        }
        switch (phrase.getGroup()) {
            case "V" -> VButton.setId("categoriesAddedButton");
            case "PV" -> PVButton.setId("categoriesAddedButton");
            case "N" -> NButton.setId("categoriesAddedButton");
            case "A" -> AButton.setId("categoriesAddedButton");
            case "AD" -> AdButton.setId("categoriesAddedButton");
            case "O" -> OButton.setId("categoriesAddedButton");
        }
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

    private void addTranslationAction(String text) {
        //Graphic:
        ImageView cancelImageView = new ImageView(cancelImage.getImage());
        Button translationButton = new Button(text, cancelImageView);
        translationButton.setId("translationButton");
        translationButton.getStylesheets().add("source/css/buttons.css");
        translationButton.setPrefHeight(30);
        translationButton.setCursor(Cursor.HAND);
        translationButton.setOnAction(actionEvent -> translationHBox.getChildren().remove(translationButton));
        translationHBox.getChildren().add(translationButton);
        HBox.setMargin(translationButton, new Insets(0, 2, 0, 2));
    }

    @FXML
    private void addTranslationButtonAction() {
        addTranslationAction(polishTextField.getText());
    }

    @FXML
    private void returnButtonAction() {
        try {
            AnchorPane cardsPane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/source/fxml/stats.fxml")));
            rootPane.getChildren().setAll(cardsPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void removeButtonAction() {
        Controller.getCardsManager().remove(phrase);
        WindowController.inform("Removed successfully");
        if (FileReader.removePhraseStats(phrase)) {
            System.out.println("[INFO]History is deleted");
        } else {
            System.out.println("[INFO]History is not deleted");
        }
        try {
            AnchorPane cardsPane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/source/fxml/stats.fxml")));
            rootPane.getChildren().setAll(cardsPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void saveButtonAction() {
        ObservableList<Node> translationButtons = translationHBox.getChildren();
        for (Node node : translationButtons) {
            Button button = (Button) node;
            translation.add(button.getText());
        }
        String engWord = englishTextField.getText();
        translationLabel.setId("addPhraseLabel");
        englishLabel.setId("addPhraseLabel");
        groupLabel.setId("addPhraseLabel");
        if (group == null) {
            group = phrase.getGroup();
        }
        if (!group.equals("") && !engWord.equals("") && translation.size() != 0) {
            if (!phrase.getEngWord().equals(engWord)) {
                cardsManager.editEngWord(phrase, engWord);
            }
            if (!phrase.getTranslation().containsAll(translation) || phrase.getTranslation().size() != translation.size()) {
                cardsManager.editTranslation(phrase, translation);
            }
            if (!phrase.getGroup().equals(group)) {
                cardsManager.editGroup(phrase, group);
            }
            returnButtonAction();
            WindowController.inform("Edited successfully");
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
}
