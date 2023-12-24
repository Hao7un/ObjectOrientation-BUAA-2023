package src;

import com.oocourse.elevator3.TimableOutput;
import java.util.ArrayList;

public class MainClass {
    public static void main(String[] args) {
        TimableOutput.initStartTimestamp(); // 初始化时间戳
        PassengerQueue waitQueue = new PassengerQueue(0,0);
        ServiceTable serviceTable = new ServiceTable();
        int [][] accessMap = {
                {0,1,1,1,1,1,1,1,1,1,1}, // 1st floor
                {1,0,1,1,1,1,1,1,1,1,1},
                {1,1,0,1,1,1,1,1,1,1,1},
                {1,1,1,0,1,1,1,1,1,1,1},
                {1,1,1,1,0,1,1,1,1,1,1},
                {1,1,1,1,1,0,1,1,1,1,1},
                {1,1,1,1,1,1,0,1,1,1,1},
                {1,1,1,1,1,1,1,0,1,1,1},
                {1,1,1,1,1,1,1,1,0,1,1},
                {1,1,1,1,1,1,1,1,1,0,1},
                {1,1,1,1,1,1,1,1,1,1,0}, //11th floor
        };
        ArrayList<PassengerQueue> processingQueues = new ArrayList<>();

        for (int i = 1; i <= 6; i++) {
            PassengerQueue parallelQueue = new PassengerQueue(i,2047);
            processingQueues.add(parallelQueue);
            Elevator elevator = new Elevator(serviceTable,parallelQueue,waitQueue,i,1,6,0.4);
            elevator.start(); //start the elevator thread
        }
        Scheduler scheduler = new Scheduler(accessMap,waitQueue,processingQueues);
        scheduler.start();

        InputThread inputThread = new InputThread(accessMap,serviceTable,
                waitQueue,processingQueues);
        inputThread.start();; // start the input thread
    }
}
