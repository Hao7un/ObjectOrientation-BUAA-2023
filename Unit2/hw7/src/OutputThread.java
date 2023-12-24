package src;

import com.oocourse.elevator3.TimableOutput;

public class OutputThread extends Thread {
    public static synchronized void println(String output) {
        TimableOutput.println(output);
    }
}
