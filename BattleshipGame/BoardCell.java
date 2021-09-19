//written by volko028

//This class is responsible for keeping track of the cells on the battleship board.
public class BoardCell {
    //class variables declared
    private Ship ship;
    boolean wasAttacked;

    //Constructor: initializes boolean
    public BoardCell (boolean wasAttacked){
        this.ship = null;
        this.wasAttacked = wasAttacked;
    }

    //Constructor: assigns ship to cell when ship is placed
    public BoardCell (Ship ship){
        this.ship = ship;
        wasAttacked = false;
    }

    //checks if cell is occupied by a ship
    public boolean isOccupied (){
        if (ship != null){
            return true;
        }
        else{
            return false;
        }
    }

    //checks if cell was previously attacked
    public boolean wasAttacked (){
        return wasAttacked;
    }

    //updates wasAttacked boolean
    public void attack (){
        if (!wasAttacked){
            wasAttacked = true;
            if ((ship != null)) {
                ship.incrementHits();
            }
        }
    }

    //checks if ship is sunk (returns boolean)
    public boolean isFatalBlow (){
        return ship.isSunk();
    }

    //getter for ship
    public Ship getShip() {
        return ship;
    }
}
