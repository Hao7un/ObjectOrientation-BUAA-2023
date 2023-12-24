package src;

import com.oocourse.elevator2.TimableOutput;
import java.util.ArrayList;

public class MainClass {
    public static void main(String[] args) {
        TimableOutput.initStartTimestamp(); // 初始化时间戳
        PassengerQueue waitQueue = new PassengerQueue(0);
        ArrayList<PassengerQueue> processingQueues = new ArrayList<>();

        for (int i = 1; i <= 6; i++) {
            PassengerQueue parallelQueue = new PassengerQueue(i);
            processingQueues.add(parallelQueue);
            Elevator elevator = new Elevator(parallelQueue,waitQueue,i,1,6,0.4);
            elevator.start(); //start the elevator thread
        }
        Scheduler scheduler = new Scheduler(waitQueue,processingQueues);
        scheduler.start();

        InputThread inputThread = new InputThread(waitQueue,processingQueues);
        inputThread.start();; // start the input thread
    }
}
