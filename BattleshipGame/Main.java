//written by volko028
import java.util.Scanner;

//This class has the main function
public class Main {
    public static void main(String[] args){

        boolean validMode = false;
        String mode = "";

        //while loop ensures valid input of "Standard" or "Expert"
        while (!validMode){
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter mode(Standard or Expert): ");

            mode = scanner.nextLine();  // Read user input
            if (mode.equals(BattleshipBoard.STANDARD) || mode.equals(BattleshipBoard.EXPERT)){
                validMode = true;
            }
        }

        //board instantiated
        BattleshipBoard board = new BattleshipBoard(mode);

        //ships placed on board (different placements depending on mode)
        if (mode.equals("Standard")){
            board.placeShip(5);
            board.placeShip(4);
            board.placeShip(3);
            board.placeShip(3);
            board.placeShip(2);
            board.displayBoard();
        }
        else{
            board.placeShip(5);
            board.placeShip(5);
            board.placeShip(4);
            board.placeShip(4);
            board.placeShip(3);
            board.placeShip(3);
            board.placeShip(3);
            board.placeShip(3);
            board.placeShip(2);
            board.placeShip(2);
            board.displayBoard();
        }

        //while loop continues until the game is over (all ships sunk)
        while (!board.isGameOver()){
            //Scanner takes in input("Drone", "Missile", "Print", or coordinates "x,y")
            Scanner scanner = new Scanner(System.in);
            String nextLine = scanner.nextLine();

            //try and catch statement catches invalid inputs
            try {
                if (nextLine.equals("Drone")){
                    //Drone power
                    System.out.println("Would you like to scan row or column? Type r for row and c for column.");
                    boolean validInput = false ;
                    String droneType = "";
                    //while loop ensures valid input for row/column (either "r" or "c")
                    while (!validInput){
                        droneType = scanner.nextLine();
                        if ((!droneType.equals("r")) && (!droneType.equals("c"))){
                            System.out.println("Invalid input. Please type r for row or c for column.");
                        }
                        else{
                            validInput = true;
                        }
                    }
                    System.out.println("Which row or column would you like to scan?");
                    boolean validInputNum = false ;
                    int rowColumnInt = 0;
                    //while loop ensures valid input for row/column index (must be greater than O and smaller than board size)
                    while (!validInputNum){
                        rowColumnInt = scanner.nextInt();
                        if ((rowColumnInt >= 0) && rowColumnInt < board.getBoardSize()){
                            validInputNum = true;
                        }
                        else{
                            System.out.println("Invalid input. Please type a number within the boundaries of the board.");
                        }
                    }
                    //Drone method called. First parameter: r/c. Second parameter: index for that row/column.
                    board.drone(droneType, rowColumnInt);

                }
                else if (nextLine.equals("Missile")){
                    //Missile power
                    System.out.println("Which coordinates would you like to fire a missile at?");
                    boolean validInputNum = false ;
                    int x = 0;
                    int y = 0;
                    //while loop ensures valid input for coordinates (x and y must be within the board)
                    while (!validInputNum){
                        String coordinateInput = scanner.nextLine();
                        String[] coordinates = coordinateInput.split(",");
                        x = Integer.parseInt(coordinates[0]);
                        y = Integer.parseInt(coordinates[1]);
                        if ((x >= 0) && (y >= 0) && (x < board.getBoardSize()) && (y < board.getBoardSize())){
                            validInputNum = true;
                        }
                        else{
                            System.out.println("Invalid input. Please type coordinates within the boundaries of the board.");
                        }
                        //missile method called
                        board.missile(x, y);
                        //board displayed
                        board.displayBoard();
                    }

                }
                else if (nextLine.equals("Print")){
                    //Prints board(including ships)
                    board.printBoard();
                }
                else {
                    //takes in x,y coordinates to fire on board
                    String[] coordinates = nextLine.split(",");
                    //calls fire method
                    board.fire(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]), false);
                    //displays board
                    board.displayBoard();
                }

            }
            catch(Exception e){
                //exception for invalid coordinates
                e.printStackTrace();
                System.out.println("Invalid coordinates entered: "+nextLine);
                System.out.println("Enter valid coordinates \"x,y\".");
            }
        }

        if (board.isGameOver()){
            //prints game over message, turns, and cannon shots when while loop/game ends
            System.out.println("Game over. Total number of turns: " + board.getTurnCounter() + " Total number of cannon shots: " + board.getCannonCounter());
        }


    }
}
