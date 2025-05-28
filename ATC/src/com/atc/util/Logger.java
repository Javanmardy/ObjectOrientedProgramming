package com.atc.util;
import java.time.LocalDateTime;
public class Logger {
    public void log(String msg) {
        System.out.println(LocalDateTime.now() + " | " + msg);
    }
}