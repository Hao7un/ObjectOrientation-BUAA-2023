package myexception;

import com.oocourse.spec2.exceptions.MessageIdNotFoundException;

import java.util.HashMap;

public class MyMessageIdNotFoundException extends MessageIdNotFoundException {
    private static int count = 0;
    private int id;
    private static HashMap<Integer,Integer> exceptionRecord = new HashMap<>();

    public MyMessageIdNotFoundException(int id) {
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
        System.out.println("minf-" + count + ", " + id + "-" + exceptionRecord.get(id));
    }
}