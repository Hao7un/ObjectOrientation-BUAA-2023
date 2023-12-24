package myexception;

import com.oocourse.spec3.exceptions.EqualMessageIdException;

import java.util.HashMap;

public class MyEqualMessageIdException extends EqualMessageIdException {
    private static int count = 0;
    private int id;
    private static HashMap<Integer,Integer> exceptionRecord = new HashMap<>();

    public MyEqualMessageIdException(int id) {
        count += 1;
        this.id = id;
        if (exceptionRecord.containsKey(id)) {
            exceptionRecord.put(id,exceptionRecord.get(id) + 1);
        } else {
            exceptionRecord.put(id, 1);
        }
    }

    @Override
    public void print() {
        System.out.println("emi-" + count + ", " + id + "-" + exceptionRecord.get(id));
    }
}

