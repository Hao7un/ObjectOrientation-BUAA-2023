package myexception;

import com.oocourse.spec3.exceptions.EqualRelationException;

import java.util.HashMap;

public class MyEqualRelationException extends EqualRelationException {
    private static int count = 0;
    private int id1;
    private int id2;
    private static HashMap<Integer,Integer> exceptionRecord = new HashMap<>();

    public MyEqualRelationException(int id1, int id2) {
        this.id1 = id1;
        this.id2 = id2;
        count++;
        if (exceptionRecord.containsKey(id1)) {
            exceptionRecord.put(id1, exceptionRecord.get(id1) + 1);
        } else {
            exceptionRecord.put(id1, 1);
        }
        if (id1 != id2) { //id1==id2的时候不应该重复计算
            if (exceptionRecord.containsKey(id2)) {
                exceptionRecord.put(id2, exceptionRecord.get(id2) + 1);
            } else {
                exceptionRecord.put(id2, 1);
            }
        }
    }

    @Override
    public void print() {
        if (id1 < id2) {
            System.out.println("er-" + count + ", " + id1 + '-' +
                    exceptionRecord.get(id1) + ", " + id2 + '-' + exceptionRecord.get(id2));
        } else {
            System.out.println("er-" + count + ", " + id2 + '-' +
                    exceptionRecord.get(id2) + ", " + id1 + '-' + exceptionRecord.get(id1));
        }
    }
}