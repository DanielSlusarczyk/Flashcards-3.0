package management;

import operation.FileReader;
import phrases.Dictionary;
import settings.Settings;

import java.io.FileNotFoundException;

public class CardsManager implements Settings {
    public static Dictionary phrasalVerbs;
    public static Dictionary nouns;
    public static Dictionary adjectives;
    public static Dictionary adverbs;
    public static Dictionary verbs;
    public static Dictionary others;
    public static Dictionary allWords;
    private Mode mode;

    public CardsManager(String inFile) throws FileNotFoundException {
        phrasalVerbs = new Dictionary();
        nouns = new Dictionary();
        adjectives = new Dictionary();
        adverbs = new Dictionary();
        others = new Dictionary();
        allWords = new Dictionary();
        FileReader.readCards(inFile);
    }

    public void setMode(Mode mode){
        this.mode = mode;
    }
}
