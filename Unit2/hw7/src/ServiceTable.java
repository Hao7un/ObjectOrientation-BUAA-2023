package src;

import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class ServiceTable {
    private final HashMap<Integer,Semaphore> inService;
    private final HashMap<Integer,Semaphore> onlyTake;

    public ServiceTable() {
        this.inService = new HashMap<>();
        this.onlyTake = new HashMap<>();
        for (int i = 1; i <= 11; i++) {
            inService.put(i,new Semaphore(4));
        }
        for (int i = 1; i <= 11; i++) {
            onlyTake.put(i,new Semaphore(2));
        }
    }

    public void addService(int floor,boolean isOnlyTake) {
        try {
            inService.get(floor).acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (isOnlyTake) {
            try {
                onlyTake.get(floor).acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeService(int floor,boolean isOnlyTake) {
        inService.get(floor).release();
        if (isOnlyTake) {
            onlyTake.get(floor).release();
        }
    }
}
