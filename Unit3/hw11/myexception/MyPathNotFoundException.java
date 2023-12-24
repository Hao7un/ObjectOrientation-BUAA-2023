package myexception;

import com.oocourse.spec3.exceptions.PathNotFoundException;

import java.util.HashMap;

public class MyPathNotFoundException extends PathNotFoundException {
    private static int count = 0;
    private int id;
    private static HashMap<Integer,Integer> exceptionRecord = new HashMap<>();

    public MyPathNotFoundException(int id) {
        this.id = id;
        count++;
        if (exceptionRecord.containsKey(id)) {
            exceptionRecord.put(id, exceptionRecord.get(id) + 1);
        } else {
            exceptionRecord.put(id, 1);
        }
    }

    @Override
    public void print() {
        System.out.println("pnf-" + count + ", " + id + "-" + exceptionRecord.get(id));
    }
}