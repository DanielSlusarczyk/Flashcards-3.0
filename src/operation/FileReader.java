package operation;

import exceptions.IncorrectLineException;
import management.CardsManager;
import phrases.Phrase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class FileReader {
    private static List<String> dateList;
    private static List<Double> timeList;

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
            addToDictionary(tmpLine.substring(tmpLine.indexOf("<Grp>") + 5), engWord, translation, correctAnswers, allAnswers);
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

    public static void addToDictionary(String group, String engWord, List<String> translation, int correctAnswers, int allAnswers) {
        CardsManager.allWords.addCard(engWord, translation, correctAnswers, allAnswers, group);
        switch (group) {
            case "PV" -> CardsManager.phrasalVerbs.addCard(engWord, translation, correctAnswers, allAnswers, "PV");
            case "N" -> CardsManager.nouns.addCard(engWord, translation, correctAnswers, allAnswers, "N");
            case "AD" -> CardsManager.adjectives.addCard(engWord, translation, correctAnswers, allAnswers, "AD");
            case "A" -> CardsManager.adverbs.addCard(engWord, translation, correctAnswers, allAnswers, "A");
            case "V" -> CardsManager.verbs.addCard(engWord, translation, correctAnswers, allAnswers, "V");
            default -> CardsManager.others.addCard(engWord, translation, correctAnswers, allAnswers, "O");
        }
    }

    public static void writeCards(String outFile, Iterator<Phrase> iterator) {
        PrintWriter writingFile;
        try {
            writingFile = new PrintWriter(outFile);
            while (iterator.hasNext()) {
                Phrase phrase = iterator.next();
                writingFile.println("<Ang>" + phrase.getEngWord() + "<Pol>" + phrase.getTranslationAsOneString() + "<Result>" + phrase.getNmbOfCorrectAnswer() + "<>" + phrase.getNmbOfAnswer() + "<Grp>" + phrase.getGroup());
            }
            writingFile.close();
        } catch (FileNotFoundException e) {
            System.out.println("It cannot save the file");
        }
    }

    public static void writeCardsStats(Phrase phrase, Long currentTime, Double time) {
        PrintWriter printWriter;
        try {
            printWriter = new PrintWriter(new FileOutputStream("src/source/words/stats/[" + phrase.hashCode() + "]" + phrase.getEngWord() + ".txt", true));
            printWriter.append("<Date>").append(String.valueOf(currentTime)).append("<Time>").append(String.valueOf(time));
            printWriter.println();
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void readPhraseStats(Phrase phrase){
        timeList = new ArrayList<>();
        dateList = new ArrayList<>();
        Scanner readingFile;
        try {
            readingFile = new Scanner(new File("src/source/words/stats/[" + phrase.hashCode() + "]" + phrase.getEngWord() + ".txt"));
            while (readingFile.hasNextLine()) {
                String tmpLine = readingFile.nextLine();
                dateList.add(tmpLine.substring(tmpLine.indexOf("<Date>") + 6, tmpLine.indexOf("<Time>")));
                timeList.add(Double.parseDouble(tmpLine.substring(tmpLine.indexOf("<Time>") + 6)));
            }
        } catch (FileNotFoundException e) {
            System.out.println("[INFO]There is no stats of Phrase");
        }
    }

    public static List<String> getDateList(){
        return dateList;
    }

    public static List<Double> getTimeList() {
        return timeList;
    }
}
