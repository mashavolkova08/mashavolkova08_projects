package sample;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;

/**
 * <h1>Graph cumulative probability distribution function</h1>
 * The MainGraph program graphs a cumulative probability distribution
 * function using JavaFX.
 * <p>
 * @author  Masha Volkova
 * @version 1.0
 * @since  05/10/21
 */

public class MainGraph extends Application {
    //Array of bin names and array of probability values initialized
    //Variables for K and userDefinedInterval declared
    Float [] bNames = new Float[1000];
    Float [] pVals = new Float [1000];
    Float K; // slope
    int userDefinedInterval;

    /**
     * This method is used to set up a graph to display the
     * cumulative probability distribution function.
     * @param stage This is the parameter used in the start method
     * @return Nothing.
     */
    @Override
    public void start(Stage stage) {
        int count;
        count = getDataFromFile(bNames, pVals);

        //Defining the x and y axes
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        //Setting labels for the axes
        yAxis.setLabel("Cumulative Probability");
        xAxis.setLabel("Wind Speed Squared");

        // Create Line Chart
        LineChart<Number,Number> linechart = new LineChart<Number,Number>(xAxis,yAxis);

        // Prepare Data Points for line 1
        XYChart.Series series1 = new XYChart.Series();
        series1.setName("OLS");

        int binVal = 0;
        for (int i =0; i < count; i++) {
            Double y = Math.exp(-K * binVal);
            series1.getData().add(new XYChart.Data(binVal,y));
            binVal += userDefinedInterval;
        }

        XYChart.Series series2 = new XYChart.Series();
        series2.setName("Cumulative Probabilities per Interval");
        for (int i = 0; i < count; i++) {
            series2.getData().add(new XYChart.Data(bNames[i],pVals[i]));
        }

        //Setting the data to Line chart
        linechart.getData().addAll(series1,series2);
        //Creating a stack pane to hold the chart
        StackPane pane = new StackPane(linechart);
        pane.setPadding(new Insets(15, 15, 15, 15));
        pane.setStyle("-fx-background-color: BEIGE");
        //Setting the Scene
        Scene scene = new Scene(pane, 595, 350);
        stage.setTitle("Line Chart");
        stage.setScene(scene);
        stage.show();

    }

    /**
     * This is the main method. It launches the JavaFX application.
     * @param args Unused.
     * @return Nothing.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * This method is used to get data from a file
     * in order to use this data to make a graph.
     * @param binNames This is the first parameter to getDataFromFile method
     * @param probValues  This is the second parameter to getDataFromFile method
     * @return int This returns the count value
     * @exception IOException On input error.
     * @exception FileNotFoundException On invalid filename.
     * @exception Exception On conversion error.
     */
    private int getDataFromFile(Float [] binNames, Float [] probValues ) {
        BufferedReader br = null;
        String strLine = "";
        int count = 0;

        // Read input file, get interval and K value, get bin bucket names and values
        try {
            br = new BufferedReader( new FileReader("cumProbability.txt"));
            strLine = br.readLine(); // Get interval and K value
            String[] inLine = strLine.split(" ");
            userDefinedInterval = Integer.parseInt(inLine[0]);
            K = Float.parseFloat(inLine[1]);

            while( (strLine = br.readLine()) != null){
                inLine = strLine.split(" ");
                binNames[count] = Float.parseFloat(inLine[0]);
                probValues[count] = Float.parseFloat(inLine[1]);
                count++;
            }
            br.close();
        }
        catch (FileNotFoundException e) {
            System.err.println("File not found");
        }
        catch (IOException e) {
            System.err.println("Unable to read the file.");
        }
        catch (Exception e) {
            System.err.println("Cannot convert value in line: " + strLine + "to Float value");
        }
        return count;
    }
}
