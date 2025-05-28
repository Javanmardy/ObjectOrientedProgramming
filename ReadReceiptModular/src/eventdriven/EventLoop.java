package eventdriven;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class EventLoop {
    private List<EventListener> listeners = new ArrayList<>();
    private BlockingQueue<String> eventQueue = new LinkedBlockingQueue<>();
    private ExecutorService executor;

    public EventLoop(int numThreads) {
        executor = Executors.newFixedThreadPool(numThreads);
    }

    public void addListener(EventListener listener) {
        listeners.add(listener);
    }

    public void post(String event) {
        eventQueue.offer(event);
    }

    public void start() {
        new Thread(() -> {
            while (true) {
                try {
                    String event = eventQueue.take();
                    for (EventListener listener : listeners) {
                        executor.submit(() -> listener.onEvent(event));
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }
}
