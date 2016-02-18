package edu.ub.san;

/**
 * Created by san on 2/17/16 for jdk7_8_learning.
 */
public class Util {

    public static void sleepUninterrupted(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) { }
    }
}
