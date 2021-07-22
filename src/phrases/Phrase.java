package phrases;

import java.util.List;
import java.util.Objects;

public class Phrase implements Comparable<Phrase> {
    //Basic:
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
    public int hashCode() {
        return Objects.hash(engWord, translation.hashCode());
    }

    @Override
    public String toString() {
        return "[Flashcard]: " + engWord + " -> " + getTranslationAsOneString() + " Ratio: " + getRatio();
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
        //Two cards are equal only with the same translation and english word
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

    public void setStatistic(int nmbOfCorrectAnswer, int nmbOfAnswer) {
        this.nmbOfCorrectAnswer = nmbOfCorrectAnswer;
        this.nmbOfAnswer = nmbOfAnswer;
    }

    public String getTranslationAsOneString() {
        String result = "";
        for (String x : translation) {
            result += x;
            result += "|";
        }
        return result.substring(0, result.lastIndexOf("|"));
    }

    public String getEngWord() {
        return engWord;
    }

    public List<String> getTranslation() {
        return translation;
    }

    public double getRatio() {
        double value;
        double correct = nmbOfCorrectAnswer;
        double all = nmbOfAnswer;
        value = Math.abs((correct/(all + 1))-(0.5/(all + 1)));
        value = value > 1 ? 1 : value;
        value = value < 0 ? 0 : value;
        return  value;
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

    public void editEngWord(String newWord) {
        engWord = newWord;
    }

    public void editTranslation(List<String> newTranslation) {
        translation = newTranslation;
    }

    public void editGroup(String group) {
        this.group = group;
    }

}
