package src;

import com.oocourse.elevator3.ElevatorInput;
import com.oocourse.elevator3.ElevatorRequest;
import com.oocourse.elevator3.MaintainRequest;
import com.oocourse.elevator3.PersonRequest;
import com.oocourse.elevator3.Request;

import java.io.IOException;
import java.util.ArrayList;

public class InputThread extends Thread {
    private final int [][] accessMap;
    private ServiceTable serviceTable;
    private final PassengerQueue waitQueue;
    private final ArrayList<PassengerQueue> processingQueues;

    public InputThread(int [][] accessMap,ServiceTable serviceTable,PassengerQueue waitQueue,
                       ArrayList<PassengerQueue> processingQueues) {
        this.accessMap = accessMap;
        this.serviceTable = serviceTable;
        this.waitQueue = waitQueue;
        this.processingQueues = processingQueues;
    }

    @Override
    public void run() {
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        int endflag;
        while (true) {
            Request request = elevatorInput.nextRequest();
            // when request == null
            // it means there are no more lines in stdin
            endflag = 0;
            if (request == null) {
                for (PassengerQueue processingQueue : processingQueues) {
                    if ((processingQueue.isMaintain() && !processingQueue.isEnd())) {
                        processingQueue.queueWait();
                        endflag = 1;
                        break;
                    }
                    if (waitQueue.getTotalNumber() != 0) {
                        endflag = 1;
                        waitQueue.queueWait();
                    }
                }
                if (endflag == 0) {
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
                    waitQueue.addTotal();
                } else if (request instanceof ElevatorRequest) {
                    ElevatorRequest elevatorRequest = (ElevatorRequest) request;
                    PassengerQueue parallelQueue = new PassengerQueue(
                            elevatorRequest.getElevatorId(),elevatorRequest.getAccess());
                    processingQueues.add(parallelQueue);
                    src.Elevator elevator = new src.Elevator(serviceTable,parallelQueue,waitQueue,
                            elevatorRequest.getElevatorId(),
                            elevatorRequest.getFloor(),elevatorRequest.getCapacity(),
                            elevatorRequest.getSpeed());
                    elevator.start(); //start the elevator thread
                    updateAccessMap();
                } else if (request instanceof MaintainRequest) {
                    MaintainRequest maintainRequest = (MaintainRequest) request;
                    int id = maintainRequest.getElevatorId();
                    for (PassengerQueue processingQueue : processingQueues) {
                        if (processingQueue.getElevatorId() == id) {
                            processingQueue.setMaintain(true);
                            break;
                        }
                    }
                    updateAccessMap();
                }
            }
        }
        try {
            elevatorInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateAccessMap() {
        synchronized (accessMap) {
            for (int i = 0; i < 11; i++) {
                for (int j = 0; j < 11; j++) {
                    accessMap[i][j] = 0;
                }
            }
            for (PassengerQueue processingQueue : processingQueues) {
                if (processingQueue.isMaintain()) {
                    continue;
                }
                ArrayList<Integer> list = new ArrayList<>();
                for (int i = 1; i <= 11; i++) {
                    if (processingQueue.canReach(i)) {
                        list.add(i);
                    }
                }
                for (int thisFloor : list) {
                    for (int thatFloor : list) {
                        if (thisFloor != thatFloor) {
                            accessMap[thisFloor - 1][thatFloor - 1] = 1;
                        }
                    }
                }
            }
            accessMap.notifyAll();
        }
    }
}
