package com.atc.logic;
import com.atc.model.Flight;
import com.atc.model.Runway;
import com.atc.util.Logger;
import java.util.*;

public class FlightManager {
    private final List<Runway> runways;
    private final Queue<Flight> waitingQueue = new LinkedList<>();
    private final Map<Flight, Runway> assignments = new HashMap<>();
    private final Logger logger;

    public FlightManager(List<Runway> runways, Logger logger) {
        this.runways = runways;
        this.logger = logger;
    }

    public synchronized void assignRunway(Flight flight) {
        for (Runway runway : runways) {
            if (runway.isAvailable()) {
                runway.occupy(flight);
                flight.assignRunway(runway);
                assignments.put(flight, runway);
                return;
            }
        }
        waitingQueue.add(flight);
        logger.log("GLOBAL QUEUE <- " + flight.flightNumber() + " (" + flight.origin() + " -> " + flight.destination() + ") waiting");
    }

    public synchronized void releaseRunway(Flight flight) {
        Runway r = assignments.remove(flight);
        if (r != null) {
            r.release();
            flight.assignRunway(null);
            Flight next = waitingQueue.poll();
            if (next != null) {
                r.occupy(next);
                next.assignRunway(r);
                assignments.put(next, r);
            }
        }
    }
}