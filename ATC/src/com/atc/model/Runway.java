package com.atc.model;
import com.atc.enums.FlightDirection;
import com.atc.enums.RunwayState;
import com.atc.util.Logger;

public class Runway {
    private final String id;
    private RunwayState state;
    private final Logger logger;
    public Runway(String id, Logger logger) {
        this.id = id;
        this.state = RunwayState.AVAILABLE;
        this.logger = logger;
    }
    public synchronized void occupy(Flight f) {
        state = RunwayState.OCCUPIED;
        String action = f.direction() == FlightDirection.DEPARTURE ? "T/O" : "LAND";
        logger.log("Runway " + id + " " + action + " " + f.flightNumber() + " (" + f.origin() + " -> " + f.destination() + ") [assigned]");
    }
    public synchronized void release() {
        state = RunwayState.AVAILABLE;
        logger.log("Runway " + id + " released");
    }
    public synchronized boolean isAvailable() { return state == RunwayState.AVAILABLE; }
}