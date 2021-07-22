package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import management.CardsManager;

import java.net.URL;
import java.util.ResourceBundle;

public class TransitionSceneController implements Initializable {
    //FXML:
    @FXML
    Label amountLabel, categoryLabel;
    //Basic:
    CardsManager cardsManager;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cardsManager = Controller.getCardsManager();
        amountLabel.setText(String.valueOf(cardsManager.getAmount()));
        categoryLabel.setText(cardsManager.getName());
    }
}
