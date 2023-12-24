package src;

import com.oocourse.elevator1.TimableOutput;

public class OutputThread extends Thread {
    public static synchronized void println(String output) {
        TimableOutput.println(output);
    }
}
