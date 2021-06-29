package phrases;

import java.util.List;
import java.util.Objects;

public class Phrase implements Comparable<Phrase> {
    private final String engWord;
    private final List<String> translation;
    private final History history;

    public Phrase(String engWord, List<String> translation) {
        this.engWord = engWord;
        this.translation = translation;
        history = new History();
    }

    public Phrase(String engWord, List<String> translation, int nmbOfCorrectAnswer, int nbmOfAnswer) {
        this.engWord = engWord;
        this.translation = translation;
        history = new History(nmbOfCorrectAnswer, nbmOfAnswer);
    }

    public String getTranslationAsOneString() {
        StringBuilder result = new StringBuilder("| ");
        for (String x : translation) {
            result.append(x);
            result.append(" | ");
        }
        return result.toString();
    }

    public String getEngWord() {
        return engWord;
    }

    public List<String> getTranslation() {
        return translation;
    }

    @Override
    public int compareTo(Phrase o) {
        return engWord.compareTo(o.getEngWord());
    }

    @Override
    public String toString() {
        return "[Flashcard]: " + engWord + " -> " + getTranslationAsOneString();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Phrase) {
            //Translation
            List<String> shorterList = translation.size() <= ((Phrase) o).getTranslation().size() ? translation : ((Phrase) o).getTranslation();
            List<String> longerList = translation.size() < ((Phrase) o).getTranslation().size() ? ((Phrase) o).getTranslation() : translation;
            //English Word and Translation
            return longerList.containsAll(shorterList) && engWord.equals(((Phrase) o).engWord);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(engWord);
    }

    private class History {
        private final int nmbOfAnswer;
        private final int nmbOfCorrectAnswer;

        public History(int nmbOfCorrectAnswer, int nbmOfAnswer) {
            this.nmbOfAnswer = nbmOfAnswer;
            this.nmbOfCorrectAnswer = nmbOfCorrectAnswer;
        }

        public History() {
            nmbOfAnswer = 0;
            nmbOfCorrectAnswer = 0;
        }
    }
}
