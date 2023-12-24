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
    private final int capacity;
    private final double movetime;
    private final ArrayList<Passenger> passengers;
    private final PassengerQueue waitQueue;
    private final PassengerQueue parallelQueue;
    private final Strategy strategy;
    private ServiceTable serviceTable;

    public Elevator(ServiceTable serviceTable, PassengerQueue parallelQueue,
                    PassengerQueue waitQueue, int id, int startFloor,
                    int capacity, double movetime) {
        this.serviceTable = serviceTable;
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
                if (passenger.getDestination() != passenger.getToFloor()) {
                    passenger.setFromFloor(nowFloor);
                    passenger.setToFloor(passenger.getDestination());
                    waitQueue.addPassenger(passenger);
                } else { //finish
                    waitQueue.subTotal();
                }
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
        synchronized (parallelQueue) {
            for (Iterator<Passenger> it = parallelQueue.getPassengers().iterator(); it.hasNext();) {
                Passenger passenger = it.next();
                waitQueue.addPassenger(passenger);
                it.remove();
            }
            parallelQueue.notifyAll();
        }
        int flag = 0;
        if (!passengers.isEmpty()) {
            serviceTable.addService(nowFloor,false);
            open();
            waitTime(OPENTIME + CLOSETIME);
            for (Iterator<Passenger> it = passengers.iterator(); it.hasNext();) {
                Passenger passenger = it.next();
                if (passenger.getDestination() != nowFloor) { //hasn't arrived
                    Passenger temp = new Passenger(passenger.getId(),nowFloor,
                            passenger.getDestination());
                    waitQueue.addPassenger(temp);
                } else { //has arrived
                    waitQueue.subTotal();
                }
                OutputThread.println("OUT" + "-" + passenger.getId() + "-" + nowFloor + "-" + id);
                it.remove();
            }
            flag = 1;
        }
        if (flag == 1) {
            close();
            serviceTable.removeService(nowFloor,false);
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
                    parallelQueue.notifyAll();
                    return true;
                }
            }
            parallelQueue.notifyAll();
            return false;
        }
    }

    public int getDirection() {
        synchronized (parallelQueue) {
            for (Passenger passenger : parallelQueue.getPassengers()) {
                if (passenger.getFromFloor() == nowFloor) {
                    parallelQueue.notifyAll();
                    return passenger.getDirection();
                }
            }
            parallelQueue.notifyAll();
            return -1;
        }
    }

    @Override
    public void run() {
        while (true) {
            if (passengers.isEmpty() && parallelQueue.isEmpty() && parallelQueue.isEnd()) {
                break;
            }
            int openflag = 0; // whether the door has opened
            boolean onlyTake = false;
            if (hasPassengerOut()) {
                serviceTable.addService(nowFloor,false);
                open(); //open the door
                out();  //let passengers out
                openflag = 1; // the door has opened
                waitTime(OPENTIME + CLOSETIME);
            }
            if (hasPassengerIn() && !isFull()) {
                if (openflag == 0) { //only take
                    onlyTake = true;
                    serviceTable.addService(nowFloor,true);
                    open();
                    waitTime(OPENTIME + CLOSETIME);
                    openflag = 1;
                }
                in();
            }
            if (openflag == 1) { //the door has opened
                close();
                serviceTable.removeService(nowFloor,onlyTake);
            }
            synchronized (parallelQueue) {
                targetFloor = strategy.getTargetFloor(parallelQueue,passengers,direction,nowFloor);
                parallelQueue.notifyAll();
            }
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
