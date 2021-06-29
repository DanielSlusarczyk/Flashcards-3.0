package settings;

public interface Settings {
    //Window Properties
    double WIDTH = 600;
    double HEIGHT = 400;

    //Reading Phrases
    String wordPath = "src/source/words/cards.txt";

    enum Mode {
        PHRASAL_VERB, NOUN, ADJECTIVE, ADVERB, VERB, ALL;
    }
}
