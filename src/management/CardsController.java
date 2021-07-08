package management;

import javafx.animation.AnimationTimer;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import operation.AutoSaveThread;
import phrases.Phrase;
import settings.Settings;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class CardsController implements Initializable, Settings{
    @FXML
    Label questionLabel;
    @FXML
    TextField answerTextField;
    @FXML
    Button addButton;
    @FXML
    Button slideButton;
    @FXML
    Pane menuPane;
    @FXML
    AnchorPane rootPane;

    private TranslateTransition menuTranslation;
    private TranslateTransition slideButtonTranslation;

    private long lastTime;
    private static CardsManager cardsManager;
    private AnimationTimer timer;
    private static AutoSaveThread autoSaveThread;
    private Phrase phrase;
    private boolean menuShown;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cardsManager = Controller.getCardsManager();
        autoSaveThread = new AutoSaveThread(AUTO_SAVING_INTERVAL, cardsManager);
        timer = new AnimationTimer() {
            @Override
            public void handle(long time) {
                if(time - lastTime >= CHECKING_INTERVAL){
                    if(checkAnswer(phrase.getEngWord(), answerTextField.getText()) == Answer.CORRECT){
                        phrase.getHistory().setStatistic(phrase.getNmbOfCorrectAnswer() + 1, phrase.getNmbOfAnswer() + 1);
                        cardsManager.dictionaryStatus();
                        cardsManager.actualizeRatio(Answer.CORRECT);
                        phrase = cardsManager.getNextPhrase();
                        questionLabel.setText(phrase.getTranslationAsOneString());
                        answerTextField.clear();
                        System.out.println("[INFO]Poprawna odpowiedź. Współczynnik: " + cardsManager.getActualRatio());
                    }
                    lastTime = time;
                }
            }
        };
        menuTranslation = new TranslateTransition(Duration.millis(500), menuPane);
        slideButtonTranslation = new TranslateTransition(Duration.millis(500), slideButton);
        slideButtonTranslation.setFromX(0);
        slideButtonTranslation.setToX(80);
        menuTranslation.setFromX(0);
        menuTranslation.setToX(80);
        startAction();
    }

    private Answer checkAnswer(String correct, String answer){
        if(correct.equalsIgnoreCase(answer)){
            return Answer.CORRECT;
        }
        return Answer.INCORRECT;
    }

    private void startAction(){
        cardsManager.dictionariesStatus();
        phrase = cardsManager.getNextPhrase();
        questionLabel.setText(phrase.getTranslationAsOneString());
        autoSaveThread.start();
        timer.start();
    }

    public static AutoSaveThread getThread(){
        return autoSaveThread;
    }

    @FXML
    private void slideButtonAction(){
        if(menuShown){
            menuTranslation.setRate(-1);
            menuTranslation.play();
            slideButtonTranslation.setRate(-1);
            slideButtonTranslation.play();
            menuShown = false;
        }
        else {
            menuTranslation.setRate(1);
            menuTranslation.play();
            slideButtonTranslation.setRate(1);
            slideButtonTranslation.play();
            menuShown = true;
        }
    }

    @FXML
    private void addButtonAction() throws IOException {
        AnchorPane pane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("../source/fxml/addPhrase.fxml")));
        rootPane.getChildren().setAll(pane);
    }
}
