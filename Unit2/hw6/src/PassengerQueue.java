package src;

import java.util.ArrayList;

public class PassengerQueue {
    private int elevatorId;
    private boolean isEnd;
    private boolean isMaintain;
    private final ArrayList<Passenger> passengers;

    public PassengerQueue(Integer id) {
        this.isEnd = false;
        this.isMaintain = false;
        this.elevatorId = id;
        this.passengers = new ArrayList<>();
    }

    public synchronized void addPassenger(Passenger passenger) {
        passengers.add(passenger);
        notifyAll();
    }

    public synchronized Passenger getOnePassenger() {
        if (passengers.isEmpty() && !isEnd) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (passengers.isEmpty()) {
            return null;
        }
        Passenger passenger = passengers.get(0);
        passengers.remove(0);
        notifyAll();
        return passenger;
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
        return isEnd;
    }

    public synchronized void setMaintain(Boolean maintain) {
        this.isMaintain = maintain;
        notifyAll();
    }

    public synchronized boolean isMaintain() {
        return isMaintain;
    }

    public synchronized int getElevatorId() {
        return elevatorId;
    }

    public synchronized boolean isEmpty() {
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
