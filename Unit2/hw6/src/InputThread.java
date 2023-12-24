package src;

import com.oocourse.elevator2.ElevatorInput;
import com.oocourse.elevator2.ElevatorRequest;
import com.oocourse.elevator2.MaintainRequest;
import com.oocourse.elevator2.PersonRequest;
import com.oocourse.elevator2.Request;
import java.io.IOException;
import java.util.ArrayList;

public class InputThread extends Thread {
    private final PassengerQueue waitQueue;
    private final ArrayList<PassengerQueue> processingQueues;

    public InputThread(PassengerQueue waitQueue, ArrayList<PassengerQueue> processingQueues) {
        this.waitQueue = waitQueue;
        this.processingQueues = processingQueues;
    }

    @Override
    public void run() {
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        int flag;
        while (true) {
            Request request = elevatorInput.nextRequest();
            // when request == null
            // it means there are no more lines in stdin
            flag = 0;
            if (request == null) {
                for (PassengerQueue processingQueue : processingQueues) {
                    if (processingQueue.isMaintain() && !processingQueue.isEnd()) {
                        processingQueue.queueWait();
                        flag = 1;
                    }
                }
                if (flag == 0) {
                    waitQueue.setEnd(true);
                    break;
                }
            } else {
                // a new valid request
                if (request instanceof PersonRequest) {
                    PersonRequest personRequest = (PersonRequest) request;
                    Passenger passenger = new Passenger(personRequest.getPersonId(),
                            personRequest.getFromFloor(), personRequest.getToFloor());
                    waitQueue.addPassenger(passenger);
                } else if (request instanceof ElevatorRequest) {
                    ElevatorRequest elevatorRequest = (ElevatorRequest) request;
                    PassengerQueue parallelQueue = new PassengerQueue(
                            elevatorRequest.getElevatorId());
                    processingQueues.add(parallelQueue);
                    Elevator elevator = new Elevator(parallelQueue,waitQueue,
                            elevatorRequest.getElevatorId(),
                            elevatorRequest.getFloor(),elevatorRequest.getCapacity(),
                            elevatorRequest.getSpeed());
                    elevator.start(); //start the elevator thread
                } else if (request instanceof MaintainRequest) {
                    MaintainRequest maintainRequest = (MaintainRequest) request;
                    int id = maintainRequest.getElevatorId();
                    for (PassengerQueue processingQueue : processingQueues) {
                        if (processingQueue.getElevatorId() == id) {
                            processingQueue.setMaintain(true);
                            break;
                        }
                    }
                }
            }
        }
        try {
            elevatorInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
