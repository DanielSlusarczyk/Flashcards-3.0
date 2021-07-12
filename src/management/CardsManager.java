package management;

import exceptions.UnhandledSituationException;
import operation.FileReader;
import comparators.RatioComparator;
import phrases.Dictionary;
import phrases.Phrase;
import settings.Settings;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class CardsManager implements Settings, Iterable<Phrase> {
    public static Dictionary phrasalVerbs;
    public static Dictionary nouns;
    public static Dictionary adjectives;
    public static Dictionary adverbs;
    public static Dictionary verbs;
    public static Dictionary others;
    public static Dictionary allWords;
    private static List<Dictionary> dictionaryList;
    private Mode mode;
    private Dictionary usedDictionary;
    private double sessionRatio;
    private int phraseCounter;


    public CardsManager(String inFile) throws FileNotFoundException {
        phrasalVerbs = new Dictionary("Phrasal Verbs");
        nouns = new Dictionary("Nouns");
        verbs = new Dictionary("Verbs");
        adjectives = new Dictionary("Adjectives");
        adverbs = new Dictionary("Adverbs");
        others = new Dictionary("Others");
        allWords = new Dictionary("All Words");
        FileReader.readCards(inFile);
        dictionaryList = new ArrayList<>();
        dictionaryList.add(phrasalVerbs);
        dictionaryList.add(nouns);
        dictionaryList.add(adjectives);
        dictionaryList.add(adverbs);
        dictionaryList.add(others);
        dictionaryList.add(allWords);
        dictionaryList.add(verbs);
    }

    @Override
    public Iterator<Phrase> iterator() {
        return usedDictionary.iterator();
    }

    public Phrase getNextPhrase() {
        Phrase result;
        try {
            if (usedDictionary == null || usedDictionary.size() == 0) {
                throw new UnhandledSituationException("[WANING]The used dictionary contains no phrases!");
            }
        }catch (UnhandledSituationException exception){
            exception.printStackTrace();
        }
        if(SettingsController.newFirst){
            System.out.println("[INFO]New Phrase");
            result = usedDictionary.getNew();
            if(result != null){
                return result;
            }
        }
        //Random phrase every AMOUNT_OF_CARD_BETWEEN_RANDOM
        phraseCounter++;
        if (phraseCounter % AMOUNT_OF_CARDS_BETWEEN_RANDOM == 0) {
            int number = new Random().nextInt(usedDictionary.size());
            System.out.println("[INFO]Random phrase: Number:" + number);
            return usedDictionary.get(number);
        }
        System.out.println("[INFO]Phrase with the closest convergence");
        result = usedDictionary.get(sessionRatio);
        return result;
    }

    public void dictionariesStatus() {
        for (Dictionary dictionary : dictionaryList) {
            System.out.println(dictionary.toString());
            Iterator<Phrase> iterator = dictionary.iterator();
            while (iterator.hasNext()) {
                System.out.println(iterator.next().toString());
            }
        }
    }

    public void actualizeRatio(Answer answer) {
        switch (answer) {
            case CORRECT -> sessionRatio = sessionRatio - sessionRatio * RATIO_INCREASE_FOR_CORRECT_ANSWER;
            case INCORRECT -> sessionRatio = sessionRatio + sessionRatio * RATIO_DECREASE_FOR_INCORRECT_ANSWER;
            case TYPO -> sessionRatio = sessionRatio - sessionRatio * RATIO_INCREASE_FOR_TYPO;
        }
    }

    public Double getActualRatio() {
        return sessionRatio;
    }

    public int getAmount(){
        return usedDictionary.size();
    }

    public String getName(){
        return usedDictionary.getName();
    }

    public Dictionary getAllWords(){
        return allWords;
    }

    public int getNmb(){
        return phraseCounter;
    }


    public void setMode(Mode mode) {
        this.mode = mode;
        switch (mode) {
            case ALL -> usedDictionary = new Dictionary("All", new RatioComparator(), allWords);
            case NOUN -> usedDictionary = new Dictionary("Noun", new RatioComparator(), nouns);
            case VERB -> usedDictionary = new Dictionary("Verb", new RatioComparator(), verbs);
            case ADVERB -> usedDictionary = new Dictionary("Adverb", new RatioComparator(), adverbs);
            case ADJECTIVE -> usedDictionary = new Dictionary("Adjective", new RatioComparator(), adjectives);
            case PHRASAL_VERB -> usedDictionary = new Dictionary("Phrasal Verb", new RatioComparator(), phrasalVerbs);
            case OTHERS -> usedDictionary = new Dictionary("Others", new RatioComparator(), others);
        }
        dictionaryList.add(usedDictionary);
        sessionRatio = usedDictionary.getTheHighestRatio();
    }

    public void remove(Phrase phrase){
        usedDictionary.remove(phrase);
        allWords.remove(phrase);
        switch(usedDictionary.getName()){
            case "Noun" -> nouns.remove(phrase);
            case "Verb" -> verbs.remove(phrase);
            case "Adverb" -> adverbs.remove(phrase);
            case "Adjective" -> adjectives.remove(phrase);
            case "Phrasal Verb" -> phrasalVerbs.remove(phrase);
            case "Others" -> others.remove(phrase);
        }
    }

    public Iterator<Phrase> autoSaveIterator(){
        //Actualize allWords by usedDictionary
        Iterator<Phrase> iterator = usedDictionary.iterator();
        while (iterator.hasNext()){
            Phrase phrase = iterator.next();
            if(allWords.contains(phrase)){
                allWords.get(phrase).setStatistic(phrase.getNmbOfCorrectAnswer(), phrase.getNmbOfAnswer());
            }
        }
        return allWords.iterator();
    }

    public void editEngWord(Phrase phrase, String newWord){
        String oldPath = FileReader.getPath(phrase);
        phrase.editEngWord(newWord);
        if(!FileReader.editName(oldPath, FileReader.getPath(phrase))){
            System.out.println("[INFO] Problem with new name!");
            System.out.println("[INFO] oldPath: " + oldPath);
            System.out.println("[INFO] newPath: " + FileReader.getPath(phrase));
        }
    }

    public void editTranslation(Phrase phrase, List<String> newTranslation){
        String oldPath = FileReader.getPath(phrase);
        phrase.editTranslation(newTranslation);
        if(!FileReader.editName(oldPath, FileReader.getPath(phrase))){
            System.out.println("[INFO] Problem with new name!");
        }
    }

    public void editGroup(Phrase phrase, String group){
        switch(phrase.getGroup()){
            case "Noun" -> nouns.remove(phrase);
            case "Verb" -> verbs.remove(phrase);
            case "Adverb" -> adverbs.remove(phrase);
            case "Adjective" -> adjectives.remove(phrase);
            case "Phrasal Verb" -> phrasalVerbs.remove(phrase);
            case "Others" -> others.remove(phrase);
        }
        switch(group){
            case "Noun" -> nouns.add(phrase);
            case "Verb" -> verbs.add(phrase);
            case "Adverb" -> adverbs.add(phrase);
            case "Adjective" -> adjectives.add(phrase);
            case "Phrasal Verb" -> phrasalVerbs.add(phrase);
            case "Others" -> others.add(phrase);
        }
        phrase.editGroup(group);
    }

}
