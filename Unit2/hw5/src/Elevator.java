package src;

import java.util.ArrayList;
import java.util.Iterator;

public class Elevator extends Thread {
    public static final int MOVETIME = 400;
    public static final int OPENTIME = 200;
    public static final int CLOSETIME = 200;
    public static final int CAPACITY = 6;
    public static final int UP = 1;
    public static final int DOWN = 2;

    private final int id;
    private int passengerNum;
    private int nowFloor;
    private int targetFloor;
    private int direction;
    private  ArrayList<Passenger> passengers;
    private PassengerQueue waitQueue; // the Queue where people wait to get in the elevator
    private Strategy strategy;

    public Elevator(PassengerQueue waitQueue,int id) {
        this.passengerNum = 0;
        this.nowFloor = 1;
        this.id = id;
        this.direction = UP;
        this.targetFloor = -1;
        this.passengers = new ArrayList<>();
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
        for (Iterator<Passenger> it = waitQueue.getPassengers().iterator(); it.hasNext(); ) {
            Passenger passenger = it.next();
            if (passenger.getFromFloor() == nowFloor && passenger.getDirection() == direction
                && !isFull()) {
                passengerNum += 1;
                this.passengers.add(passenger);
                OutputThread.println("IN" + "-" + passenger.getId() + "-" + nowFloor + "-" + id);
                it.remove();
            }
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

    public boolean isFull() {
        return passengerNum == CAPACITY;
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
        for (Passenger passenger :waitQueue.getPassengers()) {
            if (passenger.getFromFloor() == nowFloor &&
                passenger.getDirection() == direction) {
                return true;
            }
        }
        return false;
    }

    public int getDirection() {
        for (Passenger passenger : waitQueue.getPassengers()) {
            if (passenger.getFromFloor() == nowFloor) {
                return passenger.getDirection();
            }
        }
        return -1;
    }

    @Override
    public void run() {
        while (true) {
            if (passengers.isEmpty() && waitQueue.isEmpty()
                && waitQueue.isEnd()) {
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

            targetFloor = strategy.getTargetFloor(waitQueue,passengers,direction,nowFloor);
            if (targetFloor != -1) {  // need to move
                if (targetFloor > nowFloor) {
                    direction = UP;
                    waitTime(MOVETIME);
                    move();
                } else if (targetFloor < nowFloor) {
                    direction = DOWN;
                    waitTime(MOVETIME);
                    move();
                } else { //targetFloor == nowFloor
                    direction = getDirection();
                }
            } else if (waitQueue.isEnd()) {
                break;
            } else { // no need to move
                waitQueue.queueWait();
            }
        }
    }
}
