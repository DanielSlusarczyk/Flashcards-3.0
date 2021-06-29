package phrases;

import java.util.*;

public class Dictionary implements Iterable {
    private final Set<Phrase> flashcards;

    public Dictionary() {
        flashcards = new TreeSet<>();
    }

    public void addNewCard(String engWord, List<String> translation) {
        Phrase phrase = new Phrase(engWord, translation);
        flashcards.add(phrase);
    }

    public void addCard(String engWord, List<String> translation, int nmbOfCorrectAnswer, int nbmOfAnswer) {
        Phrase phrase = new Phrase(engWord, translation, nmbOfCorrectAnswer, nbmOfAnswer);
        flashcards.add(phrase);
    }

    @Override
    public Iterator iterator() {
        return flashcards.iterator();
    }
}
