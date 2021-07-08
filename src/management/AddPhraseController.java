package management;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class AddPhraseController implements Initializable {
    CardsManager cardsManager;
    @FXML
    AnchorPane rootPane;
    @FXML
    TextField englishTextField;
    @FXML
    TextField polishTextField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cardsManager = Controller.getCardsManager();
    }

    public void addButtonAction(){

    }
}
