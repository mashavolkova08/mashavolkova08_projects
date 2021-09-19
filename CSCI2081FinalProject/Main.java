package sample;

import java.util.Scanner;
import java.io.*;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * <h1>Read data file, create histogram, calculate probability</h1>
 * The Main program reads file with wind value data, creates histogram,
 * calculates OLS, and calculates probability using cumulative probability
 * distribution.
 * <p>
 * @author  Masha Volkova
 * @version 1.0
 * @since  05/10/21
 */

public class Main {
    /**
     * This is the main method.
     * @param args Unused.
     * @return Nothing.
     * @exception IOException On input error.
     * @exception FileNotFoundException On invalid Filename.
     */

    public static void main(String args[]) {

        //Array for wind values initialized
        Float[] windValues = new Float[9000];
        //Histogram with bins initialized
        Bin[] histogram = new Bin[200];
        //Scanner initialized
        Scanner scanner = new Scanner(System.in);

        //Variables initialized
        boolean validFilename = false;
        String filenameInput;

        // While loop checks if valid filename is entered by user
        while (!validFilename) {

            try {
                System.out.println("Enter filename: ");
                filenameInput = scanner.next();
                File file = new File("Augspurger_2018_03.csv");

                if (file.exists()) {
                    validFilename = true;
                } else {
                    System.out.println("File does not exist. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Invalid filename. Please try again.");
            }
        }


        //Variables initialized
        boolean validDimensions = false;
        String intervalInput;
        int userDefinedInterval = 0;

        //While loop checks if valid interval is entered by user (must be between 50 and 100)
        while (!validDimensions) {
            try {
                System.out.println("Enter integer between 50 and 100 for the number of intervals: ");
                intervalInput = scanner.next();
                userDefinedInterval = Integer.parseInt(intervalInput);

                if (userDefinedInterval >= 50 && userDefinedInterval <= 100) {
                    validDimensions = true;
                } else {
                    System.out.println("Invalid integer entered. Please enter integer between 50 and 100.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please integer between 50 and 100.");
            }
        }

        //Histogram created by defining Bin constructor with interval, count, and cumulative probability
        for (int i = 0; i < 200; i++) {
            histogram[i] = new Bin(i * userDefinedInterval, 0, 0);
        }

        //Variables initialized
        BufferedReader br = null;
        String strLine = "";
        String[] inLine = new String[9];
        int count = 0;

        //Reads file to get wind values
        //Adds wind values to array
        try {
            br = new BufferedReader(new FileReader("Augspurger_2018_03.csv"));
            for (int i = 0; i < 8; i++) {
                br.readLine();
            }
            while ((strLine = br.readLine()) != null) {
                inLine = strLine.split(",");
                windValues[count] = Float.parseFloat(inLine[5]);
                count ++;
            }
            br.close();
        } catch (IOException e) {
            System.err.println("Unable to read the file.");
        } catch (Exception e) {
            System.err.println("Cannot convert value in line: " + inLine[7] + "to Float value");
        }

        //Makes histogram by iterating through the wind values array
        //Increments count in corresponding bin
        for (int i = 0; i < windValues.length; i++) {
            for (int j = 0; j < 200; j++) {
                if (windValues[i] != null){
                    if ((Math.pow(windValues[i], 2.0) >= histogram[j].interval)&&((Math.pow(windValues[i], 2.0)) < (histogram[j + 1].interval))){
                        histogram[j].count += 1;
                        break;
                    }
                }
            }
        }

        //Normalize counts in bins to cumulative probabilities
        //If probabilities negative, stops conversion
        double cumulativeProb = 1.0;
        for (int i = 0; i < 200; i++) {
            cumulativeProb = cumulativeProb - (float) histogram[i].count/ (float) windValues.length;

            if (cumulativeProb < 0.0){
                break;
            }
            histogram[i].cumProbability = (float)cumulativeProb;
        }

         //OLS calculated
         //Used to find K value (sum of log of the cumulative probabilities divided by sum of the cumulative interval length)
         //Exclude sparse data (<= 0.01)
        Float num = 0.0f;
        Float den = 0.0f;
        for (int j = 0; j < 200; j++) {
            if (histogram[j].cumProbability <= 0.01)
                break;
            num = num - (float)Math.log(histogram[j].cumProbability);
            den = den + histogram[j+1].interval;
        }
        Float K = num / den;

        // Write userDefinedInterval and K value in the first line of cumProbability.txt file
        // Write bin intervals and cumulative probabilities to cumProbability.txt file
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(new File("cumProbability.txt"));
            printWriter.println(userDefinedInterval + " " + K);
            for (int i = 0; i < 200; i++){
                printWriter.println(histogram[i].interval + " " + (Float.valueOf(histogram[i].cumProbability)));
            }
            printWriter.close();
        }
        catch (FileNotFoundException f) {
            System.out.println("File not found.");
        }

        //Variable initialized
        boolean continueLoop = true;

       //While loop continues until userInput is not 'q' (quit)
        while (continueLoop){
            String userInput = "";
            System.out.println("Enter ‘less’, ‘greaterEq’, or ‘q’ to quit: ");
            boolean validInput = false;

            //While loop checks for valid input ('less, ‘greaterEq’, or ‘q’)
            while (!validInput){
                userInput = scanner.next();
                if (userInput.equals("less") || userInput.equals("greaterEq") || userInput.equals("q")){
                    validInput = true;
                }
                else{
                    System.out.println("Incorrect input. Please enter ‘less’, ‘greaterEq’, or ‘q’ to quit");
                }

            }

            //If statement ends while loop if user input is 'q'

            if (userInput.equals("q")){
                continueLoop = false;
                System.exit(0);
            }

            String windSpeedInput = "";
            Float windSpeed = (float)0;
            System.out.println("Enter windspeed: ");
            boolean validSpeed = false;

            //While loop checks for valid speed (greater than or equal to 0.0)
            while (!validSpeed) {
                try {

                    windSpeedInput = scanner.next();
                    windSpeed = Float.parseFloat(windSpeedInput);
                    if (windSpeed >= 0.0) {
                        validSpeed = true;
                    } else {
                        System.out.println("Invalid input. Please enter wind speed as positive number");
                    }

                } catch (Exception e) {
                    System.out.println("Invalid input. Please enter wind speed as float (0.0)");
                }
            }

            //Probability calculated based on user input (less/greaterEq and wind speed)
            if (userInput.equals("less")){
                Float prob = (float)(1.0 - Math.exp(-K * Math.pow(windSpeed, 2)));
                System.out.println("Probability wind speed < " + windSpeed + " is " + prob);
            }
            else if (userInput.equals("greaterEq")){
                Float prob = (float)(Math.exp(-K * Math.pow(windSpeed, 2)));
                System.out.println("Probability wind speed >= " + windSpeed + " is " + prob);
            }

        }
    }
}
