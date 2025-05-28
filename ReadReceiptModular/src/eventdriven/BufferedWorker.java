package eventdriven;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class BufferedWorker implements Runnable {
    private final BlockingQueue<String> queue;
    private final BufferedWriter sharedWriter;
    private final int bufferSize;
    private final List<String> buffer = new ArrayList<>();
    private final Object lock;

    public BufferedWorker(BlockingQueue<String> queue, BufferedWriter sharedWriter, int bufferSize, Object lock) {
        this.queue = queue;
        this.sharedWriter = sharedWriter;
        this.bufferSize = bufferSize;
        this.lock = lock;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String event = queue.poll(1, TimeUnit.SECONDS);
                if (event == null || event.equals("END")) {
                    flushBuffer();
                    break;
                }
                buffer.add(event);
                if (buffer.size() >= bufferSize) {
                    flushBuffer();
                }
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private void flushBuffer() throws IOException {
        if (buffer.isEmpty()) return;

        synchronized (lock) {
            for (String e : buffer) {
                sharedWriter.write(e);
            }
            sharedWriter.flush();
        }

        buffer.clear();
    }
}
