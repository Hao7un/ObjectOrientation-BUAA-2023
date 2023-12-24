package src;

import java.util.ArrayList;

public class Scheduler extends Thread {
    private final PassengerQueue waitQueue;
    private final ArrayList<PassengerQueue> processingQueues;  //Queues

    public Scheduler(PassengerQueue waitQueue, ArrayList<PassengerQueue> processingQueues) {
        this.waitQueue = waitQueue;
        this.processingQueues = processingQueues;
    }

    @Override
    public void run() {
        int cnt = 0;
        while (true) {
            if (waitQueue.isEmpty() && waitQueue.isEnd()) {
                for (PassengerQueue processingQueue : processingQueues) {
                    processingQueue.setEnd(true);
                }
                return;
            }
            Passenger passenger = waitQueue.getOnePassenger();
            if (passenger == null) {
                continue;
            }
            // get a passenger
            while (processingQueues.get(cnt).isMaintain()) { //the elevator is not maintained
                cnt  = (cnt + 1) % processingQueues.size();
            }
            processingQueues.get(cnt).addPassenger(passenger);
            cnt  = (cnt + 1) % processingQueues.size();
        }
    }
}
