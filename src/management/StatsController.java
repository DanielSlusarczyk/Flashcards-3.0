package management;

import comparators.AlphabeticComparator;
import comparators.RatioComparator;
import comparators.ReverseAlphabeticComparator;
import comparators.ReverseRatioComparator;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import phrases.Dictionary;
import phrases.Phrase;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class StatsController implements Initializable {
    public static Phrase editPhrase;
    public static Phrase detailedPhrase;
    private static CardsManager cardsManager;
    @FXML
    ScrollPane AZScrollPane, ZAScrollPane, ratioScrollPane, reverseRatioScrollPane;
    @FXML
    TabPane tabPane;
    @FXML
    AnchorPane detailsAnchorPane, rootPane, sessionInfoPane;
    @FXML
    Button returnButton, editButton;
    @FXML
    Label ratioInfoLabel, nmbInfoLabel;
    private Dictionary AZDictionary, ZADictionary, ratioDictionary, reverseRatioDictionary;
    private TranslateTransition statsTranslateTransition, chartTranslateTransition;
    private boolean statsShown;
    private List<Button> detailButtons;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cardsManager = Controller.getCardsManager();
        AZDictionary = new Dictionary("AZ", new AlphabeticComparator(), cardsManager.getAllWords());
        ZADictionary = new Dictionary("ZA", new ReverseAlphabeticComparator(), cardsManager.getAllWords());
        ratioDictionary = new Dictionary("Ratio", new RatioComparator(), cardsManager.getAllWords());
        reverseRatioDictionary = new Dictionary("ReverseRatio", new ReverseRatioComparator(), cardsManager.getAllWords());
        statsTranslateTransition = new TranslateTransition(Duration.millis(500), tabPane);
        chartTranslateTransition = new TranslateTransition(Duration.millis(500), detailsAnchorPane);
        detailButtons = new ArrayList<>();
        detailsAnchorPane.setLayoutY(-350);
        statsTranslateTransition.setFromY(0);
        statsTranslateTransition.setToY(250);
        chartTranslateTransition.setFromY(0);
        chartTranslateTransition.setToY(350);
        nmbInfoLabel.setText("Flashcard: " + cardsManager.getNmb());
        ratioInfoLabel.setText("Ratio: " + (double) Math.round(cardsManager.getActualRatio() * 100) / 100);
        editButton.setVisible(false);
        initAZ();
        initZA();
        initRatio();
        initReverseRatio();
    }

    private void initAZ() {
        setContent(AZDictionary.iterator(), AZScrollPane);
    }

    private void initZA() {
        setContent(ZADictionary.iterator(), ZAScrollPane);
    }

    private void initRatio() {
        setContent(ratioDictionary.iterator(), ratioScrollPane);
    }

    private void initReverseRatio() {
        setContent(reverseRatioDictionary.iterator(), reverseRatioScrollPane);
    }

    private void setContent(Iterator<Phrase> phraseIterator, ScrollPane scrollPane) {
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.gridLinesVisibleProperty();
        gridPane.getColumnConstraints().add(new ColumnConstraints(30));
        gridPane.getColumnConstraints().add(new ColumnConstraints(200));
        gridPane.getColumnConstraints().add(new ColumnConstraints(310));
        gridPane.getColumnConstraints().add(new ColumnConstraints(40));
        setTitleLabels(gridPane);
        int tmp = 1;
        while (phraseIterator.hasNext()) {
            Phrase phrase = phraseIterator.next();
            setPhraseLabels(phrase, tmp, gridPane);
            Button button = new Button("Detail");
            button.setId("detailButton");
            button.getStylesheets().add("source/css/buttons.css");
            button.cursorProperty().setValue(Cursor.HAND);
            button.setOnAction(actionEvent -> detailButtonAction(button, phrase, scrollPane));
            detailButtons.add(button);
            gridPane.add(button, 4, tmp);
            tmp++;
        }
        scrollPane.setContent(gridPane);
    }

    private void setTitleLabels(GridPane gridPane) {
        Label engDesc = new Label("English:");
        engDesc.setId("tabPaneLabel");
        engDesc.getStylesheets().add("source/css/labels.css");
        Label translationDesc = new Label("Translation:");
        translationDesc.setId("tabPaneLabel");
        translationDesc.getStylesheets().add("source/css/labels.css");
        Label ratioDesc = new Label("Ratio:");
        ratioDesc.setId("tabPaneLabel");
        ratioDesc.getStylesheets().add("source/css/labels.css");
        gridPane.add(engDesc, 1, 0);
        gridPane.add(translationDesc, 2, 0);
        gridPane.add(ratioDesc, 3, 0);
    }

    private void setPhraseLabels(Phrase phrase, int tmp, GridPane gridPane) {
        Label number = new Label(tmp + ".");
        number.setId("tabPaneLabel");
        number.getStylesheets().add("source/css/labels.css");
        Label engLabel = new Label(phrase.getEngWord());
        engLabel.setId("tabPaneLabel");
        engLabel.getStylesheets().add("source/css/labels.css");
        Label translationLabel = new Label(phrase.getTranslationAsOneString().replace("|", ", "));
        translationLabel.setId("tabPaneLabel");
        translationLabel.getStylesheets().add("source/css/labels.css");
        Label ratioLabel = new Label(String.valueOf((double) (Math.round(phrase.getRatio() * 100)) / 100));
        ratioLabel.setId("tabPaneLabel");
        ratioLabel.getStylesheets().add("source/css/labels.css");
        gridPane.add(number, 0, tmp);
        gridPane.add(engLabel, 1, tmp);
        gridPane.add(translationLabel, 2, tmp);
        gridPane.add(ratioLabel, 3, tmp);
    }

    private void detailButtonAction(Button button, Phrase phrase, ScrollPane scrollPane) {
        if (statsShown) {
            boolean reload = false;
            for (Button b : detailButtons) {
                if (!b.equals(button) && b.getText().equals("Hide")) {
                    b.setText("Detail");
                    reload = true;
                }
            }
            if (!reload) {
                scrollPane.setPrefHeight(350);
                statsTranslateTransition.setRate(-1);
                statsTranslateTransition.setOnFinished(actionEvent -> {
                });
                editButton.setVisible(false);
                statsTranslateTransition.play();
                chartTranslateTransition.setRate(-1);
                chartTranslateTransition.setOnFinished(actionEvent1 -> {
                    detailsAnchorPane.getChildren().clear();
                    sessionInfoPane.setVisible(true);
                });
                chartTranslateTransition.play();
                button.setText("Detail");
                statsShown = false;
            } else {
                button.setText("Hide");
                detailedPhrase = phrase;
                AnchorPane pane;
                try {
                    pane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("../source/fxml/details.fxml")));
                    detailsAnchorPane.getChildren().setAll(pane);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            detailedPhrase = phrase;
            AnchorPane pane;
            try {
                pane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("../source/fxml/details.fxml")));
                detailsAnchorPane.getChildren().setAll(pane);
            } catch (IOException e) {
                e.printStackTrace();
            }
            statsTranslateTransition.setRate(1);
            statsTranslateTransition.play();
            statsTranslateTransition.setOnFinished(actionEvent -> {
                scrollPane.setPrefHeight(100);
                editButton.setVisible(true);
            });
            chartTranslateTransition.setRate(1);
            chartTranslateTransition.play();
            chartTranslateTransition.setOnFinished(actionEvent1 -> {
            });
            button.setText("Hide");
            sessionInfoPane.setVisible(false);
            statsShown = true;
        }
    }

    @FXML
    private void returnButtonAction() {
        try {
            AnchorPane cardsPane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("../source/fxml/cards.fxml")));
            rootPane.getChildren().setAll(cardsPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editButtonAction(){
        editPhrase = detailedPhrase;
        try {
            AnchorPane cardsPane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("../source/fxml/editPhrase.fxml")));
            rootPane.getChildren().setAll(cardsPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
