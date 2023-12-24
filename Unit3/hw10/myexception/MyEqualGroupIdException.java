package myexception;

import com.oocourse.spec2.exceptions.EqualGroupIdException;

import java.util.HashMap;

public class MyEqualGroupIdException extends EqualGroupIdException {
    private static int count = 0;
    private int id;
    private static HashMap<Integer,Integer> exceptionRecord = new HashMap<>();

    public MyEqualGroupIdException(int id) {
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
        System.out.println("egi-" + count + ", " + id + "-" + exceptionRecord.get(id));
    }
}

