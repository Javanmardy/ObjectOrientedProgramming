package eventdriven;

import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();

        int numThreads = 8;
        int bufferSize = 1000;
        int totalEvents = 300000;

        Object writeLock = new Object();
        BufferedWriter sharedWriter = new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream("read_receipts.log", false), StandardCharsets.UTF_8)
        );

        List<BlockingQueue<String>> queues = new ArrayList<>();
        List<Thread> workers = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            BlockingQueue<String> q = new LinkedBlockingQueue<>();
            queues.add(q);
            Thread t = new Thread(new BufferedWorker(q, sharedWriter, bufferSize, writeLock));
            workers.add(t);
            t.start();
        }

        for (int i = 0; i < totalEvents; i++) {
            String event = makeFixedSizeMessage(i);
            queues.get(i % numThreads).put(event);
        }

        for (BlockingQueue<String> q : queues) q.put("END");
        for (Thread t : workers) t.join();
        sharedWriter.close();

        long end = System.currentTimeMillis();
        System.out.println("Execution time: " + (end - start) + " ms");

    }

    public static String makeFixedSizeMessage(int i) {
        String header = String.format("MSG|%06d|%d|", i, System.currentTimeMillis());
        int headerLen = header.getBytes(StandardCharsets.UTF_8).length;
        int targetSize = 1024;
        int padSize = targetSize - headerLen - 1;
        StringBuilder filler = new StringBuilder(padSize);
        Random rand = new Random();

        for (int j = 0; j < padSize; j++) {
            filler.append((char) ('A' + rand.nextInt(26)));
        }

        return header + filler + "\n";
    }
}
