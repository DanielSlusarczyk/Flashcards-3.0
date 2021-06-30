package operation;

import exceptions.UnhandledSituationException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import management.CardsManager;
import settings.Settings;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class Controller implements Initializable, Settings {
    @FXML
    Button button1;
    @FXML
    Button button2;
    @FXML
    Button button3;
    @FXML
    Button button4;
    @FXML
    Button button5;
    @FXML
    Button button6;
    @FXML
    AnchorPane rootPane;

    private ArrayList <javafx.scene.control.Button> buttons;
    private static CardsManager cardsManager;

    public static CardsManager getCardsManager(){
        return cardsManager;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            cardsManager = new CardsManager(Settings.wordPath);
        } catch (FileNotFoundException e){
            System.out.println("There is no default file!");
            System.exit(1);
            //TODO: W momencie wykrycia braku pliku program powinnien prosić o podanie ścieżki ręcznie
        }
        System.out.println("[INFO]Fiszki wczytane");
        setActionForButtons();
    }

    private void setActionForButtons(){
        buttons = new ArrayList<>();
        buttons.add(button1);
        buttons.add(button2);
        buttons.add(button3);
        buttons.add(button4);
        buttons.add(button5);
        buttons.add(button6);
        for(javafx.scene.control.Button button : buttons){
            button.setOnAction(e -> {
                try {
                    initializeByChosenSet(button.getText());
                    //Load new FXML and assign it to scene
                    AnchorPane pane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("../source/fxml/cards.fxml")));
                    rootPane.getChildren().setAll(pane);
                } catch (UnhandledSituationException | IOException exception) {
                    System.out.println(exception.toString());
                }
            });
        }
    }

    private void initializeByChosenSet(String set) throws UnhandledSituationException {
        System.out.println("[INFO]Inicjalizacja za pomocą: " + set);
        switch (set){
            case "Phrasal Verb" -> cardsManager.setMode(Mode.PHRASAL_VERB);
            case "Noun" -> cardsManager.setMode(Mode.NOUN);
            case "Verb" -> cardsManager.setMode(Mode.VERB);
            case "Adjective" -> cardsManager.setMode(Mode.ADJECTIVE);
            case "Adverb" -> cardsManager.setMode(Mode.ADVERB);
            case "All" -> cardsManager.setMode(Mode.ALL);
            default -> throw new UnhandledSituationException("Switch - Buttons");
        }
    }

}
