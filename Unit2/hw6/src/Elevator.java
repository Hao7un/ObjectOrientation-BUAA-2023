package src;

import java.util.Iterator;
import java.util.ArrayList;

public class Elevator extends Thread {
    public static final int OPENTIME = 200;
    public static final int CLOSETIME = 200;
    public static final int UP = 1;
    public static final int DOWN = 2;

    private final int id;
    private int passengerNum;
    private int nowFloor;
    private int targetFloor;
    private int direction;
    private int capacity;
    private final double movetime;
    private  ArrayList<Passenger> passengers;
    private PassengerQueue waitQueue;
    private PassengerQueue parallelQueue; // the Queue where people wait to get in the elevator
    private Strategy strategy;

    public Elevator(PassengerQueue parallelQueue,PassengerQueue waitQueue,int id,
                    int startFloor,int capacity,double movetime) {
        this.passengerNum = 0;
        this.nowFloor = startFloor;
        this.id = id;
        this.direction = UP;
        this.targetFloor = -1;
        this.passengers = new ArrayList<>();
        this.parallelQueue = parallelQueue;
        this.capacity = capacity;
        this.movetime = movetime;
        this.waitQueue = waitQueue;
        this.strategy = new Strategy();
    }

    public void open() {
        OutputThread.println("OPEN" + "-" + nowFloor + "-" + id);
    }

    public void close() {
        OutputThread.println("CLOSE" + "-" + nowFloor + "-" + id);
    }

    public void in() {
        synchronized (parallelQueue) {
            for (Iterator<Passenger> it = parallelQueue.getPassengers().iterator();
                 it.hasNext(); ) {
                Passenger passenger = it.next();
                if (passenger.getFromFloor() == nowFloor && passenger.getDirection() == direction
                        && !isFull()) {
                    passengerNum += 1;
                    this.passengers.add(passenger);
                    OutputThread.println("IN" + "-" + passenger.getId() + "-" +
                            nowFloor + "-" + id);
                    it.remove();
                }
            }
            parallelQueue.notifyAll();
        }
    }

    public void out() {
        for (Iterator<Passenger> it = passengers.iterator(); it.hasNext();) {
            Passenger passenger = it.next();
            if (passenger.getToFloor() == nowFloor) {
                passengerNum -= 1;
                OutputThread.println("OUT" + "-" + passenger.getId() + "-" + nowFloor + "-" + id);
                it.remove();
            }
        }
    }

    public void move() {
        if (direction == UP) {
            nowFloor += 1;
        } else if (direction == DOWN) {
            nowFloor -= 1;
        }
        OutputThread.println("ARRIVE" + "-" + nowFloor + "-" + id);
    }

    public void maintain() {
        for (Iterator<Passenger> it = parallelQueue.getPassengers().iterator(); it.hasNext();) {
            Passenger passenger = it.next();
            waitQueue.addPassenger(passenger);
            it.remove();
        }
        int flag = 0;
        if (!passengers.isEmpty()) {
            open();
            waitTime(OPENTIME + CLOSETIME);
            for (Iterator<Passenger> it = passengers.iterator(); it.hasNext();) {
                Passenger passenger = it.next();
                Passenger temp = new Passenger(passenger.getId(),nowFloor,passenger.getToFloor());
                waitQueue.addPassenger(temp);
                OutputThread.println("OUT" + "-" + passenger.getId() + "-" + nowFloor + "-" + id);
                it.remove();
            }
            flag = 1;
        }
        if (flag == 1) {
            close();
        }
        OutputThread.println("MAINTAIN_ABLE-" + id);
    }

    public boolean isFull() {
        return passengerNum == capacity;
    }

    private void waitTime(int time) {
        try {
            sleep(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean hasPassengerOut() {
        for (Passenger passenger: passengers) {
            if (passenger.getToFloor() == nowFloor) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPassengerIn() {
        synchronized (parallelQueue) {
            for (Passenger passenger :parallelQueue.getPassengers()) {
                if (passenger.getFromFloor() == nowFloor &&
                        passenger.getDirection() == direction) {
                    return true;
                }
            }
            parallelQueue.notifyAll();
        }
        return false;
    }

    public int getDirection() {
        for (Passenger passenger : parallelQueue.getPassengers()) {
            if (passenger.getFromFloor() == nowFloor) {
                return passenger.getDirection();
            }
        }
        return -1;
    }

    @Override
    public void run() {
        while (true) {
            if (passengers.isEmpty() && parallelQueue.isEmpty()
                    && parallelQueue.isEnd()) {
                break;
            }
            int flag = 0; // whether the door has opened
            if (hasPassengerOut()) {
                open(); //open the door
                out();  //let passengers out
                flag = 1; // the door has opened
                waitTime(OPENTIME + CLOSETIME);
            }
            if (hasPassengerIn() && !isFull()) {
                if (flag == 0) { //the door hasn't opened
                    open();
                    waitTime(OPENTIME + CLOSETIME);
                    flag = 1;
                }
                in();
            }
            if (flag == 1) { //the door has opened
                close();
            }
            targetFloor = strategy.getTargetFloor(parallelQueue,passengers,direction,nowFloor);
            if (parallelQueue.isMaintain()) {
                maintain();
                parallelQueue.setEnd(true); //have maintained, should end
                break;
            }
            if (targetFloor != -1) {  // need to move
                if (targetFloor > nowFloor) {
                    direction = UP;
                    waitTime((int)(movetime * 1000));
                    move();
                } else if (targetFloor < nowFloor) {
                    direction = DOWN;
                    waitTime((int)(movetime * 1000));
                    move();
                } else { //targetFloor == nowFloor
                    direction = getDirection();
                }
            } else if (parallelQueue.isEnd()) {
                break;
            } else if (parallelQueue.isMaintain()) {
                maintain();
                parallelQueue.setEnd(true); //have maintained, should end
                break;
            } else { // no need to move
                parallelQueue.queueWait();
            }
        }
    }
}
