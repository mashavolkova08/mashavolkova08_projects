import java.util.Scanner;
import java.io.*;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static void main(String args[]) {
        Float[] windValues = new Float[9000];
        Bin[] histogram = new Bin[200];
        int maxSpeedSqr = 1000;
        //Bin[] histogram;
        Scanner scanner = new Scanner(System.in);

        boolean validFilename = false;
        String filenameInput;

        //While loop checks if valid filename is entered by user
        while (!validFilename) {

            try {
                //System.out.println("Enter filename: ");
                //filenameInput = scanner.next();
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

        //While loop checks if valid interval is entered by user
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

        // int numBins = maxSpeedSqr/ userDefinedInterval +1;
        //int numBins = 200;

        //System.out.println("numBins: " + numBins);

        ///histogram = new Bin[numBins];

        for (int i = 0; i < 200; i++) {
            // define a Bin constructor to set the end point of the
            // interval spanned by each bin, the count of wind speeds in
            // that interval, and the cumulative probability (to be computed)
            histogram[i] = new Bin(i * userDefinedInterval, 0, 0);
        }




        BufferedReader br = null;
        String strLine = "";
        String[] inLine = new String[9];
        int count = 0;

        // Read in file, get wind values

        try {
            br = new BufferedReader(new FileReader("Augspurger_2018_03.csv"));
            for (int i = 0; i < 8; i++) {
                br.readLine();
            }
            while ((strLine = br.readLine()) != null) {
                inLine = strLine.split(",");
                windValues[count] = Float.parseFloat(inLine[5]);
                //System.out.println(inLine[5]);
                count ++;
            }
            br.close();
        } catch (IOException e) {
            System.err.println("Unable to read the file.");
        } catch (Exception e) {
            System.err.println("Cannot convert value in line: " + inLine[7] + "to Float value");
        }

        //Normalize counts (step 3)
        for (int i = 0; i < windValues.length; i++) {
            for (int j = 0; j < 200; j++) {
                // Note, you should square the wind values before comparing them
                if (windValues[i] != null){
                    if ((Math.pow(windValues[i], 2.0) >= histogram[j].interval)&&((Math.pow(windValues[i], 2.0)) < (histogram[j + 1].interval))){
                        histogram[j].count += 1;
                        break;
                    }
                }
            }

        }
        double cumulativeProb = 1.0;
        for (int i = 0; i < 200; i++) {
            cumulativeProb = cumulativeProb - (float) histogram[i].count/ (float) windValues.length;

            if (cumulativeProb < 0.0){
                break;
            }
            histogram[i].cumProbability = (float)cumulativeProb;
        }



        //Ordinary Least Squares (step 4)
        Float num = 0.0f;
        Float den = 0.0f;
        for (int j = 0; j < 200; j++) {
            if (histogram[j].cumProbability <= 0.01)
                break;
            num = num - (float)Math.log(histogram[j].cumProbability);
            den = den + histogram[j+1].interval;
        }
        Float K = num / den;

        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(new File("cumProbability.txt"));
            printWriter.println(userDefinedInterval + " " + K);
            for (int i = 0; i < 200; i++){
                //Write out j and histogram[j].cumProbabilty to the file to cumProbablity.txt
                //printWriter.println(i + " " + (Float.valueOf(histogram[i].cumProbability)));
                printWriter.println(histogram[i].interval + " " + (Float.valueOf(histogram[i].cumProbability)));
            }
            printWriter.close();
        }
        catch (FileNotFoundException f) {
            System.out.println("File not found.");
        }


        boolean continueLoop = true;

        while (continueLoop){
            String userInput = "";

            System.out.println("Enter ‘less’, ‘greaterEq’, or ‘q’ to quit: ");
            boolean validInput = false;
            while (!validInput){
                userInput = scanner.next();
                if (userInput.equals("less") || userInput.equals("greaterEq") || userInput.equals("q")){
                    validInput = true;
                }
                else{
                    System.out.println("Incorrect input. Please enter ‘less’, ‘greaterEq’, or ‘q’ to quit");
                }

            }

            float windSpeed = 0;
            System.out.println("Enter windspeed: ");
            boolean validSpeed = false;
            while (!validSpeed){
                try{
                    windSpeed = scanner.nextFloat();
                    if (windSpeed >= 0.0){
                        validSpeed = true;
                    }
                    else{
                        System.out.println("Invalid input. Please enter wind speed as positive number");
                    }
                }
                catch (Exception e){
                    System.out.println("Invalid input. Please enter wind speed as float (0.0)");
                }


            }


            if (userInput.equals("less")){
                Float prob = (float)(1.0 - Math.exp(-K * Math.pow(windSpeed, 2)));
                System.out.println("Probability wind speed < " + windSpeed + " is " + prob);
            }
            else if (userInput.equals("greaterEq")){
                Float prob = (float)(Math.exp(-K * Math.pow(windSpeed, 2)));
                System.out.println("Probability wind speed >= " + windSpeed + " is " + prob);
            }
            else{
                continueLoop = false;
            }


        }
    }
}


