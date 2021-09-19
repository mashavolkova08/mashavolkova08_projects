// Name: Masha Volkova
// x500: volko028

import java.util.Random;
import java.util.Scanner;

public class MyMaze{
    //Variables and maze array declared
    Cell[][] maze;
    private static final int LEFT = 1;
    private static final int UP = 2;
    private static final int RIGHT = 3;
    private static final int DOWN = 4;
    private int numRows;
    private int numCols;

    //Constructor
    public MyMaze(int rows, int cols) {
        numRows = rows;
        numCols = cols;
        maze = new Cell[numRows][numCols];

        //Cells created based on matrix dimensions
        for (int i = 0; i < numRows; i++){
            for (int j = 0; j < numCols; j++){
                maze[i][j] = new Cell();
            }
        }
    }

    //Creates new maze
    public static MyMaze makeMaze(int rows, int cols) {
        //New maze created
        MyMaze newMaze = new MyMaze(rows,cols);

        //Stack initialized at start index {0,0}
        Stack1Gen<int[]> stack = new Stack1Gen <int[]>();
        stack.push(new int[]{0,0});

        //Top right cell marked as visited (start of maze)
        newMaze.maze[0][0].setVisited(true);
        // Right boundary deleted at bottom right cell (exit of maze)
        newMaze.maze[rows-1][cols-1].setRight(false);
        int[] index;

        //While loop used to create the maze, iterates until stack is empty
        while (!stack.isEmpty()){
            //Top element retrieved from stack
            index = stack.top();

            //If statement checks to see if there is a deadend using isDeadend() helper method
            //If there is a deadend, top element is removed from the stack
            if (!newMaze.isDeadend(index[0], index[1])){
                boolean validMove = false;
                int moveDirection;
                //While loop iterates until a valid move is generated (to make maze)
                while (!validMove){
                    //Helper method newTurn() used to randomly choose new direction for next cell
                    moveDirection = newTurn();

                    //If statement used to check validity of neighboring cells
                    //Conditions: randomly generated direction, valid index in matrix, if cell has been visited

                    //Left neighbor
                    if (moveDirection == LEFT && index[1]-1>=0 && !newMaze.maze[index[0]][index[1]-1].getVisited()){
                        //Neighboring cell's index added to stack
                        stack.push(new int[]{index[0],index[1]-1});
                        //Neighboring cell marked as visited
                        newMaze.maze[index[0]][index[1]-1].setVisited(true);
                        //Wall removed between current and neighboring cell
                        newMaze.maze[index[0]][index[1]-1].setRight(false);
                        //Ends while loop
                        validMove = true;
                    }
                    //Upward neighbor
                    else if (moveDirection == UP && index[0]-1>=0 && !newMaze.maze[index[0]-1][index[1]].getVisited()){
                        //Neighboring cell's index added to stack
                        stack.push(new int[]{index[0]-1,index[1]});
                        //Neighboring cell marked as visited
                        newMaze.maze[index[0]-1][index[1]].setVisited(true);
                        //Wall removed between current and neighboring cell
                        newMaze.maze[index[0]-1][index[1]].setBottom(false);
                        //Ends while loop
                        validMove = true;
                    }
                    //Right neighbor
                    else if (moveDirection == RIGHT && index[1]+1 < cols && !newMaze.maze[index[0]][index[1]+1].getVisited()){
                        //Neighboring cell's index added to stack
                        stack.push(new int[]{index[0],index[1]+1});
                        //Neighboring cell marked as visited
                        newMaze.maze[index[0]][index[1]+1].setVisited(true);
                        //Wall removed between current and neighboring cell
                        newMaze.maze[index[0]][index[1]].setRight(false);
                        //Ends while loop
                        validMove = true;
                    }
                    //Downward neighbor
                    else if (moveDirection == DOWN && index[0]+1 < rows && !newMaze.maze[index[0]+1][index[1]].getVisited()){
                        //Neighboring cell's index added to stack
                        stack.push(new int[]{index[0]+1,index[1]});
                        //Neighboring cell marked as visited
                        newMaze.maze[index[0]+1][index[1]].setVisited(true);
                        //Wall removed between current and neighboring cell
                        newMaze.maze[index[0]][index[1]].setBottom(false);
                        //Ends while loop
                        validMove = true;
                    }

                }

            }
            else{
                //If there is a deadend, current cell's index is removed from the top of the stack
                stack.pop();
            }

        }
        //Resets visited boolean value for all cells to false
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < cols; j++){
                newMaze.maze[i][j].setVisited(false);
            }
        }
        return newMaze;
    }

    //Method generates random number to determine direction of next turn (helper method for makeMaze)
    public static int newTurn (){
        //Random integer between 1 and 4 generated and returned
        Random rand = new Random();
        int num = rand.nextInt(4)+1;
        return num;
    }

    //Checks to see if there is a deadend (helper method for makeMaze)
    public boolean isDeadend (int row, int col){
        //Check left
        if (col-1>0 && !maze[row][col-1].getVisited()){
            return false;
        }
        //Check right
        else if (col+1 < numCols && !maze[row][col + 1].getVisited()){
            return false;
        }
        //Check up
        else if (row-1 >0 && !maze[row-1][col].getVisited()){
            return false;
        }
        //Check down
        else if (row+1< numRows && !maze[row+1][col].getVisited()){
            return false;
        }
        else {
            return true;
        }
    }

    //Prints maze
    public void printMaze(boolean path) {
        //Prints the top border of the maze
        printTopBorder();
        //For loop used to print each row of maze (cells and bottom border)
        for (int i = 0; i < numRows; i++) {
            int index = i;
            printCellRow(index, path);
            printBottomRow(index);
        }
    }

    //Prints top border (helper method for printMaze)
    public void printTopBorder(){
        System.out.print("|");
        //For loop used to print top walls
        for (int j = 0; j < numCols; j++){
            System.out.print("---|");
        }
        System.out.println("");
    }

    //Prints cells containing stars and right borders (helper method for printMaze)
    public void printCellRow(int row, boolean path){
        //Prints wall on the left of the leftmost cell it isn't in the first row
        if (row!=0){
            System.out.print("|");
        }
        else {
            System.out.print(" ");
        }

        //For loop used to print stars/empty cells and right borders if they exist
        for (int j = 0; j < numCols; j++){
            //Prints cell content
            if (path && maze[row][j].getVisited()){
                System.out.print(" * ");
            }
            else{
                System.out.print("   ");
            }

            //Prints right border
            if (maze[row][j].getRight()){
                System.out.print("|");
            }
            else{
                System.out.print(" ");
            }
        }

        System.out.println("");
    }


    //Prints bottom boundary for cells (helper method for printMaze)
    public void printBottomRow (int row){
        //Leftmost wall printed
        System.out.print("|");
        //For loop used to print the rest of border (checks to see if bottom wall exists)
        for (int j = 0; j < numCols; j++){
            if (maze[row][j].getBottom()){
                System.out.print("---|");
            }
            else {
                System.out.print("   |");
            }
        }
        System.out.println("");
    }

    //Finds solution for maze
    public void solveMaze() {
        //Queue initialized at start index {0,0}
        Q1Gen<int[]> queue = new Q1Gen<int[]>();
        queue.add(new int[] {0,0});

        //While loop iterates until queue is empty
        while (!queue.isEmpty()){
            //Front index de-queued
            int[] frontIndex = queue.remove();
            //Cell corresponding to front index is marked as visited
            maze[frontIndex[0]][frontIndex[1]].setVisited(true);

            //If statement checks if the maze is solved (front index is the finish point)
            if (frontIndex[0] == numRows-1 && frontIndex[1] == numCols-1){
                //maze solved
                break;
            }
            //En-queues left neighbor
            //Conditions: index inside matrix, no right wall (reachable), cell not visited
            if (frontIndex[1]-1>=0 && !maze[frontIndex[0]][frontIndex[1]-1].getRight() && !maze[frontIndex[0]][frontIndex[1]-1].getVisited()){
                queue.add(new int[]{frontIndex[0],frontIndex[1]-1});
            }
            //En-queues upper neighbor
            //Conditions: index inside matrix, no bottom wall (reachable), cell not visited
            if (frontIndex[0]-1>=0 && !maze[frontIndex[0]-1][frontIndex[1]].getBottom() && !maze[frontIndex[0]-1][frontIndex[1]].getVisited()){
                queue.add(new int[]{frontIndex[0]-1,frontIndex[1]});
            }
            //En queues right neighbor
            //Conditions: index inside matrix, no right wall (reachable), cell not visited
            if (frontIndex[1]+1 < numCols && !maze[frontIndex[0]][frontIndex[1]].getRight() && !maze[frontIndex[0]][frontIndex[1]+1].getVisited()){
                queue.add(new int[]{frontIndex[0],frontIndex[1]+1});
            }
            //En-queues downward neighbor
            //Conditions: index inside matrix, no bottom wall (reachable), cell not visited
            if (frontIndex[0]+1 < numRows && !maze[frontIndex[0]][frontIndex[1]].getBottom() && !maze[frontIndex[0]+1][frontIndex[1]].getVisited()){
                queue.add(new int[]{frontIndex[0]+1,frontIndex[1]});
            }
        }
        //Maze printed
        printMaze(true);
    }

    //Main method
    public static void main(String[] args){
        //Variables initialized
        boolean validDimensions = false;
        int rowNum = 0;
        int colNum = 0;

        //While loop ensures valid dimensions are entered (number of rows > 0, number of columns > 0)
        while (!validDimensions){
            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter number of rows for maze: ");
            rowNum = scanner.nextInt();
            System.out.println("Enter number of columns for maze: ");
            colNum = scanner.nextInt();

            if (rowNum > 0 && colNum > 0){
                validDimensions = true;
            }
            else{
                System.out.println("Invalid dimensions entered. Please try again.");
            }
        }

        //Maze instantiated
        MyMaze maze = makeMaze(rowNum,colNum);

        //Maze printed
        System.out.println("Maze:");
        maze.printMaze(true);

        //Maze solution printed
        System.out.println("\nMaze solution:");
        maze.solveMaze();
    }
}
