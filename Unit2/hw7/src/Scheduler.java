package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class Scheduler extends Thread {
    private final int[][] accessMap;
    private final PassengerQueue waitQueue;
    private final ArrayList<PassengerQueue> processingQueues;
    private int roundrobinCount;

    public Scheduler(int[][] accessMap, PassengerQueue waitQueue,
                     ArrayList<PassengerQueue> processingQueues) {
        this.accessMap = accessMap;
        this.waitQueue = waitQueue;
        this.processingQueues = processingQueues;
        this.roundrobinCount = 0;
    }

    @Override
    public void run() {
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
            dispatch(passenger);
        }
    }

    private void dispatch(Passenger passenger) {
        /* 先看一个请求能否在直达 */
        int temp = roundrobinCount;
        int start = passenger.getFromFloor();
        int end = passenger.getDestination();
        do {
            if (!processingQueues.get(temp).isMaintain() &&
                    processingQueues.get(temp).canReach(start) &&
                    processingQueues.get(temp).canReach(end)) {
                passenger.setToFloor(passenger.getDestination());
                processingQueues.get(temp).addPassenger(passenger);
                roundrobinCount = (temp + 1) % processingQueues.size();
                return;
            }
            temp = (temp + 1) % processingQueues.size();
        } while (temp != roundrobinCount);
        /*必须分配路线*/
        ArrayList<Integer> path = searchRoutine(passenger.getFromFloor(),
                passenger.getDestination());
        passenger.setFromFloor(path.get(0));
        passenger.setToFloor(path.get(1));
        start = path.get(0);
        end = path.get(1);
        temp = roundrobinCount;
        do {
            if (!processingQueues.get(temp).isMaintain() &&
                    processingQueues.get(temp).canReach(start) &&
                    processingQueues.get(temp).canReach(end)) {
                processingQueues.get(temp).addPassenger(passenger);
                roundrobinCount = (temp + 1) % processingQueues.size();
                return;
            }
            temp = (temp + 1) % processingQueues.size();
        } while (temp != roundrobinCount);
    }

    public ArrayList<Integer> searchRoutine(int startFloor, int endFloor) {
        int start =  startFloor - 1;
        int end = endFloor - 1;
        Queue<Integer> queue = new LinkedList<>();
        HashMap<Integer, Integer> parentMap = new HashMap<>();
        synchronized (accessMap) {
            boolean[] visitedNodes = new boolean[accessMap.length];
            visitedNodes[start] = true;
            queue.offer(start);
            while (!queue.isEmpty()) {
                int currentNode = queue.poll();
                if (currentNode == end) {
                    // 输出最短路径
                    ArrayList<Integer> path = new ArrayList<>();
                    while (currentNode != start) {
                        path.add(0, currentNode + 1);
                        currentNode = parentMap.get(currentNode);
                    }
                    path.add(0, start + 1);
                    accessMap.notifyAll();
                    return path;
                }
                for (int i = 0; i < accessMap[currentNode].length; i++) {
                    if (accessMap[currentNode][i] == 1 && !visitedNodes[i]) {
                        visitedNodes[i] = true;
                        parentMap.put(i, currentNode);
                        queue.offer(i);
                    }
                }
            }
            accessMap.notifyAll();
        }
        System.out.println("!!!!!NO ROUTINE!!!!!!!");
        return null;
    }
}