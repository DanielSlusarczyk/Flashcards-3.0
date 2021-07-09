package settings;

public interface Settings {
    //Window Properties
    double WIDTH = 700;
    double HEIGHT = 500;

    //Reading Phrases
    String wordPath = "src/source/words/cards.txt";

    //Phrase Sequence
    int AMOUNT_OF_CARD_BETWEEN_RANDOM = 10;
    double RATIO_INCREASE_FOR_CORRECT_ANSWER = 0.03;
    double RATIO_DECREASE_FOR_INCORRECT_ANSWER = 0.03;
    double RATIO_INCREASE_FOR_TYPO = 0.01;

    //General
    long CHECKING_INTERVAL = 100_000_000;
    long INIT_INTERVAL = 1_000_000_000;
    int AUTO_SAVING_INTERVAL = 1000 * 5;

    enum Mode {
        PHRASAL_VERB, NOUN, ADJECTIVE, ADVERB, VERB, ALL, OTHERS;
    }

    enum Answer {
        CORRECT, INCORRECT, TYPO;
    }
}
