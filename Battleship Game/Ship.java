//written by volko028

//This class is responsible for keeping track of each ship.
public class Ship {
    //class variables declared
    private int shipLength;
    private int numHits;
    private int numShip;

    //Constructor: initializes ship length, number of hits, and ship number for each ship
    public Ship (int shipLength, int numShip){
        this.shipLength = shipLength;
        this.numHits = 0;
        this.numShip = numShip;
    }

    //increments hits when ship is attacked
    public void incrementHits(){
        numHits++;
    }

    //checks if ship is sunk
    public boolean isSunk(){
        if (numHits == shipLength){
            return true;
        }
        else {
            return false;
        }
    }

    //returns ship ID (for print method in BattleshipBoard class)
    public int getShipID(){
        return numShip;
    }

}
