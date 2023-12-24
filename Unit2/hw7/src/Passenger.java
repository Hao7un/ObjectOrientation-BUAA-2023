package src;

public class Passenger {
    private final int id;
    private int fromFloor;
    private int toFloor;

    private final int destination;

    public Passenger(int id,int fromFloor,int toFloor) {
        this.id = id;
        this.fromFloor = fromFloor;
        this.toFloor = toFloor;
        this.destination = toFloor;
    }

    public int getId() {
        return this.id;
    }

    public void setFromFloor(int fromFloor) {
        this.fromFloor = fromFloor;
    }

    public void setToFloor(int toFloor) {
        this.toFloor = toFloor;
    }

    public int getFromFloor() {
        return this.fromFloor;
    }

    public int getToFloor() {
        return this.toFloor;
    }

    public int getDestination() { return this.destination; }

    public int getDirection() {
        if (fromFloor > toFloor) {
            return 2;
        } else {
            return 1;
        }
    }
}
