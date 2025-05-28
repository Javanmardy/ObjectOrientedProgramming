package com.atc.util;
import java.util.Timer;
import java.util.TimerTask;

public class TimerScheduler {
    private static final Timer timer = new Timer(true);
    public static void schedule(Runnable task, long delay) {
        timer.schedule(new TimerTask() {
            @Override public void run() { task.run(); }
        }, delay);
    }
}