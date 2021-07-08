package phrases;

import java.util.List;
import java.util.Objects;

public class Phrase implements Comparable<Phrase> {
    private final String engWord;
    private final List<String> translation;
    private final History history;
    private String group;

    public Phrase(String engWord, List<String> translation, String group) {
        this.engWord = engWord;
        this.translation = translation;
        history = new History();
        this.group = group;
    }

    public Phrase(String engWord, List<String> translation, int nmbOfCorrectAnswer, int nbmOfAnswer, String group) {
        this.engWord = engWord;
        this.translation = translation;
        history = new History(nmbOfCorrectAnswer, nbmOfAnswer);
        this.group = group;
    }

    public Phrase(Phrase phrase, int nmbOfCorrectAnswer, int nbmOfAnswer, History history) {
        this.engWord = phrase.engWord;
        this.translation = phrase.getTranslation();
        this.history = history;
        this.group = phrase.getGroup();
        history.setStatistic(nmbOfCorrectAnswer, nbmOfAnswer);
    }

    public String getTranslationAsOneString() {
        StringBuilder result = new StringBuilder();
        for (String x : translation) {
            result.append(x);
            result.append("|");
        }
        return result.substring(0, result.toString().lastIndexOf("|"));
    }

    public String getEngWord() {
        return engWord;
    }

    public List<String> getTranslation() {
        return translation;
    }

    public double getRatio() {
        return history.getRatio();
    }

    public String getGroup(){
        return group;
    }

    public int getNmbOfCorrectAnswer(){
        return history.nmbOfCorrectAnswer;
    }

    public int getNmbOfAnswer(){
        return history.nmbOfAnswer;
    }

    public History getHistory(){
        return history;
    }

    @Override
    public int compareTo(Phrase o) {
        if (engWord.compareTo(o.getEngWord()) == 0) {
            List<String> shorterList = translation.size() <= o.getTranslation().size() ? translation : o.getTranslation();
            List<String> longerList = translation.size() > o.getTranslation().size() ? translation : o.getTranslation();
            for (int i = 0; i < shorterList.size(); i++) {
                if (shorterList.get(i).compareTo(longerList.get(i)) != 0) {
                    return 1;
                }
            }
            if (longerList.size() > shorterList.size()) {
                return 1;
            }
        }
        return engWord.compareTo(o.getEngWord());
    }

    @Override
    public String toString() {
        return "[Flashcard]: " + engWord + " -> " + getTranslationAsOneString() + " WYNIK: " + getRatio();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Phrase) {
            //Translation
            List<String> shorterList = translation.size() <= ((Phrase) o).getTranslation().size() ? translation : ((Phrase) o).getTranslation();
            List<String> longerList = translation.size() > ((Phrase) o).getTranslation().size() ? translation : ((Phrase) o).getTranslation();
            //English Word and Translation
            return longerList.containsAll(shorterList) && engWord.equals(((Phrase) o).engWord);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(engWord, translation.hashCode());
    }

    public class History {
        private int nmbOfAnswer;
        private int nmbOfCorrectAnswer;

        public History(int nmbOfCorrectAnswer, int nbmOfAnswer) {
            this.nmbOfAnswer = nbmOfAnswer;
            this.nmbOfCorrectAnswer = nmbOfCorrectAnswer;
        }

        public void setStatistic(int nmbOfCorrectAnswer, int nmbOfAnswer){
            this.nmbOfCorrectAnswer = nmbOfCorrectAnswer;
            this.nmbOfAnswer = nmbOfAnswer;
        }

        public History() {
            nmbOfAnswer = 0;
            nmbOfCorrectAnswer = 0;
        }

        public double getRatio() {
            if (nmbOfAnswer != 0)
                return (double) nmbOfCorrectAnswer / (double) nmbOfAnswer;
            return 0;
        }

        public int getNmbOfCorrectAnswer(){
            return nmbOfCorrectAnswer;
        }

        public int getNmbOfAnswer() {
            return nmbOfAnswer;
        }
    }
}
