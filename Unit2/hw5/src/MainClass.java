package src;

import com.oocourse.elevator1.TimableOutput;
import java.util.ArrayList;

public class MainClass {
    public static void main(String[] args) {
        TimableOutput.initStartTimestamp(); // 初始化时间戳
        ArrayList<PassengerQueue> requestQueues = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            PassengerQueue passengerQueue = new PassengerQueue();
            requestQueues.add(passengerQueue);
            Elevator elevator = new Elevator(passengerQueue,i + 1);
            elevator.start(); //start the elevator thread
        }
        InputThread inputThread = new InputThread(requestQueues);
        inputThread.start();; // start the input thread
    }
}
