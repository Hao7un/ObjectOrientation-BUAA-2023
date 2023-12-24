package myexception;

import com.oocourse.spec3.exceptions.GroupIdNotFoundException;

import java.util.HashMap;

public class MyGroupIdNotFoundException extends GroupIdNotFoundException {
    private static int count = 0;
    private int id;
    private static HashMap<Integer,Integer> exceptionRecord = new HashMap<>();

    public MyGroupIdNotFoundException(int id) {
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
        System.out.println("ginf-" + count + ", " + id + "-" + exceptionRecord.get(id));
    }
}