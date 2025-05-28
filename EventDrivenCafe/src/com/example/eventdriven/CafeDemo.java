
package com.example.eventdriven;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class CafeDemo {

    private static final String[] DRINKS = {
            "latte", "espresso", "cappuccino", "americano", "mocha", "tea"
    };

    private static final AtomicInteger ID = new AtomicInteger(1);

    public static void main(String[] args) {

        EventLoop loop = new EventLoop();

        loop.addListener(ev -> {
            String[] parts = ev.split("\\|");
            switch (parts[0]) {
                case "NEW":
                    System.out.printf("%s  NEW   Order #%s (%s) from %s%n",
                            timestamp(), parts[1], parts[2], parts[3]);
                    break;
                case "READY":
                    System.out.printf("%s  DONE  Ready #%s (%s)%n",
                            timestamp(), parts[1], parts[2]);
                    break;
            }
        });

        loop.addListener(ev -> {
            String[] parts = ev.split("\\|");
            if (!parts[0].equals("NEW")) return;

            int orderId = Integer.parseInt(parts[1]);
            String drink = parts[2];

            new Thread(() -> {
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(2000, 5000));
                } catch (InterruptedException ignored) {}
                loop.post("READY|" + orderId + "|" + drink);
            }).start();
        });

        loop.start();

        for (int i = 1; i <= 3; i++) {
            final int botId = i;
            Thread bot = new Thread(() -> {
                Random rnd = new Random();
                try {
                    while (true) {
                        Thread.sleep(2500 + rnd.nextInt(2500)); 
                        placeOrder(loop, "Bot-" + botId, DRINKS[rnd.nextInt(DRINKS.length)]);
                    }
                } catch (InterruptedException ignored) {}
            });
            bot.start();
        }
    }

    private static void placeOrder(EventLoop loop, String source, String drink) {
        int id = ID.getAndIncrement();
        loop.post("NEW|" + id + "|" + drink + "|" + source);
    }

    private static String timestamp() {
        return java.time.LocalTime.now().withNano(0).toString();
    }
}
