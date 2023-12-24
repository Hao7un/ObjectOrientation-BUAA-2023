package myexception;

import com.oocourse.spec3.exceptions.EmojiIdNotFoundException;

import java.util.HashMap;

public class MyEmojiIdNotFoundException extends EmojiIdNotFoundException {
    private static int count = 0;
    private int id;
    private static HashMap<Integer,Integer> exceptionRecord = new HashMap<>();

    public MyEmojiIdNotFoundException(int id) {
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
        System.out.println("einf-" + count + ", " + id + "-" + exceptionRecord.get(id));
    }
}