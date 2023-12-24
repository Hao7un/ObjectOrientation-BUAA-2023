package src;

import java.util.ArrayList;

public class PassengerQueue {
    private boolean isEnd;
    private final ArrayList<Passenger> passengers;

    public PassengerQueue() {
        this.isEnd = false;
        this.passengers = new ArrayList<>();
    }

    public synchronized void addPassenger(Passenger passenger) {
        passengers.add(passenger);
        notifyAll();
    }

    public synchronized ArrayList<Passenger> getPassengers() {
        notifyAll();
        return passengers;
    }

    public synchronized void setEnd(Boolean end) {
        this.isEnd = end;
        notifyAll();
    }

    public synchronized boolean isEnd() {
        notifyAll();
        return isEnd;
    }

    public synchronized boolean isEmpty() {
        notifyAll();
        return this.passengers.isEmpty();
    }

    public synchronized void queueWait() {
        try {
            wait();
        }  catch (Exception e) {
            e.printStackTrace();
        }
    }
}
