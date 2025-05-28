
package com.example.eventdriven;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class EventLoop {
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private final List<EventListener> listeners = new CopyOnWriteArrayList<>();

    private volatile boolean running = true;
    private Thread dispatcher;

    public void post(String event) {
        queue.offer(event);
    }

    public void addListener(EventListener l) {
        listeners.add(l);
    }

    public void start() {
        dispatcher = new Thread(() -> {
            try {
                while (running) {
                    String ev = queue.take();
                    for (EventListener l : listeners) {
                        l.handle(ev);
                    }
                }
            } catch (InterruptedException ignored) {}
        });
        dispatcher.setDaemon(true);
        dispatcher.start();
    }

    public void stop() {
        running = false;
        dispatcher.interrupt();
    }
}
