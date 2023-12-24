package src;

public class Passenger {
    private final int id;
    private final int fromFloor;
    private final int toFloor;

    private final int direction;

    Passenger(int id,int fromFloor,int toFloor) {
        this.id = id;
        this.fromFloor = fromFloor;
        this.toFloor = toFloor;
        if (fromFloor < toFloor) {
            direction = 1; //upward
        } else {
            direction = 2; //downward
        }
    }

    public int getId() {
        return this.id;
    }

    public int getFromFloor() {
        return this.fromFloor;
    }

    public int getToFloor() {
        return this.toFloor;
    }

    public int getDirection() {
        return this.direction;
    }
}
