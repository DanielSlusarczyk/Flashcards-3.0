package controllers;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import management.CardsManager;
import operation.AutoSaveThread;
import operation.FileReader;
import phrases.Phrase;
import settings.Settings;

import java.io.IOException;
import java.net.URL;
import java.util.*;

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
    @FXML
    ImageView newTag;
    //Basic:
    private boolean menuShown, answerButtonClicked, typoHintShown;
    private long startAnswering, endAnswering, lastTime, lastTimeOfInit, timerTime, startPauseTime;
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
                setQuestion(phrase);
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
                    if (result == Answer.INCORRECT) {
                        incorrectAnswerAction(time);
                    } else if (result == Answer.TYPO) {
                        typoAnswerAction(time);
                    } else if (result == Answer.CORRECT) {
                        correctAnswerAction(time);
                    }
                    setTag();
                    lastTime = time;
                }
            }
        };
    }

    private void correctAnswerAction(long time) {
        //Stop time
        endAnswering = time;
        //Actualize stats
        if (showButton.getText().equals("Continue")) {
            //Answer is shown
            showButton.setText("Show");
        } else if (typoHintShown) {
            //Typo hint was shown
            phrase.setStatistic(phrase.getNmbOfCorrectAnswer() + 1, phrase.getNmbOfAnswer() + 1);
            cardsManager.actualizeRatio(Answer.TYPO);
            typoHintShown = false;
            FileReader.writeCardsStats(phrase, System.currentTimeMillis(), answeringTime(), Answer.TYPO);
            System.out.println("[INFO]Typo: Answering time: " + answeringTime());
        } else {
            //Correct answer
            phrase.setStatistic(phrase.getNmbOfCorrectAnswer() + 1, phrase.getNmbOfAnswer() + 1);
            cardsManager.actualizeRatio(Answer.CORRECT);
            FileReader.writeCardsStats(phrase, System.currentTimeMillis(), answeringTime(), Answer.CORRECT);
            System.out.println("[INFO]Correct: Answering time: " + answeringTime());
        }
        //Prepare board
        typoButton.setVisible(false);
        //Get new phrase
        phrase = cardsManager.getNextPhrase();
        setQuestion(phrase);
        answerTextField.clear();
        answerLabel.setText("");
        //Start time
        startAnswering = time;
    }

    private void typoAnswerAction(long time) {
        if (SettingsController.tips) {
            typoButton.setVisible(true);
        }
        if (answerButtonClicked) {
            incorrectAnswerAction(time);
        }
    }

    private void incorrectAnswerAction(long time) {
        if (answerButtonClicked) {
            //Stop time
            endAnswering = time;
            //Actualize stats
            phrase.setStatistic(phrase.getNmbOfCorrectAnswer(), phrase.getNmbOfAnswer() + 1);
            cardsManager.actualizeRatio(Answer.INCORRECT);
            FileReader.writeCardsStats(phrase, System.currentTimeMillis(), answeringTime(), Answer.INCORRECT);
            System.out.println("[INFO]Incorrect: Answering time: " + answeringTime());
            //Prepare board
            typoButton.setVisible(false);
            //Show answer and wait
            fadeAnswerLabel.play();
            setQuestion(phrase);
            answerButtonClicked = false;
            //Start time
            startAnswering = time;
        }
        typoButton.setVisible(false);
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
        int[] ranges = {84, 68, 52, 36, 20, 1};
        line6.setVisible(percent > ranges[0]);
        line5.setVisible(percent > ranges[1]);
        line4.setVisible(percent > ranges[2]);
        line3.setVisible(percent > ranges[3]);
        line2.setVisible(percent > ranges[4]);
        line1.setVisible(percent > ranges[5]);
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

    private void setTag() {
        newTag.setVisible(phrase.getNmbOfAnswer() == 0);
    }

    private void setQuestion(Phrase phrase){
        List<String> translation = new ArrayList<>(phrase.getTranslation());
        Collections.shuffle(translation);
        questionLabel.setText(getTranslationAsOneString(translation));
    }

    public String getTranslationAsOneString(List<String> list) {
        String result = "";
        for (String x : list) {
            result += x;
            result += ", ";
        }
        return result.substring(0, result.lastIndexOf(","));
    }

    @FXML
    private void showButtonAction() {
        if (showButton.getText().equals("Show")) {
            answerButtonClicked = true;
            showButton.setText("Continue");
        } else if (showButton.getText().equals("Continue")) {
            //Get new phrase
            if (phrase == null) return;
            phrase = cardsManager.getNextPhrase();
            setQuestion(phrase);
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
        AnchorPane pane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/source/fxml/addPhrase.fxml")));
        rootPane.getChildren().setAll(pane);
    }

    @FXML
    private void statsButtonAction() throws IOException {
        AnchorPane pane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/source/fxml/stats.fxml")));
        rootPane.getChildren().setAll(pane);
    }

    @FXML
    private void settingsButtonAction() throws IOException {
        AnchorPane pane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/source/fxml/settings.fxml")));
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
        AnchorPane pane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/source/fxml/menu.fxml")));
        rootPane.getChildren().setAll(pane);
    }
}
