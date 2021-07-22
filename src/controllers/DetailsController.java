package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import operation.FileReader;
import phrases.Phrase;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class DetailsController implements Initializable {
    //FXML:
    @FXML
    Label titleLabel, subtitleLabel, correctLabel, allLabel, averageLabel;
    @FXML
    AnchorPane rootPane;
    //Basic:
    private Phrase phrase;
    private List<String> correctDates;
    private List<Double> correctTimes;
    private List<String> incorrectDates;
    private List<Double> incorrectTimes;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setLabels();
        FileReader.readPhraseStats(phrase);
        correctDates = FileReader.getCorrectDateList();
        correctTimes = FileReader.getCorrectTimeList();
        incorrectDates = FileReader.getIncorrectDateList();
        incorrectTimes = FileReader.getIncorrectTimeList();
        setChart();
        averageLabel.setText("Average time: " + ((double) (Math.round(getAverage(correctTimes) * 100)) / 100));
    }

    private void setLabels() {
        phrase = StatsController.detailedPhrase;
        titleLabel.setText(phrase.getEngWord());
        subtitleLabel.setText(phrase.getTranslationAsOneString().replace("|", ", "));
        correctLabel.setText("Correct Answer: " + phrase.getNmbOfCorrectAnswer());
        allLabel.setText("All Answer: " + phrase.getNmbOfAnswer());
    }

    private double getAverage(List<Double>list){
        double result = 0;
        if(list.size() != 0){
            for(Double x : list){
                result += x;
            }
            result /= list.size();
        }
        return result;
    }

    private void setChart() {
        final NumberAxis xAxis = new NumberAxis();
        xAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(xAxis) {
            @Override
            public String toString(Number object) {
                long value = object.longValue();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date resultDate = new Date(value);
                return simpleDateFormat.format(resultDate);
            }
        });
        xAxis.setAutoRanging(false);
        System.out.println("[INFO]Min value:" + Math.min(getMin(correctDates), getMin(incorrectDates)));
        System.out.println("[INFO]Max value:" + Math.max(getMax(correctDates), getMax(incorrectDates)));
        xAxis.setLowerBound(0.99999 * Math.min(getMin(correctDates), getMin(incorrectDates)));
        xAxis.setUpperBound(1.00001 * Math.max(getMax(correctDates), getMax(incorrectDates)));
        xAxis.setTickUnit(((1.00001 * (Math.max(getMax(correctDates), getMax(incorrectDates))) - (0.99999 * Math.min(getMin(correctDates), getMin(incorrectDates)))) + 1000) / 4);
        final NumberAxis yAxis = new NumberAxis();
        final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        //Defining a series
        XYChart.Series<Number, Number> correctSeries = new XYChart.Series<>();
        correctSeries.setName("Correct");
        XYChart.Series<Number, Number> incorrectSeries = new XYChart.Series<>();
        correctSeries.setName("Incorrect");
        for (int i = 0; i < correctDates.size(); i++) {
            correctSeries.getData().add(new XYChart.Data<>(Long.parseLong(correctDates.get(i)), correctTimes.get(i)));
        }
        for (int i = 0; i < incorrectDates.size(); i++) {
            incorrectSeries.getData().add(new XYChart.Data<>(Long.parseLong(incorrectDates.get(i)), incorrectTimes.get(i)));
        }
        lineChart.getData().add(incorrectSeries);
        lineChart.getData().add(correctSeries);
        //Chart Properties
        lineChart.setPrefSize(675, 225);
        lineChart.setLayoutY(125);
        lineChart.legendVisibleProperty().setValue(false);
        lineChart.getStylesheets().add("source/css/chart.css");
        rootPane.getChildren().add(lineChart);
    }

    private double getMin(List<String> list) {
        if (list.size() == 0) {
            return (double) System.currentTimeMillis();
        }
        long min = Long.parseLong(list.get(0));
        for (String string : list) {
            if (Long.parseLong(string) < min) {
                min = Long.parseLong(string);
            }
        }
        return (double) min;
    }

    private double getMax(List<String> list) {
        long max = 0;
        if (list.size() == 0) {
            return (double) System.currentTimeMillis();
        }
        for (String string : list) {
            if (Long.parseLong(string) > max) {
                max = Long.parseLong(string);
            }
        }
        return (double) max;
    }

}
