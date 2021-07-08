package phrases;

import java.util.*;

public class Dictionary implements Iterable {
    private final Set<Phrase> flashcards;
    String name;

    public Dictionary(String name, Comparator<Phrase> comparator, Dictionary dictionary) {
        flashcards = new TreeSet<>(comparator);
        flashcards.addAll(dictionary.flashcards);
        this.name = name;
    }
    public Dictionary(String name) {
        flashcards = new TreeSet<>();
        this.name = name;
    }

    public void addNewCard(String engWord, List<String> translation, String group) {
        Phrase phrase = new Phrase(engWord, translation, group);
        flashcards.add(phrase);
    }

    public void addCard(String engWord, List<String> translation, int nmbOfCorrectAnswer, int nbmOfAnswer, String group) {
        Phrase phrase = new Phrase(engWord, translation, nmbOfCorrectAnswer, nbmOfAnswer, group);
        flashcards.add(phrase);
    }

    public void addCards(Dictionary dictionary){
        flashcards.addAll(dictionary.flashcards);
    }

    public double getTheHighestRatio(){
        double result = 0;
        for(Phrase phrase : flashcards){
            if(phrase.getRatio() > result)
                result = phrase.getRatio();
        }
        return result;
    }

    public int size(){
        return flashcards.size();
    }

    public Phrase get(int index){
        Iterator<Phrase> ite = iterator();
        Phrase result = null;
        for(int i = 0; i < index; i++){
            result = ite.next();
        }
        return result;
    }

    public Phrase get(double ratio){
        for(Phrase phrase : flashcards){
            if(phrase.getRatio() >= ratio){
                return phrase;
            }
        }
        return null;
    }

    public Phrase get(Phrase phrase){
        for(Phrase p : flashcards){
            if(p.equals(phrase)){
                return p;
            }
        }
        return null;
    }

    public void add(Phrase phrase){
        flashcards.add(phrase);
    }

    public boolean contains(Phrase phrase){
        return flashcards.contains(phrase);
    }

    @Override
    public String toString(){
        return "[DICTIONARY]: " + name;
    }

    @Override
    public Iterator<Phrase> iterator() {
        return flashcards.iterator();
    }
}
