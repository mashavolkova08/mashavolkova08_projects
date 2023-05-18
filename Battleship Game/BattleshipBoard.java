//written by volko028
import java.util.Random;

public class BattleshipBoard {

    //class variables declared/initialized
    public static final String STANDARD = "Standard";
    public static final String EXPERT = "Expert";
    private BoardCell[][] board;
    private boolean expert;
    private int boardSize;
    private static final String HORIZONTAL = "Horizontal";
    private static final String VERTICAL = "Vertical";
    private int turnCounter = 0;
    private int numShips = 0;
    private int cannonCounter = 0;
    private int droneCounter = 0;
    private int missileCounter = 0;
    private int numShip = 0;

    //Constructor: takes in standard/expert mode, sets board size, instantiates board array
    public BattleshipBoard(String mode) {
        if (mode.equals("Standard")) {
            expert = false;
            boardSize = 8;
        } else if (mode.equals("Expert")) {
            expert = true;
            boardSize = 12;
        }
        board = new BoardCell[boardSize][boardSize];
    }

    //getter for board size
    public int getBoardSize (){
        return boardSize;
    }

    //getter for number of turns
    public int getTurnCounter (){
        return turnCounter;
    }

    //getter for number of cannon shots
    public int getCannonCounter() {
        return cannonCounter;
    }

    //method to place ships on board
    public void placeShip(int shipLength) {
        // selected horizontal/vertical direction randomly
        String[] arr = {HORIZONTAL, VERTICAL};
        Random rand = new Random();
        String direction = arr[rand.nextInt(arr.length)];
        //ship number assignment
        numShip++;
        //ship instantiated (Ship class)
        Ship ship = new Ship(shipLength, numShip);

        boolean check = false;
        int x;
        int y;
        int i;

        //placement for horizontal ships
        if (direction.equals(HORIZONTAL)) {
            //while loop generates random integers for x and y until they produce a ship within the board
            while (!check) {
                //random integers for x and y
                x = rand.nextInt(boardSize);
                y = rand.nextInt(boardSize);
                //if statement checks if the next few cells are within the board and vacant to place a ship
                if (((x + shipLength) < boardSize) && checkVacancy(x, y, shipLength, direction)) {
                    //for loop places assigns cells to a ship(BoardCell class used)
                    for (i = 0; i < shipLength; i++) {
                        board[x + i][y] = new BoardCell(ship);
                    }
                    //ends while loop
                    check = true;
                }
            }
        }
        //placement for vertical ships
        else if (direction.equals(VERTICAL)) {
            //while loop generates random integers for x and y until they produce a ship within the board
            while (!check) {
                //random integers for x and y
                x = rand.nextInt(boardSize);
                y = rand.nextInt(boardSize);
                //if statement checks if the next few cells are within the board and vacant to place a ship
                if (((y + shipLength) < boardSize) && checkVacancy(x, y, shipLength, direction)) {
                    //for loop places assigns cells to a ship(BoardCell class used)
                    for (i = 0; i < shipLength; i++) {
                        board[x][y + i] = new BoardCell(ship);
                    }
                    //ends while loop
                    check = true;
                }
            }

        }
        numShips ++;
    }

    // method to check if cells are null when ships are placed on board
    private boolean checkVacancy(int startX, int startY, int shipLength, String direction) {
        //checks if cells are vacant when horizontal ship placed
        if (direction.equals(HORIZONTAL)) {
            //for loop used to check if cells are null
            for (int i = 0; i < shipLength; i++) {
                if (board[startX + i][startY] != null) {
                    return false;
                }
            }
        }
        //checks if cells are vacant when vertical ship placed
        else {
            //for loop used to check if cells are null
            for (int i = 0; i < shipLength; i++) {
                if (board[startX][startY + i] != null) {
                    return false;
                }
            }
        }
        //returns true if cells are vacant, otherwise returns false
        return true;
    }

    //method to fire at given coordinates
    //boolean value checks to see if missile power is being used
    public void fire(int x, int y, boolean missileFired) {
        //turn count and cannon counter is not used for missile power
        if (!missileFired){
            turnCounter++;
            cannonCounter++;
        }

        if (board[x][y] == null){
            //if board is null: "Miss" is printed and board cell is marked as attacked
            System.out.println("Miss");
            board[x][y] = new BoardCell(true);
        }
        else if (board[x][y].wasAttacked() || x >= boardSize || y >= boardSize){
            //checks to see if cell was previously attacked, penalty if missile power not being used
            if (!missileFired){
                System.out.println("Penalty");
                turnCounter++;
            }
        }
        else if (board[x][y].isOccupied()){
            //checks if cell is occupied by ship then attacks cell
            board[x][y].attack();
            //if statement checks if ship is sunk or hit
            if (board[x][y].isFatalBlow()){
                System.out.println("Sunk");
                numShips --;
            }
            else{
                System.out.println("Hit");
            }
        }
    }

    //method for drone power
    public void drone(String droneType, int rowColumnInt){
        int occupiedCounter = 0;
        turnCounter++;
        droneCounter++;
        //checks if drone has been used max amount of times
        if (!expert && (droneCounter > 1)) {
            System.out.println("Drone has been used a max amount of times.");
        }
        else if (expert && (droneCounter > 2)) {
            System.out.println("Drone has been used a max amount of times.");
        }
        else{
            //if statement differentiates between searching row or column
            if (droneType.equals("r")){
                //searches row using for loop, occupied counter increased by one if a cell is occupied
                for (int i=0; i< boardSize; i++){
                    if ((board[rowColumnInt][i] != null) && board[rowColumnInt][i].isOccupied()){
                        occupiedCounter++;
                    }
                }
                System.out.println("Drone has scanned " + occupiedCounter + " targets in specified area.");
            }
            else{
                //searches column using for loop, occupied counter increased by one if a cell is occupied
                for (int i=0; i< boardSize; i++){
                    if ((board[i][rowColumnInt] != null) && board[i][rowColumnInt].isOccupied()){
                        occupiedCounter++;
                    }
                }
                //message for number of occupied cells printed
                System.out.println("Drone has scanned " + occupiedCounter + " targets in specified area.");
            }
        }

        }

    //method for missile power
    public void missile(int x, int y){
        missileCounter++;
        turnCounter++;
        //checks if missile power is used max amount of times
        if (!expert && (missileCounter > 1)) {
            System.out.println("Missile has been used a max amount of times.");
        }
        else if (expert && (missileCounter > 2)) {
            System.out.println("Missile has been used a max amount of times.");
        }
        else {
            //missile fired for all 9 cells in firing range (only fires if coordinate is within the board)
            //fire method used, parameter for missileFired is true
            fire(x,y, true);
            if (((x-1)>=0) && ((x-1)<boardSize) && ((y-1)>=0) && ((y-1)<boardSize)){
                fire(x-1,y-1, true);
            }
            if (((x)>=0) && ((x)<boardSize) && ((y-1)>=0) && ((y-1)<boardSize)){
                fire(x,y-1, true);
            }
            if (((x+1)>=0) && ((x+1)<boardSize) && ((y-1)>=0) && ((y-1)<boardSize)){
                fire(x+1,y-1, true);
            }
            if (((x-1)>=0) && ((x-1)<boardSize) && ((y)>=0) && ((y)<boardSize)){
                fire(x-1,y, true);
            }
            if (((x+1)>=0) && ((x+1)<boardSize) && ((y)>=0) && ((y)<boardSize)){
                fire(x+1,y, true);
            }
            if (((x-1)>=0) && ((x-1)<boardSize) && ((y+1)>=0) && ((y+1)<boardSize)){
                fire(x-1,y+1, true);
            }
            if (((x)>=0) && ((x)<boardSize) && ((y+1)>=0) && ((y+1)<boardSize)){
                fire(x,y+1, true);
            }
            if (((x+1)>=0) && ((x+1)<boardSize) && ((y+1)>=0) && ((y+1)<boardSize)){
                fire(x+1,y+1, true);
            }

        }
    }

    //method to check if game is over
    //ends while loop in main method
    public boolean isGameOver (){
        if (numShips == 0){
            return true;
        }
        else {
            return false;
        }
    }

    //method to print board (including ships)
    public void printBoard() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == null){
                    System.out.print("-");
                }
                else if (!board[i][j].isOccupied() && !board[i][j].wasAttacked()){
                    System.out.print("-");
                }
                else if (!board[i][j].isOccupied() && board[i][j].wasAttacked()){
                    System.out.print("0");
                }
                else if (board[i][j].isOccupied() && !board[i][j].wasAttacked()){
                    System.out.print(board[i][j].getShip().getShipID());
                }
                else if (board[i][j].isOccupied() && board[i][j].wasAttacked()){
                    System.out.print("X");
                }

            }
            System.out.println();
        }
    }

    //method to display board after each turn
    public void displayBoard() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == null){
                    System.out.print("-");
                }
                else if (!board[i][j].isOccupied() && !board[i][j].wasAttacked()){
                    System.out.print("-");
                }
                else if (!board[i][j].isOccupied() && board[i][j].wasAttacked()){
                    System.out.print("0");
                }
                else if (board[i][j].isOccupied() && !board[i][j].wasAttacked()){
                    System.out.print("-");
                }
                else if (board[i][j].isOccupied() && board[i][j].wasAttacked()){
                    System.out.print("X");
                }

            }
            System.out.println();
        }
    }
}
