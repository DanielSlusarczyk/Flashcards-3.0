package operation;

import exceptions.IncorrectLineException;
import management.CardsManager;
import controllers.Controller;
import phrases.Phrase;
import settings.Settings;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FileReader implements Settings {
    //Static:
    private static List<String> correctDateList, incorrectDateList;
    private static List<Double> correctTimeList, incorrectTimeList;

    public static void readCards() {
        BufferedReader readingFile;
        try {
            readingFile = new BufferedReader(new InputStreamReader(new FileInputStream(wordPath), StandardCharsets.UTF_8));
            String tmpLine = readingFile.readLine();
            while (tmpLine != null) {
                try {
                    formatCorrectness(tmpLine);
                } catch (IncorrectLineException e) {
                    System.out.println(e.toString());
                    break;
                }
                String engWord = extractEngWord(tmpLine);
                List<String> translation = extractTranslation(tmpLine);
                int correctAnswers = Integer.parseInt(tmpLine.substring(tmpLine.indexOf("<Result>") + 8, tmpLine.indexOf("<>")));
                int allAnswers = Integer.parseInt(tmpLine.substring(tmpLine.indexOf("<>") + 2, tmpLine.indexOf("<Grp>")));
                addToDictionary(tmpLine.substring(tmpLine.indexOf("<Grp>") + 5), engWord, translation, correctAnswers, allAnswers);
                tmpLine = readingFile.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
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
        //Add phrase to usedDictionary after making new cards while using
        if (Controller.getCardsManager() != null) {
            Controller.getCardsManager().getUsedDictionary().addCard(engWord, translation, correctAnswers, allAnswers, "O");
        }
    }

    public static void writeCards(Iterator<Phrase> iterator) {
        PrintWriter writingFile;
        try {
            writingFile = new PrintWriter(new OutputStreamWriter(new FileOutputStream(wordPath), StandardCharsets.UTF_8));
            while (iterator.hasNext()) {
                Phrase phrase = iterator.next();
                writingFile.println("<Ang>" + phrase.getEngWord() + "<Pol>" + phrase.getTranslationAsOneString() + "<Result>" + phrase.getNmbOfCorrectAnswer() + "<>" + phrase.getNmbOfAnswer() + "<Grp>" + phrase.getGroup());
            }
            writingFile.close();
        } catch (FileNotFoundException e) {
            System.out.println("[INFO]It cannot save the file");
        }
    }

    public static void writeCardsStats(Phrase phrase, Long currentTime, Double time, Answer answer) {
        PrintWriter printWriter;
        try {
            printWriter = new PrintWriter(new FileOutputStream(getPath(phrase), true));
            switch (answer){
                case CORRECT -> printWriter.append("<Date>").append(String.valueOf(currentTime)).append("<Time>").append(String.valueOf(time)).append("<Answer>").append("Correct");
                case INCORRECT -> printWriter.append("<Date>").append(String.valueOf(currentTime)).append("<Time>").append(String.valueOf(time)).append("<Answer>").append("Incorrect");
                case TYPO -> printWriter.append("<Date>").append(String.valueOf(currentTime)).append("<Time>").append(String.valueOf(time)).append("<Answer>").append("Typo");
            }
            printWriter.println();
            printWriter.close();
        } catch (FileNotFoundException e) {
            System.out.println("[INFO]Phrase " + phrase.getEngWord() + "should has history");
        }
    }

    public static void readPhraseStats(Phrase phrase) {
        correctTimeList = new ArrayList<>();
        correctDateList = new ArrayList<>();
        incorrectTimeList = new ArrayList<>();
        incorrectDateList = new ArrayList<>();
        BufferedReader readingFile;
        try {
            readingFile = new BufferedReader(new InputStreamReader(new FileInputStream(getPath(phrase))));
            String tmpLine = readingFile.readLine();
            while (tmpLine != null) {
                String answeringDate = tmpLine.substring(tmpLine.indexOf("<Date>") + 6, tmpLine.indexOf("<Time>"));
                if(!tmpLine.contains("<Answer>")){
                    correctDateList.add(answeringDate);
                    correctTimeList.add(Double.parseDouble(tmpLine.substring(tmpLine.indexOf("<Time>") + 6)));
                } else{
                    String answer = tmpLine.substring(tmpLine.indexOf("<Answer>") + 8).trim();
                    double answeringTime = Double.parseDouble(tmpLine.substring(tmpLine.indexOf("<Time>") + 6, tmpLine.indexOf("<Answer>")));
                    switch (answer){
                        case "Correct", "Typo" -> {
                            correctTimeList.add(answeringTime);
                            correctDateList.add(answeringDate);
                        }
                        case "Incorrect" -> {
                            incorrectTimeList.add(answeringTime);
                            incorrectDateList.add(answeringDate);
                        }
                    }
                }
                tmpLine = readingFile.readLine();
            }
            readingFile.close();
        } catch (IOException e) {
            System.out.println("[INFO]" + phrase.getEngWord() + " has no history");
        }
    }

    public static boolean removePhraseStats(Phrase phrase) {
        File file = new File(getPath(phrase));
        return file.delete();
    }

    public static String getPath(Phrase phrase) {
        return historyPath + "[" + phrase.hashCode() + "]" + phrase.getEngWord() + ".txt";
    }

    public static boolean editName(String oldPath, String newPath) {
        Path source = Paths.get(oldPath);
        Path target = Paths.get(newPath);
        try {
            Files.move(source, target);
        } catch (IOException e) {
            return true;
        }
        return false;
    }

    public static List<String> getCorrectDateList() {
        return correctDateList;
    }

    public static List<Double> getCorrectTimeList() {
        return correctTimeList;
    }


    public static List<String> getIncorrectDateList() {
        return incorrectDateList;
    }

    public static List<Double> getIncorrectTimeList() {
        return incorrectTimeList;
    }
}
