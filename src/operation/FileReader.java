package operation;

import exceptions.IncorrectLineException;
import management.CardsManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileReader {
    //Correct Format Example:
    //<Ang>home<Pol>dom<Result>1<>2<Grp>N
    //Types of membership:
    //PV-Phrasal verb
    //N- Noun
    //AD- Adjective
    //A- Adverb
    //O- Other

    public static void readCards(String inFile) throws FileNotFoundException {
        Scanner readingFile;
        readingFile = new Scanner(new File(inFile));
        while (readingFile.hasNextLine()) {
            String tmpLine = readingFile.nextLine();
            try {
                formatCorrectness(tmpLine);
            } catch (IncorrectLineException e) {
                System.out.println(e.toString());
            }
            String engWord = extractEngWord(tmpLine);
            List<String> translation = extractTranslation(tmpLine);
            int correctAnswers = Integer.parseInt(tmpLine.substring(tmpLine.indexOf("<Result>") + 8, tmpLine.indexOf("<>")));
            int allAnswers = Integer.parseInt(tmpLine.substring(tmpLine.indexOf("<>") + 2, tmpLine.indexOf("<Grp>")));
            addToDictionary(tmpLine, engWord, translation, correctAnswers, allAnswers);
        }
    }

    private static void formatCorrectness(String tmpLine) throws IncorrectLineException {
        if (!tmpLine.matches("^<Ang>.+<Pol>.+<Result>\\d+<>\\d+<Grp>[A-Z]{1,2}$")) {
            throw new IncorrectLineException(tmpLine);
        }
    }

    private static String extractEngWord(String tmpLine) {
        return tmpLine.substring(tmpLine.indexOf("<Ang>") + 5, tmpLine.indexOf("<Pol>"));
    }

    private static List<String> extractTranslation(String tmpLine) {
        List<String> translation = new ArrayList<>();
        if (!tmpLine.contains("|")) {
            translation.add(tmpLine.substring(tmpLine.indexOf("<Pol>") + 5, tmpLine.indexOf("<Result>")));
        } else {
            int startIte = tmpLine.indexOf("<Pol>") + 5;
            int finishIte = tmpLine.indexOf("|");

            while (finishIte != -1) {
                translation.add(tmpLine.substring(startIte, finishIte));
                startIte = finishIte + 1;
                finishIte = tmpLine.indexOf("|", startIte + 1) == -1 ? tmpLine.indexOf("<Result>", startIte) : tmpLine.indexOf("|", startIte + 1);
            }
        }
        return translation;
    }

    private static void addToDictionary(String tmpLine, String engWord, List<String> translation, int correctAnswers, int allAnswers) {
        CardsManager.allWords.addCard(engWord, translation, correctAnswers, allAnswers);
        switch (tmpLine.substring(tmpLine.indexOf("<Grp>") + 5)) {
            case "PV" -> CardsManager.phrasalVerbs.addCard(engWord, translation, correctAnswers, allAnswers);
            case "N" -> CardsManager.nouns.addCard(engWord, translation, correctAnswers, allAnswers);
            case "AD" -> CardsManager.adjectives.addCard(engWord, translation, correctAnswers, allAnswers);
            case "A" -> CardsManager.adverbs.addCard(engWord, translation, correctAnswers, allAnswers);
            case "V" -> CardsManager.verbs.addCard(engWord, translation, correctAnswers, allAnswers);
            default -> CardsManager.others.addCard(engWord, translation, correctAnswers, allAnswers);
        }
    }
}
