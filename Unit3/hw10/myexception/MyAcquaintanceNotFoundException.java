package myexception;

import com.oocourse.spec2.exceptions.AcquaintanceNotFoundException;

import java.util.HashMap;

public class MyAcquaintanceNotFoundException extends AcquaintanceNotFoundException {

    private static int count = 0;
    private int id;
    private static HashMap<Integer,Integer> exceptionRecord = new HashMap<>();

    public MyAcquaintanceNotFoundException(int id) {
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
        System.out.println("anf-" + count + ", " + id + "-" + exceptionRecord.get(id));
    }

}
