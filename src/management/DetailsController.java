package management;

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
    @FXML
    Label titleLabel, subtitleLabel, correctLabel, allLabel;
    @FXML
    AnchorPane rootPane;

    private Phrase phrase;
    private List<String> dates;
    private List<Double> times;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setLabels();
        FileReader.readPhraseStats(phrase);
        dates = FileReader.getDateList();
        times = FileReader.getTimeList();
        setChart();
    }

    private void setLabels() {
        titleLabel.setStyle("-fx-text-fill: rgb(255, 255, 255)");
        subtitleLabel.setStyle("-fx-text-fill: rgb(255, 255, 255)");
        correctLabel.setStyle("-fx-text-fill: rgb(255, 255, 255)");
        allLabel.setStyle("-fx-text-fill: rgb(255, 255, 255)");
        phrase = StatsController.detailedPhrase;
        titleLabel.setText(phrase.getEngWord());
        subtitleLabel.setText(phrase.getTranslationAsOneString().replace("|", ", "));
        correctLabel.setText("Correct Answer: " + phrase.getNmbOfCorrectAnswer());
        allLabel.setText("All Answer: " + phrase.getNmbOfAnswer());
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
        System.out.println("[INFO]Min value:" + getMin(dates));
        System.out.println("[INFO]Max value:" + getMax(dates));
        xAxis.setLowerBound(0.999999 * getMin(dates));
        xAxis.setUpperBound(1.000001 * getMax(dates));
        xAxis.setTickUnit(((1.000001 * (getMax(dates)) - (0.999999 * getMin(dates))) + 1000)/4);
        final NumberAxis yAxis = new NumberAxis();
        final LineChart<Number, Number> lineChart =
                new LineChart<>(xAxis, yAxis);
        //Defining a series
        XYChart.Series series = new XYChart.Series();
        for (int i = 0; i < dates.size(); i++) {
            series.getData().add(new XYChart.Data(Long.parseLong(dates.get(i)), times.get(i)));
        }
        lineChart.getData().add(series);
        //Chart Properties
        lineChart.setPrefSize(675, 225);
        lineChart.setLayoutY(125);
        lineChart.legendVisibleProperty().setValue(false);
        lineChart.getStylesheets().add("source/css/chart.css");
        rootPane.getChildren().add(lineChart);
    }

    private double getMin(List<String> list){
        if(list.size() == 0){
            return (double) System.currentTimeMillis();
        }
        long min = Long.parseLong(list.get(0));
        for(String string : list){
            if(Long.parseLong(string) < min){
                min = Long.parseLong(string);
            }
        }
        return (double)min;
    }

    private double getMax(List<String> list){
        long max = 0;
        if(list.size() == 0){
            return (double)System.currentTimeMillis();
        }
        for(String string : list){
            if(Long.parseLong(string) > max){
                max = Long.parseLong(string);
            }
        }
        return (double)max;
    }

}
