package operation;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import management.CardsManager;
import phrases.Phrase;
import settings.Settings;

import java.awt.event.WindowListener;
import java.net.URL;
import java.util.ResourceBundle;

public class CardsController implements Initializable, Settings{
    @FXML
    Label questionLabel;
    @FXML
    TextField answerTextField;
    @FXML
    Button startButton;
    @FXML
    AnchorPane rootPane;

    long lastTime;

    private static CardsManager cardsManager;
    AnimationTimer timer;

    private Phrase phrase;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cardsManager = Controller.getCardsManager();
        timer = new AnimationTimer() {
            @Override
            public void handle(long time) {
                if(time - lastTime >= CHECKING_INTERVAL){
                    if(checkAnswer(phrase.getEngWord(), answerTextField.getText()) == Answer.CORRECT){
                        cardsManager.addPhrase(new Phrase(phrase, phrase.getNmbOfCorrectAnswer(), phrase.getNmbOfAnswer(), phrase.getHistory()));
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
    }

    private Answer checkAnswer(String correct, String answer){
        if(correct.toLowerCase().equals(answer.toLowerCase())){
            return Answer.CORRECT;
        }
        return Answer.INCORRECT;
    }

    @FXML
    private void startButtonAction(){
        startButton.setVisible(false);
        cardsManager.dictionaryStatus();
        phrase = cardsManager.getNextPhrase();
        questionLabel.setText(phrase.getTranslationAsOneString());
        timer.start();
    }

}
