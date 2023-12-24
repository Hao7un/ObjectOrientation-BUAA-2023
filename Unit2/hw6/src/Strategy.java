package src;

import java.util.ArrayList;

import static java.lang.Math.abs;

public class Strategy { // look strategy
    public static final int UP = 1;

    public int getTargetFloor(PassengerQueue waitQueue, ArrayList<Passenger> passengers,
                              int direction, int nowFloor) {
        if (passengers.isEmpty()) {
            return look(waitQueue,direction,nowFloor);
        } else {
            return getFarthest(passengers,nowFloor);
        }
    }

    public int look(PassengerQueue waitQueue,int direction,int nowFloor) {
        int target = 0;
        int highest = -1;
        int lowest = 100;
        for (Passenger passenger : waitQueue.getPassengers()) {
            if (passenger.getFromFloor() >= highest) {
                highest = passenger.getFromFloor();
            }
            if (passenger.getFromFloor() <= lowest) {
                lowest = passenger.getFromFloor();
            }
        }
        if (direction == UP) {
            if (highest >= nowFloor) {
                return highest;
            }
            if (lowest < nowFloor) {
                return lowest;
            }
        } else {
            if (lowest <= nowFloor) {
                return lowest;
            }
            if (highest > nowFloor) {
                return highest;
            }
        }
        return -1;
    }

    public int getFarthest(ArrayList<Passenger> passengers,int nowFloor) {
        int max = -1;
        int floor = 0;
        for (Passenger passenger : passengers) {
            if (abs(passenger.getToFloor() - nowFloor) > max) {
                max = abs(passenger.getToFloor() - nowFloor);
                floor = passenger.getToFloor();
            }
        }
        return floor;
    }
}
