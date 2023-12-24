package myexception;

import com.oocourse.spec3.exceptions.PersonIdNotFoundException;

import java.util.HashMap;

public class MyPersonIdNotFoundException extends PersonIdNotFoundException {
    private static int count = 0;
    private int id;
    private static HashMap<Integer,Integer> exceptionRecord = new HashMap<>();

    public MyPersonIdNotFoundException(int id) {
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
        System.out.println("pinf-" + count + ", " + id + "-" + exceptionRecord.get(id));
    }
}