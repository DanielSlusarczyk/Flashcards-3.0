package management;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import operation.AutoSaveThread;
import operation.FileReader;
import phrases.Phrase;
import settings.Settings;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class CardsController implements Initializable, Settings {
    //Static:
    private static CardsManager cardsManager;
    private static AutoSaveThread autoSaveThread;
    //FXML:
    @FXML
    Pane menuPane, answerPane;
    @FXML
    AnchorPane rootPane;
    @FXML
    Label questionLabel, answerLabel, typoLabel;
    @FXML
    TextField answerTextField;
    @FXML
    Button addButton, slideButton, showButton, typoButton;
    @FXML
    Line line1, line2, line3, line4, line5, line6;
    //Basic:
    private boolean menuShown, answerButtonClicked, typoHintShown;
    private long startAnswering, endAnswering, lastTime, lastTimeOfInit, timerTime, startPauseTime;
    //Own:
    private AnimationTimer timer;
    private Phrase phrase;
    //Transitions:
    private TranslateTransition menuTranslation;
    private TranslateTransition slideButtonTranslation;
    private FadeTransition fadeAnswerLabel;

    public static AutoSaveThread getThread() {
        return autoSaveThread;
    }

    public static void startThread() {
        autoSaveThread = new AutoSaveThread(AUTO_SAVING_INTERVAL, cardsManager);
        autoSaveThread.start();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cardsManager = Controller.getCardsManager();
        autoSaveThread = new AutoSaveThread(AUTO_SAVING_INTERVAL, cardsManager);
        setTimer();
        setTransitions();
        startAction();
    }

    private void startAction() {
        autoSaveThread.start();
        timer.start();
    }

    private Answer checkAnswer(String correct, String answer) {
        int distance = levenshteinDistance(answer, correct);
        double percent = (double) (correct.length() - distance) / correct.length();
        adjustBackground(percent * 100);
        if (distance == 0) {
            return Answer.CORRECT;
        } else if (distance == 1) {
            return Answer.TYPO;
        }
        return Answer.INCORRECT;
    }

    private void setTimer() {
        timer = new AnimationTimer() {
            public void init(long time) {
                //First Phrase
                phrase = cardsManager.getNextPhrase();
                if (phrase == null) return;
                questionLabel.setText(phrase.getTranslationAsOneString().replace("|", ", "));
                startAnswering = time;
            }

            @Override
            public void handle(long time) {
                timerTime = time;
                //Auto checking
                if (time - lastTimeOfInit >= INIT_INTERVAL && phrase == null) {
                    init(time);
                    lastTimeOfInit = time;
                }
                if (time - lastTime >= CHECKING_INTERVAL && phrase != null) {
                    Answer result = checkAnswer(phrase.getEngWord(), answerTextField.getText().trim().toLowerCase());
                    if (result == Answer.INCORRECT && answerButtonClicked) {
                        endAnswering = time;
                        incorrectAnswerAction();
                        startAnswering = time;
                    } else if (result == Answer.TYPO) {
                        if (SettingsController.tips) {
                            typoButton.setVisible(true);
                        }
                        if (answerButtonClicked) {
                            endAnswering = time;
                            incorrectAnswerAction();
                            startAnswering = time;
                        }
                    } else if (result == Answer.CORRECT) {
                        endAnswering = time;
                        correctAnswerAction();
                        startAnswering = time;
                    } else if (result == Answer.INCORRECT) {
                        typoButton.setVisible(false);
                    }
                    lastTime = time;
                }
            }
        };
    }

    private void correctAnswerAction() {
        //Actualize stats
        phrase.setStatistic(phrase.getNmbOfCorrectAnswer() + 1, phrase.getNmbOfAnswer() + 1);
        if (typoHintShown) {
            cardsManager.actualizeRatio(Answer.TYPO);
            typoHintShown = false;
        } else {
            cardsManager.actualizeRatio(Answer.CORRECT);
        }
        if (!showButton.getText().equals("Continue")) {
            FileReader.writeCardsStats(phrase, System.currentTimeMillis(), answeringTime());
            System.out.println("[INFO]Answering time: " + answeringTime());
        } else {
            showButton.setText("Show");
        }
        //Prepare board
        typoButton.setVisible(false);
        //Get new phrase
        phrase = cardsManager.getNextPhrase();
        questionLabel.setText(phrase.getTranslationAsOneString().replace("|", ", "));
        answerTextField.clear();
        answerLabel.setText("");
    }

    private void incorrectAnswerAction() {
        //Actualize stats
        phrase.setStatistic(phrase.getNmbOfCorrectAnswer(), phrase.getNmbOfAnswer() + 1);
        cardsManager.actualizeRatio(Answer.INCORRECT);
        FileReader.writeCardsStats(phrase, System.currentTimeMillis(), answeringTime());
        System.out.println("[INFO]Answering time: " + answeringTime());
        //Prepare board
        typoButton.setVisible(false);
        //Show answer and wait
        fadeAnswerLabel.play();
        answerLabel.setText(phrase.getEngWord().replace("|", ", "));
        answerButtonClicked = false;
    }

    private void setTransitions() {
        menuTranslation = new TranslateTransition(Duration.millis(500), menuPane);
        slideButtonTranslation = new TranslateTransition(Duration.millis(500), slideButton);
        fadeAnswerLabel = new FadeTransition(Duration.millis(1000), answerLabel);
        slideButtonTranslation.setFromX(0);
        slideButtonTranslation.setToX(80);
        menuTranslation.setFromX(0);
        menuTranslation.setToX(80);
        fadeAnswerLabel.setFromValue(0);
        fadeAnswerLabel.setToValue(1);
        fadeAnswerLabel.setRate(1);
    }

    private void adjustBackground(double percent) {
        line6.setVisible(percent > 84);
        line5.setVisible(percent > 68);
        line4.setVisible(percent > 52);
        line3.setVisible(percent > 36);
        line2.setVisible(percent > 20);
        line1.setVisible(percent > 1);
    }

    private int levenshteinDistance(CharSequence lhs, CharSequence rhs) {
        int len0 = lhs.length() + 1;
        int len1 = rhs.length() + 1;
        int[] cost = new int[len0];
        int[] new_cost = new int[len0];
        for (int i = 0; i < len0; i++) cost[i] = i;
        for (int j = 1; j < len1; j++) {
            new_cost[0] = j;
            for (int i = 1; i < len0; i++) {
                int match = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;
                int cost_replace = cost[i - 1] + match;
                int cost_insert = cost[i] + 1;
                int cost_delete = new_cost[i - 1] + 1;
                new_cost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
            }
            int[] swap = cost;
            cost = new_cost;
            new_cost = swap;
        }
        return cost[len0 - 1];
    }

    private double answeringTime() {
        return (double) (endAnswering - startAnswering) / 1_000_000_000;
    }

    private void timeCorrection(long start, long end) {
        startAnswering = startAnswering + (end - start);
    }


    @FXML
    private void showButtonAction() {
        if (showButton.getText().equals("Show")) {
            answerButtonClicked = true;
            showButton.setText("Continue");
        } else if (showButton.getText().equals("Continue")) {
            //Get new phrase
            if(phrase == null) return;
            phrase = cardsManager.getNextPhrase();
            questionLabel.setText(phrase.getTranslationAsOneString().replace("|", ", "));
            startAnswering = timerTime;
            answerTextField.clear();
            answerLabel.setText("");
            showButton.setText("Show");
        }
    }

    @FXML
    private void slideButtonAction() {
        if (menuShown) {
            //Hide menu
            menuTranslation.setRate(-1);
            menuTranslation.play();
            slideButtonTranslation.setRate(-1);
            slideButtonTranslation.play();
            answerPane.setDisable(false);
            timeCorrection(startPauseTime, timerTime);
            menuShown = false;
        } else {
            //Show menu
            menuTranslation.setRate(1);
            menuTranslation.play();
            slideButtonTranslation.setRate(1);
            slideButtonTranslation.play();
            answerPane.setDisable(true);
            startPauseTime = timerTime;
            menuShown = true;
        }
    }

    @FXML
    private void addButtonAction() throws IOException {
        AnchorPane pane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("../source/fxml/addPhrase.fxml")));
        rootPane.getChildren().setAll(pane);
    }

    @FXML
    private void statsButtonAction() throws IOException {
        AnchorPane pane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("../source/fxml/stats.fxml")));
        rootPane.getChildren().setAll(pane);
    }

    @FXML
    private void settingsButtonAction() throws IOException {
        AnchorPane pane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("../source/fxml/settings.fxml")));
        rootPane.getChildren().setAll(pane);
    }

    @FXML
    private void showHint() {
        typoLabel.setVisible(true);
        typoLabel.setText(answerTextField.getText() + " -> " + phrase.getEngWord());
        typoHintShown = true;
    }

    @FXML
    private void hideHint() {
        typoLabel.setVisible(false);
    }

    @FXML
    private void exitButtonAction() throws IOException {
        SettingsController.autoSave = false;
        AnchorPane pane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("../source/fxml/menu.fxml")));
        rootPane.getChildren().setAll(pane);
    }
}
