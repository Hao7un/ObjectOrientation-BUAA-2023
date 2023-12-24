package src;

import java.util.ArrayList;
import com.oocourse.elevator1.ElevatorInput;
import com.oocourse.elevator1.PersonRequest;
import java.io.IOException;

public class InputThread extends Thread {
    private ArrayList<PassengerQueue> passengerQueues;

    public InputThread(ArrayList<PassengerQueue> passengerQueues) {
        this.passengerQueues = passengerQueues;
    }

    @Override
    public void run() {
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        int cnt = 0;
        while (true) {
            PersonRequest input = elevatorInput.nextPersonRequest();
            // when request == null
            // it means there are no more lines in stdin
            if (input == null) {
                for (PassengerQueue passengerQueue : passengerQueues) {
                    passengerQueue.setEnd(true);
                }
                break;
            } else {
                // a new valid request
                Passenger passenger = new Passenger(input.getPersonId(),
                        input.getFromFloor(), input.getToFloor());
                //dispatch requests to different elevator
                passengerQueues.get(cnt).addPassenger(passenger);
                cnt = (cnt + 1) % 6;
            }
        }

        try {
            elevatorInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

