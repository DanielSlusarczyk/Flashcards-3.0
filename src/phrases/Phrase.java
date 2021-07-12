package phrases;

import java.util.List;
import java.util.Objects;

public class Phrase implements Comparable<Phrase> {
    private String engWord;
    private List<String> translation;
    private String group;
    private int nmbOfAnswer;
    private int nmbOfCorrectAnswer;

    public Phrase(String engWord, List<String> translation, String group) {
        this.engWord = engWord;
        this.translation = translation;
        this.group = group;
    }

    public Phrase(String engWord, List<String> translation, int nmbOfCorrectAnswer, int nbmOfAnswer, String group) {
        this.engWord = engWord;
        this.translation = translation;
        this.group = group;
        this.nmbOfCorrectAnswer = nmbOfCorrectAnswer;
        this.nmbOfAnswer = nbmOfAnswer;
    }

    public Phrase(Phrase phrase, int nmbOfCorrectAnswer, int nbmOfAnswer) {
        this.engWord = phrase.engWord;
        this.translation = phrase.getTranslation();
        this.group = phrase.getGroup();
        this.nmbOfAnswer = nbmOfAnswer;
        this.nmbOfCorrectAnswer = nmbOfCorrectAnswer;
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
    public int hashCode() {
        return Objects.hash(engWord, translation.hashCode());
    }

    @Override
    public String toString() {
        return "[Flashcard]: " + engWord + " -> " + getTranslationAsOneString() + " Ratio: " + getRatio();
    }

    public void setStatistic(int nmbOfCorrectAnswer, int nmbOfAnswer) {
        this.nmbOfCorrectAnswer = nmbOfCorrectAnswer;
        this.nmbOfAnswer = nmbOfAnswer;
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
        if (nmbOfAnswer != 0)
            return (double) nmbOfCorrectAnswer / (double) nmbOfAnswer;
        return 1;
    }

    public String getGroup() {
        return group;
    }

    public int getNmbOfCorrectAnswer() {
        return nmbOfCorrectAnswer;
    }

    public int getNmbOfAnswer() {
        return nmbOfAnswer;
    }

    public void editEngWord(String newWord){
        engWord = newWord;
    }

    public void editTranslation(List<String> newTranslation){
        translation = newTranslation;
    }

    public void editGroup(String group){
        this.group = group;
    }

}
